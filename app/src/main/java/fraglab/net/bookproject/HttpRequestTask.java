package fraglab.net.bookproject;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;
import java.util.Map;

class HttpRequestTask extends AsyncTask<String, Void, BookInformation> {

    private final Map<ResultField, TextView> resultTextViewMap;
    private final WeakReference<ProgressBar> progressBar;

    HttpRequestTask(Map<ResultField, TextView> resultTextViewMap,
                    ProgressBar progressBar) {
        this.resultTextViewMap = resultTextViewMap;
        this.progressBar = new WeakReference<>(progressBar);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.get().setVisibility(View.VISIBLE);
    }

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
        progressBar.get().setVisibility(View.INVISIBLE);
        resultTextViewMap.get(ResultField.ISBN).setText(bookInformation.getIsbn());
        resultTextViewMap.get(ResultField.TITLE).setText(bookInformation.getTitle());
        resultTextViewMap.get(ResultField.AUTHOR).setText(bookInformation.getAuthor());
        resultTextViewMap.get(ResultField.PUBLISHER).setText(bookInformation.getPublisher());
        resultTextViewMap.get(ResultField.SOURCE_URL).setText(bookInformation.getSourceUrl());
    }

}
