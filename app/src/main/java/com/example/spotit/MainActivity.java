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
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
private String Fetch;
private EditText wordInput;
private Button searchButton;
private  TextView resultView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        wordInput = findViewById(R.id.wordInput);
        searchButton = findViewById(R.id.searchButton);
        resultView = findViewById(R.id.resultView);
        // Dynamically set text
        searchButton.setOnClickListener(view -> {
            String w = wordInput.getText().toString().trim();
            if(!w.isEmpty()){
                fetchMeaning(w);
            }
            else{
                resultView.setText("Please enter the word");
            }
        });
        String fullText = "Tap on any word to see a Dialog message!";

        textView.setText(fullText);

        makeTextClickable(textView, fullText);
    }
    private void fetchMeaning(String w){
        DictionaryAPIHelper.fetchMeaning(w,result -> {
            Fetch = result;
            resultView.setText(Fetch);
        });
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
                    // Show Top Dialog instead of Toast
                    showTopDialog("You clicked: " + word);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false); // Remove underline
                    ds.setColor(Color.WHITE);   // Optional: Change color when clickable
                }
            };

            spannableString.setSpan(clickableSpan, wordStart, wordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            startIndex = wordEnd + 1; // Move to next word (+1 for space)
        }

        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance()); // Enable word clicking
    }

    private void showTopDialog(String message) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.top_dialog); // Use custom dialog layout

        // Set dialog position to top
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.TOP); // Moves the dialog to the top
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Transparent background
        }

        TextView textView1 = dialog.findViewById(R.id.textViewDialog);
        textView1.setText(message);

        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> dialog.dismiss()); // Close Dialog on Button Click

        dialog.show();
    }
}
