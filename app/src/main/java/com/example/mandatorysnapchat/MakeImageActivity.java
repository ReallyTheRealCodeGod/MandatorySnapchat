package com.example.mandatorysnapchat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.mandatorysnapchat.repo.Repo;
import com.example.mandatorysnapchat.view.DrawingView;

import java.util.UUID;

public class MakeImageActivity extends AppCompatActivity {

    private SeekBar mThickness;
    private DrawingView mDrawLayout;
    private Button erase, draw;
    private Paint drawPaint = new Paint();
    private EditText addTextToImageTextField;
    private EditText changeTitleTextField;
    private String Title = "new snap";


    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_image);
        addTextToImageTextField = findViewById(R.id.addTextToImagePlainText);
        changeTitleTextField = findViewById(R.id.changeTitlePlainText);


        //anmoder om at checke for, om der er allerede er blevet giver adgang til Androids billede filsystem, ellers
        //bliver der anmodet om at gøre det. Hvis der allerede er blevet givet adgang,så hent billedet.
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else {
            getPhoto();
        }

        setupDraw();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            try {
                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                mDrawLayout.setImageToDraw(resized);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //sender billedet til firebase og returnere derefter til "ListImageActivityChat"
    public void sendImgToFirebase(View firebaseButton) {
        Repo.singletonRepo().uploadBitmap(UUID.randomUUID().toString(), Title, mDrawLayout.returnBitmap());
        finish();
    }

    //add text, and send to firebase + finish
    public void textOnImageWhenButtonIsPressed(View button) {
        String text = addTextToImageTextField.getText().toString();
        Repo.singletonRepo().uploadBitmap(UUID.randomUUID().toString(), Title, drawTextToBitmap(mDrawLayout.returnBitmap(), text));
        finish();
    }

    public Bitmap drawTextToBitmap(Bitmap image, String gText) {
        Bitmap.Config bitmapConfig = image.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        image = image.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// new antialised Paint
        paint.setColor(Color.rgb(0, 0, 161));
        paint.setTextSize((int) (50)); // text size in pixels
        paint.setShadowLayer(5f, 0f, 5f, Color.WHITE); // text shadow
        canvas.drawText(gText, 10, 100, paint);
        return image;
    }

    public void changeTitleButton(View view) {
        Title = changeTitleTextField.getText().toString();
    }

    private void setupDraw() {
        mThickness = (SeekBar) findViewById(R.id.thickness);
        mDrawLayout = (DrawingView) findViewById(R.id.viewDraw);
        erase = (Button) findViewById(R.id.erase);
        draw= (Button) findViewById(R.id.draw);

        mDrawLayout.setVisibility(View.VISIBLE);
        mDrawLayout.setDrawingCacheEnabled(true);
        mDrawLayout.setEnabled(true);
        mThickness.setMax(50);
        mThickness.setProgress(10);
        mDrawLayout.setPaintAlpha(mThickness.getProgress());
        int currLevel = mDrawLayout.getPaintAlpha();
        mThickness.setProgress(currLevel);
        mDrawLayout.invalidate();

        erase.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawPaint.setColor(Color.TRANSPARENT);
                mDrawLayout.setErase(true);

            }
        });

        draw.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDrawLayout.setErase(false);

            }
        });

        mThickness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                mDrawLayout.setPaintAlpha(mThickness.getProgress());
            }
        });
    }
}

