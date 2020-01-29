Design and implement a RESTful API (including data model and the backing implementation) for
money transfers between accounts.

### Explicit requirements:

1. You can use Java or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like (except Spring), but don't forget about
requirement #2 and keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require a
pre-installed container/server).
7. Demonstrate with tests that the API works as expected.

### Implicit requirements:

1. The code produced by you is expected to be of high quality.
2. There are no detailed requirements, use common sense.

### How to run

1) Unzip test-revolut.zip.
2) Run either test-revolut.bat or test-revolut (bat for Windows, sh for Linux)

### Running unit tests

To run unit tests you should install sbt. Command for unit tests is : sbt test

### Lombok

If you're going to start this application from intellij idea, you should install Lombok plugin and enable AnnotationProcessor at Settings.

### Technology stack

Java 8\
Sbt\
Play framework\
Logback\
Lombok\
Mockito\
JUnit 5

### Endpoints

| HTTP METHOD | Path | Description |
| -----------| ------ | ------ |
| GET | /accounts/ | get all accounts | 
| GET | /accounts/{id} | get account by id | 
| DELETE | /accounts/{id} | soft delete account by id. It marks record as deleted but keep in the database. We don't need to break relationships between transactions and accounts | 
| GET | /transactions/ | get all transactions | 
| GET | /transactions/{id} | get transaction by id | 
| POST | /transactions/ | create transactions | 

### Create account - POST
```sh
{  
  "name":"test",
  "email":"test@gmail.com",
  "balance": 50
} 
```

### Create transaction - POST
```sh
{ 
  "sender": {"id": 1},
  "receiver":{"id":2},
  "amount":15
}
```
