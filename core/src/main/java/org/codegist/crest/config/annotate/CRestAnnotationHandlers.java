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

package org.codegist.crest.config.annotate;

import org.codegist.crest.annotate.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author laurent.gilles@codegist.org
 */
public final class CRestAnnotationHandlers {

    private CRestAnnotationHandlers(){
        throw new IllegalStateException();
    }
    public static AnnotationHandlers getInstance(){
        return new DefaultAnnotationHandlers(getHandlersMap());
    }

    public static Map<Class<? extends Annotation>, AnnotationHandler<?>> getHandlersMap(){
        Map<Class<? extends Annotation>, AnnotationHandler<?>> handlers = new HashMap<Class<? extends Annotation>, AnnotationHandler<?>>();
        handlers.put(ConnectionTimeout.class, new ConnectionTimeoutAnnotationHandler());
        handlers.put(Consumes.class, new ConsumesAnnotationHandler());
        handlers.put(CookieParam.class, new CookieParamAnnotationHandler());
        handlers.put(CookieParams.class, new CookieParamsAnnotationHandler());
        handlers.put(DELETE.class, new DELETEAnnotationHandler());
        handlers.put(Encoded.class, new EncodedAnnotationHandler());
        handlers.put(Encoding.class, new EncodingAnnotationHandler());
        handlers.put(EndPoint.class, new EndPointAnnotationHandler());
        handlers.put(EntityWriter.class, new EntityWriterAnnotationHandler());
        handlers.put(ErrorHandler.class, new ErrorHandlerAnnotationHandler());
        handlers.put(FormParam.class, new FormParamAnnotationHandler());
        handlers.put(FormParams.class, new FormParamsAnnotationHandler());
        handlers.put(GET.class, new GETAnnotationHandler());
        handlers.put(HEAD.class, new HEADAnnotationHandler());
        handlers.put(HeaderParam.class, new HeaderParamAnnotationHandler());
        handlers.put(HeaderParams.class, new HeaderParamsAnnotationHandler());
        handlers.put(ListSeparator.class, new ListSeparatorAnnotationHandler());
        handlers.put(MatrixParam.class, new MatrixParamAnnotationHandler());
        handlers.put(MatrixParams.class, new MatrixParamsAnnotationHandler());
        handlers.put(MultiPartParam.class, new MultiPartParamAnnotationHandler());
        handlers.put(MultiPartParams.class, new MultiPartParamsAnnotationHandler());
        handlers.put(OPTIONS.class, new OPTIONSAnnotationHandler());
        handlers.put(Path.class, new PathAnnotationHandler());
        handlers.put(PathParam.class, new PathParamAnnotationHandler());
        handlers.put(PathParams.class, new PathParamsAnnotationHandler());
        handlers.put(POST.class, new POSTAnnotationHandler());
        handlers.put(Produces.class, new ProducesAnnotationHandler());
        handlers.put(PUT.class, new PUTAnnotationHandler());
        handlers.put(QueryParam.class, new QueryParamAnnotationHandler());
        handlers.put(QueryParams.class, new QueryParamsAnnotationHandler());
        handlers.put(RequestInterceptor.class, new RequestInterceptorAnnotationHandler());
        handlers.put(ResponseHandler.class, new ResponseHandlerAnnotationHandler());
        handlers.put(RetryHandler.class, new RetryHandlerAnnotationHandler());
        handlers.put(Serializer.class, new SerializerAnnotationHandler());
        handlers.put(SocketTimeout.class, new SocketTimeoutAnnotationHandler());
        return handlers;
    }
}