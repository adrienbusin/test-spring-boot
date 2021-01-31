Test spring boot with kotlin + rxJava + retrofit 

This backend is easy to use for android developer with commons libraries as Retrofit, Rx Java or kotlin.

How to run :
Open Intellij

run ./gradlew bootRun 

run this curl to call your exposed API 

curl --location --request GET 'http://localhost:8080/repos' \
--header 'Authorization: test' \
--header 'x-api-key: test'



