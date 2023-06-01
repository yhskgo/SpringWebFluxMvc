package com.yhskgo.blogpress.repository;

import java.util.List;

import com.yhskgo.blogpress.model.Blog;
import com.yhskgo.blogpress.model.Comment;

public interface BlogRepositoryCustom {
	
	List<Comment> getAllComments(int from, int size);
	
	List<Comment> getCommentsForStatus(String status, int from, int size);
	
	int getCurrentChildSequence(String blogId, String parentCommentId);
	
	List<Blog> search(String searchText);

}
