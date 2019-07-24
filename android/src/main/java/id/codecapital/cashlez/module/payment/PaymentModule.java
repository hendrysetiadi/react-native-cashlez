package id.codecapital.cashlez.module.payment;

import com.cashlez.android.sdk.CLErrorResponse;
import com.cashlez.android.sdk.CLPayment;
import com.cashlez.android.sdk.CLTransferDetail;
import com.cashlez.android.sdk.bean.TransactionType;
import com.cashlez.android.sdk.companion.printer.CLPrinterCompanion;
import com.cashlez.android.sdk.companion.reader.CLReaderCompanion;
import com.cashlez.android.sdk.payment.CLDimoResponse;
import com.cashlez.android.sdk.payment.CLMandiriPayResponse;
import com.cashlez.android.sdk.payment.CLPaymentResponse;
import com.cashlez.android.sdk.payment.CLTCashQRResponse;
import com.cashlez.android.sdk.payment.noncash.CLPaymentHandler;
import com.cashlez.android.sdk.payment.noncash.ICLPaymentHandler;
import com.cashlez.android.sdk.payment.noncash.ICLPaymentService;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class PaymentModule extends ReactContextBaseJavaModule implements ICLPaymentService, iPaymentModule {
    private final ReactApplicationContext reactContext;
    private ICLPaymentHandler mCLPaymentHandler;
    private Promise mPromise;

    public PaymentModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNCashlezPayment";
    }



    @ReactMethod
    @Override
    public void init(Promise promise) {
        if (mCLPaymentHandler == null) {
            this.mCLPaymentHandler = new CLPaymentHandler(getCurrentActivity(), null);
        }
    }


    @ReactMethod
    @Override
    public void doPayCash(String amount, String description, Promise promise) {
        this.mPromise = promise;

        CLPayment cashCLPayment = new CLPayment();
        cashCLPayment.setAmount(amount);
        cashCLPayment.setDescription(description);
        cashCLPayment.setTransactionType(TransactionType.CASH);

        mCLPaymentHandler.doProceedPayment(cashCLPayment);
    }

    @Override
    public void onCashPaymentSuccess(CLPaymentResponse clPaymentResponse) {
        String message = clPaymentResponse.getMessage();
        mPromise.resolve(message);
    }

    @Override
    public void onCashPaymentError(CLErrorResponse clErrorResponse) {
        String errorCode = "" + clErrorResponse.getErrorCode();
        String errorMessage = errorCode + ": " + clErrorResponse.getErrorMessage();
        mPromise.reject(errorCode, errorMessage);
    }



    @Override
    public void onReaderSuccess(CLReaderCompanion clReaderCompanion) {

    }

    @Override
    public void onReaderError(CLErrorResponse clErrorResponse) {

    }

    @Override
    public void onPrinterSuccess(CLPrinterCompanion clPrinterCompanion) {

    }

    @Override
    public void onPrinterError(CLErrorResponse clErrorResponse) {

    }

    @Override
    public void onInsertCreditCard(CLPaymentResponse clPaymentResponse) {

    }

    @Override
    public void onInsertOrSwipeDebitCard(CLPaymentResponse clPaymentResponse) {

    }

    @Override
    public void onSwipeDebitCard(CLPaymentResponse clPaymentResponse) {

    }

    @Override
    public void onPaymentSuccess(CLPaymentResponse clPaymentResponse) {

    }

    @Override
    public void onRemoveCard(String s) {

    }

    @Override
    public void onProvideSignatureRequest(CLPaymentResponse clPaymentResponse) {

    }

    @Override
    public void onPaymentError(CLErrorResponse clErrorResponse, String s) {

    }

    @Override
    public void onProvideSignatureError(CLErrorResponse clErrorResponse) {

    }

    @Override
    public void onTCashQRSuccess(CLTCashQRResponse cltCashQRResponse) {

    }

    @Override
    public void onTCashQRError(CLErrorResponse clErrorResponse) {

    }

    @Override
    public void onCheckTCashQRStatusSuccess(CLTCashQRResponse cltCashQRResponse) {

    }

    @Override
    public void onCheckTCashQRStatusError(CLErrorResponse clErrorResponse) {

    }

    @Override
    public void onDimoSuccess(CLDimoResponse clDimoResponse) {

    }

    @Override
    public void onDimoError(CLErrorResponse clErrorResponse) {

    }

    @Override
    public void onCheckDimoStatusSuccess(CLDimoResponse clDimoResponse) {

    }

    @Override
    public void onCheckDimoStatusError(CLErrorResponse clErrorResponse) {

    }

    @Override
    public void onCancelDimoSuccess(CLDimoResponse clDimoResponse) {

    }

    @Override
    public void onCancelDimoError(CLErrorResponse clErrorResponse) {

    }

    @Override
    public void onMandiriPaySuccess(CLMandiriPayResponse clMandiriPayResponse) {

    }

    @Override
    public void onMandiriPayError(CLErrorResponse clErrorResponse) {

    }

    @Override
    public void onCheckMandiriPayStatusSuccess(CLMandiriPayResponse clMandiriPayResponse) {

    }

    @Override
    public void onCheckMandiriPayStatusError(CLErrorResponse clErrorResponse) {

    }

    @Override
    public void onPaymentDebitTransferRequestConfirmation(CLTransferDetail clTransferDetail) {

    }
}
