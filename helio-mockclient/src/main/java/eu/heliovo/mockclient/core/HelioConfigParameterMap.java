/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package eu.heliovo.mockclient.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Map that holds user defined parameters.
 * @author marco soldati (at) fhnw.ch
 *
 */
public class HelioConfigParameterMap extends HashMap<HelioConfigParameterMap.UserParamType, Object> {
    
    /**
     * Enumeration of valid user parameter keys
     * @author marco soldati (at) fhnw.ch
     *
     */
    static enum UserParamType {
        ;
        
        /**
         * Type of the user parameter
         */
        private final Class<?> type;
    
        /**
         * Create user parameter an assign a type
         * @param type type of the user parameter
         */
        private UserParamType(Class<?> type) {
            this.type = type;
            
        }
    
        /**
         * Return the type of the user parameter.
         * @return the type of the user parameter.
         */
        public Class<?> getType() {
            return type;
        }
    }

    /**
     * Make sure the object is of the same type as specified in the key ({@link UserParamType#getType()})
     */
    @Override
    public Object put(HelioConfigParameterMap.UserParamType key, Object value) {
    	if (key == null) {
    		throw new IllegalArgumentException("Attrribute 'key' must not be null.");
    	}
    	if (value == null) {
    		throw new IllegalArgumentException("Attrribute 'value' must not be null.");
    	}
        if (!key.getType().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Argument 'value' must be of type '" + key.getType() + "', but is " + value.getClass().getName() + ": " + key + "=" + value);
        }
        return super.put(key, value);
    }
    
    /**
     * Get value of a parameter as boolean. If the Parameter is not specified in the map this method will return false.
     * @param key the key to look for
     * @return the value of the Boolean key. 
     * @throws IllegalArgumentException if type of argument key is not Boolean.
     */    
    public boolean getBoolean(UserParamType key) throws IllegalArgumentException {
        if (key.getType() != Boolean.class) {
            throw new IllegalArgumentException("Type of UserParamType '" + key + "' should be Boolean, but is '" + key.getType() + "'");
        }
        Boolean bool = (Boolean)get(key);
        return bool != null && bool;
    }
}