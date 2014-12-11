package com.eightpsman.funnyds.android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.core.ImageActorData;
import com.eightpsman.funnyds.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class FunnyDS extends Activity implements DeviceDrawer.DrawerCallback, View.OnClickListener{

    ClientManager manager;

    /** views */
    DeviceDrawer deviceDrawer;
    View viewContainer;
    View controlView;
    LinearLayout actorContainer;
    ImageButton fullButton;
    ImageButton fillButton;
    ImageButton moreButton;
    TextView infoView;
    EditText sizeText;
    EditText velocText;
    CheckBox keepOrientation;
    PopupWindow popupWindow;

    String[] sizeList;
    List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    List<ImageButton> imageButtons = new ArrayList<ImageButton>();
    int nowBitmapIndex;
    int nowSizeIndex = 5;
    int nowVelocIndex = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = ClientManager.getUniqueInstance();

        setContentView(R.layout.main);

        deviceDrawer = (DeviceDrawer) findViewById(R.id.drawer_view);
        viewContainer   = findViewById(R.id.viewContainer);
        controlView     = findViewById(R.id.controlView);
        actorContainer  = (LinearLayout) findViewById(R.id.actorContainer);
        fullButton      = (ImageButton) findViewById(R.id.fullButton);
        fillButton      = (ImageButton) findViewById(R.id.fillButton);
        moreButton      = (ImageButton) findViewById(R.id.moreButton);
        infoView        = (TextView) findViewById(R.id.infoView);
        keepOrientation = (CheckBox) findViewById(R.id.keep_orientation);

        fullButton.setOnClickListener(this);
        fillButton.setOnClickListener(this);
        moreButton.setOnClickListener(this);

        deviceDrawer.setManager(manager);
        deviceDrawer.setCallback(this);

        sizeList = getResources().getStringArray(R.array.actor_size);

        /** init popup window */
        popupWindow = new PopupWindow(this);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(150);
        popupWindow.setFocusable(true);
        View popupView = getLayoutInflater().inflate(R.layout.popup, null);
        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#99FFFFFF")));

        Button pickButon = (Button) popupView.findViewById(R.id.pick_image);
        pickButon.setOnClickListener(this);

        ImageButton prevSize = (ImageButton) popupView.findViewById(R.id.prev_size);
        ImageButton nextSize = (ImageButton) popupView.findViewById(R.id.next_size);
        prevSize.setOnClickListener(this);
        nextSize.setOnClickListener(this);

        ImageButton prevVeloc = (ImageButton) popupView.findViewById(R.id.prev_veloc);
        ImageButton nextVeloc = (ImageButton) popupView.findViewById(R.id.next_veloc);
        prevVeloc.setOnClickListener(this);
        nextVeloc.setOnClickListener(this);

        nowSizeIndex = 4;
        sizeText = (EditText) popupView.findViewById(R.id.size_value);
        sizeText.setText(sizeList[nowSizeIndex]);

        nowVelocIndex = 4;
        velocText = (EditText) popupView.findViewById(R.id.veloc_value);
        velocText.setText(sizeList[nowVelocIndex]);

        loadOriginalImage();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                showControlView();
                Log.d(TAG, "Action down");
                break;
            case MotionEvent.ACTION_UP:
                delayHideControlView();
                Log.d(TAG, "Action up");
                break;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.shutdown();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        delayHideControlView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Device device = manager.getLocalDevice();

        if (keepOrientation.isChecked())
            return;
        float pw, ph;
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            pw = Math.min(device.pw, device.ph);
            ph = Math.max(device.pw, device.ph);
        }else{
            pw = Math.max(device.pw, device.ph);
            ph = Math.min(device.pw, device.ph);
        }
        device.pw = pw;
        device.ph = ph;
        device.calc(manager.getDpi());
        manager.updateDevice(device);
        deviceDrawer.translateToCenter();
    }

    public void showControlView(){
        handler.removeCallbacks(delayHideControlViewRunner);
        controlView.setVisibility(View.VISIBLE);
        controlView.animate().alpha(1f).setDuration(500).start();
    }

    public void delayHideControlView(){
        handler.postDelayed(delayHideControlViewRunner, HIDE_CONTROL_VIEW_DELAY);
    }

    Handler handler = new Handler();
    Runnable delayHideControlViewRunner = new Runnable() {
        @Override
        public void run() {
            if (popupWindow.isShowing()){
                delayHideControlView();
            }else{
                controlView.animate().alpha(0f).setDuration(500).start();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controlView.setVisibility(View.GONE);
                    }
                }, 500);
            }
        }
    };

    public static final String TAG = "FunnyDS";
    public static final int HIDE_CONTROL_VIEW_DELAY = 2000;


    @Override
    public void onChangeView(float translate_x, float translate_y, float scale) {
        infoView.setText(String.format("TRANSLATE: %.2f : %.2f   SCALE: %d%%", translate_x, translate_y, (int)(scale*100)));
    }

    @Override
    public void onNewActor(final float x, final float y) {
        new Thread(){
            @Override
            public void run(){
                Bitmap bitmap = bitmaps.get(nowBitmapIndex);
                int size = Integer.parseInt(sizeList[nowSizeIndex]);
                float h = AppUtils.getActorIndependenceSize(size) * manager.getDpi();
                float w = h * (float)bitmap.getWidth() / bitmap.getHeight();
                float veloc = AppUtils.getActorIndependenceVelocity(Integer.parseInt(sizeList[nowVelocIndex])) * manager.getDpi();
                byte[] data = AndroidUtils.convertBitmapToByte(bitmap);
                final ImageActorData imageActorData = new ImageActorData(
                        data, manager.getDpi(), x, y, w, h, veloc, veloc);
                manager.newImageActor(imageActorData);
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == fullButton.getId()){
            deviceDrawer.setFullScreen();
        }else if (viewId == fillButton.getId()){
            deviceDrawer.setFillScreen();
        }else if (viewId == moreButton.getId()){
            popupWindow.showAsDropDown(view);
        }else if (viewId == R.id.next_size){
            nowSizeIndex = (nowSizeIndex + 1) % sizeList.length;
            sizeText.setText(sizeList[nowSizeIndex]);
        }else if (viewId == R.id.prev_size){
            nowSizeIndex = (nowSizeIndex - 1) % sizeList.length;
            if (nowSizeIndex < 0) nowSizeIndex += sizeList.length;
            sizeText.setText(sizeList[nowSizeIndex]);
        }else if (viewId == R.id.next_veloc){
            nowVelocIndex = (nowVelocIndex + 1) % sizeList.length;
            velocText.setText(sizeList[nowVelocIndex]);
        }else if (viewId == R.id.prev_veloc){
            nowVelocIndex = (nowVelocIndex - 1) % sizeList.length;
            if (nowVelocIndex < 0) nowVelocIndex += sizeList.length;
            velocText.setText(sizeList[nowVelocIndex]);
        }else if (viewId == R.id.pick_image){
            pickImageByIntent();
        }

    }

    View.OnClickListener imageButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (int i=0; i<imageButtons.size(); i++)
                if (imageButtons.get(i) == view){
                    imageButtons.get(i).setSelected(true);
                    nowBitmapIndex = i;
                }else imageButtons.get(i).setSelected(false);
        }
    };

    public void addImageBitmap(Bitmap bitmap){
        ImageButton imageButton = new ImageButton(FunnyDS.this);
        imageButton.setImageBitmap(bitmap);
        imageButton.setAdjustViewBounds(true);
        imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageButton.setBackgroundResource(R.drawable.image_button_background);
        actorContainer.addView(imageButton,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        actorContainer.invalidate();
        imageButton.setOnClickListener(imageButtonListener);
        bitmaps.add(bitmap);
        imageButtons.add(imageButton);
        actorContainer.invalidate();
    }
    public void loadOriginalImage(){
        for (int i=0; i<5; i++){
            Bitmap bitmap = AndroidUtils.loadBitmapFromAssets(this, "fish_" + i + ".png");
            addImageBitmap(bitmap);
        }
    }

    public void loadImage(String path){
        Bitmap bitmap = AndroidUtils.loadBitmapFromStorage(path);
        addImageBitmap(bitmap);
    }

    public static final int PICK_IMAGE_REQUEST_CODE = 1111;

    public void pickImageByIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case PICK_IMAGE_REQUEST_CODE:
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri,
                            new String[] {MediaStore.Images.ImageColumns.DATA }, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(0);
                    cursor.close();
                    loadImage(imagePath);
                    break;
            }
        }
    }
}
