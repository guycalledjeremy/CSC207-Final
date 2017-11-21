package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/** Manages all the imageFiles under a root folder */
@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class ImageFileManager {
  /** String to match all tagable files */
  private static final String FILE_MATCH_STRING =
      ".*[.](jpg|jpeg|png|gif|bmp|JPG|JPEG|PNG|GIF|BMP)";

  /** the root of the directory */
  private File root;

  /** the tagManager */
  private TagManager tagManager = new TagManager();

  /**
   * Construct a new ImageFileManager object.
   *
   * @param path the root directory for this ImageFileManager object
   */
  public ImageFileManager(String path) {
    this(new File(path));
  }

  public ImageFileManager(File file) {
    root = file;
    tagManager = new TagManager(getAllCurrentTags());
  }

  public static void main(String[] args) {
    ImageFileManager imageFileManager1 = new ImageFileManager("/Users/Jeremy/Desktop/test");
    ImageFile imageFile = new ImageFile("");
    for (ImageFile thisImageFile : imageFileManager1.getAllImageFiles()) {
      if (thisImageFile.getName().equals("Sakshi @\"foo\"")) {
        imageFile = thisImageFile;
      }
    }

    Scanner scanner = new Scanner(System.in);
    String input = "";
    while (!input.equals("exit")) {
      System.out.println(
          "Enter a number to execute a command (type \"exit\" to exit):\n"
              + "(1) change to a new file\n"
              + "(2) to add a Tag\n"
              + "(3) to remove a Tag\n"
              + "(4) to get Tags\n"
              + "(5) to get previous Tags\n"
              + "(6) to move file\n");
      input = scanner.nextLine();
      String output = "";
      switch (input) {
        case "1":
          System.out.println("Where to?");
          input = scanner.nextLine();
          imageFile = new ImageFile(input);
          output = "Changed to " + input;
          break;

        case "2":
          System.out.println("What tag should I add?");
          input = scanner.nextLine();
          if (imageFile.addTag(input)) {
            output = "Added " + input;
          } else {
            output = "Adding tag failed";
          }
          break;

        case "3":
          System.out.println("What tag should I remove?");
          input = scanner.nextLine();
          if (imageFile.removeTag(input)) {
            output = "Removed " + input;
          } else {
            output = "Removing tag failed";
          }

          break;

        case "4":
          output = Arrays.toString(imageFile.getTags());
          break;

        case "5":
          output = Arrays.toString(imageFile.getPreviousTags());

          break;

        case "6":
          System.out.println("Where should I move the image?");
          input = scanner.nextLine();
          if (imageFile.moveFile(input)) {
            output = "Moved image" + input;
          } else {
            output = "Moving image failed";
          }
      }
      if (output.equals("") && !input.equals("exit")) {
        output = "This is not a valid input.";
      }
      System.out.println(output);
    }
  }

  /**
   * Returns all the image files anywhere under the root directory.
   *
   * @return a ImageFile[] of all image files anywhere under the root directory.
   */
  @SuppressWarnings("ConstantConditions")
  public ImageFile[] getAllImageFiles() {
    List<File> matchingFiles = new ArrayList<>();
    if (root.isDirectory() || (root.isFile() && root.getName().matches(FILE_MATCH_STRING))) {
      matchingFiles.add(root);

      int i = 0;
      while (i < matchingFiles.size()) {
        // Check if element at i in ret is a Directory, a File, or it exists.
        if (matchingFiles.get(i).isDirectory()) {
          // Element at i is a directory, so determine if it has children and remove the Element at
          // i.
          // Check if the Element at i has children.
          if (matchingFiles.get(i).list() != null) {
            // Element at i has children, so add children to the ArrayList if they match the regEx.
            for (File file : matchingFiles.get(i).listFiles()) {
              if (file.getName().matches(FILE_MATCH_STRING) || file.isDirectory()) {
                matchingFiles.add(file);
              }
            }
          }
          matchingFiles.remove(i);
        } else if (matchingFiles.get(i).isFile()) {
          // Element at i is a File, so increment i by 1.
          i += 1;
        } else {
          // Element doesn't exist in filesystem, so remove from ret.
          matchingFiles.remove(i);
        }
      }
    }
    ImageFile[] ret = new ImageFile[matchingFiles.size()];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = new ImageFile(matchingFiles.get(i));
      ret[i].addObserver(tagManager);
    }
    return ret;
  }

  /**
   * Returns all the image files directly under the root directory.
   *
   * @return a ImageFile[] of all images files directly under the root directory.
   */
  @SuppressWarnings("ConstantConditions")
  public ImageFile[] getLocalImageFiles() {
    List<File> matchingFiles = new ArrayList<>();
    if (root.list() != null) {
      for (File file : root.listFiles()) {
        if (file.isFile() && file.getName().matches(FILE_MATCH_STRING)) {
          matchingFiles.add(file);
        }
      }
    }
    ImageFile[] ret = new ImageFile[matchingFiles.size()];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = new ImageFile(matchingFiles.get(i));
      ret[i].addObserver(tagManager);
    }
    return ret;
  }

  /**
   * returns all the current existing tags, associated or unassociated to images
   *
   * @return a String[] of all the tags.
   */
  public String[] getAllCurrentTags() {
    return tagManager.getTags();
  }

  /**
   * Deletes Tag from all files and tagManager
   *
   * @param tag the tag to delete
   * @return true if it succeeds, false if it doesn't.
   */
  public boolean deleteTag(String tag) {
    boolean ret = true;
    if (!tagManager.removeTag(tag)) {
      ret = false;
    }
    if (ret) {
      for (ImageFile imageFile : getAllImageFiles()) {
        if (!imageFile.removeTag(tag)) {
          ret = false;
        }
      }
    }
    return ret;
  }

  /**
   * Changes the working directory of ImageFileManager if new directory exists. All tags that aren't
   * associated with an Image will when be unavailable when switch happens unless restored.
   *
   * @param path the String path of the root folder to try to switch to
   * @return true if changingDirectory succeeds.
   */
  public boolean changeDirectory(String path) {
    return changeDirectory(new File(path));
  }

  /**
   * Changes the working directory of ImageFileManager if new directory exists. All tags that aren't
   * associated with an Image will when be unavailable when switch happens unless restored.
   *
   * @param root the root folder to try to switch to
   * @return true if changingDirectory succeeds.
   */
  public boolean changeDirectory(File root) {
    boolean ret = false;
    if (root.exists()) {
      ret = true;
      this.root = root;
      tagManager = new TagManager();
    }
    return ret;
  }

  public File getRoot() {
    return root;
  }
}
