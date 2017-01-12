package GemhDaily;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

import elod.harvest.gemh.daily.GemhDaily;
import elod.harvest.gemh.daily.gemhDatas.Cpa;
import elod.harvest.gemh.daily.gemhDatas.Main;
import elod.tool.afm.search.CompanyInfo;
import elod.tool.afm.search.Kad;
import elod.tool.afm.search.NoDataReturned;
import elod.tool.afm.search.SearchAndSave;
import orgType.OrgTypeFinder;
import orgType.OrgTypeInfo;




/**
 * @author A. Tzanis
 */

@SuppressWarnings("unused")
public class DailySearch {

	HelperMethods hm = new HelperMethods();
	Configurations pc = new Configurations();
	
	@SuppressWarnings("static-access")
	public boolean Search(String gemhNumber, WebDriver driver) throws IOException, InterruptedException, SQLException, NoDataReturned{
		
		String filepath = null;
		boolean found = false;
		
		/** dailyConnection **/
    	GemhDaily gd=new GemhDaily()
                .setDbAddress("jdbc:mysql://83.212.86.155/gemhdaily?useUnicode=true&characterEncoding=UTF-8")
                .setDbUser("gemh")
                .setDbPassword("gemh@69");
    	
    	boolean exist = hm.gemhChecker(gemhNumber.substring(0, 7), gd);				//Check if gemh exists in database
    
    	if (exist){
    		System.out.println("Company already in the database!");
    		found = true;
    	}
    	else{
    		
	    	boolean dataReturned = false;
			
			String scrappingDate = null; 												//Current date
	    	String vatId = null;														//Company vatId
			String gemiStartDate = null;												//Gemi start date 
			String mainLink = pc.mainLink;												//Main Link
	    	String resultsLink = pc.resultsLink;										//Results Link
			String responsibleOffice = null;											//Responsible office of the gemi number
	    	String gemiStatus = null;													//Active or Disactive in gemi
	    	String companyFullName = null;												//Full company name
			String companyTitle = null;													//Company title
			String companyGemi = null;													//Company GEMI id
			String companyType = null;													//Company type given by gsis			
			String address = null;														//Company address given by gsis
			String city = null;															//Company city given by gsis
			String postCode = null;														//Company postCode given by gsis
			String doyName = null;														//Company doy name given by gsis
			String doyCode = null;														//Company doy code given by gsis
			String doyStartDate = null;													//Company doy start date given by gsis
			String doyEndDate = null;													//Company doy end date given by gsis
			String doyStatus = null;													//Company doy status given by gsis
			String individual = null;													//Individual or not given by gsis
			String doyCompanyType = null;												//Company type given by gsis
			List<Kad> kad = null;														//List of kad given by gsis
			String filePath = null;														//Output filepath
			String latitude = null;														//Company address latitude coordinates
			String longitude = null;													//Company address longitude coordinates
			BufferedWriter bufferedWriter = null;
			boolean valid = false;
			boolean exists = false;
			File dailyDirectory;
			String dailyPath;
			
			java.sql.Date dt1 = new java.sql.Date(System.currentTimeMillis());			//Get the current date in format 26-11-2015
			scrappingDate = dt1.toString();
			
			driver.get(mainLink);														//Driver visit mainLink
			
			/** in server mode check if firefox and Xvfb is working **/
			if (!pc.local){
				hm.driverCheck(driver);
				filepath = pc.filePathConfigurationServer;								// Gsis search configuration filepath
		    }
			else{
				filepath = pc.filePathConfigurationLocal;								// Gsis search configuration filepath
			}
			
			/** search by gemh **/
			WebElement gemh = (new WebDriverWait(driver, 20))
					  .until(ExpectedConditions.presenceOfElementLocated(By.id("gemiNumber")));		// waits until page is loaded, max wait time 10 secs
			
			WebElement element = driver.findElement(By.id("gemiNumber"));							// Find the text input element of AFM
			
			element.sendKeys(gemhNumber);															// Enter the gemh we want to search
			element.submit();																		// Now submit the form
			
			/** in server mode check if firefox and Xvfb is working **/
			if (!pc.local){
				hm.driverCheck(driver);
			}
				
			// wait for max 20 seconds until the page results are loaded
			(new WebDriverWait(driver, 20))
			.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
																			+ "/div/div[2]/div/div/div[2]/div/table/tbody/tr[1]/td[1]"))); 		// waits until page is loaded, max wait time 10 secs
				
			int results = driver.findElements(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div[2]/div/div/div[2]/div/table/tbody/tr")).size();
			
			String success = driver.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div[2]"
					+ "/div/div/div[2]/div/table/tbody/tr/td")).getText();			
	
			//If site has results
			if (!success.equalsIgnoreCase("Δεν υπάρχουν δεδομένα")){
				
				found = true;
				
				gemiStartDate = driver.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div[2]/div/div"
															+ "/div[2]/div/table/tbody/tr[1]/td[7]")).getText();
				gemiStartDate = hm.dateConverter(gemiStartDate);
						
				if (pc.local){
					if (!gemiStartDate.equalsIgnoreCase("")){
						dailyDirectory = hm.directoryCreator (pc.filePathLocal, gemiStartDate.replaceAll("-", "_"));					// Create AE_pdf directory
					}
					else{
						dailyDirectory = hm.directoryCreator (pc.filePathLocal, scrappingDate.replaceAll("-", "_"));					// Create AE_pdf directory
					}
					dailyDirectory = hm.directoryCreator (pc.filePathLocal, gemiStartDate.replaceAll("-", "_"));					// Create AE_pdf directory
					dailyPath = dailyDirectory.getPath().toString()+"/";
				}
				else{
					if (!gemiStartDate.equalsIgnoreCase("")){
						dailyDirectory = hm.directoryCreator (pc.filePathServer, gemiStartDate.replaceAll("-", "_"));					// Create AE_pdf directory
					}
					else{
						dailyDirectory = hm.directoryCreator (pc.filePathServer, scrappingDate.replaceAll("-", "_"));					// Create AE_pdf directory
					}
					dailyPath = dailyDirectory.getPath().toString()+"/";	
				}
					
				companyGemi = driver.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div[2]"
							+ "/div/div/div[2]/div/table/tbody/tr[1]/td[1]")).getText();			//Variable to store the company GEMI id
	
				//exists = hm.fileChecker(companyGemi, dailyPath);										// Check if file already exists
				//exists = hm.gemhChecker(companyGemi, gd);											//Check if gemh exists in the db
			
				// Initialize gsis search
				SearchAndSave sAs=new SearchAndSave();
				sAs.loadProp(filepath);
				CompanyInfo ci= new CompanyInfo();
				
				try{
						
					companyFullName = driver.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div[2]"
																+ "/div/div/div[2]/div/table/tbody/tr[1]/td[2]")).getText();			//Variable to store the company title
						
					companyTitle = driver.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div[2]"
												+ "/div/div/div[2]/div/table/tbody/tr[1]/td[3]")).getText();			//Variable to store the full company name
					vatId = driver.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div[2]"
											+ "/div/div/div[2]/div/table/tbody/tr[1]/td[4]")).getText();			//Variable to store the company vat id
					
					//If vatId is null
					if(vatId==null || vatId.isEmpty() || vatId.equalsIgnoreCase("")){
						vatId = "BID"+Integer.parseInt(hm.getlastValidGemh("lastBID"));
						hm.writeMetadata("lastBID", Integer.parseInt(hm.getlastValidGemh("lastBID"))+1);
					}
					
					responsibleOffice = driver.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div[2]"
													+ "/div/div/div[2]/div/table/tbody/tr[1]/td[5]")).getText();			//Variable to store the company title
					gemiStatus = driver.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div[2]"
													+ "/div/div/div[2]/div/table/tbody/tr[1]/td[6]")).getText();			//Variable to store the company condition
					
					if (!vatId.equalsIgnoreCase("000000000") && !vatId.equalsIgnoreCase("999999999") && (vatId.length()==9)){
							
						valid = sAs.check(vatId);										//Check if afm is valid
							
						//continues to next company if afm is not valid
						if (!valid){
							System.out.println("Μη έγκυρος αριθμός ΑΦΜ...");
							if (companyFullName.contains("...")){
								companyFullName = hm.fullNameFinder((resultsLink + companyGemi), driver);
							}
							OrgTypeFinder otf = new OrgTypeFinder();
							OrgTypeInfo oti = otf.orgType(companyFullName);
							
							if (oti.getFound()){
								companyType = oti.getAbbreviation();
							}
						}
						// Take gsis details if vatId is valid
						else{
									
							System.out.println("Searching details in gsis for vatId: " +vatId+ "\n");
							
							try {
									
								ci=sAs.searchAfm(vatId);			//Ask gsis for company details
								dataReturned = true;
								
							} 
							catch (NoDataReturned e) {
								dataReturned = false;				//If gsis has not details dataReturned = false
							}
							
							if (dataReturned){
								
								sAs.insertToDb(ci);					//Add company details to sql database
								
								companyFullName = hm.stringValidator(ci.getName());
								address = hm.stringValidator(ci.getAddress().split(", ")[0]);
								city = hm.stringValidator(ci.getCity());
								postCode = hm.stringValidator(ci.getPostCode());
								doyName = hm.stringValidator(ci.getDoy().getDoy());
								doyCode = hm.stringValidator(ci.getDoy().getCode());
								if (ci.getStatusDoy()){
									doyStatus = "ΕΝΕΡΓΟΣ ΑΦΜ";
								}
								else{
									doyStatus = "ΑΝΕΝΕΡΓΟΣ ΑΦΜ";
								}
								doyStartDate = hm.dateConverter1(ci.getStartDate());
								if (ci.getEndDate() != null){
									doyEndDate = hm.dateConverter1(ci.getEndDate());
								}
								if(ci.getIndividual()){
									individual = "Φυσικό Πρόσωπο";
								}
								else{
									individual = "Μη Φυσικό Πρόσωπο";
								}
								doyCompanyType = hm.stringValidator(ci.getType_Doy());
								if (ci.getFormat() != null){
									companyType = hm.stringValidator(ci.getFormat());
								}
								kad = ci.getKad();
								
								LatLong ll = new LatLong();
						    	LatLongInfo lli = ll.getLatLog(driver,address+","+city+","+postCode+",GR");
						    	
						    	if(lli.getFound()){
						    		latitude = lli.getLatitude();
						    		longitude = lli.getLongitude();
						    	}
							}
							//In case gsis has no info get the orgType by the company name.
							else {
								if (companyFullName.contains("...")){
									companyFullName = hm.fullNameFinder((resultsLink + companyGemi), driver);
								}
								OrgTypeFinder otf = new OrgTypeFinder();
								OrgTypeInfo oti = otf.orgType(companyFullName);
								
								if (oti.getFound()){
									companyType = oti.getAbbreviation();
								}
							}
						}
					}
					//In case vatId is not valid get the orgType by the company name.
					else {
						if (companyFullName.contains("...")){
							companyFullName = hm.fullNameFinder((resultsLink + companyGemi), driver);
						}
						OrgTypeFinder otf = new OrgTypeFinder();
						OrgTypeInfo oti = otf.orgType(companyFullName);
						
						if (oti.getFound()){
							companyType = oti.getAbbreviation();
						}
					}
					
					System.out.println("--------------- Company details ---------------");
					System.out.println(vatId+ "\n" +companyGemi+ "\n" +companyType+ "\n" +companyFullName+ "\n" +companyTitle+ "\n" +responsibleOffice+ "\n" 
							+gemiStatus+ "\n" +gemiStartDate+ "\n" +(resultsLink + companyGemi)+ "\n" +scrappingDate+ "\n" +valid+ "\n" +address+ "\n" +city+ "\n" +postCode+ "\n"
							+doyName+ "\n" +doyCode+ "\n" + doyStatus+ "\n" +doyStartDate+ "\n" +doyEndDate+ "\n" +individual+ "\n" +doyCompanyType+ "\n" +latitude+ "\n" +longitude+ "\n");
					
					Main main=new Main();
					
					/** mainData **/
			    	main.setVatId(vatId);
			    	main.setGemhnumber(companyGemi);
			    	main.setOrgType(companyType);
			    	main.setName(companyFullName);
			    	main.setBrandname(companyTitle);
			    	main.setChamber(responsibleOffice);
			    	main.setStatus(gemiStatus);
			    	main.setGemhdate(gemiStartDate);
				    main.setUrl(resultsLink + companyGemi);
				    main.setIssueddate(scrappingDate);
				    if(valid){
				    	main.setCorrectVat("true");
				    }
				    else{
				    	main.setCorrectVat("false");
				    }
				    main.setStreet(address);
				    main.setLocality(city);
				    main.setPostalCode(postCode);
				    main.setDoyName(doyName);
				    main.setDoyCode(doyCode);
				    main.setValidVat(doyStatus);
				    main.setRegistrationDate(doyStartDate);
				    main.setEndDate(doyEndDate);
				    main.setFpFlag(individual);
				    main.setFirmDescription(doyCompanyType);
				    main.setLat(latitude);
				    main.setLongt(longitude);
				    	
				    int mainId=gd.storeMain(main);
						
					if (dataReturned){
						
						List<Cpa> cpas = new ArrayList<Cpa>();
						
					   	for (int j=0; j<kad.size(); j++){
										
							String kadDescription = hm.kadConverter(kad.get(j).getDescription());
							String kadCode = kad.get(j).getCode();
							
							System.out.print(kadDescription+"/"+kadCode+"\n");
							
							/** addCpas **/
							Cpa cpa=new Cpa();
							cpa.setName(kadDescription);
							cpa.setcode(kadCode);
							if(j==0){
								cpa.setKind("Main");
							}
							else{
								cpa.setKind("Secondary");
							}
							cpas.add(cpa);
					    	
							if(gd.storeCpas(cpas, mainId)){
					            System.out.println("cpa store success!");
					        }else{
					        	System.out.println("cpa store failed!");
					        }
						}
					}
							
					/*if (pc.gemhDetails){
						try {
							new RelatedFiles(driver, gd, (resultsLink + companyGemi), vatId, companyGemi, companyType, gemiStartDate, gemiStartDate, mainId,  bufferedWriter); 														//Call RelatedFiles class
						} catch (IOException e) {
							e.printStackTrace();
						}
					}*/
						
					hm.writeUnknownMetadata("NewCompaniesScrappedAt_"+scrappingDate, filePath);
					hm.writeUnknownMetadata("NewGemhScrappedAt_"+scrappingDate, gemhNumber);
						
					filePath = dailyPath +companyGemi+ ".csv";									//Variable to store the output filepath
								
					try {
										
						bufferedWriter = new BufferedWriter(new FileWriter(filePath));			//Creation of bufferedWriter object
						
						bufferedWriter.write(vatId+ "~" +companyGemi+ "~" +companyType+ "~" +companyFullName+ "~" +companyTitle+ "~" +responsibleOffice+ "~" 
								+gemiStatus+ "~" +gemiStartDate+ "~" +(resultsLink + companyGemi)+ "~" +scrappingDate+ "~" +valid+ "~" +address+ "~" +city+ "~" +postCode+ "~"
								+doyName+ "~" +doyCode+ "~" + doyStatus+ "~" +doyStartDate+ "~" +doyEndDate+ "~" +individual+ "~" +doyCompanyType+ "~" +latitude+ "~" +longitude);
						
						bufferedWriter.newLine();
										
						if (dataReturned){
										
							for (int j=0; j<kad.size(); j++){
								
								String kadDescription = hm.kadConverter(kad.get(j).getDescription());
								String kadCode = kad.get(j).getCode();
										
								if (j<kad.size()-1){
									bufferedWriter.write(kadDescription+"/"+kadCode+"~");
								}
								else {
									bufferedWriter.write(kadDescription+"/"+kadCode);
								}
											
							}
						}
									
						bufferedWriter.newLine();
										
						hm.writeUnknownMetadata("GemhVatIdList", vatId);
										
						if (pc.gemhDetails){
							try {
								new RelatedFiles(driver, gd, (resultsLink + companyGemi), vatId, companyGemi, companyType, gemiStartDate, gemiStartDate, mainId,  bufferedWriter); 														//Call RelatedFiles class
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
							
						//hm.writeUnknownMetadata("NewCompaniesScrappedAt_"+scrappingDate, filePath);
						//hm.writeUnknownMetadata("NewGemhsScrappedAt_"+scrappingDate, companyGemi);
							
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
					finally{												//Close the BufferedWriter
						try {				
							if (bufferedWriter != null) {
								bufferedWriter.flush();
								bufferedWriter.close();
							}
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
									
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
    	
		return found;														//returns true if gemh exists and false if not
	    
	}
}