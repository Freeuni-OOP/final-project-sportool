import { useState } from 'react';
import PostComments from './PostComments.jsx';
import { apiClient, getStoredAuth } from '../api/client.js';

export default function PostCard({
  post,
  showComments = true,
  expandableComments = false,
  onPostDeleted,
}) {
  const [commentsOpen, setCommentsOpen] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState('');
  const auth = getStoredAuth();
  const isMyPost = auth?.userId === post.userId;
  const shouldShowComments = auth && (showComments || (expandableComments && commentsOpen));
  const createdAtLabel = post.createdAt
    ? new Date(post.createdAt).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
      })
    : '';

  const authorLabel = isMyPost ? 'You' : 'Club Member';

  const handleDeletePost = async () => {
    if (isDeleting || !window.confirm('Delete this post?')) return;

    setIsDeleting(true);
    setDeleteError('');

    try {
      const response = await apiClient.deletePost(post.id);
      if (response?.success) {
        onPostDeleted?.(post.id);
      } else {
        setDeleteError(response?.message || 'Could not delete this post.');
      }
    } catch (error) {
      setDeleteError(error.message || 'Could not delete this post.');
    } finally {
      setIsDeleting(false);
    }
  };

  const postSummary = (
    <>
      <header className="post-card__header">
        <div className="post-card__author">
          <div className="post-card__avatar" aria-hidden="true">
            {authorLabel.charAt(0)}
          </div>
          <div className="post-card__meta">
            <span className="post-card__author-name">{authorLabel}</span>
            <span className="post-card__date">{createdAtLabel}</span>
          </div>
        </div>

        {isMyPost ? (
          <span className="post-card__badge post-card__badge--mine">My Post</span>
        ) : (
          <span className="post-card__badge">Community</span>
        )}
      </header>

      <div className="post-card__body">
        <h3 className="post-card__title">{post.title}</h3>
        <p className="post-card__content">{post.content}</p>
      </div>
    </>
  );

  return (
    <article className={`post-card${expandableComments ? ' post-card--clickable' : ''}`}>
      {expandableComments ? (
        <button
          type="button"
          className="post-card__summary"
          aria-expanded={commentsOpen}
          onClick={() => setCommentsOpen((isOpen) => !isOpen)}
        >
          {postSummary}
          <span className="post-card__comments-action">
            {commentsOpen ? 'Hide comments' : 'View comments'}
          </span>
        </button>
      ) : (
        postSummary
      )}

      {isMyPost ? (
        <div className="post-card__actions">
          {deleteError ? <span className="post-card__error">{deleteError}</span> : null}
          <button
            type="button"
            className="post-card__delete"
            onClick={handleDeletePost}
            disabled={isDeleting}
          >
            {isDeleting ? 'Deleting...' : 'Delete post'}
          </button>
        </div>
      ) : null}

      {shouldShowComments ? (
        <footer className="post-card__footer">
          <PostComments postId={post.id} auth={auth} />
        </footer>
      ) : null}
    </article>
  );
}

