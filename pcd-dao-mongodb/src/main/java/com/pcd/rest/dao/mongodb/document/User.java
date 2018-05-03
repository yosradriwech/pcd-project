package com.pcd.rest.dao.mongodb.document;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User{
 
	@Id
	private String id;
	private String name;
	private String lastName;
	private int trustRank;
	private String status;
	private String gender;
	private int Age;
	@Indexed(unique = true)
	private String login;
	private String password;
	@CreatedDate
	private Date creationDate;
	private Date activationDate;
	private Date deActivationDate;
	@LastModifiedDate
	private Date lastUpdateDate;

	public User(String name, String lastName, String gender, int age, String login, String password) {
		this.name = name;
		this.lastName = lastName;
		this.gender = gender;
		this.Age = age;
		this.login = login;
		this.password = password;
		this.trustRank = 0;
		this.status = "";
	}


	public User() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Date getDeActivationDate() {
		return deActivationDate;
	}

	public void setDeActivationDate(Date deActivationDate) {
		this.deActivationDate = deActivationDate;
	}

	public Date getLastUpdatedDate() {
		return lastUpdateDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdateDate = lastUpdatedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTrustRank() {
		return trustRank;
	}

	public void setTrustRank(int trustRank) {
		this.trustRank = trustRank;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getAge() {
		return Age;
	}

	public void setAge(int age) {
		Age = age;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Subscription [id=").append(id).append(", status=").append(status).append(", creationDate=").append(creationDate).append(", activationDate=")
				.append(activationDate).append(", deActivationDate=").append(deActivationDate).append(", lastUpdateDate=").append(lastUpdateDate).append("]");
		return sb.toString();
	}

}
