[Back to README.md](../README.md)

# Git Structure

## Branches

- Master
- Dev
- feature
  - feature-authentication
  - feature-front-end-login
  - etc..

##### Feature branches
For every new features we creating own branch. E.g if one of us is implementing spring security or creating login functionality in frontend
Reason for this make it easier for us to work with more things on same time. E.g you have problem on security and wait for help you can start on login functionality, or update code.
Its make easier for us to take over others code and start working on the code. When its working on feature branch, then we will merge it into dev.


##### Dev
When each features works and conflicts are solved we can merge together into dev. Travis will build dev, and we can verify that everythings works with the new code.

##### Master
Master always need to be stabel. To make dev merged into master it has to go trough all pull requests. It means one of the team need to do a code review and confirm the merge before its allowing merged into master