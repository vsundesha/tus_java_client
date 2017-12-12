package io.tus.java.client;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestTusUpload {
    @Test
    public void testTusUploadFile() throws IOException {
        String content = "hello world";

        File file = File.createTempFile("tus-upload-test", ".tmp");
        OutputStream output = new FileOutputStream(file);
        output.write(content.getBytes());
        output.close();

        TusUpload upload = new TusUpload(file);

        Map<String, String> metadata = new LinkedHashMap<String, String>();
        metadata.put("foo", "hello");
        metadata.put("bar", "world");
        metadata.putAll(upload.getMetadata());

        assertEquals(metadata.get("filename"), file.getName());

        upload.setMetadata(metadata);
        assertEquals(upload.getMetadata(), metadata);
        assertEquals(
                upload.getEncodedMetadata(),
                "foo aGVsbG8=,bar d29ybGQ=,filename " + TusUpload.base64Encode(file.getName().getBytes()));

        assertEquals(upload.getSize(), content.length());
        assertNotSame(upload.getFingerprint(), "");
        byte[] readContent = new byte[content.length()];
        assertEquals(upload.getInputStream().read(readContent), content.length());
        assertEquals(new String(readContent), content);
    }
}
