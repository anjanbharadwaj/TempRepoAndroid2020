package com.example.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if (!isFirstRun) {
            //show start activity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {


            setContentView(R.layout.onboarding_main_layout);

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).commit();

            PaperOnboardingEngine engine = new PaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());

            engine.setOnChangeListener(new PaperOnboardingOnChangeListener() {
                @Override
                public void onPageChanged(int oldElementIndex, int newElementIndex) {
                }
            });

            engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
                @Override
                public void onRightOut() {
                    Toast.makeText(getApplicationContext(), "Welcome to OneSharedSchool!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage("Students", "Chat with students around the globe, learn about other schools, and become a global citizen.",
                Color.parseColor("#678FB4"), R.drawable.student_icon, R.drawable.student_icon_small);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Donors", "Find schools around the globe in need of donations, and donate with the confidence that your money is being put to good use.",
                Color.parseColor("#65B0B4"), R.drawable.donor_icon, R.drawable.donor_icon_small);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Schools", "Set up fundraising campaigns for prospective donors, create a virtual classroom for students to interact with others, and share plans with other schools.",
                Color.parseColor("#9B90BC"), R.drawable.school_icon, R.drawable.school_icon_small);
        PaperOnboardingPage scr4 = new PaperOnboardingPage("For kids, by kids.", "",
                Color.parseColor("#1f945d"), R.drawable.logo_2, R.drawable.move_on);
        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        elements.add(scr4);
        return elements;
    }
}
