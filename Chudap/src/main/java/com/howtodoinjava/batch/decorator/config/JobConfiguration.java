package com.howtodoinjava.batch.decorator.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import com.howtodoinjava.batch.decorator.processor.CustomItemProcessor;
import com.howtodoinjava.batch.decorator.reader.InMemoryStudentReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.xstream.XStreamMarshaller;
import com.howtodoinjava.batch.decorator.aggregator.CustomLineAggregator;
import com.howtodoinjava.batch.decorator.classifier.CustomerClassifier;
import com.howtodoinjava.batch.decorator.mapper.CustomerRowMapper;
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

    @Bean
    public FlatFileItemWriter<Customer> jsonItemWriter() throws Exception {

        String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
        System.out.println(">> Output Path = " + customerOutputPath);
        FlatFileItemWriter<Customer> writer = new FlatFileItemWriter<Customer>();
        writer.setLineAggregator(new CustomLineAggregator());
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