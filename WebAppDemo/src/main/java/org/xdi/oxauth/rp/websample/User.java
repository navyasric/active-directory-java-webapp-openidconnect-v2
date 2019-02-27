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

import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONObject;

/**
 *  The User Class holds together all the members of a WAAD User entity and all the access methods and set methods
 *  @author Azure Active Directory Contributor
 */
@XmlRootElement
public class User extends DirectoryObject{
	
	// The following are the individual private members of a User object that holds
	// a particular simple attribute of an User object.
	protected String objectId;
	protected String objectType;
	protected String displayName;
	protected String surname;
	protected String userPrincipalName;

	/**
	 * below 4 properties are for future use
	 */
	// managerDisplayname of this user
	protected String managerDisplayname;
	
	/**
	 * The constructor for the User class. Initializes the dynamic lists and managerDisplayname variables.
	 */
	public User(){
		managerDisplayname = null;
	}

	/**
	 * @return The objectId of this user.
	 */
	public String getObjectId() {
		return objectId;
	}
	
	/**
	 * @param objectId The objectId to set to this User object.
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}


	/**
	 * @return The objectType of this User.
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * @param objectType The objectType to set to this User object.
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	/**
	 * @return The userPrincipalName of this User.
	 */
	public String getUserPrincipalName() {
		return userPrincipalName;
	}

	/**
	 * @param userPrincipalName The userPrincipalName to set to this User object.
	 */
	public void setUserPrincipalName(String userPrincipalName) {
		this.userPrincipalName = userPrincipalName;
	}

	/**
	 * @return The surname of this User.
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * @param surname The surname to set to this User Object.
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * @return The displayName of this User.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName The displayName to set to this User Object.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return new JSONObject(this).toString();
	}
	
	public String getManagerDisplayname(){
		return managerDisplayname;
	}
	
	public void setManagerDisplayname(String managerDisplayname){
		this.managerDisplayname = managerDisplayname;
	}
}
