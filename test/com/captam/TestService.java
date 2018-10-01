package com.captam;

import java.util.ArrayList;
import java.util.List;

public class TestService {

	public Q query(Q q) throws Exception {
		return q.eval(this);
	}
	
	public String getField1() { return "value1";}
	public int getNum1() {return 10;}
	public boolean getBool1() {return true;}

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
		res.add(new Doc(4));
		res.add(new Doc(5));
		return res;
	}
	
}
