cd ../framework
mvn clean install
copy target\spring-like-framework-1.0.0.jar ..\test\lib\
cd ../test
mvn clean package
copy target\testapp.war C:\xampp\tomcat\webapps

pour tester: 
-démarrer tomcat:
C:\xampp\tomcat\bin
-navigateur: localhost:8085