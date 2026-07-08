import React, { useState, useEffect } from 'react';
import HeroVisual from '../components/HeroVisual.jsx';
import Navbar from '../components/Navbar.jsx';
import Ticker from '../components/Ticker.jsx';
import { apiClient, getStoredAuth } from '../api/client';
import PostCard from '../components/PostCard.jsx';

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

  const removePostFromFeed = (postId) => {
    setPosts((currentPosts) => currentPosts.filter((post) => post.id !== postId));
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

                  {posts.map((post) => (
                    <PostCard key={post.id} post={post} onPostDeleted={removePostFromFeed} />
                  ))}
                </div>

              </div>
            </section>
        )}

        <Ticker />
      </main>
  );
}
