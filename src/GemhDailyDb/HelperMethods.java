package GemhDailyDb;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import elod.harvest.gemh.daily.gemhDatas.Cpa;
import elod.harvest.gemh.daily.gemhDatas.ExtraAnnouncements;
import elod.harvest.gemh.daily.gemhDatas.ExtraDecisionBodies;
import elod.harvest.gemh.daily.gemhDatas.ExtraLegalityControl;
import elod.harvest.gemh.daily.gemhDatas.ExtraRecomendationData;
import elod.harvest.gemh.daily.gemhDatas.ExtraRest;
import elod.harvest.gemh.daily.gemhDatas.Extras;
import elod.harvest.gemh.daily.gemhDatas.GemhRecord;
import elod.harvest.gemh.daily.gemhDatas.Main;

public class HelperMethods {

	public static List<Integer> getNullLongLat() throws SQLException{
		ResultSet rs=null;
		Statement mainStatement=null;
		Connection conn=null;
		List<Integer> records=new ArrayList<Integer>();
		try{
			conn = DriverManager.getConnection("jdbc:mysql://83.212.86.155/GemhDailyAll?useUnicode=true&characterEncoding=UTF-8","gemh","gemh@69");
			String selectMain="select COUNT(vatId) AS 'count' from Main;";
			mainStatement=conn.createStatement();
			rs = mainStatement.executeQuery(selectMain);
			
			while(rs.next()){
				
				records.add(rs.getInt("count"));
			}
			return records;
		}catch(SQLException e){
			try{rs.close();}catch(Exception e1){}
			try{mainStatement.close();}catch(Exception e1){}
			try{conn.close();}catch(Exception e1){}
			throw e;
		}finally{
			try{rs.close();}catch(Exception e1){}
			try{mainStatement.close();}catch(Exception e1){}
			try{conn.close();}catch(Exception e1){}
		}
		
		
	}
	
	
	
