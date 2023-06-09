package com.yhskgo.blogpress.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yhskgo.blogpress.constants.BlogStatus;
import com.yhskgo.blogpress.constants.BlogpressConstants;
import com.yhskgo.blogpress.constants.CommentStatus;
import com.yhskgo.blogpress.model.Blog;
import com.yhskgo.blogpress.model.Comment;
import com.yhskgo.blogpress.service.BlogService;
import com.yhskgo.blogpress.util.BlogpressUtil;


@Controller
public class BlogController {
	private Logger logger = LoggerFactory.getLogger(BlogController.class);
	
	@Autowired
	private BlogService blogService;
	
	@GetMapping("/")
	public String showHomePage(Model model) {
		logger.info("This is show home page method ");
		setProcessingData(model, BlogpressConstants.TITLE_HOME_PAGE);

		return "home";
	}
	
	@GetMapping("/controlPage")
	public String showControlPage(Model model) {
		logger.info("This is control page ");
		setProcessingData(model, BlogpressConstants.TITLE_LANDING_CONTROL_PAGE);
		return "control-page";
	}
	
	@GetMapping("/login")
	public String showLoginPage(@RequestParam(value="error", required = false) String error, @RequestParam(value="logout", required = false) 
			String logout, Model model) {
		logger.info("This is login page URL   ");
		
		if(error!=null) {
			model.addAttribute("error", "Invalid Credential provided.");
		}
		if(logout!=null) {
			model.addAttribute("message", "Logged out");
		}
		
		setProcessingData(model, BlogpressConstants.TITLE_LOGIN_PAGE);
		return "login";
	}
	
	@GetMapping("/showAddNew")
	public String showAddNew(Model model) {
		logger.info("THis is addNew page URL   ");
		setProcessingData(model, BlogpressConstants.TITLE_NEW_BLOG_PAGE);
		return "add-new";
	}
	
	@PostMapping("/addNewBlog")
	public String addNewBlog(@RequestParam(value = "title", required = true) String title, 
			@RequestParam(value = "body", required = true) String body, Model model) {
		
		logger.info("Adding new blog with title : "+title);
		Blog blog = new Blog();
		blog.setTitle(title);
		blog.setBody(body);
		blog.setCreateBy(getCurrentUserName());
		blog.setCreateDate(new Date());
		blog.setPublishDate(new Date());
		blog.setStatus(BlogStatus.PUBLISHED.getStatus());
		
		return "home";
	}
	
	@GetMapping("/manageBlogs")
	public String showManagedBlogPage(Model model) {
		logger.info("This is manage blog page method");
		setProcessingData(model, BlogpressConstants.TITLE_MANAGE_BLOG_PAGE);
		
		List<Blog> blogSearchResultList = blogService.getAllBlogs();
		model.addAttribute("blog", blogSearchResultList);
		
		return "manage-blogs";
	}
	
	@PostMapping("/showUpdateBlogPage")
	public String shsowUpdateBolgPage(@RequestParam(value = "blogId", required = true) String blogId, Model model) {
		Blog blog = blogService.getBlog(blogId);
		model.addAttribute("blog", blog);
		
		return "edit-blog";
	}
	
	@PostMapping("/updateBlog")
	public String updateBlog(@RequestParam(value = "blogId", required = true) String blogId, @RequestParam(value = "title", required = true) String title,
			@RequestParam(value = "bogy", required = true) String body, Model model) {
		
		Blog blog = blogService.getBlog(blogId);
		if(blog !=null) {
			blog.setTitle(title);
			blog.setBody(body);
			blogService.addUpdateBlog(blog);
		}
		model.addAttribute("blog", blog);
		
		return "redirect:/manageBlogs";
	}
	
	@GetMapping("/viewBlog")
	public String viewBlog(@RequestParam(value = "blogId", required = true) String blogId, Model model) {
		logger.info("showing view blog page");
		if(blogId!=null) {
			Blog blog = blogService.getBlog(blogId);
			List<Comment> approvedCommentList = null;
			if(blog.getComments()!=null && !blog.getComments().isEmpty()) {
				approvedCommentList = blog.getComments().stream().filter(comment->comment.getStatus().equalsIgnoreCase("A"))
						.collect(Collectors.toList());
				if(approvedCommentList!=null) {
					blog.setComments(approvedCommentList);
				} else {
					blog.setComments(new ArrayList<Comment>());
				}
			}
			if(blog.getComments() == null || blog.getComments().isEmpty()) {
				blog.setComments(new ArrayList<Comment>());
			}
			model.addAttribute("blog", blog);
		}
		
		setProcessingData(model, BlogpressConstants.TITLE_VIEW_BLOG_PAGE);
		
		return "view-blog";
	}
	
