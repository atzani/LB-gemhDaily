package GemhDaily;


/**
 * 
 * @author A.Tzani
 *
 */

public class Configurations {
	
	static HelperMethods hm = new HelperMethods();
		
	public static final String mainLink = "https://www.businessregistry.gr/publicity/index";
	public static final String resultsLink = "https://www.businessregistry.gr/publicity/show/";
	
	@SuppressWarnings("static-access")
	public static final String filePathLocal = hm.readConfigurationsFile("GemhDaily", "filePathLocal");					//public static String metadataFileLocal = "C:/Users/Aggelos/workspace/GemhScrap/";							// local metadata filepath
	@SuppressWarnings("static-access")
	public static String metadataFileLocal = hm.readConfigurationsFile("GemhDaily", "metadataFileLocal");				// local metadata filepath
	@SuppressWarnings("static-access")
	public static final String filePathPdfLocal = hm.readConfigurationsFile("GemhDaily", "filePathPdfLocal");			// local Pdf file filepath
	@SuppressWarnings("static-access")
	public static final String filePathExportLocal = hm.readConfigurationsFile("GemhDaily", "filePathExportLocal");		// local export files filepath
	public static final String filePathConfigurationLocal = "C:/Users/Aggelos/workspace/GemhDaily/file.txt";			// local configuration file filepath
	
	
	@SuppressWarnings("static-access")
	public static final String filePathServer = hm.readConfigurationsFile("GemhDaily", "filePathServer");					// server filepath
	@SuppressWarnings("static-access")
	public static String metadataFileServer = hm.readConfigurationsFile("GemhDaily", "metadataFileServer");					// server metadata filepath
	@SuppressWarnings("static-access")
	public static final String filePathPdfServer = hm.readConfigurationsFile("GemhDaily", "filePathPdfServer");				// local Pdf file filepath
	@SuppressWarnings("static-access")
	public static final String filePathExportServer = hm.readConfigurationsFile("GemhDaily", "filePathExportServer");		// local export files filepath
	@SuppressWarnings("static-access")
	public static final String filePathConfigurationServer = hm.readConfigurationsFile("GemhDaily", "filePathConfigurationServer");		// local configuration file filepath
	
	//@SuppressWarnings("static-access")
	//public static final String ffPath = hm.readConfigurationsFile("GemhDaily", "ffPath");				// firefox path in the server
	//@SuppressWarnings("static-access")
	//public static final String displayNum = hm.readConfigurationsFile("GemhDaily", "displayNum");		// the number of virtual display, 10 for gemhMatch, 14 for newest and 15 for oldest
	
	public static boolean local = false;							// local or server mode
	public boolean gemhDetails = true;								// get company gemh details
	
}
