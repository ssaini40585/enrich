package com.enrichplus.staffing.dto;

public record CvUploadRequest(
        String cvUrl,
        String primarySkill,
        String skillsCsv,
        Integer yearsOfExperience,
        String professionalSummary
) {
}
