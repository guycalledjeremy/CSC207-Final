package reStructure;

import java.io.File;
import java.util.*;

public class ImageFileManager {
  /** String to match all tagable files */
  private static final String FILE_MATCH_STRING = ".*[.](jpg|png|gif|bmp)";

  /** the root of the directory */
  private File root;

  /** the tagManager */
  private TagManager tagManager;

  /**
   * Construct a new ImageFileManager object.
   *
   * @param path the root directory for this ImageFileManager object
   */
  public ImageFileManager(String path) {
    root = new File(path);
    tagManager = new TagManager(getAllTags());
  }

  /**
   * Returns all the image files anywhere under the root directory.
   *
   * @return a ImageFile[] of all image files anywhere under the root directory.
   */
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
    }
    return ret;
  }

  /**
   * Returns all the image files directly under the root directory.
   *
   * @return a ImageFile[] of all images files directly under the root directory.
   */
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
    }
    return ret;
  }

  public String[] getAllCurrentTags() {
    updateTagManager();
    return tagManager.getTags();
  }

  /**
   * Deletes Tag from all files and tagManager
   *
   * @param tag the tag to delet
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

  /** updates the tagManager so it keeps up with the new tags */
  private boolean updateTagManager() {
    Set<String> tags = new HashSet<>();
    for (ImageFile imageFile : getAllImageFiles()) {
      tags.addAll(Arrays.asList(imageFile.getTags()));
    }
    return tagManager.update(tags.toArray(new String[tags.size()]));
  }
}
