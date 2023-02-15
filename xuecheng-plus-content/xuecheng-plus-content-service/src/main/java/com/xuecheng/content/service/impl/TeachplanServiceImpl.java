package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.DeleteTeachPlanDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/14 12:11
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    private int getTeachplanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count + 1;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //通过课程计划id判断是新增和修改
        Long teachplanId = saveTeachplanDto.getId();
        if (teachplanId == null) {
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            //确定排序字段，找到它的同级节点个数，排序字段就是个数加1  select count(1) from teachplan where course_id=117 and parentid=268
            Long parentid = saveTeachplanDto.getParentid();
            Long courseId = saveTeachplanDto.getCourseId();
            int teachplanCount = getTeachplanCount(courseId, parentid);
            teachplan.setOrderby(teachplanCount);
            teachplanMapper.insert(teachplan);

        } else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            //将参数复制到teachplan
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }

    }


    /**
     * 根据teachPlan id删除课程计划
     *
     * @param id teachPlan id
     * @return DeleteTeachPlanDto
     */
    @Transactional
    @Override
    public DeleteTeachPlanDto deleteTeachplan(Long id) {
        //判断前端传的是大章节还是小章节 --> 根据id查询teachPlan表
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan.getParentid() == 0) {
            //删除的是大章节 -->判断是否存在小章节 -->根据大章节id查询
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Teachplan::getParentid, id); // 是否有小章节的 parentId是章节的id
            Integer count = teachplanMapper.selectCount(wrapper);
            if (count > 0) { //说明有子节点
                return DeleteTeachPlanDto.err();
            }
            //到这里说明没有字节点,执行删除
            teachplanMapper.deleteById(id);
            return DeleteTeachPlanDto.sucess();
        }

        // 小章节进行删除
        //删除teachplan_media表
        LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachplanMedia::getTeachplanId, id);
        Integer count = teachplanMediaMapper.selectCount(wrapper);
        if (count > 0) {
            //执行删除
            teachplanMediaMapper.delete(wrapper);
        }
        //删除teachplan表
        teachplanMapper.deleteById(id);

        return DeleteTeachPlanDto.sucess();

    }


}
