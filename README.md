# LOG Management

## TODO
* Testcases
* Bug fixes
* Email Verification on Local account Creation

## Authentication
* Currently either Google(OIDC) & Local(user/password) based authentication is supported
* After authentication flow (OAuth or User/password), system generates JWT Bearer token which is used for API authentication

## RBAC
* System has concept of `account`,`user`,`tokens`,`role`, `permissions`
* Each Rest API, exposes certain set permissons (like list, create, upload_file etc), Current System authorization works based on these permissions.  
* Roles is group of permissions, System has following default roles, new roles can be easily created -
    * SUPER_ADMIN - only users in System account (inital acount) can be Super Admin, they have access to full user
    * ACCOUNT_ADMIN - All the users which have admin previledges in given account
    * ACCOUNT_USER - All the user which have readonly access in given account 
    * DATA_ACCESS - They are treated as nomal account with restricted access ie. can upload files only
* An user can be part of multiple accounts, similarly an account can have multiple users
* Both Use Roles and Permissions get stored in DB, and its easier to create new roles based on requirements (they are just permission groupings)

## User Registration
* New Use can be onboarded into System using Self Registration or thru google login (domains can be restricted using Oauth config in application conf)
* Once user is created they wont have access to any account (exception is below) and ACCOUNT_ADMIN/SUPER_ADMIN need to explicitly add them to existing account with given role
* Optionally, user can be onboarded in advance using accountUser onboarding API, this only creates new user and role and marks user DISABLED, once user get registered they will have access to new account

## Account Registration
* Can only be done by SUPER_ADMIN using admin APIs
* By default user creating account also becomes ACCOUNT_ADMIN

## Access Token Creation
* On creating new accessToken, system returns access keys & secret keys (one time)
* Users/Machines can use these keys to get JWT token and upload new files

## Audit Events
* Audit Events are stored using AuditEventLog(audit_event_log), where major events like Login, userCreation,file upload are captured
* Different modules(accounts/users) expose RestAPI to fetch Adit events

## Initial Configs
* `src/main/resources/application.yml` - `app.admin` contains super admin user/creds, default login 
    * username - 
    * password - `password`
* `src/main/resources/application.yml` - GMail Oauth Configs

## Modules
* __log-management-core__ - Springboot Backend for Log Management
* __log-management-ui__ - React Based UI frontend
* __agent-fileuploader__ - Sample File uploader in Java

## Running Apps
* __log-management-core__
    * `mvn springboot:run` - This will start backend server on port 8080
    * `src/main/resources/application.yml` - Configs for server

* __log-management-ui__
    * `npm run start` - This will start UI server on port 3000

* __agent-fileuploader__
    * `java -jar <jarName> <clientId> <clientSecret> <fileToUpload> [<serverHost>]`

## API Endpoints
Below are the End Points exposed from API server.
These endpoints are secured using Spring (com.demo.config.SecurityConfig)
* `admin/accounts` - Create/Update/Remove Accounts
* `acounts/<accounts>/events` - Accounts Audit Events
* `acounts/<accounts>/users` - Register/Update/Remove new/existing Users in a account
* `acounts/<accounts>/agents` - Create/Update/Remove new AccessToken in a account
* `data/<accounts>/fileUpload` - File upload by AccessToken
* `users/me` - Details about logged in user
* `users/me/events` - User Events
* `/auth/signUp` - Register User using user/password
* `/auth/login` - User Login API
* `/auth/token` - Access token endpoints

