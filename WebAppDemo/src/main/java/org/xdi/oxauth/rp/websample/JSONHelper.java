/*******************************************************************************
 // Copyright (c) Microsoft Corporation.
 // All rights reserved.
 //
 // This code is licensed under the MIT License.
 //
 // Permission is hereby granted, free of charge, to any person obtaining a copy
 // of this software and associated documentation files(the "Software"), to deal
 // in the Software without restriction, including without limitation the rights
 // to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
 // copies of the Software, and to permit persons to whom the Software is
 // furnished to do so, subject to the following conditions :
 //
 // The above copyright notice and this permission notice shall be included in
 // all copies or substantial portions of the Software.
 //
 // THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 // IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 // FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
 // AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 // LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 // OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 // THE SOFTWARE.
 ******************************************************************************/
package org.xdi.oxauth.rp.websample;

import java.lang.reflect.Field;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONObject;

/**
 * This class provides the methods to parse JSON Data from a JSON Formatted
 * String.
 * 
 * @author Azure Active Directory Contributor
 * 
 */
public class JSONHelper {

    JSONHelper() {
    }

    /**
     * This method parses an JSON Object out of a collection of JSON Objects
     * within a string
     * 
     * @param jsonObject
     * @return An JSON Object that would contains the DirectoryObject.
     * @throws Exception
     */
    public static JSONObject fetchDirectoryObjectJSONObject(JSONObject jsonObject) throws Exception {
        JSONObject jObj = new JSONObject();
        jObj = jsonObject.optJSONObject("responseMsg");
        return jObj;
    }

    /**
     * This is a generic method that copies the simple attribute values from an
     * argument jsonObject to an argument generic object.
     * 
     * @param jsonObject
     *            The jsonObject from where the attributes are to be copied.
     * @param destObject
     *            The object where the attributes should be copied into.
     * @throws Exception
     *             Throws a Exception when the operation are unsuccessful.
     */
    public static <T> void convertJSONObjectToDirectoryObject(JSONObject jsonObject, T destObject) throws Exception {

        // Get the list of all the field names.
        Field[] fieldList = destObject.getClass().getDeclaredFields();

        // For all the declared field.
        for (int i = 0; i < fieldList.length; i++) {
            // If the field is of type String, that is
            // if it is a simple attribute.
            if (fieldList[i].getType().equals(String.class)) {
                // Invoke the corresponding set method of the destObject using
                // the argument taken from the jsonObject.
                destObject
                        .getClass()
                        .getMethod(String.format("set%s", WordUtils.capitalize(fieldList[i].getName())),
                                new Class[] { String.class })
                        .invoke(destObject, new Object[] { jsonObject.optString(fieldList[i].getName()) });
            }
        }
    }
}
