
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import junit.framework.TestCase;

public class TestSuite extends TestCase {

	protected MBeanServerConnection connection;
	protected JMXConnector connector;
	protected static final ObjectName service;
	protected static final ObjectName OHS_Instance;
	protected static final ObjectName SetDirective_conf;
	protected static final ObjectName OHS_conf;
	protected static final ObjectName OHS_NEW_CONFIG_MBEAN;

	static {
		try {
			service = new ObjectName(
					"com.bea:Name=EditService,Type=weblogic.management.mbeanservers.edit.EditServiceMBean");
		} catch (MalformedObjectNameException e) {
			throw new AssertionError(e.getMessage());
		}
	}

	static {
		try {
			OHS_Instance = new ObjectName(
					"oracle.ohs:OHSInstance=mb1,component=OHS,type=OHSInstance.NMProp");
		} catch (MalformedObjectNameException e) {
			throw new AssertionError(e.getMessage());
		}
	}
	
	static {
		try {
			SetDirective_conf = new ObjectName(
					"oracle.as.management.mbeans.register:type=component,name="
							+ "mb1" + ",instance=" + "mb1");
		} catch (MalformedObjectNameException e) {
			throw new AssertionError(e.getMessage());
		}
	}
	
	static {
		try {
			OHS_conf = new ObjectName(
					"oracle.ohs:type=OHSInstance,name=ohs2" );
		} catch (MalformedObjectNameException e) {
			throw new AssertionError(e.getMessage());
		}
	}

	static {
		try {
			OHS_NEW_CONFIG_MBEAN = new ObjectName(
					"oracle.as.management.mbeans.register:type=component,name="
							+ "mb1" + ",instance=" + "mb1");
		} catch (MalformedObjectNameException e) {
			throw new AssertionError(e.getMessage());
		}
	}
	protected String hostname = null;
	protected String portString = null;
	protected String username = null;
	protected String password = null;


	public void setUp(String args[]) throws Exception {

        	String adminPort=args[0];
		String machineName=args[1];
		hostname = machineName;
		portString = adminPort;
		username = "weblogic";
		password = "welcome1";
		String protocol = "t3";

		Integer portInteger = Integer.valueOf(portString);
		int port = portInteger.intValue();
		String jndiroot = "/jndi/";
		String mserver = "weblogic.management.mbeanservers.edit";
		JMXServiceURL serviceURL = new JMXServiceURL(protocol, hostname, port,
				jndiroot + mserver);

		Hashtable h = new Hashtable();
		h.put(Context.SECURITY_PRINCIPAL, username);
		h.put(Context.SECURITY_CREDENTIALS, password);
		h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES,
				"weblogic.management.remote");
		h.put("jmx.remote.x.request.waiting.timeout", new Long(10000));
		connector = JMXConnectorFactory.connect(serviceURL, h);

