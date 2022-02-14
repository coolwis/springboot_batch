package com.psc.sample.r102.batch;

import com.psc.sample.r102.custom.CustomBeanWrapperFieldExtractor;
import com.psc.sample.r102.domain.Two;
import com.psc.sample.r102.dto.TwoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManagerFactory;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class CsvToJpaJob3 {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final EntityManagerFactory entityManagerFactroy;

    private static final int chunkSize = 5;

    @Bean
    public Job csvToJpaJob3_batchBuild() throws Exception {
        return jobBuilderFactory.get("csvToJpaJob3")
                .start(csvToJpaJob3_batchStep1())
                .build();
    }

    @Bean
    public Step csvToJpaJob3_batchStep1() throws Exception {
        return stepBuilderFactory.get("csvToJpaJob3_batchStep1")
                .<TwoDto, Two>chunk(chunkSize)
                .reader(csvToJpaJob3_Reader(null))
                .processor(csvToJpaJob3_processor())
                .writer(csvToJpaJob3_dbItemWriter())
                .build();
    }


    @Bean
    public ItemProcessor<TwoDto, Two> csvToJpaJob3_processor() {
        return twoDto -> new Two(twoDto.getOne(), twoDto.getTwo());
    }

    @Bean
    public JpaItemWriter<Two> csvToJpaJob3_dbItemWriter() {
        JpaItemWriter<Two> jpaItemWriter =new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactroy);
        return jpaItemWriter;
    }

    @Bean
    @StepScope  // parameter dynamic receive
    public FlatFileItemReader<TwoDto> csvToJpaJob3_Reader( @Value("#{jobParameters[inFileName]}") String inFileName) {
        return new FlatFileItemReaderBuilder<TwoDto>()
                .name("csvToJpaJob3_FileReader")
                .resource(new FileSystemResource(inFileName))
                .delimited().delimiter(":")
                .names("one", "two")
                .targetType(TwoDto.class)
//                .recordSeparatorPolicy((SimpleRecordSeparatorPolicy) postProcess(record) -> {
//                    log.debug("policy:" +record);
//                    if(record.indexOf(":") ==-1){
//                        return null;
//                    }
//                    return record.trim();
//        })
        .build()
        ;
    }

}
