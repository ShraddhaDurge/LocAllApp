package com.localapp.Service;

import com.google.gson.Gson;
import com.localapp.Model.Product;
import com.localapp.PayloadResponse.RecommendationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class Recommendation {

    private static final String GET_URL = "http://127.0.0.1:5000/recommendation";

    @Autowired
    private Gson gson;

    @Autowired
    ProductService productService;

    public RecommendationResponse getRecommendation(int userid) {
        URL obj;
        try {
            obj = new URL(GET_URL+"?userid="+userid);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; utf-8");

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                Reader reader = new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8);
                return gson.fromJson(reader, RecommendationResponse.class);
            } else {
                //print internal server error
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new RecommendationResponse();
    }

    public List<Product> getRecommendedProducts(RecommendationResponse recommendationResponse) {
        List<Product> reProducts = new ArrayList<>();
        List<Integer> productIds = recommendationResponse.getProductList();
        System.out.println(productIds);
        for(int id : productIds) {
            Product product = productService.getById(id);
            if(product!=null) {
                reProducts.add(product);
                System.out.println(product.getProductName());
            }
        }
        return reProducts;
    }

}
