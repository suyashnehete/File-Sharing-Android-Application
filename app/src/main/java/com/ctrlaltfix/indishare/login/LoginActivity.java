package com.ctrlaltfix.indishare.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ctrlaltfix.indishare.ChatSection.ChatHomeActivity;
import com.ctrlaltfix.indishare.R;
import com.ctrlaltfix.indishare.SendAnywhere;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static com.google.firebase.auth.PhoneAuthProvider.*;

public class LoginActivity extends AppCompatActivity {

    Button verifyNumber;
    Button proceedToProfile;
    EditText phoneNumber;
    EditText verificationCode;
    EditText countryCode;
    LinearLayout loading;

    FirebaseAuth mAuth;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        verifyNumber = findViewById(R.id.verifyNumber);
        proceedToProfile = findViewById(R.id.proceedToProfile);
        phoneNumber = findViewById(R.id.phoneNumber);
        verificationCode = findViewById(R.id.verificationCode);
        loading = findViewById(R.id.loading);
        countryCode = findViewById(R.id.countryCode);
        FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        mAuth.useAppLanguage();

        verifyNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhoneNumberVerification(countryCode.getText().toString().trim()+phoneNumber.getText().toString().trim());
            }
        });

        proceedToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPhoneNumberWithCode(mVerificationId, verificationCode.getText().toString());
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("suyash", "onVerificationCompleted:" + credential.getSmsCode());
                verificationCode.setText(credential.getSmsCode());
                loading.setVisibility(View.GONE);
                // [START_EXCLUDE silent]
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("suyash", e.toString());

                verifyNumber.setVisibility(View.VISIBLE);
                verificationCode.setVisibility(View.GONE);
                proceedToProfile.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                if (e.toString().equals("com.google.firebase.FirebaseTooManyRequestsException: We have blocked all requests from this device due to unusual activity. Try again later.")){
                    Toast.makeText(LoginActivity.this, "Account Blocked For 24hrs due to unusual activity.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                loading.setVisibility(View.GONE);
                verifyNumber.setVisibility(View.GONE);
                verificationCode.setVisibility(View.VISIBLE);
                proceedToProfile.setVisibility(View.VISIBLE);
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("suyash", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };

    }

    private void startPhoneNumberVerification(String phoneNumber) {
        loading.setVisibility(View.VISIBLE);
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        loading.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == null) {
                                startActivity(
                                        new Intent(LoginActivity.this, AfterLoginProfileActivity.class)
                                                .putExtra("go",getIntent().getStringExtra("go"))
                                );
                            }else{
                                startActivity(
                                        new Intent(LoginActivity.this, getIntent().getStringExtra("go").equals("chat") ? ChatHomeActivity.class :SendAnywhere.class)
                                );
                            }
                        } else {
                            Log.d("suyash", "Login failed: "+task.getException().toString());
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                        loading.setVisibility(View.GONE);
                    }
                });
    }

}