package com.howtodoinjava.batch.decorator.config;

import java.io.File;
import javax.sql.DataSource;

import com.howtodoinjava.batch.decorator.processor.CustomItemProcessor;
import com.howtodoinjava.batch.decorator.reader.InMemoryStudentReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import com.howtodoinjava.batch.decorator.model.Customer;

@Configuration
public class JobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean
    public ItemReader<Customer> itemReader() {
        return new InMemoryStudentReader();
    }

    @Bean
    public ItemProcessor<Customer, Customer> itemProcessor() {
        return new CustomItemProcessor();
    }

    private FieldExtractor<Customer> createStudentFieldExtractor() {
        BeanWrapperFieldExtractor<Customer> extractor =
                new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{
                "id",
                "firstName",
                "lastName",
                "birthdate"
        });
        return extractor;
    }

    private LineAggregator<Customer> createStudentLineAggregator() {
        DelimitedLineAggregator<Customer> lineAggregator =
                new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(";");

        FieldExtractor<Customer> fieldExtractor = createStudentFieldExtractor();
        lineAggregator.setFieldExtractor(fieldExtractor);

        return lineAggregator;
    }
    @Bean
    public FlatFileItemWriter<Customer> jsonItemWriter() throws Exception {

        String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
        System.out.println(">> Output Path = " + customerOutputPath);
        LineAggregator<Customer> lineAggregator = createStudentLineAggregator();
        FlatFileItemWriter<Customer> writer = new FlatFileItemWriter<Customer>();
        writer.setLineAggregator(lineAggregator);
        writer.setResource(new FileSystemResource(customerOutputPath));
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public Step step1() throws Exception {
        return stepBuilderFactory.get("step1")
                .<Customer, Customer>chunk(10)
                .reader(new InMemoryStudentReader())
                .processor(new CustomItemProcessor())
                .writer(jsonItemWriter())
                .build();
    }

    @Bean
    public Job job() throws Exception {
        return jobBuilderFactory.get("job")
                .start(step1())
                .build();
    }

}
