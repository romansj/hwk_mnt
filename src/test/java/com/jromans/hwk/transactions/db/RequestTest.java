package com.jromans.hwk.transactions.db;

import com.jromans.hwk.Data;
import org.junit.jupiter.api.Test;

import static com.jromans.hwk.shared.constants.MintosCurrency.DKK;
import static com.jromans.hwk.shared.constants.MintosCurrency.EUR;
import static com.jromans.hwk.shared.constants.TransactionCategory.FX;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class RequestTest {

    @Test
    void getFxRequest() {
        Request request = Data.getRequest()
                .withAccountSrc("LV10MINT0012345678001")
                .withAccountDst("LV10MINT0012345678002")
                .withAmount(valueOf(200)).withCurrency(EUR);

        Request fxRequest = Request.getFxRequest(request, DKK, valueOf(1400));

        assertSame(FX, fxRequest.getCategory());
        assertEquals(fxRequest.getAccountSrc(), fxRequest.getAccountDst());
        assertEquals(DKK, fxRequest.getCurrency());
    }
}