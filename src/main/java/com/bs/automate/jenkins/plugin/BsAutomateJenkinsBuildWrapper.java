package com.bs.automate.jenkins.plugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.tasks.BuildWrapper;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class BsAutomateJenkinsBuildWrapper extends BuildWrapper implements
    Serializable {

  private static final long serialVersionUID = 5329868688072326491L;
  
  private static final Logger logger = Logger.getLogger(BsAutomateJenkinsBuildWrapper.class.getName());

  public static final String ENV_SELENIUM_HOST = "SELENIUM_HOST";
  public static final String ENV_SELENIUM_PORT = "SELENIUM_PORT";
  public static final String ENV_SELENIUM_BROWSER = "SELENIUM_BROWSER";
  public static final String ENV_SELENIUM_BROWSER_URL = "SELENIUM_BROWSER_URL";
  public static final String ENV_SELENIUM_BROWSER_VERSION = "SELENIUM_VERSION";
  public static final String ENV_SELENIUM_PLATFORM = "SELENIUM_PLATFORM";
  public static final String ENV_BS_USERNAME = "BS_USERNAME";
  public static final String ENV_BS_AUTHKEY = "BS_AUTHKEY";
  public static final String ENV_BS_AUTOMATE_PROJECT = "BS_AUTOMATE_PROJECT";
  public static final String ENV_BS_AUTOMATE_BUILD = "BS_AUTOMATE_BUILD";
  
  public static final String BS_AUTOMATE_HUB = "hub.browserstack.com";
  public static final String BS_AUTOMATE_HUB_PORT = "80";

  private String platform;
  private String browser;
  private String browserVersion;
  private String url;

  @DataBoundConstructor
  public BsAutomateJenkinsBuildWrapper(String platform, String browser,
      String browserVersion, String url) {
    this.platform = platform;
    this.browser = browser;
    this.browserVersion = browserVersion;
    this.url = url;
    
    logger.log(Level.INFO, "Project configured with platform=" + platform +
        " browser=" + browser + " browser version=" + browserVersion +
        " starting URL=" + url);
  }

  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl)Jenkins.getInstance().getDescriptor(getClass());
  }

  @Override
  public Environment setUp(final AbstractBuild build, Launcher launcher,
      BuildListener listener) {
    return new Environment() {
      
      @Override
      public void buildEnvVars(Map<String,String> env) {
        logger.log(Level.INFO, "Environment.buildEnvVars called");
        buildEnvVarsForSelenium(env);
        buildEnvVarsForAutomate(env);
      }

      private void buildEnvVarsForSelenium(Map<String, String> env) {
        env.put(ENV_SELENIUM_PLATFORM, platform);
        env.put(ENV_SELENIUM_BROWSER, browser);
        env.put(ENV_SELENIUM_BROWSER_VERSION, browserVersion);
        
        if(!url.isEmpty()) {
          env.put(ENV_SELENIUM_BROWSER_URL, url);
        }
        
        env.put(ENV_SELENIUM_HOST, BS_AUTOMATE_HUB);
        env.put(ENV_SELENIUM_PORT, BS_AUTOMATE_HUB_PORT);
      }

      private void buildEnvVarsForAutomate(Map<String, String> env) {
        BsAutomateJenkinsPlugin plugin = BsAutomateJenkinsPlugin.getInstance();
        
        env.put(ENV_BS_USERNAME, plugin.getUserName());
        env.put(ENV_BS_AUTHKEY, plugin.getAuthKey());
        
        final String projectName = build.getProject().getName();
        final String buildName = build.toString() + " " + build.getId();
        
        env.put(ENV_BS_AUTOMATE_PROJECT, projectName);
        env.put(ENV_BS_AUTOMATE_BUILD, buildName);
      }
      
      @Override
      public boolean tearDown(AbstractBuild build, BuildListener listener) {
        logger.log(Level.INFO, "Environment.tearDown called");
        boolean result = build.getResult() == null || build.getResult().equals(Result.SUCCESS);
        logger.log(Level.INFO, "The build " + (result?"succeeded":"failed"));
        return result;
      }
    };
  }
  
  @Extension
  public static final class DescriptorImpl extends Descriptor<BuildWrapper> {

    @Override
    public String getDisplayName() {
      return "BrowserStack Automate Support";
    }
    
    @Override
    public BuildWrapper newInstance(StaplerRequest req, JSONObject formData) throws FormException {
      return super.newInstance(req, formData);
    }

  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getBrowser() {
    return browser;
  }

  public void setBrowser(String browser) {
    this.browser = browser;
  }

  public String getBrowserVersion() {
    return browserVersion;
  }

  public void setBrowserVersion(String browserVersion) {
    this.browserVersion = browserVersion;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
