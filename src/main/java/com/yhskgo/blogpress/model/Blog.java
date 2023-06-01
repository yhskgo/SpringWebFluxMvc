package com.yhskgo.blogpress.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yhskgo.blogpress.util.BlogpressCommentComparator;
import com.yhskgo.blogpress.util.BlogpressUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Document(indexName = "blog", type="blog")
public class Blog {
	
	@Id
	private String _id;
	private String title;
	private String body;
	private String status;
	private String createdBy;
	
	@JsonFormat
	(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy'T'HH:mm:ss")
	private Date createdDate;
	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createdDate;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createdDate = createDate;
	}
	@JsonFormat
	(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy'T'HH:mm:ss")
	private Date publishDate;
	
	@Field(includeInParent=true, type = FieldType.Nested)
	private List<Comment> comments;
	
	
	
	/**
	 * @return the _id
	 */
	public String get_id() {
		return _id;
	}
	/**
	 * @param _id the _id to set
	 */
	public void set_id(String _id) {
		this._id = _id;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
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
	 * @return the createBy
	 */
	public String getCreateBy() {
		return createdBy;
	}
	/**
	 * @param createBy the createBy to set
	 */
	public void setCreateBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the comments
	 */
	public List<Comment> getComments() {
		if(comments !=null && !comments.isEmpty()) {
			Collections.sort(comments, new BlogpressCommentComparator());
		}
		return comments;
	}
	public void setComments(List<Comment> comments) {
		// TODO Auto-generated method stub
		this.comments = comments;
		
	}
	
	public String getPublishDateForDisplay() {
		String returnDateString="";
		if(this.getCreateDate()!=null) {
			returnDateString = BlogpressUtil.getFormattedDateForDisplayOnPage(createdDate);
		}
		
		return returnDateString;
	}
	
	public int getCommentSize() {
		if(this.comments !=null) {
			return this.comments.size();
		} else {
			return 0;
		}
	}
	@Override
	public String toString() {
		return "Blog [title=" + title + ", body=" + body + ", status=" + status + ", createdBy=" + createdBy
				+ ", createdDate=" + BlogpressUtil.getFormattedDateForElasticSearch(createdDate) 
				+ ", publishDate=" + BlogpressUtil.getFormattedDateForElasticSearch(publishDate) + ", comments=" + getComments() + "]";
	}
	public String setPublishDate(Date date) {
		// TODO Auto-generated method stub
		return null;
		
	}
	

}
