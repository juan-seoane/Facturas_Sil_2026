package modelo.helpers;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class RecordTypeAdapter<T> extends TypeAdapter<T> {
    private final Gson gson = new Gson();
    private final Class<T> type;

    public RecordTypeAdapter(Class<T> type) {
        this.type = type;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        gson.toJson(gson.toJsonTree(value), out);
    }

    @Override
    public T read(JsonReader in) throws IOException {
        return gson.fromJson(in, type);
    }
}

