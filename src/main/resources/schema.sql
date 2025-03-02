create database user_posts;

use user_posts;

CREATE TABLE users(
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      username VARCHAR(100) UNIQUE NOT NULL,
                      email VARCHAR(100) UNIQUE NOT NULL,
                      password VARCHAR(100) NOT NULL,
                      profile_picture VARCHAR(100),
                      bio TEXT,
                      role VARCHAR(100) NOT NULL
);

-- A Post has one user.

CREATE TABLE posts(
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      user_id BIGINT NOT NULL,
                      content TEXT NOT NULL,
                      timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- A post has multiple comments.

CREATE TABLE comments(
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         post_id BIGINT NOT NULL,
                         content TEXT NOT NULL,
                         timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- A user can follow multiple users (except themselves).
-- User - Follow {Many to Many Relationship}
CREATE TABLE follows(
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        follower_id BIGINT NOT NULL,
                        following_id BIGINT NOT NULL,
                        FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
                        FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
                        UNIQUE (follower_id, following_id), -- Prevent duplicate follows,
                        CHECK (follower_id <> following_id) -- Prevent self-following
);

-- A user can like a post.
-- A post can have multiple likes.
CREATE TABLE post_likes(
                           post_id BIGINT NOT NULL,
                           user_id BIGINT NOT NULL,
                           FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                           PRIMARY KEY (post_id,user_id)
);