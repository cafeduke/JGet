import java.io.BufferedReader;
import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.security.KeyStore;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStreamReader; 
import java.io.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
public class HttpsURLConnectionExample {
 
	public static void main(String[] args) throws Exception {

TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };

	// Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
 
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
            
                return true;
            }
        };
 
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
 
		HttpsURLConnectionExample https = new HttpsURLConnectionExample();
 
		System.out.println("Testing 1 - Send Https GET request");
		https.sendGet(args);
 
	}
		

	// HTTP GET request
	private void sendGet(String args[]) throws Exception {
 
		//System.setProperty("javax.net.ssl.keyStoreType", "jks");
		//System.setProperty("javax.net.ssl.keyStore", "/ade/skaneria_v0325/oracle/work/sslJKS.jks");
		//System.setProperty("javax.net.debug", "ssl");
		//System.setProperty("javax.net.ssl.keyStorePassword", "welcome1");		

		
 		String url=args[0];
		URL obj = new URL(url);
		HttpURLConnection con=(HttpURLConnection)obj.openConnection();
				
		con.setRequestMethod("GET");
		//add request header
		
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
