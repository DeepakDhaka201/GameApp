package com.example.ludo.test;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Three {
    private static List<One> lists = new ArrayList<>();

    public One get() {
       return lists.get(0);
    }
    public static void register(One one) {
        lists.add(one);
    }
}