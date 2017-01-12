package GemhDailyDb;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;

import GemhDailyDb.HelperMethods;
import GemhDailyDb.LatLong;
import GemhDailyDb.LatLongInfo;
import elod.harvest.gemh.daily.GemhDaily;
import elod.harvest.gemh.daily.gemhDatas.ColumnNames;
import elod.harvest.gemh.daily.gemhDatas.Main;
import elod.harvest.gemh.daily.gemhDatas.Update;

public class GemhDailyDbMain {

	public static void main(String[] args) throws SQLException, InterruptedException, MalformedURLException, IOException {

		/** dailyConnection **/
    	GemhDaily gd=new GemhDaily()
                .setDbAddress("jdbc:mysql://83.212.86.155/GemhDailyAll?useUnicode=true&characterEncoding=UTF-8")
                .setDbUser("gemh")
                .setDbPassword("gemh@69");
    	
    	List<Integer> records=new ArrayList<Integer>();
    	records = HelperMethods.getNullLongLat();
    	//records = gd.getNullVats();
    	System.out.println(records.get(0).toString());
    	
    	//WebDriver drive = HelperMethods.initializeDriver();
    	
    	for(int i=0; i<records.size(); i++){
	    	
    		Main main = new Main();
	    	main = gd.getMainData(records.get(i));
	    	
	    	if (main.getStreet() == null && main.getLocality() == null && main.getPostalCode() == null){
	    		continue;
	    	}
	    	
	    	String query = main.getStreet()+","+main.getLocality()+","+main.getPostalCode()+",GR";
	    	System.out.println(query);
	    	HelperMethods.writeMetadata("nullLongLat", query);
	    	
	    	/*LatLong ll = new LatLong();
	    	LatLongInfo lli = ll.getLatLog(drive,query);
	    	
	    	if(lli.getFound()){
	    		
	    		String latitude = lli.getLatitude();
	    		String longitude = lli.getLongitude();
	    		System.out.println(latitude+","+longitude);
	    	
	    		Update up =new Update();
	            up.addItem(new ColumnNames().lat(), "latitude");
	            up.addItem(new ColumnNames().longt(), "longitude");
	            gd.update(up,records.get(i));
	    	}*/
	    	
    	}	
    	
	    //drive.close();						//Close driverDetails
		//drive.quit();						//Quit driverDetails
		

	}

}
