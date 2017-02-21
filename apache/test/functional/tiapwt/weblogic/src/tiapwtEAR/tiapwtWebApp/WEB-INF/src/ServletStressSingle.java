/*
   ServletStressSingle.java

   Test for stressing the Java Servlet engine and surrounding
   application server.  Single thread model.

   Copyright (c) 2000 Oracle Corporation
   Author: Scott Christley

*/

import java.io.*;
import javax.servlet.*;
import java.sql.*;
import java.util.*;
import javax.servlet.http.*;

public class ServletStressSingle extends ServletStressMulti
implements SingleThreadModel
{
}
