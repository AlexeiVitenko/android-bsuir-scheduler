package by.bsuir.scheduler.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class Parser {
	static private final String URL = "http://www.bsuir.by/psched/schedulegroup?group=";
	private HttpClient mClient;
	private String mUrl;
	private int mSubGroup;
	private Pushable mBridge;
	private ParserListiner mListiner;
	
	private int counter = 0;
	/**
	 * 
	 * @param group - группа. String
	 * @param subGroup - номер подгруппы.
	 * @param bridge - Класс, который проталкивает пары в базу
	 * @param listiner - слушатель на 2 события: успешное завершение и исключительная ситуация.
	 */
	public Parser(String group, int subGroup, Pushable bridge, ParserListiner listiner){
		mClient = new DefaultHttpClient();
		mUrl = URL + group;
		mSubGroup = subGroup;
		mBridge = bridge;
		mListiner = listiner;
	}
	
	/**
	 * 
	 * @return возвращает тело html-ки. Исправленное для соответствия XML
	 */
	public String getBody(){
		HttpGet getMethod = new HttpGet(mUrl);
		String body = null;
		ResponseHandler<String> respHandler = new BasicResponseHandler();
		try {
			body = mClient.execute(getMethod, respHandler);
		} catch (ClientProtocolException e) {
			if (mListiner!=null) mListiner.onException(e);
		} catch (IOException e) {
			if (mListiner!=null) mListiner.onException(e);
		}
		return cleanBody(body);
	}
	
	/**
	 * Нужен был исключительно для тестирования. Парсит из файла.
	 * @param fileName - имя файла
	 * @return возвращает тело html-ки. Исправленное для соответствия XML
	 */
	public String getBody(String fileName){
		File f = new File(fileName);
		String body = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			while (br.ready()) {
				body += br.readLine();
			}
		} catch (FileNotFoundException e) {
			if (mListiner!=null) mListiner.onException(e);
		} catch (IOException e) {
			if (mListiner!=null) mListiner.onException(e);
		}
		return cleanBody(body);
	}
	
	/**
	 * К сожалению, не все теги чисты. В br пропал слэш. img не закрывается, поэтому его просто удаляю 
	 * @param body
	 * @return Часть документа, исправленную для соответствия XML
	 */
	private String cleanBody(String body){
		String cleanBody = body.replace("<br>", "<br/>");
		cleanBody = cleanBody.replaceAll("<img [^>]*>", " ");
		return cleanBody;
	}
	
	public void parseSchedule(){
		try {
			String body = getBody();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(body)));
			NodeList list = doc.getElementsByTagName("table");
			parseTable(list.item(0));
			if (mListiner!=null) mListiner.onComplete();
		} catch (SAXException e) {
			if (mListiner!=null) mListiner.onException(e);
			return;
		} catch (IOException e) {
			if (mListiner!=null) mListiner.onException(e);
			return;
		} catch (ParserConfigurationException e) {
			if (mListiner!=null) mListiner.onException(e);
			return;
		}catch (Exception e) {
			if (mListiner!=null) mListiner.onException(e);
			return;
		}
		Log.d("Counter",""+counter);
	}

	private void parseTable(Node tableTag){
		int childCount = tableTag.getChildNodes().getLength();
		for (int i = 1; i < childCount; i++) {
			Node child = tableTag.getChildNodes().item(i);
			parseDay(child);
		}
	}
	
	private void parseDay(Node row){
		NodeList columns = row.getChildNodes();
		String day = row.getFirstChild().getFirstChild().getNodeValue();
		List<Lesson> lessons = new ArrayList<Lesson>(); 
		int length = row.getChildNodes().item(2).getChildNodes().getLength();
		
		for (int i = 0; i < length; i++) {
			Node div = columns.item(1).getChildNodes().item(i);
			String[] weeks = (div.getFirstChild().getNodeValue()==null?"":div.getFirstChild().getNodeValue()).split(",");
			String [] params = new String[columns.getLength()-1];
			for (int j = 2; j < columns.getLength(); j++) {
				
				Node d = columns.item(j).getChildNodes().item(i);
				params[j-1]=d.getFirstChild().getNodeValue()==null?"":d.getFirstChild().getNodeValue();	
			}
			for (int k = 0; k < weeks.length; k++) {
				if (mSubGroup == 0) {
					int ii = 1;
					Lesson l = new Lesson(day, weeks[k], params[ii++], params[ii++],
							params[ii++], params[ii++], params[ii++], params[ii++]);					
					lessons.add(l); //TODO delete this
				} else {
					if (params[2].equals(""+mSubGroup)||params[2].equals("")) {
						int ii = 1;
						Lesson l = new Lesson(day, weeks[k], params[ii++], params[ii++],
								params[ii++], params[ii++], params[ii++], params[ii++]);					
						lessons.add(l); //TODO delete this
					}
				}				
			}
		}
		
		for (Lesson lesson : lessons) { //TODO and this
//			System.out.println(lesson);
			mBridge.push(lesson);
			counter++;
		}
	}
}
