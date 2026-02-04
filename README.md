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

tests à faire sp11bis:
🔧 Tests à réaliser
Sans connexion :

/public/info → OK (200)

/secure/profile → 401

/secure/admin-data → 401

Connecté en tant que "user" :

/secure/profile → OK

/secure/user-data → OK

/secure/admin-data → 403

Connecté en tant que "admin" :

/secure/admin-data → OK

/secure/moderate → OK

/secure/manage → 403 (manque le rôle "manager")

Déconnexion :

/logout → OK

Retourne 401 pour les routes protégées