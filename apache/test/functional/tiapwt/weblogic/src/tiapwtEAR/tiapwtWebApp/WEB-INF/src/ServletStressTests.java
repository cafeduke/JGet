/*
   ServletStressTests.java

   Test for stressing the Tomcat Java Servlet engine and surrounding
   application server.  Use for both multiple and single thread model.

   Copyright (c) 2000 Oracle Corporation
   Author: Scott Christley

*/

import java.io.*;
import javax.servlet.*;
import java.sql.*;
import java.util.*;
import javax.servlet.http.*;

public class ServletStressTests extends Object
{
  ServletStressMulti theServlet;

  // constructor
  public ServletStressTests(ServletStressMulti aServlet)
  {
    theServlet = aServlet;
  }

  // Response data sizes
  // This stress test writes increasing larger amounts of data
  // to the response stream.
  //
  public boolean doIncrementalResponseData(HttpServletRequest request,
					   HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// do the test
	PrintWriter out = response.getWriter();
	theServlet.displayForm(request, response, null);
	out.println("<hr>");
	for (int i = 0;i < iterations; ++i)
	  {
	    int size = initial + (increment * i);
	    char buffer[] = new char[size];

	    for (int j = 0;j < size; ++j)
	      buffer[j] = 'A';

	    out.println(buffer);
	    out.println("<p>");
	  }

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doIncrementalResponseData: "
			+ e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Response data sizes
  // This stress test starts with a large amount of data and
  // writes steadily decreases amounts of data to the response stream.
  //
  public boolean doDecrementalResponseData(HttpServletRequest request,
					  HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Decrement is not a valid value.");
	    return false;
	  }

	// do the test
	PrintWriter out = response.getWriter();
	theServlet.displayForm(request, response, null);
	out.println("<hr>");
	for (int i = 0;i < iterations; ++i)
	  {
	    int size = initial - (increment * i);
	    if (size < 0)
	      size = 1;
	    char buffer[] = new char[size];

	    for (int j = 0;j < size; ++j)
	      buffer[j] = 'A';

	    out.println(buffer);
	    out.println("<p>");
	  }

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doDecrementalResponseData: "
			+ e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Cookies
  // This stress test steadily increases the number of cookies
  // from request to request.
  //
  public boolean doIncrementalCookies(HttpServletRequest request,
				      HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// do the test
	PrintWriter out = response.getWriter();
	theServlet.displayForm(request, response, null);
	out.println("<hr>");

	Cookie[] cookies = request.getCookies();
	Cookie c;

	// Get the current iteration which is stored as a cookie
	int currentIteration = 0;
	if (cookies != null)
	  {
	    for (int i = 0;i < cookies.length; ++i)
	      {
		if (cookies[i].getName().equals("iteration"))
		  {
		    String s = cookies[i].getValue();
		    if (s != null)
		      {
			try
			  {
			    currentIteration = Integer.parseInt(s);
			  }
			catch (java.lang.NumberFormatException e)
			  {
			    out.print("ERROR: iteration cookie value is not");
			    out.println(" an integer: " + s);
			    return false;
			  }
		      }
		  }
	      }
	  }

	// increment the iteration and store as cookie
	++currentIteration;
	c = new Cookie("iteration", String.valueOf(currentIteration));
	response.addCookie(c);

	// A set of 20 letters to use as random data
	String letters = "ABCDEFGHIJKLMNOPQRST";
	// A 8 character string to use as the value
	char buffer[] = new char[8];

	// Verify the cookies provided it's not the initial iteration
	if (currentIteration != 1)
	  {
	  }

	// add the initial set of cookies
	for (int i = 0;i < initial; ++i)
	  {
	    char ltr = letters.charAt(i % 20);
	    for (int k = 0;k < 8; ++k)
	      buffer[k] = ltr;
	    String s = "CookieStress" + String.valueOf(i);
	    c = new Cookie(s, new String(buffer));
	    response.addCookie(c);
	  }

	// add additional cookies
	for (int i = 1;i < currentIteration; ++i)
	  {
	    for (int j = 0;j < increment; ++j)
	      {
		char ltr = letters.charAt(j % 20);
		for (int k = 0;k < 8; ++k)
		  buffer[k] = ltr;
		String s = "CookieStress"
		  + String.valueOf(((i - 1) * increment) + j + initial);
		c = new Cookie(s, new String(buffer));
		response.addCookie(c);
	      }
	  }

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doIncrementalCookies: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Cookies
  // This stress test steadily increases the size of the cookie
  // data from request to request.
  //
  public boolean doIncrementalCookieData(HttpServletRequest request,
					 HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// do the test
	PrintWriter out = response.getWriter();
	theServlet.displayForm(request, response, null);
	out.println("<hr>");

	Cookie[] cookies = request.getCookies();
	Cookie c;

	// Get the current iteration which is stored as a cookie
	int currentIteration = 0;
	if (cookies != null)
	  {
	    for (int i = 0;i < cookies.length; ++i)
	      {
		if (cookies[i].getName().equals("iteration"))
		  {
		    String s = cookies[i].getValue();
		    if (s != null)
		      {
			try
			  {
			    currentIteration = Integer.parseInt(s);
			  }
			catch (java.lang.NumberFormatException e)
			  {
			    out.print("ERROR: iteration cookie value is not");
			    out.println(" an integer: " + s);
			    return false;
			  }
		      }
		  }
	      }
	  }

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

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doIncrementalCookieData: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Sessions
  // This stress test steadily increases the number of objects in
  // in the session from request to request.
  //
  public boolean doIncrementalSessionObjects(HttpServletRequest request,
					     HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// do the test
	PrintWriter out = response.getWriter();
	theServlet.displayForm(request, response, null);
	out.println("<hr>");

	// Get the session
	HttpSession aSession = request.getSession(true);

	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	int currentIteration = 0;
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: iteration session object value is not");
		out.println(" an integer: " + cis);
		return false;
	      }
	  }

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
	    String s = "SessionStress" + String.valueOf(i);
	    aSession.setAttribute(s, new String(buffer));
	  }

	// add additional objects
	for (int i = 1;i < currentIteration; ++i)
	  {
	    for (int j = 0;j < increment; ++j)
	      {
		char ltr = letters.charAt(j % 20);
		for (int k = 0;k < 32; ++k)
		  buffer[k] = ltr;
		String s = "SessionStress"
		  + String.valueOf(((i - 1) * increment) + j + initial);
		aSession.setAttribute(s, new String(buffer));
	      }
	  }

	// if it is the last iteration then invalidate the session
	if (currentIteration == iterations)
	  aSession.invalidate();

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doIncrementalSessionObjects: "
			+ e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Sessions
  // This stress test steadily increases the size of the objects in
  // the session from request to request.
  //
  public boolean doIncrementalSessionSize(HttpServletRequest request,
					  HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// do the test
	PrintWriter out = response.getWriter();
	theServlet.displayForm(request, response, null);
	out.println("<hr>");

	// Get the session
	HttpSession aSession = request.getSession(true);

	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	int currentIteration = 0;
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: iteration session object value is not");
		out.println(" an integer: " + cis);
		return false;
	      }
	  }

