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

package org.codegist.crest.flickr.security;

import org.codegist.crest.http.Pair;
import org.codegist.crest.security.http.HttpEntityParamsParser;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Laurent Gilles (laurent.gilles@codegist.org)
 */
public class MultiPartEntityParamsParser implements HttpEntityParamsParser {

    private static final Pattern BOUNDARY_EXTRACTOR = Pattern.compile("boundary=(.*)");
    private static final Pattern PARAM_PATTERN = Pattern.compile("Content-Disposition:\\s*[^;]+\\s*;\\s*name=\"([^\"]+)\"(?:;[^\r]+)?\r\nContent-Type:\\s*([^;]+)\\s*(?:;\\s*charset=[^\r]+)?\r\n\r\n(.*)\r\n");

    public List<Pair> parse(String contentType, Charset charset, InputStream httpEntity) throws IOException {
        String boundary = getBoundary(contentType);
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity, charset));
        Scanner partsScanner = new Scanner(reader).useDelimiter(Pattern.quote(boundary));

        List<Pair> pairs = new ArrayList<Pair>();
        while(partsScanner.hasNext()){
            String part = partsScanner.next();

           Matcher m = PARAM_PATTERN.matcher(part);
           if(m.find())  {
               if("text/plain".equals(m.group(2))) {
                   pairs.add(new Pair(m.group(1), m.group(3)));
               }
           }
        }

        return pairs;
    }

    public static String getBoundary(String contentType){
        Matcher m = BOUNDARY_EXTRACTOR.matcher(contentType);
        m.find();
        return "--" + m.group(1) + "\r\n";
    }

}
