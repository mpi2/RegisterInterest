/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.ri.core.entities;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * This class is meant to hold the {@link Contact} fields, the contact table password fieldsc and the contact_role role
 * field. It is abstracted to a separate class so security information need not be carried around in the {@link Contact}
 * class.
 */
public class ContactExtended extends Contact {
    private String                       password;
    private boolean                      isPasswordExpired;
    private boolean                      isAccountLocked;
    private Collection<GrantedAuthority> roles;

    public void setContact(Contact contact) {
        setActive(contact.isActive);
        setAddress(contact.address);
        setCreatedAt(contact.createdAt);
        setPk(contact.pk);
        setUpdatedAt(contact.updatedAt);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordExpired() {
        return isPasswordExpired;
    }

    public void setPasswordExpired(boolean passwordExpired) {
        isPasswordExpired = passwordExpired;
    }

    public boolean isAccountLocked() {
        return isAccountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        isAccountLocked = accountLocked;
    }

    public Collection<GrantedAuthority> getRoles() {
        return roles;
    }

    public void setRoles(Collection<GrantedAuthority> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "ContactExtended{" +
                "password='" + password + '\'' +
                ", isPasswordExpired=" + isPasswordExpired +
                ", isAccountLocked=" + isAccountLocked +
                '}';
    }
}