/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.ip.mapping;

import com.anosym.utilities.geocode.CountryCode;
import com.anosym.utilities.geocode.CountryIpMappings;
import com.anosym.vjax.VMarshaller;
import com.anosym.vjax.VXMLBindingException;
import com.anosym.vjax.xml.VDocument;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author marembo
 */
public class IpMappingTest extends TestCase {

  public IpMappingTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testCanLoad() {
    try {
      CountryIpMappings code = IpMapping.getCountryIpMappings(1, 112);
      VMarshaller<CountryIpMappings> m = new VMarshaller<CountryIpMappings>();
      VDocument d = m.marshallDocument(code);
      System.out.println(d.toXmlString());
    } catch (VXMLBindingException ex) {
      Logger.getLogger(IpMappingTest.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void test212_49_88_0_to_212_49_88_255_range() {
    String ip = "212.49.88.104";
    CountryCode cc = IpMapping.findCountryCodeFromIpAddress(ip);
    assertNotNull(cc);
    assertEquals("KE", cc.getIsoCode());
  }
}
