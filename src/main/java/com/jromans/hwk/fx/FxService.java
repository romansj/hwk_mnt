package com.jromans.hwk.fx;

import com.jromans.hwk.shared.constants.MintosCurrency;

public interface FxService {
    RateData getRate(MintosCurrency base, MintosCurrency output);
}
