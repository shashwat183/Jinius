build:
	mvn package

run:
	java -cp target/httpserver-1.0-SNAPSHOT-jar-with-dependencies.jar httpserver.App

clean:
	rm -rf ./target/*

test:
	mvn test

dependencies:
	mvn dependency:resolve

deploy:
	mvn deploy:deploy-file -Durl=file:///Users/shashwatpragya/Code/sideprojects/java/httpserver/repo -Dfile=target/httpserver-$(version)-jar-with-dependencies.jar -DgroupId=httpserver -DartifactId=httpserver -Dpackaging=jar -Dversion=$(version)
