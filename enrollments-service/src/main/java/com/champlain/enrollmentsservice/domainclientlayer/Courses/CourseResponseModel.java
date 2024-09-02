package com.champlain.enrollmentsservice.domainclientlayer.Courses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseModel {

    private String courseId;
    private String courseNumber;
    private String courseName;
    private Integer numHours;
    private Double numCredits;
    private String department;
}
