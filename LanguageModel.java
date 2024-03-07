import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;

    // The window length used in this model.
    int windowLength;

    // The random number generator used by this model.
    private Random randomGenerator;

    /**
     * Constructs a language model with the given window length and a given
     * seed value. Generating texts from this model multiple times with the
     * same seed value will produce the same random texts. Good for debugging.
     */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /**
     * Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production.
     */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
    public void train(String fileName) {
        StringBuilder word = new StringBuilder();
        In in = new In(fileName);
        char c;
        List probs;
        for (int i = 0; i < windowLength; i++) {
            word.append(in.readChar());
        }
        while (!in.isEmpty()) {
            c = in.readChar();
            if (CharDataMap.containsKey(word.toString()))
                probs = CharDataMap.get(word.toString());
            else
                probs = new List();
            CharDataMap.put(word.toString(), probs);
            probs.update(c);
            word.append(c);
            word.deleteCharAt(0);
        }
        for (List probs2 : this.CharDataMap.values())
            calculateProbabilities(probs2);
    }

    // Computes and sets the probabilities (p and cp fields) of all the
    // characters in the given list. */
    public void calculateProbabilities(List probs) {
        double sum = 0;
        for (int i = 0; i < probs.getSize(); i++) {
            sum += probs.get(i).count;
        }
        for (int i = 0; i < probs.getSize(); i++) {
            if (i == 0) {
                probs.get(i).p = probs.get(i).count / sum;
                probs.get(i).cp = probs.get(i).p;
            } else {
                probs.get(i).p = probs.get(i).count / sum;
                probs.get(i).cp = probs.get(i - 1).cp + probs.get(i).p;
            }
        }
    }

    // Returns a random character from the given probabilities list.
    public char getRandomChar(List probs) {
        // Your code goes here
        double randomnum = randomGenerator.nextDouble();
        for (int i = 0; i < probs.getSize(); i++) {
            if (randomnum < probs.get(i).cp) {
                return probs.get(i).chr;
            }
        }
        return ' ';
    }

    /**
     * Generates a random text, based on the probabilities that were learned during
     * training.
     * 
     * @param initialText     - text to start with. If initialText's last substring
     *                        of size numberOfLetters
     *                        doesn't appear as a key in Map, we generate no text
     *                        and return only the initial text.
     * @param numberOfLetters - the size of text to generate
     * @return the generated text
     */
    public String generate(String initialText, int textLength) {
        if (initialText.length() < windowLength) {
            return initialText;
        }
        char c;
        StringBuilder window = new StringBuilder(initialText);
        StringBuilder text = new StringBuilder(initialText);
        for (int i = 0; i < textLength; i++) {
            List list = CharDataMap.get(window.toString());
            if (list != null) {
                c = getRandomChar(list);
                text.append(c);
                window.append(c);
                window.deleteCharAt(0);
            } else
                return text.toString();
        }
        return text.toString();
    }

    /** Returns a string representing the map of this language model. */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String key : CharDataMap.keySet()) {
            List keyProbs = CharDataMap.get(key);
            str.append(key + " : " + keyProbs + "\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        // Your code goes here
    }
}
