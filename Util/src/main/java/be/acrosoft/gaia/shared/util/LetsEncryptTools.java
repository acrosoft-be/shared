/**
 * Copyright Acropolis Software SPRL (https://www.acrosoft.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.acrosoft.gaia.shared.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Provides utilities to deal with Let's Encrypt protected hosts.
 */
public class LetsEncryptTools
{
  private static final Logger LOGGER=Logger.getLogger(LetsEncryptTools.class.getName());

  static String pathToPem="be/acrosoft/gaia/shared/util/letsencrypt.pem"; //$NON-NLS-1$
  
  private static class LETrustManager implements X509TrustManager
  {
    private List<X509Certificate> _roots;
    private CertificateFactory _factory;
    private CertPathValidator _validator;
    private Set<TrustAnchor> _anchors;
    private Set<X509Certificate> _rootSet;
    
    public LETrustManager() throws GeneralSecurityException
    {
      _validator=CertPathValidator.getInstance(CertPathValidator.getDefaultType());
      _roots=new ArrayList<>();
      _factory=CertificateFactory.getInstance("X.509"); //$NON-NLS-1$
      InputStream is=getClass().getClassLoader().getResourceAsStream(pathToPem);
      if(is==null)
        throw new GeneralSecurityException("Failed to load root PEM at "+pathToPem); //$NON-NLS-1$
      _roots=Arrays.asList(_factory.generateCertificates(is).toArray(new X509Certificate[] {}));
      _anchors=new HashSet<>();
      _rootSet=new HashSet<>();
      
      for(X509Certificate root:_roots)
      {
        _anchors.add(new TrustAnchor(root,null));
        _rootSet.add(root);
      }
    }
    
    @Override
    public synchronized void checkClientTrusted(X509Certificate[] certificates,String authType) throws CertificateException
    {
      throw new CertificateException("Unsupported operation"); //$NON-NLS-1$
    }

    @Override
    public synchronized void checkServerTrusted(X509Certificate[] certificates,String authType) throws CertificateException
    {
      try
      {
        List<Certificate> toCheck=new ArrayList<>();
        for(X509Certificate cert:certificates)
        {
          if(!_roots.contains(cert)) toCheck.add(cert);
        }
        if(toCheck.size()==0) throw new CertificateException("No certificate provided by peer"); //$NON-NLS-1$

        CertPath path = _factory.generateCertPath(toCheck);
        
        PKIXParameters params=new PKIXParameters(_anchors);
        //We're affected by JDK-8132926
        params.setRevocationEnabled(false);
        _validator.validate(path,params);
      }
      catch(GeneralSecurityException ex)
      {
        throw new CertificateException(ex);
      }
    }

    @Override
    public synchronized X509Certificate[] getAcceptedIssuers()
    {
      return _roots.toArray(new X509Certificate[] {});
    }
  }
  
  /**
   * Get an SSL socket factory to establish a connection to a server that is secured using a Let's Encrypt
   * certificate.
   * @return SSLSocketFactory compatible with Let's Encrypt certificate.
   * @throws GeneralSecurityException in case of trouble.
   */
  public static SSLSocketFactory getSSLSocketFactory() throws GeneralSecurityException
  {
    SSLContext context=SSLContext.getInstance("TLS"); //$NON-NLS-1$

    TrustManager tm = new LETrustManager();
    
    context.init(new KeyManager[] {},new TrustManager[] {tm},new SecureRandom());
    return context.getSocketFactory();
  }
  
  /**
   * Open an URL connection to the given URL secured by a Let's Encrypt certificate.
   * @param url URL.
   * @return URLConnection.
   * @throws IOException in case of error.
   */
  public static URLConnection openConnection(URL url) throws IOException
  {
    URLConnection connection=url.openConnection();
    if(!(connection instanceof HttpsURLConnection)) return connection;

    try
    {
      HttpsURLConnection scon=(HttpsURLConnection)connection;
      scon.setSSLSocketFactory(getSSLSocketFactory());
      scon.connect();
      return scon;
    }
    catch(GeneralSecurityException | SSLException ex)
    {
      //Too bad, ignore...
      LOGGER.log(Level.WARNING,"Failed to open "+url,ex); //$NON-NLS-1$
      return url.openConnection();
    }
  }
}
