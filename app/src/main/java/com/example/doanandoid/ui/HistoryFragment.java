package com.example.doanandoid.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanandoid.R;
import com.example.doanandoid.model.Food;
import com.example.doanandoid.model.OrdersHistory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private final static String TAG = "Hello";
    //Initialize Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference userDoc = db.collection("users").document(user.getUid());

    private final List<OrdersHistory> ordersHistoryList = new ArrayList<>();
    private MenuAdapter adapter = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        userDoc.collection("orders history")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        OrdersHistory ordersHistory = document.toObject(OrdersHistory.class);
                        ordersHistoryList.add(ordersHistory);

                        adapter.notifyDataSetChanged();

                        Log.d(TAG, "onComplete: " + ordersHistory.getDatePay());
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        adapter = new MenuAdapter(getActivity());

        //Set adapter list Food
        RecyclerView recyclerViewFood = root.findViewById(R.id.recvOrderHistory);
        recyclerViewFood.setAdapter(adapter);
        recyclerViewFood.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    //Custom RecyclerView Adapter
    class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
        private final Context context;

        MenuAdapter(Context context) {
            this.context = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView imageView;
            private final TextView txtDate;
            private final TextView txtListFood;
            private final TextView txtTotalPrice;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageViewMonth);
                txtDate = itemView.findViewById(R.id.txtDate);
                txtListFood = itemView.findViewById(R.id.textViewListFood);
                txtTotalPrice = itemView.findViewById(R.id.textViewTotalPrice);
            }
        }

        @NonNull
        @Override
        public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.item_history, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OrdersHistory ordersHistory = ordersHistoryList.get(position);

            //Get String Name Food
            userDoc.collection("orders history")
                    .document(ordersHistory.getId())
                    .collection("carts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        String s = "";
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Food food = document.toObject(Food.class);

                            s = s + " " + food.getName();
                            adapter.notifyDataSetChanged();

                            Log.d(TAG, "onComplete: " + food.getName());
                        }
                        ordersHistory.setListFood(s);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }});

            holder.txtDate.setText(String.valueOf(ordersHistory.getDatePay()));
            holder.txtListFood.setText(String.valueOf(ordersHistory.getListFood()));
            holder.txtTotalPrice.setText(String.format("%,d",ordersHistory.getTotalPrice()) + " đ");
        }

        @Override
        public int getItemCount() {
            return ordersHistoryList.size();
        }

    }
}