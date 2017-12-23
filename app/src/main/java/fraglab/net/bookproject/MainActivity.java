package fraglab.net.bookproject;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.validator.routines.ISBNValidator;

import java.util.HashMap;
import java.util.Map;

import fraglab.net.bookproject.zxing.IntentIntegrator;
import fraglab.net.bookproject.zxing.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Map<ResultField, TextView> resultTextViewMap;
    private ProgressBar progressBar;

    private Map<ResultField, TextView> initializeResultTextViewMap() {
        Map<ResultField, TextView> map = new HashMap<>();
        map.put(ResultField.ISBN, (TextView) findViewById(ResultField.ISBN.id));
        map.put(ResultField.AUTHOR, (TextView) findViewById(ResultField.AUTHOR.id));
        map.put(ResultField.TITLE, (TextView) findViewById(ResultField.TITLE.id));
        map.put(ResultField.PUBLISHER, (TextView) findViewById(ResultField.PUBLISHER.id));
        map.put(ResultField.SOURCE_URL, (TextView) findViewById(ResultField.SOURCE_URL.id));
        return map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        resultTextViewMap = initializeResultTextViewMap();
        progressBar = findViewById(R.id.search_progress_bar_id);
        final EditText isbnEdit = findViewById(R.id.input_isbn_id);
        isbnEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ColorStateList colorStateList = ColorStateList.valueOf(color);
//                ViewCompat.getBackgroundTintList(isbnEdit);
//                ViewCompat.setBackgroundTintList(editText, colorStateList);

                String isbn = s.toString();
                if (ISBNValidator.getInstance().isValid(isbn)) {
                    isbnEdit.setTextColor(Color.GREEN);
                } else {
                    isbnEdit.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        for (Map.Entry<ResultField, TextView> entry : resultTextViewMap.entrySet()) {
            savedInstanceState.putCharSequence(entry.getKey().name(), entry.getValue().getText());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        for (Map.Entry<ResultField, TextView> entry : resultTextViewMap.entrySet()) {
            CharSequence text = savedInstanceState.getCharSequence(entry.getKey().name());
            entry.getValue().setText(text);
        }
    }

    public void okClick(View view) {
        String isbn = getInputIsbn();
        hideKeyboard(view);
        new HttpRequestTask(resultTextViewMap, progressBar).execute(isbn);
    }

    @NonNull
    private String getInputIsbn() {
        EditText isbnEdit = findViewById(R.id.input_isbn_id);
        return isbnEdit.getText().toString();
    }

    private void setInputIsbn(String inputIsbn) {
        EditText isbnEdit = findViewById(R.id.input_isbn_id);
        isbnEdit.setText(inputIsbn);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void resetClick(View view) {
        progressBar.setVisibility(View.INVISIBLE);
        setInputIsbn("");
        for (Map.Entry<ResultField, TextView> entry : resultTextViewMap.entrySet()) {
            entry.getValue().setText("");
        }
    }

    public void scan(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            EditText isbnEdit = findViewById(R.id.input_isbn_id);
            isbnEdit.setText(scanResult.getContents());
            this.okClick(isbnEdit);
        }
    }

}
