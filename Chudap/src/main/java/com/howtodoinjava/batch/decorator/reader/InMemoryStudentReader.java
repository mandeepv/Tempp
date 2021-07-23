package com.howtodoinjava.batch.decorator.reader;
import com.howtodoinjava.batch.decorator.model.Customer;
import org.springframework.batch.item.ItemReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InMemoryStudentReader implements ItemReader<Customer> {

    private int nextStudentIndex;
    private List<Customer> studentData;

    public InMemoryStudentReader() {
        initialize();
    }

    private void initialize() {
        Customer tony = new Customer();
        tony.setId(Long.valueOf(123));
        tony.setFirstName("Tony Tester");
        tony.setLastName("master");
        tony.setBirthdate("19-10-98");

        Customer nick = new Customer();
        nick.setId(239L);
        nick.setLastName("Rookie");
        nick.setFirstName("Nick Newbie");
        nick.setBirthdate("19-10-98");

        Customer ian = new Customer();
        ian.setId(784L);
        ian.setLastName("Ian Intermediate");
        ian.setFirstName("intermediate");
        ian.setBirthdate("22-07-21");

        studentData = Collections.unmodifiableList(Arrays.asList(tony, nick, ian));
        nextStudentIndex = 0;
    }

    @Override
    public Customer read() throws Exception {
        Customer nextStudent = null;

        if (nextStudentIndex < studentData.size()) {
            nextStudent = studentData.get(nextStudentIndex);
            nextStudentIndex++;
        }
        else {
            nextStudentIndex = 0;
        }

        return nextStudent;
    }
}