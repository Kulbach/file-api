# Summary
The app can be used for uploading files on local disk and storing the file metadata in MongoDB.
It includes four endpoints for uploading, downloading and deleting files along with retrieving files metadata.

## Comments

// A short comment about the assignment in general
The assignment was nice. It was not hard to implement but each request had a trick which made the requirement more complicated than it seemed at first glance.
// Was the Acceptance Criteria easy to understand?
Yes, it was. But there were some inconsistencies for me like the one that it's firstly said /files endpoint should save files in the task acceptance criteria although than it's mentioned the endpoint should save only one file per request below.
Besides, I was not exactly sure where I have to store files: in mongodb or locally. 

## Which part of the assignment took the most time and why?
Probably, /files endpoint took the most time because I needed to learn how to deal with mongodb and files storing, and additionally I spent a lot of time on code refactoring to make it cleaner. 
I also was writing unit/integration tests for a long time because couldn't figure out how to test endpoints which uses security and was getting errors.

## What You learned
The assignment made me read a lot about best practises for exception handling in Spring Boot and I gain some new knowledge in this area.
I can't help but mention that it was interesting to learn Kotlin syntax. I loved how much less code is needed to write something in Kotlin comparing to Java. 
Would love to work with Kotlin in the future.

At first, I thought about writing the task in Kotlin, but then I decided to use Java as I know it much better and can write more standards-compliant code.

## TODOs

Additional validation is needed in /files/metas endpoint because if user provides two tokens but only one exists in db then the app will return meta for existing token and will not indicate user that another token was incorrect.
It will be a good idea to rewrite /files endpoint to support multiple files.