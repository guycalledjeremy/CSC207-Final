package model;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Manages the log file for the target class (ImageFile and TagManager in our model). */
public class Log {

    /** the log file for the target class. */
    private File log;

    private static final String LOG_FILE_SUFFIX = ".log";

    private static final String LOG_FILE_SEPARATOR = " / ";

    private static final String LOG_FILE_PREFIX = ".";

    private static final String TAG_MARKER = "@";

    /**
     * Creates a log file for a file
     *
     * @param file the physical file this log file is created for.
     */
    public Log(File file) {
        log = new File(file.getParent(), LOG_FILE_PREFIX + file.getName() + LOG_FILE_SUFFIX);
        if (!log.exists()) {
            try {
                log.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (System.getProperty("os.name").contains("Windows")) {
            Path logPath =
                    FileSystems.getDefault().getPath(log.getParentFile().getAbsolutePath(), log.getName());
            try {
                Files.setAttribute(logPath, "dos:hidden", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * When the file is moved, move this log file as well.
     *
     * @param newPath the String representation of the given directory
     * @param file the target file of this log file
     * @return Whether this moving the files was successful
     */
    boolean moveFile(String newPath, File file, boolean ret1) {
        File newLog = new File(newPath, LOG_FILE_PREFIX + file.getName() + LOG_FILE_SUFFIX);
        boolean ret = ret1 && log.renameTo(newLog);
        if (ret) {
            if (newLog.exists()) {
                log = newLog;
            }
        }
        return ret;
    }

    /**
     * Record the renaming of the target file in the log file.
     *
     * @param lastName the last name of the target file
     * @param newName the new name of the target file
     * @param newLog the new log file
     * @return the log file after recording the renaming
     */
    boolean rename(String lastName, String newName, File newLog) {
        boolean ret = true;
        // get the time
        Date time = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (!newLog.exists()) {
            ret = log.renameTo(newLog);
        }
        if (ret) {
            if (newLog.exists()) {
                // Write everything from the previous log file into the new log file.
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(log.getPath()));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(newLog, true));
                    String line = reader.readLine();
                    while (line != null) {
                        writer.append(line);
                        writer.append("\n");
                        line = reader.readLine();
                    }
                    reader.close();
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ret = false;
                }
                // Assign the log file pointer to the new log file.
                File oldLog = log;
                log = newLog;
                oldLog.delete();
            }
            // Add the new line into the now log file.
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(log, true));
                writer
                        .append(lastName)
                        .append(LOG_FILE_SEPARATOR)
                        .append(newName)
                        .append(LOG_FILE_SEPARATOR)
                        .append(dateFormat.format(time))
                        .append("\n");
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Return a list of String representations of the recording of the tag changes in the log file.
     *
     * @return the list of String representations
     */
    String[] getLog() {
        List<String> logs = new ArrayList<>();
        BufferedReader reader;
        if (!log.exists()) {
            try {
                log.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            reader = new BufferedReader(new FileReader(log.getPath()));
            String line = reader.readLine();
            while (line != null) {
                logs.add(line.replaceFirst(LOG_FILE_SEPARATOR, " -> ").replace(LOG_FILE_SEPARATOR, " | "));
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logs.toArray(new String[logs.size()]);
    }

    /**
     * Return if this log file exits.
     *
     * @return return true if this log file exist, false if not
     */
    boolean exists() {
        return log.exists();
    }

    /**
     * Return the physical log file of this Log Object.
     *
     * @return the physical log file
     */
    File getFile() {
        return log;
    }

}