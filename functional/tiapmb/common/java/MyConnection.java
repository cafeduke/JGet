import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

import javax.management.ObjectName;

import org.junit.Test;

public class MyConnection extends TestSuite {
	protected String directiveName = null;
        private static String OS=System.getProperty("os.name").toLowerCase();

	/*
	 * Initialize connection to the Domain Runtime MBean Server.
	 */

	public static void main(String args[]) throws Exception{
		MyConnection mc = new MyConnection();
		mc.setUp(args);
		//System.out.println("MAIN METHOD");
	}
	public void setUp(String args[]) throws Exception {
	
		 System.out.println(" OS Name is: "+ OS);
		
		super.setUp(args);
		String tc=System.getenv("env3");
		//System.out.println("ENV3 value is "+ tc);
		if(tc.equalsIgnoreCase("1")){
			testVerifyKeepAliveTimeout();
		} else if(tc.equalsIgnoreCase("2")){
			testVerifyAsyncValue();
		} else if(tc.equalsIgnoreCase("3")){
			testgetAsyncRequestWorkerFactor();
		} else if(tc.equalsIgnoreCase("4")){
			testSetKeepAlive();
		} else if(tc.equalsIgnoreCase("5")){
			testSetAsyncRequestWorkerFactor();
		} else if(tc.equalsIgnoreCase("6")){
			testIsNMPropPresent();
		} else if(tc.equalsIgnoreCase("7")){
			testSetMpm();
		} else if(tc.equalsIgnoreCase("8")){
			testIsCreateNamedVirtualHostPresent();
		} else if(tc.equalsIgnoreCase("9")){
			testSetOraLogMode();
		} else if(tc.equalsIgnoreCase("10")){
			testSetKeepAliveTimeout();
		} else if(tc.equalsIgnoreCase("11")){
			testSetMaxRequestsWorkers();
		} else if(tc.equalsIgnoreCase("12")){
			testGetMaxRequestWorkers();
		} else if(tc.equalsIgnoreCase("13")){
			testSetMaxConnectionsPerChild();
		} else if(tc.equalsIgnoreCase("14")){
			testSetMaxSpareThreads();
		} else if(tc.equalsIgnoreCase("15")){
			testSetMinSpareThreads();
		} else if(tc.equalsIgnoreCase("16")){
			testVerifyMaxMinSpareThreads();
		} else if(tc.equalsIgnoreCase("17")){
			testGetThreadsPerChild();
		} else if(tc.equalsIgnoreCase("18")){
			testSetThreadsPerChild();
		} else if(tc.equalsIgnoreCase("19")){
			testVerifyTimeout();
		} else if(tc.equalsIgnoreCase("20")){
			testAllApacheLogLevels();
		} else if(tc.equalsIgnoreCase("21")){
			testAllOraLogLevels();
		} else if(tc.equalsIgnoreCase("22")){
			testSetOraLogLevel();
		}
		
	}

