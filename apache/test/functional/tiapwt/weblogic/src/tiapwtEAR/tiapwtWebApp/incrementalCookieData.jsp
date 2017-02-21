<!--
   incrementalCookieData.jsp

   Cookies
   This stress test steadily increases the size of the cookie
   data from request to request.

   Copyright (c) 2003 Oracle Corporation
   Author: Scott Christley

-->

<html>
<title>Incremental Cookie Data</title>
<body>

<%
    // test status
    boolean status = true;

    // Get parameters
    int iterations = -1;
    String s = request.getParameter("iterations");
    if (s == null)
      iterations = -1;
    else {
      try { iterations = Integer.parseInt(s); }
      catch (java.lang.NumberFormatException e)
        { iterations = -1; }
    }
    if (iterations < 0) {
       status = false;
%>
Invalid valid for iterations parameter<p>
<%
    }

    int initial = -1;
    s = request.getParameter("initial");
    if (s == null)
      initial = -1;
    else {
      try { initial = Integer.parseInt(s); }
      catch (java.lang.NumberFormatException e)
        { initial = -1; }
    }
    if (initial < 0) {
       status = false;
%>
Invalid valid for intial parameter<p>
<%
    }

    int increment = -1;
    s = request.getParameter("increment");
    if (s == null)
      increment = -1;
    else {
      try { increment = Integer.parseInt(s); }
      catch (java.lang.NumberFormatException e)
        { increment = -1; }
    }
    if (increment < 0) {
       status = false;
%>
Invalid valid for increment parameter<p>
<%
    }
%>
<hr>
<%
    // do the test
    Cookie[] cookies = request.getCookies();
    Cookie c;
    int currentIteration = 0;
    if (!status)
%>
test FAILED.
<%
    else
    {
	// Get the current iteration which is stored as a cookie
	if (cookies != null)
	  {
	    for (int i = 0;i < cookies.length; ++i)
	      {
		if (cookies[i].getName().equals("iteration"))
		  {
		    String sc = cookies[i].getValue();
		    if (sc != null)
		      {
			try
			  {
			    currentIteration = Integer.parseInt(sc);
			  }
			catch (java.lang.NumberFormatException e)
			  {
%>
ERROR: iteration cookie value is not an integer: <%= sc %>
<p>
<%
			    status = false;
			  }
		      }
		  }
	      }
	  }

    }
    if (!status)
%>
test FAILED.
<%
    else
    {
	// increment the iteration and store as cookie
	++currentIteration;
	c = new Cookie("iteration", String.valueOf(currentIteration));
	response.addCookie(c);

	// A set of 20 letters to use as random data
	String letters = "ABCDEFGHIJKLMNOPQRST";

	// Verify the cookies provided it's not the initial iteration
	if (currentIteration != 1)
	  {
	  }

	// add the cookie
	int size = initial + (increment * (currentIteration - 1));
	char buffer[] = new char[size];

	char ltr = letters.charAt(currentIteration % 20);
	for (int j = 0;j < size; ++j)
	  buffer[j] = ltr;

	c = new Cookie("CookieStress", new String(buffer));
	response.addCookie(c);
%>
test PASSED.
<%
    }
%>

</body>
</html>
