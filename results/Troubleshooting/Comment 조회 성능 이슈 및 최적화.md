### 📌 Comment 조회 성능 이슈 및 최적화

---

## 1️⃣ 루트 댓글 조회 (`getRootComments`)

### 🔍 현재 코드

```java
// CommentService.java
@Transactional(readOnly = true)
public Page<CommentHierarchyDTO> getRootComments(Long postId, Pageable pageable) {
    Page<Comment> rootComments = commentRepository.findByPostIdAndParentCommentIsNull(postId, pageable);
    List<CommentHierarchyDTO> hierarchyDTOs = rootComments.stream()
            .map(comment -> buildCommentHierarchy(comment, true, true))
            .collect(Collectors.toList());
    return new PageImpl<>(hierarchyDTOs, pageable, rootComments.getTotalElements());
}

// CommentRepository.java
Page<Comment> findByPostIdAndParentCommentIsNull(Long postId, Pageable pageable);

```

### ⚠️ 발생하는 문제

- **N+1 문제 발생**
    - 각 댓글마다 `User` 정보 조회 쿼리 발생
    - 각 댓글마다 `childrenComments` 조회 쿼리 발생

### 📌 실행되는 SQL

```sql
SELECT * FROM comments WHERE post_id = ? AND parent_comment_id IS NULL;
SELECT * FROM users WHERE id = ?;  -- 여러 번 실행
SELECT * FROM comments WHERE parent_comment_id = ?;  -- 여러 번 실행

```

### ✅ 해결 방안

```java

// CommentRepository.java
@Query("""
    SELECT DISTINCT c FROM Comment c
    LEFT JOIN FETCH c.user
    WHERE c.post.id = :postId
    AND c.parentComment IS NULL
    ORDER BY c.createdDate DESC
    """)
Page<Comment> findRootComments(@Param("postId") Long postId, Pageable pageable);

```

```yaml

# application.yml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100

```

---

## 2️⃣ 대댓글 전체 조회 (`getAllChildComments`)

### 🔍 현재 코드

```java
// CommentService.java
@Transactional(readOnly = true)
public List<CommentHierarchyDTO> getAllChildComments(Long parentCommentId) {
    Comment parentComment = commentRepository.findById(parentCommentId)
            .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

    return parentComment.getChildrenComments().stream()
            .sorted(Comparator.comparing(Comment::getCreatedDate))
            .map(this::buildCommentHierarchyWithAllDescendants)
            .collect(Collectors.toList());
}
```

### ⚠️ 발생하는 문제

- **전체 대댓글을 한 번에 메모리에 로딩**
- **N+1 문제 발생**
- **페이징 처리 없음**

### ✅ 해결 방안

```java

// CommentRepository.java
@Query("""
    SELECT DISTINCT c FROM Comment c
    LEFT JOIN FETCH c.user
    WHERE c.parentComment.id = :parentId
    ORDER BY c.createdDate
    """)
Page<Comment> findChildCommentsWithUser(
    @Param("parentId") Long parentId,
    Pageable pageable
);

// CommentService.java
@Transactional(readOnly = true)
public Page<CommentHierarchyDTO> getChildComments(Long parentCommentId, Pageable pageable) {
    Page<Comment> childComments = commentRepository.findChildCommentsWithUser(parentCommentId, pageable);
    List<CommentHierarchyDTO> dtos = childComments.stream()
            .map(comment -> buildCommentHierarchy(comment, false, false))  // buildCommentHierarchy 메서드 사용
            .collect(Collectors.toList());
            
    return new PageImpl<>(dtos, pageable, childComments.getTotalElements());
}

```

---

## 3️⃣ 좋아요 관련 조회 (`likeComment`)

### 🔍 현재 코드

```java

// CommentService.java
@Transactional
public void likeComment(Long commentId, Long userId) {
    Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

    if (comment.getLikes().stream()
            .anyMatch(like -> like.getUser().getId().equals(userId))) {
        throw new AlreadyLikedException("Already liked this comment");
    }
    // ...
}

```

### ⚠️ 발생하는 문제

- **전체 `likes` 컬렉션을 메모리에 로딩**
- **단순 존재 여부 체크에 불필요한 데이터 조회**

### ✅ 해결 방안

```java

// CommentRepository.java
@Query("""
    SELECT EXISTS (
        SELECT 1 FROM CommentLike cl
        WHERE cl.comment.id = :commentId
        AND cl.user.id = :userId
    )
    """)
boolean existsByCommentIdAndUserId(
    @Param("commentId") Long commentId,
    @Param("userId") Long userId
);

// CommentService.java
@Transactional
public void likeComment(Long commentId, Long userId) {
    if (commentRepository.existsByCommentIdAndUserId(commentId, userId)) {
        throw new AlreadyLikedException("Already liked this comment");
    }
    // ... 나머지 로직
}

```