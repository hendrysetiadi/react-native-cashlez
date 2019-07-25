package id.codecapital.cashlez.module.ovo;

import com.facebook.react.bridge.Promise;

public interface iOvoModule {
    void init();

    void doStartOvoHandler();
    void doResumeOvoHandler();
    void doStopOvoHandler();

    void doPayOvo(String amount, String description, String phone, Promise promise);
    void doOvoInquiry(Promise promise);
}
