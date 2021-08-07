package AppID;

import java.io.File;

/**
 *
 * Container for a jump list file
 */
public class JumpListFile {

    private final File file;            // wraps the jump list filename
    private final String appID;         // AppID associated with this file (see AppIDList.java)
    private final String displayName;   // String to display in table

    /**
     * Constructor
     * 
     * @param appID
     * @param file
     * @param appIDList 
     */
    public JumpListFile(String appID, File file, AppIDList appIDList) {
        this.file = file;
        this.appID = appID;
        if (appID != null) {
            // try to get the application name from the static list
            // set to 'unknown' if the AppID can't be matched
            String appName = appIDList.getAppName(appID);
            if (appName == null) {
                this.displayName = appID + " (unkown)";
            } else {
                this.displayName = appName + " (" + appID + ")";
            }
        } else {
            this.displayName = file.getName();
        }
    }

    @Override
    public String toString() {
        return (displayName);
    }

    public String appID() {
        return(appID);
    }
    
    public String getFilename() {
        return(file.getAbsolutePath());
    }

}
