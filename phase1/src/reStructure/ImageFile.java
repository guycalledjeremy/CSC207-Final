package reStructure;

import java.io.*;
import java.util.*;

/** Represents a physical image file in a filesystem. */
public class ImageFile {

  private static final String LOG_FILE_SUFFIX = ".log";

  private static final String TAG_MARKER = "@";

  /** the image file in the system */
  private File file;

  /** the previous names for the file */
  private File log;

  /**
   * Construct a new ImageFile object with a given path.
   *
   * @param path the directory this ImageFile object is under
   */
  public ImageFile(String path) {
      this(new File(path));
  }

  /**
   * Construct a new ImageFile object representation of a physical file.
   *
   * @param file the physical file for this ImageFile
   */
  public ImageFile(File file) {
    this.file = file;
    log = new File(file.getParent(), getName() + LOG_FILE_SUFFIX);
    if (!log.exists() && file.exists()) {
      try {
          log.createNewFile();
      } catch (IOException e) {
          e.printStackTrace();
      }
}
  }

  // TODO: change the directory of this file

  /**
   * Move the ImageFile to a given directory.
   *
   * @param newPath the string representation of the given directory
   * @return Whether this moving of the ImageFile was successful
   * @throws IOException Throws an exception if we cannot write the file
   */
  public boolean moveFile(String newPath) throws IOException {
      String name = getName();
      File newFile = new File(newPath, getName() + getSuffix());
      File newLog = new File(newPath, getName() + LOG_FILE_SUFFIX);
      boolean ret = file.renameTo(newFile) && log.renameTo(newLog);
      if(ret) {
          if(newFile.exists()) {
              file = newFile;
          }
          if(newLog.exists()) {
              log = newLog;
          }
      }
      return ret;

  }

  // TODO: get tags
  public String[] getTags() {
    return extractTags(getName());
  }

  /** Extracts the Tags in a given string */
  private String[] extractTags(String stringWithTags) {
    List<String> tags = new ArrayList<>();
    String[] slicedString = stringWithTags.split(" ");
    for (String word : slicedString) {
        // Check each word to see if its format indicates it's a tag.
        if (word.length() >= TAG_MARKER.length()
          && word.substring(0, TAG_MARKER.length()).equals(TAG_MARKER)) {
          tags.add(word.substring(1));
      }
    }
    return tags.toArray(new String[tags.size()]);
  }

  // TODO: get previous tags
  public String[] getPreviousTags() throws IOException {
    Set<String> tags = new HashSet<>();
    String[] currentTags = getTags();
    BufferedReader reader = new BufferedReader(new FileReader(log.getPath()));
    String line = reader.readLine();
    while (line != null) {
      String[] potentialTags = extractTags(line);
      for(String potentialTag : potentialTags) {
          if(currentTags.length > 0) {
              for (String currentTag : currentTags) {
                  if (!currentTag.equals(potentialTag)) {
                      tags.add(potentialTag);
                  }
              }
          } else {
              tags.add(potentialTag);
          }
      }
      line = reader.readLine();
    }
    reader.close();

    return tags.toArray(new String[tags.size()]);
  }

  // TODO: add tag
  public boolean addTag(String newTag) throws IOException {
      return rename(String.format("%s %s%s", getName(), TAG_MARKER, newTag));
  }

  // TODO: remove tag
  public boolean removeTag(String thisTag) throws IOException {
      boolean ret = false;
      if(file.getName().contains(thisTag)) {
          ret = rename(getName().replace(String.format(" %s%s", TAG_MARKER, thisTag), ""));
      }
      return ret;
  }

  //TODO: Rename()
  public boolean rename(String newName) throws IOException {
      String lastName = getName();
      File newFile = new File(file.getParent(), newName + getSuffix());
      File newLog = new File(log.getParent(), newName + LOG_FILE_SUFFIX);
      boolean ret = file.renameTo(newFile) && log.renameTo(newLog);
      if(ret) {
          if(newFile.exists()) {
              file = newFile;
          }
          if(newLog.exists()) {
              log = newLog;
          }
          BufferedWriter writer = new BufferedWriter(new FileWriter(log, true));
          writer.append(lastName).append(String.valueOf('\n'));
          writer.close();
      }
      return ret;
  }

  /**
   * Returns the name of an image with no suffix.
   * @return the name of an image.
   */
  public String getName() {
      String ret = "";
      if(file.getName().lastIndexOf(".") != -1) {
          ret = file.getName().substring(0, file.getName().lastIndexOf("."));
      }
      return ret;
  }

  /**
   * Returns the suffix of an image.
   * @return the suffix of an image
   */
  public String getSuffix() {
      String ret = "";
      if(file.getName().lastIndexOf(".") != -1) {
          ret = file.getName().substring(file.getName().lastIndexOf("."));
      }
      return ret;
  }

    public static void main(String[] args) {
        ImageFile imageFile = new ImageFile("/home/ecford/Desktop/foo/foo.jpg");

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
                    try {
                        if (imageFile.addTag(input)) {
                            output = "Added " + input;
                        } else {
                            output = "Adding tag failed";
                        }
                    } catch (IOException ex) {
                        output = "Adding tag threw error";
                    }
                    break;

                case "3":
                    System.out.println("What tag should I remove?");
                    input = scanner.nextLine();
                    try {
                        if (imageFile.removeTag(input)) {
                            output = "Removed " + input;
                        } else {
                            output = "Removing tag failed";
                        }
                    } catch (IOException ex) {
                        output = "Removing tag threw error";
                    }
                    break;

                case "4":
                    output = Arrays.toString(imageFile.getTags());
                    break;

                case "5":
                    try {
                        output = Arrays.toString(imageFile.getPreviousTags());
                    } catch (IOException e) {
                        output = "Getting previous tags threw error";
                    }
                    break;

                case "6":
                    System.out.println("Where should I move the image?");
                    input = scanner.nextLine();
                    try {
                        if(imageFile.moveFile(input)) {
                            output = "Moved image" + input;
                        } else {
                            output = "Moving image failed";
                        }
                    } catch (IOException e) {
                        output = "Moving Image threw error";
                    }
            }
            if (output.equals("") && !input.equals("exit")) {
                output = "This is not a valid input.";
            }
            System.out.println(output);
        }
    }
}
