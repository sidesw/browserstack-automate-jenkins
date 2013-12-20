package com.bs.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

public class BsApiAuthentication {
  
  private static final Logger logger = Logger.getLogger(BsApiAuthentication.class.getName());
  
  private static final int BS_API_VERSION = 3;

  private String userName;
  private String authKey;

  public BsApiAuthentication(String userName, String authKey) {
    this.userName = userName;
    this.authKey = authKey;
  }

  public boolean authenticate() throws IOException, MalformedURLException {
    logger.log(Level.INFO, "authenticate called.");
    
    String authString = userName + ":" + authKey;
    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
    String authStringEnc = new String(authEncBytes);

    URL bsApiUrl = new URL("http://api.browserstack.com/" + BS_API_VERSION);
    
    logger.log(Level.INFO, "Attempting to perform basic HTTP authentication on " + bsApiUrl.toString());
    
    HttpURLConnection connection = (HttpURLConnection) bsApiUrl.openConnection();
    connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
    
    int responseCode = connection.getResponseCode();
    
    logger.log(Level.INFO, "Received response code "+ responseCode);

    return (responseCode == HttpURLConnection.HTTP_OK);
  }
}
