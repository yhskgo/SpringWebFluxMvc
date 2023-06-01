package com.yhskgo.blogpress.util;

import java.util.Comparator;

import com.yhskgo.blogpress.model.Comment;

public class BlogpressCommentComparator implements Comparator<Comment> {

	@Override
	public int compare(Comment c1, Comment c2) {
		if(c1==null && c2==null) {
			return 0;
		} else if(c1 != null && c2==null) {
			return 1;
		} else if(c1==null && c2 != null) {
			return -1;
		} else {
			return c1.getPosition().compareTo(c2.getPosition());
		}
	}

}
