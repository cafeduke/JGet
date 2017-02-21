import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
 
public class HttpURLConnectionExample {
 
	//private final String USER_AGENT = "Mozilla";
 
	public static void main(String[] args) throws Exception {
 
		HttpURLConnectionExample http = new HttpURLConnectionExample();
 
		System.out.println("Send Http GET request");
		http.sendGet(args);
 
	}
 
	// HTTP GET request
	private void sendGet(String args[]) throws Exception {
 
		//String url = "http://slc06mwj.us.oracle.com:7777/waiter/servlet/ServletStressMulti?mode=1&iterations=10&initial=100&increment=10000";
 		String url=args[0];
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		//add request header
		//con.setRequestProperty("User-Agent", USER_AGENT);
		try{
		int responseCode = con.getResponseCode();
		
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		System.out.println("Response Message : " + con.getResponseMessage());
		
		String rc= Integer.toString(responseCode);
		String outres=System.getenv("env2");
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("Response Code : " +rc);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//BufferedWriter bwr=new BufferedWriter(new FileWriter(new File("/scratch/skaneria/view_storage/skaneria_tiapview/apache/test/functional/tiapwt/common/output/tiapwt_content.txt")));
		String outp= System.getenv("env1");
		BufferedWriter bwr=new BufferedWriter(new FileWriter(new File(outp)));
		bwr.write(response.toString());
		bwr.flush();
		bwr.close(); 	   

		//print result
		//System.out.println(response.toString());
 
	}
	
}
