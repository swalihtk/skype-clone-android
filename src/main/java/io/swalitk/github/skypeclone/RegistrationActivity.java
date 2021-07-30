package io.swalitk.github.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {

    private EditText et_mobilenumber, et_otp;
    private Button btn_register;
    private LinearLayout linearLayout;
    private CountryCodePicker countryCodePicker;
    private String mobilenumber="";
    private String status="";

    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String authCode="";
    private ProgressDialog dialog;

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() !=null){
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        et_mobilenumber=findViewById(R.id.et_phone_number);
        et_otp=findViewById(R.id.et_otp);
        linearLayout=findViewById(R.id.linearLayout_mobile_number);
        btn_register=findViewById(R.id.btn_register);
        countryCodePicker=findViewById(R.id.ccp_country_picker);

        dialog=new ProgressDialog(RegistrationActivity.this);
        mAuth=FirebaseAuth.getInstance();
        countryCodePicker.registerCarrierNumberEditText(et_mobilenumber);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobilenumber=countryCodePicker.getFullNumberWithPlus();
                if(btn_register.getText().equals("Submit") || status.equals("Code Send")){
                    String otp=et_otp.getText().toString();
                    if(otp.equals("")){
                        Toast.makeText(RegistrationActivity.this, "Please enter otp number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dialog.setTitle("Verifying otp");
                    dialog.setMessage("Please wait..");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    PhoneAuthCredential credential=PhoneAuthProvider.getCredential(authCode, otp);
                    signInWithPhoneAuthCredential(credential);
                }else{
                    if(mobilenumber.equals("")){
                        Toast.makeText(RegistrationActivity.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dialog.setTitle("Sending otp");
                    dialog.setMessage("Please wait..");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    PhoneAuthOptions options=PhoneAuthOptions.newBuilder()
                            .setCallbacks(mCallbacks)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(RegistrationActivity.this)
                            .setPhoneNumber(mobilenumber)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                dialog.dismiss();
                Toast.makeText(RegistrationActivity.this, "Verification completed successfully", Toast.LENGTH_SHORT).show();
                    signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                dialog.dismiss();
                Toast.makeText(RegistrationActivity.this, "Otp is error", Toast.LENGTH_SHORT).show();
                btn_register.setText("Continue");
                status="";
                linearLayout.setVisibility(View.VISIBLE);
                et_otp.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                authCode=s;
                resendingToken=forceResendingToken;
                btn_register.setText("Submit");
                status="Code Sent";
                linearLayout.setVisibility(View.GONE);
                et_otp.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        };
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            finish();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(RegistrationActivity.this, "Something wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}