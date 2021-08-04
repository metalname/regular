package AppID;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * Jump list files are named in the format: AppID.automaticDestinations-ms or AppID.customDestinations-ms
 * AppID is a 16-digit hexadecimal number calculated by Windows that identifies the application
 * that the jump list belongs to
 * See StaticAppID for a list of known application id numbers
 */
public class AppIDList {

    private final HashMap<String, String> appIDMap = StaticAppID.getAppIDMap();

    public AppIDList() {
    }
    
    /**
     * Queries the user appdata folder for a list of jump files
     * Returns an array of jump list files
     * 
     * @param userPath
     * @return 
     */
    public ArrayList<JumpListFile> buildDisplayList(String userPath) {
        // jump lists are stored under $USER\AppData\Roaming\Microsoft\Windows\AutomaticDestinations and
        // $USER\AppData\Roaming\Microsoft\Windows\CustomDestinations
        var jlf = new ArrayList<JumpListFile>();        
        appendFiles(userPath + "/AppData/Roaming/Microsoft/Windows/Recent/AutomaticDestinations/", jlf);
        //appendFiles(userPath + "/AppData/Roaming/Microsoft/Windows/Recent/CustomDestinations/", jlf);
        return(jlf);
    }
    
    private void appendFiles(String path, ArrayList<JumpListFile> jlf) {
        File flist = new File(path);
        if (flist.listFiles() != null) {
            for (int i = 0; i < flist.listFiles().length; i++) {
                jlf.add(buildDisplayFile(flist.listFiles()[i]));
            }
        }        
    }
    
    private JumpListFile buildDisplayFile(File file) {
        return(new JumpListFile(getPrefix(file.getName()), file, this));
    }

    private String getPrefix(String name) {
        int i = name.indexOf(".");
        if (i > 0) {
            return (name.substring(0, i));
        } else {
            return(null);
        }
    }

    /**
     * Looks up an application name for a give AppID
     * 
     * @param appID
     * @return 
     */
    public String getAppName(String appID) {
        return (appIDMap.get(appID));
    }

}
