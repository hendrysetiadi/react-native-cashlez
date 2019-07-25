package id.codecapital.cashlez.module.payment;

import com.facebook.react.bridge.Promise;

public interface iPaymentModule {
    void init();

    void doCheckReaderCompanion(Promise promise);
    void doCheckPrinterCompanion(Promise promise);
    void doUnregisterReceiver();

    void doConnectLocationProvider();
    void doStopUpdateLocation();

    void doStartPayment(Promise promise);
    void doCloseCompanionConnection();
    void doPayCash(String amount, String description, Promise promise);
    void doPayLocalCardPin(String amount, String description, Promise promise);
    void doPayLocalCardNoPin(String amount, String description, Promise promise);
    void doPayInternationalCard(String amount, String description, Promise promise);
}
