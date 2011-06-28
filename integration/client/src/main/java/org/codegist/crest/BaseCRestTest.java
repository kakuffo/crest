/*
 * Copyright 2010 CodeGist.org
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *  ===================================================================
 *
 *  More information at http://www.codegist.org.
 */

package org.codegist.crest;

import org.codegist.common.net.Urls;
import org.codegist.crest.model.BunchOfData;
import org.codegist.crest.model.Data;
import org.codegist.crest.model.Serializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public abstract class BaseCRestTest<T> {
    public static final String DATE_FORMAT = "dd/MM/yyyy @ HH:mm:ssZ";
    public static final String ENCODING = "UTF-8";

    private static final String TEST_SERVER = System.getProperty("crest.server.end-point", "http://localhost:8080");
    private static final boolean TEST_JAXB = Boolean.valueOf(System.getProperty("crest.test.jaxb", "true"));
    private static final String TEST_TMP_DIR = System.getProperty("crest.test.tmp-dir", null);



    private static final Map<String,Object> DEFAULT_PROPERTIES = new HashMap<String, Object>(){{
        put(CRestProperty.CREST_DATE_FORMAT, DATE_FORMAT);
        put(CRestProperty.CREST_BOOLEAN_FALSE, "myFalse");
        put(CRestProperty.CREST_BOOLEAN_TRUE, "myTrue");
        put("crest.encoding", ENCODING);
        put("encoding.header", ENCODING);
        put(Serializer.class.getName(), Serializer.JACKSON);
    }};

    // TODO this is to handle the fact we CANNOT override the way jaxb serialize types!
    private static final Map<String,Object> JAXB_SPECIFIC_PROPERTIES = new HashMap<String, Object>(){{
        put(CRestProperty.CREST_DATE_FORMAT, "yyyy-MM-dd'T'HH:mm:ss+00:00");
        put(CRestProperty.CREST_BOOLEAN_TRUE, "true");
        put(CRestProperty.CREST_BOOLEAN_FALSE, "false");
        put(Serializer.class.getName(), Serializer.JAXB);
    }};
    private static final Map<String,Object> SIMPLEXML_SPECIFIC_PROPERTIES = new HashMap<String, Object>(){{
        put(Serializer.class.getName(), Serializer.SIMPLEXML);
    }};

    // TODO for some reason HttpUrlConnection header encoding always encode in ISO-8859-1
    private static final Map<String,Object> HTTP_URL_CONNECTION = new HashMap<String, Object>(){{
        put("encoding.header", System.getProperty("crest.test.http-url-connection.encoding.header", "ISO-8859-1"));
    }};

    protected final T toTest;
    private final Map<String,Object> testProps;

    public BaseCRestTest(CRestHolder holder, Class<T> service) {
        this.toTest = holder.crest.build(service);
        this.testProps = holder.properties;
    }


    public String getEffectiveDateFormat(){
        return (String)testProps.get(CRestProperty.CREST_DATE_FORMAT);
    }
    public String getEffectiveBooleanTrue(){
        return (String)testProps.get(CRestProperty.CREST_BOOLEAN_TRUE);
    }
    public String getEffectiveBooleanFalse(){
        return (String)testProps.get(CRestProperty.CREST_BOOLEAN_FALSE);
    }
    public File getTempDir(){
        return TEST_TMP_DIR != null ? new File(TEST_TMP_DIR) : null;
    }

    public String encodeHeader(String header) throws UnsupportedEncodingException {
        return new String(header.getBytes(ENCODING), (String) testProps.get("encoding.header"));
    }


    public <D> BunchOfData<D> newBunchOfData(Date val1, Boolean val2, D val3){
        return BunchOfData.create((Serializer)testProps.get(Serializer.class.getName()), val1, val2, val3);
    }
    public Data newData(int val1, String val2){
        return Data.create((Serializer)testProps.get(Serializer.class.getName()), val1, val2);
    }



    public String toString(Date date) {
        return new SimpleDateFormat(getEffectiveDateFormat()).format(date);
    }
    public String toString(boolean val){
        return val ? getEffectiveBooleanTrue() : getEffectiveBooleanFalse();
    }

    public static Date date(String date, String format) {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String url(String value) throws UnsupportedEncodingException {
        return Urls.encode(value, ENCODING);
    }

    public static String json(String value) {

        try {
            return new ObjectMapper().writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String xml(String value) {
        return value.replace("&", "&amp;");
    }

    public static void assertXMLEqual(String control, String actual) {
        try {
            XMLUnit.setIgnoreWhitespace(true);
            Diff diff = XMLUnit.compareXML(control, actual);
            assertTrue(diff.toString(), diff.similar());
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // this represent the real root builder shared by all tests
    private static CRestBuilder baseBuilder() {
        return  new CRestBuilder()
                .setConfigPlaceholder("crest.server.end-point", TEST_SERVER + "/crest-server")
                .addProperties(DEFAULT_PROPERTIES)
                .bindPlainTextDeserializerWith("text/html");
    }

    // these represents the common permutations all test will pass
    private static CRestBuilder[] baseBuilders() {
        return new CRestBuilder[] {
                baseBuilder(),
                baseBuilder().authenticatesWithOAuth( "ConsumerKey","ConsumerSecret","AccessToken","AccessTokenSecret"),
                baseBuilder().authenticatesWithBasic("My UserName", "My P@Sww0rd")
        };
    }
    public static List<CRestHolder> forEach(CRestBuilder[] builders, Builder builder){
        List<CRestHolder> holders = new ArrayList<CRestHolder>();
        for(CRestBuilder b : builders){
            holders.add(builder.build(b));
        }
        return holders;
    }
    public static List<CRestHolder> forEachBaseBuilder(Builder builder){
        return forEach(baseBuilders(), builder);
    }
    public static CRestHolder[] arrify(List<CRestHolder> list){
        return list.toArray(new CRestHolder[list.size()]);
    }

    public static interface Builder {
        CRestHolder build(CRestBuilder builder);
    }

    public static List<CRestHolder> byDefault() {
        return forEachBaseBuilder(new Builder() {
            public CRestHolder build(CRestBuilder builder) {
                return new CRestHolder(builder.build());
            }
        });
    }

    public static CRestHolder[] byRestServices() {
        List<CRestHolder> holders = new ArrayList<CRestHolder>();
        holders.addAll(byDefault());
        holders.addAll(forEachBaseBuilder(new Builder() {
            public CRestHolder build(CRestBuilder builder) {
                return new CRestHolder(builder.useHttpClientRestService().build());
            }
        }));
        return arrify(holders);
    }

    public static CRestHolder[] byRestServicesForHeaders() {
        List<CRestHolder> holders = new ArrayList<CRestHolder>();
        holders.addAll(forEachBaseBuilder(new Builder() {
            public CRestHolder build(CRestBuilder builder) {
                return new CRestHolder(builder.build(), HTTP_URL_CONNECTION);
            }
        }));
        holders.addAll(forEachBaseBuilder(new Builder() {
            public CRestHolder build(CRestBuilder builder) {
                return new CRestHolder(builder.useHttpClientRestService().build());
            }
        }));
        return arrify(holders);
    }

    public static CRestHolder[] byXmlSerializers() {
        List<CRestHolder> holders = new ArrayList<CRestHolder>();
        holders.addAll(forEachBaseBuilder(new Builder() {
            public CRestHolder build(CRestBuilder builder) {
                return new CRestHolder(builder.serializeXmlWithSimpleXml().build(), SIMPLEXML_SPECIFIC_PROPERTIES);
            }
        }));
        if(TEST_JAXB) {
            holders.addAll(forEachBaseBuilder(new Builder() {
                public CRestHolder build(CRestBuilder builder) {
                    return new CRestHolder(builder.serializeXmlWithJaxb().build(), JAXB_SPECIFIC_PROPERTIES);
                }
            }));
        }
        return arrify(holders);
    }

    public static CRestHolder[] byJsonSerializersAndRestServices() {
        return byRestServices();
    }

    public static CRestHolder[] byXmlSerializersAndRestServices() {
        List<CRestHolder> holders = new ArrayList<CRestHolder>();
        holders.addAll(forEachBaseBuilder(new Builder() {
            public CRestHolder build(CRestBuilder builder) {
                return new CRestHolder(builder.serializeXmlWithSimpleXml().build(), SIMPLEXML_SPECIFIC_PROPERTIES);
            }
        }));
        holders.addAll(forEachBaseBuilder(new Builder() {
            public CRestHolder build(CRestBuilder builder) {
                return new CRestHolder(builder.useHttpClientRestService().serializeXmlWithSimpleXml().build(), SIMPLEXML_SPECIFIC_PROPERTIES);
            }
        }));
        if(TEST_JAXB) {
            holders.addAll(forEachBaseBuilder(new Builder() {
                public CRestHolder build(CRestBuilder builder) {
                    return new CRestHolder(builder.serializeXmlWithJaxb().build(), JAXB_SPECIFIC_PROPERTIES);
                }
            }));
            holders.addAll(forEachBaseBuilder(new Builder() {
                public CRestHolder build(CRestBuilder builder) {
                    return new CRestHolder(builder.useHttpClientRestService().serializeXmlWithJaxb().build(), JAXB_SPECIFIC_PROPERTIES);
                }
            }));
        }
        return arrify(holders);
    }


    public static CRestHolder[] byJsonSerializers() {
        return arrify(byDefault());
    }

    public static Collection<CRestHolder[]> crest(CRestHolder[]... holderss) {
        Collection<CRestHolder[]> data = new ArrayList<CRestHolder[]>();
        for (CRestHolder[] holders : holderss) {
            for (CRestHolder holder : holders) {
                data.add(new CRestHolder[]{holder});
            }
        }
        return data;
    }


    public static class CRestHolder {
        public final CRest crest;
        public final Map<String,Object> properties = new HashMap<String, Object>(DEFAULT_PROPERTIES);

        public CRestHolder(CRest crest) {
            this(crest, Collections.<String, Object>emptyMap());
        }
        public CRestHolder(CRest crest, Map<String, Object> properties) {
            this.crest = crest;
            this.properties.putAll(properties);
        }
    }
}