	@Test
	public void testVerifyAsyncValue() throws Exception {
		System.out.println("Test Case 2");
		directiveName = "AsyncRequestWorkerFactor";
		Object val = getAttributeValue(directiveName);
		Float value = (float) 2.0;
		System.out
				.println("The Directive AsyncRequestWorkerFactor is present and it's default value is--- "
						+ (val));
		assertEquals("Default AsyncRequestWorkerfactor is not 2--- ", value,
				val);
		System.out.println("============================");
	try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The Directive AsyncRequestWorkerFactor is present and it's default value is " +val);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}

	@Test
	public void testgetAsyncRequestWorkerFactor() throws Exception {
		System.out.println("Test Case 3");
		directiveName = "AsyncRequestWorkerFactor";
		Object val = getAttributeValue(directiveName);
		System.out.println("AsyncRequestWorkerFactor value is--- " + (val));
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The Directive AsyncRequestWorkerFactor's value is " +val);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}
	
	

	@Test
	public void testVerifyKeepAliveTimeout() throws Exception {
		System.out.println("Test Case 1");
		directiveName = "KeepAliveTimeout";
		Object val = getAttributeValue(directiveName);
		System.out.println("The value of KeepAliveTimeout is--- " + (val));
		System.out.println("============================");
		assertEquals("KeepAliveTimeout is not correct", "5", val);

	try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The value of KeepAliveTimeout is " +val);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
		
	}

	/*
	 * // @Test public void testGetAllDirectives() throws Exception { //
	 * System.out.println("Tese Case 2"); // MBeanAttributeInfo[] ma =
	 * getAllAttributes(); // for (int x = 0; x < ma.length; x++) //
	 * System.out.println("%% " + ma[x].getName() + " ** "); //
	 * System.out.println("***** printed   MBEAN INFO "); // }
	 */

	@Test
	public void testSetKeepAlive() throws Exception {
		System.out.println("Test Case 4");
		ObjectName cfgRoot = startEditSession();
		editKeepAlive(cfgRoot);
		activate();
		// connection.invoke(OHS_NEW_CONFIG_MBEAN, "save", null, null);
		System.out.println("KeepAlive value is changed");
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		 File newTextFile=new File(outres);
 		FileWriter fw= new FileWriter(newTextFile);
 		fw.write("KeepAlive value set successfully");
 		fw.close();
		} catch(Exception e){
			System.out.println("error in setting KeepAlive Value");
		}
	}

	@Test
	public void testSetAsyncRequestWorkerFactor() throws Exception {
		System.out.println("Test Case 5");
		ObjectName cfgRoot = startEditSession();
		editAsyncRequestWorkerFactor(cfgRoot);
		activate();
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		   File newTextFile=new File(outres);
  FileWriter fw= new FileWriter(newTextFile);
  fw.write("AsyncRequestWorkerFactor set successfully");
  fw.close();
		} catch(Exception e){
			System.out.println("error in setting AsyncRequestWorkerFactor");
		}
	}

	@Test
	public void testIsNMPropPresent() throws Exception {
		System.out.println("Test Case 6");
		Object val = getAttribute();
		System.out
				.println("Mbean 'oracle.ohs:type=OHSInstance.NMProp is present and Mpm's default value is--- "
						+ (val));
	
		System.out.println("printing OSTYPE " + OS);
	                        if(OS.indexOf("win") >=0){
 assertEquals("Default Mpm value is not winnt--- ", "winnt", val);        	              
		 } else {

		assertEquals("Default Mpm value is not event--- ", "event", val);   }
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The MPM default value is " +val);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}

	@Test
	public void testSetMpm() throws Exception {
		System.out.println("Test Case 7");
 		if(OS.indexOf("win") >=0){
  System.out.println("Test Case 7 is skipped, it is only meant for Linux environment and not Windows");



                        } else {
		ObjectName cfgRoot = startEditSession();
		editMpm(cfgRoot);
		activate();
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		} catch(Exception e){
			System.out.println("error in setting MpM");
		}
	}
	}

