/*
 * Updater for Bukkit.
 *
 * This class provides the means to safely and easily update a plugin, or check to see if it is updated using dev.bukkit.org
 */

package Bammerbom.UltimateCore;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Check dev.bukkit.org to find updates for a given plugin, and download the updates if needed.
 * <p/>
 * <b>VERY, VERY IMPORTANT</b>: Because there are no standards for adding auto-update toggles in your plugin's config, this system provides NO CHECK WITH YOUR CONFIG to make sure the user has allowed auto-updating.
 * <br>
 * It is a <b>BUKKIT POLICY</b> that you include a boolean value in your config that prevents the auto-updater from running <b>AT ALL</b>.
 * <br>
 * If you fail to include this option in your config, your plugin will be <b>REJECTED</b> when you attempt to submit it to dev.bukkit.org.
 * <p/>
 * An example of a good configuration option would be something similar to 'auto-update: true' - if this value is set to false you may NOT run the auto-updater.
 * <br>
 * If you are unsure about these rules, please read the plugin submission guidelines: http://goo.gl/8iU5l
 *
 * @author Gravity
 * @version 2.1
 */

public class UltimateUpdater {

    private static Plugin plugin;
    private UpdateType type;
    private static String versionName;
    private String versionLink;
    private String versionType;
    private String versionGameVersion;

    private boolean announce; // Whether to announce file downloads

    private URL url; // Connecting to RSS
    private File file; // The plugin's file
    private static Thread thread; // Updater thread

    //private int id = -1; // Project's Curse ID
    private String apiKey = null; // BukkitDev ServerMods API key
    private static final String TITLE_VALUE = "name"; // Gets remote file's title
    private static final String LINK_VALUE = "downloadUrl"; // Gets remote file's download link
    private static final String TYPE_VALUE = "releaseType"; // Gets remote file's release type
    private static final String VERSION_VALUE = "gameVersion"; // Gets remote file's build version
    private static final String QUERY = "/servermods/files?projectIds="; // Path to GET
    private static final String HOST = "https://api.curseforge.com"; // Slugs will be appended to this to get to the project's RSS feed

    private static final String USER_AGENT = "Updater (by Gravity)";
    private static final String delimiter = "^v|[\\s_-]v"; // Used for locating version numbers in file names
    //private static final String[] NO_UPDATE_TAG = { "-DEV", "-PRE", "-SNAPSHOT" }; // If the version number contains one of these, don't update.
    private static final int BYTE_SIZE = 1024; // Used for downloading files
    private final YamlConfiguration config = new YamlConfiguration(); // Config file
    private String updateFolder;// The folder that downloads will be placed in
    private UltimateUpdater.UpdateResult result = UltimateUpdater.UpdateResult.SUCCESS; // Used for determining the outcome of the update process

    /**
     * Gives the developer the result of the update process. Can be obtained by called {@link #getResult()}
     */
    public enum UpdateResult {
        /**
         * The updater found an update, and has readied it to be loaded the next time the server restarts/reloads.
         */
        SUCCESS,
        /**
         * The updater did not find an update, and nothing was downloaded.
         */
        NO_UPDATE,
        /**
         * The server administrator has disabled the updating system.
         */
        DISABLED,
        /**
         * The updater found an update, but was unable to download it.
         */
        FAIL_DOWNLOAD,
        /**
         * For some reason, the updater was unable to contact dev.bukkit.org to download the file.
         */
        FAIL_DBO,
        /**
         * When running the version check, the file on DBO did not contain a recognizable version.
         */
        FAIL_NOVERSION,
        /**
         * The id provided by the plugin running the updater was invalid and doesn't exist on DBO.
         */
        FAIL_BADID,
        /**
         * The server administrator has improperly configured their API key in the configuration.
         */
        FAIL_APIKEY,
        /**
         * The updater found an update, but because of the UpdateType being set to NO_DOWNLOAD, it wasn't downloaded.
         */
        UPDATE_AVAILABLE
    }

    /**
     * Allows the developer to specify the type of update that will be run.
     */
    public enum UpdateType {
        /**
         * Run a version check, and then if the file is out of date, download the newest version.
         */
        DEFAULT,
        /**
         * Don't run a version check, just find the latest update and download it.
         */
        NO_VERSION_CHECK,
        /**
         * Get information about the version and the download size, but don't actually download anything.
         */
        NO_DOWNLOAD
    }

