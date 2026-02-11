package com.codewithmad.smatool.source;

import com.codewithmad.smatool.model.Post;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JsonFileSource implements DataSource {
    private final Path filePath;

    public JsonFileSource(String path) {
        this.filePath = Paths.get(path);
    }

    @Override
    public List<Post> fetch() throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("Dosya bulunamadı: " + filePath);
        }

        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            Post[] posts = gson.fromJson(reader, Post[].class);
            return posts != null ? Arrays.asList(posts) : Collections.emptyList();
        }
    }
}