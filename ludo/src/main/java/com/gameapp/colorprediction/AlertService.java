package com.gameapp.colorprediction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlertService<T> {
    public void sendAlert(T context) {
        log.info("Sending alert for {}", context);
    }
}
