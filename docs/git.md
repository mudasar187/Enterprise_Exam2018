[Back to README.md](../README.md)

# Git Structure

## Branches

- Master
- Dev
- release
  - release-0.1
  - release-0.2
  - etc...
- feature
  - feature-authentication
  - feature-front-end-login
  - etc..

##### Feature branches
For every new features we creating own branch. E.g if one of us is implementing spring security or creating login functionality in frontend
Reason for this make it easier for us to work with more things on same time. E.g you have problem on security and wait for help you can start on login functionality, or update code.
Its make easier for us to take over others code and start working on the code.

##### Release branches
When we are finished with one feature, we can merge it into a release branch. In this release branch will it wait until we push it to master branch.

##### Dev
When all features are merged together into a release and all conflicts are solved then we can push it to dev. Travis will build dev, and we can verify that everythings works with the new code.

##### Master
Master always need to be stabel. To make dev merged into master it has to go trough all pull requests. It means one of the team need to do a code review and confirm the merge before its allowing merged into master