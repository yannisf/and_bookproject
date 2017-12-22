package fraglab.net.bookproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import fraglab.net.bookproject.zxing.IntentIntegrator;
import fraglab.net.bookproject.zxing.IntentResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void okClick(View view) {
        EditText isbnEdit = findViewById(R.id.input_isbn_id);
        String isbn = isbnEdit.getText().toString();
        setProgressBarVisibility(View.VISIBLE);
        hideKeyboard(view);
        new HttpRequestTask().execute(isbn);
    }

    private void setProgressBarVisibility(int visible) {
        ProgressBar progressBar = findViewById(R.id.search_progress_bar_id);
        progressBar.setVisibility(visible);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void resetClick(View view) {
        setProgressBarVisibility(View.INVISIBLE);
        EditText isbnEdit = findViewById(R.id.input_isbn_id);
        isbnEdit.setText("");
        bindText(R.id.value_isbn_id, "");
        bindText(R.id.value_title_id, "");
        bindText(R.id.value_author_id, "");
        bindText(R.id.value_publisher_id, "");
        bindText(R.id.value_source_url_id, "");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        TextView isbnTextView = findViewById(R.id.value_isbn_id);
        TextView titleTextView = findViewById(R.id.value_title_id);
        TextView authorTextView = findViewById(R.id.value_author_id);
        TextView publisherTextView = findViewById(R.id.value_publisher_id);
        TextView sourceUrlTextView = findViewById(R.id.value_source_url_id);
        savedInstanceState.putCharSequence("isbn", isbnTextView.getText());
        savedInstanceState.putCharSequence("author", authorTextView.getText());
        savedInstanceState.putCharSequence("title", titleTextView.getText());
        savedInstanceState.putCharSequence("publisher", publisherTextView.getText());
        savedInstanceState.putCharSequence("sourceUrl", sourceUrlTextView.getText());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        CharSequence isbn = savedInstanceState.getCharSequence("isbn");
        TextView isbnTextView = findViewById(R.id.value_isbn_id);
        isbnTextView.setText(isbn);
        CharSequence title = savedInstanceState.getCharSequence("title");
        TextView titleTextView = findViewById(R.id.value_title_id);
        titleTextView.setText(title);
        CharSequence author = savedInstanceState.getCharSequence("author");
        TextView authorTextView = findViewById(R.id.value_author_id);
        authorTextView.setText(author);
        CharSequence publisher = savedInstanceState.getCharSequence("publisher");
        TextView publisherTextView = findViewById(R.id.value_publisher_id);
        publisherTextView.setText(publisher);
        CharSequence sourceUrl = savedInstanceState.getCharSequence("sourceUrl");
        TextView soureUrlTextView = findViewById(R.id.value_source_url_id);
        soureUrlTextView.setText(sourceUrl);
    }


    private class HttpRequestTask extends AsyncTask<String, Void, BookInformation> {

        @Override
        protected BookInformation doInBackground(String... params) {
            try {
                final String url = String.format("http://192.168.1.19:8080/bookproject/search?isbn=%s&provider=biblionet", params[0]);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                return restTemplate.getForObject(url, BookInformation.class);
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return new BookInformation();
        }

        @Override
        protected void onPostExecute(BookInformation bookInformation) {
            setProgressBarVisibility(View.INVISIBLE);
            bindText(R.id.value_isbn_id, bookInformation.getIsbn());
            bindText(R.id.value_title_id, bookInformation.getTitle());
            bindText(R.id.value_author_id, bookInformation.getAuthor());
            bindText(R.id.value_publisher_id, bookInformation.getPublisher());
            bindText(R.id.value_source_url_id, bookInformation.getSourceUrl());
        }

    }

    private void bindText(int id, String text) {
        TextView textView = findViewById(id);
        textView.setText(text);
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
