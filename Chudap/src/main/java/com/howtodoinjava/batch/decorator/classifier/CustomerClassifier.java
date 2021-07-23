package com.howtodoinjava.batch.decorator.classifier;

import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;
import com.howtodoinjava.batch.decorator.model.Customer;

public class CustomerClassifier implements Classifier<Customer, ItemWriter<? super Customer>> {

    private static final long serialVersionUID = 1L;

    private ItemWriter<Customer> evenItemWriter;
    private ItemWriter<Customer> oddItemWriter;

    public CustomerClassifier(ItemWriter<Customer> evenItemWriter) {
        this.evenItemWriter = evenItemWriter;
    }

    @Override
    public ItemWriter<? super Customer> classify(Customer customer) {
        return evenItemWriter;
    }
}