import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import java.io.File;
import java.io.FileWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

public class SCRAPE {

    public static String URL_TMDB_SEARCH =  "https://api.themoviedb.org/3/search/movie?api_key=bcdeaa955ac0d6521b972fb22cd42498&query=";
    public static String URL_TMDB_ID =  "https://api.themoviedb.org/3/movie/";

    public static String IMDB_BASE_URL = "https://www.imdb.com/title/";

    public static void main(String[] args){

        Scanner in = new Scanner(System.in);
        String movie_name = null;

        System.out.println("Please Enter Movie Name:");
        movie_name = in.nextLine();

        getDATA(movie_name);

    }

    public static String getDATA(String movieName){

        movieName = movieName.replace(" ","%20");
        String FINAL_URL = IMDB_BASE_URL+getIMDBID(movieName)+"/reviews?ref_=tt_ql_3";
        //System.out.println(FINAL_URL);

        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
        HtmlPage page;

        ArrayList<Double> rating = new ArrayList<>();
        List<HtmlDivision> review = new ArrayList<>();
        try {
            page = client.getPage(FINAL_URL);

            /** Getting Reviews Using XPATH**/
            review =  page.getByXPath("//div[@class='text show-more__control']");

//            /** Getting Ratings Using XPATH**/
//            List<HtmlSpan> rawRating = (List<HtmlSpan>) page.getByXPath("//div[@class = 'ipl-ratings-bar']//span/span\n");
//
//            /** Processing the Raw Rating to rating**/
//            for(int i = 0; i< rawRating.size(); i++){
//                if(i%2 == 0)
//                    rating.add(Double.valueOf(rawRating.get(i).asText()));
//            }
//            for(int i =0; i< 1; i++){
////                System.out.println("\nRating:"+rating.get(i));
//                System.out.println("Review:"+(review.get(i).asText()));
//            }

//            File file = new File("review.txt");
//            FileWriter fw = new FileWriter(file);
//            fw.write(review.get(0).asText());
//            fw.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return review.get(0).asText();
        }
    }



    public static String getIMDBID(String searchQuery){

        String id = null;
        try {

            URL search_url = new URL(URL_TMDB_SEARCH+searchQuery);
            //System.out.println(search_url);
            HttpURLConnection urlConnection = (HttpURLConnection) search_url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();

            String json = org.apache.commons.io.IOUtils.toString(inputStream);

            String tmdb_id =JSONCustomParser(json);

            id = IMDBIDGet(tmdb_id);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return id.trim() ;
        }


    }

    private static String JSONCustomParser(String json) throws JSONException{

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException ex) {
            Logger.getLogger(SCRAPE.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONArray results = jsonObject.getJSONArray("results");

        JSONObject movie = results.getJSONObject(0);

        String id = String.valueOf(movie.getInt("id"));

        return id;
    }

    private static String  IMDBIDGet(String id){

        String IMDB_ID = null;
        URL search_url = null;

        try {

            search_url = new URL(URL_TMDB_ID+id+"?api_key=bcdeaa955ac0d6521b972fb22cd42498");
            HttpURLConnection urlConnection = (HttpURLConnection) search_url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();

            String json = org.apache.commons.io.IOUtils.toString(inputStream);

            JSONObject jsonObject = new JSONObject(json);

            IMDB_ID = jsonObject.getString("imdb_id");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return IMDB_ID;
        }
    }
}
