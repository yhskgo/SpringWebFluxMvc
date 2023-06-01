package com.yhskgo.blogpress.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yhskgo.blogpress.model.Blog;
import com.yhskgo.blogpress.model.Comment;
import com.yhskgo.blogpress.service.BlogService;

@RestController
@RequestMapping("api")
public class BlogRESTController {
	
	private Logger logger = LoggerFactory.getLogger(BlogRESTController.class);
	
	@Autowired
	private BlogService blogService;
	
	@RequestMapping(value = "/listBlogs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Blog>> getAllBlogJSON() {
		logger.info("getting all blog data in json format");
		List<Blog> allBlogs = blogService.getAllBlogs();
		
		
		List<Comment> commentList = null;
		
		for(Blog blog: allBlogs) {
			if(blog.getComments()!=null && !blog.getComments().isEmpty()) {
				commentList = blog.getComments().stream().filter(comment->comment.getStatus().equalsIgnoreCase("A"))
						.collect(Collectors.toList());
				if(commentList!=null) {
					blog.setComments(commentList);
				} else {
					blog.setComments(new ArrayList<Comment>());
				}
			}
			if(blog.getComments()==null||blog.getComments().isEmpty()) {
				blog.setComments(new ArrayList<Comment>());
			}
			
		}
		
		return new ResponseEntity<List<Blog>>(allBlogs, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/listAllComments", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Comment>> getAllCommentJSON() {
		logger.info("getting all blog data in json format");
		
		List<Comment> allComments = blogService.getAllComments(0, 100);
		
		return new ResponseEntity<List<Comment>>(allComments, HttpStatus.OK);
	}

}
