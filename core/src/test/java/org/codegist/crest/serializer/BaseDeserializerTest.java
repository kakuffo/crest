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

package org.codegist.crest.serializer;

import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

/**
 * @author laurent.gilles@codegist.org
 */
public abstract class BaseDeserializerTest {

    protected static final Charset charset = Charset.defaultCharset();
    private boolean closeFlag = false;
    private boolean inputStreamSet = false;

    @Before
    public void resetCloseFlag(){
        closeFlag = inputStreamSet = false;
    }

    protected InputStream toInputStream(String s) {
        if(s == null) {
            return null;
        }
        inputStreamSet = true;
        return new ByteArrayInputStream(s.getBytes()){
            @Override
            public void close() throws IOException {
                closeFlag = true;
            }
        };
    }

    private void assertInputStreamAsBeenClosed(){
        if(inputStreamSet) {
            assertTrue(closeFlag);
        }
    }



    protected <T> T deserialize(TypeDeserializer<T> toTest, String s) throws Exception {
        return this.<T>deserialize((Deserializer)toTest ,s);
    }
    protected <T> T deserialize(Deserializer toTest, String s) throws Exception {
        InputStream stream = toInputStream(s);
        T t = toTest.<T>deserialize(null, null, stream, charset);
        assertInputStreamAsBeenClosed();
        return t;
    }
}
