package GemhDaily;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import elod.harvest.gemh.daily.GemhDaily;
import elod.harvest.gemh.daily.gemhDatas.ExtraAnnouncements;
import elod.harvest.gemh.daily.gemhDatas.ExtraDecisionBodies;
import elod.harvest.gemh.daily.gemhDatas.ExtraLegalityControl;
import elod.harvest.gemh.daily.gemhDatas.ExtraRecomendationData;
import elod.harvest.gemh.daily.gemhDatas.ExtraRest;
import elod.harvest.gemh.daily.gemhDatas.Extras;

/**
 * @author A. Tzanis
 */

public class RelatedFiles {
	
	HelperMethods hm = new HelperMethods();
	Configurations pc = new Configurations();
	
	@SuppressWarnings({ "static-access"})
	public RelatedFiles(WebDriver driverDetails, GemhDaily gd, String resultsLink, String vatId, String gemh, String companyType, String gemiStartDate, String start, int mainId, BufferedWriter bufferedWriter) throws IOException, InterruptedException, SQLException {
			
		String scrappingDate ;							//Current date
		String subject = null;							//Subject of the dicision
		String decisionDate = null;						//Original release date of the dicision
		String publDate = null;							//Publication date of the dicision
		String finalDate = null; 
		String pdfLink = null;							//PDF download link url
		String decisionCode = null;						//Decision code of the second table
		String bodyType = null;
		int rowCount = 0;
		int decisionId = 0;
		
		java.sql.Date dt2 = new java.sql.Date(System.currentTimeMillis());
		scrappingDate = dt2.toString();
		
		//In case we have not gemh date we use the current date for the pdf file name
		if(start.equalsIgnoreCase("") || start.isEmpty()){
			start = scrappingDate;
		}
		
		driverDetails.get(resultsLink);										//Driver visit resultsLink
		
		/** in server mode check if driver is working **/
		if (!pc.local){
			hm.driverCheck(driverDetails);
		}
		
		(new WebDriverWait(driverDetails, 30))
		.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
						+ "/div/div/div/div[2]/div[1]/div[1]"))); 		// waits until page is loaded, max wait time 10 secs
		
		driverDetails.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);
		
		int tableCount = driverDetails.findElements(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div/div[2]/div")).size()-2;			// Finds the count of relates to the company FEK
		System.out.println("Number of Arrays:" +tableCount);
        
		 
        if (tableCount > 0){
        	
        	//A list with all the extra files, of any kind
            List<Extras> extras=new ArrayList<Extras>();
           
        	for (int i=0; i<tableCount; i++){
	       
        		String arrayTitle = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
        											+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[1]")).getText();
		        
        		if (arrayTitle.equalsIgnoreCase("Αρχεία Ανακοίνωσης")){
		        	
        			ExtraAnnouncements announcements =new ExtraAnnouncements();

        			String archiveCategory = "Announcement_Archives";
        			
        			System.out.println("--------------- Archives Announcement ---------------\n");
		        	
		        	rowCount = driverDetails.findElements(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
		        							+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr")).size();					// Finds the count of related to the company announcement
		            System.out.println("Number of relevant documents:" +rowCount+ "\n");
		           
		            bufferedWriter.write("Αρχεία Ανακοίνωσης");																	//Writes the type of information
		        	bufferedWriter.newLine();
	        		
		        	for (int j=0; j<rowCount; j++){																			// informations of the first table "Αρχεία Ανακοίνωσης"
			        	
			        	decisionId = rowCount-j;																			//Decision id, latest decision comes first so the first row has the latest decision
			        	
			        	decisionDate = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
			        					+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+j)+ "]/td[1]")).getText();			//Variable to store the original release date of the FEK
			        	decisionDate = hm.dateConverter(decisionDate);
			        		
			        	subject = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
			        			+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+j)+ "]/td[2]")).getText();					//Variable to store the subject of the FEK
			        		
			        	publDate  = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
			    				+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+j)+ "]/td[3]")).getText();		
			        	publDate = hm.dateConverter(publDate);
			        		
			        	finalDate  = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
			    				+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+j)+ "]/td[5]")).getText();		
			        	finalDate = hm.dateConverter(finalDate);
			        		        		
			        	WebElement element = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]"
			        						+ "/div[2]/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+j)+ "]/td[4]/a"));			// Find the pdf download button element
			        	pdfLink = element.getAttribute("href").toString();																// Stores the url of the download link
			        	
			        	    	   
			        	System.out.println("--------------- " +(j+1)+ " ---------------");
			        	System.out.println(decisionId+ "\n" +decisionDate + "\n" +subject+ "\n" +publDate+ "\n" +pdfLink+ "\n" +finalDate+ "\n");
		    	   
			        	/** addExtraAnnouncements **/
			        	announcements.setGemhType("Αρχεία Ανακοίνωσης");
			        	announcements.setNumber(decisionId);
			        	announcements.setDateAnnouncement(decisionDate);
			        	announcements.setTitle(subject);
			        	announcements.setDateIssued(publDate);
			        	announcements.setDocumentUrl(pdfLink);
			        	announcements.setDateSubmitted(finalDate);
			        	announcements.setDateModified(scrappingDate);
			        	
			        	bufferedWriter.write(decisionId+ "~" +decisionDate+ "~" +subject+ "~" +publDate+ "~" +pdfLink+ "~" +finalDate+ "~" +scrappingDate);		//Writes the results to the file
			        	bufferedWriter.newLine();
			        
			        	// write the data for pdf downloader
			        	hm.writeUnknownMetadata("Pdf_"+start, vatId +"~"+ gemh +"~"+ companyType +"~"+ archiveCategory +"~"+ subject +"~"+
					                        	decisionDate +"~"+ decisionId +"~"+ pdfLink);		
		        	
			        	extras.add(announcements);					//Add announcements to extras
		        	}
		        }
	        	
        		else if (arrayTitle.equalsIgnoreCase("Δεδομένα Σύστασης από ΥΜΣ")){
	        		
        			ExtraRecomendationData recommendationData = new ExtraRecomendationData();
        			
        			String archiveCategory = "Recommendation_Data";
        			
	        		System.out.println("--------------- Recommendation data ---------------\n");
	            	
	        		rowCount = driverDetails.findElements(By.xpath("//html/body/div[1]/div/div/div"
	        						+ "/div/div/div/div[2]/div[2]/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr")).size();		// Finds the count of relates to the company FEK
	        		System.out.println("Number of relevant documents:" +rowCount+ "\n");
			           
		            bufferedWriter.write("Δεδομένα Σύστασης");																	//Writes the type of information
		        	bufferedWriter.newLine();
	        		
	                for (int k=0; k<rowCount; k++){																					// informations of the first table "Αρχεία Ανακοίνωσης"
	    	        	
	    	        	decisionId = rowCount-k;																					//Decision id, latest decision comes first so the first row has the latest decision
	    	        	
	    	        	pdfLink = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
	    	        									+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+k)+ "]/td[1]")).getText();
	    	        	
	    	        	//Κωδικός Αριθμός Δημοσίευσης	140965454000
	    	        	decisionCode = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
	    	        									+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+k)+ "]/td[2]")).getText();
	                    
	    	        	System.out.println("--------------- " +(k+1)+ " ---------------");
	    	        	System.out.println(decisionId+ "\n" +pdfLink + "\n" +decisionCode+ "\n");
	        	   
	    	        	bufferedWriter.write(decisionId+ "~" +pdfLink+ "~" +decisionCode+ "~" +scrappingDate);										//Writes the results to the file
	    	        	bufferedWriter.newLine();
	    	        	
	    	        	/** addExtraRecommendationData **/
	    	        	recommendationData.setGemhType("Δεδομένα Σύστασης από ΥΜΣ");
	    	        	recommendationData.setNumber(decisionId);
	    	        	recommendationData.setDocumentUrl(pdfLink);
	    	        	recommendationData.setPublicationNumber(decisionCode);
	    	        	recommendationData.setDateModified(scrappingDate);
	    	        	
	    	        	
	    	        	// write the data for pdf downloader
			        	hm.writeUnknownMetadata("Pdf_"+start, vatId +"~"+ gemh +"~"+ companyType +"~"+ archiveCategory +"~"+ gemiStartDate.replaceAll("-", "_") +"~"+ decisionId +"~"+ pdfLink);		
	                
			        	extras.add(recommendationData);					//Add recommendationData to extras
	                }
	            }
	        	
        		else if (arrayTitle.equalsIgnoreCase("Αρχεία Αποφάσεων Οργάνων")){
	        		
        			ExtraDecisionBodies decisionBodies = new ExtraDecisionBodies();
        			
        			String archiveCategory = "Records_of_Decisions_organs";
        			
	        		System.out.println("--------------- Records of Decisions organs ---------------\n");
	            	
	        		rowCount = driverDetails.findElements(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]"
	        						+ "/div[2]/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr")).size();		// Finds the count of relates to the company FEK
	        		System.out.println("Number of relevant documents:" +rowCount+ "\n");
			           
		            bufferedWriter.write("Αρχεία Αποφάσεων Οργάνων");																	//Writes the type of information
		        	bufferedWriter.newLine();
	        		
	                for (int l=0; l<rowCount; l++){																		// informations of the first table "Αρχεία Ανακοίνωσης"
	                	
	                	decisionId = rowCount-l;																					//Decision id, latest decision comes first so the first row has the latest decision
	    	        	
	                	decisionDate = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
	                					+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+l)+ "]/td[1]")).getText();
	                	decisionDate = hm.dateConverter(decisionDate);
			        	
	                	bodyType = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
            					+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+l)+ "]/td[2]")).getText();
            	
	                	decisionCode = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
            					+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+l)+ "]/td[3]")).getText();
            	
	                	finalDate = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
            					+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+l)+ "]/td[4]")).getText();
	                	finalDate = hm.dateConverter(finalDate);
	        	
	                	WebElement element = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]"
        						+ "/div[2]/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+l)+ "]/td[5]/a"));			// Find the pdf download button element
	                	pdfLink = element.getAttribute("href").toString();																// Stores the url of the download link
        	
	                	subject = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
            					+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+l)+ "]/td[6]")).getText();
            	
	                	publDate = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
            					+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+l)+ "]/td[8]")).getText();
	                	publDate = hm.dateConverter(publDate);
	        	
	                	System.out.println("--------------- " +(l+1)+ " ---------------");
	    	        	System.out.println(decisionId+ "\n" +decisionDate + "\n" +bodyType+ "\n" +decisionCode + "\n" +finalDate+ "\n" +pdfLink+
	    	        			"\n" +subject+ "\n" +publDate+ "\n");
	        	   
	    	        	bufferedWriter.write(decisionId+ "~" +decisionDate + "~" +bodyType+ "~" +decisionCode + "~" +finalDate+ "~" +pdfLink+
	    	        			"~" +publDate+ "~" +subject+ "~" +scrappingDate);											//Writes the results to the file
	    	        	bufferedWriter.newLine();

	    	        	/** addExtraDecisionBodies **/
	    	        	decisionBodies.setGemhType("Αρχεία Αποφάσεων Οργάνων");
	    	        	decisionBodies.setNumber(decisionId);
	    	        	decisionBodies.setDateAnnouncement(decisionDate);
	    	        	decisionBodies.setType(bodyType);
	    	        	decisionBodies.setProtocolNumber(decisionCode);
	    	        	decisionBodies.setDateSubmitted(finalDate);
	    	        	decisionBodies.setDocumentUrl(pdfLink);
	    	        	decisionBodies.setAbstract(subject);
	    	        	decisionBodies.setDateIssued(publDate);
	    	        	decisionBodies.setDateModified(scrappingDate);
	    	        
	    	        	// write the data for pdf downloader
			        	hm.writeUnknownMetadata("Pdf_"+start, vatId +"~"+ gemh +"~"+ companyType +"~"+ archiveCategory +"~"+ bodyType +"~"+
			        			decisionCode +"~"+ decisionDate +"~"+ pdfLink);		
		        	
			        	extras.add(decisionBodies);					//Add recommendationData to extras
	                }
	            }
	        	
        		else if (arrayTitle.equalsIgnoreCase("Λοιπά Αρχεία")){
	        		
        			ExtraRest rest = new ExtraRest();
        			
        			String archiveCategory = "Other_Files";
        			
	        		System.out.println("--------------- Other Files ---------------\n");
	            	
	        		rowCount = driverDetails.findElements(By.xpath("//html/body/div[1]/div/div/div"
	        						+ "/div/div/div/div[2]/div[2]/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr")).size();		// Finds the count of relates to the company FEK
	        		System.out.println("Number of relevant documents:" +rowCount+ "\n");
			           
		            bufferedWriter.write("Λοιπά Αρχεία");																	//Writes the type of information
		        	bufferedWriter.newLine();
	        		
	                for (int z=0; z<rowCount; z++){																					// informations of the first table "Αρχεία Ανακοίνωσης"
	    	        	
	    	        	decisionId = rowCount-z;																					//Decision id, latest decision comes first so the first row has the latest decision
	    	        	
	    	        	decisionDate = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
	    	        									+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+z)+ "]/td[1]")).getText();
	    	        	decisionDate = hm.dateConverter(decisionDate);
			        	
	    	        	subject = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
								+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+z)+ "]/td[2]")).getText();
		                
	    	        	bodyType = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
								+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+z)+ "]/td[3]")).getText();
		                
	    	        	WebElement element = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
															+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+z)+ "]/td[4]/a"));
		                pdfLink = element.getAttribute("href").toString();																// Stores the url of the download link
        	
	    	        	System.out.println("--------------- " +(z+1)+ " ---------------");
	    	        	System.out.println(decisionId+ "\n" +decisionDate + "\n" +subject+ "\n" +bodyType+ "\n" +pdfLink+ "\n");
	            		
	    	        	bufferedWriter.write(decisionId+ "~" +decisionDate + "~" +subject+ "~" +bodyType + "~" +pdfLink+ "~" +scrappingDate);										//Writes the results to the file
	    	        	bufferedWriter.newLine();

	    	        	/** addExtraRest **/
	    	        	rest.setGemhType("Λοιπά Αρχεία");
	    	        	rest.setNumber(decisionId);
	    	        	rest.setDateIssued(decisionDate);
	    	        	rest.setTitle(subject);
	    	        	rest.setType(bodyType);
	    	        	rest.setDocumentUrl(pdfLink);
	    	        	rest.setDateModified(scrappingDate);
	    	        	
	    	        	// write the data for pdf downloader
			        	hm.writeUnknownMetadata("Pdf_"+start, vatId +"~"+ gemh +"~"+ companyType +"~"+ archiveCategory +"~"+ subject +"~"+
					                        	decisionDate +"~"+ pdfLink);		
		        	
			        	extras.add(rest);					//Add recommendationData to extras
	                }
	            }
	        	
        		else if (arrayTitle.equalsIgnoreCase("Αρχεία Αποφάσεων Αρχών Ελέγχου Νομιμότητας")){
	        		
        			ExtraLegalityControl legalityControl = new ExtraLegalityControl();
        			
        			String archiveCategory = "Records_of_Decisions_Legality_Control_Authorities";
        			
	        		System.out.println("--------------- Records of Decisions Legality Control Authorities ---------------\n");
	            	
	        		rowCount = driverDetails.findElements(By.xpath("//html/body/div[1]/div/div/div"
	        						+ "/div/div/div/div[2]/div[2]/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr")).size();		// Finds the count of relates to the company FEK
	        		System.out.println("Number of relevant documents:" +rowCount+ "\n");
			           
		            bufferedWriter.write("Αρχεία Αποφάσεων Αρχών Ελέγχου Νομιμότητας");																	//Writes the type of information
		        	bufferedWriter.newLine();
	        		
	                for (int n=0; n<rowCount; n++){																					// informations of the first table "Αρχεία Ανακοίνωσης"
	    	        	
	    	        	decisionId = rowCount-n;																					//Decision id, latest decision comes first so the first row has the latest decision
	    	        	
	    	        	decisionDate = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
	    	        									+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+n)+ "]/td[1]")).getText();
	    	        	decisionDate = hm.dateConverter(decisionDate);
			        	
	    	        	subject = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
								+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+n)+ "]/td[2]")).getText();
		                
	    	        	bodyType = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
								+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+n)+ "]/td[3]")).getText();
		                
	    	        	decisionCode = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
								+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+n)+ "]/td[4]")).getText();
		                
	    	        	finalDate = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
								+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+n)+ "]/td[5]")).getText();
	    	        	finalDate = hm.dateConverter(finalDate);
	    	        	
	    	        	WebElement element = driverDetails.findElement(By.xpath("//html/body/div[1]/div/div/div/div/div/div/div[2]/div[2]"
															+ "/div/div/div/div[2]/div[" +(3+i)+ "]/div[2]/table/tbody/tr[" +(1+n)+ "]/td[6]/a"));
		                pdfLink = element.getAttribute("href").toString();																// Stores the url of the download link
        	
	    	        	System.out.println("--------------- " +(n+1)+ " ---------------");
	    	        	System.out.println(decisionId+ "\n" +decisionDate + "\n" +subject+ "\n" +bodyType+ "\n" +decisionCode+ "\n" +finalDate+ "\n" +pdfLink+ "\n");
	            		
	    	        	bufferedWriter.write(decisionId+ "~" +decisionDate + "~" +subject+ "~" +bodyType + "~" +decisionCode+ "~" +finalDate+ "~" +pdfLink+ "~" +scrappingDate);										//Writes the results to the file
	    	        	bufferedWriter.newLine();

	    	        	/** addExtraLegalityControl **/
	    	        	legalityControl.setGemhType("Αρχεία Αποφάσεων Αρχών Ελέγχου Νομιμότητας");
	    	        	legalityControl.setNumber(decisionId);
	    	        	legalityControl.setDateAnnouncement(decisionDate);
	    	        	legalityControl.setTitle(subject);
	    	        	legalityControl.setAuthorityControl(bodyType);
	    	        	legalityControl.setProtocolNumber(decisionCode);
	    	        	legalityControl.setDateSubmitted(finalDate);
	    	        	legalityControl.setDocumentUrl(pdfLink);
	    	        	legalityControl.setDateModified(scrappingDate);
	    	        	
	    	        	// write the data for pdf downloader
			        	hm.writeUnknownMetadata("Pdf_"+start, vatId +"~"+ gemh +"~"+ companyType +"~"+ archiveCategory +"~"+ subject +"~"+
					                        	decisionDate +"~"+ pdfLink);		
			        
			        	extras.add(legalityControl);					//Add recommendationData to extras
	                }
	            }
	        }
        	//store those extras on DB
            gd.storeExtras(extras, mainId);
        }
    }
}