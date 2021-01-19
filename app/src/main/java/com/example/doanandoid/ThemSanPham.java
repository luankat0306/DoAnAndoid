package com.example.doanandoid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanandoid.model.Food;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ThemSanPham extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText edtTen, edtHinh, edtGia, edtMa, edtMoTa;
    Button btnThem;
    Food food = new Food();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_san_pham);

        edtTen = (EditText) findViewById(R.id.editTensanpham);
        edtGia = (EditText) findViewById(R.id.editgiasanpham);
        edtHinh = (EditText) findViewById(R.id.edithinhanh);
        edtMoTa = (EditText) findViewById(R.id.editmota);
        btnThem = (Button) findViewById(R.id.buttonthem);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String log = edtTen.getText().toString();
                Log.d("success", " Thêm Thành công" + log );
                food.setName(edtTen.getText().toString());
                int i = Integer.parseInt(edtGia.getText().toString());
                food.setPrice(i);
                food.setImage(edtHinh.getText().toString());

                db.collection("food").add(food).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                });
            }
        });

    }
}