		try {
			connection = connector.getMBeanServerConnection();
		} catch (IOException ioe) {
			System.out.println("Failed to initialize admin server connection:"
					+ ioe.getMessage());
		}
	}

	public MBeanAttributeInfo[] getAllAttributes() throws Exception {
		System.out.println("### Will print MBEAN INFO NOW");
		MBeanAttributeInfo[] marray = connection.getMBeanInfo(
				OHS_NEW_CONFIG_MBEAN).getAttributes();
		// for (int x = 0; x < marray.length; x++)
		// System.out.println("%% " + marray[x].getName() + " ** ");
		// System.out.println("***** printed   MBEAN INFO ");
		return marray;
	}
	
	public String[] getLogLevels(String attr) throws Exception {
		String[] value = (String[]) connection.getAttribute(OHS_NEW_CONFIG_MBEAN, attr);
		// for (int x = 0; x < marray.length; x++)
		// System.out.println("%% " + marray[x].getName() + " ** ");
		// System.out.println("***** printed   MBEAN INFO ");
		return value;
	}

	public Object getAttributeValue(String attr) throws Exception {
		Object value = connection.getAttribute(OHS_NEW_CONFIG_MBEAN, attr);
		return value;

	}

	public Object getAttribute() throws Exception {

		Object value = connection.getAttribute(OHS_Instance, "Mpm");
		return value;

	}

	public String getCreateNamedVirtualHost() throws Exception {
		String r = null;
		try {
			String[] pa = { "" };
			String[] si = { "" };

			r = (String) (connection.invoke(OHS_NEW_CONFIG_MBEAN,
					"createNamedVirtualHost", pa, si));
		} catch (Exception e) {
			System.out
					.println("Verified that the operation createNamedVirtualHost is not supported by OHS 12.1.4");
		}
		return r;
	}
	
	public String addDirectiveNew(ObjectName cfgRoot) throws Exception {
		String r = null;
			String[] p1 = { "Listen", "1128", "httpd.conf" };
			String[] p2 = { "java.lang.String", "java.lang.String", "java.lang.String" };

			r = (String) (connection.invoke(SetDirective_conf,
					"addDirective", p1, p2));
			//System.out.println(r);
		return r;
	}

	public String setDirectiveNew(ObjectName cfgRoot) throws Exception {
		String r = null;
			String[] p1 = { "Listen", "1128", "httpd.conf" };
			String[] p2 = { "java.lang.String", "java.lang.String", "java.lang.String" };

			r = (String) (connection.invoke(SetDirective_conf,
					"setDirective", p1, p2));
		return r;
	}
	
	public String updateDirectiveNew(ObjectName cfgRoot) throws Exception {
		String r = null;
			String[] p1 = { "Listen", "1128", "7091", "httpd.conf" };
			String[] p2 = { "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String" };

			r = (String) (connection.invoke(SetDirective_conf,
					"updateDirective", p1, p2));
		return r;
	}
	
	public String updateVirtualHostNew(ObjectName cfgRoot) throws Exception {
		String r = null;
			String[] p1 = {  "*:4443", "*:1128", "ssl.conf" };
			String[] p2 = {  "java.lang.String", "java.lang.String", "java.lang.String" };

			r = (String) (connection.invoke(SetDirective_conf,
					"updateVirtualHost", p1, p2));
		return r;
	}
	
	public String methodGetFileContents() throws Exception {
		String r = null;
		try {
			String[] pa = { "httpd.conf" };
			String[] si = { "java.lang.String" };

			r = (String) (connection.invoke(OHS_NEW_CONFIG_MBEAN,
					"getFileContents", pa, si));
		} catch (Exception e) {
			System.out
					.println("CONTENT");
		}
		return r;
	}
	
	public void methodSetFileContents(String cont) throws Exception{
		//try {
			String[] pa = { "httpd.conf","cont" };
			String[] si = { "java.lang.String","java.lang.String"};

			connection.invoke(OHS_NEW_CONFIG_MBEAN,
					"setFileContents", pa, si);
			//System.out.println("File content changed");
		//} catch (Exception e) {
			System.out
					.println("Error in changing content");
		//}
	}

	public ObjectName startEditSession() throws Exception {
		// Get the object name for ConfigurationManagerMBean.
		ObjectName cfgMgr = (ObjectName) connection.getAttribute(service,
				"ConfigurationManager");

		ObjectName domainConfigRoot = (ObjectName) connection.invoke(cfgMgr,
				"startEdit", new Object[] { new Integer(60000),
						new Integer(120000) }, new String[] {
						"java.lang.Integer", "java.lang.Integer" });
		if (domainConfigRoot == null) {
			// Couldn't get the lock
			throw new Exception("Somebody else is editing already");
		}
		return domainConfigRoot;
	}

	public void editKeepAlive(ObjectName cfgRoot) throws Exception {
		// The calling method passes in the object name for DomainMBean.
		// This method only needs to set the value of an attribute
		// in DomainMBean.
		Attribute adminport = new Attribute("KeepAlive", new Boolean("false"));
		connection.setAttribute(OHS_NEW_CONFIG_MBEAN, adminport);
	}
	
	public void editKeepAliveTimeout(String time) throws Exception {
		try{
		Attribute adminport = new Attribute("KeepAliveTimeout", new String(time));
		connection.setAttribute(OHS_NEW_CONFIG_MBEAN, adminport);
		} catch (Exception e){
			System.out.println("KeepAliveTimeout value "+time+" is not valid. Use KeepAliveTimeout num[ms|s|mi|h].");
			System.out.println("============================");
			fail("Use KeepAliveTimeout num[ms|s|mi|h]");
			
		}
	}
	
	public void editMaxRequestWorkers(Integer time) throws Exception {
		//try{
		Attribute adminport = new Attribute("MaxRequestWorkers", new Integer(time));
		connection.setAttribute(OHS_NEW_CONFIG_MBEAN, adminport);
		System.out.println("MaxConnectionsPerChild value is changed to "+time);
//		} catch (Exception e){
//			System.out.println("MaxRequestWorker value you are trying to set in invalid-- It can not be negative or exceed the range");
//		}
	}
	
	public void editMaxConnectionsPerChild(Integer time) throws Exception {
		//try{
		Attribute adminport = new Attribute("MaxConnectionsPerChild", new Integer(time));
		connection.setAttribute(OHS_NEW_CONFIG_MBEAN, adminport);
		System.out.println("MaxRequestWorkers value is changed to "+time);
//		} catch (Exception e){
//			System.out.println("MaxRequestWorker value you are trying to set in invalid-- It can not be negative or exceed the range");
//		}
	}
	
	public void editMaxSpareServers(Integer time) throws Exception {
		//try{
		Attribute adminport = new Attribute("MaxSpareServers", new Integer(time));
		connection.setAttribute(OHS_NEW_CONFIG_MBEAN, adminport);
		System.out.println("MaxSpareServers value is changed to "+time);
//		} catch (Exception e){
//			System.out.println("MaxRequestWorker value you are trying to set in invalid-- It can not be negative or exceed the range");
//		}
	}
	
	public void editMaxSpareThreads(Integer time) throws Exception {
		//try{
		Attribute adminport = new Attribute("MaxSpareThreads", new Integer(time));
		connection.setAttribute(OHS_NEW_CONFIG_MBEAN, adminport);
		System.out.println("MaxSpareThreads value is changed to "+time);
//		} catch (Exception e){
//			System.out.println("MaxRequestWorker value you are trying to set in invalid-- It can not be negative or exceed the range");
//		}
	}
	
	public void editMinSpareServers(Integer time) throws Exception {
		//try{
		Attribute adminport = new Attribute("MinSpareServers", new Integer(time));
		connection.setAttribute(OHS_NEW_CONFIG_MBEAN, adminport);
		System.out.println("MinSpareServers value is changed to "+time);
//		} catch (Exception e){
//			System.out.println("MaxRequestWorker value you are trying to set in invalid-- It can not be negative or exceed the range");
//		}
	}
	
	public void editMinSpareThreads(Integer time) throws Exception {
		//try{
		Attribute adminport = new Attribute("MinSpareThreads", new Integer(time));
		connection.setAttribute(OHS_NEW_CONFIG_MBEAN, adminport);
		System.out.println("MinSpareThreads value is changed to "+time);
//		} catch (Exception e){
//			System.out.println("MaxRequestWorker value you are trying to set in invalid-- It can not be negative or exceed the range");
//		}
	}
	
	public void editThreadsPerChild(Integer time) throws Exception {
		//try{
		Attribute adminport = new Attribute("ThreadsPerChild", new Integer(time));
		connection.setAttribute(OHS_NEW_CONFIG_MBEAN, adminport);
		System.out.println("ThreadsPerChild value is changed to "+time);
//		} catch (Exception e){
//			System.out.println("MaxRequestWorker value you are trying to set in invalid-- It can not be negative or exceed the range");
//		}
	}
	
	public void editOraLogLevel(String v) throws Exception {
		//try{
		Attribute adminport = new Attribute("OraLogLevel", new String(v));
		connection.setAttribute(OHS_NEW_CONFIG_MBEAN, adminport);
		System.out.println("OraLogLevel value is changed to "+v);
//		} catch (Exception e){
//			System.out.println("MaxRequestWorker value you are trying to set in invalid-- It can not be negative or exceed the range");
//		}
	}
	
	public void editOraLogMode(ObjectName cfgRoot, String val) throws Exception {
		
		try{
		Attribute adminport = new Attribute("OraLogMode", new String(val));
		connection.setAttribute(OHS_NEW_CONFIG_MBEAN, adminport);
		System.out.println("OraLogMode value is changed to " + val.toUpperCase());
		} catch(Exception e){
			if(val.equalsIgnoreCase("XML")){
			System.out.println("OraLOgMode value you are trying to set is incorrect--OraLogMode does not support XML in 12.1.4. OHS");
			} else
			System.out.println("OraLOgMode value you are trying to set is incorrect-- it can only be TEXT or APACHE ");
		//assertTrue(val.equalsIgnoreCase("text")||val.equalsIgnoreCase("apache"));
			System.out.println("============================");
			fail("It can only be TEXT or APACHE ");
		
		}
	}

	public void editAsyncRequestWorkerFactor(ObjectName cfgRoot)
			throws Exception {
		try {
			Attribute adminport = new Attribute("AsyncRequestWorkerFactor",
					new Float("2"));
			connection.setAttribute(OHS_NEW_CONFIG_MBEAN, adminport);
			System.out.println("AsyncRequestWorkerFactor value is changed");
		} catch (Exception ex) {
			System.out
					.println("AsyncRequestWorkerFactor value you are trying to set is incorrect-- it can only be double or float ");
			System.out.println("============================");
			fail("It can only be double or float");
		}
	}

	public void editMpm(ObjectName cfgRoot) throws Exception {
		try {
			Attribute adminport = new Attribute("Mpm", new String("event"));
			connection.setAttribute(OHS_Instance, adminport);
			System.out.println("Mpm value is changed");
		} catch (Exception ex) {
			System.out
					.println("Mpm value you are trying to set is incorrect-- Valid values are 'worker', 'event', and 'prefork' for linux and 'winnt' for windows");
		}
	}

	public ObjectName activate() throws Exception {
		// Get the object name for ConfigurationManagerMBean.
		ObjectName cfgMgr = (ObjectName) connection.getAttribute(service,
				"ConfigurationManager");
		// Instruct MBeanServerConnection to invoke
		// ConfigurationManager.activate(long timeout).
		// The activate operation returns an ActivationTaskMBean.
		// You can use the ActivationTaskMBean to track the progress
		// of activating changes in the domain.
		ObjectName task = (ObjectName) connection.invoke(cfgMgr, "activate",
				new Object[] { new Long(120000) },
				new String[] { "java.lang.Long" });
		return task;
	}

}
