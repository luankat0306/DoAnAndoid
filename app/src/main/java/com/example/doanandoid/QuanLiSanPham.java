package com.example.doanandoid;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanandoid.model.Food;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuanLiSanPham extends AppCompatActivity {

    class QuanLySPAdapter extends ArrayAdapter<Food> {

        public QuanLySPAdapter(Context context, int resource) {
            super(context,resource);
        }

        public QuanLySPAdapter() {
            super(QuanLiSanPham.this,android.R.layout.simple_list_item_1, foods);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if(row == null)
            {
                LayoutInflater layoutInflater = getLayoutInflater();
                row = layoutInflater.inflate(R.layout.item_food,null);
            }
            Food food = foods.get(position);
            TextView textViewTenSp = (TextView)row.findViewById(R.id.textViewTenSP);
            textViewTenSp.setText(String.valueOf(food.getName()));

            TextView textViewGia = (TextView)row.findViewById(R.id.textViewGia);
            textViewGia.setText(String.valueOf(food.getPrice()));

            Button btnDelete = (Button) row.findViewById(R.id.btnAdd);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFood(food.getId());
                }
            });


            return row;
        }
    }

    private final static String TAG = "Asd";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Food> foods = new ArrayList<Food>();
    private QuanLySPAdapter adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_li_san_pham);
        ListView list = (ListView)findViewById(R.id.list_item_food);

        db.collection("food")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " item " + document.getData());

                                Food food = document.toObject(Food.class);
                                food.setId(document.getId());
                                foods.add(food);
                                adapter.notifyDataSetChanged();
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
        adapter = new QuanLySPAdapter();
        list.setAdapter(adapter);

    }

    public void deleteFood(String id) {
        db.collection("food").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
}