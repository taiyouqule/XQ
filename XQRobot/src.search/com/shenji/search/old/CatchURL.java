package com.shenji.search.old;
//爬虫暂时不用，该类废弃

/*package com.shenji.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;

import com.shenji.log.WriteLog;

public class CatchURL {
	
	private static String[] readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        String[] result = new String[3];
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 0;
            // һ�ζ���һ�У�ֱ������nullΪ�ļ�����
            while ((tempString = reader.readLine()) != null) {
                if(tempString.length()<11)
                	result[line++] = null;
                else
                	result[line++] = tempString.substring(5, tempString.length()-6);
            }
            reader.close();
            while(line<3)
            	result[line++] = null;
        } catch (Exception e) {
        	WriteLog writeLog = new WriteLog();
        	writeLog.Write(e.getMessage(),CatchURL.class.getName());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e1) {
                	WriteLog writeLog = new WriteLog();
                	writeLog.Write(e1.getMessage(),CatchURL.class.getName());
                }
            }
        }
        return result;
    }

	public static String[] GetURL(String key) {
		ScraperConfiguration config;
		Scraper scraper;
		try
		{
			config = new ScraperConfiguration("D:\\Program Files\\tomcat\\webapps\\axis2\\WEB-INF\\classes\\baidu.xml");//�����ļ���·��
			scraper = new Scraper(config, "D:\\Program Files\\tomcat\\webapps\\axis2\\WEB-INF\\classes");//������Ŀ¼
			scraper.addVariableToContext("url","http://www.baidu.com/s?wd="+key+"&rn=3");
			scraper.addVariableToContext("filePath","catalog.xml");
			scraper.setDebug(false);
			scraper.execute();//����Ŀ�ʼִ��
			
			return readFileByLines("D:\\Program Files\\tomcat\\webapps\\axis2\\WEB-INF\\classes\\catalog.xml");
		}
		catch(Exception e)
		{
			WriteLog writeLog = new WriteLog();
			writeLog.Write(e.getMessage(),CatchURL.class.getName());
			return null;
		}
	}
}
*/