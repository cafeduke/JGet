package com.github.cafeduke;

import javax.net.ssl.HostnameVerifier;

/**
 * @author Raghunandan.Seshadri
 */
public class SSLVerifier implements HostnameVerifier
{
    public boolean verify(String hostname, javax.net.ssl.SSLSession session)
    {
        return true;
    }
}
