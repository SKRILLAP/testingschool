package com.schoolmanagement.dto;

public class UserLoginResponse extends CommonApiResponse {

	private UserDto user;

	private String jwtToken;

	private int showChangePassword;

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	public int getShowChangePassword() {
		return showChangePassword;
	}

	public void setShowChangePassword(int showChangePassword) {
		this.showChangePassword = showChangePassword;
	}

	@Override
	public String toString() {
		return "UserLoginResponse [user=" + user + ", jwtToken=" + jwtToken + ", showChangePassword="
				+ showChangePassword + "]";
	}
	
	

}
