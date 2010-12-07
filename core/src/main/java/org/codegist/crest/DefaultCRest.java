/*
 * Copyright 2010 CodeGist.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * ===================================================================
 *
 * More information at http://www.codegist.org.
 */

package org.codegist.crest;

import org.codegist.common.lang.Disposable;
import org.codegist.common.lang.Disposables;
import org.codegist.common.lang.Strings;
import org.codegist.common.reflect.Methods;
import org.codegist.common.reflect.ProxyFactory;
import org.codegist.crest.config.ConfigFactoryException;
import org.codegist.crest.config.InterfaceConfig;
import org.codegist.crest.config.MethodConfig;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

/**
 * Default CRest implementation based on {@link org.codegist.crest.CRestContext} interface data model.
 * <p>On top of the behavior described in {@link org.codegist.crest.CRest}, this implementation adds :
 * <p>- {@link org.codegist.crest.interceptor.RequestInterceptor} to intercept any requests before it gets fired.
 * <p>- {@link org.codegist.crest.serializer.Serializer} to customize the serialization process of any types.
 * <p>- {@link org.codegist.crest.injector.Injector} to inject complexe types that can't be reduced to a String via the serializers.
 * <p>- {@link org.codegist.crest.handler.ResponseHandler} to customize response handling when interface method's response type is not one of raw types.
 * <p>- {@link org.codegist.crest.handler.ErrorHandler} to customize how the created interface behaves when any error occurs during the method call process.
 * @author Laurent Gilles (laurent.gilles@codegist.org)
 */
public class DefaultCRest implements CRest, Disposable {

    private final CRestContext context;

    /**
     * @param context The CRest configuration holder
     */
    public DefaultCRest(CRestContext context) {
        this.context = context;   
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public <T> T build(Class<T> interfaze) throws CRestException {
        try {
            return (T) context.getProxyFactory().createProxy(interfaze.getClassLoader(), new RestInterfacer(interfaze), new Class[]{interfaze});
        } catch (Exception e) {
            throw CRestException.transform(e);
        }
    }

    private class RestInterfacer<T> implements ProxyFactory.InvocationHandler {

        private final InterfaceContext interfaceContext;

        private RestInterfacer(Class<T> interfaze) throws ConfigFactoryException {
            InterfaceConfig config = context.getConfigFactory().newConfig(interfaze, context);
            this.interfaceContext = new DefaultInterfaceContext(config, context.getProperties());
        }

        @Override
        public Object invoke(Object target, Method method, Object[] args) throws Throwable {
            Object res = execIfObjectClassMethod(target, method, args);
            if (res != null) return res;
            return exec(method, args);
        }

        int i = 0 ;
        private Object exec(Method method, Object[] args) throws Exception {
            MethodConfig methodConfig = interfaceContext.getConfig().getMethodConfig(method);

            RequestContext requestContext = new DefaultRequestContext(interfaceContext, method, args);
            ResponseContext responseContext;
            Exception exception = null;
            int i = 0;
            do {
                exception = null;
                HttpResponse response;
                try {
                    HttpRequest request = buildRequest(requestContext);
                    if (request == null) {
                        // Request cancelled by requestInterceptor, returning
                        return null;
                    }
                    response = context.getRestService().exec(request);
                    responseContext = new DefaultResponseContext(requestContext, response);
                } catch (HttpException e) {
                    responseContext = new DefaultResponseContext(requestContext, e.getResponse());
                    exception = e;
                } catch (RuntimeException e) {
                    responseContext = new DefaultResponseContext(requestContext, null);
                    exception = e;
                }
            }while(exception != null && methodConfig.getRetryHandler().retry(responseContext, exception, ++i));

            if (exception != null) {
                return methodConfig.getErrorHandler().handle(responseContext, exception);
            }

            try {
                return handle(responseContext);
            } catch (Exception e) {
                return methodConfig.getErrorHandler().handle(responseContext, e);
            }
        }

        private Object handle(ResponseContext responseContext) {
            Class<?> returnTypeClass = responseContext.getRequestContext().getMethodConfig().getMethod().getReturnType();
            boolean closeResponse = false;
            try {
                if (returnTypeClass.equals(InputStream.class)) {
                    // If InputStream return type, then alway return raw response
                    return responseContext.getResponse().asStream();
                } else if (returnTypeClass.equals(Reader.class)) {
                    return responseContext.getResponse().asReader();
                } else {
                    return responseContext.getRequestContext().getMethodConfig().getResponseHandler().handle(responseContext);
                }
            } catch (CRestException e) {
                closeResponse = true;
                throw e;
            } catch (RuntimeException e) {
                closeResponse = true;
                throw new CRestException(e);
            } finally {
                if (closeResponse && responseContext.getResponse() != null) {
                    responseContext.getResponse().close();
                }
            }
        }

        private HttpRequest buildRequest(RequestContext requestContext) throws URISyntaxException {
            String fullpath = requestContext.getConfig().getEndPoint() + Strings.defaultIfBlank(requestContext.getConfig().getContextPath(), "") + requestContext.getMethodConfig().getPath();
            HttpRequest.Builder builder = new HttpRequest.Builder(fullpath, interfaceContext.getConfig().getEncoding())
                    .using(requestContext.getMethodConfig().getHttpMethod())
                    .timeoutSocketAfter(requestContext.getMethodConfig().getSocketTimeout())
                    .timeoutConnectionAfter(requestContext.getMethodConfig().getConnectionTimeout());

            if (!requestContext.getConfig().getGlobalInterceptor().beforeParamsInjectionHandle(builder, requestContext)) {
                // Request cancelled by global requestInterceptor, returning
                return null;
            }
            if (!requestContext.getMethodConfig().getRequestInterceptor().beforeParamsInjectionHandle(builder, requestContext)) {
                // Request cancelled by method requestInterceptor, returning
                return null;
            }

            int count = requestContext.getMethodConfig().getParamCount();
            for (int i = 0; i < count; i++) {
                requestContext.getMethodConfig().getParamConfig(i).getInjector().inject(builder, new DefaultParamContext(requestContext, i));
            }

            if (!requestContext.getMethodConfig().getRequestInterceptor().afterParamsInjectionHandle(builder, requestContext)) {
                // Request cancelled by method requestInterceptor, returning
                return null;
            }

            if (!requestContext.getConfig().getGlobalInterceptor().afterParamsInjectionHandle(builder, requestContext)) {
                // Request cancelled by global requestInterceptor, returning
                return null;
            }

            return builder.build();
        }


        Object execIfObjectClassMethod(Object target, Method method, Object[] args) {
            if (!method.getDeclaringClass().equals(Object.class)) return null;
            if (Methods.isToString(method)) {
                return this.toString();
            } else if (Methods.isEquals(method)) {
                return target == args[0];
            } else if (Methods.isHashCode(method)) {
                return this.hashCode();
            } else {
                return null;
            }
        }

    }


    public void dispose() {
        Disposables.dispose(context.getRestService());
    }
}