# Java Agent Example


# How to run
- Clone the sample [application]() and this repo. 
- Build both the repo using going into the path and execute the command `mvn clean install`
- Now you will get the jar files in the target. Copy the path of the `.jar` file in Example Application and Copy the
 path of the `-dependencies.jar` file in JavaAgent.
- First, run the application only with the Example Application using the command `$ java -jar <path of the packaged jar>`
 and observe the output. `Hi I am main.` will be printed in the console. 
- Then run the application attached with the java agent using the command `$ java -javaagent:<path of agent jar file> -jar <path of the packaged jar file you want to intercept>` and observe the output. `Logging using Agent` will be printed additionally in the console. This ensures that the java agent has been intercepted and added to the body of the main method.

