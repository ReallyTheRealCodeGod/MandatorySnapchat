# MandatorySnapchat


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

//receive er en metode som er implementeret fra Tasklistener interfacet, det gør det muligt at anvende
//en generisk Tasklistener i Repo klassen, så man bare kan kalde Repo metoden, med objektet(this) som argument og modtage
//billedet i Tasklistener metoden (receive), som er implementeret nedenunder her.
@Override
public void receive(byte[] bytes) {
    imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0, bytes.length));
}


