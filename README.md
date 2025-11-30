cd framework
mvn clean install
copier target/spring-like-framework-1.0.0.jar dans test/lib
cd ../test
mvn clean package
copier .war dans webapps de tomcat
lancer tomcat
localhost:8085