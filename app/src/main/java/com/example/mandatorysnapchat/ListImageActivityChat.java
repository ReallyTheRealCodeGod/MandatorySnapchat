package com.example.mandatorysnapchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.mandatorysnapchat.adapter.MyAdapter;
import com.example.mandatorysnapchat.model.Snap;
import com.example.mandatorysnapchat.repo.Repo;

import java.util.ArrayList;
import java.util.List;

public class ListImageActivityChat extends AppCompatActivity implements Updatable{

    private List<Snap> snaps = new ArrayList<>();
    private ListView listView;
    private MyAdapter myAdapter;
    private Button newImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_snap_chat);
        setupListView();
        Repo.singletonRepo().setup(this , snaps);
        newImageSnapButton();

    }

    private void newImageSnapButton() {
        newImageButton = findViewById(R.id.makeNewImageButton);
        newImageButton.setOnClickListener(click -> {
                Intent intent = new Intent(this, MakeImageActivity.class);
                startActivity(intent);
        });
    }


    // Forklaring på hvad der sker:
    // Jeg ligger snaps listen, som jeg får fra Repo klassen ind i adapteren,
    // sammen med context(som er en abstract class, som alle aktiviteter nedarver, det giver informationer om et objekt), det gør at listviewet bliver populated korrekt
    // med de rigtige værdier og med et specifikt index, så jeg kan håndtere den specifikke fil, hvis det er nødvendigt
    // derefter kalder jeg en listener på listviewet, som bliver kaldt ved at klikke på et af snapsne som er i arrayet af billeder
    // den giver så positionen, id og andre vigtige værdier videre som jeg kan bruge til at behandle den korrekte data.
    // jeg bruger den til at få den korrekte snap, ved den rigtige position, der hvor jeg klikkede. Derefter ligger jeg værdien ind i et intent,
    // så jeg kan se billedet og håndtere det i en anden klasse, som jeg bruger til at fremvise billedet, efter det er blevet klikket på
    // for så derefter at slette det, efter det er blevet vist
    private void setupListView() {
        listView = findViewById(R.id.listview);
        myAdapter = new MyAdapter(snaps, this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, imageDetail.class);
            intent.putExtra("snap", snaps.get(position));
            startActivity(intent);
        });
    }

    @Override
    public void update(Object o) {
        myAdapter.notifyDataSetChanged();
    }
}