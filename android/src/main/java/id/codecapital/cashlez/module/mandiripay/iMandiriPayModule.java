package id.codecapital.cashlez.module.mandiripay;

import com.facebook.react.bridge.Promise;

public interface iMandiriPayModule {
    void init();

    void doStartMandiriPayHandler();
    void doResumeMandiriPayHandler();
    void doStopMandiriPayHandler();

    void doPayMandiriPay(String amount, String description, Promise promise);
    void doPrintQR(Promise promise);
    void doCheckMandiriPayStatus(Promise promise);
    void doPrintMandiriPay(Promise promise);
}
