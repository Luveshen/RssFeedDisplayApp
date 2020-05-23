package com.example.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listApps;
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
    private int feedLimit = 10;

    private final String URL_KEY = "URL";
    private final String FEED_LIMIT_KEY = "KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = findViewById(R.id.xmlListView);

        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(URL_KEY);
            feedLimit = savedInstanceState.getInt(FEED_LIMIT_KEY);
        }

        downloadUrl(String.format(feedUrl, feedLimit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        if (feedLimit == 10) {
            menu.findItem(R.id.mnu10).setChecked(true);
        } else {
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String newFeedUrl = feedUrl;
        int newLimit = feedLimit;
        boolean refresh = false;
        switch (id) {
            case R.id.mnuFree:
                newFeedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                newFeedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                newFeedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    newLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + newLimit);
                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " feedLimit unchanged");
                    return super.onOptionsItemSelected(item);
                }
            case R.id.mnuRefresh:
                refresh = true;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        if (!newFeedUrl.equals(feedUrl) || newLimit != feedLimit || refresh) {
            feedUrl = newFeedUrl;
            feedLimit = newLimit;

            downloadUrl(String.format(feedUrl, feedLimit));
        }
        return true;
    }

    private void downloadUrl(String feedUrl) {
        Log.d(TAG, "downloadUrl: starting Asynctask");
        DownloadData downloadData = new DownloadData();
        downloadData.execute(feedUrl);
        Log.d(TAG, "downloadUrl: done");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(URL_KEY, feedUrl);
        outState.putInt(FEED_LIMIT_KEY, feedLimit);
        super.onSaveInstanceState(outState);
    }

    private class DownloadData extends AsyncTask<String, Void, String> {

        private static final String TAG = "DownloadData";

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: Response code " + response);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;
                char[] inputBuffer = new char[500];
                while (true) {
                    charsRead = reader.read(inputBuffer);
                    if (charsRead < 0) {
                        break;
                    } else if (charsRead > 0) {
                        xmlResult.append(inputBuffer, 0, charsRead);
                    }
                }

                reader.close();
                return xmlResult.toString();
            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: Security Exception. Needs permission? " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

            ArrayAdapter arrayAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseApplications.getApplications());

//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item, parseApplications.getApplications());
            listApps.setAdapter(arrayAdapter);
        }

    }
}
