/**
 * 
 */
package com.gcit.library.model;

/**
 * @author gcit
 *
 */
public class ResponseBody<T> {
	private T data;
	private Boolean success;
	private String message;
	
	public T getData() {
		return data;
	}
	public void setData(T object) {
		this.data = object;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
