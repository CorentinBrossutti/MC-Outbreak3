package com.bp389.outbreak.translate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;


public final class Translator {
	private static HashMap<String, String> trs = new HashMap<>();
	/**
	 * Load the translate from an external file (classpath relative)
	 * @param externalFile File object - represents the translate to load
	 */
	public static void load(File externalFile){
        try {
            mLoad(Files.readAllLines(externalFile.toPath(), StandardCharsets.ISO_8859_1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	/**
	 * Load an internal classpath translation
	 * @param classpathAbsolute Absolute path to translate (ex: com/bp389/outbreak/translate/languages/english.txt)
	 */
	public static void load(String classpathAbsolute){
        try {
            mLoad(Files.readAllLines(Paths.get(Translator.class.getResource(classpathAbsolute).toURI())));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * Very heavy method due to the loop and split() methods, use only on startup if possible
	 * @param lines List of lines read from the file
	 */
	public static void mLoad(List<String> lines){
		trs.clear();
		String[] temp;
		for(String s : lines){
			if(s.startsWith(";") || s.isEmpty() || s.equalsIgnoreCase(" "))
				continue;
			temp = s.split("=", 2);
			trs.put(temp[0], temp[1]);
		}
	}
	/**
	 * Get a translate value, from pre-loaded language file
	 * @param key Key representing the translate value 
	 * @return The translated string
	 */
	public static String tr(String key){
		if(!trs.containsKey(key)){
			System.err.println("Translator.tr(str) : unable to reach key -> " + key);
			return "TRANSLATION ERROR";
		}
		return trs.get(key);
	}
}