	// increment the iteration and store in the session
	++currentIteration;
	aSession.setAttribute("Iteration", String.valueOf(currentIteration));

	// A set of 20 letters to use as random data
	String letters = "ABCDEFGHIJKLMNOPQRST";

	// Verify the object provided it's not the initial iteration
	if (currentIteration != 1)
	  {
	  }

	// add the object
	int size = initial + (increment * (currentIteration - 1));
	char buffer[] = new char[size];
	char ltr = letters.charAt(currentIteration % 20);
	for (int j = 0;j < size; ++j)
	  buffer[j] = ltr;
	aSession.setAttribute("SessionStress", new String(buffer));

	// if it is the last iteration then invalidate the session
	if (currentIteration == iterations)
	  aSession.invalidate();

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doIncrementalSessionSize: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Sessions
  // This stress test creates/invalidates the session numerous times
  // within a single request.
  //
  public boolean doSessionLoop(HttpServletRequest request,
			       HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// A set of 20 letters to use as random data
	String letters = "ABCDEFGHIJKLMNOPQRST";
	// A 32 character string to use as the value
	char buffer[] = new char[32];

	// do the test
	PrintWriter out = response.getWriter();
	theServlet.displayForm(request, response, null);
	out.println("<hr>");

	for (int l = 0;l < iterations; ++l)
	  {
	    // Get the session
	    HttpSession aSession = request.getSession(true);

	    if (!aSession.isNew())
	      {
		out.println("ERROR: Session is not new.<p>");
		return false;
	      }

	    // add initial set of some objects
	    for (int i = 0;i < initial; ++i)
	      {
		char ltr = letters.charAt(i % 20);
		for (int k = 0;k < 32; ++k)
		  buffer[k] = ltr;
		String s = "SessionStress" + String.valueOf(i);
		aSession.setAttribute(s, new String(buffer));
	      }

	    // add additional objects
	    for (int i = 1;i < l; ++i)
	      {
		for (int j = 0;j < increment; ++j)
		  {
		    char ltr = letters.charAt(j % 20);
		    for (int k = 0;k < 32; ++k)
		      buffer[k] = ltr;
		    String s = "SessionStress"
		      + String.valueOf(((i - 1) * increment) + j + initial);
		    aSession.setAttribute(s, new String(buffer));
		  }
	      }

	    // invalidate the session
	    aSession.invalidate();
	  }

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doSessionLoop: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Sessions
  // This stress test leaves the session invalidated from request to
  // request to maximize the number of sessions hitting timeout.
  //
  public boolean doSessionTimeout(HttpServletRequest request,
				  HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }

	// A set of 20 letters to use as random data
	String letters = "ABCDEFGHIJKLMNOPQRST";
	// A 32 character string to use as the value
	char buffer[] = new char[32];

	// do the test
	PrintWriter out = response.getWriter();
	theServlet.displayForm(request, response, null);
	out.println("<hr>");

	// Get the session
	HttpSession aSession = request.getSession(true);

	// The session should be new each time
	if (!aSession.isNew())
	  {
	    out.println("ERROR: Session is not new.<p>");
	    return false;
	  }

	// add some objects
	for (int i = 0;i < initial; ++i)
	  {
	    char ltr = letters.charAt(i % 20);
	    for (int k = 0;k < 32; ++k)
	      buffer[k] = ltr;
	    String s = "SessionStress" + String.valueOf(i);
	    aSession.setAttribute(s, new String(buffer));
	  }

	// We explicily don't invalidate the session, so the
	// session should eventually timeout.  New requests all
	// create new sessions.

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doSessionTimeout: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Headers
  // This stress test steadily increases the number of headers
  // from request to request.
  //
  public boolean doIncrementalHeaders(HttpServletRequest request,
				      HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// do the test
	PrintWriter out = response.getWriter();
	theServlet.displayForm(request, response, null);
	out.println("<hr>");

	// Get the session
	HttpSession aSession = request.getSession(true);

	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	int currentIteration = 0;
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: iteration session object value is not");
		out.println(" an integer: " + cis);
		return false;
	      }
	  }

