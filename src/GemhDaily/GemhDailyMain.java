package GemhDaily;


import org.openqa.selenium.WebDriver;



/**
 * @author A. Tzanis
 */

public class GemhDailyMain {
	
	/** 
	 *  Daily search of the gemh site for new entered companies. 
	 *  For each company take details and decisions related to this companies
	 */
    @SuppressWarnings({ "static-access" })
	public static void main(String[] args) throws Exception{
    	
    	
    	HelperMethods hm = new HelperMethods();
    	DailySearch search = new DailySearch();
    	
    	WebDriver driver = null;
    	
    	boolean run = true;
    	
    	String startGemh = null;
    	
    	int searchStop = Integer.parseInt(hm.readConfigurationsFile("GemhDaily", "searchStop"));
    	int searchCounter;
    	
    	while (run){
    		
    		//hm.sleepMode(0);							// Enter in sleep mode until next next day at 00:01:00
    		
    		boolean driversOk = false;
        	
    		// wait until driver is initialization complete and driver is ready for use
    		while(!driversOk){
        		try{
					driver = hm.initializeDriver();
					driversOk = true;
				}
				catch (Exception e){
					driversOk = false;
					System.out.println("Something goes wrong with the driver initialization!");
				}
	        }
	        
    		if (driversOk){
    			System.out.println("Driver is ready for use!");
    		}
    		
        	java.sql.Date dt1 = new java.sql.Date(System.currentTimeMillis());				//Get the current date in format 26-11-2015
    		String date = dt1.toString();
    		  
    		//Initialize startGemh and start searchCounter from the last valid gemhNumber
    		startGemh = hm.getlastValidGemh("lastValid");
    		searchCounter = Integer.parseInt(startGemh.substring(5))+1; 					//Increase last valid by one, to start searching the next gemh number
    		startGemh = startGemh.substring(0, 5);
    		
    		String startTime = hm.currentTime();
    		
    		int foundCompanies = 0;
    		
    		int notFoundCounter = 0;
    		
    		boolean stop = false;
    		
	    	while (!stop){	
    		
	    		while(searchCounter < searchStop && notFoundCounter < 4){
    				
	    			int insideCounter = 0;
	    			boolean found = false;
	        		String gemh = startGemh+hm.getValidNumbers(searchCounter, 2);						//14061 00
	    			
	    			while (!found && insideCounter<100)	{
	    				
		    			String gemhNumber = gemh+hm.getValidNumbers(insideCounter, 2);					//14061 00 00
	    				gemhNumber = gemhNumber+"000";													//14061 00 00 000
	    				
	    				try {
	    					
				    		System.out.println("Searching new company whith gemh number :" +gemhNumber);
				    		found = search.Search(gemhNumber, driver);
	    					
				    		if (found){
								foundCompanies++;
								hm.writeMetadata("lastValid", Integer.parseInt(gemhNumber.substring(0, 7)));
								notFoundCounter = 0;													//If found zero the notFoundCounter					
							}
				    		
						} 
						catch (Exception e) {
							e.printStackTrace();
						}
		    				
			    		insideCounter++;					//Increase insideCounter by one
			    	}
	    			
	    			if (!found){
						notFoundCounter++;					//If not found increase notFoundCounter by one
					}
	    			
		    		searchCounter++;						//Increase searchCounter by one
		    		
		    	}
    			
	    		if (notFoundCounter == 4){
	    			stop = true;
	    		}
	    		
	    		startGemh = hm.getStartGemhNumber(startGemh);
	    		searchCounter = Integer.parseInt(hm.readConfigurationsFile("GemhDaily", "searchStart"));
	    		
	    	}
    		
    		//Case there are not results for the company
    		if (foundCompanies == 0){
    			System.out.println("No companies found");
    			hm.writeUnknownMetadata("DailyExecutionExport", date +", No companies found.. , Searching duration was " +hm.searchDuration(startTime));
    		}
    		else{
    			System.out.println("Number of new companies: " +foundCompanies);
    			hm.writeUnknownMetadata("DailyExecutionExport", date +", Number of new companies: " +foundCompanies+ ", Searching duration was " +hm.searchDuration(startTime));
    		}
    		
    		driver.close();						//Close driverDetails
    		driver.quit();						//Quit driverDetails
    		
    		hm.sleepMode(0);					// Enter in sleep mode until next next day at 00:01:00
    		
    	}
    }
 }
