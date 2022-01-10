package org.vaadin.example;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MarkovChainTest {
    private static final String mainTextPath = "src/test/SampleTexts/sampleLongText.txt";
    private static final String pdfFilePath = "src/test/SampleTexts/AnimalLogic_CL.pdf";
    private static final String jpgFilePath = "src/test/SampleTexts/GowthamMohan-COVID.JPG";
    private static final String shortFilePath = "src/test/SampleTexts/shortText.txt";

    @Test
    public void basicTestOneWordTest() throws IOException {
        File initialFile = new File(mainTextPath);
        InputStream targetStream = new FileInputStream(initialFile);
        MarkovTextGenerator markovTextGenerator = new MarkovTextGenerator();
        String outputText = markovTextGenerator.generateTextWithMarkov(targetStream,1,1);
        String[] words = outputText.split("\\s+");
        // we count the first input as 1, as the generation starts with a space. So in total we count N  + 1 words.
        Assert.assertEquals(words.length-1, 1);
    }

    @Test
    public void fullBasicTest() throws IOException {
        File initialFile = new File(mainTextPath);
        InputStream targetStream = new FileInputStream(initialFile);
        MarkovTextGenerator markovTextGenerator = new MarkovTextGenerator();
        String outputText = markovTextGenerator.generateTextWithMarkov(targetStream,50,200);
        String[] words = outputText.split("\\s+");
        Assert.assertEquals(words.length-1, 200);
    }

    @Test
    public void basicSmallFileTest() throws IOException {
        File initialFile = new File(shortFilePath);
        InputStream targetStream = new FileInputStream(initialFile);
        MarkovTextGenerator markovTextGenerator = new MarkovTextGenerator();
        String outputText = markovTextGenerator.generateTextWithMarkov(targetStream,2,4);
        String[] words = outputText.split("\\s+");
        // we count the first input as 1, as the generation starts with a space. So in total we count N  + 1 words.
        Assert.assertEquals(words.length-1, 4);
    }

    @Test
    public void fullBasicPDFTest() throws IOException {
        File initialFile = new File(pdfFilePath);
        InputStream targetStream = new FileInputStream(initialFile);
        MarkovTextGenerator markovTextGenerator = new MarkovTextGenerator();
        String outputText = markovTextGenerator.generateTextWithMarkov(targetStream,50,200);
        String[] words = outputText.split("\\s+");
        Assert.assertEquals(words.length-1, 200);
    }

    @Test
    public void sizeOverRangeTest() throws IOException {
        File initialFile = new File(mainTextPath);
        InputStream targetStream = new FileInputStream(initialFile);
        MarkovTextGenerator markovTextGenerator = new MarkovTextGenerator();
        Exception exception = Assert.assertThrows(RuntimeException.class, () -> {
            markovTextGenerator.generateTextWithMarkov(targetStream,500,1);
        });
        String expectedMessage = "Output size is less than the phrase range size";
        String actualMessage = exception.getMessage();

        Assert.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void zeroRangeAndSizeTest() throws IOException {
        File initialFile = new File(mainTextPath);
        InputStream targetStream = new FileInputStream(initialFile);
        MarkovTextGenerator markovTextGenerator = new MarkovTextGenerator();
        Exception exception = Assert.assertThrows(RuntimeException.class, () -> {
            markovTextGenerator.generateTextWithMarkov(targetStream,0,0);
        });
        String expectedMessage = "Size of your option cannot be less than 1";
        String actualMessage = exception.getMessage();

        Assert.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void nullFileTest() {
        MarkovTextGenerator markovTextGenerator = new MarkovTextGenerator();
        Exception exception = Assert.assertThrows(RuntimeException.class, () -> {
            markovTextGenerator.generateTextWithMarkov(null,10,20);
        });
        String expectedMessage = "File value is null. Need to add an acceptable txt file.";
        String actualMessage = exception.getMessage();

        Assert.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void pdfFileTest() throws IOException {
        File initialFile = new File(jpgFilePath);
        InputStream targetStream = new FileInputStream(initialFile);
        MarkovTextGenerator markovTextGenerator = new MarkovTextGenerator();
        String outputText = markovTextGenerator.generateTextWithMarkov(targetStream,50,200);
        String[] words = outputText.split("\\s+");
        Assert.assertEquals(words.length-1, 200);
    }

    @Test
    public void shortFileTest() throws IOException {
        File initialFile = new File(shortFilePath);
        InputStream targetStream = new FileInputStream(initialFile);
        MarkovTextGenerator markovTextGenerator = new MarkovTextGenerator();
        Exception exception = Assert.assertThrows(RuntimeException.class, () -> {
            markovTextGenerator.generateTextWithMarkov(targetStream,10,100);
        });
        String expectedMessage = "Output size is greater than the length of the word";
        String actualMessage = exception.getMessage();

        Assert.assertTrue(actualMessage.contains(expectedMessage));
    }
}
