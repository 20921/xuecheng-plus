package com.xuecheng.content.api;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;

    /**
     * 根据课程id查询对应教师信息
     *
     * @param courseId
     * @return
     */
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> list(@PathVariable Long courseId) {
        return courseTeacherService.list(Wrappers.<CourseTeacher>lambdaQuery()
                .eq(CourseTeacher::getCourseId, courseId));
    }

    /**
     * 更新/添加教师信息(只能更新/添加本机构教师信息)
     * @param courseTeacher
     * @return
     */
    @PostMapping("/courseTeacher")
    public CourseTeacher saveCourseTeacher(@RequestBody @Validated CourseTeacher courseTeacher) {
        Long company = 1232141425L;
        return courseTeacherService.saveCourseTeacher(company, courseTeacher);
    }

    /**
     * 删除课程中对应教师信息(只能删除本机构教师信息)
     * @param courseId
     * @param teacherId
     */
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void removeTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        Long company = 1232141425L;
        courseTeacherService.removeCourseTeacher(company, courseId, teacherId);
    }
}
