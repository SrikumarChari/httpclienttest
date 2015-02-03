/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.httpclienttest;

import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Srikumar
 */
public class HttpClientTestMain {

    public static void main(String[] args) {
        //authenticate with the server
        URL url;
        try {
            url = new URL("http://198.11.209.34:5000/v2.0/tokens");
        } catch (MalformedURLException ex) {
            System.out.printf("Invalid Endpoint - not a valid URL {}", "http://198.11.209.34:5000/v2.0");
            return;
        }
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        try {
            HttpPost httpPost = new HttpPost(url.toString());
            httpPost.setHeader("Content-Type", "application/json");
            StringEntity strEntity = new StringEntity("{\"auth\":{\"tenantName\":\"Gemini-network-prj\",\"passwordCredentials\":{\"username\":\"sri\",\"password\":\"srikumar12\"}}}");
            httpPost.setEntity(strEntity);
            //System.out.println("Executing request " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                //get the response status code 
                String respStatus = response.getStatusLine().getReasonPhrase();

                //get the response body
                int bytes = response.getEntity().getContent().available();
                InputStream body = response.getEntity().getContent();
                BufferedReader in = new BufferedReader(new InputStreamReader(body));
                String line;
                StringBuffer sbJSON = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    sbJSON.append(line);
                }
                String json = sbJSON.toString();
                EntityUtils.consume(response.getEntity());

                GsonBuilder gsonBuilder = new GsonBuilder();
                Object parsedJson = gsonBuilder.create().fromJson(json, Object.class);
                System.out.printf("Parsed json is of type %s\n\n", parsedJson.getClass().toString());
                if (parsedJson instanceof com.google.gson.internal.LinkedTreeMap) {
                    com.google.gson.internal.LinkedTreeMap treeJson = (com.google.gson.internal.LinkedTreeMap) parsedJson;
                    if (treeJson.containsKey("id")) {
                        String s = (String) treeJson.getOrDefault("id", "no key provided");
                        System.out.printf("\n\ntree contained id %s\n", s);
                    } else {
                        System.out.printf("\n\ntree does not contain key id\n");
                    }
                }
            } catch (IOException ex) {
                System.out.print(ex);
            } finally {
                response.close();
            }
        } catch (IOException ex) {
            System.out.print(ex);
        } finally {
            //lots of exceptions, just the connection and exit
            try {
                httpclient.close();
            } catch (IOException | NoSuchMethodError ex) {
                System.out.print(ex);
                return;
            }
        }
    }
}
