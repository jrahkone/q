package com.captam;

/*
 *  Copyright 2018 Jukka-Pekka Rahkonen
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at root directory of this project.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
