/**
 *    Auth:riozenc
 *    Date:2019年3月11日 下午6:21:13
 *    Title:com.riozenc.titanTool.datasources.type.BaseDatabase.java
 **/
package com.riozenc.titanTool.datasources.type;

public class BaseDatabase {
	private String drive;
	private String url;
	private String username;
	private String password;

	protected String getDrive() {
		return drive;
	}

	protected void setDrive(String drive) {
		this.drive = drive;
	}

	protected String getUrl() {
		return url;
	}

	protected void setUrl(String url) {
		this.url = url;
	}

	protected String getUsername() {
		return username;
	}

	protected void setUsername(String username) {
		this.username = username;
	}

	protected String getPassword() {
		return password;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

	protected String join(String dbName, String param) {

		return dbName + "." + param;
	}

}
