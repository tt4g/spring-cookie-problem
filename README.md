spring-projects/spring-framework#26541 problem reproduction project.

```bash
$ ./gradlew bootRun
$ ab -c 100 -n 10000 http://127.0.0.1:8080/assets/index.html
```
