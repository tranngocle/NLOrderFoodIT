package com.example.nlorderfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nlorderfood.Adapters.CategoryAdapter;
import com.example.nlorderfood.Adapters.FirestoreAdapter;
import com.example.nlorderfood.Adapters.NewDishesAdapter;
import com.example.nlorderfood.Helpers.Constants;
import com.example.nlorderfood.Models.Category;
import com.example.nlorderfood.Models.Food;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryFoodItemClickListener, FirestoreAdapter.OnListItemClick, NewDishesAdapter.OnNewDishItemClick {
    private FirebaseFirestore firestore;
    FirestoreAdapter mealsAdapter;
    TextView hinameTxt;
    Button viewOrdersBtn;
    RecyclerView rvCategories, rvNewDishes;
    ArrayList<Category> categories;
    CategoryAdapter adapter;
    RecyclerView rvMeals;
    FloatingActionButton fab;
    LinearLayout homeLnl, restLnl, profileLnl;
    private LinearLayoutManager categoryLinearLyout;
    private LinearLayoutManager newDishesLinearLyout;
    private LinearLayoutManager popularFoodsLayout;
    //Preference field
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

        categories=new ArrayList<>();
        getCategories();
        adapter= new CategoryAdapter(categories,this);
        //Initialising preference
        sharedPreferences=getSharedPreferences("USER_DATA",MODE_PRIVATE);



        categoryLinearLyout = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);
        rvCategories.setLayoutManager(categoryLinearLyout);
        rvCategories.setAdapter(adapter);

        //String username=sharedPreferences.getString("FNAME","");
        hinameTxt.setText("Chào, "+sharedPreferences.getString("FNAME",""));
        //Initializing Firebase Firestore service
        firestore = FirebaseFirestore.getInstance();
        //START

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, CartView.class);
                startActivity(intent);
            }
        });
        profileLnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, UserProfile.class);
                startActivity(intent);
            }
        });
        Query qMeals = firestore
                .collection("US4U").document("MENU").collection("Món Chính");
        FirestoreRecyclerOptions<Food> oMeals = new FirestoreRecyclerOptions.Builder<Food>()
                .setQuery(qMeals, Food.class)
                .build();

        //Top Picks
        popularFoodsLayout =new GridLayoutManager(MainActivity.this,2);
        mealsAdapter =new FirestoreAdapter(oMeals,this,this);
        rvMeals.setLayoutManager(popularFoodsLayout);
        rvMeals.setAdapter(mealsAdapter);

        //New Dishes
        newDishesLinearLyout = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);
        rvNewDishes.setLayoutManager(newDishesLinearLyout);
        rvNewDishes.setAdapter(mealsAdapter);

        viewOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, ViewOrders.class);
                startActivity(intent);
            }
        });

        restLnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, RestaurantView.class);
                startActivity(intent);
                finish();
            }
        });

    }
    @Override
    protected void onStop(){
        super.onStop();
        mealsAdapter.stopListening();
    }

    @Override
    protected void onStart(){
        super.onStart();
        mealsAdapter.startListening();
    }

    private void getCategories(){

        categories.add(new Category("Món Trán Miệng",1,R.drawable.monchinh));
        categories.add(new Category("Nước Trái Cây",2,R.drawable.monchinh));
        categories.add(new Category("Món Chính",3,R.drawable.monchinh));
        categories.add(new Category("Món Khai Vị",4,R.drawable.monchinh));
        categories.add(new Category("Salads",5,R.drawable.monchinh));



    }

    /*private  void getFoods(){
        //(String imageUrl, String foodname, String description, String category, String status, int price)
        foods.add(new Food(R.drawable.dessertimg,"Dessert","double beef patty, provolone, homemade chili sauce, grilled onions, pickled sweet peppers, thin french fries, coleslaw. ","D","Active",200));
        foods.add(new Food(R.drawable.pizza,"Dessert","double beef patty, provolone, homemade chili sauce, grilled onions, pickled sweet peppers, thin french fries, coleslaw","D","Active",200));
        foods.add(new Food(R.drawable.juice,"Dessert","double beef patty, provolone, homemade chili sauce, grilled onions, pickled sweet peppers, thin french fries, coleslaw","D","Active",200));
        foods.add(new Food(R.drawable.mainmeal,"Dessert","double beef patty, provolone, homemade chili sauce, grilled onions, pickled sweet peppers, thin french fries, coleslaw","D","Active",200));
        foods.add(new Food(R.drawable.pizza,"Dessert","double beef patty, provolone, homemade chili sauce, grilled onions, pickled sweet peppers, thin french fries, coleslaw","D","Active",200));
        foods.add(new Food(R.drawable.mainmeal,"Dessert","double beef patty, provolone, homemade chili sauce, grilled onions, pickled sweet peppers, thin french fries, coleslawe","D","Active",200));
        foods.add(new Food(R.drawable.dessertimg,"Dessert","double beef patty, provolone, homemade chili sauce, grilled onions, pickled sweet peppers, thin french fries, coleslaw","D","Active",200));
        foods.add(new Food(R.drawable.juice,"Dessert","double beef patty, provolone, homemade chili sauce, grilled onions, pickled sweet peppers, thin french fries, coleslaw","D","Active",200));
        foods.add(new Food(R.drawable.mainmeal,"Dessert","double beef patty, provolone, homemade chili sauce, grilled onions, pickled sweet peppers, thin french fries, coleslaw","D","Active",200));
    }*/


    @Override
    public void onCategoryFoodItemClick(Category category) {
        System.out.println("Danh mục được chọn: "+category.getName());
    }

    @Override
    public void onItemclick(Food foodItem, int position) {
        viewFoodDetais(foodItem, position);
    }
        /*
    @Override
    public void onFoodItemClick(Food food, int position) {
        System.out.println("Clicked Food Item: "+food.getFoodname()+" at position: " +position);
        Bundle bundle=new Bundle();
        Intent intent=new Intent(MainActivity.this, FoodDetail.class);
        bundle.putString("NAME",food.getFoodname());
        bundle.putString("CATEGORY",food.getCategory());
        bundle.putString("DESCRIPTION",food.getDescription());
        bundle.putString("IMAGE",food.getImageUrl());
        bundle.putInt("PRICE",food.getPrice());
        intent.putExtras(bundle);
        startActivity(intent);
    }
    */

    private void initializeViews(){
        rvCategories=findViewById(R.id.rv_restaurants);
        rvMeals =findViewById(R.id.rv_meals);
        rvNewDishes= findViewById(R.id.rv_newDishes);
        fab=findViewById(R.id.fab_cart);
        homeLnl=findViewById(R.id.home_layout);
        restLnl=findViewById(R.id.rest_layout);
       // settingsLnl=findViewById(R.id.settings_layout);
        profileLnl=findViewById(R.id.profile_layout);
        hinameTxt=findViewById(R.id.textView_hi_name);
        viewOrdersBtn=findViewById(R.id.view_ordersBtn);
    }

    @Override
    public void onDishItemclick(Food foodItem, int position) {
        viewFoodDetais(foodItem,position);
    }

    private void viewFoodDetais(Food foodItem, int position){
        System.out.println("Tên món ăn được chọn: "+foodItem.getFoodname()+" tại vị trí: " +position +" có mã số: "+foodItem.getId());

        Bundle bundle=new Bundle();
        Intent intent=new Intent(MainActivity.this, FoodDetail.class);

        bundle.putString(Constants.FOOD_NAME,foodItem.getFoodname());
        bundle.putString(Constants.FOOD_ID,foodItem.getId());
        bundle.putString(Constants.FOOD_CATEGORY,foodItem.getCategory());
        bundle.putString(Constants.FOOD_DESCRIPTION,foodItem.getDescription());
        bundle.putString(Constants.FOOD_IMAGE_URL,foodItem.getImageUrl());
        bundle.putInt(Constants.FOOD_PRICE,foodItem.getPrice());
        bundle.putString(Constants.FOOD_STATUS,foodItem.getStatus());
        intent.putExtras(bundle);

        startActivity(intent);
    }

}