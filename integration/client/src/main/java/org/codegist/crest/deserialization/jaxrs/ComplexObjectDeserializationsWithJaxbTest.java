/*
 * Copyright 2011 CodeGist.org
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

package org.codegist.crest.deserialization.jaxrs;

import org.codegist.crest.CRestBuilder;
import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.ResponseHandler;
import org.codegist.crest.deserialization.common.CommonComplexObjectDeserializationsTest;
import org.codegist.crest.deserialization.common.IComplexObjectDeserializations;
import org.codegist.crest.serializer.jaxb.JaxbDeserializer;
import org.codegist.crest.util.JaxbSomeDatasResponseHandler;
import org.codegist.crest.util.model.jaxb.JaxbSomeData;
import org.junit.runners.Parameterized;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Collection;
import java.util.List;

/**
 * @author Laurent Gilles (laurent.gilles@codegist.org)
 */
public class ComplexObjectDeserializationsWithJaxbTest extends CommonComplexObjectDeserializationsTest<ComplexObjectDeserializationsWithJaxbTest.ComplexObjectDeserializationsWithJaxb> {

    public ComplexObjectDeserializationsWithJaxbTest(CRestHolder crest) {
        super(crest, ComplexObjectDeserializationsWithJaxb.class);
    }

    @Parameterized.Parameters
    public static Collection<CRestHolder[]> getData() {
        return crest(arrify(forEachBaseBuilder(new Builder() {
            public CRestHolder build(CRestBuilder builder) {
                return new CRestHolder(builder.deserializeXmlWith(JaxbDeserializer.class).build(), JAXB_SPECIFIC_PROPERTIES);
            }
        })));
    }


    /**
     * @author laurent.gilles@codegist.org
     */
    @EndPoint("{crest.server.end-point}")
    @Path("deserialization/complexobject")
    public static interface ComplexObjectDeserializationsWithJaxb extends IComplexObjectDeserializations {


        @GET
        @Path("xml")
        JaxbSomeData someDataGuessed(
                @QueryParam("date-format") String dateFormat,
                @QueryParam("boolean-true") String booleanTrue,
                @QueryParam("boolean-false") String booleanFalse);

        @GET
        @Produces("application/xml")
        JaxbSomeData someDataForced(
                @QueryParam("date-format") String dateFormat,
                @QueryParam("boolean-true") String booleanTrue,
                @QueryParam("boolean-false") String booleanFalse);

        @GET
        @Path("xmls")
        @ResponseHandler(JaxbSomeDatasResponseHandler.class)
        JaxbSomeData[] someDatas(
                @QueryParam("date-format") String dateFormat,
                @QueryParam("boolean-true") String booleanTrue,
                @QueryParam("boolean-false") String booleanFalse);

        @GET
        @Path("xmls")
        @ResponseHandler(JaxbSomeDatasResponseHandler.class)
        List<JaxbSomeData> someDatas2(
                @QueryParam("date-format") String dateFormat,
                @QueryParam("boolean-true") String booleanTrue,
                @QueryParam("boolean-false") String booleanFalse);

    }
}
