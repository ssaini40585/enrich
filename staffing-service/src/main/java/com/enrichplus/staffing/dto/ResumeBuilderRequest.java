package com.enrichplus.staffing.dto;

public record ResumeBuilderRequest(
        String primarySkill,
        String skillsCsv,
        Integer yearsOfExperience,
        String professionalSummary
) {
}
