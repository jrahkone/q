package com.captam;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Q {
	List<Kv> props = new ArrayList<Kv>();
	char qm = '"';
	class Kv {
		String key;
		Object value;
		void toJson(StringBuilder sb) { sb.append(""+qm+key+qm+":"); valueToJson(sb,value);}
		void parse(Buf b) {
			b.trim(); b.expect(qm); key=b.until(qm);b.trim();b.expect(':');b.trim(); // key read, next read value:
			value=parseValue(b);
		}
	}
	public Object get(String key) {	Kv kv = prop(key); if (kv==null)return null; return kv.value;}
	public String str(String key) {	return (String)get(key);}
	public Q set(String key, Object v) {
		Kv kv = prop(key);
		if (kv==null) {kv=new Kv(); kv.key=key; if(props==null) props=new ArrayList<Kv>(); props.add(kv);}
		kv.value=v; return this;
	}
	private Kv addKv() {if(props==null){props=new ArrayList<Kv>();}Kv kv=new Kv();props.add(kv); return kv;}
	private Kv prop(String key) {
		if (props==null||key==null) return null;
		for (Kv kv : props) { if (key.equals(kv.key)) return kv;}
		return null;
	}
	
	public String toString() {return toJson();}
	public String toJson() { StringBuilder sb = new StringBuilder(); toJson(sb); return sb.toString();}
	void toJson(StringBuilder sb) {
		sb.append("{"); String sep="";
		if (props!=null) { for (Kv kv : props) {sb.append(sep);kv.toJson(sb);sep=",";}}
		sb.append("}");
	}
	void valueToJson(StringBuilder sb, Object o) {
		if (o==null) sb.append("null");
		else if (o instanceof String) sb.append(""+qm+o+qm);
		else if (o instanceof Q) ((Q)o).toJson(sb);
		else if (o instanceof List) { sb.append("["); String sep=""; for (Object o2 : ((List)o)) {sb.append(sep); valueToJson(sb,o2); sep=",";} sb.append("]");}
		else sb.append(""+o);
	}
	Object parseValue(Buf b) {
		Object value=null; b.trim();
		if (b.is(qm)){ b.next(); value=b.until(qm);}
		else if (b.is('{')) { value=new Q();((Q)value).parse(b);}
		else if (b.is('[')) {
			b.next(); value = new ArrayList();
			while (true) {
				b.trim(); if (b.is(']')) break;
				((List)value).add(parseValue(b)); b.trim(); if (b.is(']')) break;
				b.trim(); b.expect(',');
			}
			b.expect(']');
		}
		else if (b.match("null")) {value=null;}
		else if (b.match("true")) {value=true;}
		else if (b.match("false")) {value=false;}
		else {value=b.readNum(); if (value==null) fail("expected number");} 
		return value;
	}
	
	void parse(Buf b){
		b.trim(); b.expect('{');
		while (true) {
			b.trim();
			if (b.is(qm)) addKv().parse(b);
			b.trim(); if (b.is('}')) break;
			b.expect(',');
		}
		b.expect('}');
	}
	
	static Q parse(String s) { Q q=new Q(); Buf b = new Buf(s); q.parse(b); return q;}
	static Q parseFile(String fname) throws Exception {
		 byte[] encoded = Files.readAllBytes(Paths.get(fname));
		 String s = new String(encoded, Charset.forName("UTF-8"));
		 return parse(s);
	}
	
	static class Buf {
		int idx = 0; int len;
		String s;
		Buf(String s){this.s=s;len=s.length();}
 		void trim() { while(idx<len && Character.isWhitespace(s.charAt(idx))) { idx++;}}
 		void expect(char ch) {if(ch!=s.charAt(idx)) fail("expected:"+ch+" but was:"+s.charAt(idx));idx++;}
 		boolean is(char ch) {return is(0,ch);}
 		boolean is(int i, char ch) {return s.charAt(idx+i)==ch;}
 		char peek(int i) {return s.charAt(idx+i);}
 		void next(int i) {idx+=i;}
 		void next() {next(1);}
 		String until(char ch) {
 			String res="";
 			while (true) {
 				if (idx>=len) break;
 				char c = s.charAt(idx++);
 				if (ch==c) break;
 				res+=""+c;
 			}
 			return res;
 		}
 		boolean match(String str) {
 			for (int i=0;i<str.length();i++) { if (s.charAt(idx+i)!=str.charAt(i)) return false;}
 			idx+=str.length(); return true;
 		}
 		public Object readNum() {
 			StringBuilder sb = new StringBuilder(); int i=0;
 			if (is('-')) {sb.append("-");i++;}
 			boolean isfloat=false;
 			while (true) {
 				if (idx+i>=len) break;
 				if (peek(i)=='.') { sb.append("."); i++; isfloat=true; continue;}
 				if (Character.isDigit(peek(i))) { sb.append(peek(i++)); continue;}
 				break; // not a digit
 			}
 			Object num = null;
 			if (isfloat) { num=Double.parseDouble(sb.toString());}
 			else {num=Long.parseLong(sb.toString());}
 			idx+=sb.length();
 			return num;
 		}
	}
	
	public Q eval(Object o) throws Exception {
		if (props!=null&&o!=null) {
			for (Kv kv:props) {
				String mname = kv.key;
				mname="get"+Character.toUpperCase(mname.charAt(0))+mname.substring(1);
				Method m = o.getClass().getDeclaredMethod(mname);
				Object v = m.invoke(o);
				if (kv.value instanceof Q) {
					((Q)kv.value).eval(v);
				} else if (kv.value instanceof List) {
					Q qi = (Q)((List)kv.value).get(0);
					List lst = new ArrayList();
					for (Object i : (List)v) { lst.add(qi.eval(i).copy());}
					kv.value=lst;
				} else {
					kv.value=v;
				}
			}
		}
		return this;
	}

	public Q copy() { Q q = new Q(); for (Kv kv : props) { q.set(kv.key,copyValue(kv.value)); } return q;}
	public Object copyValue(Object v) {
		if (v instanceof Q) return ((Q)v).copy();
		if (v instanceof List) { List lst = new ArrayList(); for (Object i:(List)v) { lst.add(copyValue(i));} return lst;}
		return v;
	}
	
	static void fail(Exception e) { fail(e.getMessage());}
	static void fail(String msg) { throw new RuntimeException(msg);}
	static void print(String msg) {System.out.println(msg);}
	void print() {print(toJson());}
}
