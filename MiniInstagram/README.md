## Outline
- [Design Considerations](https://github.com/KunyueXing/Mini-Instagram/blob/main/MiniInstagram/README.md#design-considerations)
- [Requirements and Object-oriented Design](https://github.com/KunyueXing/Mini-Instagram/blob/main/MiniInstagram/README.md#requirements-and-object-oriented-design)
  - [Requirements](https://github.com/KunyueXing/Mini-Instagram/blob/main/MiniInstagram/README.md#requirements)  
  - [Use case analysis](https://github.com/KunyueXing/Mini-Instagram/blob/main/MiniInstagram/README.md#use-case-analysis)
  - [Class diagram](https://github.com/KunyueXing/Mini-Instagram/blob/main/MiniInstagram/README.md#class-diagram)
- [User Interface Design](https://github.com/KunyueXing/Mini-Instagram/blob/main/MiniInstagram/README.md#user-interface-design)
- [Data model](https://github.com/KunyueXing/Mini-Instagram/blob/main/MiniInstagram/README.md#data-model)
  - [Database Rules](https://github.com/KunyueXing/Mini-Instagram/blob/main/MiniInstagram/README.md#database-rules)
- [References](https://github.com/KunyueXing/Mini-Instagram/blob/main/MiniInstagram/README.md#references)

## Design Considerations
   This application is developed in Android Studio using Android API level 31.

## Requirements and Object-oriented Design
### Requirements
1. A user can create an account with email, username and password. 
2. A user can edit profile, e.g. add or update avatar, links, biography, etc.
3. A user can set account visibility to public or private. 
4. A user can follow another user. 
5. A user can unfollow another user.
6. A user can remove a follower.
7. A user can block another user.
8. A user can create a Following Group. 
9. A user can delete a Following Group.
10. A user can add a Following to an existing group.
11. A user can remove a Following from an existing group.
12. When trying to follow a private User A, User B must send a Follow Request to User A. User B can follow User A only if User A approves the Follow Request.
15. A user can search for another user by username.
16. A user can create a post along with an image and optional texts.
17. A user can choose to show posts from all Followings or from a specific Following Group, in chronological order.
19. A user can like a post. 
20. A user can comment on a post.
21. A user can like a comment. 
22. A user can reply to a comment.
23. An in-app notification is sent to User A when User B follows User A, User B sends a Follow Request to User A, User B approves User A's Follow Request, User B likes or comments on one of User A's posts or comments.
24. A user can check her own or another user's Followers list and Following list
25. A user can check her own or another user's posts list.

### Use case analysis
#### Actors
- Primary actors:
    - User, who can create a profile of themselves with personal info. They can create posts with pics and words. They can interact with other users, e.g., follow/unfollow others, block others, group following, comment and like other users’ posts and comments.
- Secondary actors: 
    - System, responsible for sending out notifications for new followers, comments, likes, etc.  

#### Use cases
- User
    - add/ update profile: add or update information such as avatar, username, and bio.
    - set account privacy
    - follow/ unfollow / group following / unfollow other users 
    - remove follower / block other users
    - set / edit the group of their following users
    - add/ delete post: To add a new post, or delete an existing post
    - set post privacy
    - like / comment post: to like a post, comment on a post
    - add / delete / like comment: To add a new comment, like a particular comment, or delete a comment
    - Search users: To search for other users
    - check follower / following list
    - check posts list
 - System
    - send new follower notification
    - send new comment notifications
    - send new like notification
    - send new following request notification

### Class diagram
<br>
<picture>
 <img alt="class diagram" src="https://firebasestorage.googleapis.com/v0/b/mini-instagram-1.appspot.com/o/Doc%2Fins.drawio.png?alt=media&token=4d0c1764-9248-44b3-9911-991d30ffe115">
</picture>
<br>

## User Interface Design
1. User register / login page
<br>
<picture>
 <img alt="register and login" src="https://firebasestorage.googleapis.com/v0/b/mini-instagram-1.appspot.com/o/Doc%2Flogin.png?alt=media&token=b5884e5f-839a-45b0-a9ea-37a5a3aa0d74" width="600" height="600">
</picture>
<br>

2. Home page, home fragment
<br>
<picture>
 <img alt="home" src="https://firebasestorage.googleapis.com/v0/b/mini-instagram-1.appspot.com/o/Doc%2Fhomepage-edit.jpg?alt=media&token=32fbffe6-59f5-407e-8334-3807b7726242" width="300" height="600">
</picture>
<br>

3. New post page
<br>
<picture>
 <img alt="new post page" src="https://firebasestorage.googleapis.com/v0/b/mini-instagram-1.appspot.com/o/Doc%2Fnewpost.jpg?alt=media&token=3e28ef14-cc97-4c3d-9a2b-5ed9d05888d1" width="300" height="600">
</picture>
<br>

4. Person fragment
<br>
   User's own page:
<br>
<picture>
 <img alt="person" src="https://firebasestorage.googleapis.com/v0/b/mini-instagram-1.appspot.com/o/Doc%2Fprofile.png?alt=media&token=6f81b0a9-58c0-4db4-9e7c-9953a76521b3" width="300" height="600">
</picture>
<br>

<br>
    When checking other users page, there's a menu pop up when clicking follow/following
<br>
<picture>
 <img alt="person" src="https://firebasestorage.googleapis.com/v0/b/mini-instagram-1.appspot.com/o/Doc%2Fother%20user%20profile.png?alt=media&token=b134a87f-e0da-4c77-89e4-33c593d4516b" width="300" height="600">
</picture>
<br>

<br>
    when click on a photo in grid layout, show post detail as follow:
<br>
<picture>
 <img alt="post detail" src="https://firebasestorage.googleapis.com/v0/b/mini-instagram-1.appspot.com/o/Doc%2Fpost%20detail.png?alt=media&token=1b634cd9-df2d-4267-b81f-de1eb274738a" width="300" height="600">
</picture>
<br>

5. following / follower list
<br>
<picture>
 <img alt="following / follower list" src="https://firebasestorage.googleapis.com/v0/b/mini-instagram-1.appspot.com/o/Doc%2Ffollowing%20list.png?alt=media&token=d2d399f6-3606-4cc2-b8be-7e3a1e8cdaa3" width="800" height="600">
</picture>
<br>

6. Search fragment
<br>
<picture>
 <img alt="search" src="https://firebasestorage.googleapis.com/v0/b/mini-instagram-1.appspot.com/o/Doc%2Fsearch.png?alt=media&token=52e90f44-8d2f-4792-a252-4bd6ed0999c9" width="300" height="600">
</picture>
<br>

7. Notification fragment
<br>
<picture>
 <img alt="notification" src="https://firebasestorage.googleapis.com/v0/b/mini-instagram-1.appspot.com/o/Doc%2Fnotification.png?alt=media&token=aebe6f94-7dab-4f36-8317-c2e94ef03350" width="300" height="600">
</picture>
<br>

8. User profile setting page
<br>
<picture>
 <img alt="profile setting" src="https://firebasestorage.googleapis.com/v0/b/mini-instagram-1.appspot.com/o/Doc%2Fedit%20profile.png?alt=media&token=d41e51bb-20a0-473b-972b-34399cc67de3" width="300" height="600">
</picture>
<br>

9. Comment page
<br>
<picture>
 <img alt="Comment page" src="https://firebasestorage.googleapis.com/v0/b/mini-instagram-1.appspot.com/o/Doc%2Fcomments.png?alt=media&token=0623ebcb-d734-47c1-9ce0-a24057b939dd" width="300" height="600">
</picture>
<br>

## Data model
The images of the application stored in Firebase Storage. All data is stored in Firebase realtime database.\
\
The data model design uses some of the Firebase best practices, It has some tradeoffs made for simplicity that would not scale to very large number of users. \
\
The database has eight "root" nodes
1. `Users`
   - a list of `User` objects
   - key: user ID, `<ID>`
   - value: a hashmap of user profile, e.g. `/users/<ID>/email` is the email of the user with id=`<ID>`
      | key       |     value     |
      |-----:     |---------------|
      | email     |               |
      |username   |               |
      |profile pic|               |
      |   ...     |               |
2. `Posts`
    - a list of `Post` objects
    - key: randomly generated push ID, `<POST-ID>`
    - value: a hashmap contain information such as authorID, content, imageURL, etc.
      | key       |     value     |
      |-----:     |---------------|
      | UID       |               |
      |content    |               |
      |authorID   |               |
      |   ...     |               |
3. `Comments`
     - a list of `Comment` objects
     - key: randomly generated push ID, `<Comment-ID>`
     - value: a hashmap contain information such as authorID, content, etc.
4. `User-posts`
     - a list of posts posted by the user
     - key: user ID, 
     - `/User-posts/<USER-ID>/` is a list of all posts made by a certain user, keyed by the same push ID usded in the `Posts` tree.
     - This makes it easy to query "all posts by a specific user" without filtering through all `Post` objects.
5. `Post-comments`
     - a list of comments under the post
     - key: post ID, 
     - `/Post-comments/<POST-ID>/` is a list of all comments on a certain post with id `<POST-ID>`, keyed by the same push ID usded in the `Comments` tree.
     - Similarly, this is designed to accelerate query "all comments under a specific user" without filtering through all `Comment` objects. By keeping this data in its own tree rather than nesting it under `Posts`, we make it possible to load a post without loading all comments while still having a known path to access all comments for a particular post.
6. `Comment-comments`
     - a list of comments under the comment
     - key: comment ID
     - `/Comment-comments/<Comment-ID>/` is a list of all comments under a certain comment, keyed by the same push ID usded in the `Comments` tree.
7. `User-following`
     - a list of users followed by the user
     - key: userID
        - key: userID,
        - value: true 
        - `/User-following/<USER-ID>/<GROUP-ID>`, is a list of users followed by a specific user and group under a certain group, keyed by the user ID
     - This makes it easy to query "all users followed by a specific user", etc.
8. `User-followedby`
     - a list of users following the specific user
     - key: userID
       - key: userID
       - value: true      
9. `User-group`
     - a list of users following the specific user under a certain group
     - key: userID
       - key: groupID
         - key: userID
         - value: true    
    - This makes it easy to query " a list of users followed and grouped by a specific user", etc.
10. `Likes`
    * a list of users liked a specific post
    * key: postID
      * key: userID
      * value: true
11. `Notifications`
    * a list of notifications to a specific user
    * key: userID
      * key: notificationID
      * value: notification
  

### Database Rules
Below listed some of the rules that limit access and validate data:
1. User profile are only readable/writable by the user who owns it.
2. Posts and Comments can be read by anyone but only written by logged-in users.
3. User posts can be read by anyone but only written by the user that owns it, and with a matching UID
4. User-following list can be read by anyone, but only written by the user that owns it.
5. User-followed list can be read by anyone. can be written by the user who owns it.


## References
### Firebase
- [Firebase official docs](https://firebase.google.com/docs/guides?hl=en&authuser=0)
  - [Read and Write Data on Android](https://firebase.google.com/docs/database/android/read-and-write)
  - [Work with Lists of Data on Android](https://firebase.google.com/docs/database/android/lists-of-data) 
- [Add firebase to your Android project](https://firebase.google.com/docs/android/setup)
- [Youtube: Firebase YouTube channel](https://www.youtube.com/@Firebase/playlists)
  -  [SQL Databases and the Firebase Database](https://www.youtube.com/watch?v=WacqhiI-g_o&list=PLl-K7zZEsYLlP-k-RKFa7RyNPa9_wCH2s)
  -  [Introducing Firebase Realtime Database](https://www.youtube.com/watch?v=U5aeM5dvUpA&list=PLl-K7zZEsYLmgdxMEHar35Wo26fLWm9BI)
  -  [Getting started with Firebase Authentication on Android](https://www.youtube.com/watch?v=wm626abfMM8)
- [Github: Quick start of firebase-android, google official](https://github.com/firebase/quickstart-android)
   - [Firebase realtime database - android ]( https://github.com/firebase/quickstart-android/blob/master/database/README.md)
   - [Firebase Storage - android](https://github.com/firebase/quickstart-android/tree/master/storage)

### Tutorials about Android and Object-oriented Design
- [YouTube: Create a Complete Android App with Firebase Start to End](https://www.youtube.com/watch?v=7kzttk9lj3U)
- [The Complete Android Oreo Developer Course - Build 23 Apps! ](https://www.udemy.com/course/the-complete-android-oreo-developer-course/)
- [Grokking object oriented design interview](https://www.educative.io/courses/grokking-the-low-level-design-interview-using-ood-principles)
- [Android Developers Youtube Channel](https://www.youtube.com/user/androiddevelopers)
- [YouTube: Testing on Android](https://www.youtube.com/watch?v=EkfVL5vCDmo&list=PLQkwcJG4YTCSYJ13G4kVIJ10X5zisB2Lq)
- Android Official Doc
  - [Test apps on Android](https://developer.android.com/training/testing)
  - [Create dynamic lists with RecyclerView](https://developer.android.com/develop/ui/views/layout/recyclerview)
  - [App architecture](https://developer.android.com/topic/architecture/intro)
  - [App resources overview](https://developer.android.com/guide/topics/resources/providing-resources) 

### Gatech Courses taken
- [CS 6300: Software Development Process](https://omscs.gatech.edu/cs-6300-software-development-process)
- [CS 6310: Software Architecture and Design](https://omscs.gatech.edu/cs-6310-software-architecture-design)
