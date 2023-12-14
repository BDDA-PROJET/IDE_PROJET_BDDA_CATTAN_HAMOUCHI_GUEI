package up.mi.bdda.tests.disk;

import org.junit.jupiter.api.Test;

import up.mi.bdda.app.DBParams;
import up.mi.bdda.app.disk.DiskManager;
import up.mi.bdda.app.page.PageId;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * The DiskManagerTest class is a unit test class for testing the DiskManager
 * class. It doesn't have any fields, but it has several methods, each of which
 * is a test case for a specific functionality of the DiskManager class.
 */
public class DiskManagerTest {
  /**
   * This is the constructor of the test class. It sets up some parameters for the
   * database.
   */
  public DiskManagerTest() {
    DBParams.DBPath = "DB";
    DBParams.SGBDPageSize = 4096;
    DBParams.DMFFileCount = 4;
  }

  /**
   * This test checks the {@code allocPage} method of the {@code DiskManager} when
   * there are no deallocated page IDs. It verifies that the allocated page IDs
   * are as expected.
   * 
   * @throws IOException
   */
  @Test
  public void test_allocPage_deallocatedPageIdsEmpty() throws IOException {
    DiskManager diskManager = DiskManager.getSingleton();
    PageId pageId1 = new PageId(0, 0);
    PageId pageId2 = new PageId(1, 0);
    PageId allocatedPageId = diskManager.allocPage();
    assertEquals(pageId1, allocatedPageId);
    allocatedPageId = diskManager.allocPage();
    assertEquals(pageId2, allocatedPageId);
  }

  /**
   * This test checks the {@code allocPage} method of the {@code DiskManager} when
   * there are deallocated page IDs. It verifies that the allocated page IDs are
   * as expected.
   * 
   * @throws IOException
   */
  @Test
  public void test_allocPage_deallocatedPageIdsNotEmpty() throws IOException {
    DiskManager diskManager = DiskManager.getSingleton();
    PageId pageId1 = new PageId(0, 0);
    PageId pageId2 = new PageId(1, 0);
    PageId allocatedPageId = diskManager.allocPage();
    assertEquals(pageId1, allocatedPageId);
    allocatedPageId = diskManager.allocPage();
    assertEquals(pageId2, allocatedPageId);
    diskManager.deallocPage(pageId1);
    allocatedPageId = diskManager.allocPage();
    assertEquals(pageId1, allocatedPageId);
  }

  /**
   * This test checks the {@code deallocPage} method of the {@code DiskManager}
   * with a page ID that was previously allocated. It verifies that the count of
   * allocated pages decreases by one.
   * 
   * @throws IOException
   */
  @Test
  public void test_deallocPage_previouslyAllocated() throws IOException {
    DiskManager diskManager = DiskManager.getSingleton();
    PageId pageId = diskManager.allocPage();
    int initialAllocatedPageCount = diskManager.getAllocatedPageCount();
    diskManager.deallocPage(pageId);
    int finalAllocatedPageCount = diskManager.getAllocatedPageCount();
    assertEquals(initialAllocatedPageCount - 1, finalAllocatedPageCount);
  }

  /**
   * This test checks the {@code deallocPage} method of the {@code DiskManager}
   * with a page ID that was not previously allocated. It verifies that the count
   * of allocated pages remains the same.
   */
  @Test
  public void test_deallocPage_notPreviouslyAllocated() {
    DiskManager diskManager = DiskManager.getSingleton();
    PageId pageId = new PageId(0, 0);
    int initialAllocatedPageCount = diskManager.getAllocatedPageCount();
    diskManager.deallocPage(pageId);
    int finalAllocatedPageCount = diskManager.getAllocatedPageCount();
    assertEquals(initialAllocatedPageCount, finalAllocatedPageCount);
  }

  /**
   * This test checks the {@code deallocPage} method of the {@code DiskManager}
   * with multiple page IDs. It verifies that the count of allocated pages
   * decreases correctly.
   * 
   * @throws IOException
   */
  @Test
  public void test_deallocAllPages() throws IOException {
    DiskManager diskManager = DiskManager.getSingleton();
    PageId pageId1 = diskManager.allocPage();
    PageId pageId2 = diskManager.allocPage();
    int initialAllocatedPageCount = diskManager.getAllocatedPageCount();
    diskManager.deallocPage(pageId1);
    diskManager.deallocPage(pageId2);
    int finalAllocatedPageCount = diskManager.getAllocatedPageCount();
    assertEquals(initialAllocatedPageCount - 2, finalAllocatedPageCount);
  }

  /**
   * This test checks the {@code writePage} and {@code readPage} methods of the
   * {@code DiskManager}. It verifies that data written to a page can be correctly
   * read back.
   * 
   * @throws IOException if an I/O error occurs
   */
  @Test
  public void test_writePageAndReadPage() throws IOException {
    DiskManager diskManager = DiskManager.getSingleton();
    PageId pageId = diskManager.allocPage();

    // Create a ByteBuffer with test data
    ByteBuffer testData = ByteBuffer.allocate(100);
    testData.put("Test data".getBytes());
    testData.flip();

    // Write the test data to the page
    diskManager.writePage(pageId, testData);

    // Create a new ByteBuffer to read the data from the page
    ByteBuffer readData = ByteBuffer.allocate(100);

    // Read the data from the page
    diskManager.readPage(pageId, readData);

    // Reset the position of the readData buffer
    readData.flip();

    // Convert the readData buffer to a string
    String readString = new String(readData.array(), StandardCharsets.UTF_8).trim();

    // Assert that the read data matches the test data
    assertEquals("Test data", readString);
  }
}
