
Execute `sbt run` and in a new terminal window:

```
  $ curl http://127.0.0.1:8081/users
  []
  $ curl -H "Content-Type: application/json" -X POST -d '{"name":"John"}' http://127.0.0.1:8081/users
  OK
  $ curl http://127.0.0.1:8081/users
  [{"name":"John"}]
  $ curl -H "Content-Type: application/json" -X POST -d '{"name":"John"}' http://127.0.0.1:8081/users
  The request could not be processed because of conflict in the request, such as an edit conflict.
  $ curl http://127.0.0.1:8081/users
  [{"name":"John"}]
  $ curl -H "Content-Type: application/json" -X POST -d '{"name":"Marc"}' http://127.0.0.1:8081/users
  OK
  $ curl http://127.0.0.1:8081/users
[{"name":"John"},{"name":"Marc"}]
```