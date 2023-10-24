package de.checkpoint.utils;

public class MaxMemory {

	   public static void main(String[] args) {
	        Runtime rt = Runtime.getRuntime();
	        	        
	        System.out.println ("gesamter Speicher:  " + ((rt.totalMemory() / 1024) / 1024) + " MB" + " (" + (rt.totalMemory() / 1024) +" kB)");
	        System.out.println ("freier Speicher:    " + ((rt.freeMemory()  / 1024) / 1024) + " MB" + " (" + (rt.freeMemory()  / 1024) +" kB)");
	        System.out.println ("genutzter Speicher: " + (((rt.totalMemory() - rt.freeMemory()) / 1024) / 1024) + " MB" + " (" + ((rt.totalMemory() - rt.freeMemory()) / 1024) +" kB)");
	        
	   }
}






