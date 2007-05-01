package org.jia.ptrack.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class User implements Serializable
{
  private String firstName;
  private String lastName;
  private String login;
  private String password;
  private Set roles;
  private Department department;

  public User()
  {
  }

  public User(String login, String firstName, String lastName,
			String password, RoleType role, Department department) {
	  this(login, firstName, lastName, password, Collections.singleton(role), department);
  }
  

  public User(String login, String firstName, String lastName,
              String password, Set roles, Department department)
  {
    this.login = login;
    this.firstName = firstName;
    this.lastName = lastName;
    this.password = password;
    this.roles = roles;
    assert !roles.isEmpty();
	this.department = department;
  }

  public void setLogin(String login)
  {
    this.login = login;
  }

  public String getLogin()
  {
    return login;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getPassword()
  {
    return password;
  }

  public void setRole(RoleType role)
  {
    throw new UnsupportedOperationException();
  }

  public RoleType getRole()
  {
	for (Iterator it = roles.iterator(); it.hasNext();) {
		RoleType role = (RoleType) it.next();
		if (role.isPtrackRole())
			return role;
		
	}
    throw new UnsupportedOperationException();
  }

  public String toString()
  {
    return firstName + " " + lastName;
  }

public boolean isPasswordValid(String password) {
	return getPassword().equals(password);
}

public Set getRoles() {
	return roles;
}

public Department getDepartment() {
	return department;
}

}