package model.image_tag_explorer;

import model.file_explorer.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Image implements java.io.Serializable {

  private File imageFile;
  private List<Tag> tags;
  private Stack<String> previousNames;

  /**
   * Constructs an abstract image with a referring file with no tags.
   *
   * @param imageFile the abstract file representation of the image file
   */
  public Image(File imageFile) {
    this.imageFile = imageFile;
    this.tags = new ArrayList<>();
    previousNames = new Stack<>();
  }

//  public Image(File imageFile, Tag[] tags) throws Exception {
//    this.imageFile = imageFile;
//    this.tags = new ArrayList<>(tags.length);
//    previousNames = new Stack<>();
//
//    if (imageFile.getName().contains("@")) {
//      String[] possibleTags =
//          imageFile.getName().substring(imageFile.getName().indexOf("@")).split("@| ");
//      for(String possibleTag : possibleTags) {
//        if(possibleTag.startsWith("@")) {
//          boolean noTag = true;
//          for(Tag tag : tags) {
//            if(possibleTag.substring(1) == tag.getName()) {
//              noTag = false;
//            }
//          }
//          if(noTag) {
//            throw new Exception("No Tag");
//          }
//        }
//      }
//    }
//    // Adding Tags into the Image name.
//    for (Tag tag : tags) {
//      addTag(tag);
//    }
//    // TODO: if there are "@" tags on the image name that aren't in the given Tag[] array throw an
//    // custom error.
//    // why do we need to check this if we are constructing a new Image object?
//  }

  /**
   * adds a given tag to the Image, modifies the image filename to match the new change
   *
   * @param newTag the tag to add to the image
   */
  public void addTag(Tag newTag) {
    boolean here = false;
    // Checking if this Tag is new to this Image
    for (Tag tag : tags) {
      if (tag.getName().equals(newTag.getName())) {
        here = true;
      }
    }

    if (!here) {
      rename(String.format("%s @%s", imageFile.getName(), newTag.getName()));
      tags.add(newTag);
      newTag.addImage(this);
    }
  }

  /**
   * tries to remove a given tag from the Image, modifies the image filename to match the new change
   *
   * @param tag the tag to try and remove from the image
   * @return true if tag removal was successful, false if not.
   */
  public void removeTag(Tag tag) {
    boolean here = false;
    // Checking if this Tag is in this Image
    for (String tagName : previousNames) {
      if (tagName.equals(tag.getName())) {
        here = true;
      }
    }

    // If this Tag is in this Image, then remove it.
    if (here) {
      tags.remove(tag);
      previousNames.remove("@" + tag.getName());
      tag.removeImage(this);
      // TODO: this return value of tag.removeImage(this) is not used in this call
    }
  }

  public void rename(String newName) {
    //logger added for everytime the image is renamed
      try {
          Log my_log = new Log("log.txt");
          my_log.logger.info(imageFile.getName() + "is being renamed to" + newName);

      } catch (Exception e) {}

      previousNames.push(imageFile.getName());
      imageFile.renameTo(new File(imageFile.getParentFile(), newName));
    // TODO: write a tester that makes sure this method does what's expected.
  }

  /**
   * returns the name of an Image.
   *
   * @return the name of the Image.
   */
  public String getName() {
    return imageFile.getName();
  }
}