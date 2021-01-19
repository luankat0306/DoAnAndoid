package com.example.doanandoid.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanandoid.DangNhap;
import com.example.doanandoid.model.Food;
import com.example.doanandoid.R;
import com.firebase.ui.auth.AuthUI;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    private final static String TAG = "Hello";
    //Initialize Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference userDoc = db.collection("users").document(user.getUid());

    private final List<Food> foods = new ArrayList<Food>();
    private MenuAdapter adapter = null;
    private ImageButton menuButton, btnRefresh, btnAnVat, btnNuoc, btnCom, btnBanh;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //Get List
        getListFood();

        adapter = new MenuAdapter(getActivity());

        //Set adapter list Food
        RecyclerView recyclerViewFood = root.findViewById(R.id.lvDatMon);
        recyclerViewFood.setAdapter(adapter);
        recyclerViewFood.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Search
        SearchView searchView = root.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        menuButton = root.findViewById(R.id.menuButton);
        registerForContextMenu(menuButton);

        //Filter Category
        filterCategory();
        btnAnVat = (ImageButton) root.findViewById(R.id.an_vat);
        btnAnVat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foods.clear();
                getListFoodByCategory("dN1qNbmRC6D0BcOT5Ka3");
            }
        });

        btnBanh = (ImageButton) root.findViewById(R.id.banh_keo);
        btnBanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foods.clear();
                getListFoodByCategory("GaR85HKyhfZjbYcXHmhq");
            }
        });

        btnNuoc = (ImageButton) root.findViewById(R.id.nuoc);
        btnNuoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foods.clear();
                getListFoodByCategory("YlAI7bTrHITd1c2bIQXa");
            }
        });
        btnCom = (ImageButton) root.findViewById(R.id.com);
        btnCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foods.clear();
                getListFoodByCategory("bqDvBRpnxiaJAoNgklI1");
            }
        });

        //Refresh Food
        btnRefresh = root.findViewById(R.id.imageButtonRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foods.clear();
                getListFood();
            }
        });

        return root;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                AuthUI.getInstance()
                        .signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(getActivity(), DangNhap.class);
                                startActivity(intent);
                            }
                        });
                break;
        }

        return true;
    }

    //Get List Food
    public void getListFood() {
        getListFoodByCategory("YlAI7bTrHITd1c2bIQXa");
        getListFoodByCategory("GaR85HKyhfZjbYcXHmhq");
        getListFoodByCategory("bqDvBRpnxiaJAoNgklI1");
        getListFoodByCategory("dN1qNbmRC6D0BcOT5Ka3");
    }

    //Get List Food by Category
    public void getListFoodByCategory(String doc) {
        db.collection("menu").document(doc).collection("foods")
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

    //Filter Category
    public void filterCategory() {

    }

    //Custom RecyclerView Adapter
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
                imageView = itemView.findViewById(R.id.imageViewQLSP);
                textViewTenSp = itemView.findViewById(R.id.textViewTenSP);
                textViewGia = itemView.findViewById(R.id.textViewGia);
                btnAdd = itemView.findViewById(R.id.btnAdd);
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

            Glide.with(HomeFragment.this)
                    .load(food.getImage())
                    .into(holder.imageView);

            holder.textViewTenSp.setText(String.valueOf(food.getName()));
            holder.textViewGia.setText(String.format("%,d",food.getPrice()) + " đ");
            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference cartDoc = userDoc.collection("carts").document();
                    food.setId(cartDoc.getId());
                    cartDoc.set(food).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Đã thêm " + food.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }

        //Search
        public void filter(String text) {
            foodsCopy = new ArrayList<>(foods);
            foods.clear();
            if(text.isEmpty() || text.length() == 0){
                getListFood();
            } else{
                text = covertToString(text.toLowerCase());
                for(Food item: foodsCopy){
                    if(covertToString(item.getName().toLowerCase()).contains(text)){
                        foods.add(item);
                    }
                }
            }

            notifyDataSetChanged();
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