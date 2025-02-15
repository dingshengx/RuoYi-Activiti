package com.ruoyi.batch.config;

import com.ruoyi.batch.listener.JobListener;
import com.ruoyi.batch.listener.StepListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  public BatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job reportJob(JobListener listener, Step step1) {
    return jobBuilderFactory
        .get("reportJob")
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .flow(step1)
        .end()
        .build();
  }

  @Bean
  public Step step1(
      ItemReader<? extends String> reader,
      ItemProcessor<? super String, ? extends String> processor,
      ItemWriter<? super String> writer,
      StepListener listener) {
    return stepBuilderFactory
        .get("step1")
        .<String, String>chunk(10)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .listener(listener)
        .build();
  }

  /**
   * 读取过程
   *
   * <p>这里读取的是一个CSV文件，其中包含两个字段：name和value。
   *
   * @return ItemReader
   */
  @Bean
  public ItemReader<String> reader() {
    FlatFileItemReader<String> reader = new FlatFileItemReader<>();
    reader.setResource(new ClassPathResource("sample-data.csv"));
    reader.setLineMapper(
        new DefaultLineMapper<String>() {
          {
            setLineTokenizer(
                new DelimitedLineTokenizer() {
                  {
                    setNames(new String[] {"name", "value"});
                  }
                });
            setFieldSetMapper(
                new BeanWrapperFieldSetMapper<String>() {
                  {
                    setTargetType(String.class);
                  }
                });
          }
        });
    return reader;
  }

  /**
   * 数据处理过程
   *
   * <p>这里是一个简单的数据处理过程，将输入的字符串转换为大写形式。
   *
   * @return itemProcessor
   */
  @Bean
  public ItemProcessor<String, String> processor() {
    return String::toUpperCase;
  }

  /**
   * 输出过程，这里输出到控制台
   *
   * @return ItemWriter
   */
  @Bean
  public ItemWriter<String> writer() {
    return items -> items.forEach(System.out::println);
  }
}