	// increment the iteration and store in the session
	++currentIteration;
	aSession.setAttribute("Iteration", String.valueOf(currentIteration));

	// A set of 20 letters to use as random data
	String letters = "ABCDEFGHIJKLMNOPQRST";
	// A 32 character string to use as the value
	char buffer[] = new char[32];

	// add the initial set of headers
	for (int i = 0;i < initial; ++i)
	  {
	    char ltr = letters.charAt(i % 20);
	    for (int k = 0;k < 32; ++k)
	      buffer[k] = ltr;
	    String s = "HeaderStress" + String.valueOf(i);
	    response.setHeader(s, new String(buffer));
	  }

	// add additional headers
	for (int i = 1;i < currentIteration; ++i)
	  {
	    for (int j = 0;j < increment; ++j)
	      {
		char ltr = letters.charAt(j % 20);
		for (int k = 0;k < 32; ++k)
		  buffer[k] = ltr;
		String s = "HeaderStress"
		  + String.valueOf(((i - 1) * increment) + j + initial);
		response.setHeader(s, new String(buffer));
	      }
	  }

	// if it is the last iteration then invalidate the session
	if (currentIteration == iterations)
	  aSession.invalidate();

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doIncrementalHeaders: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Headers
  // This stress test steadily increases the size of the header data
  // from request to request.
  //
  public boolean doIncrementalHeaderData(HttpServletRequest request,
					 HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// do the test
	PrintWriter out = response.getWriter();
	theServlet.displayForm(request, response, null);
	out.println("<hr>");

	// Get the session
	HttpSession aSession = request.getSession(true);

	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	int currentIteration = 0;
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: iteration session object value is not");
		out.println(" an integer: " + cis);
		return false;
	      }
	  }

	// increment the iteration and store in the session
	++currentIteration;
	aSession.setAttribute("Iteration", String.valueOf(currentIteration));

	// A set of 20 letters to use as random data
	String letters = "ABCDEFGHIJKLMNOPQRST";

	// add the header
	int size = initial + (increment * (currentIteration - 1));
	char buffer[] = new char[size];
	char ltr = letters.charAt(currentIteration % 20);
	for (int j = 0;j < size; ++j)
	  buffer[j] = ltr;
	response.setHeader("HeaderStress", new String(buffer));

	// if it is the last iteration then invalidate the session
	if (currentIteration == iterations)
	  aSession.invalidate();

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doIncrementalHeaderData: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Exceptions
  // This stress test throws constant servlet exceptions back to the
  // servlet engine.
  //
  public boolean doExceptions(HttpServletRequest request,
			      HttpServletResponse response)
    throws ServletException
  {
    boolean status = false;

    throw new ServletException("Exceptions stress test.");
  }

  // Redirects
  // This stress test performs an increasing number of redirects
  // from request to request.
  //
  public boolean doRedirects(HttpServletRequest request,
			     HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// Get the session
	HttpSession aSession = request.getSession(true);

	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	int currentIteration = 0;
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		PrintWriter out = response.getWriter();
		out.print("ERROR: iteration session object value is not");
		out.println(" an integer: " + cis);
		return false;
	      }
	  }

	// Get the current redirect level which is stored in the session
	String rls = (String) aSession.getAttribute("RedirectLevel");
	int redirectLevel = initial + (currentIteration * increment);
	if (rls != null)
	  {
	    try
	      {
		redirectLevel = Integer.parseInt(rls);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		PrintWriter out = response.getWriter();
		out.print("ERROR: redirect level session object value is");
		out.println(" not an integer: " + rls);
		return false;
	      }
	  }

	// decrement the redirect level and store in the session
	--redirectLevel;
	aSession.setAttribute("RedirectLevel", String.valueOf(redirectLevel));

	// do the test
	// If the redirect level has reached zero
	// then we are done with redirects
	if (redirectLevel == 0)
	  {
	    // increment the iteration and store in the session
	    ++currentIteration;
	    aSession.setAttribute("Iteration",
				  String.valueOf(currentIteration));

	    PrintWriter out = response.getWriter();
	    theServlet.displayForm(request, response, null);
	    out.println("<hr>");

	    // if it is the last iteration then invalidate the session
	    if (currentIteration == iterations)
	      aSession.invalidate();
	  }
	else
	  {
	    response.sendRedirect(request.getRequestURI() + "?mode="
				  + theServlet.INC_REDIRECTS + "&iterations="
				  + iterations + "&initial="
				  + initial + "&increment="
				  + increment);
	  }

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doRedirects: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Forwards
  // This stress test performs an increasing number of forwards
  // from request to request.
  //
  public boolean doForwards(HttpServletRequest request,
			    HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// Get the session
	PrintWriter out = response.getWriter();
	HttpSession aSession = request.getSession(true);

	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	int currentIteration = 0;
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: iteration session object value is not");
		out.println(" an integer: " + cis);
		return false;
	      }
	  }

	// Get the current forward level which is stored in the session
	String fls = (String) aSession.getAttribute("ForwardLevel");
	int forwardLevel = initial + (currentIteration * increment);
	if (fls != null)
	  {
	    try
	      {
		forwardLevel = Integer.parseInt(fls);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: forward level session object value is");
		out.println(" not an integer: " + fls);
		return false;
	      }
	  }

	// decrement the forward level and store in the session
	--forwardLevel;
	aSession.setAttribute("ForwardLevel", String.valueOf(forwardLevel));

	// do the test
	// If the forward level has reached zero
	// then we are done with forwards
	if (forwardLevel == 0)
	  {
	    // increment the iteration and store in the session
	    ++currentIteration;
	    aSession.setAttribute("Iteration",
				  String.valueOf(currentIteration));

	    // Remove the forward level from the session so that
	    // it will start over with the next request
	    aSession.removeAttribute("ForwardLevel");

	    theServlet.displayForm(request, response, null);
	    out.println("<hr>");

	    // if it is the last iteration then invalidate the session
	    if (currentIteration == iterations)
	      {
		out.println("Invalidating the session");
		aSession.invalidate();
	      }
	  }
	else
	  {
	    // otherwise perform an forward from this servlet
	    String path = request.getServletPath() + "?mode="
	      + theServlet.INC_FORWARDS + "&iterations="
	      + iterations + "&initial="
	      + initial + "&increment="
	      + increment;
	    RequestDispatcher rd = request
	      .getRequestDispatcher(path);
	    out.println("***** Start Forward: " + forwardLevel);
	    rd.forward(request, response);
	    out.println("***** End Forward: " + forwardLevel);
	  }

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doForwards: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Includes
  // This stress test performs an increasing number of includes
  // from request to request.
  //
  public boolean doIncludes(HttpServletRequest request,
			    HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// Get the session
	PrintWriter out = response.getWriter();
	HttpSession aSession = request.getSession(true);

	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	int currentIteration = 0;
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: iteration session object value is not");
		out.println(" an integer: " + cis);
		return false;
	      }
	  }

	// Get the current include level which is stored in the session
	String ils = (String) aSession.getAttribute("IncludeLevel");
	int includeLevel = initial + (currentIteration * increment);
	if (ils != null)
	  {
	    try
	      {
		includeLevel = Integer.parseInt(ils);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: include level session object value is");
		out.println(" not an integer: " + ils);
		return false;
	      }
	  }

	// decrement the include level and store in the session
	--includeLevel;
	aSession.setAttribute("IncludeLevel", String.valueOf(includeLevel));

	// do the test
	// If the include level has reached zero
	// then we are done with includes
	out.println("***** Start Include: " + includeLevel);
	if (includeLevel == 0)
	  {
	    // increment the iteration and store in the session
	    ++currentIteration;
	    aSession.setAttribute("Iteration",
				  String.valueOf(currentIteration));

	    // Remove the include level from the session so that
	    // it will start over with the next request
	    aSession.removeAttribute("IncludeLevel");

	    theServlet.displayForm(request, response, null);
	    out.println("<hr>");

	    // if it is the last iteration then invalidate the session
	    if (currentIteration == iterations)
	      {
		out.println("Invalidating the session");
		aSession.invalidate();
	      }
	  }
	else
	  {
	    // otherwise perform an include from this servlet
	    String path = request.getServletPath() + "?mode="
	      + theServlet.INC_INCLUDES + "&iterations="
	      + iterations + "&initial="
	      + initial + "&increment="
	      + increment;
	    RequestDispatcher rd = request
	      .getRequestDispatcher(path);
	    out.println("blah blah blah, here is some data");
	    out.println("now go do an include");
	    rd.include(request, response);
	    out.println("okay, the include is done");
	  }
	out.println("***** End Include: " + includeLevel);

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doIncludes: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Includes
  // This stress test steadily increases the size of data from an
  // include from request to request.
  //
  public boolean doIncrementalIncludeData(HttpServletRequest request,
					  HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// Get the session
	PrintWriter out = response.getWriter();
	HttpSession aSession = request.getSession(true);

	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	int currentIteration = 0;
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: iteration session object value is not");
		out.println(" an integer: " + cis);
		return false;
	      }
	  }

	// Check to see if we are in the initial request or in the
	// included request by checking a flag in the session
	String ils = (String) aSession.getAttribute("DoInclude");

	// do the test
	// If the flag is set, then output include data
	// otherwise set the flag and do an include
	if (ils != null)
	  {
	    out.println("***** Start Include");

	    // Remove the include flag from the session so that
	    // it will start over with the next request
	    aSession.removeAttribute("DoInclude");

	    // increment the iteration and store in the session
	    ++currentIteration;
	    aSession.setAttribute("Iteration",
				  String.valueOf(currentIteration));

	    // output the data
	    theServlet.displayForm(request, response, null);
	    out.println("<hr>");
	    for (int i = 0;i < currentIteration; ++i)
	      {
		int size = initial + (increment * i);
		char buffer[] = new char[size];

		for (int j = 0;j < size; ++j)
		  buffer[j] = 'A';

		out.println(buffer);
		out.println("<p>");
	      }

	    // if it is the last iteration then invalidate the session
	    if (currentIteration == iterations)
	      {
		out.println("Invalidating the session");
		aSession.invalidate();
	      }

	    out.println("***** End Include");
	  }
	else
	  {
	    // set the include flag in the session
	    aSession.setAttribute("DoInclude", "yes");

	    // perform an include from this servlet
	    String path = request.getServletPath() + "?mode="
	      + theServlet.INC_INCLUDE_DATA + "&iterations="
	      + iterations + "&initial="
	      + initial + "&increment="
	      + increment;
	    RequestDispatcher rd = request
	      .getRequestDispatcher(path);
	    out.println("blah blah blah, here is some data");
	    out.println("now go do an include");
	    rd.include(request, response);
	    out.println("okay, the include is done");
	  }

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doIncludeData: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Attributes
  // This stress test steadily increases the number of attributes set
  // for a forward from request to request.
  //
  public boolean doForwardAttribute(HttpServletRequest request,
				    HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// Get the session
	HttpSession aSession = request.getSession(true);
	PrintWriter out = response.getWriter();

	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	int currentIteration = 0;
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: iteration session object value is not");
		out.println(" an integer: " + cis);
		return false;
	      }
	  }

	// A set of 20 letters to use as random data
	String letters = "ABCDEFGHIJKLMNOPQRST";
	// A 32 character string to use as the value
	char buffer[] = new char[32];

	// Get the current forward level which is stored in the session
	String fls = (String) aSession.getAttribute("ForwardLevel");

	// do the test
	// If the forward level is defined in the session
	// then we are the forwarded request
	if (fls != null)
	  {
	    // Print out the attributes
	    ServletContext sc = theServlet.getServletContext();
	    Enumeration e = sc.getAttributeNames();
	    while (e.hasMoreElements())
	      {
		String n = (String) e.nextElement();
		Object o = sc.getAttribute(n);
		out.println("Attribute name: " + n
			    + " with value: " + o);
	      }

	    // Verify the attributes we set
	    // initial attributes
	    for (int i = 0;i < initial; ++i)
	      {
		char ltr = letters.charAt(i % 20);
		for (int k = 0;k < 32; ++k)
		  buffer[k] = ltr;
		String expect = new String(buffer);
		String s = "ForwardAttributeStress" + String.valueOf(i);
		String o = (String) sc.getAttribute(s);
		if (o == null)
		  {
		    out.println("Did not find expected attribute name: "
				+ s);
		    status = false;
		  }
		else if (!expect.equals(o))
		  {
		    out.println("Expected Attribute name: " + s
				+ " with value: " + expect
				+ " but instead got value: " + o);
		    status = false;
		  }
	      }
	    // additional attributes
	    for (int i = 1;i < currentIteration; ++i)
	      {
		for (int j = 0;j < increment; ++j)
		  {
		    char ltr = letters.charAt(j % 20);
		    for (int k = 0;k < 32; ++k)
		      buffer[k] = ltr;
		    String expect = new String(buffer);
		    String s = "ForwardAttributeStress"
		      + String.valueOf(((i - 1) * increment) + j + initial);
		    String o = (String) sc.getAttribute(s);
		    if (o == null)
		      {
			out.println("Did not find expected attribute name: "
				    + s);
			status = false;
		      }
		    else if (!expect.equals(o))
		      {
			out.println("Expected Attribute name: " + s
				    + " with value: " + expect
				    + " but instead got value: " + o);
			status = false;
		      }
		  }
	      }

	    // increment the iteration and store in the session
	    ++currentIteration;
	    aSession.setAttribute("Iteration",
				  String.valueOf(currentIteration));

	    // Remove the forward level from the session so that
	    // it will start over with the next request
	    aSession.removeAttribute("ForwardLevel");

	    theServlet.displayForm(request, response, null);
	    out.println("<hr>");

	    // if it is the last iteration then invalidate the session
	    if (currentIteration == iterations)
	      {
		out.println("Invalidating the session");
		aSession.invalidate();
	      }
	  }
	else
	  {
	    // otherwise perform an forward from this servlet
	    // set the forward level so that know we are doing a forward
	    aSession.setAttribute("ForwardLevel", "yes");

	    // Get the servlet context
	    ServletContext sc = theServlet.getServletContext();

	    // add the initial set of attributes
	    for (int i = 0;i < initial; ++i)
	      {
		char ltr = letters.charAt(i % 20);
		for (int k = 0;k < 32; ++k)
		  buffer[k] = ltr;
		String s = "ForwardAttributeStress" + String.valueOf(i);
		sc.setAttribute(s, new String(buffer));
	      }

	    // add additional attributes
	    for (int i = 1;i < currentIteration; ++i)
	      {
		for (int j = 0;j < increment; ++j)
		  {
		    char ltr = letters.charAt(j % 20);
		    for (int k = 0;k < 32; ++k)
		      buffer[k] = ltr;
		    String s = "ForwardAttributeStress"
		      + String.valueOf(((i - 1) * increment) + j + initial);
		    sc.setAttribute(s, new String(buffer));
		  }
	      }

	    // perform the forward
	    String path = request.getServletPath() + "?mode="
	      + theServlet.INC_FORWARD_ATTRIBUTE + "&iterations="
	      + iterations + "&initial="
	      + initial + "&increment="
	      + increment;
	    RequestDispatcher rd = request
	      .getRequestDispatcher(path);
	    out.println("***** Start Forward");
	    rd.forward(request, response);
	    out.println("***** End Forward");
	  }

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doForwardAttribute: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Attributes
  // This stress test steadily increases the size of data for an attribute
  // for a forward from request to request.
  //
  public boolean doForwardAttributeData(HttpServletRequest request,
					HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// Get the session
	HttpSession aSession = request.getSession(true);
	PrintWriter out = response.getWriter();

	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	int currentIteration = 0;
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: iteration session object value is not");
		out.println(" an integer: " + cis);
		return false;
	      }
	  }

	// A set of 20 letters to use as random data
	String letters = "ABCDEFGHIJKLMNOPQRST";

	// Get the current forward level which is stored in the session
	String fls = (String) aSession.getAttribute("ForwardLevel");

	// do the test
	// If the forward level is defined in the session
	// then we are the forwarded request
	if (fls != null)
	  {
	    // Print out the attributes
	    ServletContext sc = theServlet.getServletContext();
	    Enumeration e = sc.getAttributeNames();
	    while (e.hasMoreElements())
	      {
		String n = (String) e.nextElement();
		Object o = sc.getAttribute(n);
		out.println("Attribute name: " + n
			    + " with value: " + o);
	      }

	    // Verify the attributes we set
	    int size = initial + (increment * (currentIteration));
	    char buffer[] = new char[size];
	    char ltr = letters.charAt(currentIteration % 20);
	    for (int j = 0;j < size; ++j)
	      buffer[j] = ltr;
	    String expect = new String(buffer);
	    String s = "ForwardAttributeDataStress"
	      + String.valueOf(currentIteration);
	    String o = (String) sc.getAttribute(s);
	    if (o == null)
	      {
		out.println("Did not find expected attribute name: "
			    + s);
		status = false;
	      }
	    else if (!expect.equals(o))
	      {
		out.println("Expected Attribute name: " + s
			    + " with value: " + expect
			    + " but instead got value: " + o);
		status = false;
	      }

	    // increment the iteration and store in the session
	    ++currentIteration;
	    aSession.setAttribute("Iteration",
				  String.valueOf(currentIteration));

	    // Remove the forward level from the session so that
	    // it will start over with the next request
	    aSession.removeAttribute("ForwardLevel");

	    theServlet.displayForm(request, response, null);
	    out.println("<hr>");

	    // if it is the last iteration then invalidate the session
	    if (currentIteration == iterations)
	      {
		out.println("Invalidating the session");
		aSession.invalidate();
	      }
	  }
	else
	  {
	    // otherwise perform an forward from this servlet
	    // set the forward level so that know we are doing a forward
	    aSession.setAttribute("ForwardLevel", "yes");

	    // Get the servlet context
	    ServletContext sc = theServlet.getServletContext();

	    // add the attribute
	    int size = initial + (increment * (currentIteration));
	    char buffer[] = new char[size];
	    char ltr = letters.charAt(currentIteration % 20);
	    for (int j = 0;j < size; ++j)
	      buffer[j] = ltr;
	    String s = "ForwardAttributeDataStress"
	      + String.valueOf(currentIteration);
	    sc.setAttribute(s, new String(buffer));

	    // perform the forward
	    String path = request.getServletPath() + "?mode="
	      + theServlet.INC_FORWARD_ATTRIBUTE_DATA + "&iterations="
	      + iterations + "&initial="
	      + initial + "&increment="
	      + increment;
	    RequestDispatcher rd = request
	      .getRequestDispatcher(path);
	    out.println("***** Start Forward");
	    rd.forward(request, response);
	    out.println("***** End Forward");
	  }

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doForwardAttributeData: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Attributes
  // This stress test steadily increases the number of attributes set
  // for an include from request to request.
  //
  public boolean doIncludeAttribute(HttpServletRequest request,
				    HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// Get the session
	HttpSession aSession = request.getSession(true);
	PrintWriter out = response.getWriter();

	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	int currentIteration = 0;
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: iteration session object value is not");
		out.println(" an integer: " + cis);
		return false;
	      }
	  }

	// A set of 20 letters to use as random data
	String letters = "ABCDEFGHIJKLMNOPQRST";
	// A 32 character string to use as the value
	char buffer[] = new char[32];

	// Get the current include level which is stored in the session
	String ils = (String) aSession.getAttribute("IncludeLevel");

	// do the test
	// If the include level is defined in the session
	// then we are the included request
	if (ils != null)
	  {
	    // Print out the attributes
	    ServletContext sc = theServlet.getServletContext();
	    Enumeration e = sc.getAttributeNames();
	    while (e.hasMoreElements())
	      {
		String n = (String) e.nextElement();
		Object o = sc.getAttribute(n);
		out.println("Attribute name: " + n
			    + " with value: " + o);
	      }

	    // Verify the attributes we set
	    // initial attributes
	    for (int i = 0;i < initial; ++i)
	      {
		char ltr = letters.charAt(i % 20);
		for (int k = 0;k < 32; ++k)
		  buffer[k] = ltr;
		String expect = new String(buffer);
		String s = "IncludeAttributeStress" + String.valueOf(i);
		String o = (String) sc.getAttribute(s);
		if (o == null)
		  {
		    out.println("Did not find expected attribute name: "
				+ s);
		    status = false;
		  }
		else if (!expect.equals(o))
		  {
		    out.println("Expected Attribute name: " + s
				+ " with value: " + expect
				+ " but instead got value: " + o);
		    status = false;
		  }
	      }
	    // additional attributes
	    for (int i = 1;i < currentIteration; ++i)
	      {
		for (int j = 0;j < increment; ++j)
		  {
		    char ltr = letters.charAt(j % 20);
		    for (int k = 0;k < 32; ++k)
		      buffer[k] = ltr;
		    String expect = new String(buffer);
		    String s = "IncludeAttributeStress"
		      + String.valueOf(((i - 1) * increment) + j + initial);
		    String o = (String) sc.getAttribute(s);
		    if (o == null)
		      {
			out.println("Did not find expected attribute name: "
				    + s);
			status = false;
		      }
		    else if (!expect.equals(o))
		      {
			out.println("Expected Attribute name: " + s
				    + " with value: " + expect
				    + " but instead got value: " + o);
			status = false;
		      }
		  }
	      }

	    // increment the iteration and store in the session
	    ++currentIteration;
	    aSession.setAttribute("Iteration",
				  String.valueOf(currentIteration));

	    // Remove the include level from the session so that
	    // it will start over with the next request
	    aSession.removeAttribute("IncludeLevel");

	    theServlet.displayForm(request, response, null);
	    out.println("<hr>");

	    // if it is the last iteration then invalidate the session
	    if (currentIteration == iterations)
	      {
		out.println("Invalidating the session");
		aSession.invalidate();
	      }
	  }
	else
	  {
	    // otherwise perform an include from this servlet
	    // set the include level so that know we are doing a include
	    aSession.setAttribute("IncludeLevel", "yes");

	    // Get the servlet context
	    ServletContext sc = theServlet.getServletContext();

	    // add the initial set of attributes
	    for (int i = 0;i < initial; ++i)
	      {
		char ltr = letters.charAt(i % 20);
		for (int k = 0;k < 32; ++k)
		  buffer[k] = ltr;
		String s = "IncludeAttributeStress" + String.valueOf(i);
		sc.setAttribute(s, new String(buffer));
	      }

	    // add additional attributes
	    for (int i = 1;i < currentIteration; ++i)
	      {
		for (int j = 0;j < increment; ++j)
		  {
		    char ltr = letters.charAt(j % 20);
		    for (int k = 0;k < 32; ++k)
		      buffer[k] = ltr;
		    String s = "IncludeAttributeStress"
		      + String.valueOf(((i - 1) * increment) + j + initial);
		    sc.setAttribute(s, new String(buffer));
		  }
	      }

	    // perform the include
	    String path = request.getServletPath() + "?mode="
	      + theServlet.INC_INCLUDE_ATTRIBUTE + "&iterations="
	      + iterations + "&initial="
	      + initial + "&increment="
	      + increment;
	    RequestDispatcher rd = request
	      .getRequestDispatcher(path);
	    out.println("***** Start Include");
	    rd.include(request, response);
	    out.println("***** End Include");
	  }

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doIncludeAttribute: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }

  // Attributes
  // This stress test steadily increases the size of data for an attribute
  // for an include from request to request.
  //
  public boolean doIncludeAttributeData(HttpServletRequest request,
					HttpServletResponse response)
  {
    boolean status = false;

    try
      {
	// get the test parameters
	int iterations = theServlet.getIterations(request);
	if (iterations < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Iterations is not a valid value.");
	    return false;
	  }
	int initial = theServlet.getInitialSize(request);
	if (initial < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Initial Size is not a valid value.");
	    return false;
	  }
	int increment = theServlet.getIncrement(request);
	if (increment < 0)
	  {
	    theServlet.displayForm(request, response,
				   "Size Increment is not a valid value.");
	    return false;
	  }

	// Get the session
	HttpSession aSession = request.getSession(true);
	PrintWriter out = response.getWriter();

	// Get the current iteration which is stored in the session
	String cis = (String) aSession.getAttribute("Iteration");
	int currentIteration = 0;
	if (cis != null)
	  {
	    try
	      {
		currentIteration = Integer.parseInt(cis);
	      }
	    catch (java.lang.NumberFormatException e)
	      {
		out.print("ERROR: iteration session object value is not");
		out.println(" an integer: " + cis);
		return false;
	      }
	  }

	// A set of 20 letters to use as random data
	String letters = "ABCDEFGHIJKLMNOPQRST";

	// Get the current include level which is stored in the session
	String ils = (String) aSession.getAttribute("IncludeLevel");

	// do the test
	// If the include level is defined in the session
	// then we are the included request
	if (ils != null)
	  {
	    // Print out the attributes
	    ServletContext sc = theServlet.getServletContext();
	    Enumeration e = sc.getAttributeNames();
	    while (e.hasMoreElements())
	      {
		String n = (String) e.nextElement();
		Object o = sc.getAttribute(n);
		out.println("Attribute name: " + n
			    + " with value: " + o);
	      }

	    // Verify the attributes we set
	    int size = initial + (increment * (currentIteration));
	    char buffer[] = new char[size];
	    char ltr = letters.charAt(currentIteration % 20);
	    for (int j = 0;j < size; ++j)
	      buffer[j] = ltr;
	    String expect = new String(buffer);
	    String s = "IncludeAttributeDataStress"
	      + String.valueOf(currentIteration);
	    String o = (String) sc.getAttribute(s);
	    if (o == null)
	      {
		out.println("Did not find expected attribute name: "
			    + s);
		status = false;
	      }
	    else if (!expect.equals(o))
	      {
		out.println("Expected Attribute name: " + s
			    + " with value: " + expect
			    + " but instead got value: " + o);
		status = false;
	      }

	    // increment the iteration and store in the session
	    ++currentIteration;
	    aSession.setAttribute("Iteration",
				  String.valueOf(currentIteration));

	    // Remove the include level from the session so that
	    // it will start over with the next request
	    aSession.removeAttribute("IncludeLevel");

	    theServlet.displayForm(request, response, null);
	    out.println("<hr>");

	    // if it is the last iteration then invalidate the session
	    if (currentIteration == iterations)
	      {
		out.println("Invalidating the session");
		aSession.invalidate();
	      }
	  }
	else
	  {
	    // otherwise perform an include from this servlet
	    // set the include level so that know we are doing a include
	    aSession.setAttribute("IncludeLevel", "yes");

	    // Get the servlet context
	    ServletContext sc = theServlet.getServletContext();

	    // add the attribute
	    int size = initial + (increment * (currentIteration));
	    char buffer[] = new char[size];
	    char ltr = letters.charAt(currentIteration % 20);
	    for (int j = 0;j < size; ++j)
	      buffer[j] = ltr;
	    String s = "IncludeAttributeDataStress"
	      + String.valueOf(currentIteration);
	    sc.setAttribute(s, new String(buffer));

	    // perform the include
	    String path = request.getServletPath() + "?mode="
	      + theServlet.INC_INCLUDE_ATTRIBUTE_DATA + "&iterations="
	      + iterations + "&initial="
	      + initial + "&increment="
	      + increment;
	    RequestDispatcher rd = request
	      .getRequestDispatcher(path);
	    out.println("***** Start Include");
	    rd.include(request, response);
	    out.println("***** End Include");
	  }

	status = true;
      }
    catch (Exception e)
      {
	try
	  {
	    PrintWriter out = response.getWriter();
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    out.println("ERROR: Exception in doIncludeAttributeData: " + e);
	    out.println(sw.toString());
	  }
	catch (Exception ne)
	  { /* not much we can do here */ }
	status = false;
      }

    return status;
  }
}
