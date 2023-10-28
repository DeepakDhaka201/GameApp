package com.gameapp.core.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.gameapp.core.redis.RedisService;

import java.util.Objects;

import static java.lang.Thread.sleep;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedisService redisService;

    public Boolean acquireLock(String key, int ttl) {
        log.info("Acquiring Lock with Key : {}", key);
        if (Objects.nonNull(redisService.get(key))) {
            log.error("Lock already exists");
            return false;
        }

        redisService.set(key, true, Long.valueOf(ttl));
        return true;
    }

    public Boolean isLockAcquired(String key) {
        return Objects.nonNull(redisService.get(key));
    }

    public void releaseLock(String key) {
        if (Objects.isNull(key)) {
            return;
        }
        redisService.delete(key);
    }

    public Boolean waitForLock(String key, int timeOut) throws InterruptedException {
        int itr = 0;
        while (!acquireLock(key, 10*1000)) {
            if (itr > timeOut * 1000 / 300) {
                throw new InterruptedException("Not able to get the lock. Please try again");
            }
            sleep(300);
            itr += 3;
        }
        return true;
    }
}
