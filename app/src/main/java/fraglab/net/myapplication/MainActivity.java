package fraglab.net.myapplication;

import android.content.Context;
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
        bindText(R.id.value_isbn_id, "");
        bindText(R.id.value_title_id, "");
        bindText(R.id.value_author_id, "");
        bindText(R.id.value_publisher_id, "");
        bindText(R.id.value_source_url_id, "");
    }

    private class HttpRequestTask extends AsyncTask<String, Void, BookInformation> {

        @Override
        protected BookInformation doInBackground(String...params) {
            try {
                final String url = String.format("http://192.168.1.19:8080/bookproject/search?isbn=%s", params[0]);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                return restTemplate.getForObject(url, BookInformation.class);
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
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

}
