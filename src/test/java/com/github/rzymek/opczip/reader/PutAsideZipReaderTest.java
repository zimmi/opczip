package com.github.rzymek.opczip.reader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;

import static com.github.rzymek.opczip.reader.OrderedZipStreamReaderTest.generateEntry;
import static com.github.rzymek.opczip.reader.ZipEntryReaderTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PutAsideZipReaderTest {

    final static File testFile = new File("target", PutAsideZipReaderTest.class.getName() + ".zip");
    private static final int SIZE = 40;

    @BeforeAll
    static void createTestFile() throws Exception {
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(testFile))) {
            zip.setLevel(Deflater.BEST_COMPRESSION);
            generateEntry(zip, '1', SIZE);
            generateEntry(zip, '2', SIZE);
            generateEntry(zip, '3', SIZE);
            generateEntry(zip, '4', SIZE);
            generateEntry(zip, '5', SIZE);
            zip.closeEntry();
        }
    }

    @Test
    void test() throws IOException {
        try (PutAsideZipStreamReader reader = new PutAsideZipStreamReader(new FileInputStream(testFile))) {
            assertEquals("file_1.txt", reader.nextEntry().getName());
            assertEquals("1111111111111111111111111111111111111111", entryToString(reader));
            assertEquals("file_2.txt", reader.nextEntry().getName());
            reader.saveStream();
            assertEquals("file_3.txt", reader.nextEntry().getName());
            assertEquals("3333333333333333333333333333333333333333", entryToString(reader));
            assertEquals("file_4.txt", reader.nextEntry().getName());
            reader.skipStream();
            assertEquals("file_5.txt", reader.nextEntry().getName());
            assertEquals("5555555555555555555555555555555555555555", entryToString(reader));
            assertNull(reader.nextEntry());
            assertNull(reader.getCompressedStream());
            assertNull(reader.nextEntry());
            assertEquals("2222222222222222222222222222222222222222", ZipEntryReaderTest.toString(reader.restoreStream("file_2.txt")));
        }
    }

}
