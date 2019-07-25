package id.codecapital.cashlez.module.payment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.cashlez.android.sdk.CLCardProcessingMode;
import com.cashlez.android.sdk.CLErrorResponse;
import com.cashlez.android.sdk.CLPayment;
import com.cashlez.android.sdk.CLTransferDetail;
import com.cashlez.android.sdk.bean.ApprovalStatus;
import com.cashlez.android.sdk.bean.TransactionType;
import com.cashlez.android.sdk.companion.printer.CLPrinterCompanion;
import com.cashlez.android.sdk.companion.reader.CLReaderCompanion;
import com.cashlez.android.sdk.payment.CLDimoResponse;
import com.cashlez.android.sdk.payment.CLMandiriPayResponse;
import com.cashlez.android.sdk.payment.CLPaymentResponse;
import com.cashlez.android.sdk.payment.CLTCashQRResponse;
import com.cashlez.android.sdk.payment.CLVerificationMode;
import com.cashlez.android.sdk.payment.noncash.CLPaymentHandler;
import com.cashlez.android.sdk.payment.noncash.ICLPaymentHandler;
import com.cashlez.android.sdk.payment.noncash.ICLPaymentService;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class PaymentModule extends ReactContextBaseJavaModule implements ICLPaymentService, iPaymentModule {
    protected static final int MY_PERMISSIONS_REQUEST = 8;

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
    public void init() {
        if (mCLPaymentHandler == null) {
            this.mCLPaymentHandler = new CLPaymentHandler(getCurrentActivity(), null);
        }
    }



    /* ---------------------
     *  CHECK READER
     * --------------------- */

    @ReactMethod
    @Override
    public void doCheckReaderCompanion(Promise promise) {
        this.mPromise = promise;

        if (mCLPaymentHandler == null) {
            Toast.makeText(reactContext, "Payment Handler is null. Please init the Payment", Toast.LENGTH_LONG);
        } else {
            mCLPaymentHandler.doCheckReaderCompanion();
        }
    }

    @ReactMethod
    @Override
    public void doUnregisterReceiver() {
        mCLPaymentHandler.doUnregisterReceiver();
    }

    @Override
    public void onReaderSuccess(final CLReaderCompanion clReaderCompanion) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = String.format("Reader Connection Status: %s\nMessage: %s",
                        String.valueOf(clReaderCompanion.isConnected()),
                        clReaderCompanion.getMessage());
                mPromise.resolve(message);
            }
        });
    }

    @Override
    public void onReaderError(final CLErrorResponse clErrorResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String errorCode = "" + clErrorResponse.getErrorCode();
                String errorMessage = errorCode + ": " + clErrorResponse.getErrorMessage();
                mPromise.reject(errorCode, errorMessage);
            }
        });
    }



    /* ---------------------
     *  CHECK PRINTER
     * --------------------- */

    @ReactMethod
    @Override
    public void doCheckPrinterCompanion(Promise promise) {
        this.mPromise = promise;
        mCLPaymentHandler.doCheckPrinterCompanion();
    }

    @Override
    public void onPrinterSuccess(CLPrinterCompanion clPrinterCompanion) {
        String message = String.format("Printer Connection Status: %s\nMessage: %s",
                String.valueOf(clPrinterCompanion.isConnected()),
                clPrinterCompanion.getMessage());
        mPromise.resolve(message);
    }

    @Override
    public void onPrinterError(CLErrorResponse clErrorResponse) {
        String errorCode = "" + clErrorResponse.getErrorCode();
        String errorMessage = errorCode + ": " + clErrorResponse.getErrorMessage();
        mPromise.reject(errorCode, errorMessage);
    }



    /* ---------------------
     *  PAYMENT INITIALIZE
     * --------------------- */

    @ReactMethod
    @Override
    public void doConnectLocationProvider() {
        mCLPaymentHandler.doConnectLocationProvider();
    }

    @ReactMethod
    @Override
    public void doStopUpdateLocation() {
        mCLPaymentHandler.doStopUpdateLocation();
    }

    @ReactMethod
    @Override
    public void doStartPayment(Promise promise) {
        this.mPromise = promise;

        if (mCLPaymentHandler == null) {
            Toast.makeText(reactContext, "Payment Handler is null. Please init the Payment", Toast.LENGTH_LONG);
        } else {
            mCLPaymentHandler.doStartPayment(this);
        }
    }

    @ReactMethod
    @Override
    public void doCloseCompanionConnection() {
        mCLPaymentHandler.doCloseCompanionConnection();
    }



    /* --------------------
    *   CASH PAYMENT
    *  -------------------- */

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



    /* ------------------------
     *   LOCAL CARD PAYMENT
     *  ----------------------- */

    @ReactMethod
    @Override
    public void doPayLocalCardPin(String amount, String description, Promise promise) {
        this.mPromise = promise;

        CLPayment localPinCLPayment = new CLPayment();
        localPinCLPayment.setAmount(amount);
        localPinCLPayment.setDescription(description);
        localPinCLPayment.setCardProcessingMode(CLCardProcessingMode.LOCAL_CARD);
        localPinCLPayment.setVerificationMode(CLVerificationMode.PIN);

        mCLPaymentHandler.doProceedPayment(localPinCLPayment);
    }

    @ReactMethod
    @Override
    public void doPayLocalCardNoPin(String amount, String description, Promise promise) {
        this.mPromise = promise;

        CLPayment localNoPinCLPayment = new CLPayment();
        localNoPinCLPayment.setAmount(amount);
        localNoPinCLPayment.setDescription(description);
        localNoPinCLPayment.setCardProcessingMode(CLCardProcessingMode.LOCAL_CARD);
        localNoPinCLPayment.setVerificationMode(CLVerificationMode.NO_PIN);

        mCLPaymentHandler.doProceedPayment(localNoPinCLPayment);
    }

    @ReactMethod
    @Override
    public void doPayInternationalCard(String amount, String description, Promise promise) {
        this.mPromise = promise;

        CLPayment internationalCardCLPayment = new CLPayment();
        internationalCardCLPayment.setAmount(amount);
        internationalCardCLPayment.setDescription(description);
        internationalCardCLPayment.setCardProcessingMode(CLCardProcessingMode.INTERNATIONAL_CARD);

        mCLPaymentHandler.doProceedPayment(internationalCardCLPayment);
    }

    @Override
    public void onInsertCreditCard(CLPaymentResponse clPaymentResponse) {
        Toast.makeText(this.reactContext, clPaymentResponse.getMessage(), Toast.LENGTH_LONG);
    }

    @Override
    public void onInsertOrSwipeDebitCard(CLPaymentResponse clPaymentResponse) {
        Toast.makeText(this.reactContext, clPaymentResponse.getMessage(), Toast.LENGTH_LONG);
    }

    @Override
    public void onSwipeDebitCard(CLPaymentResponse clPaymentResponse) {
        Toast.makeText(this.reactContext, clPaymentResponse.getMessage(), Toast.LENGTH_LONG);
    }

    @Override
    public void onRemoveCard(final String removeCardResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(reactContext, removeCardResponse, Toast.LENGTH_LONG);
            }
        });
    }


    @Override
    public void onPaymentSuccess(CLPaymentResponse clPaymentResponse) {
        String message = clPaymentResponse.getMessage();

        if (clPaymentResponse.getTransactionStatus() != null
                && (clPaymentResponse.getTransactionStatus() == ApprovalStatus.APPROVED.getCode()
                    || clPaymentResponse.getTransactionStatus() == ApprovalStatus.PENDING.getCode())) {
                mPromise.resolve(message);
        } else {
            String errorCode = "" + clPaymentResponse.getErrorCode();
            mPromise.reject(errorCode, message);
        }
    }

    @Override
    public void onPaymentError(CLErrorResponse clErrorResponse, String transactionId) {
        String errorCode = "" + clErrorResponse.getErrorCode();
        String errorMessage = errorCode + ": Transaction ID " + transactionId + " --> " + clErrorResponse.getErrorMessage();
        mPromise.reject(errorCode, errorMessage);
    }


    @Override
    public void onProvideSignatureRequest(CLPaymentResponse clPaymentResponse) {

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
