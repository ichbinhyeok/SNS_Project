package com.example.sns_project.projection;

import java.time.LocalDateTime;

public interface CommentHierarchyProjection {
    Long getId();                      // id
    String getContent();                // content
    LocalDateTime getCreatedDate();     // created_date
    LocalDateTime getModifiedDate();    // modified_date
    Integer getDepth();                 // depth
    Long getParentCommentId();          // parent_comment_id
    Long getPostId();                   // post_id
    Long getUserId();                   // user_id
    String getAuthorName();             // author_name
    String getAuthorEmail();            // author_email
    Integer getHierarchyDepth();        // hierarchy_depth
    String getPath();                   // path
    Long getReplyCount();  // 추가

}
