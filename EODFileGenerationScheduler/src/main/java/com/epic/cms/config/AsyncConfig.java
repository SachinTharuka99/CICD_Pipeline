package com.epic.cms.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncConfigurer;

public class AsyncConfig implements AsyncConfigurer {

    /*
    This will enable application level threadpoolexecutor instead of default one.
    method level executors defined in commonconfiguration module threadpoolconfig class.
    @Override
    public Executor getAsyncExecutor() {
        return new ThreadPoolTaskExecutor();
    }
*/

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
