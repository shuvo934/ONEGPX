package com.shuvo.ttit.onegpx.login;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.shuvo.ttit.onegpx.MapsMenu.MapsActivity;
import com.shuvo.ttit.onegpx.R;
import com.shuvo.ttit.onegpx.progressbar.WaitProgress;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    TextInputEditText user;
    TextInputEditText pass;

    TextView login_failed;

    Button login;

    CheckBox checkBox;

    String userName = "";
    String password = "";

    String android_id = "";
    String model = "";
    String brand = "";
    String ipAddress = "";
    String hostUserName = "";
    String osName = "";

    WaitProgress waitProgress = new WaitProgress();
    private Boolean conn = false;
    private Boolean infoConnected = false;
    private Boolean connected = false;
    
    public static final String MyPREFERENCES = "UserPassTRKBK";
    public static final String user_emp_code = "nameKey";
    public static final String user_password = "passKey";
    public static final String checked = "trueFalse";

    SharedPreferences sharedpreferences;

    String getUserName = "";
    String getPassword = "";
    boolean getChecked = false;

    String userId = "";
    public static ArrayList<UserInfoList> userInfoLists;
    public static ArrayList<UserDesignation> userDesignations;
    public static String dateUptoFromDB = "";
    public static int CameraDeactivate = 0;
    public static int TrackDeactivate = 0;
    public static int WayPointDeactivate = 0;

    String emp_id = "";
    AppUpdateManager appUpdateManager;

    Logger logger = Logger.getLogger(Login.class.getName());

    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                    result -> {
                        if (result.getResultCode() != RESULT_OK) {

                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Login.this)
                                    .setTitle("Update Failed!")
                                    .setMessage("Failed to update the app. Please retry again.")
                                    .setIcon(R.drawable.activity_icon3)
                                    .setPositiveButton("Retry", (dialog, which) -> getAppUpdate())
                                    .setNegativeButton("Cancel", (dialog, which) -> finish());
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    });

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        appUpdateManager = AppUpdateManagerFactory.create(Login.this);
        userInfoLists = new ArrayList<>();
        userDesignations = new ArrayList<>();
        login_failed = findViewById(R.id.email_pass_miss);

        login = findViewById(R.id.log_in_button);

        user = findViewById(R.id.user_name_given_log_in);
        pass = findViewById(R.id.password_given_log_in);
        checkBox = findViewById(R.id.remember_checkbox);

        sharedpreferences = getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);
        getUserName = sharedpreferences.getString(user_emp_code,null);
        getPassword = sharedpreferences.getString(user_password,null);
        getChecked = sharedpreferences.getBoolean(checked,false);

        if (getUserName != null) {
            user.setText(getUserName);
        }
        if (getPassword != null) {
            pass.setText(getPassword);
        }
        checkBox.setChecked(getChecked);

        StringBuilder builder = new StringBuilder();
        builder.append("ANDROID: ").append(Build.VERSION.RELEASE);

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
                logger.log(Level.WARNING,e.getMessage(),e);
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append(": ").append(fieldName);
                //builder.append(" : ").append(fieldName).append(" : ");
                //builder.append("sdk=").append(fieldValue);
            }
        }

        System.out.println("OS: " + builder);
        //Log.d(LOG_TAG, "OS: " + builder.toString());

        //System.out.println("HOSTTTTT: " + getHostName());

        osName = builder.toString();

        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        System.out.println(android_id);

        model = android.os.Build.MODEL;

        brand = Build.BRAND;

        ipAddress = getIPAddress(true);

        hostUserName = getHostName("localhost");

        System.out.println("SSDSD: " + getHostName("localhost"));


        pass.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    pass.clearFocus();
                    closeKeyBoard();

                    login_failed.setVisibility(View.GONE);
                    userName = Objects.requireNonNull(user.getText()).toString();
                    password = Objects.requireNonNull(pass.getText()).toString();

                    if (!userName.isEmpty() && !password.isEmpty()) {
                        getUser();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Give User Name and Password", Toast.LENGTH_SHORT).show();
                    }

                    return false; // consume.
                }
            }
            return false;
        });

        login.setOnClickListener(v -> {

            closeKeyBoard();

            login_failed.setVisibility(View.GONE);
            userName = Objects.requireNonNull(user.getText()).toString();
            password = Objects.requireNonNull(pass.getText()).toString();

            if (!userName.isEmpty() && !password.isEmpty()) {
                getUser();
            } else {
                Toast.makeText(getApplicationContext(), "Please Give User Name and Password", Toast.LENGTH_SHORT).show();
            }

        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("বন্ধ করুন/QUIT")
                        .setMessage("আপনি কি অ্যাপ থেকে বের হতে চান?/Are you sure want to quit?")
                        .setPositiveButton("হ্যাঁ/YES", (dialog, which) -> System.exit(0))
                        .setNegativeButton("না/NO", (dialog, which) -> {

                        });
                AlertDialog alert = builder.create();
                alert.setCanceledOnTouchOutside(false);
                alert.setCancelable(false);
                alert.show();
            }
        });

        getAppUpdate();
    }

    private void getAppUpdate() {
        System.out.println("HELLO1");
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE))  {
//                try {
//                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
//                            (IntentSenderForResultStarter) activityResultLauncher,
//                            AppUpdateOptions
//                                    .newBuilder(IMMEDIATE)
//                                    .build(),
//                            10101);
//                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
//                            Dashboard.this,AppUpdateOptions.newBuilder(IMMEDIATE).build(),
//                            101010);
//                } catch (IntentSender.SendIntentException e) {
//                    logger.log(Level.WARNING,e.getMessage(),e);
//                }
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                        activityResultLauncher, AppUpdateOptions
                                .newBuilder(IMMEDIATE)
                                .build());
            }
            else {
                System.out.println("No update available");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
//                                try {
//                                    appUpdateManager.startUpdateFlowForResult(
//                                            appUpdateInfo,
//                                            this,
//                                            AppUpdateOptions.defaultOptions(IMMEDIATE),
//                                            10101);
//                                } catch (IntentSender.SendIntentException e) {
//                                    logger.log(Level.WARNING,e.getMessage(),e);
//                                }
                                appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                                        activityResultLauncher,AppUpdateOptions
                                                .newBuilder(IMMEDIATE)
                                                .build());
                            }
                        });
    }

    public static String getHostName(String defValue) {
        try {
            @SuppressLint("DiscouragedPrivateApi") Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            return defValue;
        }
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    private void closeKeyBoard () {
        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent event){
        closeKeyBoard();
        return super.onTouchEvent(event);
    }

    public void getUser() {
        waitProgress.show(getSupportFragmentManager(), "WaitBar");
        waitProgress.setCancelable(false);
        conn = false;
        connected = false;
        infoConnected = false;
        userId = "";
        userInfoLists = new ArrayList<>();
        userDesignations = new ArrayList<>();

        String get_url = "http://103.56.208.123:8086/terrain/tr_kabikha/user_login/oxUserLogin?user_name="+userName+"&pass="+password;

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);

        StringRequest getUserId = new StringRequest(Request.Method.GET, get_url, response -> {
            conn = true;
            try {
                connected = true;
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject userIdInfo = array.getJSONObject(i);
                        userId = userIdInfo.getString("val").equals("null") ? "" : userIdInfo.getString("val");
                    }
                }

                if (userId.isEmpty() || userId.equals("-1")) {
                    goToDashboard();
                }
                else {
                    getUserDetails(userId);
                }
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                goToDashboard();
            }

        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            goToDashboard();
        });

        requestQueue.add(getUserId);
    }

    public void getUserDetails(String u_id) {
        String userInfoUrl = "http://103.56.208.123:8086/terrain/tr_kabikha/user_login/getUserInfo?user_id="+u_id;

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);

        StringRequest userInfoRequest = new StringRequest(Request.Method.GET, userInfoUrl, response -> {
            conn = true;
            try {
                connected = true;
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject userInfo = array.getJSONObject(i);
                        String usr_name = userInfo.getString("usr_name").equals("null") ? "" : userInfo.getString("usr_name");
                        String usr_fname = userInfo.getString("usr_fname").equals("null") ? "" : userInfo.getString("usr_fname");
                        String usr_lname = userInfo.getString("usr_lname").equals("null") ? "" : userInfo.getString("usr_lname");
                        String usr_email = userInfo.getString("usr_email").equals("null") ? "" : userInfo.getString("usr_email");
                        String usr_contact = userInfo.getString("usr_contact").equals("null") ? "" : userInfo.getString("usr_contact");
                        String usr_emp_id = userInfo.getString("usr_emp_id").equals("null") ? "" : userInfo.getString("usr_emp_id");
                        String dist_name = userInfo.getString("dist_name").equals("null") ? "" : userInfo.getString("dist_name");
                        String thana_name = userInfo.getString("thana_name").equals("null") ? "" : userInfo.getString("thana_name");
                        emp_id = usr_emp_id;
                        userInfoLists.add(new UserInfoList(usr_name,usr_fname,usr_lname,usr_email,usr_contact,
                                usr_emp_id,dist_name, thana_name));
                        dateUptoFromDB = userInfo.getString("sub_date").equals("null") ? "" : userInfo.getString("sub_date");
                        CameraDeactivate = userInfo.getInt("camera_flag");
                        WayPointDeactivate = userInfo.getInt("waypoint_flag");
                        TrackDeactivate = userInfo.getInt("track_flag");
                    }
                }
                getUserData();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                goToDashboard();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            goToDashboard();
        });

        requestQueue.add(userInfoRequest);
    }

    public void getUserData() {
        String designationUrl = "http://103.56.208.123:8086/terrain/tr_kabikha/user_login/getUserDesignations?emp_id="+emp_id;
        String login_log_url = "http://103.56.208.123:8086/terrain/tr_kabikha/user_login/login_log";

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);

        StringRequest loginLogReq = new StringRequest(Request.Method.POST, login_log_url, response -> {
            try {
                conn = true;
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                if (string_out.equals("Successfully Created")) {
                    infoConnected = true;
                }
                else {
                    System.out.println(string_out);
                    connected = false;
                }
                goToDashboard();

            } catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                goToDashboard();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            goToDashboard();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String u_name = userInfoLists.get(0).getUserName();
                headers.put("P_IULL_USER_ID",u_name);
                headers.put("P_IULL_CLIENT_HOST_NAME",brand+" "+model);
                headers.put("P_IULL_CLIENT_IP_ADDR",ipAddress);
                headers.put("P_IULL_CLIENT_HOST_USER_NAME",hostUserName);
                headers.put("P_IULL_SESSION_USER_ID",userId);
                headers.put("P_IULL_OS_NAME",osName);
                headers.put("P_LOG_TYPE","4");
                return headers;
            }
        };

        StringRequest designationRequest = new StringRequest(Request.Method.GET, designationUrl, response -> {
            conn = true;
            try {
                connected = true;
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject desigInfo = array.getJSONObject(i);
                        String jsm_code = desigInfo.getString("jsm_code");
                        String temp_title = desigInfo.getString("temp_title");
                        String jsd_id = desigInfo.getString("jsd_id");
                        String jsd_objective = desigInfo.getString("jsd_objective");
                        String dept_name = desigInfo.getString("dept_name");
                        String divm_name = desigInfo.getString("divm_name");
                        String desig_name = desigInfo.getString("desig_name");
                        String desig_priority = desigInfo.getString("desig_priority");
                        String joiningdate = desigInfo.getString("joiningdate");
                        String divm_id = desigInfo.getString("divm_id");
                        userDesignations.add(new UserDesignation(jsm_code,temp_title,jsd_id,jsd_objective,dept_name,
                                divm_name,desig_name,desig_priority,joiningdate,divm_id));
                    }
                }
                requestQueue.add(loginLogReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                goToDashboard();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            goToDashboard();
        });

        requestQueue.add(designationRequest);
    }

    public void goToDashboard() {
        waitProgress.dismiss();
        if (conn) {
            if (connected) {
                if (!userId.equals("-1") && !userId.isEmpty()) {
                    if (infoConnected) {
                        if (checkBox.isChecked()) {
                            System.out.println("Remembered");
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove(user_emp_code);
                            editor.remove(user_password);
                            editor.remove(checked);
                            editor.putString(user_emp_code,userName);
                            editor.putString(user_password,password);
                            editor.putBoolean(checked,true);
                            editor.apply();
                            editor.commit();
                        } else {
                            System.out.println("Not Remembered");
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove(user_emp_code);
                            editor.remove(user_password);
                            editor.remove(checked);

                            editor.apply();
                            editor.commit();
                        }

                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    else {
                        getUser();
                    }
                }
                else {
                    login_failed.setVisibility(View.VISIBLE);
                }
                conn = false;
                connected = false;
                infoConnected = false;
            }
            else {
                AlertDialog dialog = new AlertDialog.Builder(Login.this)
                        .setMessage("There is a network issue in the server. Please Try later.")
                        .setPositiveButton("Retry", null)
                        .show();

                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(v -> {

                    getUser();
                    dialog.dismiss();
                });
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog dialog = new AlertDialog.Builder(Login.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Retry", null)
                    .show();
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {

                getUser();
                dialog.dismiss();
            });
        }
    }

//    public boolean isConnected () {
//        boolean connected = false;
//        boolean isMobile = false;
//        try {
//            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo nInfo = cm.getActiveNetworkInfo();
//            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
//            return connected;
//        } catch (Exception e) {
//            Log.e("Connectivity Exception", e.getMessage());
//        }
//        return connected;
//    }
//
//    public boolean isOnline () {
//
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//            int exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        } catch (IOException | InterruptedException e) {
//            logger.log(Level.WARNING,e.getMessage(),e);
//        }
//
//        return false;
//    }
//
//    public class CheckLogin extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            waitProgress.show(getSupportFragmentManager(), "WaitBar");
//            waitProgress.setCancelable(false);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if (isConnected() && isOnline()) {
//
//                LoginQuery();
//                if (connected) {
//                    conn = true;
//                    message = "Internet Connected";
//                }
//
//            } else {
//                conn = false;
//                message = "Not Connected";
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            waitProgress.dismiss();
//            if (conn) {
//
//                if (!userId.equals("-1")) {
//                    if (infoConnected) {
//
//                        if (checkBox.isChecked()) {
//                            System.out.println("Remembered");
//                            SharedPreferences.Editor editor = sharedpreferences.edit();
//                            editor.remove(user_emp_code);
//                            editor.remove(user_password);
//                            editor.remove(checked);
//                            editor.putString(user_emp_code,userName);
//                            editor.putString(user_password,password);
//                            editor.putBoolean(checked,true);
//                            editor.apply();
//                            editor.commit();
//                        } else {
//                            System.out.println("Not Remembered");
//                            SharedPreferences.Editor editor = sharedpreferences.edit();
//                            editor.remove(user_emp_code);
//                            editor.remove(user_password);
//                            editor.remove(checked);
//
//                            editor.apply();
//                            editor.commit();
//                        }
//
////                        user.setText("");
////                        pass.setText("");
////                        checkBox.setChecked(false);
//
//
//
//                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//                        startActivity(intent);
//                        finish();
//
//                    } else {
//                        new CheckLogin().execute();
//                    }
//
//                } else {
//
//                    login_failed.setVisibility(View.VISIBLE);
//                }
//                conn = false;
//                connected = false;
//                infoConnected = false;
//
//
//            } else {
//                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//                AlertDialog dialog = new AlertDialog.Builder(Login.this)
//                        .setMessage("Please Check Your Internet Connection")
//                        .setPositiveButton("Retry", null)
//                        .show();
//
////                dialog.setCancelable(false);
////                dialog.setCanceledOnTouchOutside(false);
//                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positive.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        new CheckLogin().execute();
//                        dialog.dismiss();
//                    }
//                });
//            }
//        }
//    }
//
//    public void LoginQuery () {
//
//
//        try {
//            this.connection = createConnection();
//            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//            userInfoLists = new ArrayList<>();
//            userDesignations = new ArrayList<>();
//
//            Statement stmt = connection.createStatement();
//            StringBuffer stringBuffer = new StringBuffer();
//
//
//            ResultSet rs = stmt.executeQuery("select VALIDATE_USER_DB('" + userName + "',HAMID_ENCRYPT_DESCRIPTION_PACK.HEDP_ENCRYPT('" + password + "')) val from dual\n");
//
//
//            while (rs.next()) {
//                stringBuffer.append("USER ID: " + rs.getString(1) + "\n");
//                userId = rs.getString(1);
//
//            }
//
//            if (!userId.equals("-1")) {
//
//                ResultSet dateVerify = stmt.executeQuery("Select TO_CHAR(USR_DATE_UPTO,'DD-MON-RR hh:mm:ss am') SUB_DATE, NVL(USR_CAMERA_DEACTIVATE_FLAG,0), NVL(USR_WAYPOINT_DEACTIVATE_FLAG,0), NVL(USR_TRACK_DEACTIVATE_FLAG,0) from ISP_USER where USR_ID = "+userId+"");
//
//                while (dateVerify.next()) {
//                    dateUptoFromDB = dateVerify.getString(1);
//                    CameraDeactivate = dateVerify.getInt(2);
//                    WayPointDeactivate = dateVerify.getInt(3);
//                    TrackDeactivate = dateVerify.getInt(4);
//
//                }
//
//                ResultSet resultSet = stmt.executeQuery("Select USR_NAME, USR_FNAME, USR_LNAME, USR_EMAIL, USR_CONTACT, USR_EMP_ID, \n" +
//                        "(Select DIST_NAME FROM DISTRICT WHERE DIST_ID = USR_DIST_ID) DIST_NAME,\n" +
//                        "(Select DD_THANA_NAME FROM DISTRICT_DTL WHERE DD_ID = USR_DD_ID) THANA_NAME\n" +
//                        "FROM ISP_USER\n" +
//                        "WHERE USR_ID = "+userId+"");
//
//                while (resultSet.next()) {
//                    emp_id = resultSet.getString(6);
//                    userInfoLists.add(new UserInfoList(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),resultSet.getString(7),resultSet.getString(8)));
//                }
//
//                resultSet.close();
//
//                ResultSet resultSet1 = stmt.executeQuery("SELECT DISTINCT JOB_SETUP_MST.JSM_CODE, JOB_SETUP_MST.JSM_NAME TEMP_TITLE, \n" +
//                        "JOB_SETUP_DTL.JSD_ID, JOB_SETUP_DTL.JSD_OBJECTIVE, DEPT_MST.DEPT_NAME, \n" +
//                        "DIVISION_MST.DIVM_NAME, DESIG_MST.DESIG_NAME, DESIG_MST.DESIG_PRIORITY, (Select TO_CHAR(MAX(EMP_JOB_HISTORY.JOB_ACTUAL_DATE),'DD-MON-YYYY') from EMP_JOB_HISTORY) as JOININGDATE, DIVISION_MST.DIVM_ID \n" +
//                        "FROM JOB_SETUP_MST, JOB_SETUP_DTL, DEPT_MST, DIVISION_MST, DESIG_MST, EMP_JOB_HISTORY\n" +
//                        "WHERE ((JOB_SETUP_DTL.JSD_JSM_ID = JOB_SETUP_MST.JSM_ID)\n" +
//                        " AND (JOB_SETUP_MST.JSM_DIVM_ID = DIVISION_MST.DIVM_ID)\n" +
//                        " AND (DEPT_MST.DEPT_ID = JOB_SETUP_MST.JSM_DEPT_ID)\n" +
//                        " AND (JOB_SETUP_MST.JSM_DESIG_ID = DESIG_MST.DESIG_ID))\n" +
//                        " AND JOB_SETUP_DTL.JSD_ID = (SELECT JOB_JSD_ID\n" +
//                        "                            FROM EMP_JOB_HISTORY\n" +
//                        "                            WHERE JOB_ID = (SELECT MAX(JOB_ID) FROM EMP_JOB_HISTORY WHERE JOB_EMP_ID = " + emp_id + "))" +
//                        "AND EMP_JOB_HISTORY.JOB_JSD_ID = JOB_SETUP_DTL.JSD_ID");
//
//                while (resultSet1.next()) {
//                    userDesignations.add(new UserDesignation(resultSet1.getString(1), resultSet1.getString(2), resultSet1.getString(3), resultSet1.getString(4), resultSet1.getString(5), resultSet1.getString(6), resultSet1.getString(7), resultSet1.getString(8), resultSet1.getString(9), resultSet1.getString(10)));
//                }
//
//                resultSet1.close();
//
//                ResultSet resultSet2 = stmt.executeQuery("SELECT SYS_CONTEXT ('USERENV', 'SESSIONID') --INTO P_IULL_SESSION_ID\n" +
//                        "   FROM DUAL\n");
//
//                while (resultSet2.next()) {
//                    System.out.println("SESSION ID: "+ resultSet2.getString(1));
//                    sessionId = resultSet2.getString(1);
//                }
//
//                resultSet2.close();
//
//
////                ResultSet resultSet6 = stmt.executeQuery("SELECT SYS_CONTEXT ('USERENV', 'HOST') --INTO P_IULL_CLIENT_HOST_NAME\n" +
////                        "   FROM DUAL\n");
////
////                while (resultSet6.next()) {
////                    System.out.println("HOST NAME: "+ resultSet6.getString(1));
////                    hostUserName = resultSet6.getString(1);
////                }
//
////                resultSet6.close();
//
//                String userName = userInfoLists.get(0).getUserName();
//
//                CallableStatement callableStatement1 = connection.prepareCall("{call USERLOGINDTL(?,?,?,?,?,?,?,?,?)}");
//                callableStatement1.setString(1,userName);
//                callableStatement1.setString(2, brand+" "+model);
//                callableStatement1.setString(3,ipAddress);
//                callableStatement1.setString(4,hostUserName);
//                callableStatement1.setInt(5,Integer.parseInt(userId));
//                callableStatement1.setInt(6,Integer.parseInt(sessionId));
//                callableStatement1.setString(7,"1");
//                callableStatement1.setString(8,osName);
//                callableStatement1.setInt(9,4);
//                callableStatement1.execute();
//
//                callableStatement1.close();
//
//
//                infoConnected = true;
//
//            }
//
//
//            System.out.println(stringBuffer);
//
//
//            connected = true;
//
//            connection.close();
//
//        } catch (Exception e) {
//
//            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
//            Log.i("ERRRRR", e.getLocalizedMessage());
//            logger.log(Level.WARNING,e.getMessage(),e);
//        }
//
//    }

}