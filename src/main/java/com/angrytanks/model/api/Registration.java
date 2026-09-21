package com.angrytanks.model.api;

@FunctionalInterface
public interface Registration extends AutoCloseable {
  Registration NONE = () -> {};

  @Override
  void close();
}
