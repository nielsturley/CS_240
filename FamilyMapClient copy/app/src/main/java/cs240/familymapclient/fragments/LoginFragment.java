package cs240.familymapclient.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cs240.familymapclient.DataCache;
import cs240.familymapclient.R;
import cs240.familymapclient.ServerProxy;
import request.AllEventRequest;
import request.AllPersonRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.AllEventResult;
import result.AllPersonResult;
import result.LoginResult;
import result.RegisterResult;

public class LoginFragment extends Fragment {

    private static final String LOGIN_SUCCESS_KEY = "LoginSuccessKey";

    private static String serverHost;
    private static String serverPort;

    private EditText serverHostField;
    private EditText serverPortField;
    private EditText usernameField;
    private EditText passwordField;
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText emailField;
    private RadioGroup genderField;
    private Button signInButton;
    private Button registerButton;

    private Listener listener;

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) { this.listener = listener; }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        serverHostField = view.findViewById(R.id.server_host_field);
        serverPortField = view.findViewById(R.id.server_port_field);
        usernameField = view.findViewById(R.id.user_name_field);
        passwordField = view.findViewById(R.id.password_field);
        firstNameField = view.findViewById(R.id.first_name_field);
        lastNameField = view.findViewById(R.id.last_name_field);
        emailField = view.findViewById(R.id.email_field);
        genderField = view.findViewById(R.id.gender_button);
        registerButton = view.findViewById(R.id.register_button);
        signInButton = view.findViewById(R.id.sign_in_button);

        serverHostField.addTextChangedListener(mTextWatcher);
        serverPortField.addTextChangedListener(mTextWatcher);
        usernameField.addTextChangedListener(mTextWatcher);
        passwordField.addTextChangedListener(mTextWatcher);
        firstNameField.addTextChangedListener(mTextWatcher);
        lastNameField.addTextChangedListener(mTextWatcher);
        emailField.addTextChangedListener(mTextWatcher);
        genderField.setOnCheckedChangeListener((radioGroup, i) -> checkFieldsForEmpty());

        checkFieldsForEmpty();

        registerButton.setOnClickListener(view12 -> {
            serverHost = serverHostField.getText().toString();
            serverPort = serverPortField.getText().toString();

            LoginHandler registerHandler = new LoginHandler(LoginFragment.this);

            String genderString;
            if (((RadioButton) requireView().findViewById(genderField.getCheckedRadioButtonId())).getText().toString().equals("Male")) {
                genderString = "m";
            }
            else {
                genderString = "f";
            }

            RegisterTask task = new RegisterTask(registerHandler,
                    usernameField.getText().toString(), passwordField.getText().toString(),
                    firstNameField.getText().toString(), lastNameField.getText().toString(),
                    emailField.getText().toString(), genderString);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(task);
        });

        signInButton.setOnClickListener(view1 -> {
            serverHost = serverHostField.getText().toString();
            serverPort = serverPortField.getText().toString();

            LoginHandler loginHandler = new LoginHandler(LoginFragment.this);

            LoginTask task = new LoginTask(loginHandler, usernameField.getText().toString(),
                    passwordField.getText().toString());
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(task);
        });
        return view;
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFieldsForEmpty();
        }
    };

    void checkFieldsForEmpty() {
        String s1 = serverHostField.getText().toString();
        String s2 = serverPortField.getText().toString();
        String s3 = usernameField.getText().toString();
        String s4 = passwordField.getText().toString();
        String s5 = firstNameField.getText().toString();
        String s6 = lastNameField.getText().toString();
        String s7 = emailField.getText().toString();
        String s8 = ""; //genderField (#8) is a radio button
        if (!(genderField.getCheckedRadioButtonId() == -1)) {
            s8 = ((RadioButton) requireView().findViewById(genderField.getCheckedRadioButtonId())).getText().toString();
        }

        //enable sign in button when serverHost, serverPort, username, and password are filled
        signInButton.setEnabled(!s1.isEmpty() &&
                !s2.isEmpty() &&
                !s3.isEmpty() &&
                !s4.isEmpty());
        //enable register button when all fields are filled
        registerButton.setEnabled(!s1.isEmpty() &&
                !s2.isEmpty() &&
                !s3.isEmpty() &&
                !s4.isEmpty() &&
                !s5.isEmpty() &&
                !s6.isEmpty() &&
                !s7.isEmpty() &&
                !s8.isEmpty());
    }

    private static class LoginHandler extends Handler {
        //honestly, this weak reference stuff was how to get that annoying error gone.
        //Not sure what it does, but it does.
        private final WeakReference<LoginFragment> loginFragment;

        public LoginHandler(LoginFragment loginFragment) {
            this.loginFragment = new WeakReference<>(loginFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            boolean success = bundle.getBoolean(LOGIN_SUCCESS_KEY);
            if (success) {
                DataCache data = DataCache.getInstance();
                String toast = "Successfully signed in\n"
                        + data.getCurrentUserPerson().getFirstName()
                        + " "
                        + data.getCurrentUserPerson().getLastName();
                Toast.makeText(loginFragment.get().getContext(), toast, Toast.LENGTH_SHORT).show();
                if (loginFragment.get().listener != null) {
                    loginFragment.get().listener.notifyDone();
                }
            }
            else {
                Toast.makeText(loginFragment.get().getContext(), "Unable to sign in", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //inner class, implements Runnable so it can access the database as a background task
    private static class LoginTask implements Runnable {
        private final android.os.Handler messageHandler;
        private final String username;
        private final String password;

        public LoginTask(android.os.Handler messageHandler, String username, String password) {
            this.messageHandler = messageHandler;
            this.username = username;
            this.password = password;
        }

        @Override
        public void run() {
            //login to the server
            LoginRequest loginRequest = new LoginRequest(username, password);
            ServerProxy proxy = new ServerProxy(serverHost, serverPort);
            LoginResult loginResult = proxy.login(loginRequest);

            boolean success = false;
            if (loginResult.isSuccess()) {
                String authToken = loginResult.getAuthtoken();
                DataCache data = DataCache.getInstance();
                data.setCurrentUserID(loginResult.getPersonID());

                AllPersonRequest allPersonRequest = new AllPersonRequest(authToken);
                AllPersonResult allPersonResult = proxy.getPeople(allPersonRequest);

                if (allPersonResult.isSuccess()) {
                    AllEventRequest allEventRequest = new AllEventRequest(authToken);
                    AllEventResult allEventResult = proxy.getEvents(allEventRequest);

                    if (allEventResult.isSuccess()) {
                        data.insertPeopleEventData(allPersonResult.getData(), allEventResult.getData());
                        success = true;
                        sendMessage(true);
                    }
                }
            }
            if (!success) {
                sendMessage(false);
            }
        }

        private void sendMessage(Boolean success) {
            Message msg = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean(LOGIN_SUCCESS_KEY, success);
            msg.setData(messageBundle);

            messageHandler.sendMessage(msg);
        }
    }

    private static class RegisterTask implements Runnable {
        private final android.os.Handler messageHandler;
        private final String username;
        private final String password;
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String gender;


        public RegisterTask(android.os.Handler messageHandler, String username, String password,
                            String firstName, String lastName, String email, String gender) {
            this.messageHandler = messageHandler;
            this.username = username;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.gender = gender;
        }

        @Override
        public void run() {
            //register with the server
            RegisterRequest registerRequest = new RegisterRequest(username, password, email,
                    firstName, lastName, gender);
            ServerProxy proxy = new ServerProxy(serverHost, serverPort);
            RegisterResult registerResult = proxy.register(registerRequest);

            boolean success = false;
            if (registerResult.isSuccess()) {
                String authToken = registerResult.getAuthtoken();
                DataCache data = DataCache.getInstance();
                data.setCurrentUserID(registerResult.getPersonID());


                AllPersonRequest allPersonRequest = new AllPersonRequest(authToken);
                AllPersonResult allPersonResult = proxy.getPeople(allPersonRequest);

                if (allPersonResult.isSuccess()) {
                    AllEventRequest allEventRequest = new AllEventRequest(authToken);
                    AllEventResult allEventResult = proxy.getEvents(allEventRequest);

                    if (allEventResult.isSuccess()) {
                        data.insertPeopleEventData(allPersonResult.getData(), allEventResult.getData());
                        success = true;
                        sendMessage(true);
                    }
                }
            }
            if (!success) {
                sendMessage(false);
            }
        }

        private void sendMessage(Boolean success) {
            Message msg = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean(LOGIN_SUCCESS_KEY, success);
            msg.setData(messageBundle);

            messageHandler.sendMessage(msg);
        }
    }
}