package com.twog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.IOException;

import Fragments.AboutUsFragment;
import Fragments.ConfersationFragment;
import Fragments.FeedFragment;
import Fragments.MessagesFragment;
import Fragments.ProfileFragment;
import Fragments.SupportFragment;
import Models.Auth;
import Models.Conversation;
import Models.OnSingleUserRetrievedListener;
import Models.User;
import Models.UserDataProvider;
import Registration.SignIn_Activity;
import Registration.SignUp_Activity;
import UserEdit.EditUserActivity;

public class MainActivity extends AppCompatActivity {
    private Auth auth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private View headerView;
    private TextView navUsername;
    private TextView navEmail;
    private ProgressBar navNameLoading;
    public static final int REQUEST_CODE_SHOW_LOGIN_SNACKBAR = 1;
    private Button signOutBtn;
    private FeedFragment feedFragment;
    private AboutUsFragment aboutUsFragment;
    private SupportFragment supportFragment;
    private ProfileFragment profileFragment;
    private MessagesFragment messagesFragment;
    private ConfersationFragment confersationFragment;
    public Context context;
    public static String FILTER = "No";
    public static User DISPLAY_USER;
    private ProgressBar progressBar;
    private ImageView headerImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //declarations-------------------------------------------------
        toolbar = findViewById(R.id.toolbar);
        auth = new Auth(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.header_nameLbl);
        headerImageView=headerView.findViewById(R.id.header_imageView);
        //signOutBtn = findViewById(R.id.btnSignOut);
        context = this;
        navEmail = headerView.findViewById(R.id.header_emailLbl);
        navNameLoading = headerView.findViewById(R.id.header_nameLoading);
        progressBar= findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        DISPLAY_USER=auth.getSignedInUser();
        //fragments-declarations-------------------------------
        feedFragment = new FeedFragment();
        aboutUsFragment=new AboutUsFragment();
        supportFragment=new SupportFragment();
        profileFragment=new ProfileFragment();
        messagesFragment=new MessagesFragment();
        confersationFragment= new ConfersationFragment();
        //toolbar-----------------------------------------------
        toolbar.setTitle("Loading...");
        setSupportActionBar(toolbar);
        //navigation drawer menu--------------------------------------
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //test123
        String key = auth.getSignedInUserKey();
        checkUser();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Fragment fragment = feedFragment;
                switch (id) {
                    case R.id.nav_feed:
                        fragment = feedFragment;
                        toolbar.setTitle("Feed");
                        item.setChecked(false);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_profile:
                        DISPLAY_USER=auth.getSignedInUser();
                        fragment = profileFragment;
                        toolbar.setTitle("Profile");
                        item.setChecked(false);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_messages:
                        fragment=messagesFragment;
                        toolbar.setTitle("Messages");
                        item.setChecked(false);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_about:
                        fragment=aboutUsFragment;
                        toolbar.setTitle("About us");
                        item.setChecked(false);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_support:
                        fragment=supportFragment;
                        toolbar.setTitle("Support");
                        item.setChecked(false);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_signout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(drawerLayout.getContext());
                        builder.setTitle("Sign Out?");
                        builder.setMessage("Are you sure?");
                        builder.setPositiveButton("Yes, sign me out.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                auth.signOut();
                                checkUser();
                                Intent intent = new Intent(getApplicationContext(), SignIn_Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                toolbar.setTitle("Feed");
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, feedFragment).commit();
                            }
                        });
                        builder.show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                if(auth.getSignedInUser()==null){
                    Toast.makeText(context, "Please wait", Toast.LENGTH_SHORT).show();
                    toolbar.setTitle("Loading...");
                }else{
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
                return true;
            }
        });

    }
    /*public void refreshPfp(){
        User currentUser = auth.getSignedInUser();
        if (currentUser.getAvatarBase64() != null && !currentUser.getAvatarBase64().isEmpty()) {
            byte[] decodedString = Base64.decode(currentUser.getAvatarBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            headerImageView.setImageBitmap(decodedByte);
        }
    }*/
    public void setProfileFragment(User user){
        toolbar.setTitle(user.getName());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(), "setprofile").commit();
    }
    public void setMessagesFragment(){
        toolbar.setTitle("Messages");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, messagesFragment).commit();
    }
    public void setconversationFragment(Conversation conversation){
        if(!conversation.getUser1ID().equals(auth.getSignedInUserKey())){
            String key = conversation.getUser1ID();
            UserDataProvider.getInstance().getUserByKey(key, new OnSingleUserRetrievedListener() {
                @Override
                public void OnUserRetrieved(User user) {
                    if(user!=null){
                        toolbar.setTitle(user.getName());
                    }
                }
            });
        }else{
            String key = conversation.getUser2ID();
            UserDataProvider.getInstance().getUserByKey(key, new OnSingleUserRetrievedListener() {
                @Override
                public void OnUserRetrieved(User user) {
                    if(user!=null){
                        toolbar.setTitle(user.getName());
                    }
                }
            });
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConfersationFragment(conversation), "conv").commit();
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else if(getSupportFragmentManager().findFragmentByTag("setprofile")!=null && getSupportFragmentManager().findFragmentByTag("setprofile").isVisible() ){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, feedFragment).commit();
            toolbar.setTitle("Feed");
        }else if(getSupportFragmentManager().findFragmentByTag("conv")!=null && getSupportFragmentManager().findFragmentByTag("conv").isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, messagesFragment).commit();
            toolbar.setTitle("Messages");
        }
        else {
            AlertDialog.Builder builder= new AlertDialog.Builder(context);
            builder.setTitle("Exit?");
            builder.setMessage("Please select an option below");
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
    }

    @Override
    protected void onDestroy() {
        if (auth.getRememberUser().equals("false")) {
            FirebaseAuth.getInstance().signOut();
            auth.signOut();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (auth.getRememberUser().equals("false")) {
            FirebaseAuth.getInstance().signOut();
            auth.signOut();
        }
        super.onStop();
    }

    private void checkUser() {
        if (auth.getSignedInUserKey() == null) {
            Intent intent = new Intent(this, SignUp_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(intent, 1);
        } else {
            OnSingleUserRetrievedListener listener = new OnSingleUserRetrievedListener() {
                @Override
                public void OnUserRetrieved(User user) {
                    User u = user;
                    if (user.getLogin() != null) {
                        String govno = auth.getRememberUser();
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser.isEmailVerified()) {
                            user.setHasEmailVerified(true);
                            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("hasEmailVerified").setValue(true);
                        }
                        /**
                         * Если auth.getRememberUser() = false, значит при выходе из приложения( полном его закрытии), юзер будет разлогинен.
                         * Если true, то ничего не проихойдет
                         * **/
                        if (auth.getRememberUser().equals("false")) {
                            progressBar.setVisibility(View.INVISIBLE);
                            builderSet(user);
                            auth.signInWithoutRemembering(user);
                            if (auth.getSignedInUser().getAvatarBase64() != null && !auth.getSignedInUser().getAvatarBase64().isEmpty()) {
                                byte[] decodedString = Base64.decode(auth.getSignedInUser().getAvatarBase64(), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                headerImageView.setImageBitmap(decodedByte);
                            }
                            try {
                                navNameLoading.setVisibility(View.INVISIBLE);
                            } catch (Exception ex) {

                                Toast.makeText(MainActivity.this, "Unexpected progressBar minor error. Ignore it if it isn't crashing app. Else, restart the app", Toast.LENGTH_SHORT).show();
                            }
                            navUsername.setText(user.getName());
                            navEmail.setText(user.getEmail());
                            if (user.getName().length() > 10) {
                                navUsername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                            } else if (user.getName().length() > 15) {
                                navUsername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                            } else {
                                navUsername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                            }
                            if (user.getEmail().length() > 10) {
                                navEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                            } else if (user.getEmail().length() > 15) {
                                navEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            } else {
                                navEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                            }
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Welcome back, " + auth.getSignedInUser().getName(), BaseTransientBottomBar.LENGTH_SHORT);
                            snackbar.setAction("Hide", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                            start();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            navUsername.setText(user.getName());
                            if (user.getName().length() > 10) {
                                navUsername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                            } else if (user.getName().length() > 15) {
                                navUsername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                            } else {
                                navUsername.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                            }

                            navEmail.setText(user.getEmail());
                            if (user.getEmail().length() > 10) {
                                navEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                            } else if (user.getEmail().length() > 15) {
                                navEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);
                            } else {
                                navEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                            }
                            builderSet(user);
                            auth.signIn(user);
                            if (auth.getSignedInUser().getAvatarBase64() != null && !auth.getSignedInUser().getAvatarBase64().isEmpty()) {
                                byte[] decodedString = Base64.decode(auth.getSignedInUser().getAvatarBase64(), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                headerImageView.setImageBitmap(decodedByte);
                            }
                            /**
                             * Здесь я добавил трай кетч, ибо почему-то на виртуальном телефоне возникала ошибка при определении progressBar`а
                             * На физическом устрйостве такого не наблюдалось
                             * **/
                            try {
                                navNameLoading.setVisibility(View.INVISIBLE);
                            } catch (Exception ex) {
                                Toast.makeText(MainActivity.this, "Unexpected progressBar minor error. Ignore it if it isn't crashing app. Else, restart the app", Toast.LENGTH_SHORT).show();
                            }
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Welcome back, " + auth.getSignedInUser().getName(), BaseTransientBottomBar.LENGTH_SHORT);
                            snackbar.setAction("Hide", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                            start();
                        }
                    }
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            };
            UserDataProvider.getInstance().getUserByKey(auth.getSignedInUserKey(), listener);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                break;
            case R.id.menu_no:
                FILTER = "No";
                feedFragment.refreshFeed();
                Toast.makeText(context, FILTER + " filter set", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_few:
                FILTER = "One";
                feedFragment.refreshFeed();
                Toast.makeText(context, FILTER + " filter set", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_many:
                FILTER = "Half";
                feedFragment.refreshFeed();
                Toast.makeText(context, FILTER + " filter set", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_guide:

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("How filters work?");
                builder.setMessage("It's simple:\n\nOption 'No' applies no filters, so all users will appear in your feed\n\nOption 'At least one' only shows you users, that have at least one mutual interests with you" +
                        "\n\nOption '50% or more' only shows you users, that have at least half of your interests");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void builderSet(User user) {
        try {
            if (user.getName().isEmpty() || user.getAge() == -1 || user.getUserInfo().isEmpty() || user.getInterests()==null) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser.isEmailVerified()) {
                    user.setHasEmailVerified(true);
                    FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("hasEmailVerified").setValue(true);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Time to make your profile perfect");
                builder.setMessage("Please fill your name, age, choose interests and (optionally) set your profile picture\n\nYou must enter your name and age to let everybody know who are they talking to");
                builder.setPositiveButton("Edit my profile", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, EditUserActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Sign out and exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        auth.signOut();
                    }
                });
                builder.setNeutralButton("Exit app", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        } catch (Exception ex) {

        }
    }

    private void start() {
        toolbar.setTitle("Feed");
        loadFragment(feedFragment);
    }

    /**
     * Здесь я добавил этот метод потому, что возникала ошибка источник которой я не установил даже путем дебага
     * Сейчас фрагмент подгружается нормально
     **/
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);

            if (!getSupportFragmentManager().isDestroyed())
                transaction.commit();
            return true;
        }
        return false;
    }
}