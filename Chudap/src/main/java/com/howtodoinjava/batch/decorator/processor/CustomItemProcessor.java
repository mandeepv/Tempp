package com.howtodoinjava.batch.decorator.processor;

import com.howtodoinjava.batch.decorator.model.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer item) throws Exception {

        System.out.println("Processing..." + item.getFirstName());
        return item;
    }

}