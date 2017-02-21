#!/usr/bin/perl
 
use CGI::Fast;
 
while (my $q = CGI::Fast->new) {
    print "Content-type: text/html\n\n";
    print "<html><body><h1>";
    print "Hello, FastCGI world!";
    print "</h1></body></html>\n";
}
