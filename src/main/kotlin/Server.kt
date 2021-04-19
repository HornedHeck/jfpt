import org.mockftpserver.fake.FakeFtpServer
import org.mockftpserver.fake.UserAccount
import org.mockftpserver.fake.filesystem.DirectoryEntry
import org.mockftpserver.fake.filesystem.FileEntry
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem

fun setupServer() {
    val mockFS = UnixFakeFileSystem()
    mockFS.add(DirectoryEntry("/"))
    mockFS.add(DirectoryEntry("/env"))
    mockFS.add(DirectoryEntry("/dev"))
    mockFS.add(DirectoryEntry("/etc"))
    mockFS.add(FileEntry("/test1.txt", "156a465s4a65 "))
    mockFS.add(FileEntry("/test2.txt", "Test content"))

    val mockUser = UserAccount("hornedheck", "110101", "/")

    val mockServer = FakeFtpServer().apply {
        fileSystem = mockFS
        addUserAccount(mockUser)
        serverControlPort = SERVER_CONTROL_PORT
    }
    mockServer.start()
}