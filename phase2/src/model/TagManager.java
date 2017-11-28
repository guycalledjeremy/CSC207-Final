package model;

import java.util.*;

/** manages a collection of tags. */
public class TagManager extends Observable implements Observer, Taggable {
  /** A set of tags. */
  private Set<String> tags;

  private Log log;

  private String lastErasedTag;

  private static final String LOG_FILE_NAME = "TagManager";

  /** Construct a new TagManager with no existing tag. */
  public TagManager() {
    tags = new HashSet<>();
    log = new Log(".", LOG_FILE_NAME);
    tags.addAll(Arrays.asList(log.getColumn(1)));
  }

  /**
   * Construct a new TagManager with a list of existing tags.
   *
   * @param tags a list of string representations for existing tags.
   */
  public TagManager(String[] tags) {
    this.tags = new HashSet<>(Arrays.asList(tags));
  }

  /**
   * Add a new tag to the TagManager.
   *
   * @param newTag the string representation for this new tag
   * @return a boolean indicating whether the adding of this tag succeeded
   */
  public boolean addTag(String newTag) {
      Set<String> oldSet = new HashSet<>(tags);
    boolean success =  tags.add(newTag);
    if(success) {
      setChanged();
      notifyObservers();
      log.rename(oldSet.toString(), tags.toString());
    }
    return success;
  }

  /**
   * Remove a tag from the TagManager.
   *
   * @param thisTag the string representation for this new tag
   * @return a boolean indicating whether the removal of this tag succeeded
   */
  public boolean removeTag(String thisTag) {
    Set<String> oldSet = new HashSet<>(tags);
    boolean success =  tags.remove(thisTag);
    if(success) {
      lastErasedTag = thisTag;
      setChanged();
      notifyObservers();
      log.rename(oldSet.toString(), tags.toString());
    }
    return success;
  }

  /**
   * Return existing tags.
   *
   * @return a String[] of the existing tags
   */
  public String[] getTags() {
    return tags.toArray(new String[tags.size()]);
  }

  /**
   * Update the tagManager with new tags.
   *
   * @param o the observable object that updates the observer TagManager when new Tag is added
   * @param arg pass in argument
   */
  @Override
  public void update(Observable o, Object arg) {
    for (String tag : ((ImageFile) o).getTags()) {
      addTag(tag);
    }
  }

  public String getLastErasedTag() {
    return lastErasedTag;
  }

//  public Set<String> getPreviousGlobalTags() {
//    return log.getPreviousGlobalTags();
//  }
}
