<h1>TO DO</h1>

Auth:
-> change password functionality (already implemented in the backend)
-> register functionality and button
-> get user roles and permissions from backend
-> implement auth guards \*(also, redirect user to forbidden if they are logged in and try to access login/register)

User / Profile / Account:
-> generic LIST operation would be nice (only one component that takes care of both users and profiles):

Account:
-> Implement components for the Account entity (list, create, update, delete) - do it like the Profile entity
-> Have some extra field to edit??? Currently, we edit the currency (this might not be the best idea)!

4 eyes check:
--- \*-> show changes in the entity, \*highlight the changed fields
--- -> Approve/Reject buttons (based on the user role and permissions)

Sidenav:
-> Show components based on user role and permissions

Aesthetic:
-> Review the User Detailed View after getting the endpoints from the backend
