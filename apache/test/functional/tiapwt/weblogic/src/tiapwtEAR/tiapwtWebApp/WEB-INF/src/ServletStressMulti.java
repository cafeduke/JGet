/*
   ServletStressMulti.java

   Test for stressing the Java Servlet engine and surrounding
   application server.  Multiple thread model.

   Copyright (c) 2000 Oracle Corporation
   Author: Scott Christley

*/

import java.io.*;
import javax.servlet.*;
import java.sql.*;
import java.util.*;
import javax.servlet.http.*;

public class ServletStressMulti extends HttpServlet 
{
  // object which performs the actual tests
  ServletStressTests testInstance = null;

  // Operational modes
  static final int INC_RESP_DATA = 1;
  static final int DEC_RESP_DATA = 2;
  static final int INC_COOKIES = 3;
  static final int INC_COOKIE_DATA = 4;
  static final int INC_SESSION_OBJS = 5;
  static final int INC_SESSION_DATA = 6;
  static final int SESSION_LOOP = 7;
  static final int SESSION_TIMEOUT = 8;
  static final int INC_HEADERS = 9;
  static final int INC_HEADER_DATA = 10;
  static final int EXCEPTIONS = 11;
  static final int INC_REDIRECTS = 12;
  static final int INC_FORWARDS = 13;
  static final int INC_INCLUDES = 14;
  static final int INC_INCLUDE_DATA = 15;
  static final int INC_FORWARD_ATTRIBUTE = 16;
  static final int INC_FORWARD_ATTRIBUTE_DATA = 17;
  static final int INC_INCLUDE_ATTRIBUTE = 18;
  static final int INC_INCLUDE_ATTRIBUTE_DATA = 19;

  public void doGet (HttpServletRequest request,
		     HttpServletResponse response)
    throws ServletException, IOException
  {
    int mode = 0;
    boolean status = false;

    if (testInstance == null)
      testInstance = new ServletStressTests(this);

    response.setContentType("text/html");
    String modeString = request.getParameter("mode");
    if (modeString == null)
      {
	displayForm(request, response, null);
      }
    else
      {
	try
	  {
	    mode = Integer.parseInt(modeString);
	    status = true;
	  }
	catch (java.lang.NumberFormatException e)
	  {
	    displayForm(request, response,
			"Operational Mode is not a valid integer value.");
	    status = false;
	  }

	if (status)
	  {
	    switch (mode)
	      {
	      case INC_RESP_DATA:
		status = testInstance
		  .doIncrementalResponseData(request, response);
		break;
	      case DEC_RESP_DATA:
		status = testInstance
		  .doDecrementalResponseData(request, response);
		break;
	      case INC_COOKIES:
		status = testInstance
		  .doIncrementalCookies(request, response);
		break;
	      case INC_COOKIE_DATA:
		status = testInstance
		  .doIncrementalCookieData(request, response);
		break;
	      case INC_SESSION_OBJS:
		status = testInstance
		  .doIncrementalSessionObjects(request, response);
		break;
	      case INC_SESSION_DATA:
		status = testInstance
		  .doIncrementalSessionSize(request, response);
		break;
	      case SESSION_LOOP:
		status = testInstance
		  .doSessionLoop(request, response);
		break;
	      case SESSION_TIMEOUT:
		status = testInstance
		  .doSessionTimeout(request, response);
		break;
	      case INC_HEADERS:
		status = testInstance
		  .doIncrementalHeaders(request, response);
		break;
	      case INC_HEADER_DATA:
		status = testInstance
		  .doIncrementalHeaderData(request, response);
		break;
	      case EXCEPTIONS:
		status = testInstance
		  .doExceptions(request, response);
		break;
	      case INC_REDIRECTS:
		status = testInstance
		  .doRedirects(request, response);
		break;
	      case INC_FORWARDS:
		status = testInstance
		  .doForwards(request, response);
		break;
	      case INC_INCLUDES:
		status = testInstance
		  .doIncludes(request, response);
		break;
	      case INC_INCLUDE_DATA:
		status = testInstance
		  .doIncrementalIncludeData(request, response);
		break;
	      case INC_FORWARD_ATTRIBUTE:
		status = testInstance
		  .doForwardAttribute(request, response);
		break;
	      case INC_FORWARD_ATTRIBUTE_DATA:
		status = testInstance
		  .doForwardAttributeData(request, response);
		break;
	      case INC_INCLUDE_ATTRIBUTE:
		status = testInstance
		  .doIncludeAttribute(request, response);
		break;
	      case INC_INCLUDE_ATTRIBUTE_DATA:
		status = testInstance
		  .doIncludeAttributeData(request, response);
		break;
	      default:
		displayForm(request, response,
			    "Operational Mode is not a valid value.");
		status = false;
		break;
	      }
	  }
      }

    PrintWriter out = response.getWriter();
    out.println("<hr>");
    if (status)
      out.println("test PASSED.");
    else
      out.println("test FAILED.");
  }

