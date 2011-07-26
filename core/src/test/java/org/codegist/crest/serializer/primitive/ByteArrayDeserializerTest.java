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

package org.codegist.crest.serializer.primitive;

import org.codegist.crest.serializer.BaseDeserializerTest;
import org.codegist.crest.serializer.TypeDeserializer;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

/**
 * @author laurent.gilles@codegist.org
 */
public class ByteArrayDeserializerTest extends BaseDeserializerTest {

    private final TypeDeserializer<byte[]> toTest = new ByteArrayDeserializer();

    @Test
    public void shouldDeserializeToByteArray() throws Exception {
        String s = "hello";
        assertArrayEquals(s.getBytes(), deserialize(toTest, s));
    }

    @Test
    public void shouldDeserializeNullToNull() throws Exception {
        assertNull(deserialize(toTest,null));
    }
}
