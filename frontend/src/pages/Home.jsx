import React, { useState, useEffect } from 'react';
import HeroVisual from '../components/HeroVisual.jsx';
import Navbar from '../components/Navbar.jsx';
import Ticker from '../components/Ticker.jsx';
import { apiClient, getStoredAuth } from '../api/client';


function PostComments({ postId, auth }) {
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');

  const fetchComments = async () => {
    try {
      const data = await apiClient.getComments(postId);
      setComments(data || []);
    } catch (err) {
      console.error("Error fetching comments:", err);
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
        postId: postId,
        content: newComment
      });

      if (response && response.success) {
        setNewComment('');
        fetchComments();
      }
    } catch (err) {
      alert("Error sending comment");
    }
  };

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm("Are you sure you want to delete this comment?")) return;

    try {
      const response = await apiClient.deleteComment(commentId);
      if (response && response.success) {
        fetchComments();
      } else if (response && response.message) {
        alert(response.message);
      }
    } catch (err) {
      console.error("Error deleting comment:", err);
    }
  };

  return (
      <div style={{ marginTop: '1.5rem', paddingTop: '1rem', borderTop: '1px solid #222' }}>
        <h4 style={{ color: '#fff', fontSize: '0.9rem', marginBottom: '0.75rem' }}>
          Comments ({comments.length})
        </h4>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', marginBottom: '1rem' }}>
          {comments.map((c) => (
              <div key={c.id} style={{ background: '#1a1a1e', padding: '0.6rem 0.8rem', borderRadius: '6px', fontSize: '0.85rem', position: 'relative' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.25rem' }}>
                  <span style={{ color: '#3b82f6', fontWeight: '600' }}>{c.userFullName}</span>
                  <span style={{ color: '#555', fontSize: '0.7rem', marginRight: auth.userId === c.userId ? '1.5rem' : '0' }}>
                    {c.createdAt ? new Date(c.createdAt).toLocaleDateString() : ''}
                  </span>
                </div>
                <p style={{ color: '#ccc', margin: 0 }}>{c.content}</p>

                {auth.userId === c.userId && (
                    <button
                        onClick={() => handleDeleteComment(c.id)}
                        style={{ position: 'absolute', right: '0.5rem', top: '0.5rem', background: 'none', border: 'none', color: '#ef4444', cursor: 'pointer', fontSize: '0.85rem' }}
                        title="Delete comment"
                    >
                      🗑️
                    </button>
                )}
              </div>
          ))}
        </div>

        <form onSubmit={handleSendComment} style={{ display: 'flex', gap: '0.5rem' }}>
          <input
              type="text"
              placeholder="Write a comment..."
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              style={{ flex: 1, padding: '0.5rem 0.75rem', background: '#1a1a1e', border: '1px solid #333', borderRadius: '6px', color: '#fff', fontSize: '0.85rem', outline: 'none' }}
              required
          />
          <button type="submit" style={{ background: '#3b82f6', color: '#fff', padding: '0.5rem 1rem', borderRadius: '6px', fontSize: '0.85rem', border: 'none', fontWeight: '500', cursor: 'pointer' }}>
            Send
          </button>
        </form>
      </div>
  );
}


export default function Home() {
  const [posts, setPosts] = useState([]);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const auth = getStoredAuth();

  const fetchPosts = async () => {
    try {
      setLoading(true);
      const data = await apiClient.getPosts();
      setPosts(data || []);
    } catch (err) {
      setError('Failed to load community posts.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (auth) {
      fetchPosts();
    }
  }, []);

  const handleSubmitPost = async (e) => {
    e.preventDefault();
    if (!title.trim() || !content.trim()) return;

    try {
      const response = await apiClient.createPost({ title, content });
      if (response.success) {
        setTitle('');
        setContent('');
        fetchPosts();
      }
    } catch (err) {
      alert(err.message || 'Error creating post.');
    }
  };

  return (
      <main className="app-shell home-page" id="home">
        <Navbar />

        <section className="hero section home-hero">
          <div className="container hero__grid">
            <div className="hero__content">
              <p className="eyebrow">Player matchmaking platform</p>
              <h1 className="hero__title">
                <span className="hero__title-line">Own</span>
                <span className="hero__title-line">The</span>
                <span className="hero__title-line">Court</span>
                <span className="hero__title-line hero__title-line--outline">Play</span>
                <span className="hero__title-line hero__title-line--outline">After</span>
                <span className="hero__title-line hero__title-line--outline">Dark</span>
              </h1>
              <p className="hero__description">
                SporTool gives players seamless matchmaking — find rivals at your level, lock court time, and get from queue to first serve.
              </p>
            </div>
            <div className="hero__visual-wrap">
              <HeroVisual />
            </div>
          </div>
        </section>

        {auth && (
            <section className="section community-feed" style={{ padding: '4rem 0', background: '#0a0a0c' }}>
              <div className="container" style={{ maxWidth: '800px', margin: '0 auto', padding: '0 1rem' }}>

                {/* Post Creation Form */}
                <form onSubmit={handleSubmitPost} style={{ background: '#121214', padding: '2rem', borderRadius: '12px', border: '1px solid #222', marginBottom: '3rem' }}>
                  <h3 style={{ fontSize: '1.25rem', fontWeight: '600', marginBottom: '1rem', color: '#fff' }}>Share an Update 📢</h3>
                  <div style={{ marginBottom: '1rem' }}>
                    <input
                        type="text"
                        placeholder="Post Title..."
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        style={{ width: '100%', padding: '0.75rem', background: '#1a1a1e', border: '1px solid #333', borderRadius: '6px', color: '#fff', outline: 'none' }}
                        required
                    />
                  </div>
                  <div style={{ marginBottom: '1rem' }}>
                <textarea
                    placeholder="What's on your mind?..."
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    rows="3"
                    style={{ width: '100%', padding: '0.75rem', background: '#1a1a1e', border: '1px solid #333', borderRadius: '6px', color: '#fff', outline: 'none', resize: 'vertical' }}
                    required
                />
                  </div>
                  <button type="submit" style={{ background: '#3b82f6', color: '#fff', padding: '0.6rem 1.5rem', borderRadius: '6px', fontWeight: '500', cursor: 'pointer', border: 'none' }}>
                    Publish Post
                  </button>
                </form>

                {/* Feed Display */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                  <h2 style={{ fontSize: '1.5rem', fontWeight: '700', color: '#fff', borderBottom: '1px solid #222', paddingBottom: '0.5rem' }}>Community Feed</h2>

                  {loading && <p style={{ color: '#888' }}>Loading posts...</p>}
                  {error && <p style={{ color: '#ef4444' }}>{error}</p>}

                  {!loading && posts.length === 0 && (
                      <p style={{ color: '#888' }}>No posts available yet.</p>
                  )}

                  {posts.map((post) => {
                    const isMyPost = auth.userId === post.userId;

                    return (
                        <div key={post.id} style={{ background: '#121214', padding: '1.5rem', borderRadius: '12px', border: '1px solid #222' }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <h3 style={{ fontSize: '1.25rem', fontWeight: '700', color: '#fff' }}>{post.title}</h3>
                            {isMyPost ? (
                                <span style={{ background: 'rgba(59, 130, 246, 0.2)', color: '#3b82f6', padding: '0.25rem 0.6rem', borderRadius: '4px', fontSize: '0.75rem', fontWeight: '600' }}>
                              My Post 👤
                            </span>
                            ) : (
                                <span style={{ background: 'rgba(255, 255, 255, 0.05)', color: '#aaa', padding: '0.25rem 0.6rem', borderRadius: '4px', fontSize: '0.75rem' }}>
                              By Someone Else 🌍
                            </span>
                            )}
                          </div>
                          <p style={{ color: '#aaa', marginTop: '0.5rem', lineHeight: '1.6', whiteSpace: 'pre-wrap' }}>{post.content}</p>
                          <div style={{ marginTop: '1rem', display: 'flex', justifyContent: 'flex-end', fontSize: '0.75rem', color: '#666' }}>
                            <span>{post.createdAt ? new Date(post.createdAt).toLocaleDateString('en-US') : ''}</span>
                          </div>

                          <PostComments postId={post.id} auth={auth} />
                        </div>
                    );
                  })}
                </div>

              </div>
            </section>
        )}

        <Ticker />
      </main>
  );
}