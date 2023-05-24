## Design Considerations
1. System Environment


## Requirements and Object-oriented Design


## User Interface Design


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
6. `Comment-Comments`
     - a list of comments under the comment
     - key: comment ID
     - `/Comment-comments/<Comment-ID>/` is a list of all comments under a certain comment, keyed by the same push ID usded in the `Comments` tree.
7. `User-following`
     - a list of users followed by the user
     - key: userID
        - key: groupID, 
        - `/User-following/<USER-ID>/<GROUP-ID>`, is a list of users followed by a specific user and group under a certain group, keyed by the user ID
     - This makes it easy to query "all users followed by a specific user", " a list of users followed and grouped by a specific user", etc.
8. `User-followed`
     - a list of users following the specific user
     - key: userID


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
- [Add firebase to your Android project](https://firebase.google.com/docs/android/setup)
- [Firebase YouTube channel](https://www.youtube.com/@Firebase/playlists)
- [Quick start of firebase-android, google official](https://github.com/firebase/quickstart-android)
   - [Firebase realtime database - android ]( https://github.com/firebase/quickstart-android/blob/master/database/README.md)
   - [Firebase Storage - android](https://github.com/firebase/quickstart-android/tree/master/storage)
