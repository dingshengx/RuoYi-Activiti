package com.ruoyi.activiti.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class CounterCompletedListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        DelegateExecution execution = delegateTask.getExecution();
        delegateTask.setVariableLocal("nrOfCompletedInstances", delegateTask.getExecution().getParent().getVariable("nrOfCompletedInstances"));
        delegateTask.setVariableLocal("nrOfActiveInstances", delegateTask.getExecution().getParent().getVariable("nrOfActiveInstances"));
    }

}
