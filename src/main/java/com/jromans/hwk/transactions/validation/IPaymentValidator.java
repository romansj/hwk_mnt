package com.jromans.hwk.transactions.validation;

import com.jromans.hwk.accounts.IAccount;
import com.jromans.hwk.transactions.db.Request;

public interface IPaymentValidator {
    boolean validate(IAccount account, Request request);
}
