package com.bs.api;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jvnet.hudson.test.HudsonTestCase;

public class BsApiAuthenticationTest extends HudsonTestCase {
  
  private String userName;
  private String authKey;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    userName = System.getenv("BS_USERNAME");
    authKey = System.getenv("BS_AUTHKEY");
  }
  
  public void testAuthenticate() throws MalformedURLException, IOException {
    BsApiAuthentication bsApiAuthentication = new BsApiAuthentication(userName, authKey);
    
    assert(bsApiAuthentication.authenticate());
    
    bsApiAuthentication = new BsApiAuthentication("SomeUser", "SomeGibberishPassword");
    
    assert(!bsApiAuthentication.authenticate());
  }

}
