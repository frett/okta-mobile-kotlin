/*
 * Copyright 2021-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.testhelpers

typealias RequestMatcher = (request: OktaRecordedRequest) -> Boolean

object RequestMatchers {
    fun header(key: String, value: String): RequestMatcher {
        return { request ->
            request.headers[key] == value
        }
    }

    fun not(requestMatcher: RequestMatcher): RequestMatcher {
        return { request ->
            !requestMatcher.invoke(request)
        }
    }

    fun doesNotContainHeader(key: String): RequestMatcher {
        return { request ->
            !request.headers.names().contains(key)
        }
    }

    fun path(path: String): RequestMatcher {
        return { request ->
            var requestPath = request.path
            val queryIndex = requestPath?.indexOf("?") ?: -1
            if (queryIndex > -1) {
                // Remove the query params.
                requestPath = requestPath?.substring(0, queryIndex)
            }
            requestPath?.endsWith(path) ?: false
        }
    }

    fun query(query: String): RequestMatcher {
        return { request ->
            val requestPath = request.path
            val queryIndex = requestPath?.indexOf("?") ?: -1
            if (queryIndex > -1) {
                requestPath?.substring(queryIndex + 1) == query
            } else {
                false
            }
        }
    }

    fun method(method: String): RequestMatcher {
        return { request -> request.method == method }
    }

    fun body(body: String): RequestMatcher {
        return { request ->
            val actual = request.bodyText
            actual == body
        }
    }

    fun composite(vararg matchers: RequestMatcher): RequestMatcher {
        return { request ->
            matchers.all { it(request) }
        }
    }
}
