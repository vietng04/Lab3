package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final ArrayList<String> translators = new ArrayList<String>();

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                translators.add(jsonObject.toString());
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        List<String> languages = new ArrayList<>();
        for (String translator : translators) {
            JSONObject jsonObject = new JSONObject(translator);
            if (jsonObject.getString("alpha3").equals(country)) {
                for (String key : jsonObject.keySet()) {
                    if (!"alpha3".equals(key) && !"alpha2".equals(key) && !"id".equals(key)) {
                        languages.add(key);
                    }
                }
            }
        }
        return languages;
    }

    @Override
    public List<String> getCountries() {
        List<String> countries = new ArrayList<>();
        for (String translator : translators) {
            countries.add("alpha3");
        }
        return countries;
    }

    @Override
    public String translate(String country, String language) {
        for (String translator : translators) {
            JSONObject jsonObject = new JSONObject(translator);
            if (jsonObject.getString("alpha3").equals(country)) {
                return jsonObject.getString(language);
            }
        }
        return null;
    }
}
