package GemhDaily;

import java.io.IOException;
import java.net.MalformedURLException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class LatLong {
	
	HelperMethods hm = new HelperMethods();
	
	public LatLongInfo getLatLog(WebDriver driver, String address) throws MalformedURLException, IOException, InterruptedException { 
	
		driver.get("http://www.latlong.net/");
		
		boolean found = false;
		
		(new WebDriverWait(driver, 5))
		.until(ExpectedConditions.presenceOfElementLocated(By.id("gadres"))); 		// waits until page is loaded, max wait time 10 secs
		
		WebElement inputForm = driver.findElement(By.id("gadres"));
		
		inputForm.sendKeys(address);
		inputForm.submit();
		
		try{
			(new WebDriverWait(driver, 5))
			.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//html/body/main/div[2]/div/div/div[1]/div[4]/div[4]/div/div[2]/div"))).getText(); 		// waits until page is loaded, max wait time 10 secs
					
			String Coordinate = driver.findElement(By.xpath("//html/body/main/div[2]/div/div/div[1]/div[4]/div[4]/div/div[2]/div")).getText();
			
			if (Coordinate.equalsIgnoreCase("\\(0,0\\)")){
				found = false;
				return new LatLongInfo(null,null,false);
			}
			else{
				found = true;
				String latitude = Coordinate.split(",")[0].replaceAll("\\(", "").replaceAll(" ", "");
				String longitude = Coordinate.split(",")[1].replaceAll("\\)", "").replaceAll(" ", "");
				return new LatLongInfo(latitude,longitude,found);
			}
		}
		catch (Exception e){
			System.out.println("Coordinates can not be found!!");
			return new LatLongInfo(null,null,false);
		}
		
	}
	
}