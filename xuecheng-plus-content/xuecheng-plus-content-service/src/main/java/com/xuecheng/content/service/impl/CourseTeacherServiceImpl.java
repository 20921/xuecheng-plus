package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseBaseInfoService;

import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {

    @Autowired
    private CourseBaseInfoService courseBaseService;

    @Override
    public CourseTeacher saveCourseTeacher(Long company, CourseTeacher courseTeacher) {
        Long companyId = courseBaseService.getById(courseTeacher.getCourseId()).getCompanyId();
        if (!company.equals(companyId))
            XueChengPlusException.cast("只能修改或新增本机构对应教师信息");
        this.saveOrUpdate(courseTeacher);
        return this.getById(courseTeacher.getId());
    }

    @Override
    public void removeCourseTeacher(Long company, Long courseId, Long teacherId) {
        Long companyId = courseBaseService.getById(courseId).getCompanyId();
        if (!company.equals(companyId))
            XueChengPlusException.cast("只能删除本机构对应教师信息");
        this.remove(Wrappers.<CourseTeacher>lambdaQuery()
                .eq(CourseTeacher::getCourseId, courseId)
                .eq(CourseTeacher::getId, teacherId));
    }
}