	/**
	 * get all main table data for the given ID
	 * @param mainId
	 * @return
	 * @throws SQLException
	 */
	public static GemhRecord getVatIdStarting(String vatId) throws SQLException	{
			
			Statement annoStatement = null;
			ResultSet annoRS = null;
			
			ResultSet deciRS = null;
			Statement deciStatement= null;
			
			ResultSet legalRS = null;
			Statement legalStatement = null;
	
			ResultSet recomRS = null;
			Statement recomStatement = null;
	
			ResultSet restRS = null;
			Statement restStatement = null;
			ResultSet cpaRS   = null;
			Statement cpaStatement  = null;
			ResultSet rs  = null;
			Statement mainStatement = null;
			Connection conn = null;
			try {
				conn = DriverManager.getConnection("jdbc:mysql://83.212.86.155/gemhdaily?useUnicode=true&characterEncoding=UTF-8","gemh","gemh@69");
				
				 mainStatement=conn.createStatement();
				 rs = mainStatement.executeQuery("select * from Main where vatId like '"+vatId+"%'" );
				if(rs.next()){
					//data found
					GemhRecord gemh=new GemhRecord();
					Main main=new Main();
					
					
					main.setDbId(rs.getInt("id"));
					main.setVatId(rs.getString("vatId"));
					main.setGemhnumber(rs.getString("gemhnumber"));
					main.setOrgType(rs.getString("orgType"));
					main.setName(rs.getString("name"));
					main.setBrandname(rs.getString("brandname"));
					main.setChamber(rs.getString("chamber"));
					main.setStatus(rs.getString("status"));
					main.setGemhdate(rs.getString("gemhdate"));
					main.setUrl(rs.getString("url"));
					main.setIssueddate(rs.getString("issueddate"));
					main.setCorrectVat(rs.getString("correctVat"));
					main.setStreet(rs.getString("street"));
					main.setLocality(rs.getString("locality"));
					main.setPostalCode(rs.getString("postalCode"));
					main.setDoyName(rs.getString("doyName"));
					main.setDoyCode(rs.getString("doyCode"));
					main.setValidVat(rs.getString("validVat"));
					main.setRegistrationDate(rs.getString("registrationDate"));
					main.setEndDate(rs.getString("endDate"));
					main.setFpFlag(rs.getString("fpFlag"));
					main.setFirmDescription(rs.getString("firmDescription"));
					main.setLat(rs.getString("lat"));
					main.setLongt(rs.getString("longt"));				
	
					gemh.setMain(main);
					rs.close();
					mainStatement.close();
					//read cpas
					
					cpaStatement = conn.createStatement();
					cpaRS = cpaStatement.executeQuery("select * from Cpa where mainId="+main.getDbId());
					List<Cpa> cpas=new ArrayList<Cpa>();
					while(cpaRS.next()){
						Cpa cpa=new Cpa(cpaRS.getString("cpaName"),cpaRS.getString("cpaCode"),cpaRS.getString("cpaKind"));
						cpas.add(cpa);
					}
					
					gemh.setCpas(cpas);
					cpaStatement.close();
					cpaRS.close();
					
					//get extra files
					
					restStatement = conn.createStatement();
					restRS = restStatement.executeQuery("select * from ExtraRest where mainId="+main.getDbId());
					List<Extras> extras =new ArrayList<Extras>();
				
					while(restRS.next()){
						ExtraRest extra=new ExtraRest();					
	
						extra.setGemhType(restRS.getString("gemhType"));
						extra.setNumber(restRS.getInt("number"));
						extra.setDateIssued(restRS.getString("dateIssued"));
						extra.setTitle(restRS.getString("title"));
						extra.setType(restRS.getString("type"));
						extra.setDocumentUrl(restRS.getString("documentUrl"));
						extra.setDateModified(restRS.getString("dateModified"));
						extra.setMainId(restRS.getInt("mainId"));
										
										
						extras.add(extra);
					}
					restRS.close();
					restStatement.close();
					
							
					recomStatement = conn.createStatement();
					recomRS = recomStatement.executeQuery("select * from ExtraRecommendatioData where mainId="+main.getDbId());
					while(recomRS.next()){
						ExtraRecomendationData extra=new ExtraRecomendationData();
						extra.setGemhType(recomRS.getString("gemhType"));
						extra.setNumber(recomRS.getInt("number"));
						extra.setDocumentUrl(recomRS.getString("documentUrl"));
						extra.setPublicationNumber(recomRS.getString("publicationNumber"));
						extra.setDateModified(recomRS.getString("dateModified"));
						extra.setMainId(recomRS.getInt("mainId"));
						extras.add(extra);
					}
					recomRS.close();
					recomStatement.close();
					
					
					legalStatement = conn.createStatement();
					legalRS = legalStatement.executeQuery("select * from ExtraLegalityControl where mainId="+main.getDbId());
					while(legalRS.next()){
						ExtraLegalityControl extra=new ExtraLegalityControl();
	
	
						extra.setGemhType(legalRS.getString("gemhType"));
						extra.setNumber(legalRS.getInt("number"));
						extra.setDateAnnouncement(legalRS.getString("dateAnnouncement"));
						extra.setTitle(legalRS.getString("title"));
						extra.setAuthorityControl(legalRS.getString("authorityControl"));
						extra.setProtocolNumber(legalRS.getString("protocolNumber"));
						extra.setDateSubmitted(legalRS.getString("dateSubmitted"));
						extra.setDocumentUrl(legalRS.getString("documentUrl"));
						extra.setDateModified(legalRS.getString("dateModified"));
						extra.setMainId(legalRS.getInt("mainId"));
	
						extras.add(extra);
					}
					legalRS.close();
					legalStatement.close();
							
					deciStatement = conn.createStatement();
					deciRS = deciStatement.executeQuery("select * from ExtraDecisionBodies where mainId="+main.getDbId());
					while(deciRS.next()){
						ExtraDecisionBodies extra = new ExtraDecisionBodies();
	
						extra.setGemhType(deciRS.getString("gemhType"));
						extra.setNumber(deciRS.getInt("number"));
						extra.setDateAnnouncement(deciRS.getString("dateAnnouncement"));
						extra.setType(deciRS.getString("type"));
						extra.setProtocolNumber(deciRS.getString("protocolNumber"));
						extra.setDateSubmitted(deciRS.getString("dateSubmitted"));
						extra.setDocumentUrl(deciRS.getString("documentUrl"));
						extra.setAbstract(deciRS.getString("abstract"));
						extra.setDateIssued(deciRS.getString("dateIssued"));
						extra.setDateModified(deciRS.getString("dateModified"));
						extra.setMainId(deciRS.getInt("mainId"));
		
						extras.add(extra);
					}
					deciRS.close();
					deciStatement.close();
					
					annoStatement = conn.createStatement();
					annoRS = annoStatement.executeQuery("select * from ExtraAnnouncements where mainId="+main.getDbId());
					while(annoRS.next()){
						ExtraAnnouncements extra=new ExtraAnnouncements();
						
						extra.setGemhType(annoRS.getString("gemhType"));
						extra.setNumber(annoRS.getInt("number"));
						extra.setDateAnnouncement(annoRS.getString("dateAnnouncement"));
						extra.setTitle(annoRS.getString("title"));
						extra.setDateIssued(annoRS.getString("dateIssued"));
						extra.setDocumentUrl(annoRS.getString("documentUrl"));
						extra.setDateSubmitted(annoRS.getString("dateSubmitted"));
						extra.setDateModified(annoRS.getString("dateModified"));
						extra.setMainId(annoRS.getInt("mainId"));	
						
						extras.add(extra);
					}
					annoRS.close();
					annoStatement.close();
					gemh.setExtras(extras);
					conn.close();
					return gemh;
				}else{
					conn.close();
					return null;
				}
			}catch (SQLException e1) {conn.close();
			// TODO Auto-generated catch block
			try{ annoStatement.close();}catch(Exception e){}
			try{ annoRS.close();}catch(Exception e){}			
			try{ deciRS.close();}catch(Exception e){}
			try{ deciStatement.close();}catch(Exception e){}			
			try{ legalRS.close();}catch(Exception e){}
			try{ legalStatement.close();}catch(Exception e){}
			try{ recomRS.close();}catch(Exception e){}
			try{ recomStatement.close();}catch(Exception e){}
			try{ restRS.close();}catch(Exception e){}
			try{ restStatement.close();}catch(Exception e){}
			try{ cpaRS.close();}catch(Exception e){}
			try{ cpaStatement.close();}catch(Exception e){}
			try{ rs.close();}catch(Exception e){}
			try{ mainStatement.close();}catch(Exception e){}
			try{ conn.close();}catch(Exception e){}
			
			e1.printStackTrace();
			throw e1;
		}finally{
			try{ annoStatement.close();}catch(Exception e){}
			try{ annoRS.close();}catch(Exception e){}			
			try{ deciRS.close();}catch(Exception e){}
			try{ deciStatement.close();}catch(Exception e){}			
			try{ legalRS.close();}catch(Exception e){}
			try{ legalStatement.close();}catch(Exception e){}
			try{ recomRS.close();}catch(Exception e){}
			try{ recomStatement.close();}catch(Exception e){}
			try{ restRS.close();}catch(Exception e){}
			try{ restStatement.close();}catch(Exception e){}
			try{ cpaRS.close();}catch(Exception e){}
			try{ cpaStatement.close();}catch(Exception e){}
			try{ rs.close();}catch(Exception e){}
			try{ mainStatement.close();}catch(Exception e){}
			try{ conn.close();}catch(Exception e){}
		}
		
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
			caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "phantomjs.exe");
			//caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "/usr/local/share/phantomjs-1.9.8-linux-x86_64/bin/phantomjs"); 
			
			ArrayList<String> cliArgsCap = new ArrayList<String>();
			cliArgsCap.add("--webdriver-loglevel=NONE");
			caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
			Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
			
			WebDriver driver = new PhantomJSDriver(caps);
	        
			return driver;
		}
		
		
		
		/**
	     * Export to a file the current page number.
	     * 
	     * @param String the page number
	     */
		public static void writeMetadata(String fileName, String query) {
			
			try {
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName + ".csv", true)));
			    out.println(query);
			    out.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
			
		}

}
