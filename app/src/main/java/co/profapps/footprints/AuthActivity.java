package co.profapps.footprints;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.profapps.footprints.util.AppUtils;

public class AuthActivity extends AppCompatActivity {
    private static final String TAG = AuthActivity.class.getSimpleName();

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private CallbackManager callbackManager;
    private int authListenerNotifyCalls = 0;

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_auth);
        setupFirebaseAuthentication();
        setupUI();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupFirebaseAuthentication() {
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                authListenerNotifyCalls++;

                if (user != null) {
                     if (authListenerNotifyCalls == 2) {
                         startMainActivity();
                     }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void setupUI() {
        // Customize action bar on recent windows
        AppUtils.setAppBarColorOnRecentWindows(this);

        // Style status bar and hide action bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this,
                R.color.colorDarkGray));

        if (getSupportActionBar() != null && getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
        }

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            startMainActivity();
                        } else {
                            AppUtils.showDialog(AuthActivity.this,
                                    getString(R.string.dialog_error_title),
                                    task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}
