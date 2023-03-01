package com.example.ludo.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class One implements IOne{
    Logger logger = LoggerFactory.getLogger(One.class);
    private final Two two;

    public void run() {
        two.test();
    }

    @PostConstruct
    public void init() {
        logger.info("testiness");
        Three.register(this);
    }
}