  // Display test form
  // Display an HTML form which provides the test parameters
  public void displayForm(HttpServletRequest request,
			  HttpServletResponse response,
			  String message)
    throws ServletException, IOException
  {
    PrintWriter out = response.getWriter();
    String s;
    int mode = 1;

    // get the mode
    String modeString = request.getParameter("mode");
    if (modeString != null)
      {
	try
	  {
	    mode = Integer.parseInt(modeString);
	  }
	catch (java.lang.NumberFormatException e)
	  {
	    mode = 1;
	  }
      }

    out.println("<form method=GET action=\"" + this.getClass().getName()
		+ "\">");
    out.println("Operational Mode:");
    out.println("<select name=mode>");
    if (mode == INC_RESP_DATA)
      out.println("<option value=1 selected>Incremental Response Data");
    else
      out.println("<option value=1>Incremental Response Data");
    if (mode == DEC_RESP_DATA)
      out.println("<option value=2 selected>Decremental Response Data");
    else
      out.println("<option value=2>Decremental Response Data");
    if (mode == INC_COOKIES)
      out.println("<option value=3 selected>Incremental Cookies");
    else
      out.println("<option value=3>Incremental Cookies");
    if (mode == INC_COOKIE_DATA)
      out.println("<option value=4 selected>Incremental Cookie Data");
    else
      out.println("<option value=4>Incremental Cookie Data");
    if (mode == INC_SESSION_OBJS)
      out.println("<option value=5 selected>Incremental Session Objects");
    else
      out.println("<option value=5>Incremental Session Objects");
    if (mode == INC_SESSION_DATA)
      out.println("<option value=6 selected>Incremental Session Object Data");
    else
      out.println("<option value=6>Incremental Session Object Data");
    if (mode == SESSION_LOOP)
      out.println("<option value=7 selected>Session Create/Invalidate Loop");
    else
      out.println("<option value=7>Session Create/Invalidate Loop");
    if (mode == SESSION_TIMEOUT)
      out.println("<option value=8 selected>Session Timeout");
    else
      out.println("<option value=8>Session Timeout");
    if (mode == INC_HEADERS)
      out.println("<option value=9 selected>Incremental Headers");
    else
      out.println("<option value=9>Incremental Headers");
    if (mode == INC_HEADER_DATA)
      out.println("<option value=10 selected>Incremental Header Data");
    else
      out.println("<option value=10>Incremental Header Data");
    if (mode == EXCEPTIONS)
      out.println("<option value=11 selected>Exceptions");
    else
      out.println("<option value=11>Exceptions");
    if (mode == INC_REDIRECTS)
      out.println("<option value=12 selected>Incremental Redirects");
    else
      out.println("<option value=12>Incremental Redirects");
    if (mode == INC_FORWARDS)
      out.println("<option value=13 selected>Incremental Forwards");
    else
      out.println("<option value=13>Incremental Forwards");
    if (mode == INC_INCLUDES)
      out.println("<option value=14 selected>Incremental Includes");
    else
      out.println("<option value=14>Incremental Includes");
    if (mode == INC_INCLUDE_DATA)
      out.println("<option value=15 selected>Incremental Include Data");
    else
      out.println("<option value=15>Incremental Include Data");
    if (mode == INC_FORWARD_ATTRIBUTE)
      out.println("<option value=16 selected>Incremental Forward Attributes");
    else
      out.println("<option value=16>Incremental Forward Attributes");
    if (mode == INC_FORWARD_ATTRIBUTE_DATA)
      out.println("<option value=17 selected>Incremental Forward Attribute Data");
    else
      out.println("<option value=17>Incremental Forward Attribute Data");
    if (mode == INC_INCLUDE_ATTRIBUTE)
      out.println("<option value=18 selected>Incremental Include Attributes");
    else
      out.println("<option value=18>Incremental Include Attributes");
    if (mode == INC_INCLUDE_ATTRIBUTE_DATA)
      out.println("<option value=19 selected>Incremental Include Attribute Data");
    else
      out.println("<option value=19>Incremental Include Attribute Data");
    out.println("</select>");
    out.println("<p>Iterations:");
    s = request.getParameter("iterations");
    if (s == null)
      out.println("<input type=text name=iterations>");
    else
      {
	out.print("<input type=text name=iterations");
	out.println(" value=\"" + s + "\">");
      }
    out.println("<p>Initial Size:");
    s = request.getParameter("initial");
    if (s == null)
      out.println("<input type=text name=initial>");
    else
      {
	out.print("<input type=text name=initial");
	out.println(" value=\"" + s + "\">");
      }
    out.println("<p>Size Increment/Decrement:");
    s = request.getParameter("increment");
    if (s == null)
      out.println("<input type=text name=increment>");
    else
      {
	out.print("<input type=text name=increment");
	out.println(" value=\"" + s + "\">");
      }
    out.println("<p><input type=submit value=\"Run Test\">");
    out.println("</form>");

    // Display message if provided
    if (message != null)
      out.println("<hr><h2>" + message + "</h2>");
  }

  // Get iterations
  // Parse the iterations parameter
  public int getIterations(HttpServletRequest request)
  {
    int iterations = -1;

    String s = request.getParameter("iterations");
    if (s == null)
      iterations = -1;
    else
      {
	try
	  {
	    iterations = Integer.parseInt(s);
	  }
	catch (java.lang.NumberFormatException e)
	  {
	    iterations = -1;
	  }
      }

    return iterations;
  }

  // Get initial size
  // Parse the initial parameter
  public int getInitialSize(HttpServletRequest request)
  {
    int initial = -1;

    String s = request.getParameter("initial");
    if (s == null)
      initial = -1;
    else
      {
	try
	  {
	    initial = Integer.parseInt(s);
	  }
	catch (java.lang.NumberFormatException e)
	  {
	    initial = -1;
	  }
      }

    return initial;
  }

  // Get size increment
  // Parse the increment parameter
  public int getIncrement(HttpServletRequest request)
  {
    int increment = -1;

    String s = request.getParameter("increment");
    if (s == null)
      increment = -1;
    else
      {
	try
	  {
	    increment = Integer.parseInt(s);
	  }
	catch (java.lang.NumberFormatException e)
	  {
	    increment = -1;
	  }
      }

    return increment;
  }

}
