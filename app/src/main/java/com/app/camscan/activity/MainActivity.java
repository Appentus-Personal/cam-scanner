package com.app.camscan.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.tabs.TabLayout;
import com.itextpdf.text.pdf.PdfObject;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.app.camscan.R;
import com.app.camscan.adapter.AllGroupAdapter;
import com.app.camscan.adapter.DrawerItemAdapter;
import com.app.camscan.db.DBHelper;
import com.app.camscan.main_utils.Constant;
import com.app.camscan.models.DBModel;
import com.app.camscan.models.DrawerModel;
import com.app.camscan.utils.AdsUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class MainActivity extends BaseActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    int SELECT_PICTURE = 200;

    private static final String TAG = "MainActivity";
    public static MainActivity mainActivity;
    public Bitmap bitmap;

    public static ArrayList<Bitmap> idcardImgList = new ArrayList<>();

    protected AllGroupAdapter allGroupAdapter;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.IdentifyActivity.equals("PrivacyPolicyActivity")) {
//                startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
//                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("QRGenerateActivity")) {
                startActivity(new Intent(MainActivity.this, QRGenerateActivity.class));
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("QRReaderActivity")) {
                startActivity(new Intent(MainActivity.this, QRReaderActivity.class));
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("MainGalleryActivity")) {
                ImagePicker.with((Activity) MainActivity.this)
                        .setStatusBarColor("#25c4a4")
                        .setToolbarColor("#25c4a4")
                        .setBackgroundColor("#ffffff")
                        .setFolderMode(true)
                        .setFolderTitle("Gallery")
                        .setMultipleMode(true)
                        .setShowNumberIndicator(true)
                        .setAlwaysShowDoneButton(true)
                        .setMaxSize(1)
                        .setShowCamera(false)
                        .setLimitMessage("You can select up to 1 images")
                        .setRequestCode(100)
                        .start();
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("ScannerActivity")) {
                startActivity(new Intent(MainActivity.this, ScannerActivity.class));
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("GroupDocumentActivity")) {
                Intent intent2 = new Intent(MainActivity.this, GroupDocumentActivity.class);
                intent2.putExtra("current_group", current_group);
                startActivity(intent2);
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("CropDocumentActivity")) {
                startActivity(new Intent(MainActivity.this, CropDocumentActivity.class));
                Constant.IdentifyActivity = "";
            }
        }
    };

    public String current_group;
    protected String current_mode;
    public RadioGroup radioGroup;

    public Dialog dialogMore, dialogItem;

    public TextView sortBy, create_folder, shareAll, TextAbout;

    public ImageView iv_preview_crop;
    public ImageView clearText;

    public String TextPrivacy;

    public DBHelper dbHelper;
    protected DrawerItemAdapter drawerItemAdapter;
    private ArrayList<DrawerModel> drawerList = new ArrayList<>();
    private DrawerLayout drawer_ly;
    protected SharedPreferences.Editor editor;

    private EditText et_search, et_group_name;

    public EditText et_folder_name;

    public ArrayList<DBModel> groupList = new ArrayList<>();

    public ImageView iv_clear_txt;
    protected ImageView iv_close_search;
    protected ImageView iv_drawer;
    protected ImageView iv_group_camera;
    protected ImageView iv_more;
    protected ImageView iv_search;
    protected LinearLayoutManager layoutManager;
    private ListView lv_drawer;

    public LinearLayout ly_empty;

    public SharedPreferences preferences;
    private RelativeLayout rl_search_bar;

    public RecyclerView rv_group;
    protected String selected_sorting;
    protected int selected_sorting_pos;

    public String[] tabList = {"All Documents", "Documents", "ID Card", "Books", "Photos"};
    private TabLayout tag_tabs;
    protected ActionBarDrawerToggle toggle;

    public TextView tv_empty;
    private ImageView iv_folder;
    private ImageView iv_grid;

    @Override
    public void onResume() {
        new setAllGroupAdapter().execute(new String[0]);
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".PrivacyPolicyActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".QRGenerateActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".QRReaderActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".MainGalleryActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".ScannerActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".GroupDocumentActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".CropDocumentActivity"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        dbHelper = new DBHelper(this);
        preferences = getSharedPreferences("mypref", 0);
        init();
        bindView();
        AdsUtils.loadGoogleInterstitialAd(mainActivity, MainActivity.this);

    }


    private void init() {
        drawer_ly = (DrawerLayout) findViewById(R.id.drawer_ly);
        lv_drawer = (ListView) findViewById(R.id.lv_drawer);

        shareAll = (TextView) findViewById(R.id.share_all);
        sortBy = (TextView) findViewById(R.id.sort_by);
        create_folder = (TextView) findViewById(R.id.create_folder);

        iv_folder = (ImageView) findViewById(R.id.iv_list);
        iv_grid = (ImageView) findViewById(R.id.iv_grid);

        clearText = (ImageView) findViewById(R.id.clear_text);

        iv_drawer = (ImageView) findViewById(R.id.iv_drawer);

        iv_search = (ImageView) findViewById(R.id.iv_search);

        iv_more = (ImageView) findViewById(R.id.iv_more);
        rl_search_bar = (RelativeLayout) findViewById(R.id.rl_search_bar);
        iv_close_search = (ImageView) findViewById(R.id.iv_close_search);

        et_search = (EditText) findViewById(R.id.et_search);
        et_group_name = (EditText) findViewById(R.id.et_group_name);

        iv_preview_crop = (ImageView) findViewById(R.id.iv_preview_crop);

//        et_folder_name = (EditText) findViewById(R.id.et_folder_name);

        TextAbout = (TextView) findViewById(R.id.txtAbout);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        iv_clear_txt = (ImageView) findViewById(R.id.iv_clear_txt);
        tag_tabs = (TabLayout) findViewById(R.id.tag_tabs);
        rv_group = (RecyclerView) findViewById(R.id.rv_group);
        ly_empty = (LinearLayout) findViewById(R.id.ly_empty);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        iv_group_camera = (ImageView) findViewById(R.id.iv_group_camera);
    }


    private void bindView() {
        drawerList.add(new DrawerModel("Home", R.drawable.ic_home_main));
        drawerList.add(new DrawerModel("QR Code Scan", R.drawable.qr_code_ic));
        drawerList.add(new DrawerModel("QR Code Generate", R.drawable.ic_qr_generate));
        drawerList.add(new DrawerModel("About Us", R.drawable.ic_about_main));
        drawerList.add(new DrawerModel("Theme", R.drawable.ic_theme));
        drawerList.add(new DrawerModel("Terms and Condition", R.drawable.ic_tnc_main));
        drawerList.add(new DrawerModel("Privacy Policy", R.drawable.ic_pp_main));
        drawerList.add(new DrawerModel("Share App", R.drawable.ic_share_main));
        drawerList.add(new DrawerModel("Rate Us", R.drawable.ic_rate_main));

//        drawerList.add(new DrawerModel("Home", R.drawable.home));
//        drawerList.add(new DrawerModel("QR Code Scan", R.drawable.qr_code));
//        drawerList.add(new DrawerModel("QR Code Generate", R.drawable.qr_generate));
//        drawerList.add(new DrawerModel("About Us", R.drawable.about_us));
//        drawerList.add(new DrawerModel("Theme", R.drawable.ic_brightness_blue));
//        drawerList.add(new DrawerModel("Terms and Condition", R.drawable.terms_condition));
//        drawerList.add(new DrawerModel("Privacy Policy", R.drawable.privacy_policy));
//        drawerList.add(new DrawerModel("Share App", R.drawable.ic_share_app));
//        drawerList.add(new DrawerModel("Rate Us", R.drawable.rate_us));


/*//        drawerList.add(new DrawerModel("Theme" ,R.drawable.brightness_green));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            drawerList.add(new DrawerModel(getResources().getString(R.string.darkTheme), R.drawable.theme_drawer));
//        }

            toggle = new ActionBarDrawerToggle(this, drawer_ly, R.string.drawer_open, R.string.drawer_close);
            drawer_ly.addDrawerListener(toggle);*/

            drawerItemAdapter = new DrawerItemAdapter(this, drawerList);
            lv_drawer.setAdapter(drawerItemAdapter);

            setTab();
        }

    private void setTab() {

        for (String text : tabList) {
            TabLayout tabLayout = tag_tabs;
            tabLayout.addTab(tabLayout.newTab().setText((CharSequence) text)); }

        for (int i = 0; i < tag_tabs.getTabCount(); i++) {
            View tab = ((ViewGroup) tag_tabs.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, 30, 0);
            tab.requestLayout();
        }

        Constant.current_tag = "All Docs";

        tag_tabs.addOnTabSelectedListener((TabLayout.OnTabSelectedListener) new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e(MainActivity.TAG, "onTabSelected: " + Constant.current_tag);
                Constant.current_tag = tabList[tab.getPosition()];
                new setAllGroupAdapter().execute(new String[0]);
            }
        });


        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (i3 == 0) {
                    iv_more.setVisibility(View.VISIBLE);
                } else if (i3 == 1) {
                    iv_clear_txt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (groupList.size() > 0) {
                    filter(editable.toString());
                }
            }
        });
    }


    public void filter(String str) {
        ArrayList arrayList = new ArrayList();
        Iterator<DBModel> it = groupList.iterator();
        while (it.hasNext()) {
            DBModel next = it.next();
            if (next.getGroup_name().toLowerCase().contains(str.toLowerCase())) {
                arrayList.add(next);
            }
        }
        allGroupAdapter.filterList(arrayList);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_list:
                iv_folder.setVisibility(View.INVISIBLE);
                iv_grid.setVisibility(View.VISIBLE);
                editor = preferences.edit();
                editor.putString("ViewMode", "List");
                editor.apply();
                new setAllGroupAdapter().execute(new String[0]);
                return;


            case R.id.iv_grid:
                iv_grid.setVisibility(View.INVISIBLE);
                iv_folder.setVisibility(View.VISIBLE);
                editor = preferences.edit();
                editor.putString("ViewMode", "Grid");
                editor.apply();
                new setAllGroupAdapter().execute(new String[0]);
                return;

            /*   iv_grid.setImageResource(R.drawable.folder);*/

            case R.id.iv_clear_txt:
                et_search.setText("");
                iv_clear_txt.setVisibility(View.GONE);
                return;
            case R.id.iv_close_search:
                iv_search.setVisibility(View.VISIBLE);
                rl_search_bar.setVisibility(View.GONE);
                et_search.setText("");
                hideSoftKeyboard(et_search);
                return;
            case R.id.iv_drawer:
                drawer_ly.openDrawer(GravityCompat.START);
                return;
            case R.id.iv_group_camera:
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 2);
                return;

            case R.id.gallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                return;

            case R.id.iv_more:
                dialogMore = new Dialog(this);
                dialogMore.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogMore.setContentView(R.layout.main_file_menu);
                dialogMore.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//               dialog.getWindow().setLayout(-1, -2);
                dialogMore.setCanceledOnTouchOutside(true);
                dialogMore.setCancelable(true);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialogMore.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.BOTTOM;
                dialogMore.getWindow().setAttributes(lp);
                dialogMore.show();
                return;

            case R.id.iv_search:
                iv_search.setVisibility(View.GONE);
                rl_search_bar.setVisibility(View.VISIBLE);
                showSoftKeyboard(et_search);

                return;
            default:
                return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void openNewFolderDialog(String groupName) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.create_folder_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//      dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);


        et_folder_name = (EditText) dialog.findViewById(R.id.et_folder_name);

        String folder_name = "FineCapture" + Constant.getDateTime("_ddMMHHmmss");
        et_folder_name.setText(folder_name);

        ((TextView) dialog.findViewById(R.id.tv_create)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String finalFolderName = et_folder_name.getText().toString().trim();

                if (!finalFolderName.isEmpty()) {
                    String group_date = Constant.getDateTime("yyyy-MM-dd  hh:mm a");
                    if (groupName.isEmpty()) {        // for create new folder

                        try {
                            dbHelper.createDocTable(finalFolderName);
                            dbHelper.addGroup(new DBModel(finalFolderName, group_date, "", Constant.current_tag));
                        }
                        catch (Exception ignored){
                            Toast.makeText(MainActivity.this ,"Already Created" ,Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        dbHelper.createDocTable(finalFolderName);
                        dbHelper.addGroup(new DBModel(finalFolderName, group_date, "", Constant.current_tag));
                        // for move new folder
                        boolean isSuccess = false;
                        ArrayList<DBModel> allFileList = dbHelper.getGroupDocs(groupName);
                        for (int i = 0; i < allFileList.size(); i++) {
                            DBModel newDbModel = allFileList.get(i);
                            long isMove = dbHelper.moveGroupDoc(finalFolderName, newDbModel.getGroup_doc_img(), newDbModel.getGroup_doc_name(), "Insert text here...");
                            if (isMove <= 0) {
                                isSuccess = false;
                                break;
                            } else {
                                isSuccess = true;
                            }
                        }
                        if (isSuccess) {
                            Toast.makeText(mainActivity, "Move successfully", Toast.LENGTH_SHORT).show();
                            dbHelper.deleteGroup(groupName);
                        }
                    }
                    new setAllGroupAdapter().execute(new String[0]);
                    dialog.dismiss();
                } else {
                    Toast.makeText(mainActivity, "Folder name is required", Toast.LENGTH_SHORT).show();
                }

            }

        });
        ((TextView) dialog.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ((ImageView) dialog.findViewById(R.id.clear_text)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_folder_name.setText("");
            }

        });

        dialog.show();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i == 4) {
                        if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
                            Constant.IdentifyActivity = "QRGenerateActivity";
                            AdsUtils.showGoogleInterstitialAd(MainActivity.this, true);
                            return;
                        }
                        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 4);
                    }
                } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
                    Constant.IdentifyActivity = "QRReaderActivity";
                    AdsUtils.showGoogleInterstitialAd(MainActivity.this, true);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 3);
                }
            } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
                Constant.inputType = "Group";
                Constant.IdentifyActivity = "ScannerActivity";
                AdsUtils.showGoogleInterstitialAd(MainActivity.this, false);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 2);
            }
        } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
            Constant.inputType = "Group";
            Constant.IdentifyActivity = "MainGalleryActivity";
            AdsUtils.showGoogleInterstitialAd(MainActivity.this, true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.import_from_gallery:
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1);
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {

        if (i2 == RESULT_OK) {
            if (i == SELECT_PICTURE) {
                Uri selectedImageUri = intent.getData();

                try {
                  bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Constant.original != null) {
                    Constant.original.recycle();
                    System.gc();
                }
                Constant.current_camera_view = "Document";
                Constant.original = bitmap;
                Constant.IdentifyActivity = "CropDocumentActivity";
                AdsUtils.showGoogleInterstitialAd(MainActivity.this, true);

            }
        }

        if (ImagePicker.shouldHandleResult(i, i2, intent, 100)) {
            Iterator<Image> it = ImagePicker.getImages(intent).iterator();

            while (it.hasNext()) {
                Image next = it.next();
                if (Build.VERSION.SDK_INT >= 29) {
                    Glide.with(getApplicationContext()).asBitmap().load(next.getUri()).into(new SimpleTarget<Bitmap>() {
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            if (Constant.original != null) {
                                Constant.original.recycle();
                                System.gc();
                            }
                            Constant.current_camera_view = "Document";
                            Constant.original = bitmap;
                            Constant.IdentifyActivity = "CropDocumentActivity";
                            AdsUtils.showGoogleInterstitialAd(MainActivity.this, true);
                        }
                    });
                } else {
                    Glide.with(getApplicationContext()).asBitmap().load(next.getPath()).into(new SimpleTarget<Bitmap>() {
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            if (Constant.original != null) {
                                Constant.original.recycle();
                                System.gc();
                            }
                            Constant.current_camera_view = "Document";
                            Constant.original = bitmap;
                            Constant.IdentifyActivity = "CropDocumentActivity";
                            AdsUtils.showGoogleInterstitialAd(MainActivity.this, false);
                        }
                    });
                }

            }
        }
        super.onActivityResult(i, i2, intent);
    }


    /*Clicks on menu item */

    public void BottomClick(View view) {
        switch (view.getId()) {
            case R.id.share_all:
                new shareAllGroup().execute(new String[0]);
                return;

            case R.id.sort_by:

                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View v = LayoutInflater.from(this).inflate(R.layout.sort_by_dialog,null);

                RadioButton radio_btn_Ad = (RadioButton) v.findViewById(R.id.rb_Ad);
                RadioButton radio_btn_Dd = (RadioButton) v.findViewById(R.id.rb_Dd);
                RadioButton radio_btn_An = (RadioButton) v.findViewById(R.id.rb_An);
                RadioButton radio_btn_Dn = (RadioButton) v.findViewById(R.id.rb_Dn);
                dialog.setContentView(v);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//               dialog.getWindow().setLayout(-1, -2);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);

                if (selected_sorting.equals(Constant.ascending_date)) {
                    selected_sorting_pos = 0;
                } else if (selected_sorting.equals(Constant.descending_date)) {
                    selected_sorting_pos = 1;
                } else if (selected_sorting.equals(Constant.ascending_name)) {
                    selected_sorting_pos = 2;
                } else if (selected_sorting.equals(Constant.descending_name)) {
                    selected_sorting_pos = 3;
                }

                radio_btn_Ad.setOnClickListener(view1 -> {
                    mainActivity.editor = mainActivity.preferences.edit();
                    editor.putString("sortBy", Constant.ascending_date);
                    editor.apply();
                    new setAllGroupAdapter().execute(new String[0]);
                });



                radio_btn_Dd.setOnClickListener(view1 -> {
                    mainActivity.editor = mainActivity.preferences.edit();
                    editor.putString("sortBy", Constant.descending_date);
                    editor.apply();
                    new setAllGroupAdapter().execute(new String[0]);
                });


                radio_btn_An.setOnClickListener(view1 -> {
                    mainActivity.editor = mainActivity.preferences.edit();
                    editor.putString("sortBy", Constant.ascending_name);
                    editor.apply();
                    new setAllGroupAdapter().execute(new String[0]);
                });


                radio_btn_Dn.setOnClickListener(view1 -> {
                    mainActivity.editor = mainActivity.preferences.edit();
                    editor.putString("sortBy", Constant.descending_name);
                    editor.apply();
                    new setAllGroupAdapter().execute(new String[0]);
                });
                dialog.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.BOTTOM;
                dialog.getWindow().setAttributes(lp);
                return;


            case R.id.create_folder:
                openNewFolderDialog("");
                return;

            default:
                return;
        }

    }

    private class shareAllGroup extends AsyncTask<String, Void, String> {
        ArrayList<Uri> allPDFList;
        ProgressDialog progressDialog;

        private shareAllGroup() {
            allPDFList = new ArrayList<>();
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            Iterator it = groupList.iterator();
            while (it.hasNext()) {
                String group_name = ((DBModel) it.next()).getGroup_name();
                new ArrayList().clear();
                ArrayList<DBModel> groupDocs = dbHelper.getShareGroupDocs(group_name.replace(" ", ""));
                ArrayList arrayList = new ArrayList();
                Iterator<DBModel> it2 = groupDocs.iterator();
                while (it2.hasNext()) {
                    DBModel next = it2.next();
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        arrayList.add(BitmapFactory.decodeStream(new FileInputStream(next.getGroup_doc_img()), (Rect) null, options));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (arrayList.size() > 0) {
                    createPDFfromBitmap(group_name, arrayList, "temp");
                    allPDFList.add(BaseActivity.getURIFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getResources().getString(R.string.app_name) + "/" + group_name + ".pdf", MainActivity.this));
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND_MULTIPLE");
            intent.setType("application/pdf");
            intent.putExtra("android.intent.extra.STREAM", allPDFList);
            intent.putExtra("android.intent.extra.SUBJECT", "Share All");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent createChooser = Intent.createChooser(intent, (CharSequence) null);
            progressDialog.dismiss();
            startActivity(createChooser);
        }
    }

    public class setAllGroupAdapter extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        public setAllGroupAdapter() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(MainActivity.this);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.setMessage("Loading Data...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            if (Constant.current_tag.equals("All Documents")) {
                groupList = dbHelper.getAllGroups();
                return null;
            } else if (Constant.current_tag.equals("Documents")) {
                groupList = dbHelper.getGroupsByTag("Documents");
                return null;
            } else if (Constant.current_tag.equals("ID Card")) {
                groupList = dbHelper.getGroupsByTag("ID Card");
                return null;
            } else if (Constant.current_tag.equals("Books")) {
                groupList = dbHelper.getGroupsByTag("Books");
                return null;
            } else if (Constant.current_tag.equals("Personal Tag")) {
                groupList = dbHelper.getGroupsByTag("Personal Tag");
                return null;
            } else {
                groupList = dbHelper.getAllGroups();
                return null;
            }
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            if (groupList.size() > 0) {
                rv_group.setVisibility(View.VISIBLE);
                ly_empty.setVisibility(View.GONE);

                mainActivity.selected_sorting = mainActivity.preferences.getString("sortBy", Constant.descending_date);

                if (selected_sorting.equals(Constant.ascending_date)) {
                    Log.e(MainActivity.TAG, "onPostExecute: ascending_date");
                } else if (selected_sorting.equals(Constant.descending_date)) {
                    Collections.reverse(groupList);
                } else if (selected_sorting.equals(Constant.ascending_name)) {
                    Collections.sort(groupList, new SortByName());
                } else if (selected_sorting.equals(Constant.descending_name)) {
                    Collections.sort(groupList, new SortByName());
                }

                mainActivity.current_mode = mainActivity.preferences.getString("ViewMode", "List");
                if (current_mode.equals("Grid")) {
                    mainActivity.layoutManager = new GridLayoutManager((Context) mainActivity, 2, RecyclerView.VERTICAL, false);
                } else {
                    mainActivity.layoutManager = new LinearLayoutManager(mainActivity, RecyclerView.VERTICAL, false);
                }
                rv_group.setHasFixedSize(true);
                rv_group.setLayoutManager(layoutManager);
                mainActivity.allGroupAdapter = new AllGroupAdapter(mainActivity, mainActivity.groupList, current_mode);
                rv_group.setAdapter(allGroupAdapter);
            } else {
                mainActivity.selected_sorting = mainActivity.preferences.getString("sortBy", Constant.descending_date);
                rv_group.setVisibility(View.GONE);
                ly_empty.setVisibility(View.VISIBLE);
                if (Constant.current_tag.equals("All Documents")) {
                    tv_empty.setText(getResources().getString(R.string.all_docs_empty));
                } else if (Constant.current_tag.equals("Documents")) {
                    tv_empty.setText(getResources().getString(R.string.all_docs_empty));
                } else if (Constant.current_tag.equals("ID Card")) {
                    tv_empty.setText(getResources().getString(R.string.id_card_empty));
                } else if (Constant.current_tag.equals("Books")) {
                    tv_empty.setText(getResources().getString(R.string.academic_docs_empty));
                } else if (Constant.current_tag.equals("Personal Tag")) {
                    tv_empty.setText(getResources().getString(R.string.personal_tag_empty));
                } else {
                    tv_empty.setText(getResources().getString(R.string.all_docs_empty));
                }
            }
            progressDialog.dismiss();
        }
    }

    class SortByName implements Comparator<DBModel> {
        SortByName() {
        }

        @Override
        public int compare(DBModel dBModel, DBModel dBModel2) {
            if (selected_sorting.equals(Constant.ascending_name)) {
                return new File(dBModel.group_name).getName().compareToIgnoreCase(new File(dBModel2.group_name).getName());
            }
            if (selected_sorting.equals(Constant.descending_name)) {
                return new File(dBModel2.group_name).getName().compareToIgnoreCase(new File(dBModel.group_name).getName());
            }
            return 0;
        }

    }


    public void clickOnListItem(String str) {
        current_group = str;
        Constant.IdentifyActivity = "GroupDocumentActivity";
        AdsUtils.showGoogleInterstitialAd(MainActivity.this, false);
    }

    public void clickOnListMore(DBModel dbModel, final String name, String date) {

        dialogItem = new Dialog(this);
        dialogItem.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogItem.setContentView(R.layout.group_bottomsheet_dialog);
        dialogItem.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//      dialog.getWindow().setLayout(-1, -2);
        dialogItem.setCanceledOnTouchOutside(true);
        dialogItem.setCancelable(true);
        dialogItem.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogItem.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogItem.getWindow().setAttributes(lp);

        final TextView tv_dialog_title = (TextView) dialogItem.findViewById(R.id.tv_dialog_title);

        tv_dialog_title.setText(name);
        ((TextView) dialogItem.findViewById(R.id.tv_dialog_date)).setText(date);

        RelativeLayout rl_save_as_pdf = dialogItem.findViewById(R.id.rl_save_as_pdf);
        RelativeLayout rl_share = dialogItem.findViewById(R.id.rl_share);
        RelativeLayout rl_save_to_gallery = dialogItem.findViewById(R.id.rl_save_to_gallery);
        RelativeLayout rl_send_to_mail = dialogItem.findViewById(R.id.rl_send_to_mail);
        RelativeLayout rl_move_folder = dialogItem.findViewById(R.id.rl_move_folder);

        if (dbModel.getGroup_first_img() != null) {
            if (dbModel.getGroup_first_img().isEmpty()) {
                rl_save_as_pdf.setVisibility(View.GONE);
                rl_share.setVisibility(View.GONE);
                rl_save_to_gallery.setVisibility(View.GONE);
                rl_send_to_mail.setVisibility(View.GONE);
                rl_move_folder.setVisibility(View.GONE);
            }
        }

        rl_move_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoveFolderDialog(dbModel);
                dialogItem.dismiss();
            }
        });

        rl_save_as_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this, R.style.ThemeWithRoundShape);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.save_pdf_dialog_main);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//              dialog.getWindow().setLayout(-1, -2);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.BOTTOM;
                dialog.getWindow().setAttributes(lp);

               /* if (AdmobAds.SHOW_ADS) {
                    AdmobAds.loadNativeAds(MainActivity.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
                } else {
                    dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
                }*/

                ((RelativeLayout) dialog.findViewById(R.id.rl_save_pdf)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveGroupAsPDFDialog(name, "Save as PDF", tv_dialog_title.getText().toString());
                        dialog.dismiss();
                    }
                });
                ((RelativeLayout) dialog.findViewById(R.id.rl_save_pdf_pswrd)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareGroupPDFWithPswrd(name, "Save", tv_dialog_title.getText().toString());
                        dialog.dismiss();
                    }
                });
               /* ((ImageView) dialog.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });*/
                dialogItem.dismiss();
                dialog.show();
            }
        });

        rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareGroup(name);
                dialogItem.dismiss();
            }
        });


        ((RelativeLayout) dialogItem.findViewById(R.id.rl_rename)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGroupName(name);
                dialogItem.dismiss();
            }
        });

        rl_save_to_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new saveGroupToGallery(name).execute(new String[0]);
                dialogItem.dismiss();
            }
        });

        rl_send_to_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTomail(name, "gmail");
                dialogItem.dismiss();
            }
        });

        ((RelativeLayout) dialogItem.findViewById(R.id.rl_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.delete_document_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//               dialog.getWindow().setLayout(-1, -2);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialogItem.dismiss();
                dialog.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.BOTTOM;
                dialog.getWindow().setAttributes(lp);

/*
                if (AdmobAds.SHOW_ADS) {
                    AdmobAds.loadNativeAds(MainActivity.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
                } else {
                    dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
                }*/

                ((TextView) dialog.findViewById(R.id.tv_delete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbHelper.deleteGroup(name);
                        new setAllGroupAdapter().execute(new String[0]);
                        dialogItem.dismiss();
                        dialog.dismiss();
                    }
                });
                ((TextView) dialog.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogItem.dismiss();
                        dialog.dismiss();
                    }
                });



            }

        });

    }

    ArrayList<DBModel> modelArrayList = new ArrayList<>();
    String selectedFolderName = "";

    private void openMoveFolderDialog(DBModel selectedDBModel) {
        selectedFolderName = "";

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.move_folder_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//      dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);

        DBHelper dbHelper = new DBHelper(this);

        modelArrayList.clear();
        modelArrayList = dbHelper.getOnlyAllGroups();

        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
        LinearLayout ll_new_folder_add = (LinearLayout) dialog.findViewById(R.id.ll_new_folder_add);

        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "inter_medium.ttf");

        for (int i = 0; i < modelArrayList.size(); i++) {
            RadioButton rb = new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(modelArrayList.get(i).group_name);
            rb.setTypeface(createFromAsset);
            rb.setTextSize(15.0f);
            rb.setTextColor(ContextCompat.getColorStateList(mainActivity, R.color.black));

//            rb.setTextColor(mainActivity.getResources().getColor(R.color.black));
            rg.addView(rb);
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    selectedFolderName = (String) checkedRadioButton.getText();
//                    Log.e("name ", selectedFolderName);
                }

            }
        });
        ((TextView) dialog.findViewById(R.id.tv_move)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedFolderName.isEmpty()) {
                    boolean success = false;
                    ArrayList<DBModel> allFileList = dbHelper.getGroupDocs(selectedDBModel.getGroup_name().replace(" ", ""));
                    for (int i = 0; i < allFileList.size(); i++) {
                        DBModel newDbModel = allFileList.get(i);
                        long isMove = dbHelper.moveGroupDoc(selectedFolderName, newDbModel.getGroup_doc_img(), newDbModel.getGroup_doc_name(), "Insert text here...");
                        if (isMove <= 0) {
                            success = false;
                            break;
                        } else {
                            success = true;
                        }
                    }
                    if (success) {
                        Toast.makeText(mainActivity, "Move successfully", Toast.LENGTH_SHORT).show();
                        dbHelper.deleteGroup(selectedDBModel.getGroup_name().replace(" ", ""));
                        new setAllGroupAdapter().execute();
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(mainActivity, "Please select at least one folder", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ((TextView) dialog.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ll_new_folder_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewFolderDialog(selectedDBModel.getGroup_name().replace(" ", ""));
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void saveGroupAsPDFDialog(String name, String title, String str3) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.save_pdf_dialog_sub);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//      dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);

      /*  if (AdmobAds.SHOW_ADS) {
            AdmobAds.loadNativeAds(MainActivity.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        */
        final TextView textView = (TextView) dialog.findViewById(R.id.tvtext);
        final EditText et_pdf_name = (EditText) dialog.findViewById(R.id.et_pdf_name);

        textView.setText(title);
        et_pdf_name.setText(str3);
        et_pdf_name.setSelection(et_pdf_name.length());


        ((TextView) dialog.findViewById(R.id.tv_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textView.getText().toString().equals("Save as PDF")) {
                    new saveGroupAsPDF(name, PdfObject.TEXT_PDFDOCENCODING, "", et_pdf_name.getText().toString()).execute(new String[0]);
                    dialog.dismiss();
                    return;
                }
                shareGroupPDFWithPswrd(name, "save", et_pdf_name.getText().toString());
                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogItem.show();
                dialog.dismiss();
            }
        });
        ((ImageView) dialog.findViewById(R.id.clear_text)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_pdf_name.setText("");
            }

        });
        dialog.show();
    }

    private class saveGroupAsPDF extends AsyncTask<String, Void, String> {
        String group_name;
        String inputType;
        String password;
        String pdfName;
        ProgressDialog progressDialog;

        private saveGroupAsPDF(String str, String str2, String str3, String str4) {
            group_name = str;
            inputType = str2;
            password = str3;
            pdfName = str4;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            new ArrayList().clear();
            ArrayList<DBModel> groupDocs = dbHelper.getGroupDocs(group_name.replace(" ", ""));
            ArrayList arrayList = new ArrayList();
            Iterator<DBModel> it = groupDocs.iterator();
            while (it.hasNext()) {
                DBModel next = it.next();
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    arrayList.add(BitmapFactory.decodeStream(new FileInputStream(next.getGroup_doc_img()), (Rect) null, options));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputType.equals(PdfObject.TEXT_PDFDOCENCODING)) {
                createPDFfromBitmap(pdfName, arrayList, "save");
            } else {
                createProtectedPDFfromBitmap(pdfName, arrayList, password, "save");
            }
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, "Save Successfully", Toast.LENGTH_SHORT).show();
        }
    }


    public void shareGroup(final String name) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_group_doc);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//      dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);

        /*Ads*/
       /* if (AdmobAds.SHOW_ADS) {
            AdmobAds.loadNativeAds(MainActivity.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }*/


        ((RelativeLayout) dialog.findViewById(R.id.rl_share_pdf)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new shareGroupAsPDF(name, PdfObject.TEXT_PDFDOCENCODING, "", "", "all").execute(new String[0]);
                dialog.dismiss();
            }
        });
        ((RelativeLayout) dialog.findViewById(R.id.rl_share_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<DBModel> groupDocs = dbHelper.getGroupDocs(name.replace(" ", ""));
                ArrayList arrayList = new ArrayList();
                Iterator<DBModel> it = groupDocs.iterator();
                while (it.hasNext()) {
                    arrayList.add(BaseActivity.getURIFromFile(it.next().getGroup_doc_img(), MainActivity.this));
                }
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND_MULTIPLE");
                intent.setType("image/*");
                intent.putExtra("android.intent.extra.STREAM", arrayList);
                intent.putExtra("android.intent.extra.SUBJECT", name);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, (CharSequence) null));
                dialog.dismiss();
            }
        });
        ((RelativeLayout) dialog.findViewById(R.id.rl_share_pdf_pswrd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareGroupPDFWithPswrd(name, "share", "");
                dialog.dismiss();
            }
        });
        ((ImageView) dialog.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void shareGroupPDFWithPswrd(String name, String saveOrShare, String pdfName) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.set_pdf_pswrd);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//      dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);


       /* if (AdmobAds.SHOW_ADS) {
            AdmobAds.loadNativeAds(MainActivity.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }*/

        final EditText et_enter_pswrd = (EditText) dialog.findViewById(R.id.et_enter_pswrd);
        final EditText et_confirm_pswrd = (EditText) dialog.findViewById(R.id.et_confirm_pswrd);
        final ImageView iv_confirm_pswrd_show = (ImageView) dialog.findViewById(R.id.iv_confirm_pswrd_show);
        final ImageView iv_confirm_pswrd_hide = (ImageView) dialog.findViewById(R.id.iv_confirm_pswrd_hide);
        final ImageView iv_enter_pswrd_show = (ImageView) dialog.findViewById(R.id.iv_enter_pswrd_show);
        final ImageView iv_enter_pswrd_hide = (ImageView) dialog.findViewById(R.id.iv_enter_pswrd_hide);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/inter_medium.ttf");
        et_enter_pswrd.setTypeface(typeface);
        et_confirm_pswrd.setTypeface(typeface);

        et_enter_pswrd.setInputType(129);
        et_confirm_pswrd.setInputType(129);

        iv_enter_pswrd_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_enter_pswrd_show.setVisibility(View.GONE);
                iv_enter_pswrd_hide.setVisibility(View.VISIBLE);
                et_enter_pswrd.setTransformationMethod(new HideReturnsTransformationMethod());
                et_enter_pswrd.setSelection(et_enter_pswrd.getText().length());
            }
        });
        iv_enter_pswrd_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_enter_pswrd_show.setVisibility(View.VISIBLE);
                iv_enter_pswrd_hide.setVisibility(View.GONE);
                et_enter_pswrd.setTransformationMethod(new PasswordTransformationMethod());
                et_enter_pswrd.setSelection(et_enter_pswrd.getText().length());
            }
        });
        iv_confirm_pswrd_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_confirm_pswrd_show.setVisibility(View.GONE);
                iv_confirm_pswrd_hide.setVisibility(View.VISIBLE);
                et_confirm_pswrd.setTransformationMethod(new HideReturnsTransformationMethod());
                et_confirm_pswrd.setSelection(et_enter_pswrd.getText().length());
            }
        });
        iv_confirm_pswrd_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_confirm_pswrd_show.setVisibility(View.VISIBLE);
                iv_confirm_pswrd_hide.setVisibility(View.GONE);
                et_confirm_pswrd.setTransformationMethod(new PasswordTransformationMethod());
                et_confirm_pswrd.setSelection(et_enter_pswrd.getText().length());
            }
        });

        ((TextView) dialog.findViewById(R.id.tv_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_enter_pswrd.getText().toString().equals("") || et_confirm_pswrd.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else if (!et_enter_pswrd.getText().toString().equals(et_confirm_pswrd.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Your password & Confirm password do not match.", Toast.LENGTH_LONG).show();
                } else if (saveOrShare.equals("share")) {
                    new shareGroupAsPDF(name, "PDF With Password", et_enter_pswrd.getText().toString(), "", "all").execute(new String[0]);
                    dialog.dismiss();
                } else {
                    new saveGroupAsPDF(name, "PDF With Password", et_enter_pswrd.getText().toString(), pdfName).execute(new String[0]);
                    dialog.dismiss();
                }
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogItem.show();
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private class shareGroupAsPDF extends AsyncTask<String, Void, String> {
        private String group_name;
        private String inputType;
        private String mailId;
        private String password;
        private ProgressDialog progressDialog;
        private String shareType;

        private shareGroupAsPDF(String group_name, String inputType, String password, String mailId, String shareType) {
            this.group_name = group_name;
            this.inputType = inputType;
            this.password = password;
            this.mailId = mailId;
            this.shareType = shareType;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            new ArrayList().clear();
            ArrayList<DBModel> groupDocs = dbHelper.getGroupDocs(group_name.replace(" ", ""));
            ArrayList arrayList = new ArrayList();
            Iterator<DBModel> it = groupDocs.iterator();
            while (it.hasNext()) {
                DBModel next = it.next();
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    arrayList.add(BitmapFactory.decodeStream(new FileInputStream(next.getGroup_doc_img()), (Rect) null, options));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputType.equals(PdfObject.TEXT_PDFDOCENCODING)) {
                createPDFfromBitmap(group_name, arrayList, "temp");
            } else {
                createProtectedPDFfromBitmap(group_name, arrayList, password, "temp");
            }
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            Uri uRIFromFile = BaseActivity.getURIFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getResources().getString(R.string.app_name) + "/" + group_name + ".pdf", MainActivity.this);
            if (shareType.equals("gmail")) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.setType("application/pdf");
                intent.putExtra("android.intent.extra.STREAM", uRIFromFile);
                intent.putExtra("android.intent.extra.SUBJECT", group_name);
                intent.putExtra("android.intent.extra.EMAIL", new String[]{mailId});
                intent.setPackage("com.google.android.gm");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent createChooser = Intent.createChooser(intent, (CharSequence) null);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                startActivity(createChooser);
                return;
            }
            Intent intent2 = new Intent();
            intent2.setAction("android.intent.action.SEND");
            intent2.setType("application/pdf");
            intent2.putExtra("android.intent.extra.STREAM", uRIFromFile);
            intent2.putExtra("android.intent.extra.SUBJECT", group_name);
            intent2.putExtra("android.intent.extra.EMAIL", new String[]{mailId});
            intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent createChooser2 = Intent.createChooser(intent2, (CharSequence) null);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            startActivity(createChooser2);
        }
    }

    public void updateGroupName(final String name) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.update_group_name);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//      dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);


      /*  if (AdmobAds.SHOW_ADS) {
            AdmobAds.loadNativeAds(MainActivity.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }*/

        EditText editText = (EditText) dialog.findViewById(R.id.et_group_name);
        editText.setText(name);
        editText.setSelection(editText.length());


        ((TextView) dialog.findViewById(R.id.tv_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("") || Character.isDigit(editText.getText().toString().charAt(0))) {
                    Toast.makeText(MainActivity.this, "Please Enter Valid Document Name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                dbHelper.updateGroupName(MainActivity.this, name, editText.getText().toString().trim());
                dialog.dismiss();
                new setAllGroupAdapter().execute(new String[0]);
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

        ((ImageView) dialog.findViewById(R.id.clear_text)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");

            }
        });

    }

    private class saveGroupToGallery extends AsyncTask<String, Void, String> {
        private String group_name;
        private ProgressDialog progressDialog;

        private saveGroupToGallery(String group_name) {
            this.group_name = group_name;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            try {
                ArrayList<DBModel> groupDocs = dbHelper.getGroupDocs(group_name.replace(" ", ""));
                ArrayList arrayList = new ArrayList();
                Iterator<DBModel> it = groupDocs.iterator();
                while (it.hasNext()) {
                    arrayList.add(it.next().getGroup_doc_img());
                }
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap decodeStream = BitmapFactory.decodeStream(new FileInputStream((String) it2.next()), (Rect) null, options);
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getResources().getString(R.string.app_name) + "/Images");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File file2 = new File(file, System.currentTimeMillis() + ".jpg");
                    if (file2.exists()) {
                        file2.delete();
                    }
                    if (decodeStream != null) {
                        decodeStream.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file2));
                        saveImageToGallery(file2.getPath(), MainActivity.this);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, "Save Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendTomail(String name, String shareType) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.enter_email_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//      dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);


       /* if (AdmobAds.SHOW_ADS) {
            AdmobAds.loadNativeAds(MainActivity.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }*/
        final EditText editText = (EditText) dialog.findViewById(R.id.et_emailId);

        ((TextView) dialog.findViewById(R.id.tv_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("")) {
                    dialog.dismiss();
                } else if (!editText.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                } else {
                    new shareGroupAsPDF(name, PdfObject.TEXT_PDFDOCENCODING, "", editText.getText().toString(), shareType).execute(new String[0]);
                    dialog.dismiss();
                }
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }


    public void onDrawerItemSelected(int i) {
        /*Home*/
        if (i == 0) {

            tag_tabs.getTabAt(0).select();
            drawer_ly.closeDrawer(GravityCompat.START);
        }
        /*Qr Scan*/
        else if (i == 1) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 3);
        }

        /*Qr generate*/
        else if (i == 2) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 4);
        }

        /*About us*/
        else if (i == 3) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }

            Intent send = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(send);
        }
        /*T&c*/
        else if (i == 5) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }
            Intent send = new Intent(MainActivity.this, TermsConditionActivity.class);
            startActivity(send);

        }
        /*PP*/
        else if (i == 6) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }

            Intent send = new Intent(MainActivity.this, PrivacyPolicy.class);
            startActivity(send);

            /*Share*/
        } else if (i == 7) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }
            Constant.shareApp(this);


            /*Rate us*/
        } else if (i == 8) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }

            try {
                Constant.rateApp(this);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }

            /*Theme*/

        } else if (i == 4) {

            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);

                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                View view = LayoutInflater.from(this).inflate(R.layout.theme_bottom_sheet,null);

                RadioButton systemDefault = (RadioButton) view.findViewById(R.id.system_default);
                RadioButton light = (RadioButton) view.findViewById(R.id.light_theme);
                RadioButton dark = (RadioButton) view.findViewById(R.id.dark_theme);
                dialog.setContentView(view);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                //  dialog.getWindow().setLayout(-1, -2);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);

                systemDefault.setOnClickListener(v -> {
                    Toast.makeText(MainActivity.this, "System default", Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putInt(Constant.KEY_THEME,Constant.THEME_UNDEFINED).apply();
                    setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM ,Constant.THEME_UNDEFINED);

                });


                light.setOnClickListener(v-> {
                    Toast.makeText(MainActivity.this, "Light Theme", Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putInt(Constant.KEY_THEME, Constant.THEME_LIGHT).apply();
                    setTheme(AppCompatDelegate.MODE_NIGHT_NO, Constant.THEME_LIGHT);

                });

                dark.setOnClickListener(v -> {
                    Toast.makeText(this ,"Dark Theme",Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putInt(Constant.KEY_THEME, Constant.THEME_DARK).apply();
                    setTheme(AppCompatDelegate.MODE_NIGHT_YES, Constant.THEME_DARK);
                });


                dialog.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.BOTTOM;
                dialog.getWindow().setAttributes(lp);

            }

        }
    }

    public void saveTheme(int theme) {
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(Constant.KEY_THEME, theme).apply();
    }

    private void setTheme(int themeMode, int prefsMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
        saveTheme(prefsMode);
    }

    @Override
    public void onBackPressed() {
        if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
            drawer_ly.closeDrawer(GravityCompat.START);
        } else if (rl_search_bar.getVisibility() == View.VISIBLE) {
            rl_search_bar.setVisibility(View.GONE);
            iv_search.setVisibility(View.VISIBLE);
            iv_more.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }
}
