/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.ri.core.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Created by mrelac on 24/07/2015.
 */
public class InterestException extends Exception {

    private HttpStatus httpStatus;
    
    public InterestException() {
        super();
    }

    public InterestException(String message) {
        super(message);
    }
    
    public InterestException(HttpStatus httpStatus) {
        super();
        this.httpStatus = httpStatus;
    }
    
    public InterestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public InterestException(Exception e) {
        super(e);
    }

    public InterestException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterestException(Throwable cause) {
        super(cause);
    }

    public InterestException(String message, Throwable cause, boolean enableSuppression, boolean writeableStackTrace) {
        super(message, cause, enableSuppression, writeableStackTrace);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
