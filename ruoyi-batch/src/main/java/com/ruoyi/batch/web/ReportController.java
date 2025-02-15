package com.ruoyi.batch.web;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class ReportController {

  @Autowired private JobLauncher jobLauncher;

  @Autowired private Job reportJob;

  @PostMapping("/generate-report")
  public String generateReport() {
    JobParameters jobParameters =
        new JobParametersBuilder().addLong("startAt", System.currentTimeMillis()).toJobParameters();
    try {
      jobLauncher.run(reportJob, jobParameters);
      return "Report generation job launched successfully!";
    } catch (Exception e) {
      return "Failed to launch report generation job: " + e.getMessage();
    }
  }
}
