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
   - a list of `User` objects,
   - key: user ID, `<ID>`
   - value: a hashmap of user profile, e.g. `/users/<ID>/email` is the email of the user with id=`<ID>`
      | key       |     value     |
      |-----:     |---------------|
      | email     |               |
      |username   |               |
      |profile pic|               |
2. `Posts`
    - a list of `Post` objects


### Database Rules




## References
