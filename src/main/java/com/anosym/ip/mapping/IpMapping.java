/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.ip.mapping;

import com.anosym.utilities.Utility;
import com.anosym.utilities.geocode.CountryCode;
import com.anosym.utilities.geocode.CountryIpMapping;
import com.anosym.utilities.geocode.CountryIpMappings;
import com.anosym.utilities.geocode.Ipv4;
import com.anosym.vjax.VMarshaller;
import com.anosym.vjax.VXMLBindingException;
import com.anosym.vjax.xml.VDocument;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;

/**
 *
 * @author marembo
 */
public class IpMapping {

  private static InputStream getIpMappingFile(int firstOctet, int secondOctet) {
    return IpMapping.class.getResourceAsStream("/" + firstOctet + "/" + secondOctet + ".xml");
  }

  public static CountryIpMappings getCountryIpMappings(int firstOctet, int secondOctet) {
    try {
      InputStream ipStream = getIpMappingFile(firstOctet, secondOctet);
      if (ipStream == null) {
        return null;
      }
      VDocument doc = VDocument.parseDocument(ipStream);
      return new VMarshaller<CountryIpMappings>().unmarshall(doc);
    } catch (VXMLBindingException ex) {
      Logger.getLogger(IpMapping.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static CountryCode findCountryCodeFromIpAddress(String ipAddress) {
    Ipv4 ip = new Ipv4(ipAddress);
    return findCountryCodeFromIpAddress(ip);
  }

  private static CountryCode searchCountryCodeFromIp(Ipv4 ip, int firstOctet, int fromSecondOctet, int toSecondOctet) {
    if (fromSecondOctet > toSecondOctet) {
      return null;
    }
    CountryIpMappings cims = getCountryIpMappings(firstOctet, fromSecondOctet);
    if (cims != null) {
      CountryIpMapping cim = cims.findCountryIpMapping(ip);
      if (cim != null) {
        return cim.getCountryCode();
      }
    }
    return searchCountryCodeFromIp(ip, firstOctet, ++fromSecondOctet, toSecondOctet);
  }

  public static CountryCode findCountryCodeFromIpAddress(Ipv4 ip) {
    int initialSecondOctet = 0;
    int lastSecondOctet = 255;
    int secondOctet = ip.getSecondOctet();
    CountryCode countryCode = searchCountryCodeFromIp(ip, ip.getFirstOctet(), initialSecondOctet, secondOctet);
    if (countryCode == null) {
      return searchCountryCodeFromIp(ip, ip.getFirstOctet(), secondOctet + 1, lastSecondOctet);
    } else {
      return countryCode;
    }
  }

  public static void runIpMapping(Ipv4 ip) {
    try {
      String url = "http://freegeoip.net/xml/" + ip.toString();
      HtmlCleaner htmlCleaner = new HtmlCleaner();
      CleanerProperties cp = htmlCleaner.getProperties();
      String xml = IOUtils.toString(new URI(url).toURL()
              .openConnection().getInputStream());
      //      new SimpleXmlSerializer(cp).writeToStream(node, System.out);
      VDocument html = VDocument.parseDocumentFromString(xml);
      String isoCode = html.getRootElement().findChild("CountryCode").toContent();
      CountryCode cc = Utility.findCountryCodeFromCountryIsoCode(isoCode);
      System.out.println(cc);
    } catch (IOException ex) {
      Logger.getLogger(IpMapping.class.getName()).log(Level.SEVERE, null, ex);
    } catch (URISyntaxException ex) {
      Logger.getLogger(IpMapping.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
