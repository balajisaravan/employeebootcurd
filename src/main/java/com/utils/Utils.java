package com.utils;
import org.springframework.stereotype.Component;

@Component
public class Utils {

	public String generateRandomNumber() {
		int randomNumber = (int)(Math.random()*99999)+10000;
		String number = String.valueOf(randomNumber);

		return number;
	}
	
}
