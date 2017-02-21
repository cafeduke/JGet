<!--
   incrementalSessionObjects.jsp

   Sessions
   This stress test steadily increases the number of objects in
   in the session from request to request.

   Copyright (c) 2003 Oracle Corporation
   Author: Scott Christley

-->

<html>
<title>Incremental Session Objects</title>
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
    // Get the session
    HttpSession aSession = request.getSession(true);
    int currentIteration = 0;
    if (!status)
%>
test FAILED.
<%
    else
    {
	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
%>
ERROR: iteration session object value is not an integer: <%= cis %>
<p>
<%
                status = false;
	      }
	  }
    }
    if (!status)
%>
test FAILED.
<%
    else
    {
	// increment the iteration and store in the session
	++currentIteration;
	aSession.setAttribute("Iteration", String.valueOf(currentIteration));

	// A set of 20 letters to use as random data
	String letters = "ABCDEFGHIJKLMNOPQRST";
	// A 32 character string to use as the value
	char buffer[] = new char[32];

	// Verify the objects provided it's not the initial iteration
	if (currentIteration != 1)
	  {
	  }

	// add the initial set of objects
	for (int i = 0;i < initial; ++i)
	  {
	    char ltr = letters.charAt(i % 20);
	    for (int k = 0;k < 32; ++k)
	      buffer[k] = ltr;
	    String sv = "SessionStress" + String.valueOf(i);
	    aSession.setAttribute(sv, new String(buffer));
	  }

	// add additional objects
	for (int i = 1;i < currentIteration; ++i)
	  {
	    for (int j = 0;j < increment; ++j)
	      {
		char ltr = letters.charAt(j % 20);
		for (int k = 0;k < 32; ++k)
		  buffer[k] = ltr;
		String sv = "SessionStress"
		  + String.valueOf(((i - 1) * increment) + j + initial);
		aSession.setAttribute(sv, new String(buffer));
	      }
	  }

	// if it is the last iteration then invalidate the session
	if (currentIteration == iterations)
	  aSession.invalidate();
%>
test PASSED.
<%
    }
%>

</body>
</html>