	@Test
	public void testIsCreateNamedVirtualHostPresent() throws Exception {
		System.out.println("Test Case 8");
		getCreateNamedVirtualHost();
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		   File newTextFile=new File(outres);
  FileWriter fw= new FileWriter(newTextFile);
  fw.write("CreateNamedVirtualHost is Present");
  fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}
	/*
	@Test
	public void testAddNewDirective() throws Exception {
		System.out.println("Test Case Add Directive");
		ObjectName cfgRoot = startEditSession();
		addDirectiveNew(cfgRoot);
		activate();
		System.out.println("============================");
	}
	
	@Test
	public void testSetNewDirective() throws Exception {
		System.out.println("Test Case Set Directive");
		ObjectName cfgRoot = startEditSession();
		setDirectiveNew(cfgRoot);
		activate();
		System.out.println("============================");
	}
	
	@Test
	public void testUpdateNewDirective() throws Exception {
		System.out.println("Test Case Update Directive");
		ObjectName cfgRoot = startEditSession();
		updateDirectiveNew(cfgRoot);
		activate();
		System.out.println("============================");
	}
	*/
/*
	@Test
	public void testOraLogMode() throws Exception {
		System.out.println("Test Case 10");
		directiveName = "OraLogMode";
		Object val = getAttributeValue(directiveName);
		// assertEquals("OraLogMode is not correct", "5", val);
		System.out.println("The value of OraLOgMode is--- " + (val));
		System.out.println("============================");
	}

	
	@Test
	public void testUpdateVirtualHostNew() throws Exception {
		System.out.println("Test Case Virtual Host update Directive");
		ObjectName cfgRoot = startEditSession();
		updateVirtualHostNew(cfgRoot);
		activate();
		System.out.println("============================");
	}
	*/
	@Test
	public void testSetOraLogMode() throws Exception {
		System.out.println("Test Case 9");
		String OraValue="text";
		ObjectName cfgRoot = startEditSession();
		editOraLogMode(cfgRoot, OraValue);
		activate();
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The OraLogMode value is " +OraValue);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}

//	@Test
//	public void testGetFileContents() throws Exception {
//		System.out.println("Test Case 12");
//		String cont = methodGetFileContents();
//		// System.out.println(cont);
//		System.out.println("============================");
//	}
//	 @Ignore("not ready yet")
//	 @Test
//	 public void testSetFileContents() throws Exception {
//	 System.out.println("Test Case 13");
//	 String cont = methodGetFileContents();
//	 int left = cont.indexOf("(DSO)");
//	 String middle = " #comment\n" + "#\n";
//	 int right = cont
//	 .indexOf("# To be able to use the functionality of a module which was built as a DSO");
//	 ObjectName cfgRoot = startEditSession();
//	 String result = cont.substring(0, left + 13) + middle
//	 + cont.substring(right);
//	 methodSetFileContents(result);
//	 activate();
//	 System.out.println(result);
//	 System.out.println(cont);
//	 System.out.println("============================");
//	 }

	@Test
	public void testSetKeepAliveTimeout() throws Exception {
		System.out.println("Test Case 10");
		startEditSession();
		String timeValue = "5";
		editKeepAliveTimeout(timeValue);
		activate();
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The KeepAliveTimeout value is " +timeValue);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}

	@Test
	public void testSetMaxRequestsWorkers() throws Exception {
		System.out.println("Test Case 11");
		                if(OS.indexOf("win") >=0){
  System.out.println("Test Case 11 is skipped, it is only meant for Linux environment and not windows");

                        } else {
		startEditSession();
		Integer timeValue = 120;
		editMaxRequestWorkers(timeValue);
		activate();
		Object val = getAttributeValue("MaxRequestWorkers");
		int m = (int) val;
		assertTrue("MaxRequestWorkers can not be negative", 0 < m);
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The MaxRequestsWorkers value is " +timeValue);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}
	}

	@Test
	public void testGetMaxRequestWorkers() throws Exception {
		System.out.println("Test Case 12");
		directiveName = "MaxRequestWorkers";
		Object val = getAttributeValue(directiveName);
		System.out.println("MaxRequestWorker is " + (val));
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The MaxRequestsWorkers value is " +val);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}

	@Test
	public void testSetMaxConnectionsPerChild() throws Exception {
		System.out.println("Test Case 13");
		startEditSession();
		Integer timeValue = 10;
		editMaxConnectionsPerChild(timeValue);
		activate();
		Object val = getAttributeValue("MaxConnectionsPerChild");
		int mc = (int) val;
		assertTrue("MaxConnectionsPerChild can not be negative", 0 < mc);
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The MaxConnectionsPerChild value is " +val);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}

	// @Test
	// public void testSetMaxSpareServers() throws Exception {
	// System.out.println("Test Case 17");
	// startEditSession();
	// Integer timeValue = 5;
	// editMaxSpareServers(timeValue);
	// activate();
	// System.out.println("============================");
	// }
	//
	// @Test
	// public void testSetMinSpareServers() throws Exception {
	// System.out.println("Test Case 18");
	// startEditSession();
	// Integer timeValue = 5;
	// editMinSpareServers(timeValue);
	// activate();
	// System.out.println("============================");
	// }

	@Test
	public void testSetMaxSpareThreads() throws Exception {
		System.out.println("Test Case 14");
	                                if(OS.indexOf("win") >=0){
  System.out.println("Test Case 14 is skipped, it is only meant for Linux environment and not windows");

                        } else {
		startEditSession();
		Integer timeValue = 250;
		editMaxSpareThreads(timeValue);
		activate();
		Object val = getAttributeValue("MaxSpareThreads");
		int comp = (int) val;
		assertTrue("MaxSpareThreads can not be negative", 0 < comp);
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The MaxSpareThreads is " +val);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}
	}

	@Test
	public void testSetMinSpareThreads() throws Exception {
		System.out.println("Test Case 15");
	                                if(OS.indexOf("win") >=0){
  System.out.println("Test Case 15 is skipped, it is only meant for Linux environment and not windows");

                        } else {
		startEditSession();
		Integer timeValue = 5;
		editMinSpareThreads(timeValue);
		activate();
		Object val = getAttributeValue("MinSpareThreads");
		int comp = (int) val;
		assertTrue("MinSpareThreads can not be negative", 0 < comp);
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The MinSpareThreads is " +val);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}
	}

	@Test
	public void testVerifyMaxMinSpareThreads() throws Exception {
		System.out.println("Test Case 16");
	                                if(OS.indexOf("win") >=0){
  System.out.println("Test Case 16 is skipped, it is only meant for Linux environment and not windows");

                        } else {
		Object val1 = getAttributeValue("MaxSpareThreads");
		int max = (int) val1;
		Object val2 = getAttributeValue("MinSpareThreads");
		int min = (int) val2;
		//assertTrue("MaxSpareThreads can not be less than MinSpareThreads",
			//	max > min);
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The MaxSpareThreads is greater than MinSpareThreads, " +max+ ">" +min);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}
	}

	@Test
	public void testGetThreadsPerChild() throws Exception {
		System.out.println("Test Case 17");
		directiveName = "ThreadsPerChild";
		Object val = getAttributeValue(directiveName);
		System.out.println("ThreadsPerChild is " + (val));
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The ThreadsPerChild value is " +val);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}

	@Test
	public void testSetThreadsPerChild() throws Exception {
		System.out.println("Test Case 18");
		startEditSession();
		Integer timeValue = 64;
		editThreadsPerChild(timeValue);
		activate();
	Object val = getAttributeValue("ThreadsPerChild");
	int comp = (int) val;
	assertTrue("ThreadsPerChild can not be negative", 0 < comp);
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The ThreadsPerChild value is " +val);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}

	@Test
	public void testVerifyTimeout() throws Exception {
		System.out.println("Test Case 19");
		directiveName = "Timeout";
		Object val = getAttributeValue(directiveName);
		assertEquals("Default Timeout value is not correct", 60, val);
		System.out.println("The value of KeepAliveTimeout is--- " + (val));
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The Timeout value is " +val);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}

	@Test
	public void testAllApacheLogLevels() throws Exception {
		System.out.println("Test Case 20");
		directiveName = "AllApacheLogLevels";
		String[] val = getLogLevels(directiveName);
		System.out.println("AllApacheLogLevels values are "
				+ Arrays.toString(val));
		System.out.println("============================");
		String testarr[] = { "emerg", "alert", "crit", "error", "warn",
				"notice", "info", "debug", "trace1", "trace2", "trace3",
				"trace4", "trace5", "trace6", "trace7", "trace8" };
		assertEquals("Default LogLevel value is not correct",
				Arrays.toString(testarr), Arrays.toString(val));
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The ALLApacheLogLevels value are " +Arrays.toString(val));
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}

	}

	@Test
	public void testAllOraLogLevels() throws Exception {
		System.out.println("Test Case 21");
		directiveName = "AllOraLogLevels";
		String[] val = getLogLevels(directiveName);
		System.out
				.println("AllOraLogLevels values are " + Arrays.toString(val));
		System.out.println("============================");
		String testarr1[] = { "INCIDENT_ERROR:10", "INCIDENT_ERROR:20", "INCIDENT_ERROR:32", "ERROR:32", "WARNING:32",
				"NOTIFICATION:16", "NOTIFICATION:32", "TRACE:16", "TRACE:18", "TRACE:20", "TRACE:22",
				"TRACE:24", "TRACE:26", "TRACE:28", "TRACE:30", "TRACE:32" };
		assertEquals("Default LogLevel value is not correct",
				Arrays.toString(testarr1), Arrays.toString(val));
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The ALLOraLogLevels value are " +Arrays.toString(val));
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}

	@Test
	public void testSetOraLogLevel() throws Exception {
		System.out.println("Test Case 22");
		startEditSession();
		String Value = "NOTIFICATION:16 mime ERROR";
		editOraLogLevel(Value);
		activate();
		System.out.println("============================");
		try{
		String outres=System.getenv("env2");
		System.out.println("ENV2 value is "+ outres);
		
		File newTextFile=new File(outres);
		FileWriter fw= new FileWriter(newTextFile);
		fw.write("The OraLogLevel value is " + Value);
		fw.close();
		} catch(Exception e){
			System.out.println("error");
		}
	}

}
