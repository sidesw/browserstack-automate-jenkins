package com.bs.automate.jenkins.plugin;

import hudson.model.Environment;
import hudson.model.FreeStyleBuild;
import hudson.model.AbstractBuild;
import hudson.model.FreeStyleProject;

import java.util.HashMap;
import java.util.Map;

import org.jvnet.hudson.test.HudsonTestCase;

public class BsAutomateJenkinsBuildWrapperTest extends HudsonTestCase {
  
  private final String userName = "SomeUser";
  private final String authKey = "SomeGibberishPassword";

  private final String platform = "MAC";
  private final String browser = "chrome";
  private final String browserVersion = "27.0";
  private final String url = "www.browserstack.com";
  
  private final String projectName = "MyTestProject";
  private final String buildName = "MyTestBuild";
  AbstractBuild<?, ?> build;

  private Environment environment;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    BsAutomateJenkinsPlugin plugin = BsAutomateJenkinsPlugin.getInstance();
    plugin.setUserName(userName);
    plugin.setAuthKey(authKey);
    
    BsAutomateJenkinsBuildWrapper buildWrapper = 
        new BsAutomateJenkinsBuildWrapper(platform, browser, browserVersion, url);
    
    FreeStyleProject project = createFreeStyleProject();
    project.renameTo(projectName);
    build = new FreeStyleBuild(project);
    build.setDisplayName(buildName);
    
    environment = buildWrapper.setUp(build, null, null);
  }
  
  public void testEnvironmentBuildEnvVars() {
    Map<String, String> env = new HashMap<String, String>();
    
    environment.buildEnvVars(env);
    
    // now test the values in the env against the expected values here
    assertEquals(userName, env.get(BsAutomateJenkinsBuildWrapper.ENV_BS_USERNAME));
    assertEquals(authKey, env.get(BsAutomateJenkinsBuildWrapper.ENV_BS_AUTHKEY));
    
    assertEquals(platform, env.get(BsAutomateJenkinsBuildWrapper.ENV_SELENIUM_PLATFORM));
    assertEquals(browser, env.get(BsAutomateJenkinsBuildWrapper.ENV_SELENIUM_BROWSER));
    assertEquals(browserVersion, env.get(BsAutomateJenkinsBuildWrapper.ENV_SELENIUM_BROWSER_VERSION));
    assertEquals(url, env.get(BsAutomateJenkinsBuildWrapper.ENV_SELENIUM_BROWSER_URL));
    
    assertEquals(projectName, env.get(BsAutomateJenkinsBuildWrapper.ENV_BS_AUTOMATE_PROJECT));
    
    final String fullBuildName = projectName + " " + buildName;
    assert(env.get(BsAutomateJenkinsBuildWrapper.ENV_BS_AUTOMATE_BUILD).startsWith(fullBuildName));
    
    // now check selenium host and port
    assertEquals(BsAutomateJenkinsBuildWrapper.BS_AUTOMATE_HUB,
        env.get(BsAutomateJenkinsBuildWrapper.ENV_SELENIUM_HOST));
    assertEquals(BsAutomateJenkinsBuildWrapper.BS_AUTOMATE_HUB_PORT,
        env.get(BsAutomateJenkinsBuildWrapper.ENV_SELENIUM_PORT));
    
  }

}
