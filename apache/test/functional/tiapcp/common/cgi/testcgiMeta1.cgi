#!%APACHE_PERL_BIN%

# Copyright (c) 2000, 2001, Oracle Corporation.  All rights reserved.  
#
# NAME
#	testcgiMeta1.cgi.txt - template for testcgiMeta1.cgi
# 
# Description
#
#	This test checks that the mandantory minimum set of CGI variables are set. 
# Notes
#
# Modified	(MM/DD/YYYY)
#	aejones	12/12/2000 - Creation
#
# Test Info
#	type = perl/cgi script
# End Test Info
#
 
use CGI;
$query=new CGI;
print $query->header;
    
#MetaVariables: The WWW Common Gateway Interface Version 1.1
#http://web.golux.com/coar/cgi/draft-coar-cgi-v11-03.txt
	 
#The canonical metavariables defined by this specification are:
#AUTH_TYPE
#CONTENT_LENGTH
#CONTENT_TYPE
#GATEWAY_INTERFACE
#REMOTE_ADDR
#PATH_TRANSLATED
#QUERY_STRING
#REMOTE_ADDR
#REMOTE_HOST
#REMOTE_IDENT
#REMOTE_USER
#REQUEST_METHOD
#SCRIPT_NAME
#SERVER_NAME
#SERVER_PORT
#SERVER_PROTOCOL
#SERVER_SOFTWARE
	  
#Of these variables, the following *must* be set under all circumstances:
#REMOTE_ADDR
#REQUEST_METHOD
#SCRIPT_NAME
#SERVER_NAME
#SERVER_PORT
#SERVER_PROTOCOL
#SERVER_SOFTWARE

#The default variables that get set under Apache also include:
#DOCUMENT_ROOT
#GATEWAY_INTERFACE
#HTTP_AGENT (doesn't work under test harness though, only with web browser)

#This test is test #1 of a 3 part test.  If you wish to run this test under a browser,
#unremark line 100 ("setTimeout(\"button1Clicked()\",1000)";)and in testcgiMeta2.cgi
#javascript code has no effect in test harness with line 100 remmed.
print<<"EOF";
	<html>
	<head>
	<title> TestCGIMeta1.cgi </title>
	<script language="JavaScript">
	function button1Clicked()
	{
		document.test.test.click()
	}
	</script>
	</head>
	<body>
EOF

$TESTFAIL = 1;  #we can change it from pass to fail, but never from fail to pass
$current_os = ("$^O\n");
    
print "Operating system is <font color=\"Red\">", $current_os;
print "</font>";
print "<br>";
		                            
&getenvs($ENV{"DOCUMENT_ROOT"},"DOCUMENT_ROOT");
&getenvs($ENV{"GATEWAY_INTERFACE"},"GATEWAY_INTERFACE");
#&getenvs($ENV{"HTTP_ACCEPT"},"HTTP_ACCEPT");
&getenvs($ENV{"HTTP_USER_AGENT"},"HTTP_USER_AGENT");
&getenvs($ENV{"REMOTE_ADDR"},"REMOTE_ADDR");
&getenvs($ENV{"REQUEST_METHOD"}, "REQUEST_METHOD");
&getenvs($ENV{"SCRIPT_NAME"}, "SCRIPT_NAME");
&getenvs($ENV{"SERVER_NAME"},"SERVER_NAME");
&getenvs($ENV{"SERVER_PORT"},"SERVER_PORT");
&getenvs($ENV{"SERVER_PROTOCOL"},"SERVER_PROTOCOL");
&getenvs($ENV{"SERVER_SOFTWARE"},"SERVER_SOFTWARE");
  
print<<"EOF2";
<br><br>
<form action="testcgiMeta2.cgi" method="get" name="test">
<input type="submit" name="test" value="submit">
EOF2
if ($PASS_FAIL)
{
	print "\ntest FAILED.\n";
	exit();
}
else
{
	$PASS_FAIL1="PASS";
	print "<input type=\"hidden\" name=\"passfail1\" value=\"$PASS_FAIL1\">";
	print "<br>";
	print "\ntest PASSED.\n";
}

print<<"EOF3";
</form>
<script language="JavaScript">
#setTimeout("button1Clicked()",1000)
</script>
</body></html>
EOF3
sub getenvs
{
	$tempvar = $_[1];
	$tempval = $_[0];
	print "<br>";
	if($tempval != 0 || $tempval ne "")  #make sure the variable value is something ot    
	{
		print ("<br>\n $tempvar = <FONT COLOR=\"RED\">",$tempval);
		print "</font>";
	}
	else
	{
		print "$_[1] is defined but not set.<br>";
		$PASS_FAIL=$TESTFAIL;
	}
}


