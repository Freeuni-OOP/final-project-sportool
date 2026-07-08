import React, { useEffect, useState } from 'react';
import { apiClient } from '../api/client.js';

export default function PostComments({ postId, auth }) {
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');

  const fetchComments = async () => {
    try {
      const data = await apiClient.getComments(postId);
      setComments(data || []);
    } catch (err) {
      console.error('Error fetching comments:', err);
    }
  };

  useEffect(() => {
    fetchComments();
  }, [postId]);

  const handleSendComment = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;

    try {
      const response = await apiClient.createComment({
        postId,
        content: newComment,
      });

      if (response && response.success) {
        setNewComment('');
        fetchComments();
      }
    } catch (err) {
      alert('Error sending comment');
    }
  };

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm('Are you sure you want to delete this comment?')) return;

    try {
      const response = await apiClient.deleteComment(commentId);
      if (response && response.success) {
        fetchComments();
      } else if (response && response.message) {
        alert(response.message);
      }
    } catch (err) {
      console.error('Error deleting comment:', err);
    }
  };

  return (
    <div className="post-comments">
      <h4 className="post-comments__title">Comments ({comments.length})</h4>

      <div className="post-comments__list">
        {comments.map((c) => (
          <div key={c.id} className="post-comments__item">
            <div className="post-comments__item-header">
              <span className="post-comments__author">{c.userFullName}</span>
              <span className="post-comments__date">
                {c.createdAt ? new Date(c.createdAt).toLocaleDateString() : ''}
              </span>
            </div>
            <p className="post-comments__content">{c.content}</p>
            {auth.userId === c.userId && (
              <button
                type="button"
                className="post-comments__delete"
                onClick={() => handleDeleteComment(c.id)}
              >
                Delete
              </button>
            )}
          </div>
        ))}
      </div>

      <form className="post-comments__form" onSubmit={handleSendComment}>
        <input
          type="text"
          placeholder="Write a comment..."
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          required
        />
        <button type="submit">Send</button>
      </form>
    </div>
  );
}

