1) Starting a http server to host the yaml file common to all processes.
Steps:
 - Install python
 - Go to the folder which contains the yaml in the command prompt and type the following command.
   python -m http.server

2) Getting the code Running

Eclipse:
   Import the project shim into eclipse or create a new eclipse project and include the src and resources folder provided in the solution. Make sure the library jars 
included in the resources folder are on the build path. Edit the yaml according to requirement and run the applications using the lauch configs included in the 
solution. Note the lauch configs program arguements will need to be modified to include the url for the http server being used.

3) Executing the cases

Once the application is started the console on eclipse will display an interactive options screen which will allow the user the options to test the various cases.

**Note : The code assumes that the yaml is formatted correctly and is not missing essential information.