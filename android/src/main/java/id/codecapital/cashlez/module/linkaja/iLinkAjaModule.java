package id.codecapital.cashlez.module.linkaja;

import com.facebook.react.bridge.Promise;

public interface iLinkAjaModule {
    void init();

    void doStartLinkAjaHandler();
    void doResumeLinkAjaHandler();
    void doStopLinkAjaHandler();

    void doPayLinkAja(String amount, String description, Promise promise);
    void doPrintQR(Promise promise);
    void doCheckLinkAjaStatus(Promise promise);
}
