package org.oasis_open.contextserver.impl.conditions.initializers;

import org.oasis_open.contextserver.api.PropertyType;
import org.oasis_open.contextserver.api.conditions.initializers.ChoiceListInitializer;
import org.oasis_open.contextserver.api.conditions.initializers.ChoiceListValue;
import org.oasis_open.contextserver.api.conditions.initializers.I18nSupport;
import org.oasis_open.contextserver.api.services.DefinitionsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Initializer for the set of available profile properties.
 */
public class ProfilePropertyTypeChoiceListInitializer implements ChoiceListInitializer, I18nSupport {

    DefinitionsService definitionsService;

    @Override
    public List<ChoiceListValue> getValues(Object context) {
        Set<PropertyType> profileProperties = definitionsService.getPropertyTypeByTag(definitionsService.getTag("profileProperties"), true);
        List<ChoiceListValue> choiceListValues = new ArrayList<>(profileProperties.size());
        for (PropertyType propertyType : profileProperties) {
            choiceListValues.add(new PropertyTypeChoiceListValue("properties." + propertyType.getId(), propertyType.getNameKey(),
                    propertyType.getValueTypeId()));
        }
        return choiceListValues;
    }

    public void setDefinitionsService(DefinitionsService definitionsService) {
        this.definitionsService = definitionsService;
    }
}
