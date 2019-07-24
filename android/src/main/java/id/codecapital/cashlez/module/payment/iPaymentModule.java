package id.codecapital.cashlez.module.payment;

import com.cashlez.android.sdk.CLPayment;
import com.facebook.react.bridge.Promise;

public interface iPaymentModule {
    void init(Promise promise);
    void doPayCash(String amount, String description, Promise promise);
}
