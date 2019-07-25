package id.codecapital.cashlez.module.ovo;

import android.widget.Toast;

import com.cashlez.android.sdk.CLErrorResponse;
import com.cashlez.android.sdk.CLPayment;
import com.cashlez.android.sdk.bean.ApprovalStatus;
import com.cashlez.android.sdk.payment.CLPaymentResponse;
import com.cashlez.android.sdk.payment.ovo.CLOvoHandler;
import com.cashlez.android.sdk.payment.ovo.ICLOvoHandler;
import com.cashlez.android.sdk.payment.ovo.ICLOvoService;
import com.cashlez.android.sdk.payment.voidpayment.CLVoidResponse;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import id.codecapital.cashlez.module.Const;

public class OvoModule extends ReactContextBaseJavaModule implements ICLOvoService, iOvoModule {
    private final ReactApplicationContext reactContext;
    private ICLOvoHandler mCLOvoHandler;
    private CLPaymentResponse mCLPaymentResponse;
    private Promise mPromise;

    public OvoModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNCashlezOvo";
    }



    @ReactMethod
    @Override
    public void init() {
        if (mCLOvoHandler == null) {
            this.mCLOvoHandler = new CLOvoHandler(getCurrentActivity(), null, this);
        }
    }



    @ReactMethod
    @Override
    public void doStartOvoHandler() {
        mCLOvoHandler.doStartOvoHandler();
    }

    @ReactMethod
    @Override
    public void doResumeOvoHandler() {
        mCLOvoHandler.doResumeOvoHandler();
    }

    @ReactMethod
    @Override
    public void doStopOvoHandler() {
        mCLOvoHandler.doStopOvoHandler();
    }



    @ReactMethod
    @Override
    public void doPayOvo(String amount, String description, String phone, Promise promise) {
        this.mPromise = promise;

        CLPayment ovoCLPayment = new CLPayment();
        ovoCLPayment.setAmount(amount);
        ovoCLPayment.setDescription(description);
        ovoCLPayment.setCustomerMobilePhone(phone);

        mCLOvoHandler.doOvoPayment(ovoCLPayment);
    }

    @Override
    public void onOvoPaymentSuccess(CLPaymentResponse clPaymentResponse) {
        mCLPaymentResponse = clPaymentResponse;

        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, true);
        map.putString(Const.RETURN_CODE_KEY, null);
        map.putString(Const.RETURN_MESSAGE_KEY, "Please proceed the Payment on the OVO Apps");

        mPromise.resolve(map);
    }

    @Override
    public void onOvoPaymentError(CLErrorResponse clErrorResponse) {
        String errorCode = "" + clErrorResponse.getErrorCode();
        String errorMessage = errorCode + ": " + clErrorResponse.getErrorMessage();

        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
        map.putString(Const.RETURN_CODE_KEY, errorCode);
        map.putString(Const.RETURN_MESSAGE_KEY, errorMessage);

        mPromise.resolve(map);
    }



    @ReactMethod
    @Override
    public void doOvoInquiry(Promise promise) {
        this.mPromise = promise;

        if (mCLPaymentResponse != null) {
            mCLOvoHandler.doOvoInquiry(mCLPaymentResponse);
        } else {
            Toast.makeText(reactContext, "Invalid Payment Response", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onOvoInquirySuccess(CLPaymentResponse clPaymentResponse) {
        int transactionStatus = clPaymentResponse.getTransactionStatus();
        String approvalMessage = ApprovalStatus.getStatus(transactionStatus).getMessage();
        WritableMap map = new WritableNativeMap();

        if (transactionStatus == ApprovalStatus.APPROVED.getCode()) {
            map.putBoolean(Const.RETURN_SUCCESS_KEY, true);
        } else {
            map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
        }

        map.putString(Const.RETURN_CODE_KEY, null);
        map.putString(Const.RETURN_MESSAGE_KEY, approvalMessage);

        mPromise.resolve(map);
    }

    @Override
    public void onOvoInquiryError(CLErrorResponse clErrorResponse) {
        String errorCode = "" + clErrorResponse.getErrorCode();
        String errorMessage = errorCode + ": " + clErrorResponse.getErrorMessage();

        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
        map.putString(Const.RETURN_CODE_KEY, errorCode);
        map.putString(Const.RETURN_MESSAGE_KEY, errorMessage);

        mPromise.resolve(map);
    }



    @Override
    public void onOvoVoidPaymentSuccess(CLVoidResponse clVoidResponse) {

    }

    @Override
    public void onOvoVoidPaymentError(CLErrorResponse clErrorResponse) {

    }
}