    /**
     * Represents the various release types of a file on BukkitDev.
     */
    public enum ReleaseType {
        /**
         * An "alpha" file.
         */
        ALPHA,
        /**
         * A "beta" file.
         */
        BETA,
        /**
         * A "release" file.
         */
        PRERELEASE,
        
        RELEASE
    }

    /**
     * Initialize the updater.
     *
     * @param plugin   The plugin that is checking for an update.
     * @param id       The dev.bukkit.org id of the project.
     * @param file     The file that the plugin is running from, get this by doing this.getFile() from within your main class.
     * @param type     Specify the type of update this will be. See {@link UpdateType}
     * @param announce True if the program should announce the progress of new updates in console.
     */
    public UltimateUpdater(Plugin plugin, int id, File file, UpdateType type, boolean announce) {
        UltimateUpdater.plugin = plugin;
        this.type = type;
        this.announce = announce;
        this.file = file;

        this.updateFolder = plugin.getServer().getUpdateFolder();

        final File pluginFile = plugin.getDataFolder().getParentFile();
        final File updaterFile = new File(pluginFile, "Updater");
        final File updaterConfigFile = new File(updaterFile, "config.yml");

        this.config.options().header("This configuration file affects all plugins using the Updater system (version 2+ - http://forums.bukkit.org/threads/96681/ )" + '\n'
                + "If you wish to use your API key, read http://wiki.bukkit.org/ServerMods_API and place it below." + '\n'
                + "Some updating systems will not adhere to the disabled value, but these may be turned off in their plugin's configuration.");
        this.config.addDefault("api-key", "PUT_API_KEY_HERE");
        this.config.addDefault("disable", false);

        if (!updaterFile.exists()) {
            updaterFile.mkdir();
        }

        boolean createFile = !updaterConfigFile.exists();
        try {
            if (createFile) {
                updaterConfigFile.createNewFile();
                this.config.options().copyDefaults(true);
                this.config.save(updaterConfigFile);
            } else {
                this.config.load(updaterConfigFile);
            }
        } catch (final Exception e) {
            if (createFile) {
                r.log(r.error + "The updater could not create configuration at " + updaterFile.getAbsolutePath());
            } else {
                r.log(r.error + "The updater could not load configuration at " + updaterFile.getAbsolutePath());
            }
        }

        if (this.config.getBoolean("disable")) {
            this.result = UpdateResult.DISABLED;
            return;
        }

        String key = this.config.getString("api-key");
        if (key.equalsIgnoreCase("PUT_API_KEY_HERE") || key.equals("")) {
            key = null;
        }

        this.apiKey = key;

        try {
            this.url = new URL(UltimateUpdater.HOST + UltimateUpdater.QUERY + id);
        } catch (final MalformedURLException e) {
            r.log("Could not connect to network for updating.");
            this.result = UpdateResult.FAIL_BADID;
        }

        UltimateUpdater.thread = new Thread(new UpdateRunnable());
        UltimateUpdater.thread.start();
    }

    /**
     * Get the result of the update process.
     *
     * @return result of the update process.
     * @see UpdateResult
     */
    public UltimateUpdater.UpdateResult getResult() {
        UltimateUpdater.waitForThread();
        return this.result;
    }

    /**
     * Get the latest version's release type.
     *
     * @return latest version's release type.
     * @see ReleaseType
     */
    public ReleaseType getLatestType() {
        UltimateUpdater.waitForThread();
        if (this.versionType != null) {
            for (ReleaseType type : ReleaseType.values()) {
                if (this.versionType.equals(type.name().toLowerCase())) {
                    return type;
                }
            }
        }
        return null;
    }

    /**
     * Get the latest version's game version (such as "CB 1.2.5-R1.0").
     *
     * @return latest version's game version.
     */
    public String getLatestGameVersion() {
        UltimateUpdater.waitForThread();
        return this.versionGameVersion;
    }

    /**
     * Get the latest version's name (such as "Project v1.0").
     *
     * @return latest version's name.
     */
    public static String getLatestName() {
        waitForThread();
        return versionName;
    }

