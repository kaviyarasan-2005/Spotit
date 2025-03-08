package com.example.spotit;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final HashMap<String, String> wordCache = new HashMap<>(); // Caching meanings
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Background thread

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        String fullText = "Tap on any word to see its meaning! Development and dedication is key to success.";
        textView.setText(fullText);

        makeTextClickable(textView, fullText);
    }

    private void makeTextClickable(TextView textView, String fullText) {
        SpannableString spannableString = new SpannableString(fullText);
        String[] words = fullText.split(" ");
        int startIndex = 0;

        for (String word : words) {
            final int wordStart = startIndex;
            final int wordEnd = startIndex + word.length();

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    fetchMeaning(word);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(Color.WHITE);
                }
            };

            spannableString.setSpan(clickableSpan, wordStart, wordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            startIndex = wordEnd + 1;
        }

        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void fetchMeaning(String word) {
        if (wordCache.containsKey(word)) {
            showTopDialog(wordCache.get(word)); // Use cached meaning
        } else {
            executorService.execute(() -> { // Run API call in background
                DictionaryAPIHelper.fetchMeaning(word, result -> {
                    wordCache.put(word, result); // Cache result
                    runOnUiThread(() -> showTopDialog(result)); // Update UI safely
                });
            });
        }
    }

    private void showTopDialog(String message) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.top_dialog);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.TOP);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView textViewDialog = dialog.findViewById(R.id.textViewDialog);
        textViewDialog.setText(message);

        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
