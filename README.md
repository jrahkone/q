# Q - simple java query template

Example query:

```json
{
	"num":777,
	"isOk":true,
	"user":{
		"firstName":"",
		"lastName":"",
		"email":""
	},
	"docs":[ 
		{"title":""}
	]
}
```

You can evaluate the query by:

```java
Q q = Q.parse(jsonString); // or you can parse file: Q.parseFile(filename);
q.eval(o); // Object o is the target where to query all fields in given query object.
String result = q.toJson(); // get result as json. Note: query object q is modified and contains the result.
```

Currently target object must contain all corresponding getters for all properties or exception is thrown. 
(TODO: more convinient would be to get some default values or error indicators in result and optional error log).




 

