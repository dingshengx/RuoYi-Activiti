package com.ruoyi.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class StepListener implements StepExecutionListener {

  @Override
  public void beforeStep(StepExecution stepExecution) {
    System.out.println("Step started!");
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    System.out.println("Step finished with status: " + stepExecution.getStatus());
    return ExitStatus.COMPLETED;
  }
}
