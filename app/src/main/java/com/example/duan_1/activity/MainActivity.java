package com.example.duan_1.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.bumptech.glide.Glide;
import com.example.duan_1.adapter.ItemMain_Adapter;
import com.example.duan_1.adapter.SanphamNew_Adapter;
import com.example.duan_1.fragment.Fragment_Giohang;
import com.example.duan_1.fragment.Fragment_Sp_ChiTiet;
import com.example.duan_1.fragment.Fragment_Thit;
import com.example.duan_1.fragment.Fragment_Vegetable;
import com.example.duan_1.interface_.MyOnClickItemListener;
import com.example.duan_1.interface_.New_OnClickListener;
import com.example.duan_1.model_.GioHang;
import com.example.duan_1.model_.ItemProduct;
import com.example.duan_1.model_.Sanpham_New;
import com.example.duan_1.R;
import com.example.duan_1.ultil.Server_Local;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    private ImageView img_logOut, img_Cart;
    private ViewFlipper viewFlipper;
    TextView tv_hi ;
    Intent intent;
    List<ItemProduct> itemProducts;
    RecyclerView recyclerView_Item ;
    RecyclerView recyclerView_spNew;
    List<Sanpham_New> newList;
    GridLayoutManager layoutManager;
    SanphamNew_Adapter sanphamNew_adapter;
    Fragment fragment ;
    public static List<GioHang> gioHangList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this,"????ng Nh???p Th??nh C??ng",Toast.LENGTH_SHORT).show();
        initUI();
        sanphamNew();
        viewflipper();
        initListener();
        listProduct();



    }

    private void sanphamNew() {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Server_Local.Sp_New, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i<response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);
                        int ID = jsonObject.getInt("id");
                        String Tensp = jsonObject.getString("tensp");
                        Integer Giasp = jsonObject.getInt("giasp");
                        String Hinhanhsp = jsonObject.getString("hinhanhsp");
                        String Motasp = jsonObject.getString("motasp");
                        int IDsanpham = jsonObject.getInt("idsanpham");
                        newList.add(new Sanpham_New(ID,Tensp,Giasp,Hinhanhsp,Motasp,IDsanpham));
                        sanphamNew_adapter.notifyDataSetChanged();
                    }
                }catch (Exception e){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
        newList = new ArrayList<>();
        layoutManager = new GridLayoutManager(getApplicationContext(),4 );
        sanphamNew_adapter = new SanphamNew_Adapter(getApplicationContext(), newList);
        recyclerView_spNew.setHasFixedSize(true);
        recyclerView_spNew.setAdapter(sanphamNew_adapter);
        recyclerView_spNew.setLayoutManager(layoutManager);

        sanphamNew_adapter.New_Click(new New_OnClickListener() {
            @Override
            public void onClick(Sanpham_New sanpham_new) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = new Fragment_Sp_ChiTiet();
                Bundle bundle = new Bundle();
                bundle.putInt("id",sanpham_new.getID());
                bundle.putString("tensp", sanpham_new.getTensanpham());
                bundle.putInt("giasp", sanpham_new.getGiasanpham());
                bundle.putString("hinhsp", sanpham_new.getHinhanhsanpham());
                bundle.putString("motasp", sanpham_new.getMotasanpham());
                bundle.putInt("idsanpham",sanpham_new.getIDsanpham());
                fragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.layout_Main, fragment).commit();
                fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
            }
        });
    }

    private void listProduct() {
        itemProducts = new ArrayList<>();
        itemProducts.add(new ItemProduct(R.drawable.meat_icon, "Th???t"));
        itemProducts.add(new ItemProduct(R.drawable.vegetable_icon, "Rau S???ch"));
        itemProducts.add(new ItemProduct(R.drawable.fruit_icon, "Hoa Qu???"));
        itemProducts.add(new ItemProduct(R.drawable.egg_icon, "Tr???ng"));
        itemProducts.add(new ItemProduct(R.drawable.rice_icon, "G???o"));
        itemProducts.add(new ItemProduct(R.drawable.milk_icon, "S???a"));
        itemProducts.add(new ItemProduct(R.drawable.seafood_icon, "H???i S???n"));
        itemProducts.add(new ItemProduct(R.drawable.true_juice_icon, "N?????c ??p"));
        itemProducts.add(new ItemProduct(R.drawable.combofood_icon, "Combo"));
        itemProducts.add(new ItemProduct(R.drawable.fastfood_icon, "??n V???t"));
        itemProducts.add(new ItemProduct(R.drawable.cskh_icon, "CSKH"));
        itemProducts.add(new ItemProduct(R.drawable.help_icon, "H??? Tr???"));

        ItemMain_Adapter main_adapter = new ItemMain_Adapter(itemProducts, MainActivity.this);
        recyclerView_Item.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        recyclerView_Item.setHasFixedSize(true);
        recyclerView_Item.setAdapter(main_adapter);

        //S??? l?? s??? ki???n click v??o item trong RV
        main_adapter.setMyOnClickItemListener(new MyOnClickItemListener() {
            @Override
            public void onClick(ItemProduct itemProduct) {
                switch (itemProduct.getProductName()){
                    case "Th???t" :
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragment = new Fragment_Thit();
                        fragmentTransaction.replace(R.id.layout_Main,fragment).commit();
                        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
                        break;
                    case "Rau S???ch" :
                        FragmentManager fragmentManager1 = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction1= fragmentManager1.beginTransaction();
                        fragment = new Fragment_Vegetable();
                        fragmentTransaction1.replace(R.id.layout_Main,fragment).commit();
                        fragmentTransaction1.addToBackStack(fragment.getClass().getSimpleName());
                        break;
                    case "CSKH" :
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("G???i T???ng ????i Ch??m S??c Kh??ch H??ng !!!");
                        builder.setPositiveButton("G???i", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:0975414741"));
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("H???y", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();
                        break;
                }
            }
        });
    }
    public  void closeFr(){
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    private void viewflipper() {
        List<String> listViewFlipper = new ArrayList<>();
        listViewFlipper.add("https://amthuchue.info/wp-content/uploads/2019/06/meo-chon-thit-heo-thit-bo-01.png");
        listViewFlipper.add("https://thitbohalo.vn/wp-content/uploads/2017/11/thit-bo-uc-4-1024x576.jpg");
        listViewFlipper.add("https://cdn.huongnghiepaau.com/wp-content/uploads/2019/03/ky-thuat-che-bien-thit-bo.jpg");
        listViewFlipper.add("https://organicfood.vn/image/catalog/Anhblog/rau-cu-qua-huu-co-1.jpg");
        listViewFlipper.add("https://photo-1-baomoi.zadn.vn/w1000_r1/2020_02_10_304_33919863/cb78a7a896eb7fb526fa.jpg");
        listViewFlipper.add("https://anmongiday.com/wp-content/uploads/2019/09/chon-hai-san.jpg");
        listViewFlipper.add("https://toplist.vn/images/800px/dia-chi-ban-trai-cay-nhap-khau-chat-luong-tai-ha-noi-131158.jpg");
        listViewFlipper.add("https://halona.com.vn/wp-content/uploads/2019/08/diem-danh-20-loai-trai-cay-tot-cho-ba-bau-1-1.jpg");

        for (int i = 0; i < listViewFlipper.size(); i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(listViewFlipper.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3500);
        viewFlipper.setAutoStart(true);
        Animation slide_in_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in_right);
        viewFlipper.setOutAnimation(slide_out_right);
    }

    private void initListener() {

        //X??? L?? N??t ????ng Xu???t
        img_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
                builder.setTitle("????ng Xu???t?");
                builder.setMessage("B???n C?? Mu???n ????ng Xu???t Kh??ng ?");
                builder.setCancelable(true);
                builder.setPositiveButton("?????ng ??", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.getInstance().logOut();
                        intent = new Intent(MainActivity.this, Login_Activity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
        //X??? L?? N??t ????ng Xu???t
        img_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = new Fragment_Giohang();
                fragmentTransaction.add(R.id.layout_Main, fragment).commit();
                fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
            }
        });


    }

    private void initUI() {
        AndroidNetworking.initialize(MainActivity.this);
        tv_hi  = findViewById(R.id.tv_hi);
        img_Cart = findViewById(R.id.img_Cart);
        recyclerView_Item = findViewById(R.id.recycleView_main);
        img_logOut = findViewById(R.id.img_logOut);
        viewFlipper = findViewById(R.id.viewFliper);
        recyclerView_spNew = findViewById(R.id.recycleView_spNew);
        if (gioHangList !=null){

        }else {
            gioHangList = new ArrayList<>();
        }
        intent = getIntent();
        String hi = intent.getStringExtra("user");
        tv_hi.setText("Xin ch??o, "+hi);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LoginManager.getInstance().logOut();
    }


    public void noClick(View view) {
    }
}
