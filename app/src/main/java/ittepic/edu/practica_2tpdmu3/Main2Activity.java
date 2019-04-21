package ittepic.edu.practica_2tpdmu3;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    EditText m, g ,o;
    Button insertar,eliminar,mostrar,otro;
    private DatabaseReference base;
    List<Marca> marcasList;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        m=findViewById(R.id.txtMarca);
        g=findViewById(R.id.txtTipo);
        o=findViewById(R.id.txtOrigen);

        otro=findViewById(R.id.otraCol);
        insertar= findViewById(R.id.insertar);
        eliminar=findViewById(R.id.eliminar);
        mostrar= findViewById(R.id.mostrar);
        listView= findViewById(R.id.lista);


        base= FirebaseDatabase.getInstance().getReference();
        base.child("MARCAS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                marcasList = new ArrayList<>();

                if(dataSnapshot.getChildrenCount()<=0){
                    Toast.makeText(Main2Activity.this, "Aun no hay datos", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(final DataSnapshot snap : dataSnapshot.getChildren()){
                    base.child("MARCAS").child(snap.getKey()).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Marca u = dataSnapshot.getValue(Marca.class);

                                    if(u!=null){
                                        marcasList.add(u);
                                    }
                                    cargarSelect();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Marca n = new Marca(m.getText().toString(), g.getText().toString(), o.getText().toString());
                if(n.nom.isEmpty() || n.giro.isEmpty() || n.nac.isEmpty()){
                    Toast.makeText(Main2Activity.this, "EXISTEN CAMPOS VACIOS", Toast.LENGTH_SHORT).show();
                    return;
                }
                base.child("MARCAS").child(n.nom).setValue(n)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Main2Activity.this, "EXITO", Toast.LENGTH_SHORT).show();
                                m.setText("");g.setText("");o.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Main2Activity.this, "ERROR!!!", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        otro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Main2Activity.this,Main3Activity.class);
                startActivity(intent);
            }
        });
        mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarMarcas();
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EliminarMarca();
            }
        });
    }
    public void cargarSelect(){
        if (marcasList.size()==0) return;
        String nombres[] = new String[marcasList.size()];

        for(int i = 0; i<nombres.length; i++){
            Marca u = marcasList.get(i);
            nombres[i] = ((Marca) u).nom;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
        listView.setAdapter(adapter);
    }
    public void EliminarMarca(){
        final EditText id = new EditText(this);
        id.setHint("Marca a eliminar");
        AlertDialog.Builder a= new AlertDialog.Builder(this);
        a.setTitle("Atencion").setMessage("Marca a elimnar").setView(id).setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                elimina(id.getText().toString());
            }
        }).setNegativeButton("Cancelar",null).show();

    }
    private void elimina(String id){
        base.child("MARCAS").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main2Activity.this, "SE ELIMINO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                        m.setText("");g.setText("");o.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main2Activity.this, "ERROR AL ELIMINAR", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void MostrarMarcas(){
        final EditText id = new EditText(this);
        id.setHint("Nombre de la marca");
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("Marca a actualizar:").setView(id).setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mostrarM(id.getText().toString());
            }
        }).setNegativeButton("Cancelar", null).show();
    }
    public void mostrarM(String id){
        FirebaseDatabase.getInstance().getReference().child("MARCAS").child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Marca usseer = dataSnapshot.getValue(Marca.class);

                        if(usseer!=null) {
                            m.setText(usseer.nom);
                            g.setText(usseer.giro);
                            o.setText(usseer.nac);
                        } else {
                            Toast.makeText(Main2Activity.this, "Error, no se escotro la marca", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
