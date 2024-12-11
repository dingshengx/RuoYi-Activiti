package com.ruoyi.activiti.service.impl;

import com.ruoyi.activiti.domain.dto.DefinitionIdDTO;
import com.ruoyi.activiti.domain.dto.QueryProcessDefinitionDTO;
import com.ruoyi.activiti.domain.vo.ProcessDefinitionVO;
import com.ruoyi.activiti.mapper.ActReDeploymentMapper;
import com.ruoyi.activiti.service.IProcessDefinitionService;
import com.ruoyi.common.config.ZnderConfig;
import com.ruoyi.common.utils.file.FileUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
public class ProcessDefinitionServiceImpl implements IProcessDefinitionService {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ActReDeploymentMapper actReDeploymentMapper;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;

    @Override
    public List<ProcessDefinitionVO> selectProcessDefinitionList(QueryProcessDefinitionDTO processDefinition) {
        return actReDeploymentMapper.selectProcessDefinitionList(processDefinition);
    }

    @Override
    public DefinitionIdDTO getDefinitionsByInstanceId(String instanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        String deploymentId = processInstance.getDeploymentId();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        return new DefinitionIdDTO(processDefinition);
    }

    @Override
    public int deleteProcessDefinitionById(String id) {
        // 输入参数校验
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("definitionId必填");
        }

        try {
            repositoryService.deleteDeployment(id, false);
            return 1;
        } catch (Exception e) {
            log.error("Failed to delete deployment with ID: {}", id, e);
            throw new RuntimeException("删除失败：存在代办未处理");
        }
    }


    @Override
    public void uploadStreamAndDeployment(MultipartFile file) throws IOException {
        // 获取上传的文件名
        String fileName = file.getOriginalFilename();
        // 得到输入流（字节流）对象
        InputStream fileInputStream = file.getInputStream();
        // 文件的扩展名
        String extension = FilenameUtils.getExtension(fileName);

        if (extension.equals("zip")) {
            ZipInputStream zip = new ZipInputStream(fileInputStream);
            repositoryService.createDeployment()//初始化流程
                    .addZipInputStream(zip)
                    .deploy();
        } else {
            repositoryService.createDeployment()//初始化流程
                    .addInputStream(fileName, fileInputStream)

                    .deploy();
        }
    }

    @Override
    public void suspendOrActiveApply(String id, Integer suspendState) {
        if (1==suspendState) {
            // 当流程定义被挂起时，已经发起的该流程定义的流程实例不受影响（如果选择级联挂起则流程实例也会被挂起）。
            // 当流程定义被挂起时，无法发起新的该流程定义的流程实例。
            // 直观变化：act_re_procdef 的 SUSPENSION_STATE_ 为 2
            repositoryService.suspendProcessDefinitionById(id);
        } else if (2==suspendState) {
            repositoryService.activateProcessDefinitionById(id);
        }
    }

    @Override
    public String upload(MultipartFile multipartFile) throws IOException {
       return FileUploadUtils.upload(ZnderConfig.getUploadPath()+"/processDefinition" , multipartFile);
    }

    @Override
    public void addDeploymentByString(String stringBPMN) {
        repositoryService.createDeployment()
                .addString("CreateWithBPMNJS.bpmn", stringBPMN)
                .deploy();
    }

    @Override
    public void getProcessDefineXML(HttpServletResponse response, String deploymentId, String resourceName) throws IOException {
        InputStream inputStream = repositoryService.getResourceAsStream(deploymentId, resourceName);
        int count = inputStream.available();
        byte[] bytes = new byte[count];
        response.setContentType("text/xml");
        OutputStream outputStream = response.getOutputStream();
        while (inputStream.read(bytes) != -1) {
            outputStream.write(bytes);
        }
        inputStream.close();
    }
}
