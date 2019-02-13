#!%APACHE_PERL_BIN%

# Copyright (c) 2000, 2001, Oracle Corporation.  All rights reserved.  
#
# NAME
#       testcgiMeta2.cgi.txt - template for testcgiMeta2.cgi
#
# Description
#       This test checks that the mandantory referer minimum set of CGI variables
#	 are set. 
#
# Notes
#
# Modified      (MM/DD/YYYY)
#       aejones 12/12/2000 - Creation
#
# Test Info
#       type = perl/cgi script
# End Test Info
#

use CGI;
$cgi=new CGI;
print $cgi->header;
$PASS_FAIL1=$cgi->param('passfail1');
#MetaVariables: The WWW Common Gateway Interface Version 1.1
#http://web.golux.com/coar/cgi/draft-coar-cgi-v11-03.txt

#If this page was called from another page or another location via the GET method,
#there will be a reference to how you got here and whats in the query string.
#QUERY_STRING
#HTTP_USER_AGENT
#REQUEST_METHOD

#This test is part of a larger suite.  To run via a browser instead of the test harness,
#unrem line 59 ("setTimeout(\"button1Clicked()\",1000)";)  Javascript has no effect 
#in test harness with line 59 remmed.
print<<"EOF";
	<html>
	<head>
	<title> testcgiMeta2.pl </title>
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
#the content types only apply when the refering page issued a get instead of a POST to
#this page.
&getenvs($ENV{"QUERY_STRING"}, "QUERY_STRING");
&getenvs($ENV{"REQUEST_METHOD"}, "REQUEST_METHOD");
&getenvs($ENV{"HTTP_USER_AGENT"}, "HTTP_USER_AGENT");

print<<"EOF2";
<br><br>
<form action="testcgiMeta3.cgi/cgi-bin/cattleprod/testcgiMeta3.cgi" method="post" name="test">
<input type="submit" name="test" value="submit">
EOF2
if ($PASS_FAIL)
{
	print "<br>\ntest FAILED.\n";
	exit();
}                                                           
else
{
	$PASS_FAIL2="PASS";
	print "<br>";
	print "<br>\ntest PASSED.\n";
	
}
print<<"EOF2";
</form>
<script language="JavaScript">
#setTimeout("button1Clicked()",1000)
</script>  
</body>
</html>
EOF2
#------------------------------------------------------------
sub getenvs()
{
	$tempvar = $_[1];
	$tempval = $_[0];
	print "<br>\n";
	if($tempval != 0 || $tempval ne "")  #make sure the variable value is something ot   
	{
		print ("$tempvar = <FONT COLOR=\"RED\">",$tempval);
		print "</font>";
	}
	else
	{
		print "$_[1] is defined but not set.<br>";
		$PASS_FAIL=$TESTFAIL;
	}
}

    
