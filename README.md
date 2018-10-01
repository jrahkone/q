# Q - simple query template for java

Example query (all values in json will be replaced with result values):

```json
{
	"num":10,
	"isOk":false,
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

Target object (bean) should have corresponding getters for data:

```java
public class MyBean {
	public int getNum() {return 777;}
	public boolean getIsOk() {return true;}
	class User {
		public String getFirstName() {return "fname1";}
		public String getLastName() {return "lname1";}
		public String getEmail() {return "email1";}
	}
	public User getUser() { return new User();}
	class Doc {
		int id;
		Doc(int id){this.id=id;}
		public String getTitle() {return "title"+id;}
	}
	
	public List<Doc> getDocs() {
		List<Doc> res = new ArrayList<Doc>();
		res.add(new Doc(1));
		res.add(new Doc(2));
		res.add(new Doc(3));
		return res;
	}
}
```

You can evaluate the query by:

```java
Q q = Q.parse(jsonString);  // or you can parse file: Q.parseFile(filename);
q.eval(new MyBean());       // Target object bean should have all getters for data to be queried.
String result = q.toJson(); // get result as json. Note: query object q is modified and contains the result.
```

Currently target object must contain all corresponding getters for all properties or exception is thrown. 
(TODO: more convinient would be to get some default values or error indicators in result and optional error log).




 

