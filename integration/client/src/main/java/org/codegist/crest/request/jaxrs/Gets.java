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

package org.codegist.crest.request.jaxrs;

import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.request.common.Requests;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author Laurent Gilles (laurent.gilles@codegist.org)
 */

@EndPoint("{crest.server.end-point}")
@Path("request/get")
public interface Gets extends Requests {

    @GET
    String raw();

    @GET
    @Path("accept")
    @Produces({"application/custom1", "application/custom2"})
    String accept();

    @GET
    @Path("content-type")
    @Consumes("application/custom")
    String contentType();

}