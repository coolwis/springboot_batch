 package com.psc.sample.r102.batch;

import com.psc.sample.r102.custom.CustomBeanWrapperFieldExtractor;
import com.psc.sample.r102.dto.TwoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.IOException;

 @RequiredArgsConstructor
@Slf4j
@Configuration
public class CsvJobMultiFile {

     //for multi file
    private final ResourceLoader resourceLoader ;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private static final int chunkSize = 5;

    @Bean
    public Job csvJobMultiFile_batchBuild() throws Exception {
        return jobBuilderFactory.get("csvJobMultiFile")
                .start(csvJobMultiFile_batchStep1())
                .build();
    }

    @Bean
    public Step csvJobMultiFile_batchStep1() throws Exception {
        return stepBuilderFactory.get("csvJobMultiFile_batchStep1")
                .<TwoDto, TwoDto>chunk(chunkSize)
                .reader(csvJobMultiFile_FileReader())
                .writer(csvJobMultiFile_FileWriter(new FileSystemResource("R102/output/csvJobMultiFile_output.txt")))
                .build();
    }

    @Bean
    public MultiResourceItemReader <TwoDto> csvJobMultiFile_FileReader() {

        MultiResourceItemReader<TwoDto> multiResourceItemReader = new MultiResourceItemReader<>();

        try {
            multiResourceItemReader.setResources(
                    ResourcePatternUtils.getResourcePatternResolver(this.resourceLoader).getResources(
                            "classpath:sample/csvJobMultiFile/*.txt"
                    ));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DefaultLineMapper<TwoDto> defaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames("one","two");
        delimitedLineTokenizer.setDelimiter(":");

        BeanWrapperFieldSetMapper<TwoDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(TwoDto.class);

        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);

//        multiResourceItemReader.setLineMapper(defaultLineMapper);
        return multiResourceItemReader;
    }

    @Bean
    public FlatFileItemWriter<TwoDto> csvJobMultiFile_FileWriter(Resource outputResource) throws Exception {

        // 방법의 일환일 뿐 processor 권장 함
        CustomBeanWrapperFieldExtractor<TwoDto> fieldExtractor = new CustomBeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"one", "two"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<TwoDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter("-");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<TwoDto>()
                .name("csvJobMultiFile_FileWriter")
                .resource(outputResource)
                .lineAggregator(lineAggregator)
                .build();
    }
}
