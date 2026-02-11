package com.codewithmad.smatool.source;

import com.codewithmad.smatool.model.Post;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ApiSource implements DataSource {
    private final String apiUrl;

    public ApiSource(String url) {
        this.apiUrl = url;
    }

    @Override
    public List<Post> fetch() throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int status = conn.getResponseCode();
        if (status != 200) {
            throw new IOException("API Hatası: HTTP " + status);
        }

        try (Reader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            Post[] posts = gson.fromJson(reader, Post[].class);
            return posts != null ? Arrays.asList(posts) : Collections.emptyList();
        } finally {
            conn.disconnect();
        }
    }
}