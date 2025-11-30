cd ../framework
mvn clean install
copy target\spring-like-framework-1.0.0.jar ..\test\lib\
cd ../test
mvn clean package
copy target\testapp.war C:\xampp\tomcat\webapps
lancer tomcat
localhost:8085