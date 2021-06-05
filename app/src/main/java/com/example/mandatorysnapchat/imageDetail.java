package com.example.mandatorysnapchat;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mandatorysnapchat.model.Snap;
import com.example.mandatorysnapchat.repo.Repo;

import androidx.appcompat.app.AppCompatActivity;

public class imageDetail extends AppCompatActivity implements Tasklistener {

    private TextView titleTextView;
    private ImageView imageView;
    private Button backButton;
    private String snapID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_layout);
        Snap snap = getIntent().getParcelableExtra("snap");
        snapID = snap.getID();

        imageView = findViewById(R.id.imageViewDetail);
        titleTextView = findViewById(R.id.textViewDetailTitle);

        titleTextView.setText(snap.getTitle());
        Repo.singletonRepo().downloadSnap(snapID, this);

        backButton = findViewById(R.id.backButtonDetailView);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Repo.singletonRepo().deleteSnap(snapID);
                finish();
            }
        });
    }

    //receive er en metode som er implementeret fra Tasklistener interfacet, det gør det muligt at anvende
    //en generisk Tasklistener i Repo klassen, så man bare kan kalde Repo metoden, med objektet(this) som argument og modtage
    //billedet i Tasklistener metoden (receive), som er implementeret nedenunder her.
    @Override
    public void receive(byte[] bytes) {
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0, bytes.length));
    }
}
