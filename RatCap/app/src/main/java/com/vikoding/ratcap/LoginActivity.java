package com.vikoding.ratcap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.transition.Visibility;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // UI references.
    private EditText mNameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordView2;
    private View mStatusContainer;
    private Switch mStatusSwitch;
    private TextView mStatusText;
    private View mProgressView;
    private View mLoginFormView;

    private boolean isRegistering = false;
    private boolean pressedRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (pressedRegister) {
                        Log.d("Firebase", "onAuthStateChanged:signed_in:" + user.getUid());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };

        mNameView = (EditText) findViewById(R.id.name);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView2 = (EditText) findViewById(R.id.password2);
        mStatusSwitch = (Switch) findViewById(R.id.status_switch);
        mStatusText = (TextView) findViewById(R.id.status_text);
        mStatusContainer = findViewById(R.id.status_container);

        mStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (buttonView.isChecked()) {
                    mStatusText.setText("Status: Admin");
                } else {
                    mStatusText.setText("Status: User");
                }
            }
        });

        final Button mSignInButton = (Button) findViewById(R.id.login_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        final TextView registerText = (TextView) findViewById(R.id.register_text);
        registerText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isRegistering = !isRegistering;
                updateVisibility(true);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        updateVisibility(false);
    }

    /**
     * Updates the visibility of the different inputs based on the type of authentication: login vs registration
     * @param animated if the change has to be animated or not
     */
    public void updateVisibility(boolean animated) {
        final TextView registerText = (TextView) findViewById(R.id.register_text);
        final Button mSignInButton = (Button) findViewById(R.id.login_button);
        if (!isRegistering) {
            // Inside Login
            if (animated) {
                mLoginFormView.animate().rotationYBy(90f).setListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        registerText.setText("Don't have an account? Register");
                        mSignInButton.setText("Log in");
                        mPasswordView2.setVisibility(View.GONE);
                        mNameView.setVisibility(View.GONE);
                        mStatusContainer.setVisibility(View.GONE);
                        mLoginFormView.setRotationY(270f);
                        mLoginFormView.animate().rotationY(360f).setListener(null);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                });
            } else {
                registerText.setText("Don't have an account? Register");
                mSignInButton.setText("Log in");
                mPasswordView2.setVisibility(View.GONE);
                mStatusContainer.setVisibility(View.GONE);
                mNameView.setVisibility(View.GONE);
            }
        } else {
            // Inside Register
            if (animated) {
                mLoginFormView.animate().rotationYBy(90f).setListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        registerText.setText("Have an account? Login");
                        mSignInButton.setText("Register");
                        mPasswordView2.setVisibility(View.VISIBLE);
                        mNameView.setVisibility(View.VISIBLE);
                        mStatusContainer.setVisibility(View.VISIBLE);
                        mLoginFormView.setRotationY(270f);
                        mLoginFormView.animate().rotationY(360f).setListener(null);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                });
            } else {
                registerText.setText("Have an account? Login");
                mSignInButton.setText("Register");
                mPasswordView2.setVisibility(View.VISIBLE);
                mNameView.setVisibility(View.VISIBLE);
                mStatusContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * When the user clicks the button, try logging in or registering
     * Also checks for most types of errors
     */
    public void submit() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        pressedRegister = true;
        if (password.equals("")) {
            mPasswordView.setError("The password cannot be empty");
            return;
        }
        if (isRegistering) {
            if (!password.equals(mPasswordView2.getText().toString())) {
                mPasswordView2.setError("The passwords must match");
                return;
            }
            final String name = mNameView.getText().toString() + (mStatusSwitch.isChecked() ? "[admin]" : "");
            if (name.equals("")) {
                mNameView.setError("Name cannot be empty");
                return;
            }
            showProgress(true);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("FIREBASE", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                showProgress(false);
                            } else {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("FIREBASE", "User profile updated.");
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    intent.putExtra("NAME", name);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    showProgress(false);
                                                }
                                            }
                                        });
                            }

                        }
                    });
        } else {
            showProgress(true);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("FIREBASE", "signInWithEmail:onComplete:" + task.isSuccessful());
                            showProgress(false);
                            if (!task.isSuccessful()) {
                                Log.w("FIREBASE", "signInWithEmail:failed", task.getException());
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}

