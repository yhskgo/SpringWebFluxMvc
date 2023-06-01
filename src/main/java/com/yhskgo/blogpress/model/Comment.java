package com.yhskgo.blogpress.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yhskgo.blogpress.util.BlogpressUtil;

public class Comment {
	
	private String id;
	
	private String blogId;
	private String parentId;
	private int childSequence;
	private String position;
	private String status;
	private int level;
	private String user;
	private String emailAddress;
	private String commentText;
	
	@JsonFormat
	(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy 'T'HH:mm:ss")
	private Date createdDate;
	
	/**
	 * @return the createDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setCreatedDate(Date createdDateAt) {
		this.createdDate = createdDateAt;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the blogId
	 */
	public String getBlogId() {
		return blogId;
	}
	/**
	 * @param blogId the blogId to set
	 */
	public void setBlogId(String blogId) {
		this.blogId = blogId;
	}
	/**
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}
	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	/**
	 * @return the childSequence
	 */
	public int getChildSequence() {
		return childSequence;
	}
	/**
	 * @param childSequence2 the childSequence to set
	 */
	public void setChildSequence(int childSequence2) {
		this.childSequence = childSequence2;
	}
	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	/**
	 * @return the commentText
	 */
	public String getCommentText() {
		return commentText;
	}
	/**
	 * @param commentText the commentText to set
	 */
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
	public String getCreatedDateForDisplay() {
		String returnDateStr="";
		if(this.getCreatedDate()!=null) {
			returnDateStr = BlogpressUtil.getFormattedDateForDisplayOnPage(createdDate);
		}
		return returnDateStr;
	}
	@Override
	public String toString() {
		return "Comment {"
				+ "\"position\":" + position+"\"" 
				+ "\"user\":" + user +"\""
				+ "\"emailAddress\":" + emailAddress +"\""
				+ "\"commentText\":"+ commentText + "\""
				+ "\"createDate\":" + BlogpressUtil.getFormattedDateForElasticSearch(createdDate) + "\"";
	}
	
	
	
	


}
