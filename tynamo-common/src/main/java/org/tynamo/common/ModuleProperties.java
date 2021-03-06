package org.tynamo.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class ModuleProperties {
	public static final String PROPERTYFILE = "module.properties";
	public static final String VERSION = "module.version";

	public static String getVersion(Class<?> aClass) {
		String expectedPropertyPath = aClass.getPackage().getName() + "/" + PROPERTYFILE;
		String version = null;
		InputStream inputStream = aClass.getResourceAsStream(PROPERTYFILE);
		if (inputStream == null) {
			version = aClass.getPackage().getImplementationVersion();
			if (aClass.getResource("").toString().startsWith("file:") && "false".equalsIgnoreCase(System.getProperty("tapestry.production-mode"))) version = "development-SNAPSHOT"; 
			if (version == null) throw new IllegalArgumentException("Neither properties file '" + expectedPropertyPath + "' nor '" + aClass.getPackage().getName() + "' package-specific Implementation-Version in META-INF/manifest.mf was found");
		}
		else version = getPropertyValue(aClass, VERSION);
		if (version.endsWith("SNAPSHOT")) {
			long timestamp = System.currentTimeMillis();
			try {
				// adapted from http://stackoverflow.com/questions/2057351/how-do-i-get-the-last-modification-time-of-a-java-resource
				URL url = aClass.getResource("");
				URLConnection connection = url.openConnection();
				timestamp = connection.getLastModified();
				connection.getInputStream().close();
			} catch (Throwable t) {//ignore
			}
			version += "-" + timestamp;
		}
		return version;
	}
	
	public static String getPropertyValue(Class<?> aClass, String propertyName) {
		String expectedPropertyPath = aClass.getPackage().getName() + "/" + PROPERTYFILE;
		Properties moduleProperties = new Properties();
		String propertyValue = null;
		InputStream inputStream = aClass.getResourceAsStream(PROPERTYFILE);
		if (inputStream == null) throw new IllegalArgumentException("No property file resource found from " + expectedPropertyPath);
		else try {
			moduleProperties.load(inputStream);
			propertyValue = moduleProperties.getProperty(propertyName);
			if (propertyValue == null) throw new IllegalArgumentException(VERSION + " was not found from " + expectedPropertyPath);
			if (propertyValue.startsWith("${")) throw new IllegalArgumentException(VERSION + " is not filtered in resource " + expectedPropertyPath);
		} catch (IOException e) {
			throw new IllegalArgumentException("Couldn't read property file resource from " + expectedPropertyPath, e);
		}
		return propertyValue;
		
	}
	

}
