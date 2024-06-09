package com.example.schedully;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.schedully.databinding.ActivityCenterBinding;

public class activity_center extends AppCompatActivity {
    ActivityCenterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCenterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupFAB();
        setupBottomNavigationView();
        setupFilterIcon(false);
    }

    public void setupFilterIcon(boolean show) {
        ImageView filterIcon = findViewById(R.id.filter_icon);
        filterIcon.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            filterIcon.setOnClickListener(view -> showFilterPopup(view));
        }
    }

    public void showFilterPopup(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.inflate(R.menu.filter_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
            if (currentFragment instanceof AssignmentFragment) {
                ((AssignmentFragment) currentFragment).handleFilterSelection(item.getItemId());
            }
            return true;
        });
        popupMenu.show();
    }

    private void setupFAB() {
        binding.fab.setOnClickListener(v -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
            if (currentFragment instanceof OnFabClickListener) {
                ((OnFabClickListener) currentFragment).onFabClick();
            }
        });
    }

    private void setupBottomNavigationView() {
        replaceFragment(new DashboardFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setSelectedItemId(R.id.dashboard);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if(item.getItemId() == R.id.dashboard){
                fragment = new DashboardFragment();

            }else if(item.getItemId() == R.id.assignments){

                fragment = new AssignmentFragment();

            }else if(item.getItemId() == R.id.exams){

                fragment = new ExamFragment();
            }else if(item.getItemId() == R.id.activities){
                fragment = new ExtracurricularsFragment();
            }
            replaceFragment(fragment);
            return true;
        });    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    public interface OnFabClickListener {
        void onFabClick();
    }
}
