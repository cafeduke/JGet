#!%APACHE_PERL_BIN%

# Copyright (c) 2000, 2001, Oracle Corporation.  All rights reserved.  
#
# NAME
#       testcgiCookie2.cgi.txt - template for testcgiCookie2.cgi
#
# Description
#       This test reads the cookie that was set by testcgiCookie2.cgi
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
$query=new CGI;
print $query->header;

print "<html>\n";
print "<head><title>Cookies Set?</title></head>\n";
print "<body>\nThis checks to see if our cookie was successfully set by this browswer.<p>";
print "<hr><pre>";
$flag = "";		#init flag
$raw_cookie=$ENV{'HTTP_COOKIE'};  #get raw cookie
@cookies=split /;/,$raw_cookie;		#split name/value pairs
if (@cookies eq "")			#if there's no cookie, then we fail no matter what
{
	print "\nTest FAILED.\n";
}
else
{
	foreach $cookie (@cookies)
	{
		@cookie_split = split /=/,$cookie;	#split into name value pairs
		foreach $name_value (@cookie_split)
		{
			if ($name_value eq "COOKIE")	#look for the string COOKIE
			{
				$flag = "COOKIE";
			}
			if ($name_value eq "SET")		#if COOKIE=SET, then we know it's ours
			{
				if ($flag eq "COOKIE")
				{
					print "\ntest PASSED.\n";  #and the test passed
				}
			}
		}
	}
	if ($flag eq "")
	{
		print "\ntest FAILED.\n";
	}
}
print "</pre>\n";
print "</body></html>\n";
