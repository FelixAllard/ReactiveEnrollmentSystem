package com.champlain.courseservice.presentationlayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestModel {

    private String courseNumber;
    private String courseName;
    private Integer numHours;
    private Double numCredits;
    private String department;
}