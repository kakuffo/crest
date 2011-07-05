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

package org.codegist.crest.security.oauth;

import org.codegist.common.lang.Validate;
import org.codegist.crest.http.HttpMethod;
import org.codegist.crest.http.Pair;
import org.codegist.crest.security.Authorization;
import org.codegist.crest.security.AuthorizationToken;

import java.util.List;

import static org.codegist.crest.http.Pairs.join;

/**
 * OAuth authentification manager implementation.
 * <p>Refresh implementation is based on oauth_session_handle token extra parameter
 * @author Laurent Gilles (laurent.gilles@codegist.org)
 */
public class OAuthorization implements Authorization {

    private final OAuthenticator oauth;
    private volatile OAuthToken accessOAuthToken;

    public OAuthorization(OAuthenticator oauth, OAuthToken accessOAuthToken) {
        Validate.notNull(oauth, "OAuthenticator is required");
        Validate.notNull(accessOAuthToken, "Token is required");
        this.oauth = oauth;
        this.accessOAuthToken = accessOAuthToken;
    }

    public AuthorizationToken authorize(HttpMethod method, String url, Pair... parameters) {
        List<Pair> oauthParams = oauth.oauth(this.accessOAuthToken, method, url, parameters);
        return new AuthorizationToken("OAuth", join(oauthParams, ',', '=', false, true));
    }

    public void refresh() {
        this.accessOAuthToken = oauth.refreshAccessToken(this.accessOAuthToken, this.accessOAuthToken.getAttribute("oauth_session_handle"));
    }

}
