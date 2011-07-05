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

package org.codegist.crest.model.jaxb;

import org.codegist.crest.model.BunchOfData;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author Laurent Gilles (laurent.gilles@codegist.org)
 */
@XmlRootElement(name = "bunchOfData")
public class JaxbBunchOfData<T> extends BunchOfData<T> {

    public JaxbBunchOfData(Date val1, Boolean val2, T val3) {
        super(val1,val2,val3);
    }

    public JaxbBunchOfData() {
    }



}

