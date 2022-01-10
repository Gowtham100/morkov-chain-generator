package org.vaadin.example;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

@Service
public class MarkovTextGenerator implements Serializable {

    /**
     * This is the main method that is called in the MainView class
     * This method takes the populated hashmap from the helper function (contains the prefix/suffix key value)
     * Then use those key value pairs and randomly combine them and add it to the stringResult with spaces.
     * Once all the suffix is used or if the suffix value is "" return the stringResult with space.
     * If the stringResult size is greater than outputSize then return the string result with space.
     *
     * @param file InputStream for the file that will be uploaded
     * @param phraseRangeSize Pool size of what is going ot be used in the Markov chain
     * @param outputSize Total output size
     * @return
     */
    public String generateTextWithMarkov(InputStream file, int phraseRangeSize, int outputSize) {

        //Make sure that the range of phrases is not less than 1
        // If this value is empty there will be  random text to have the ability to generate a text
        if (phraseRangeSize < 1) {throw new IllegalArgumentException("Size of your option cannot be less than 1");}
        if (file == null) {throw new IllegalArgumentException("File value is null. Need to add an acceptable txt file.");}

        // Read all file content and split them by words (by using the spaces between them)
        byte[] readFileContent = null;
        try {
            readFileContent = file.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] words = new String(readFileContent).trim().split("\\s+");

        // If outputSize is less than phraseRangeSize or outputSize is  more than the number of words in the file
        // A text cannot be generated as the outputSize would be out of range.
        if (outputSize < phraseRangeSize) {
            throw new IllegalArgumentException("Output size is less than the phrase range size");
        } else if (outputSize >= words.length) {
            throw new IllegalArgumentException("Output size is greater than the length of the word");
        }

        // Dictionary that contains all possible values for each key word
        Map<String, List<String>> wordDict = generateHashMap(phraseRangeSize, words);

        // Begin random generation
        int n = 0;
        Random random = new Random();
        int randomInt = random.nextInt(wordDict.size());
        // Prefix generation to generate connecting words
        // Select random phrase from the dictionary key set
        String prefix = (String) wordDict.keySet().toArray()[randomInt];
        List<String> stringResult = new ArrayList<>(Arrays.asList(prefix.split("\\s+")));

         while (true) {
            List<String> suffix = wordDict.get(prefix);
             // a null check in the suffix is added for corrupted files that are uploaded.
             // is corrupted, the system will fill in 'null' to match outputted values
             if (suffix != null) {
                 // If there is only one suffix, add that to the output string and return the value
                 // else randomly choose one of the suffix and add it to the output string
                 if (suffix.size() == 1) {
                     // If suffix is empty return the string builder value
                     if (Objects.equals(suffix.get(0), "")) {
                         return stringResult.stream().reduce("", (a, b) -> a + " " + b);
                     }
                     stringResult.add(suffix.get(0));
                 } else {
                     randomInt = random.nextInt(suffix.size());
                     stringResult.add(suffix.get(randomInt));
                 }
             } else {
                 int leftOver = outputSize - stringResult.size();
                 while (leftOver != 0) {
                     stringResult.add("null");
                     leftOver--;
                 }
                 return stringResult.stream().limit(outputSize).reduce("", (a, b) -> a + " " + b);
             }
            // If we have reached output size then return the result, but limit to the phrase range
            if (stringResult.size() >= outputSize) {
                return stringResult.stream().limit(outputSize).reduce("", (a, b) -> a + " " + b);
            }
             // go to the next value in the map and combine the suffix value in it
             // n is used to skip the repeating val and add the suffix
            n++;
            prefix = stringResult.stream().skip(n).limit(phraseRangeSize).reduce("", (a, b) -> a + " " + b).trim();
        }
    }

    /**
     * This method basically creates a map of key value
     * Where the Key is the Prefix and
     * The value is the Suffix.
     *
     * @param phraseRangeSize pool size range
     * @param words words taken from file
     * @return
     */
    private Map<String, List<String>> generateHashMap(int phraseRangeSize, String[] words) {
        // Dictionary that contains all possible values for each key word
        Map<String, List<String>> wordDict = new HashMap<>();

        for (int i = 0; i < (words.length - phraseRangeSize); i++) {
            StringBuilder sBuilder = new StringBuilder(words[i]);
            for (int j = i + 1; j < i + phraseRangeSize; j++) {
                sBuilder.append(' ').append(words[j]);
            }
            String value = "";
            if ((i + phraseRangeSize) < words.length-1) {
                value = words[i + phraseRangeSize];
            }
            // If dictionary does not contain the  sBuilder value
            // create a list and have that value added to the list
            // else add the value to the existing list for the corresponding dictionary value
            if (!wordDict.containsKey(sBuilder.toString())) {
                ArrayList<String> list = new ArrayList<>();
                list.add(value);
                wordDict.put(sBuilder.toString(), list);
            } else {
                wordDict.get(sBuilder.toString()).add(value);
            }
        }
        return wordDict;
    }
}
