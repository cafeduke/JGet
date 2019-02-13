#!%APACHE_PERL_BIN%

# Copyright (c) 2000, 2001, Oracle Corporation.  All rights reserved.  
#
# NAME
#       testcgiMeta3.cgi.txt - template for testcgiMeta3.cgi
#
# Description
#
#       This test checks that the mandantory minimum set of CGI path info
#	 variables are set. 
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
$PASS_FAIL2=$cgi->param('passfail2');
#MetaVariables: The WWW Common Gateway Interface Version 1.1
#http://web.golux.com/coar/cgi/draft-coar-cgi-v11-03.txt

#If this page was called from another page or another location via the POST
#method, the following variables must be set:
#CONTENT_LENGTH
#CONTENT_TYPE
#HTTP_REFERER
#REQUEST_METHOD
#in this case I've passed along extra path info.  Because of that,
#PATH_INFO and
#PATH_TRANSLATED must be set also
print<<"EOF";
	<html>
	<head>
	<title> testcgiMeta3.pl </title>
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

#the content types only apply when the refering page issued a post instead of a get to get 
#this page.  If it was a get, then content_type and content_length will be defined, but
#they will be of length null.  Since the calling form passed along extra path info
#PATH_INFO and PATH_TRANSLATED have values too (result should show same URL though)

#To run this test from a browser, you first need to unrem line 67
#("setTimeout(\"button1Clicked()\",1000)";)

&getenvs($ENV{"CONTENT_TYPE"},"CONTENT_TYPE");
&getenvs($ENV{"CONTENT_LENGTH"}, "CONTENT_LENGTH");
&getenvs($ENV{"HTTP_REFERER"}, "HTTP_REFERER");
&getenvs($ENV{"REQUEST_METHOD"}, "REQUEST_METHOD");
&getenvs($ENV{"PATH_INFO"}, "PATH_INFO");
&getenvs($ENV{"PATH_TRANSLATED"}, "PATH_TRANSLATED");

if ($PASS_FAIL)
{
	print "<br>";
	print "\ntest FAILED.\n";
	exit();
}
else
{
print "<br><br>";
print "<form action=\"testcgiCookie1.cgi\" method=\"post\" name=\"test\">";
print "<input type=\"submit\" name=\"test\" value=\"submit\">";
		
$PASS_FAIL3="PASS";
print "<br><br>";
print "\ntest PASSED.\n";
print "</form>";
print "<script language=\"JavaScript\">";
#print "setTimeout(\"button1Clicked()\",1000)";
print "</script>";
print "</body>";
print "</html>";
}
#------------------------------------------------------------
sub getenvs()
{
	$tempvar = $_[1];	
	$tempval = $_[0];
	print "\n<br>";
	if($tempval != 0 || $tempval ne "")  #make sure the variable value is something other than 0 or null
	{
		print ("\n$tempvar = <FONT COLOR=\"RED\">",$tempval);
		print "</font>";
	}
	else 
	{
		print $_[1] . "is defined but not set.<br>\n";
		$PASS_FAIL=$TESTFAIL;
		return;
	}
}

