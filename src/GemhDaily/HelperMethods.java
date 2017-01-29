package GemhDaily;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

import elod.harvest.gemh.daily.GemhDaily;
import elod.harvest.gemh.daily.gemhDatas.GemhRecord;



/**
 * @author A. Tzanis
 */

public class HelperMethods {

	
	/**
     * Find and transform the current date into the yyyy-MM-dd format.
     * 
     * @return String the current date in the yyyy-MM-dd format
     */
	public String getCurrentDate() {
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = sdf.format(cal.getTime());
		
		return currentDate;
	}
	
	
	/**
     * Find the previus date into the yyyy-MM-dd format.
     * 
     * @return String the previus date in the yyyy-MM-dd format
     */
	public static String getStartDate(int days) {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE,-days);    
	    String previusDate = dateFormat.format(cal.getTime());
		
		return previusDate;
	}
	
	
	/**
     * Find the prePrevius date into the yyyy-MM-dd format.
     * 
     * @return String the previus date in the yyyy-MM-dd format
     */
	public String getStopDate(int days) {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -days);    
	    String prePreviusDate = dateFormat.format(cal.getTime());
		
		return prePreviusDate;
	}
	
	
	/**Create a new directory at directoryPath with the directoryName we want
     * 
     * @param directoryPath the path where we want to create the new directory
     * @param directoryName the name we want to give to the directory
     * @return the new directory
     */
	public static File directoryCreator (String directoryPath, String directoryName) {
       
    	File newDirectory = new File(directoryPath, directoryName);		// Create a new directory
		
    	if(!newDirectory.exists()){										// Check if directory exists
			newDirectory.mkdirs();
		}
		return newDirectory;
		
    }
  	
    
	/** 
     * Check if a file exists in a directory 
     */ 
    public static boolean fileChecker(String companyGemi, String directoryPath){
    	
    	
    	boolean flag = false;
       
    	String filePath = null; 			
    	
    	if (Configurations.local){
			filePath = directoryPath +companyGemi+ ".csv";					//Variable to store the output filepath
		}
		else{
			filePath =  directoryPath + companyGemi +".csv";				//Variable to store the output filepath
		}
		 
        File gemhCsv = new File(filePath);														// Path of the gemh csv file if it already exists
       
        // check if file exists in gemh directory
        if (gemhCsv.exists()){
			System.out.println("\nFile " + companyGemi + ".csv already exist in " +filePath+ " directory!\n");
			writeUnknownMetadata("GemhVatIdList", companyGemi);
			flag = true;
		}
        
        return flag;																			// returns true if file exists and false if not
    }
	
	
    
	/** 
     * Check if gemh number exist in db 
	 * @throws SQLException 
     */ 
    public static boolean gemhChecker(String companyGemi, GemhDaily gd) throws SQLException{
    	
    	
    	boolean flag;
       
    	GemhRecord gemhRecord = gd.getGemhStarting(companyGemi);								//Get the gemhRecord which gemh starts with companyGemi
    	
    	try{
    		@SuppressWarnings("unused")
			String gemhNumber = gemhRecord.getMain().getGemhnumber();
    		flag = true;																		//If gemhRecord exists true
    	}
    	catch(Exception e){
    		flag = false;																		//Else false
    	}
        
        return flag;																			// returns true if file exists and false if not
    }
    
    
    /** Enter sleep mode until 00:01:00 plus the value of int hours of the next day
	 * @param hour int hours how many hours after 00:01:00 we want to start again. The value must be between 0 - 23.
     * 
     * @throws InterruptedException
     */
    public static void sleepMode(int hour) throws InterruptedException{
    	
    	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    	Calendar cal = Calendar.getInstance();
    	String time = dateFormat.format(cal.getTime());
    	System.out.println(time);
    	    	
    	int Hour = Integer.parseInt(time.split(":")[0]);
		int min = Integer.parseInt(time.split(":")[1]);
		int sec = Integer.parseInt(time.split(":")[2]);
		
		int sleep = ((24 - Hour - 1 + hour)*3600)+((60-min)*60)+((60-sec)*1);
		
		int sleepHour = sleep/3600;
		int sleepMin = (sleep%3600)/60;
		int sleepSec = (sleep%3600)%60;
		
		System.out.println("Entering sleep mode for " +corectTimeFormat(sleepHour)+ ":" +corectTimeFormat(sleepMin)+ ":" + corectTimeFormat(sleepSec));
		
		TimeUnit.SECONDS.sleep(sleep);										// Sleep 10 seconds
	
	}
    
    
    /** Duration of searching in HH:mm:ss format
     * 
     * @param String startingTime
     * @return String duration
     */
    public static String searchDuration(String startingTime){
    	
    	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    	Calendar cal = Calendar.getInstance();
    	String endTime = dateFormat.format(cal.getTime());
    	System.out.println("Search starts at: " +startingTime);
    	System.out.println("Search ends at: " +endTime);
    	
    	int startHour = Integer.parseInt(startingTime.split(":")[0]);
		int startMin = Integer.parseInt(startingTime.split(":")[1]);
		int startSec = Integer.parseInt(startingTime.split(":")[2]);
		
    	int endHour = Integer.parseInt(endTime.split(":")[0]);
		int endMin = Integer.parseInt(endTime.split(":")[1]);
		int endSec = Integer.parseInt(endTime.split(":")[2]);
		
		int running = ((endHour - startHour)*3600)+((endMin-startMin)*60)+((endSec-startSec)*1);
		
		int runningHour = running/3600;
		int runningMin = (running%3600)/60;
		int runningSec = (running%3600)%60;
		
		String duration = corectTimeFormat(runningHour)+ ":" +corectTimeFormat(runningMin)+ ":" + corectTimeFormat(runningSec);
		
		System.out.println("Search duration was: " +duration);
		
		return duration ;
	
	}
    
    
    /** Current time in "HH:mm:ss" format
     * 
     * @param String startingTime
     * @return String duration
     */
    public static String currentTime(){
    	
    	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    	Calendar cal = Calendar.getInstance();
    	String currentTime = dateFormat.format(cal.getTime());
    	
    	System.out.println("Searching starts at: " +currentTime);
    	
		return currentTime ;
	
	}
    
    
    /** Gives the corect time format, if given hour 1 convert it to 01 etc
     * 
     * @param String input
     * @return String corectInput
     */
    public static String corectTimeFormat(int input){
    	
    	String corectInput = Integer.toString(input);
    	
    	if (corectInput.length() == 1){
    		corectInput = "0" +corectInput;
    	}
    	
		return corectInput ;
	
	}
    
    
    /**
	 * Convert a date from 1/8/2013 to 2013-08-01
	 * @param String date
	 * @return String date
	 */
	public String dateConverter (String date){
		
		if (!date.equalsIgnoreCase("") && !date.isEmpty()){
			
			String dateParts[] = date.split("/");
			
			if (dateParts[1].length() == 1){
				dateParts[1] = "0" + dateParts[1];
			}
			
			if (dateParts[0].length() == 1){
				dateParts[0] = "0" + dateParts[0];
			}
			
			date = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
			
		}
		
		return date.replaceAll("\\s+","");
	}
	
	
	/**
	 * Convert a date from 1-8-2013 to 2013-08-01
	 * @param String date
	 * @return String date
	 */
	public String dateConverter1 (String date){
		
		if (!date.equalsIgnoreCase("") && !date.isEmpty()){
			
			String dateParts[] = date.split("-");
			
			if (dateParts[1].length() == 1){
				dateParts[1] = "0" + dateParts[1];
			}
			
			if (dateParts[0].length() == 1){
				dateParts[0] = "0" + dateParts[0];
			}
			
			date = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
			
		}
		
		return date.replaceAll("\\s+","");
	}
	
	
	
	/**
	 * Convert the Kad description to be compatible for the csv
	 * @param String KadDescription
	 * @return String KadDescription
	 */
	public String kadConverter (String KadDescription){
		
		if (!KadDescription.equalsIgnoreCase("") && !KadDescription.isEmpty()){
			
			KadDescription = KadDescription.replaceAll("-", "").replaceAll(", ", " - ");
		
		}
		
		return KadDescription;
	}
	
	
	/**
	 * Convert the string to be valid for the csv
	 * @param String string
	 * @return String string
	 */
	public String stringValidator (String string){
		
		if (!string.equalsIgnoreCase("") && !string.isEmpty()){
			if (string.startsWith(" ")){
				string = string.substring(1);							//Removes the first character of the string if it si the " "
			}
			string = string.replaceAll("(\\r|\\n)", "");				//Delates the change line character
		}
		
		return string;
        
	}
	
	
	
    /**
     * Export to a file the unknown metadata.
     * 
     * @param fileName String the output filename
     * @param metadata String the unknown metadata
     */
	@SuppressWarnings("static-access")
	public static void writeUnknownMetadata(String fileName, String metadata) {
		
		Configurations pc = new Configurations();
		Writer out;
		
		try {
			
			if (fileName.contains("Pdf_")){
				
				if(pc.local){
					out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream
						                    (pc.filePathPdfLocal + fileName + ".csv", true), "UTF8"));
				}
				else{
					out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream
											(pc.filePathPdfServer + fileName + ".csv", true), "UTF8"));
				}
				
			}
			else {
				
				if(pc.local){
					out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream
	                       (pc.filePathExportLocal + fileName + ".csv", true), "UTF8"));
				}
				else{
					out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream
		                       (pc.filePathExportServer + fileName + ".csv", true), "UTF8"));
				}
				
			}
			
			out.append(metadata);
		    out.append(System.getProperty("line.separator"));
		    out.close();
		    
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
	}
    
	
	/** Firefox profile builder **/
    public static FirefoxProfile firefoxProfile(){
    	
    	FirefoxProfile profile = new FirefoxProfile();

    	 profile.setPreference("browser.download.folderList", 2);
         profile.setPreference("plugin.disable_full_page_plugin_for_types", "application/pdf");
         profile.setPreference("browser.helperApps.neverAsk.saveToDisk","application/csv,text/csv,application/pdfss, application/excel" );
         profile.setPreference("browser.download.manager.showWhenStarting", false);
         profile.setPreference("pdfjs.disabled", true);
         
		return profile;
    	
    }
     
    /** Start firefox in headless gui
     * 
     * @param ffPath String the firefox path in the server
     * @param displayNum String the xvbf display number.
     * @return firefoxBinary FirefoxBinary
     */
    public static FirefoxBinary firefoxBinary(String ffPath, String displayNum){
    	
    	// Setup firefox binary to start in Xvfb        
        displayNum = ":" + displayNum;											// Stores the display number created in xcfb
    	String Xport = System.getProperty(						
                "lmportal.xvfb.id", displayNum);								// Get the xvfb id
        final File firefoxPath = new File(System.getProperty(
                "lmportal.deploy.firefox.path", ffPath));						// Get the firefox path in the system
        FirefoxBinary firefoxBinary = new FirefoxBinary(firefoxPath);			// Create a new instance of the firefox binary 
        firefoxBinary.setEnvironmentProperty("DISPLAY", Xport);					// Set the display number to the firefoxBinary
    	
        return firefoxBinary;
        
    }


    /** get the full name of the company
     * 
     * @param resultsLink String the url of the results page.
     * @return coFullName String the company full name
     * @throws InterruptedException
     */
	public String fullNameFinder(String resultsLink, WebDriver driverDetails) throws InterruptedException {

		String coFullName = null;												// Company full name
		
		driverDetails.get(resultsLink);												//Driver visit resultsLink
		
		/** in server mode check if firefox and Xvfb is working **/
		if (!Configurations.local){
			driverCheck(driverDetails);
		}
		
		(new WebDriverWait(driverDetails, 20))
		.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]"
						+ "/div[2]/div/div/div/div[2]/div[2]/div[2]"))); 		// waits until page is loaded, max wait time 10 secs
			
		coFullName = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]"
						+ "/div[2]/div/div/div/div[2]/div[2]/div[2]")).getText();			// company full name

		return coFullName;
	}
    
    
	/**
     * Export to a file the current page number.
     * 
     * @param String the page number
     */
	public static void writeMetadata(String fileName, int matadata) {
		
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName + ".csv", false)));
		    out.println(matadata);
		    out.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
	}


	/**
     * Click the button found by the elementId given.
     * 
     * @param WebDriver driver 
     * @param String elementId
     */
	public static void click(WebDriver driver, String elementId) {
		
		new WebDriverWait(driver, 10)
	    .ignoring(StaleElementReferenceException.class)
	    .until(new Predicate<WebDriver>() {
	        public boolean apply(WebDriver driver) {
	            driver.findElement(By.id(elementId)).click();
	            return true;
	        }
	    });
		
	}
	
	
	/**
	 * 
	 * @param by
	 * @param driver
	 * @return
	 */
	WebElement getStaleElem(By by, WebDriver driver) {
	    
		try {
			System.out.println("Trying to return element..");
	        return driver.findElement(by);
	    } catch (StaleElementReferenceException e) {
	        System.out.println("Attempting to recover from StaleElementReferenceException ...");
	        return getStaleElem(by, driver);
	    } catch (NoSuchElementException ele) {
	        System.out.println("Attempting to recover from NoSuchElementException ...");
	        return getStaleElem(by, driver);
	    }
	}
	
	
	
	/** Check if driver is conected 
	 * 
	 * @param driver WebDriver
	 * @throws InterruptedException 
	 */
	public static void driverCheck(WebDriver driver) throws InterruptedException {
		
		if (driver.toString() == null){
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			HelperMethods.writeUnknownMetadata("connectionFailure", dateFormat.format(date));
			System.exit(0);
		}
		
	}
	
	
	/** Get from the server the id of working Xvfb, and count them
	 * 
	 * @return length int the number of working Xvfb
	 */
	public static int xvbfId() {
		
		String line = null;
		int length = 0;
		
	    try {
	      Process p = Runtime.getRuntime().exec("pidof Xvfb");
	      BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
	      while ((line = input.readLine()) != null)
	      {
	        System.out.println(line);
	        length = line.split(" ").length;
	        System.out.println(length);
	      }
	    } catch (Exception err) {
	      System.out.println(err);
	    }

		return length;
	}


	
	/**Validate the number for the gemh number.
	 * 
	 * @param number int the number we want to validate.
	 * @param length int the wanted length.
	 * @return validNumber String the number with the wanted length.
	 */
	public static String getValidNumbers(int number, int length) {

		String validNumber = Integer.toString(number);
		
		while (validNumber.length() != length){
			validNumber = "0"+validNumber;
		}

		return validNumber;
	}


	/**Calculate the first gemh to search, given the past day's first gemh number searched.
	 * 
	 * @param Integer.parseInt(startGemh) int the last gemh number searched last day.
	 * @return startGemhNumber int the first gemh number to search.
	 */
	
	public String getStartGemhNumber(String startGemh) {

		String startGemhNumber = Integer.toString(Integer.parseInt(startGemh) + 1);
		
		return startGemhNumber;
	}


	//@SuppressWarnings("static-access")
	public static WebDriver initializeDriver() throws InterruptedException {

		//Configurations pc = new Configurations();
    	//WebDriver driver = null;
    	
		/** driver initialization **/
		/*if (pc.local){
			//driver = new HtmlUnitDriver();										// Create a new instance of the firefox driver on server mode
			driver = new FirefoxDriver();											// Create a new instance of the firefox driver on server mode
		}
		else{
			String ffPath = pc.ffPath;												// firefox path in the server
			String displayNum = pc.displayNum;										// the number of virtual display
			FirefoxProfile profile = firefoxProfile();								// Creates a new profile
	    	FirefoxBinary binary = firefoxBinary(ffPath, displayNum);				// Creates a new binary
	    	driver = new FirefoxDriver(binary, profile);							// Create a new instance of the firefox driver on server mode
	    }
		
		//To hide warnings logs from execution console.
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.OFF);
		
		TimeUnit.SECONDS.sleep(1);
		*/
    	
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);
		caps.setCapability("screen-resolution", "1280x1024");
		caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		//caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "phantomjs.exe");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "/usr/local/share/phantomjs-1.9.8-linux-x86_64/bin/phantomjs"); 
		
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--webdriver-loglevel=NONE");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
		Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
		
		WebDriver driver = new PhantomJSDriver(caps);
        
		return driver;
	}

	
	/**
     * Read the value of configuration data and returns it.
     * 
     * @param file String the configuration data file name.
     * @param dataName String the configuration data we want to get the value.
     *  
     * @return txt String the value of the configuration data
     */
	public static String readConfigurationsFile(String file, String dataName) {
		
		String txt = null;
		boolean found = false;
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(file+".csv"));
			
			String line;
			while ((line = in.readLine()) != null) {
			
				if (dataName.equalsIgnoreCase(line.split("=")[0])){
					txt = line.split("=")[1];
					found = true;
				}
				
			}
			
			if (!found){
				System.out.println("Configuration data "+dataName+" not found!");
			}
			
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Cannot read processed CSV file!");
		}

		return txt;
	}
	
	
	/**
     * Read the last valid gemh from the csv file and return it.
     * 
     * @return startGemh String the last valid gemh
     */
	public static String getlastValidGemh(String filePath) {
		
		String startGemh = null;
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath+".csv"));
			String line;
			while ((line = in.readLine()) != null) {
				startGemh = line;
			}
			
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Cannot read processed TXT file!");
		}

		return startGemh;
	}
    
}
