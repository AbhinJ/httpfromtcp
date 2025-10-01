package com.jain.abhinav;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReadFile {
    public static final String POISON_PILL = "::END_OF_STREAM::";
    public static BlockingQueue<String> getLinesFromProducer(InputStream is) {
        BlockingQueue<String> lines = new LinkedBlockingQueue<String>();
        Runnable producerTask = () -> {
            try(is) {
                byte[] buffer = new byte[8];
                int bytesRead;
                StringBuilder currentLine = new StringBuilder();
                while ((bytesRead = is.read(buffer)) != -1) {
                    String actualChunk = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                    String[] parts = actualChunk.split("\n", -1);
                    for (int i = 0; i < parts.length - 1; i++) {
                        currentLine.append(parts[i]);
                        lines.put(currentLine.toString());
                        currentLine.setLength(0);
                    }
                    currentLine.append(parts[parts.length - 1]);
                }
                if (!currentLine.isEmpty()) lines.put(currentLine.toString());
            } catch (IOException e) {
                System.err.println("Error reading from stream: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                try {
                    lines.put(POISON_PILL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        new Thread(producerTask).start();
        return lines;
    }
}
