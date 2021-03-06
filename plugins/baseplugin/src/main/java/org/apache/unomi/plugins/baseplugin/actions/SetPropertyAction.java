/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.unomi.plugins.baseplugin.actions;

import org.apache.unomi.api.Event;
import org.apache.unomi.api.PropertyType;
import org.apache.unomi.api.actions.Action;
import org.apache.unomi.api.actions.ActionExecutor;
import org.apache.unomi.api.services.EventService;
import org.apache.unomi.api.services.PrivacyService;
import org.apache.unomi.api.services.ProfileService;
import org.apache.unomi.persistence.spi.PropertyHelper;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class SetPropertyAction implements ActionExecutor {

    private PrivacyService privacyService;
    private ProfileService profileService;

    public void setPrivacyService(PrivacyService privacyService) {
        this.privacyService = privacyService;
    }

    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    public int execute(Action action, Event event) {
        boolean storeInSession = Boolean.TRUE.equals(action.getParameterValues().get("storeInSession"));

        String propertyName = (String) action.getParameterValues().get("setPropertyName");

        Object propertyValue = action.getParameterValues().get("setPropertyValue");
        if(propertyValue == null) {
            propertyValue = action.getParameterValues().get("setPropertyValueMultiple");
        }
        Object propertyValueInteger = action.getParameterValues().get("setPropertyValueInteger");
        Object setPropertyValueMultiple = action.getParameterValues().get("setPropertyValueMultiple");
        Object setPropertyValueBoolean = action.getParameterValues().get("setPropertyValueBoolean");

        if (propertyValue == null) {
            if (propertyValueInteger != null) {
                propertyValue = PropertyHelper.getInteger(propertyValueInteger);
            }
            if (setPropertyValueMultiple != null) {
               propertyValue = setPropertyValueMultiple;
            }
            if (setPropertyValueBoolean != null) {
                propertyValue = PropertyHelper.getBooleanValue(setPropertyValueBoolean);
            }
        }
        if (propertyValue != null && propertyValue.equals("now")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            propertyValue = format.format(event.getTimeStamp());
        }

        Object target = storeInSession ? event.getSession() : event.getProfile();

        if (PropertyHelper.setProperty(target, propertyName, propertyValue, (String) action.getParameterValues().get("setPropertyStrategy"))) {
            return storeInSession ? EventService.SESSION_UPDATED : EventService.PROFILE_UPDATED;
        }
        return EventService.NO_CHANGE;
    }

}
