import org.example.FSCreator
import org.example.FSException
import org.example.FSFile
import org.example.FSFolder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class FSCreatorTests {
    private var tempDir: Path? = null

    @BeforeEach
    fun setUp() {
        tempDir = Files.createTempDirectory("testFSCreator")
    }

    @AfterEach
    fun setDown() {
        tempDir?.toFile()?.deleteRecursively()
    }

    @Test
    fun testCreateFile() {
        val file = FSFile("testFile.txt", "This is the content of the test file")
        FSCreator.create(file, tempDir.toString())

        val filePath = tempDir!!.resolve(file.name)
        Assertions.assertTrue(Files.exists(filePath))
        Assertions.assertEquals(file.content, String(Files.readAllBytes(filePath)))
    }

    @Test
    fun testCreateEmptyFolder() {
        val folder = FSFolder("testFolder", emptyList())
        FSCreator.create(folder, tempDir.toString())

        val folderPath = tempDir!!.resolve(folder.name)
        Assertions.assertTrue(Files.exists(folderPath))
        Assertions.assertTrue(Files.isDirectory(folderPath))
    }

    @Test
    fun testCreateFolderWithFile() {
        val file = FSFile("nestedFile.txt", "This is the content of the nested file")
        val folder = FSFolder("testFolder", listOf(file))
        FSCreator.create(folder, tempDir.toString())

        val folderPath = tempDir!!.resolve(folder.name)
        Assertions.assertTrue(Files.exists(folderPath))
        Assertions.assertTrue(Files.exists(folderPath.resolve(file.name)))
        Assertions.assertEquals(file.content, String(Files.readAllBytes(folderPath.resolve(file.name))))
    }

    @Test
    fun testCreateSeveralFoldersAndFiles() {
        val file1 = FSFile("file1.txt", "This is the content of file1.")
        val file2 = FSFile("file2.txt", "This is the content of file2.")
        val folder1 = FSFolder("folder1", listOf(file1, file2))
        val file3 = FSFile("file3.txt", "This is the content of file3.")
        val rootFolder = FSFolder("root", listOf(folder1, file3))
        FSCreator.create(rootFolder, tempDir.toString())

        val rootFolderPath = tempDir!!.resolve(rootFolder.name)
        Assertions.assertTrue(Files.exists(rootFolderPath))
        Assertions.assertTrue(Files.exists(rootFolderPath.resolve(file3.name)))
        Assertions.assertEquals(file3.content, String(Files.readAllBytes(rootFolderPath.resolve(file3.name))))

        val folder1Path = rootFolderPath.resolve(folder1.name)
        Assertions.assertTrue(Files.exists(folder1Path))
        Assertions.assertTrue(Files.exists(folder1Path.resolve(file1.name)))
        Assertions.assertTrue(Files.exists(folder1Path.resolve(file2.name)))
        Assertions.assertEquals(file1.content, String(Files.readAllBytes(folder1Path.resolve(file1.name))))
        Assertions.assertEquals(file2.content, String(Files.readAllBytes(folder1Path.resolve(file2.name))))
    }

    @Test
    fun testThrowsExceptionDestinationDoesNotExists() {
        val file = FSFile("testFile.txt", "This is the content of the test file")
        val exception = Assertions.assertThrows(FSException::class.java) {
            FSCreator.create(file, tempDir!!.resolve("doesNotExist").toString())
        }
        val expectedMessage = "Destination directory does not exist."
        Assertions.assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun testThrowsExceptionDestinationIsFile() {
        val destinationFilePath = tempDir!!.resolve("destinationFile")
        Files.createFile(destinationFilePath)
        val file = FSFile("testFile.txt", "This is the content of the test file")
        val exception = Assertions.assertThrows(FSException::class.java) {
            FSCreator.create(file, destinationFilePath.toString())
        }
        val expectedMessage = "Destination is not a directory."
        Assertions.assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun testThrowsExceptionFileAlreadyExists() {
        val file = FSFile("testFile.txt", "This is the content of the test file")
        FSCreator.create(file, tempDir.toString())
        val exception = Assertions.assertThrows(FSException::class.java) {
            FSCreator.create(file, tempDir.toString())
        }
        val expectedMessage = "File testFile.txt already exists."
        Assertions.assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun testThrowsExceptionFolderAlreadyExists() {
        val folder = FSFolder("testFolder", emptyList())
        FSCreator.create(folder, tempDir.toString())
        val exception = Assertions.assertThrows(FSException::class.java) {
            FSCreator.create(folder, tempDir.toString())
        }
        val expectedMessage = "Folder testFolder already exists."
        Assertions.assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun testRewriteExistingModeForFile() {
        val file1 = FSFile("testFile.txt", "This content should be rewritten")
        FSCreator.create(file1, tempDir.toString())
        val file2 = FSFile("testFile.txt", "New content of the file")
        FSCreator.create(file2, tempDir.toString(), true)

        val filePath = tempDir!!.resolve(file1.name)
        Assertions.assertTrue(Files.exists(filePath))
        Assertions.assertEquals(file2.content, String(Files.readAllBytes(filePath)))
    }

    @Test
    fun testRewriteExistingModeForFolder() {
        val folder = FSFolder("testFolder", emptyList())
        FSCreator.create(folder, tempDir.toString())
        val file = FSFile("testFile.txt", "This is the content of the test file")
        val newFolder = FSFolder("testFolder", listOf(file))
        FSCreator.create(newFolder, tempDir.toString(), true)

        val folder1Path = tempDir!!.resolve(folder.name)
        Assertions.assertTrue(Files.exists(folder1Path))
        Assertions.assertTrue(Files.exists(folder1Path.resolve(file.name)))
        Assertions.assertEquals(file.content, String(Files.readAllBytes(folder1Path.resolve(file.name))))
    }

    @Test
    fun testRewriteExistingModelSameFileNameAsExistingFolder() {
        val file = FSFile("test", "This is the content of the test file")
        FSCreator.create(file, tempDir.toString())
        val folder = FSFolder("test", emptyList())
        val exception = Assertions.assertThrows(FSException::class.java) {
            FSCreator.create(folder, tempDir.toString(), true)
        }
        val expectedMessage = "File test already exists, you can not create folder with this name."
        Assertions.assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun testRewriteExistingModelSameFolderNameAsExistingFile() {
        val folder = FSFolder("test", emptyList())
        FSCreator.create(folder, tempDir.toString())
        val file = FSFile("test", "This is the content of the test file")
        val exception = Assertions.assertThrows(FSException::class.java) {
            FSCreator.create(file, tempDir.toString(), true)
        }
        val expectedMessage = "Folder test already exists, you can not create file with this name."
        Assertions.assertEquals(expectedMessage, exception.message)
    }



}