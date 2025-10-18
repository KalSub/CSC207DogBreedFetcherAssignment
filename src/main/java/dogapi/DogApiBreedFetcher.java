package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException{
        String url = "https://dog.ceo/api/breed/" + breed + "/list";


        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new BreedNotFoundException("No breed found with name " + breed);
            }

            assert response.body() != null;
            String content = response.body().string();
            JSONObject jsonObject = new JSONObject(content);

            if (jsonObject.getString("status").equals("error")) {
                throw new BreedNotFoundException("No breed found with name " + breed);
            }

            JSONArray subBreeds = jsonObject.getJSONArray("message");
            List<String> subBreedList = new ArrayList<>();
            for (int i = 0; i < subBreeds.length(); i++) {
                subBreedList.add(subBreeds.getString(i));
            }
            return subBreedList;
        }
        catch (IOException e) {
            throw new BreedNotFoundException("Could not fetch breed list");
        }
        catch (Exception e) {
            throw new BreedNotFoundException("Error occurred while fetching breed list");
        }
    }
}