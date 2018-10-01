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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.captam.Q.Buf;

public class QTest {

	@Test
	public void test() {
		Q q = new Q();
		assertEquals("{}",""+q);
		q.set("a","b");
		assertEquals("{\"a\":\"b\"}",""+q);
		q.set("a",100);
		assertEquals("{\"a\":100}",""+q);
		q.set("a",100.01);
		assertEquals("{\"a\":100.01}",""+q);
		q.set("a",true);
		assertEquals("{\"a\":true}",""+q);
		q.set("a",false);
		assertEquals("{\"a\":false}",""+q);
		q.set("a",null);
		assertEquals("{\"a\":null}",""+q);
	}
	
	@Test
	public void testList() {
		Q q = new Q();
		assertEquals("{}",""+q);
		List lst = new ArrayList();
		lst.add(1); lst.add(2);
		q.set("a",lst);
		assertEquals("{\"a\":[1,2]}",""+q);
		lst.add(new Q());
		assertEquals("{\"a\":[1,2,{}]}",""+q);		
	}

	@Test
	public void testBuf() {
		Buf b = new Buf(" foo");
		b.trim();
		assertTrue(b.is(0,'f'));
		b.trim();
		assertTrue(b.is(1,'o'));
		assertTrue(b.is(2,'o'));
		b.expect('f');
		b.trim();
		b.expect('o');
		b.expect('o');
		
		b = new Buf("{ \"foo\":10 }");
		b.trim(); b.expect('{'); b.trim(); b.expect('"');
		assertEquals("foo",b.until('"'));
		b.expect(':');
		
	}
	
	@Test
	public void testParse() {
		String json = "{\"foo\":\"bar\",\"num\":10,\"bool\":false,\"o\":{\"sub\":1}}";
		Q q = Q.parse(json);
		assertEquals(json,q.toJson());
	}

	@Test
	public void testParseList() {
		String json = "{\"items\":[1,2,\"foo\"]}";
		Q q = Q.parse(json);
		assertEquals(json,q.toJson());
	}

	@Test
	public void testParseNestedLists() {
		String json = "{\"items\":[\"foo\",[1,2],[[1]]]}";
		Q q = Q.parse(json);
		assertEquals(json,q.toJson());
		// with white spaces 
		String json2 = "{ \"items\": [  \"foo\"  , [1,  2],[[1  ]]  ]  }";
		q = Q.parse(json2);
		assertEquals(json,q.toJson());		
	}

	@Test
	public void parseFile() throws Exception {
		Q q = Q.parseFile("syntax.json");
		Q.print(q.toJson());
	}
	
	@Test
	public void eval() throws Exception {
		String json = "{\"field1\":\"\",\"num1\":1000,\"bool1\":false}"; 
		Q q = Q.parse(json);
		q.eval(new TestService());
		assertEquals("{\"field1\":\"value1\",\"num1\":10,\"bool1\":true}",q.toJson());
		Q.print("evaluated:"+q.toJson());
	}
	
	@Test
	public void evalSubQ() throws Exception {
		String json = "{\"field1\":\"\",\"num1\":1000,\"bool1\":false,\"user\":{\"firstName\":\"\",\"lastName\":\"\",\"email\":\"\"}}"; 
		Q q = Q.parse(json);
		q.eval(new TestService());
		assertEquals("{\"field1\":\"value1\",\"num1\":10,\"bool1\":true,\"user\":{\"firstName\":\"fname1\",\"lastName\":\"lname1\",\"email\":\"email1\"}}",q.toJson());
		Q.print("evaluated:"+q.toJson());
	}
	
	@Test
	public void evalLists() throws Exception {
		Q q = Q.parseFile("test.json");
		q.eval(new TestService());
		q.print();
	}
	
}
