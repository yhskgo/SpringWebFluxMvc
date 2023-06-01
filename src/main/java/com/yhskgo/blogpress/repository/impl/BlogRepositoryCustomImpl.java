package com.yhskgo.blogpress.repository.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.IncludeExclude;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhskgo.blogpress.model.Blog;
import com.yhskgo.blogpress.model.Comment;
import com.yhskgo.blogpress.repository.BlogRepositoryCustom;

@Repository
public class BlogRepositoryCustomImpl implements BlogRepositoryCustom {
	
	private static Logger logger = LoggerFactory.getLogger(BlogRepositoryCustomImpl.class);
	
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Override
	public List<Comment> getAllComments(int from, int size) {
		// TODO Auto-generated method stub
		NestedAggregationBuilder aggregation = AggregationBuilders.nested("aggChild", "comments").
				subAggregation(AggregationBuilders.topHits("aggSortComment").sort("comments.createDate", SortOrder.DESC).from(from).size(size));
		SearchResponse response = elasticsearchTemplate.getClient().prepareSearch("blog")
				.setTypes("blog")
				.addAggregation(aggregation)
				.execute().actionGet();
		
		List<Aggregation> responseAggregations = response.getAggregations().asList();
		return getAllCommentsFromJson(responseAggregations.get(0).toString());
	}
	
	public List<Comment> getCommentsForStatus(String status, int from, int size) {
		IncludeExclude includeExclude = new IncludeExclude(status, null);
		
		NestedAggregationBuilder aggregation = AggregationBuilders.nested("aggChild", "comments").
				subAggregation(AggregationBuilders.terms("aggStatsComment").
				field("comments.status").
				includeExclude(includeExclude).
				subAggregation(AggregationBuilders.topHits("aggSortComment").size(10).sort("comments.createdDate", SortOrder.DESC)));
		
		SearchResponse response = elasticsearchTemplate.getClient().prepareSearch("blog")
				.setTypes("blog")
				.addAggregation(aggregation)
				.execute().actionGet();
		
		List<Aggregation> responseAgg = response.getAggregations().asList();
		
		return getAllCommentsWithStatusFromJson(responseAgg.get(0).toString());
	}
	
	private List<Comment> getAllCommentsWithStatusFromJson(String commentStatusJsonStr) {
		String commentArrStr = "{}";
		List<Comment> allComments = new ArrayList<Comment>();
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		if(commentStatusJsonStr!=null) {
			JSONObject commentJsonObj = new JSONObject(commentStatusJsonStr);
			if(commentJsonObj.getJSONObject("aggChild")!=null) {
				JSONObject aggChildObj = commentJsonObj.getJSONObject("aggChild");
				if(aggChildObj!=null && aggChildObj.getJSONObject("aggStatsComment")!=null) {
					JSONObject aggStatsCommentObj = aggChildObj.getJSONObject("aggStatsComment");
					if(aggStatsCommentObj!=null && aggStatsCommentObj.getJSONObject("buckets")!=null) {
						JSONObject commentStatusBucketObj = aggStatsCommentObj.getJSONArray("buckets").getJSONObject(0);
						if(commentStatusBucketObj!=null && commentStatusBucketObj.getJSONObject("aggSortComment") !=null) {
							JSONObject aggSortCommentObj = commentStatusBucketObj.getJSONObject("aggSortComment");
							if(aggSortCommentObj!=null && aggSortCommentObj.getJSONObject("hits")!=null) {
								JSONObject hitObject = aggSortCommentObj.getJSONObject("hits");
								
								try {
									if(hitObject!=null && hitObject.get("hits")!=null) {
										JSONArray aggCommentArr = hitObject.getJSONArray("hits");
										for (int jsonIndex = 0; jsonIndex < aggCommentArr.length(); jsonIndex++) {
											allComments.add(objectMapper.readValue(aggCommentArr.getJSONObject(jsonIndex).getJSONObject("_source").toString(), Comment.class));
										}
									}
								} catch (JSONException|IOException e) {
									// TODO: handle exception
									logger.error("error occurred while fetching all comments "+e.getMessage(), e);
								}
							}
							
						}
					}
				}
			}
		}
		
		return null;
	}

	public int getCurrentChildSequence(String blogId, String parentCommentId) {
		
		int currentChildSeq=0;
		TermQueryBuilder termQueryBuilder = new TermQueryBuilder("comments.parentId", parentCommentId);
		NestedAggregationBuilder aggregationBuilder = AggregationBuilders.nested("aggChild", "comments").
				subAggregation(AggregationBuilders.filter("filterParentId", termQueryBuilder).
						subAggregation(AggregationBuilders.max("maxChildSeq").field("comments,childSequence")));
		
		TermQueryBuilder rootTermQueryBuilder = new TermQueryBuilder("_id", blogId);
		
		SearchResponse response = elasticsearchTemplate.getClient().prepareSearch("blog").setTypes("blog")
				.addAggregation(aggregationBuilder)
				.execute().actionGet();
		
		if(response !=null) {
			if(response.getAggregations()!=null) {
				List<Aggregation> aggList = response.getAggregations().asList();
				if(aggList!=null) {
					Aggregation resultAgg = aggList.get(0);
					if(resultAgg!= null) {
						currentChildSeq = getMaxChildSequenceFromJson(resultAgg.toString());
					}
				}
			}
		}
		
		currentChildSeq = currentChildSeq+1;
	
		return currentChildSeq;
	}
	
