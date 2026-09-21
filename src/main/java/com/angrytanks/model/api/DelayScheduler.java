package com.angrytanks.model.api;

@FunctionalInterface
public interface DelayScheduler {

  Registration schedule(long delayMillis, Runnable action);
}
