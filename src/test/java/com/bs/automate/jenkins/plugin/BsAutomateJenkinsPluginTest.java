package com.bs.automate.jenkins.plugin;

import hudson.util.FormValidation.Kind;

import org.jvnet.hudson.test.HudsonTestCase;

import com.bs.automate.jenkins.plugin.BsAutomateJenkinsPlugin.DescriptorImpl;

public class BsAutomateJenkinsPluginTest extends HudsonTestCase {
  
  private String userName;
  private String authKey;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    userName = System.getenv("BS_USERNAME");
    authKey = System.getenv("BS_AUTHKEY");
  }
  
  public void testValidate() {
    DescriptorImpl descriptor = BsAutomateJenkinsPlugin.getInstance().getDescriptor();
    assertEquals(Kind.OK, descriptor.doValidate(userName, authKey).kind);
  }
  
  public void testGetAndSet() {
    BsAutomateJenkinsPlugin plugin = BsAutomateJenkinsPlugin.getInstance();
    plugin.setUserName("SomeUser");
    plugin.setAuthKey("SomeGibberishPassword");
    
    assertEquals("SomeUser", plugin.getUserName());
    assertEquals("SomeGibberishPassword", plugin.getAuthKey());
  }

}
