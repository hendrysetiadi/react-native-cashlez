package id.codecapital.cashlez.module;

import android.content.Context;

import com.cashlez.android.sdk.CLPayment;
import com.cashlez.android.sdk.CLPaymentCapability;

public class ApplicationState {
    private static ApplicationState mInstance = null;

    private Context context;
    private CLPayment payment;
    private CLPaymentCapability paymentCapability;
    private boolean isGpn;

    protected ApplicationState () {}

    public static synchronized ApplicationState getInstance() {
        if(mInstance == null) {
            mInstance = new ApplicationState();
        }
        return mInstance;
    }


    public CLPaymentCapability getPaymentCapability() {
        return paymentCapability;
    }

    public void setPaymentCapability(CLPaymentCapability paymentCapability) {
        this.paymentCapability = paymentCapability;
    }



    /* @Override
    public void onCreate() {
        super.onCreate();
    }

    public Context getContext() {
        return context;
    }

    public void setCurrentContext(Context currentContext) {
        this.context = currentContext;
    }

    public CLPayment getPayment() {
        return payment;
    }

    public void setPayment(CLPayment payment) {
        this.payment = payment;
    }

    public CLPaymentCapability getPaymentCapability() {
        return paymentCapability;
    }

    public void setPaymentCapability(CLPaymentCapability paymentCapability) {
        this.paymentCapability = paymentCapability;
    }

    public boolean isGpn() {
        return isGpn;
    }

    public void setGpn(boolean gpn) {
        isGpn = gpn;
    } */
}
