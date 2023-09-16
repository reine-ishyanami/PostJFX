package com.reine.postjfx.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MultipartBodyBuilder {
    private final String boundary;
    private final List<Part> parts;

    public MultipartBodyBuilder(String boundary) {
        this.boundary = boundary;
        this.parts = new ArrayList<>();
    }

    public void addTextPart(String name, String value) {
        parts.add(new TextPart(name, value));
    }

    public void addFilePart(String name, String fileName, Path filePath) {
        parts.add(new FilePart(name, fileName, filePath));
    }

    public byte[] build() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (Part part : parts) {
            writeBoundary(outputStream);
            part.writeTo(outputStream);
        }

        writeEndBoundary(outputStream);
        return outputStream.toByteArray();
    }

    private void writeBoundary(OutputStream outputStream) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
    }

    private void writeEndBoundary(OutputStream outputStream) throws IOException {
        outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
    }

    private interface Part {
        void writeTo(OutputStream outputStream) throws IOException;
    }

    private record TextPart(String name, String value) implements Part {

        @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                String contentDisposition = "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n";
                outputStream.write(contentDisposition.getBytes(StandardCharsets.UTF_8));
                outputStream.write(value.getBytes(StandardCharsets.UTF_8));
                outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
            }
        }

    private record FilePart(String name, String fileName, Path filePath) implements Part {

        @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                String contentDisposition = "Content-Disposition: form-data; name=\"" + name +
                        "\"; filename=\"" + fileName + "\"\r\n";
                outputStream.write(contentDisposition.getBytes(StandardCharsets.UTF_8));

                // Add Content-Type header if needed
                String contentType = "Content-Type: " + Files.probeContentType(filePath) + "\r\n";
                outputStream.write(contentType.getBytes(StandardCharsets.UTF_8));

                outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

                Files.copy(filePath, outputStream);

                outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
            }
        }
}
