Jenkins plug-in for running selenium tests using BrowserStack Automate

To compile
  $ mvn compile
  
To test
  $ mvn test
  
To install (create hpi file for Jenkins)
  $ mvn install
The file shall be saved as target/browserstack-automate.hpi

The unit tests run a basic BrowserStack authentication check which requires:
  1. The Internet (obviously)
  2. The variables BS_USERNAME and BS_AUTHKEY to be set in the environment. 
     See src/test/java/com/bs/api/BsApiAuthenticationTest.java
     and src/test/java/com/bs/automate/jenkins/plugin/BsAutomateJenkinsPluginTest.java