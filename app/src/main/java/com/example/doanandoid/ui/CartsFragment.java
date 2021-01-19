package com.example.doanandoid.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanandoid.model.Food;
import com.example.doanandoid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CartsFragment extends Fragment {

    private final static String TAG = "Asd";
    private final List<Food> foods = new ArrayList<Food>();
    private TextView txtTotalPrice;
    private Button btnThanhToan;
    private int totalPrice;
    private MenuAdapter adapter = null;
    private ListView listView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference userDoc = db.collection("users").document(user.getUid());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_carts, container, false);
        txtTotalPrice = root.findViewById(R.id.textViewPrice);
        btnThanhToan = root.findViewById(R.id.buttonPay);


        getListFoodFromCart();
        adapter = new MenuAdapter(getActivity());

        //Set adapter list Food
        RecyclerView recyclerViewFood = root.findViewById(R.id.lvcarts);
        recyclerViewFood.setAdapter(adapter);
        recyclerViewFood.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.notifyDataSetChanged();

        //Order food
        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foods.size() > 0) {
                    DocumentReference ordersHistoryDoc = userDoc.collection("orders history").document();
                    Date dateNow = new Date(System.currentTimeMillis());
                    //Format Date
                    SimpleDateFormat format = new SimpleDateFormat("dd - M - yyyy hh:mm:ss");

                    Map<String, Object> order = new HashMap<>();
                    String date = format.format(dateNow);
                    order.put("id",ordersHistoryDoc.getId());
                    order.put("datePay", date);
                    order.put("totalPrice", totalPrice);
                    ordersHistoryDoc.set(order);


                    for (int i = 0; i < foods.size(); i++) {
                        userDoc.collection("carts").document(foods.get(i).getId()).delete();
                        DocumentReference cartsDoc = ordersHistoryDoc.collection("carts").document();
                        foods.get(i).setId(cartsDoc.getId());
                        cartsDoc.set(foods.get(i));
                    }

                    getListFoodFromCart();
                    adapter.notifyDataSetChanged();
                    txtTotalPrice.setText("0 đ");
                    Toast.makeText(getActivity(), "Đặt Món Thành Công", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Hãy thêm món ăn vào giỏ", Toast.LENGTH_SHORT).show();
                }
            }

        });

        return root;
    }

    public void getListFoodFromCart() {
        userDoc.collection("carts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Food food = document.toObject(Food.class);
                                foods.add(food);
                                adapter.notifyDataSetChanged();

                                Log.d(TAG, "onComplete: " + food.getImage());
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
        private final Context context;
        private List<Food> foodsCopy;

        MenuAdapter(Context context) {
            this.context = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView imageView;
            private final TextView textViewTenSp;
            private final TextView textViewGia;
            private final ImageButton btnAdd;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.imageViewQLSP);
                textViewTenSp = (TextView)itemView.findViewById(R.id.textViewTenSP);
                textViewGia = (TextView)itemView.findViewById(R.id.textViewGia);
                btnAdd = (ImageButton) itemView.findViewById(R.id.btnAdd);
            }
        }

        @NonNull
        @Override
        public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.item_food, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position) {
            Food food = foods.get(position);

            Glide.with(CartsFragment.this /* context */)
                    .load(food.getImage())
                    .into(holder.imageView);


            holder.textViewTenSp.setText(String.valueOf(food.getName()));


            holder.textViewGia.setText(String.format("%,d",food.getPrice()) + " đ");



            holder.btnAdd.setImageResource(R.drawable.ic_clear_cart);
            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userDoc.collection("carts").document(food.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            totalPrice = totalPrice - food.getPrice();
                            foods.remove(food);
                            notifyDataSetChanged();
                        }
                    });
                }
            });

            // Total Price
            totalPrice = 0;
            for(Food food1 : foods) {
                totalPrice = totalPrice + food1.getPrice();
            }
            txtTotalPrice.setText(String.format("%,d",totalPrice) + " đ");
        }

        @Override
        public int getItemCount() {
            return foods.size();
        }

        //Xóa dấu tiếng Việt
        public String covertToString(String value) {
            try {
                String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
                Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
                return pattern.matcher(temp).replaceAll("");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }
}


