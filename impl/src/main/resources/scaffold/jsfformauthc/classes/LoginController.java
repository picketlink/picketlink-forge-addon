/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package __package_name;

import org.picketlink.Identity;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * We control the authentication process from this bean, so that in the event of a failed authentication we can add an appropriate
 * FacesMessage to the response.
 */
@Named
public class LoginController {

    @Inject
    private Identity identity;

    public String login() {
        // credentials are already setted in the DefaultLoginCredentials bean. Input fields from login page provide a direct bind
        // to this bean properties.
        this.identity.login();

        if (!this.identity.isLoggedIn()) {
            getFacesContext().addMessage(null, new FacesMessage(
                "Authentication was unsuccessful.  Please check your username and password before trying again."));
            return null;
        }

        return "/home.xhtml";
    }

    private FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
}
