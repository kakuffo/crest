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

package org.codegist.crest.deserialization.jaxrs;

import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.deserialization.common.IComplexObjectDeserializations;
import org.codegist.crest.model.jackson.JacksonSomeData;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * @author laurent.gilles@codegist.org
 */
@EndPoint("{crest.server.end-point}")
@Path("deserialization/complexobject")
public interface ComplexObjectDeserializationsWithJackson extends IComplexObjectDeserializations {

    @GET
    @Path("json")
    JacksonSomeData someDataGuessed(
            @QueryParam("date-format") String dateFormat,
            @QueryParam("boolean-true") String booleanTrue,
            @QueryParam("boolean-false") String booleanFalse);

    @GET
    @Produces("application/json")
    JacksonSomeData someDataForced(
            @QueryParam("date-format") String dateFormat,
            @QueryParam("boolean-true") String booleanTrue,
            @QueryParam("boolean-false") String booleanFalse);

    @GET
    @Path("jsons")
    JacksonSomeData[] someDatas(
            @QueryParam("date-format") String dateFormat,
            @QueryParam("boolean-true") String booleanTrue,
            @QueryParam("boolean-false") String booleanFalse);

    @GET
    @Path("jsons")
    List<JacksonSomeData> someDatas2(
            @QueryParam("date-format") String dateFormat,
            @QueryParam("boolean-true") String booleanTrue,
            @QueryParam("boolean-false") String booleanFalse);


}
