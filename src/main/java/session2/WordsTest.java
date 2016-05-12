package session2;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by matt.thomas on 12/05/2016.
 */
public class WordsTest {

        public static void main(String[] args) throws Exception {

            InputStream in = StreamWords.class.getResourceAsStream("alice-in-wonderland.txt");


            try (LineNumberReader rdr = new LineNumberReader(new InputStreamReader(in))) {

                for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
                    for (String word : line.split(" ")) {


                        if (!word.isEmpty()) {


                        }

                    }

                }
            }
        }
    }
