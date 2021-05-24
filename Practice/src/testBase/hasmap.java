package testBase;

import java.security.KeyStore.Entry;
import java.util.HashMap;
import java.util.Map;

public class hasmap {

	public static void main(String[] args) {


		
		Map<String, String> map=new HashMap<String, String>();
		map.put("1", "asd");
		
		map.put("2", "qwe");
		map.put("3", "poiu");
		map.put("4", "bnm");
		map.put("5", "hgfd");
		
		map.forEach((key,value) -> System.out.println(key +"--" +value));
		
		System.out.println(map.get("5"));
		
		
	//	Entry e =(Entry) map.entrySet();
		
	}

}
