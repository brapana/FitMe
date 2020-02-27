package com.example.fitme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    final FragmentManager fm = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationMenu = findViewById(R.id.bottomNavigation);

        bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()){
                    case R.id.actionFavorites:
                        fragment = new FavoritesFragment();
                        break;
                    case R.id.actionFoodDiary:
                        fragment = new FoodDiaryFragment();
                        break;
                    case R.id.actionSchedule:
                        fragment = new ScheduleFragment();
                        break;
                    case R.id.actionProfile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.actionHome:
                    default:
                        fragment = new HomeFragment();
                }
                fm.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationMenu.setSelectedItemId(R.id.actionHome);
    }
}
