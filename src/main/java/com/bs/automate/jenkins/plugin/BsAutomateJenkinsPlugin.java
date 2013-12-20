package com.bs.automate.jenkins.plugin;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.util.FormValidation;
import hudson.util.Secret;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.bs.api.BsApiAuthentication;

public class BsAutomateJenkinsPlugin extends Plugin implements Describable<BsAutomateJenkinsPlugin> {
  
  private static final Logger logger = Logger.getLogger(BsAutomateJenkinsPlugin.class.getName());
  
  private String userName;
  private Secret authKey;
  
  @Override
  public void configure(StaplerRequest req, JSONObject formData) throws IOException {
    setUserName(formData.getString("userName"));
    setAuthKey(formData.getString("authKey"));
    
    save();
    logger.log(Level.INFO, "Plugin configured!");
  }
  
  @Override
  public void start() throws IOException {
    load();
  }

  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl)Jenkins.getInstance().getDescriptor(getClass());
  }
  
  @Extension
  public static final class DescriptorImpl extends Descriptor<BsAutomateJenkinsPlugin> {
    
    private static final Logger myLogger = Logger.getLogger(DescriptorImpl.class.getName());

    @Override
    public String getDisplayName() {
      return "BrowserStack Automate";
    }
    
    public FormValidation doValidate(@QueryParameter String userName, @QueryParameter String authKey) {
      
      myLogger.log(Level.INFO, "doValidate called");
      
      if(userName.isEmpty()) {
        myLogger.log(Level.WARNING, "Username is empty");
        return FormValidation.error("Please give a username");
      }
      if(authKey.isEmpty()) {
        myLogger.log(Level.WARNING, "Auth key is empty");
        return FormValidation.error("Please specify the authorisation key");
      }
      if(userName.lastIndexOf(' ') != -1) {
        myLogger.log(Level.WARNING, "The username contains spaces");
        return FormValidation.error("Username cannot contain spaces");
      }
      
      try {
        BsApiAuthentication bsAuthChecker = new BsApiAuthentication(userName, authKey);
        
        if(bsAuthChecker.authenticate()) {
          myLogger.log(Level.INFO, "Validation successful");
          return FormValidation.ok("Success!");
        }
      } catch(Exception e) {
        myLogger.log(Level.WARNING, "Received " + e.getClass().getName() + ": " + e.getMessage());
        return FormValidation.error("Error: Cannot validate now");
      }
      
      myLogger.log(Level.WARNING, "Validation failed.");
      return FormValidation.error("Validation failed. Please check username and access key again.");
    }
    
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
      return aClass.isAssignableFrom(FreeStyleProject.class);
    } 
  }
  
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getAuthKey() {
    return Secret.toString(authKey);
  }

  public void setAuthKey(String authKey) {
    this.authKey = Secret.fromString(authKey);
  }
  
  public static BsAutomateJenkinsPlugin getInstance() {
    return Jenkins.getInstance().getPlugin(BsAutomateJenkinsPlugin.class);
  }
  
}
