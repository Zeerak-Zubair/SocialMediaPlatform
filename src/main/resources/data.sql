INSERT INTO users (username, email, password, profile_picture, bio, role) VALUES
('alice', 'alice@example.com', 'password123', 'alice.jpg', 'I love coding!', 'USER'),
('bob', 'bob@example.com', 'securepass', 'bob.jpg', 'Java Developer', 'USER'),
('carol', 'carol@example.com', 'mypassword', 'carol.jpg', 'Spring Boot Enthusiast', 'USER'),
('dave', 'dave@example.com', 'pass1234', 'dave.jpg', 'Tech Blogger', 'ADMIN'),
('eve', 'eve@example.com', 'hello123', 'eve.jpg', 'Data Scientist', 'USER');


INSERT INTO posts (user_id, content) VALUES
(1, 'Hello, world! This is my first post.'),
(2, 'Kotlin is amazing!'),
(3, 'Spring Boot makes backend development easy.'),
(1, 'Just started learning SQL!'),
(4, 'Exploring AI and Machine Learning concepts.');

INSERT INTO comments (user_id, post_id, content) VALUES
(2, 1, 'Welcome to the platform, Alice!'),
(3, 1, 'Great to see you here!'),
(1, 2, 'I agree! Kotlin is super cool.'),
(4, 3, 'Spring Boot is indeed powerful.'),
(5, 4, 'SQL is fundamental for database management.');

INSERT INTO follows (follower_id, following_id) VALUES
(1, 2), -- Alice follows Bob
(1, 3), -- Alice follows Carol
(2, 3), -- Bob follows Carol
(3, 4), -- Carol follows Dave
(4, 5), -- Dave follows Eve
(5, 1); -- Eve follows Alice

INSERT INTO post_likes (post_id, user_id) VALUES
(1, 2), -- Bob likes Alice's post
(1, 3), -- Carol likes Alice's post
(2, 1), -- Alice likes Bob's post
(3, 4), -- Dave likes Carol's post
(4, 5); -- Eve likes Alice's second post