	@Override
	public List<Blog> search(String searchText) {
		QueryBuilder booleanQry = QueryBuilders.boolQuery()
				.should(QueryBuilders.termQuery("title", searchText))
				.should(QueryBuilders.termQuery("body", searchText));
		
		SearchResponse response = elasticsearchTemplate.getClient().prepareSearch("blog")
				.setTypes("blog")
				.setQuery(booleanQry)
				.execute().actionGet();
		
		List<Blog> blogSearchResultList = getBlogListFromSearchJSON(response.toString());
		
		return blogSearchResultList;
	}
	
	private List<Blog> getBlogListFromSearchJSON(String jsonResponse) {
		
		List<Blog> blogResults = new ArrayList<Blog>();
		ObjectMapper objectMapper = new ObjectMapper();
		if(jsonResponse!=null) {
			JSONObject searchJson = new JSONObject(jsonResponse);
			if(searchJson.get("hits")!=null) {
				JSONObject resultJson = searchJson.getJSONObject("hits");
				try {
					if(resultJson != null && resultJson.get("hits")!=null) {
						JSONArray blogArr = resultJson.getJSONArray("hits");
						for (int jsonIndex = 0; jsonIndex < blogArr.length(); jsonIndex++) {
							blogResults.add(objectMapper.readValue(blogArr.getJSONObject(jsonIndex).getJSONObject("_source").toString(), Blog.class));
						}
					}
				} catch (JSONException|IOException e) {
					// TODO: handle exception
					logger.error("error occurred while fetching all comments" +e.getMessage(), e);
				}
			}
		}
		
		return blogResults;
	}

	private int getMaxChildSequenceFromJson(String aggJson) {
	
	int childSequence =0;
	double maxChildSeq =0.0;
	if(aggJson!=null) {
		JSONObject commentJsonObject = new JSONObject(aggJson);
		if(commentJsonObject.get("aggChild")!=null) {
			JSONObject aggChildObject = commentJsonObject.getJSONObject("aggChild");
			if(aggChildObject!=null && aggChildObject.getJSONObject("filterParentId")!=null) {
				JSONObject filteredJsonObject = aggChildObject.getJSONObject("filterParentId");
				if(filteredJsonObject!=null && filteredJsonObject.getJSONObject("maxChildSeq")!=null) {
					JSONObject maxChildSeqObject = filteredJsonObject.getJSONObject("maxChildSeq");
					if(maxChildSeqObject!=null && maxChildSeqObject.get("value")!=null && !JSONObject.NULL.equals(maxChildSeqObject.get("value"))) {
						maxChildSeq = (double) maxChildSeqObject.get("value");
						childSequence = (int) maxChildSeq;
					}
				}
			}
		}
	}
	return childSequence;
}

	private List<Comment> getAllCommentsFromJson(String commentJsonStr) {
		String commentArrString = "{}";
		List<Comment> allComments = new ArrayList<Comment>();
		
		ObjectMapper objMapper = new ObjectMapper();
		if(commentJsonStr!=null) {
			JSONObject commentJsonObj = new JSONObject(commentArrString);
			if(commentJsonObj.getJSONObject("aggChild")!=null) {
				JSONObject aggChileObj=commentJsonObj.getJSONObject("aggChild");
				if(aggChileObj!=null && aggChileObj.getJSONObject("aggSortComment")!=null) {
					JSONObject aggSortCommentObj = aggChileObj.getJSONObject("aggSortComment");
					if(aggSortCommentObj!=null&&aggSortCommentObj.getJSONObject("hits")!=null) {
						JSONObject hitObject = aggSortCommentObj.getJSONObject("hits");
						try {
							if(hitObject!=null && hitObject.get("hits")!=null) {
								JSONArray aggCommentArr = hitObject.getJSONArray("hits");
								for (int jsonIndex = 0; jsonIndex < aggCommentArr.length(); jsonIndex++) {
									allComments.add(objMapper.readValue(aggCommentArr.getJSONObject(jsonIndex).getJSONObject("_source").toString(), Comment.class));
									
								}
							}
						} catch (JSONException|IOException e) {
							// TODO: handle exception
							logger.error("error occurred while fetching all comments "+e.getMessage(), e);
						}
					}
				}
				
			}
			
		}
		return allComments;
	}


	

	

	

	

}
