package com.angrytanks.presenter.contract;

import com.angrytanks.model.api.Registration;

@FunctionalInterface
public interface FrameLoop {

  Registration start(Runnable pulse);
}