    /**
     * Get the latest version's direct file link.
     *
     * @return latest version's file link.
     */
    public String getLatestFileLink() {
        UltimateUpdater.waitForThread();
        return this.versionLink;
    }

    /**
     * As the result of Updater output depends on the thread's completion, it is necessary to wait for the thread to finish
     * before allowing anyone to check the result.
     */
    private static void waitForThread() {
        if ((thread != null) && thread.isAlive()) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
            }
        }
    }

    /**
     * Save an update from dev.bukkit.org into the server's update folder.
     *
     * @param folder the updates folder location.
     * @param file the name of the file to save it as.
     * @param link the url of the file.
     */
    private void saveFile(File folder, String file, String link) {
        if (!folder.exists()) {
            folder.mkdir();
        }
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            // Download the file
            final URL url = new URL(link);
            final int fileLength = url.openConnection().getContentLength();
            in = new BufferedInputStream(url.openStream());
            fout = new FileOutputStream(folder.getAbsolutePath() + File.separator + file);

            final byte[] data = new byte[UltimateUpdater.BYTE_SIZE];
            int count;
            if (this.announce) {
            	r.log("Downloading update: " + UltimateUpdater.versionName);
            }
            long downloaded = 0;
            while ((count = in.read(data, 0, UltimateUpdater.BYTE_SIZE)) != -1) {
                downloaded += count;
                fout.write(data, 0, count);
                final int percent = (int) ((downloaded * 100) / fileLength);
                if (this.announce && ((percent % 10) == 0)) {
                	//r.log("Downloading update: " + UltimateUpdater.versionName);
                	r.log(percent + "% " + "(" + fileLength + " bytes)");
                }
            }
            //Just a quick check to make sure we didn't leave any files from last time...
            for (final File xFile : new File(UltimateUpdater.plugin.getDataFolder().getParent(), this.updateFolder).listFiles()) {
                if (xFile.getName().endsWith(".zip")) {
                    xFile.delete();
                }
            }
            // Check to see if it's a zip file, if it is, unzip it.
            final File dFile = new File(folder.getAbsolutePath() + File.separator + file);
            if (dFile.getName().endsWith(".zip")) {
                // Unzip
                this.unzip(dFile.getCanonicalPath());
            }
            if (this.announce) {
                r.log("Downloaded new update! Reload incomming.");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                	public void run(){
                		Bukkit.getServer().reload();
                	}
                }, 60L);
            }
        } catch (final Exception ex) {
        	if(ex.getCause() == null){
            r.log(r.error + "Failed to download update. ");
        	}else{
        		r.log(r.error + "Failed to download update. " + ex.getCause());
        	}
            this.result = UltimateUpdater.UpdateResult.FAIL_DOWNLOAD;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            } catch (final Exception ex) {
            }
        }
    }

    /**
     * Part of Zip-File-Extractor, modified by Gravity for use with Updater.
     *
     * @param file the location of the file to extract.
     */
    private void unzip(String file) {
        try {
            final File fSourceZip = new File(file);
            final String zipPath = file.substring(0, file.length() - 4);
            ZipFile zipFile = new ZipFile(fSourceZip);
            Enumeration<? extends ZipEntry> e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                File destinationFilePath = new File(zipPath, entry.getName());
                destinationFilePath.getParentFile().mkdirs();
                if (entry.isDirectory()) {
                    continue;
                } else {
                    final BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                    int b;
                    final byte buffer[] = new byte[UltimateUpdater.BYTE_SIZE];
                    final FileOutputStream fos = new FileOutputStream(destinationFilePath);
                    final BufferedOutputStream bos = new BufferedOutputStream(fos, UltimateUpdater.BYTE_SIZE);
                    while ((b = bis.read(buffer, 0, UltimateUpdater.BYTE_SIZE)) != -1) {
                        bos.write(buffer, 0, b);
                    }
                    bos.flush();
                    bos.close();
                    bis.close();
                    final String name = destinationFilePath.getName();
                    if (name.endsWith(".jar") && this.pluginFile(name)) {
                        destinationFilePath.renameTo(new File(UltimateUpdater.plugin.getDataFolder().getParent(), this.updateFolder + File.separator + name));
                    }
                }
                entry = null;
                destinationFilePath = null;
            }
            e = null;
            zipFile.close();
            zipFile = null;

            // Move any plugin data folders that were included to the right place, Bukkit won't do this for us.
            for (final File dFile : new File(zipPath).listFiles()) {
                if (dFile.isDirectory()) {
                    if (this.pluginFile(dFile.getName())) {
                        final File oFile = new File(UltimateUpdater.plugin.getDataFolder().getParent(), dFile.getName()); // Get current dir
                        final File[] contents = oFile.listFiles(); // List of existing files in the current dir
                        for (final File cFile : dFile.listFiles()) // Loop through all the files in the new dir
                        {
                            boolean found = false;
                            for (final File xFile : contents) // Loop through contents to see if it exists
                            {
                                if (xFile.getName().equals(cFile.getName())) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                // Move the new file into the current dir
                                cFile.renameTo(new File(oFile.getCanonicalFile() + File.separator + cFile.getName()));
                            } else {
                                // This file already exists, so we don't need it anymore.
                                cFile.delete();
                            }
                        }
                    }
                }
                dFile.delete();
            }
            new File(zipPath).delete();
            fSourceZip.delete();
        } catch (final IOException e) {
        	r.log(r.error + "Failed to unzip file.");
            this.result = UltimateUpdater.UpdateResult.FAIL_DOWNLOAD;
        }
        new File(file).delete();
    }

    /**
     * Check if the name of a jar is one of the plugins currently installed, used for extracting the correct files out of a zip.
     *
     * @param name a name to check for inside the plugins folder.
     * @return true if a file inside the plugins folder is named this.
     */
    private boolean pluginFile(String name) {
        for (final File file : new File("plugins").listFiles()) {
            if (file.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check to see if the program should continue by evaluating whether the plugin is already updated, or shouldn't be updated.
     *
     * @param title the plugin's title.
     * @return true if the version was located and is not the same as the remote's newest.
     */
    private boolean versionCheck(String title) {
        if (this.type != UpdateType.NO_VERSION_CHECK) {
            final String localVersion = UltimateUpdater.plugin.getDescription().getVersion();
            if (title.split(delimiter).length == 2) {
                final String remoteVersion = title.split(delimiter)[1].split(" ")[0]; // Get the newest file's version number

                if (this.hasTag(localVersion) || !this.shouldUpdate(localVersion, remoteVersion)) {
                    // We already have the latest version, or this build is tagged for no-update
                    this.result = UltimateUpdater.UpdateResult.NO_UPDATE;
                    r.log("No update available.");
                    return false;
                }
            } else {
                // The file's name did not contain the string 'vVersion'
                r.log(r.error + "Please leave a ticket on the site saying: \"Updater failed to convert name\"");
                this.result = UltimateUpdater.UpdateResult.FAIL_NOVERSION;
                return false;
            }
        }
        return true;
    }
    public static String getLatestUpdate(){
    	if(thread != null && thread.isAlive()) return "";
    	String versionName = getLatestName();
    	if(versionName == null || versionName.equalsIgnoreCase("")) return "";
    	final String remoteVersion = versionName.split(delimiter)[1].split(" ")[0]; 
    	return remoteVersion;
    }

    /**
     * <b>If you wish to run mathematical versioning checks, edit this method.</b>
     * <p>
     * With default behavior, Updater will NOT verify that a remote version available on BukkitDev
     * which is not this version is indeed an "update".
     * If a version is present on BukkitDev that is not the version that is currently running,
     * Updater will assume that it is a newer version.
     * This is because there is no standard versioning scheme, and creating a calculation that can
     * determine whether a new update is actually an update is sometimes extremely complicated.
     * </p>
     * <p>
     * Updater will call this method from {@link #versionCheck(String)} before deciding whether
     * the remote version is actually an update.
     * If you have a specific versioning scheme with which a mathematical determination can
     * be reliably made to decide whether one version is higher than another, you may
     * revise this method, using the local and remote version parameters, to execute the
     * appropriate check.
     * </p>
     * <p>
     * Returning a value of <b>false</b> will tell the update process that this is NOT a new version.
     * Without revision, this method will always consider a remote version at all different from
     * that of the local version a new update.
     * </p>
     * @param localVersion the current version
     * @param remoteVersion the remote version
     * @return true if Updater should consider the remote version an update, false if not.
     */
    public boolean shouldUpdate(String localVersion, String remoteVersion) {
    	try{
    	Integer l1 = Integer.parseInt(localVersion.split("\\.")[0]);
    	Integer l2 = Integer.parseInt(localVersion.split("\\.")[1]);
    	Integer l3; try{l3 = Integer.parseInt(localVersion.split("\\.")[2]);}catch(ArrayIndexOutOfBoundsException e){l3 = 0;}
    	Integer r1 = Integer.parseInt(remoteVersion.split("\\.")[0]);
    	Integer r2 = Integer.parseInt(remoteVersion.split("\\.")[1]);
    	Integer r3; try{r3 = Integer.parseInt(remoteVersion.split("\\.")[2]);}catch(ArrayIndexOutOfBoundsException e){r3 = 0;}
    	if(l1 < r1){
    		return true;
    	}
    	if(l2 < r2 && !(l1 > r1)){
    		return true;
    	}
    	if(l3 < r3 && !(l2 > r2) && !(l1 > r1)){
    		return true;
    	}
    	return false;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return false;
    	}
    }

    /**
     * Evaluate whether the version number is marked showing that it should not be updated by this program.
     *
     * @param version a version number to check for tags in.
     * @return true if updating should be disabled.
     */
    private boolean hasTag(String version) {
        return false;
    }

    /**
     * Make a connection to the BukkitDev API and request the newest file's details.
     *
     * @return true if successful.
     */
    private boolean read() {
        try {
            final URLConnection conn = this.url.openConnection();
            conn.setConnectTimeout(5000);

            if (this.apiKey != null) {
                conn.addRequestProperty("X-API-Key", this.apiKey);
            }
            conn.addRequestProperty("User-Agent", UltimateUpdater.USER_AGENT);

            conn.setDoOutput(true);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();

            final JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.size() == 0) {
                r.log(r.error + "No files found for UltimateCore, update check failed.");
                this.result = UpdateResult.FAIL_BADID;
                return false;
            }

            UltimateUpdater.versionName = (String) ((JSONObject) array.get(array.size() - 1)).get(UltimateUpdater.TITLE_VALUE);
            this.versionLink = (String) ((JSONObject) array.get(array.size() - 1)).get(UltimateUpdater.LINK_VALUE);
            this.versionType = (String) ((JSONObject) array.get(array.size() - 1)).get(UltimateUpdater.TYPE_VALUE);
            this.versionGameVersion = (String) ((JSONObject) array.get(array.size() - 1)).get(UltimateUpdater.VERSION_VALUE);

            return true;
        } catch (final Exception e) {
            if (e.getMessage().contains("HTTP response code: 403")) {
            	r.log(r.error + "Bukkit rejected the API key provided in plugins/Updater/config.yml");
            	r.log(r.error + "Please double-check your configuration to ensure it is correct.");
                this.result = UpdateResult.FAIL_APIKEY;
            } else {
            	r.log(r.error + "No connection could be made to Bukkit, update check failed.");
                this.result = UpdateResult.FAIL_DBO;
            }
            return false;
        }
    }

    private class UpdateRunnable implements Runnable {

        public void run() {
            if (UltimateUpdater.this.url != null) {
                // Obtain the results of the project's file feed
                if (UltimateUpdater.this.read()) {
                    if (UltimateUpdater.this.versionCheck(UltimateUpdater.versionName)) {
                        if ((UltimateUpdater.this.versionLink != null) && (UltimateUpdater.this.type != UpdateType.NO_DOWNLOAD)) {
                            String name = UltimateUpdater.this.file.getName();
                            // If it's a zip file, it shouldn't be downloaded as the plugin's name
                            if (UltimateUpdater.this.versionLink.endsWith(".zip")) {
                                final String[] split = UltimateUpdater.this.versionLink.split("/");
                                name = split[split.length - 1];
                            }
                            UltimateUpdater.this.saveFile(new File(UltimateUpdater.plugin.getDataFolder().getParent(), UltimateUpdater.this.updateFolder), name, UltimateUpdater.this.versionLink);
                        } else {
                            UltimateUpdater.this.result = UpdateResult.UPDATE_AVAILABLE;
                        }
                    }
                }
            }
        }
    }
}