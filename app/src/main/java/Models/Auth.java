package Models;

import android.content.Context;
import android.content.SharedPreferences;

public class Auth {
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences sp1;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editor1;
    private static User currentUser;
    public static final String USER_KEY="key";
    public static final String REMEMBER_USER="true";
    public Auth(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(USER_KEY, Context.MODE_PRIVATE);
        sp1=context.getSharedPreferences(REMEMBER_USER, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor1 = sp1.edit();
    }

    public void signIn(User u) {
        editor1.putString(REMEMBER_USER, "true");
        editor.putString(USER_KEY, u.getKey());
        editor.commit();
        editor1.commit();
        currentUser = u;
    }
    public void signInWithoutRemembering(User u){
        editor.putString(USER_KEY, u.getKey());
        editor1.putString(REMEMBER_USER, "false");
        editor.commit();
        editor1.commit();
        currentUser = u;
    }
    public void signOut() {
        editor1.putString(REMEMBER_USER, null);
        editor.putString(USER_KEY, null);
        editor.commit();
        editor1.commit();
        currentUser = null;
    }
    public User getSignedInUser() {
        return currentUser;
    }

    public String getSignedInUserKey() {
        String key = sp.getString(USER_KEY, null);
        return key;
    }
    public String getRememberUser(){
        return sp1.getString(REMEMBER_USER, "");
    }
}
