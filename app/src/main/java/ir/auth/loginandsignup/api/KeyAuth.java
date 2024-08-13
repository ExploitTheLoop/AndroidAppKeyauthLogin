package ir.auth.loginandsignup.api;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class KeyAuth {

    private final String appname;
    private final String ownerid;
    private final String version;
    private final String url;

    private String sessionid;
    private boolean initialized;
    private UserData userData;

    public KeyAuth(String appname, String ownerid, String version, String url) {
        this.appname = appname;
        this.ownerid = ownerid;
        this.version = version;
        this.url = url;
    }

    public UserData getUserData() {
        return userData;
    }

    private String sendPostRequest(String endpoint, String... params) {
        try {
            URL url = new URL(this.url + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            StringBuilder postData = new StringBuilder();
            postData.append("type=").append(endpoint);
            for (int i = 0; i < params.length; i += 2) {
                postData.append('&').append(params[i]).append('=').append(params[i + 1]);
            }

            OutputStream os = conn.getOutputStream();
            os.write(postData.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Callback Interfaces
    public interface InitCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public interface LoginCallback {
        void onSuccess(UserData userData);
        void onFailure(String message);
    }

    public interface UpgradeCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public interface LicenseCallback {
        void onSuccess(UserData userData);
        void onFailure(String message);
    }

    public interface BanCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public interface WebhookCallback {
        void onSuccess();
        void onFailure(String message);
    }

    // Methods
    public void init(InitCallback callback) {
        new Thread(() -> {
            String response = sendPostRequest("init",
                    "ver", version,
                    "name", appname,
                    "ownerid", ownerid
            );

            if (response != null) {
                try {
                    JSONObject responseJSON = new JSONObject(response);

                    if (responseJSON.getBoolean("success")) {
                        sessionid = responseJSON.getString("sessionid");
                        initialized = true;
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure(responseJSON.getString("message"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                }
            } else {
                if (callback != null) {
                    callback.onFailure("No response from server");
                }
            }
        }).start();
    }

    public void login(String username, String password, LoginCallback callback) {
        if (!initialized) {
            System.out.println("Please initialize first");
            return;
        }

        new Thread(() -> {
            String hwid = HWID.getHWID();

            String response = sendPostRequest("login",
                    "username", username,
                    "pass", password,
                    "hwid", hwid,
                    "sessionid", sessionid,
                    "name", appname,
                    "ownerid", ownerid
            );

            if (response != null) {
                try {
                    JSONObject responseJSON = new JSONObject(response);

                    if (responseJSON.getBoolean("success")) {
                        userData = new UserData(responseJSON);
                        if (callback != null) {
                            callback.onSuccess(userData);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure("Error: " + responseJSON.getString("message"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                }
            } else {
                if (callback != null) {
                    callback.onFailure("No response from server");
                }
            }
        }).start();
    }

    public void upgrade(String username, String key, UpgradeCallback callback) {
        if (!initialized) {
            System.out.println("Please initialize first");
            return;
        }

        new Thread(() -> {
            String hwid = HWID.getHWID();

            String response = sendPostRequest("upgrade",
                    "username", username,
                    "key", key,
                    "hwid", hwid,
                    "sessionid", sessionid,
                    "name", appname,
                    "ownerid", ownerid
            );

            if (response != null) {
                try {
                    JSONObject responseJSON = new JSONObject(response);

                    if (responseJSON.getBoolean("success")) {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure("Error: " + responseJSON.getString("message"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                }
            } else {
                if (callback != null) {
                    callback.onFailure("No response from server");
                }
            }
        }).start();
    }

    public void license(String key, LicenseCallback callback) {
        if (!initialized) {
            System.out.println("Please initialize first");
            return;
        }

        new Thread(() -> {
            String hwid = HWID.getHWID();

            String response = sendPostRequest("license",
                    "key", key,
                    "hwid", hwid,
                    "sessionid", sessionid,
                    "name", appname,
                    "ownerid", ownerid
            );

            if (response != null) {
                try {
                    JSONObject responseJSON = new JSONObject(response);

                    if (responseJSON.getBoolean("success")) {
                        userData = new UserData(responseJSON);
                        if (callback != null) {
                            callback.onSuccess(userData);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure("The license does not exist");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                }
            } else {
                if (callback != null) {
                    callback.onFailure("No response from server");
                }
            }
        }).start();
    }

    public void ban(BanCallback callback) {
        if (!initialized) {
            System.out.println("Please initialize first");
            return;
        }

        new Thread(() -> {
            String hwid = HWID.getHWID();

            String response = sendPostRequest("ban",
                    "sessionid", sessionid,
                    "name", appname,
                    "ownerid", ownerid
            );

            if (response != null) {
                try {
                    JSONObject responseJSON = new JSONObject(response);

                    if (responseJSON.getBoolean("success")) {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure("Error: " + responseJSON.getString("message"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                }
            } else {
                if (callback != null) {
                    callback.onFailure("No response from server");
                }
            }
        }).start();
    }

    public void webhook(String webid, String param, WebhookCallback callback) {
        if (!initialized) {
            System.out.println("Please initialize first");
            return;
        }

        new Thread(() -> {
            String hwid = HWID.getHWID();

            String response = sendPostRequest("webhook",
                    "webid", webid,
                    "params", param,
                    "sessionid", sessionid,
                    "name", appname,
                    "ownerid", ownerid
            );

            if (response != null) {
                try {
                    JSONObject responseJSON = new JSONObject(response);

                    if (responseJSON.getBoolean("success")) {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure("Error: " + responseJSON.getString("message"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                }
            } else {
                if (callback != null) {
                    callback.onFailure("No response from server");
                }
            }
        }).start();
    }
}
