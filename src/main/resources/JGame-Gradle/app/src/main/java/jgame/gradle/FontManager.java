package jgame.gradle;

import java.awt.*;
import java.io.*;

public final class FontManager {

    private static FontManager instance;
    


    private FontManager() {
      
      	String[] tiposDeLetras={"CallOfOpsDutyIi-7Bgm4.ttf","SuperPixel-m2L8j.ttf","SnesItalic-1G9Be.ttf","PixelEmulator-xq08.ttf","PixelGamingRegular-d9w0g.ttf"};
    	
    	try{
       		
       		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        
	        for(String ttf: tiposDeLetras){
	            
	            InputStream is =FontManager.class.getClassLoader().getResourceAsStream("fonts/"+ttf);
	            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
	            ge.registerFont(font);  
	        }

        

    	}catch(Exception e){
        	System.out.println("ZAS! FontManager");
        	e.printStackTrace();
    	}

    }

    public static FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }
        return instance;
    }

    public static void loadTTF(String ttf){
    	try{
    	
    		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    		InputStream is = FontManager.class.getClassLoader().getResourceAsStream("fonts/"+ttf);
	        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
	        ge.registerFont(font);  
    	
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
}