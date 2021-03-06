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

package org.codegist.crest.entity;

import org.codegist.crest.io.Request;
import org.codegist.crest.param.Param;
import org.codegist.crest.serializer.Serializer;

import java.io.OutputStream;
import java.util.List;

import static org.codegist.crest.config.ParamType.FORM;

/**
 * @author laurent.gilles@codegist.org
 */
public class SerializingEntityWriter implements EntityWriter {

    private final Serializer<List<Param>> serializer;
    private final String contentType;

    public SerializingEntityWriter(Serializer<List<Param>> serializer, String contentType) {
        this.serializer = serializer;
        this.contentType = contentType;
    }

    public String getContentType(Request request) {
        return contentType;
    }

    public int getContentLength(Request httpRequest) {
        return -1;
    }

    public void writeTo(Request request, OutputStream outputStream) throws Exception {
        serializer.serialize(request.getParams(FORM), request.getMethodConfig().getCharset(), outputStream);
    }
}
