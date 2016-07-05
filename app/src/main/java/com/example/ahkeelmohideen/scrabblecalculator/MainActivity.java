package com.example.ahkeelmohideen.scrabblecalculator;


import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity implements SpellCheckerSession.SpellCheckerSessionListener {
    TextView out;
    TextView combos;
    EditText field;
    char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    int[] points = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        field = (EditText) findViewById(R.id.editText);
        out = (TextView) findViewById(R.id.textview2);
        combos = (TextView) findViewById(R.id.combos);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(235887);
        progressBar.setVisibility(View.GONE);
        // ViewGroup vg = findViewById (R.id.re);


        field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {

                    progressBar.setVisibility(View.VISIBLE);

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    String enteredWord = field.getText().toString().replaceAll("\\s+", "");

                    if (checkWord(enteredWord)) {
                        out.setText("Points Worth: " + calcPoints(enteredWord));
                        out.setTextColor(Color.GREEN);

                    } else {
                        out.setText("Not a Word!");
                        out.setTextColor(Color.RED);
                    }

                    new LongOperation().execute(enteredWord);

                    return true;
                }
                return false;
            }
        });
    }

    public boolean checkWord(String string) {

        String line = "";

        try {
            BufferedReader reader;

            AssetManager assetManager = getAssets();
            InputStream stream;

            stream = assetManager.open("web2.txt");
            reader = new BufferedReader(new InputStreamReader(stream));

            while ((line = reader.readLine()) != null) {
                if (string.equalsIgnoreCase(line)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    public int calcPoints(String string) {

        char[] word = string.toCharArray();
        int score = 0;

        for (int i = 0; i < word.length; i++) {

            score += points[numInAlphabet(word[i])];
        }

        return score;

    }

    public int numInAlphabet(char c) {

        for (int i = 0; i < letters.length; i++) {

            if (c == letters[i])
                return i;
        }

        return -1;
    }

    public void findCombos(String word) {

        //set Progress Bar Visible

        char[] splitWord = word.toCharArray();
        String combos = "";
        Boolean curLine = true;
        String line = "";

        try {
            BufferedReader reader;
            AssetManager assetManager = getAssets();
            InputStream stream;

            stream = assetManager.open("web2.txt");
            reader = new BufferedReader(new InputStreamReader(stream));

            while ((line = reader.readLine()) != null) {

                line = line.replaceAll("\\s+", "");

                if (line.length() < 8) {

                    for (int i = 0; i < splitWord.length; i++) {

                        if (!line.contains("" + splitWord[i]))
                            curLine = false;
                    }

                    if (curLine) {
                        combos += " " + line;
                    }
                }
                curLine = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //comboOut.setText(combos);
        return;
    }


    @Override
    public void onGetSuggestions(SuggestionsInfo[] results) {

    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {

    }

    class LongOperation extends AsyncTask<String, Integer, String> {

        Integer count = 0;

        @Override
        protected String doInBackground(String... params) {
            //set Progress Bar Visible

            char[] splitWord = params[0].toCharArray();
            String combos = "";
            Boolean curLine = true;
            String line = "";


            try {
                BufferedReader reader;
                AssetManager assetManager = getAssets();
                InputStream stream;

                stream = assetManager.open("web2.txt");
                reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {

                    count++;
                    publishProgress(count);

                    line = line.replaceAll("\\s+", "");

                    if (line.length() < 8) {

                        for (int i = 0; i < splitWord.length; i++) {

                            if (!line.contains("" + splitWord[i]))
                                curLine = false;
                        }

                        if (curLine) {
                            combos += " " + line;
                        }
                    }
                    curLine = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return combos;
        }

        @Override
        protected void onPostExecute(String result) {
            TextView txt = (TextView) findViewById(R.id.combos);
            progressBar.setVisibility(View.GONE);
            txt.setText(result);

        }

        @Override
        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            progressBar.setProgress(values[0]);
        }
    }
}

