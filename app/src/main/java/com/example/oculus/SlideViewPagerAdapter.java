package com.example.oculus;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class SlideViewPagerAdapter extends PagerAdapter {
    Context context;

    public SlideViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_screen,container,false);
        ImageView background = view.findViewById(R.id.background_image);
        ImageView objectDetc = view.findViewById(R.id.objectDetc);
        ImageView voiceCom = view.findViewById(R.id.voiceCom);
        ImageView textRecon = view.findViewById(R.id.textRecon);
        ImageView settings = view.findViewById(R.id.settings);
        ImageView ind1 = view.findViewById(R.id.indicator_1);
        ImageView ind2 = view.findViewById(R.id.indicator_2);
        ImageView ind3 = view.findViewById(R.id.indicator_3);
        ImageView ind4 = view.findViewById(R.id.indicator_4);

        TextView tutorial_text1 = view.findViewById(R.id.tutorial_text1);
        TextView tutorial_text2 = view.findViewById(R.id.tutorial_text2);

        Button skipButton = view.findViewById(R.id.skip_button);
        Button tourButton = view.findViewById(R.id.tour_button);
        Button nextEndButton = view.findViewById(R.id.next_end_button);
        Button backButton = view.findViewById(R.id.back_button);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        nextEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlideActivity.viewPager.setCurrentItem(position + 1);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlideActivity.viewPager.setCurrentItem(position - 1);
            }
        });

        switch (position) {
            case 0:
                objectDetc.setVisibility(view.INVISIBLE);
                voiceCom.setVisibility(view.INVISIBLE);
                textRecon.setVisibility(view.INVISIBLE);
                settings.setVisibility(view.INVISIBLE);
                tutorial_text1.setVisibility(view.INVISIBLE);
                tutorial_text2.setVisibility(view.INVISIBLE);
                ind1.setVisibility(view.INVISIBLE);
                ind2.setVisibility(view.INVISIBLE);
                ind3.setVisibility(view.INVISIBLE);
                ind4.setVisibility(view.INVISIBLE);

                tourButton.setEnabled(true);
                tourButton.setVisibility(view.VISIBLE);
                nextEndButton.setEnabled(false);
                nextEndButton.setVisibility(view.INVISIBLE);

                background.setImageResource(R.drawable.welcome_screen);

                tourButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SlideActivity.viewPager.setCurrentItem(position + 1);
                    }
                });

                backButton.setEnabled(false);
                backButton.setVisibility(view.INVISIBLE);
                break;

            case 1:
                objectDetc.setVisibility(view.VISIBLE);
                voiceCom.setVisibility(view.INVISIBLE);
                textRecon.setVisibility(view.VISIBLE);
                textRecon.setAlpha(0.2F);
                settings.setVisibility(view.INVISIBLE);

                ind1.setVisibility(view.VISIBLE);
                ind2.setVisibility(view.VISIBLE);
                ind3.setVisibility(view.VISIBLE);
                ind4.setVisibility(view.VISIBLE);

                backButton.setEnabled(true);
                backButton.setVisibility(view.VISIBLE);

                ind1.setImageResource(R.drawable.selected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);


                tutorial_text1.setVisibility(view.VISIBLE);
                tutorial_text1.setText("Press the Object Detection Button On The Top Left Edge of the screen or say \"Object\"  to detect an object in front of your camera.");
                tutorial_text2.setVisibility(view.INVISIBLE);


                tourButton.setEnabled(false);
                tourButton.setVisibility(view.INVISIBLE);
                break;

            case 2:
                objectDetc.setVisibility(view.VISIBLE);
                objectDetc.setAlpha(0.2F);
                voiceCom.setVisibility(view.INVISIBLE);
                textRecon.setVisibility(view.VISIBLE);
                settings.setVisibility(view.INVISIBLE);

                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.selected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);

                tutorial_text1.setVisibility(view.VISIBLE);
                tutorial_text1.setText("Press the Text Recognition Button On The Top Right Edge of the screen or say \"Text\" to detect the text in front of your camera by clicking on the text block.");
                tutorial_text2.setVisibility(view.INVISIBLE);

                tourButton.setEnabled(false);
                tourButton.setVisibility(view.INVISIBLE);
                break;

            case 3:
                objectDetc.setVisibility(view.INVISIBLE);
                voiceCom.setVisibility(view.VISIBLE);
                textRecon.setVisibility(view.INVISIBLE);
                settings.setVisibility(view.VISIBLE);
                settings.setAlpha(0.2F);

                ind2.setImageResource(R.drawable.unselected);
                ind1.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.selected);
                ind4.setImageResource(R.drawable.unselected);

                tutorial_text1.setVisibility(view.INVISIBLE);
                tutorial_text1.setText(null);
                tutorial_text2.setVisibility(view.VISIBLE);
                tutorial_text2.setText("Press the Voice Assistant button on the bottom left edge or say \"Enable Voice\" to toggle Voice Assistant Feature. You can start Voice Assistant by shaking your phone.");
                skipButton.setEnabled(true);
                skipButton.setVisibility(view.VISIBLE);

                tourButton.setEnabled(false);
                tourButton.setVisibility(view.INVISIBLE);
                break;

            case 4:
                objectDetc.setVisibility(view.INVISIBLE);
                voiceCom.setVisibility(view.VISIBLE);
                voiceCom.setAlpha(0.2F);
                textRecon.setVisibility(view.INVISIBLE);
                settings.setVisibility(view.VISIBLE);

                ind2.setImageResource(R.drawable.unselected);
                ind1.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.selected);

                tutorial_text1.setVisibility(view.INVISIBLE);
                tutorial_text1.setText(null);
                tutorial_text2.setVisibility(view.VISIBLE);
                tutorial_text2.setText("Press the settings button on the bottom right edge of the screen or say \"SETTINGS\" to access Accessibility and App info Options");
                skipButton.setEnabled(false);
                skipButton.setVisibility(view.INVISIBLE);
                nextEndButton.setText("End");

                tourButton.setEnabled(false);
                tourButton.setVisibility(view.INVISIBLE);

                nextEndButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,MenuActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }
                });

                break;
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);

    };
}
