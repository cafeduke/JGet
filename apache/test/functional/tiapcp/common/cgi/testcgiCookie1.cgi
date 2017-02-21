#!%APACHE_PERL_BIN%

# Copyright (c) 2000, 2001, Oracle Corporation.  All rights reserved.  
#
# NAME
#       testcgiCookie1.cgi.txt - template for testcgiCookie1.cgi
#
# Description
#       This test sets a cookie - expires in one minute.  It is to
#       be read by testcgiCookie2.cgi
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
$query = new CGI;

$cookie = $query->cookie(-name=>'COOKIE',
						-value=>'SET',
						-expires=>'+1m',
						-path=>'/');
print $query->header(-cookie=>$cookie);
#pretty much the rest of this code exists to support running the test under a browser
#unrem line 37 to do so.  Leave it remmed to run under test harness.
print<<"EOF";
	<html>
	<head>
	<title> testcgiCookie1.cgi </title>
	<script language="JavaScript">
	function button1Clicked()
	{
		document.test.test.click()
	}
	</script>
	</head>
	<body>
	<br><br>
	<form action="testcgiCookie2.cgi" method="get" name="test">
	This page sets a CGI cookie using CGI objects. It automatically calls testcgiCookie2.cgi.\n<p>
	<input type="submit" name="test" value="submit">
	<script language="JavaScript">
#		setTimeout("button1Clicked()",2000)
	</script>
EOF
print "\ntest PASSED.\n"; #this line is required to fake out oratst to continue - it does nothing
print $query->end_html;

