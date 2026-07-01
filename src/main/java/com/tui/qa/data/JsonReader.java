package com.tui.qa.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public final class JsonReader {

    private JsonReader() {
    }

    public static <T> T read(String fileName, Class<T> clazz) {

        try (InputStream inputStream = JsonReader.class
                .getClassLoader()
                .getResourceAsStream("testdata/" + fileName)) {

            if (inputStream == null) {
                throw new RuntimeException(fileName + " not found.");
            }

            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(inputStream, clazz);

        } catch (IOException e) {
            throw new RuntimeException("Unable to read test data.", e);
        }
    }
}