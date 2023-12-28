# Test task for JetBrains internship

## Some words about task implementation:

1. While executing the program there can be thrown exceptions in case of problems with creating entries.
I implemented my own type of exception: `FSException`

2. I change a little bit signature of the method `create`:

    ```fun create(entryToCreate: FSEntry, destination: String, rewriteExisting: Boolean = false)```

    The purpose of the `rewriteExisting` parameter is to indicate what logic the code will follow if the user wants 
to create an existing file or directory:

    Values and meanings: 

* `false` (default value) - in this case when user is trying to create an existing file or directory an `FSException` will be thrown.
* `true` - in this case for file previous content will be rewritten to the new one, for directory new files and folders
will be added in it, old content will stay untouched.

3. To run tests please use:  `./gradlew test`