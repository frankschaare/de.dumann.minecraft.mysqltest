/**
 * 
 */
package de.dumann.minecraft.model;

/**
 * @author hit
 *
 */
public class User 
{
private int id;	
private String lastName;
private String firstName;

	/**
	 * Beispielklasse für einen Benutzer
	 */
	public User() 
	{
	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}	
}
