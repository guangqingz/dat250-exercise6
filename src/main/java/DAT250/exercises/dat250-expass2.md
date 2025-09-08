One technical problem I encountered during installation was an error when running a request in Bruno, caused by port **8080** being occupied by another process. I solved this by switching to a different port.

Another issue was that my controllers returned a **200 status** even when an object wasn't found. At the same time, my delete functions didn't properly signal when nothing was deleted, even though the tests expected a **404 status** after deletion. I fixed this by throwing a `ResponseStatusException(HttpStatus.NOT_FOUND, "...")` when an entity is missing, and by changing the delete functions in `PollManager.java` to return a boolean instead of `void`. This allows the controller to return **404** if nothing was removed.

After fixing the installation and controller issues, I checked the application and tests and didn't find any more errors. All main requirements are met, so I don't think there are any remaining issues with this assignment.
