package com.angrytanks.view.contract;

import com.angrytanks.model.snapshot.MatchSnapshot;

public record GameFrame(MatchSnapshot match, boolean decomposition) {}
