package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.po.CourseTeacher;

/**
 * <p>
 * 课程-教师关系表 服务类
 * </p>
 *
 * @author itcast
 * @since 2023-02-09
 */
public interface CourseTeacherService extends IService<CourseTeacher> {

    CourseTeacher saveCourseTeacher(Long company, CourseTeacher courseTeacher);

    void removeCourseTeacher(Long company, Long courseId, Long teacherId);
}