	@PostMapping("/search")
	public String searchBlog(@RequestParam(value = "searchText") String searchText, Model model) {
		
		List<Blog> blogSearchResultBlogsList = blogService.search(searchText);
		model.addAttribute("blogSearchRequltList", blogSearchResultBlogsList);
		model.addAttribute("searchText", searchText);
		
		return "search";
	}
	
	@PostMapping("/updateCommentStatus")
	public String updateCommentStatus(@RequestParam(value = "blogId", required = true) String blogId,
			@RequestParam(value = "commentId", required = true) String commentId, 
			@RequestParam(value = "commentStatus", required = true) String commentStatus, 
			Model model) {
		logger.info("Approve comment");
		if(blogId!=null) {
			Blog blog = blogService.getBlog(blogId);
			if(blog!=null) {
				blogService.updateCommentStatus(blogId, commentId, blog.getComments(), commentStatus);
			}
		}
		
		return "manage-comments";
	}
	
	@GetMapping("/showComments")
	public String showManageComments() {
		
		return "manage-comments";
	}
	
	@PostMapping("/addComments")
	public String addComment(@RequestParam(value = "blogId", required = true) String blogId,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "comment", required = true) String comment,
			@RequestParam(value = "currentLevel", required = false, defaultValue = "0") Integer currentLevel,
			@RequestParam(value = "parentId", required = false, defaultValue = "0") String parentId,
			@RequestParam(value = "parentPosition", required = false) String parentPosition,
			Model model) {
		logger.info("Add Comment page");
		
		if(blogId!=null) {
			StringBuffer currentPositionStr = new StringBuffer();
			int childSequence = blogService.getCurrentChildSequence(blogId, parentId);
			
			if(parentPosition!=null) {
				currentPositionStr.append(parentPosition).append(".");
			}
			currentPositionStr.append(currentLevel+1).append(".").append(childSequence);
			
			Blog blog = blogService.getBlog(blogId);
			if(blog!=null) {
				List<Comment> blogComments = blog.getComments();
				if(blogComments==null) {
					blogComments = new ArrayList<Comment>();
				}
				
				Date currentDate = new Date();
				Comment blogComment = new Comment();
				blogComment.setId(BlogpressUtil.RandomNumber(currentDate));
				blogComment.setBlogId(blogId);
				blogComment.setParentId(parentId);
				blogComment.setChildSequence(childSequence);
				blogComment.setPosition(currentPositionStr.toString());
				blogComment.setStatus(CommentStatus.MODERATE.getStatus());
				blogComment.setLevel(currentLevel+1);
				blogComment.setUser(name);
				blogComment.setEmailAddress(email);
				blogComment.setCreatedDate(currentDate);
				
				blogComments.add(blogComment);
				blog.setComments(blogComments);
				blogService.addUpdateBlog(blog);
				model.addAttribute("blog", blog);
				
			}
		}
		
		setProcessingData(model, BlogpressConstants.TITLE_VIEW_BLOG_PAGE);
		return "redirect:/viewBlog?blogId="+blogId;
	}
	
	/**
	 * This method will check if current logged in user has given role
	 * @param roleName
	 * @return true or false - if user has given role
	 */
	
	private boolean checkIfUserHasRole(String roleName) {
		boolean hasUserRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.anyMatch(r->r.getAuthority().equals(roleName));
		
		return hasUserRole;
	}
	
	/**
	 * This method will check if valid user is logged in.
	 * @return boolean if user is logged in
	 */
	@ModelAttribute("valueUserLogin")
	public boolean isUserLoggedIn() {
		return SecurityContextHolder.getContext().getAuthentication()!=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
				!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
	}
	
	@ModelAttribute("hasAdminRole")
	public boolean checkIfUserHasAdminRole() {
		return checkIfUserHasRole(BlogpressConstants.rOLE_ADMIN);
	}
	
	@ModelAttribute("hadUserRole")
	public boolean checkIfUserHasUserRole() {
		return checkIfUserHasRole(BlogpressConstants.ROLE_USER);
	}


	@ModelAttribute("currentUserName")
	private String getCurrentUserName() {
		// TODO Auto-generated method stub
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private void setProcessingData(Model model, String pageTitle) {
		// TODO Auto-generated method stub
		model.addAttribute(BlogpressConstants.pAGE_TITLE, pageTitle);
		
	}

}
