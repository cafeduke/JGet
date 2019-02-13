<!--
   incrementalResponseData.jsp

   Response data sizes
   This stress test writes increasing larger amounts of data
   to the response stream.

   Copyright (c) 2003 Oracle Corporation
   Author: Scott Christley

-->

<html>
<title>Incremental Response Data</title>
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
    if (!status)
%>
test FAILED.
<%
    else
    {
       for (int i = 0;i < iterations; ++i)
       {
          int size = initial + (increment * i);
          char buffer[] = new char[size];

          for (int j = 0;j < size; ++j)
             buffer[j] = 'A';
%>
<%= buffer %>
<p>
<%
       }
%>
test PASSED.
<%
    }
%>

</body>
</html>
