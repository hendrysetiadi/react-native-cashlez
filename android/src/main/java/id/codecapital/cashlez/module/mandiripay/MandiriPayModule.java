package id.codecapital.cashlez.module.mandiripay;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.cashlez.android.sdk.CLErrorResponse;
import com.cashlez.android.sdk.CLPayment;
import com.cashlez.android.sdk.bean.ApprovalStatus;
import com.cashlez.android.sdk.bean.TransactionType;
import com.cashlez.android.sdk.companion.printer.CLPrinterCompanion;
import com.cashlez.android.sdk.payment.CLMandiriPayResponse;
import com.cashlez.android.sdk.payment.mandiripay.CLMandiriPayHandler;
import com.cashlez.android.sdk.payment.mandiripay.ICLMandiriPayHandler;
import com.cashlez.android.sdk.payment.mandiripay.ICLMandiriPayService;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.google.zxing.WriterException;

import id.codecapital.cashlez.module.Const;
import id.codecapital.cashlez.module.util.QRGenerator;

public class MandiriPayModule extends ReactContextBaseJavaModule implements iMandiriPayModule, ICLMandiriPayService {
    private final ReactApplicationContext reactContext;
    private ICLMandiriPayHandler mCLMandiriPayHandler;
    private CLMandiriPayResponse mCLMandiriPayResponse;
    private Promise mPromise;

    public MandiriPayModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNCashlezMandiriPay";
    }



    @ReactMethod
    @Override
    public void init() {
        if (mCLMandiriPayHandler == null) {
            this.mCLMandiriPayHandler = new CLMandiriPayHandler(getCurrentActivity(), null, this);
        }
    }



    @ReactMethod
    @Override
    public void doStartMandiriPayHandler() { mCLMandiriPayHandler.doStartMandiriPayHandler(); }

    @ReactMethod
    @Override
    public void doResumeMandiriPayHandler() {
        mCLMandiriPayHandler.doResumeMandiriPayHandler();
    }

    @ReactMethod
    @Override
    public void doStopMandiriPayHandler() {
        mCLMandiriPayHandler.doStopMandiriPayHandler();
    }



    @ReactMethod
    @Override
    public void doPayMandiriPay(String amount, String description, Promise promise) {
        this.mPromise = promise;

        // Create Transaction & Generate QR Code
        CLPayment mandiriPayCLPayment = new CLPayment();
        mandiriPayCLPayment.setAmount(amount);
        mandiriPayCLPayment.setDescription(description);
        mandiriPayCLPayment.setTransactionType(TransactionType.MPAY);

        mCLMandiriPayHandler.doProceedMandiriPayPayment(mandiriPayCLPayment);
    }

    @Override
    public void onMandiriPaySuccess(CLMandiriPayResponse clMandiriPayResponse) {
        mCLMandiriPayResponse = clMandiriPayResponse;

        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, true);
        map.putString(Const.RETURN_CODE_KEY, null);
        map.putString(Const.RETURN_MESSAGE_KEY, "Transaction has been created successfully");

        mPromise.resolve(map);
    }

    @Override
    public void onMandiriPayError(CLErrorResponse clErrorResponse) {
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
    public void doPrintQR(Promise promise) {
        try {
            Bitmap bitmap = QRGenerator.encodeAsBitmap(getCurrentActivity(), mCLMandiriPayResponse.getQrCodeContent());
            mCLMandiriPayHandler.doPrintQR(bitmap);

            WritableMap map = new WritableNativeMap();
            map.putBoolean(Const.RETURN_SUCCESS_KEY, true);
            map.putString(Const.RETURN_CODE_KEY, null);
            map.putString(Const.RETURN_MESSAGE_KEY, "Print QR Code success");

            mPromise.resolve(map);
        } catch (WriterException e) {
            e.printStackTrace();

            WritableMap map = new WritableNativeMap();
            map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
            map.putString(Const.RETURN_CODE_KEY, null);
            map.putString(Const.RETURN_MESSAGE_KEY, e.getMessage());

            mPromise.resolve(map);
        }
    }



    @ReactMethod
    @Override
    public void doCheckMandiriPayStatus(Promise promise) {
        this.mPromise = promise;

        if (mCLMandiriPayResponse != null) {
            mCLMandiriPayHandler.doCheckMandiriPayStatus(mCLMandiriPayResponse);
        } else {
            Toast.makeText(reactContext, "Invalid Payment Response", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCheckMandiriPayStatusSuccess(CLMandiriPayResponse clMandiriPayResponse) {
        mCLMandiriPayResponse = clMandiriPayResponse;

        int transactionStatus = clMandiriPayResponse.getTransactionStatus();
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
    public void onCheckMandiriPayStatusError(CLErrorResponse clErrorResponse) {
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
    public void doPrintMandiriPay(Promise promise) {
        this.mPromise = promise;

        if (mCLMandiriPayResponse != null) {
            mCLMandiriPayHandler.doPrintMandiriPay(mCLMandiriPayResponse);
        } else {
            Toast.makeText(reactContext, "Invalid Payment Response", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPrintingSuccess(CLPrinterCompanion clPrinterCompanion) {
        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, true);
        map.putString(Const.RETURN_CODE_KEY, null);
        map.putString(Const.RETURN_MESSAGE_KEY, clPrinterCompanion.getMessage());

        mPromise.resolve(map);
    }

    @Override
    public void onPrintingError(CLErrorResponse clErrorResponse) {
        String errorCode = "" + clErrorResponse.getErrorCode();
        String errorMessage = errorCode + ": " + clErrorResponse.getErrorMessage();

        WritableMap map = new WritableNativeMap();
        map.putBoolean(Const.RETURN_SUCCESS_KEY, false);
        map.putString(Const.RETURN_CODE_KEY, errorCode);
        map.putString(Const.RETURN_MESSAGE_KEY, errorMessage);

        mPromise.resolve(map);
    }
}
