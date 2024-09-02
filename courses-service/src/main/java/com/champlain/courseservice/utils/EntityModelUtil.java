package com.champlain.courseservice.utils;

import com.champlain.courseservice.dataaccesslayer.Course;
import com.champlain.courseservice.presentationlayer.CourseRequestModel;
import com.champlain.courseservice.presentationlayer.CourseResponseModel;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

public class EntityModelUtil {

    public static CourseResponseModel toCourseResponseModel(Course course) {
        CourseResponseModel courseResponseModel = new CourseResponseModel();
        BeanUtils.copyProperties(course, courseResponseModel);
        return courseResponseModel;
    }

    /**
     * This will transform a CourseRequestModel into a Course
     * @param courseRequestModel
     * @return Course
     */
    public static Course toCourseEntity(CourseRequestModel courseRequestModel) {
        Course course = new Course();
        BeanUtils.copyProperties(courseRequestModel, course);
        return course;
    }

    public static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }
}
