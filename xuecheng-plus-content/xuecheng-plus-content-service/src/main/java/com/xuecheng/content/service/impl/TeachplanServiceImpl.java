package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.xuecheng.base.exception.XueChengPlusException;
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

import java.util.Comparator;
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

    private List<Teachplan> getTeachplan(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        return teachplans;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //通过课程计划id判断是新增和修改
        Long teachplanId = saveTeachplanDto.getId();
        if (teachplanId == null) {
            //新增hh
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            //确定排序字段，找到它的同级节点个数，排序字段就是个数加1  select count(1) from teachplan where course_id=117 and parentid=268
            Long parentid = saveTeachplanDto.getParentid();
            Long courseId = saveTeachplanDto.getCourseId();
            //获取出全部信息
            List<Teachplan> teachplanAll = getTeachplan(courseId, parentid);
            if (teachplanAll.size() != 0) {
                //排序字段的最大值
                Teachplan teachplanMax = teachplanAll.stream().max(Comparator.comparing(Teachplan::getOrderby)).get();
                //将最大值+1
                teachplan.setOrderby(teachplanMax.getOrderby() + 1);
            }
            teachplanMapper.insert(teachplan);
        } else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            //将参数复制到teachplan
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }


    }

    @Override
    public void moveUp(Long id) {
        //取当前课程计划的排序字段
        Teachplan teachplan = teachplanMapper.selectById(id);
        Integer orderby = teachplan.getOrderby();

        //orderby比他小的 课程计划
        Long parentId = teachplan.getParentid();
        Long courseId = teachplan.getCourseId();

        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);

        wrapper.lt(Teachplan::getOrderby,orderby);

        List<Teachplan> teachPlansLt = teachplanMapper.selectList(wrapper);

        //判断是不是最小的一个
        if (CollectionUtils.isEmpty(teachPlansLt)){
            XueChengPlusException.cast("不能再向上移动辣！！！");
        }

        //不是最小就交换顺序
        Teachplan teachPlanLt = teachPlansLt.get(teachPlansLt.size() - 1);
        int temp = teachPlanLt.getOrderby();
        teachPlanLt.setOrderby(teachplan.getOrderby());
        teachplan.setOrderby(temp);



        //存表
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(teachPlanLt);
    }

    @Override
    public void movedown(Long id) {
        //取当前课程计划的排序字段
        Teachplan teachplan = teachplanMapper.selectById(id);
        Integer orderby = teachplan.getOrderby();

        //orderby比他大的 课程计划
        Long parentId = teachplan.getParentid();
        Long courseId = teachplan.getCourseId();

        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);

        wrapper.gt(Teachplan::getOrderby,orderby);
        List<Teachplan> teachPlansGt = teachplanMapper.selectList(wrapper);

        //判断是不是最大的一个
        if (CollectionUtils.isEmpty(teachPlansGt)){
            XueChengPlusException.cast("不能再向下移动辣！！！");
        }

        Teachplan teachPlanGt = teachPlansGt.get(0);
        int temp = teachPlanGt.getOrderby();
        teachPlanGt.setOrderby(teachplan.getOrderby());
        teachplan.setOrderby(temp);
//存表
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(teachPlanGt);
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
