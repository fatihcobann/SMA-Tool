package com.codewithmad.smatool.source;

import com.codewithmad.smatool.model.Post;
import java.io.IOException;
import java.util.List;

public interface DataSource {
    List<Post> fetch() throws IOException;
}