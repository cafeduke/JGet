package com.github.cafeduke.jget;

import javax.net.ssl.HostnameVerifier;

/**
 * Allow all SSL Verifier
 * 
 * @author Raghunandan.Seshadri
 */
public class SSLVerifier implements HostnameVerifier
{
    public boolean verify(String hostname, javax.net.ssl.SSLSession session)
    {
        return true;
    }
}
