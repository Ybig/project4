/*Class: CS122B
* Students: Eddie Lei & Yerlan Turekeshov
* Date: 02/21/2014
*/

import java.io.IOException;
import java.io.Console;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.sql.*;
import java.util.concurrent.TimeUnit;
import java.lang.StringBuilder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler{	
	public static void main(String[] args) throws Exception{
		XMLParser spe = new XMLParser();
		spe.run();
	}	
	
	List documents;
	HashMap genresMap = new HashMap(100);
	HashMap peopleMap = new HashMap(81000);
	HashMap booksMap = new HashMap(1000);
	HashMap publishersMap = new HashMap(1000);
	ArrayList<String> genresList = new ArrayList<String>(100);
	ArrayList<String> peopleList = new ArrayList<String>(81000);
	ArrayList<String> booksList = new ArrayList<String>(1000);
	ArrayList<String> publishersList = new ArrayList<String>(1000);
	private String tempVal;
	Console c = System.console();
	Connection connection;
	PrintWriter pw1, pw2, pw3, pw4, pw5, pw6;
	String database, technique;
	String path1, path2, path3, path4, path5, path6;
	//to maintain context
	private Document tempDoc;
	
	
	public XMLParser(){
		documents = new ArrayList();
	}
	
	public void run() throws Exception{
		if (c == null){
			System.err.println("No console!");
			System.exit(1);
		}
		
		// mySQL driver
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		parseDocument();
		
		System.out.println("Populating the " + database + " database...");
		//start time
		long time1 = System.currentTimeMillis();		
		populateDatabase();
		//finish time
		long time2 = System.currentTimeMillis();
		//time difference
		long timeTaken = (time2-time1);
		String timeTaken2 = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toSeconds(timeTaken)/60, TimeUnit.MILLISECONDS.toSeconds(timeTaken) % 60);
		System.out.println("Time taken to populate database: " + timeTaken2);		
		connection.close();
	}

	private void parseDocument(){
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {		
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			String fileName = "dblp-data.xml";
			System.out.println("Choose one of the techniques:");
			System.out.println("[1] - Batch Insert");
			System.out.println("[2] - Crazy Insert");
			technique = c.readLine();
			while(!technique.equals("1") && !technique.equals("2")){
				System.out.println("Wrong choice! Enter either 1 or 2:");
				technique = c.readLine();
			}
			EstablishConnection();
			
			if(technique.equalsIgnoreCase("2")){
				File f1 = new File(database+"genre.txt");
				File f2 = new File(database+"people.txt");
				File f3 = new File(database+"book.txt");
				File f4 = new File(database+"publisher.txt");
				File f5 = new File(database+"document.txt");
				File f6 = new File(database+"authormap.txt");
				path1 = f1.getAbsolutePath().replace("\\", "/").replace("'", "''");
				path2 = f2.getAbsolutePath().replace("\\", "/").replace("'", "''");
				path3 = f3.getAbsolutePath().replace("\\", "/").replace("'", "''");
				path4 = f4.getAbsolutePath().replace("\\", "/").replace("'", "''");
				path5 = f5.getAbsolutePath().replace("\\", "/").replace("'", "''");
				path6 = f6.getAbsolutePath().replace("\\", "/").replace("'", "''");
				pw1 = new PrintWriter(new FileWriter(f1));
				pw2 = new PrintWriter(new FileWriter(f2));
				pw3 = new PrintWriter(new FileWriter(f3));
				pw4 = new PrintWriter(new FileWriter(f4));
				pw5 = new PrintWriter(new FileWriter(f5));
				pw6 = new PrintWriter(new FileWriter(f6));
			}
			
			System.out.println("Parsing document...");
			long time1 = System.currentTimeMillis();
			//parse the file and also register this class for call backs
			sp.parse(fileName, this);
			
			long time2 = System.currentTimeMillis();
			long timeTaken = (time2-time1);
			String timeTaken2 = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toSeconds(timeTaken)/60, TimeUnit.MILLISECONDS.toSeconds(timeTaken) % 60);
			System.out.println("Time taken to parse document: " + timeTaken2);
			

			
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public void EstablishConnection(){
		try{
			System.out.println("Enter a database name to populate: ");
			database = c.readLine();
			System.out.println("Enter Username: ");
			String user = c.readLine();
			System.out.println("Enter Password: ");
			char[] passc = c.readPassword();
			String pass = new String(passc);
			
			System.out.println("Attempting to create connection");
			connection = DriverManager.getConnection("jdbc:mysql:///",user,pass);
			System.out.println("Connection Successful!");
			System.out.println("Dropping database if it already exists...");
			Statement dropDB = connection.createStatement();
			dropDB.execute("drop database if exists " + database);
			System.out.println("Creating database '"+database+"'...");
			Statement createDB = connection.createStatement();
			createDB.execute("create database " + database);
			System.out.println("Using database '"+database+"'...");
			Statement useDB = connection.createStatement();
			useDB.execute("use " + database);
			System.out.println("Creating tables for database...");
			
			Statement genreTable1 = connection.createStatement();
			genreTable1.execute("DROP TABLE IF EXISTS tbl_genres CASCADE");
			
			Statement genreTable2 = connection.createStatement();
			genreTable2.execute("CREATE TABLE tbl_genres(id  INTEGER NOT NULL AUTO_INCREMENT, genre_name VARCHAR(20), PRIMARY KEY (id))");
			
			Statement peopleTable1 = connection.createStatement();
			peopleTable1.execute("DROP TABLE IF EXISTS tbl_people CASCADE");
			
			Statement peopleTable2 = connection.createStatement();
			peopleTable2.execute("CREATE TABLE tbl_people(id   INTEGER NOT NULL AUTO_INCREMENT, name VARCHAR(61) CHARACTER SET utf8, PRIMARY KEY (id))");
			
			Statement bookTable1 = connection.createStatement();
			bookTable1.execute("DROP TABLE IF EXISTS tbl_booktitle CASCADE");
			
			Statement bookTable2 = connection.createStatement();
			bookTable2.execute("CREATE TABLE tbl_booktitle(id INTEGER NOT NULL AUTO_INCREMENT, title VARCHAR(300) CHARACTER SET utf8, PRIMARY KEY (id))");
			
			Statement publisherTable1 = connection.createStatement();
			publisherTable1.execute("DROP TABLE IF EXISTS tbl_publisher CASCADE");
			
			Statement publisherTable2 = connection.createStatement();
			publisherTable2.execute("CREATE TABLE tbl_publisher(id INTEGER NOT NULL AUTO_INCREMENT, publisher_name varchar(300), PRIMARY KEY (id))");
			
			Statement documentTable1 = connection.createStatement();
			documentTable1.execute("DROP TABLE IF EXISTS tbl_dblp_document CASCADE");
			
			Statement documentTable2 = connection.createStatement();
			documentTable2.execute("CREATE TABLE tbl_dblp_document(id INTEGER NOT NULL AUTO_INCREMENT, title VARCHAR(300) CHARACTER SET utf8, start_page INTEGER, end_page INTEGER, year INTEGER, volume INTEGER, number INTEGER, url VARCHAR(200), ee VARCHAR(100), cdrom VARCHAR(75), cite VARCHAR(75), crossref VARCHAR(75), isbn VARCHAR(21), series VARCHAR(100), editor_id INTEGER REFERENCES tbl_people(id), booktitle_id INTEGER REFERENCES tbl_booktitle(id), publisher_id INTEGER REFERENCES tbl_publisher(id),PRIMARY KEY (id))");
			
			Statement authorMapTable1 = connection.createStatement();
			authorMapTable1.execute("DROP TABLE IF EXISTS tbl_author_document_mapping CASCADE");
			
			Statement authorMapTable2 = connection.createStatement();
			authorMapTable2.execute("CREATE TABLE tbl_author_document_mapping(id INTEGER NOT NULL AUTO_INCREMENT, doc_id INTEGER REFERENCES tbl_dblp_document(id), author_id INTEGER REFERENCES tbl_people(id), PRIMARY KEY (id))");
		}
		catch (SQLException sql){
			System.out.println("Error trying to establish connection.");
			System.out.println(sql.getMessage());
			System.out.println("Try entering the info again.");
			String wait = c.readLine();
			EstablishConnection();
		}
	}
	
	private void populateDatabase(){
		try{
			StringBuilder genreInsert = new StringBuilder("insert into tbl_genres (genre_name) values ");
			StringBuilder peopleInsert = new StringBuilder("insert into tbl_people (name) values ");
			StringBuilder bookInsert = new StringBuilder("insert into tbl_booktitle (title) values ");
			StringBuilder publisherInsert = new StringBuilder("insert into tbl_publisher (publisher_name) values ");
			StringBuilder documentInsert = new StringBuilder("insert into tbl_dblp_document (title, start_page, end_page, year, volume, number, url, ee, cdrom, cite, crossref, isbn, series, editor_id, booktitle_id, publisher_id) values ");
			StringBuilder authorMapInsert = new StringBuilder("insert into tbl_author_document_mapping (doc_id, author_id) values ");
			Statement batchInsert = connection.createStatement();
			
			if(technique.equalsIgnoreCase("fast")){
				String lock = "LOCK TABLES tbl_genres WRITE, tbl_people WRITE, tbl_booktitle WRITE, tbl_publisher WRITE, tbl_dblp_document WRITE, tbl_author_document_mapping WRITE";
				Statement lockS = connection.createStatement();
				lockS.execute(lock);
				lockS.close();
			}
			
			int documentCount = 1;
			int batchCount = 0;
			Iterator it2 = genresList.iterator();
			while(it2.hasNext()) {
				String genre = (String) it2.next();
				if(technique.equalsIgnoreCase("fast"))
				{
					if(genreInsert.toString().endsWith(")"))
						genreInsert.append(", ");
					genreInsert.append("('"+genre.replace("'","''")+"')");
					batchCount++;
					if ((batchCount) % 1000 == 0) {
						batchInsert.addBatch(genreInsert.toString());
						int[] updateCounts = batchInsert.executeBatch();
						genreInsert = new StringBuilder("insert into tbl_genres (genre_name) values ");
					}
				}
				else if(technique.equalsIgnoreCase("faster"))
				{
					pw1.println(genre);
				}
			}
			
			Iterator it3 = peopleList.iterator();
			while(it3.hasNext()) {
				String person = (String) it3.next();
				if(technique.equalsIgnoreCase("fast"))
				{
					if(peopleInsert.toString().endsWith(")"))
						peopleInsert.append(", ");
					peopleInsert.append("('"+person.replace("'","''")+"')");
					batchCount++;
					if ((batchCount) % 1000 == 0) {
						batchInsert.addBatch(peopleInsert.toString());
						int[] updateCounts = batchInsert.executeBatch();
						peopleInsert = new StringBuilder("insert into tbl_people (name) values ");
					}
				}
				else if(technique.equalsIgnoreCase("faster"))
				{
					pw2.println(person);
				}
			}
			Iterator it5 = booksList.iterator();
			while(it5.hasNext()) {
				String book = (String) it5.next();
				if(technique.equalsIgnoreCase("fast"))
				{
					if(bookInsert.toString().endsWith(")"))
						bookInsert.append(", ");
					bookInsert.append("('"+book.replace("'","''")+"')");
					batchCount++;
					if ((batchCount) % 1000 == 0) {
						batchInsert.addBatch(bookInsert.toString());
						int[] updateCounts = batchInsert.executeBatch();
						bookInsert = new StringBuilder("insert into tbl_booktitle (title) values ");
					}
				}
				else if(technique.equalsIgnoreCase("faster"))
				{
					pw3.println(book);
				}
			}
			Iterator it6 = publishersList.iterator();
			while(it6.hasNext()) {
				String publisher = (String) it6.next();
				if(technique.equalsIgnoreCase("fast"))
				{
					if(publisherInsert.toString().endsWith(")"))
						publisherInsert.append(", ");
					publisherInsert.append("('"+publisher.replace("'","''")+"')");
					batchCount++;
					if ((batchCount) % 1000 == 0) {
						batchInsert.addBatch(publisherInsert.toString());
						int[] updateCounts = batchInsert.executeBatch();
						publisherInsert = new StringBuilder("insert into tbl_publisher (publisher_name) values ");
					}
				}
				else if(technique.equalsIgnoreCase("faster"))
				{
					pw4.println(publisher);
				}
			}
			Iterator it1 = documents.iterator();
			while(it1.hasNext()) {
				Document next = (Document)it1.next();
				ArrayList<Integer> authors = next.getAuthor();
				if(technique.equalsIgnoreCase("fast"))
				{
					String info = next.getInfo();
					//author mapping
					if(!authors.isEmpty())
					{
						for(int i = 0; i < authors.size(); i++)
						{
							if(authorMapInsert.toString().endsWith(")"))
								authorMapInsert.append(", ");
							authorMapInsert.append("("+documentCount+", "+authors.get(i)+")");
							batchCount++;
							if ((batchCount) % 1000 == 0) {
								batchInsert.addBatch(authorMapInsert.toString());
								int[] updateCounts = batchInsert.executeBatch();
								authorMapInsert = new StringBuilder("insert into tbl_author_document_mapping (doc_id, author_id) values ");
							}
						}
					}
					
					//insert document
					if(documentInsert.toString().endsWith(")"))
						documentInsert.append(", ");
					documentInsert.append(info);
					
					documentCount++;
					
					batchCount++;
					if ((batchCount) % 1000 == 0) {
						batchInsert.addBatch(documentInsert.toString());
						int[] updateCounts = batchInsert.executeBatch();
						documentInsert = new StringBuilder("insert into tbl_dblp_document (title, start_page, end_page, year, volume, number, url, ee, cdrom, cite, crossref, isbn, series, editor_id, booktitle_id, publisher_id) values ");
					}
				}
				else if(technique.equalsIgnoreCase("faster"))
				{
					int bookTitle = next.getBookTitle();
					int editor = next.getEditor();
					int publisher = next.getPublisher();
					String title = next.getTitle();
					int start_page = next.getStart_page();
					int end_page = next.getEnd_page();
					int year = next.getYear();
					int volume = next.getVolume();
					int number = next.getNumber();
					String url = next.getUrl();
					String ee = next.getEe();
					String cdrom = next.getCdrom();
					String cite = next.getCite();
					String crossref = next.getCrossref();
					String isbn = next.getIsbn();
					String series = next.getSeries();
					//insert people
					if(!authors.isEmpty())
					{
						for(int i = 0; i < authors.size(); i++)
						{
							pw6.println(documentCount + ",,," + authors.get(i));
						}
					}
					
					//insert document
					pw5.println(title + ",,," + start_page + ",,," + end_page + ",,," + year + ",,," + volume + ",,," + number + ",,," + url + ",,," + ee + ",,," + cdrom + ",,," + cite + ",,," + crossref + ",,," + isbn + ",,," + series + ",,," + editor + ",,," + bookTitle + ",,," + publisher);
					documentCount++;
				}
			}
			
			String disableKeys1 = "ALTER TABLE tbl_genres DISABLE KEYS";
			String disableKeys2 = "ALTER TABLE tbl_people DISABLE KEYS";
			String disableKeys3 = "ALTER TABLE tbl_booktitle DISABLE KEYS";
			String disableKeys4 = "ALTER TABLE tbl_publisher DISABLE KEYS";
			String disableKeys5 = "ALTER TABLE tbl_dblp_document DISABLE KEYS";
			String disableKeys6 = "ALTER TABLE tbl_author_document_mapping DISABLE KEYS";
			Statement keys1 = connection.createStatement();
			Statement keys2 = connection.createStatement();
			Statement keys3 = connection.createStatement();
			Statement keys4 = connection.createStatement();
			Statement keys5 = connection.createStatement();
			Statement keys6 = connection.createStatement();
			keys1.execute(disableKeys1);
			keys2.execute(disableKeys2);
			keys3.execute(disableKeys3);
			keys4.execute(disableKeys4);
			keys5.execute(disableKeys5);
			keys6.execute(disableKeys6);
			
			if(technique.equalsIgnoreCase("1")){
				String lock = "LOCK TABLES tbl_genres WRITE, tbl_people WRITE, tbl_booktitle WRITE, tbl_publisher WRITE, tbl_dblp_document WRITE, tbl_author_document_mapping WRITE";
				Statement lockS = connection.createStatement();
				lockS.execute(lock);
				lockS.close();
				
				if(!genreInsert.toString().endsWith("values "))
					batchInsert.addBatch(genreInsert.toString());
				if(!peopleInsert.toString().endsWith("values "))
					batchInsert.addBatch(peopleInsert.toString());
				if(!bookInsert.toString().endsWith("values "))
					batchInsert.addBatch(bookInsert.toString());
				if(!publisherInsert.toString().endsWith("values "))
					batchInsert.addBatch(publisherInsert.toString());
				if(!documentInsert.toString().endsWith("values "))
					batchInsert.addBatch(documentInsert.toString());
				if(!authorMapInsert.toString().endsWith("values "))
					batchInsert.addBatch(authorMapInsert.toString());
				
				int[] updateCounts = batchInsert.executeBatch();
				
				String unlock = "UNLOCK TABLES";
				Statement unlockS = connection.createStatement();
				unlockS.execute(unlock);
				
				batchInsert.close();
				unlockS.close();
			}
			
			if(technique.equalsIgnoreCase("2")){
				pw1.flush();
				pw1.close();
				pw2.flush();
				pw2.close();
				pw3.flush();
				pw3.close();
				pw4.flush();
				pw4.close();
				pw5.flush();
				pw5.close();
				pw6.flush();
				pw6.close();
				
				String load1 = "LOAD DATA INFILE '" + path1 + "' INTO TABLE tbl_genres FIELDS TERMINATED BY ',,,' LINES TERMINATED BY '\r\n' (genre_name)";
				String load2 = "LOAD DATA INFILE '" + path2 + "' INTO TABLE tbl_people FIELDS TERMINATED BY ',,,' LINES TERMINATED BY '\r\n' (name)";
				String load3 = "LOAD DATA INFILE '" + path3 + "' INTO TABLE tbl_booktitle FIELDS TERMINATED BY ',,,' LINES TERMINATED BY '\r\n' (title)";
				String load4 = "LOAD DATA INFILE '" + path4 + "' INTO TABLE tbl_publisher FIELDS TERMINATED BY ',,,' LINES TERMINATED BY '\r\n' (publisher_name)";
				String load5 = "LOAD DATA INFILE '" + path5 + "' INTO TABLE tbl_dblp_document FIELDS TERMINATED BY ',,,' LINES TERMINATED BY '\r\n' (title, start_page, end_page, year, volume, number, url, ee, cdrom, cite, crossref, isbn, series, editor_id, booktitle_id, publisher_id)";
				String load6 = "LOAD DATA INFILE '" + path6 + "' INTO TABLE tbl_author_document_mapping FIELDS TERMINATED BY ',,,' LINES TERMINATED BY '\r\n' (doc_id, author_id)";
				Statement load1S = connection.createStatement();
				Statement load2S = connection.createStatement();
				Statement load3S = connection.createStatement();
				Statement load4S = connection.createStatement();
				Statement load5S = connection.createStatement();
				Statement load6S = connection.createStatement();
				load1S.execute(load1);
				load2S.execute(load2);
				load3S.execute(load3);
				load4S.execute(load4);
				load5S.execute(load5);
				load6S.execute(load6);
				load1S.close();
				load2S.close();
				load3S.close();
				load4S.close();
				load5S.close();
				load6S.close();
			}
			String enableKeys1 = "ALTER TABLE tbl_genres ENABLE  KEYS";
			String enableKeys2 = "ALTER TABLE tbl_people ENABLE  KEYS";
			String enableKeys3 = "ALTER TABLE tbl_booktitle ENABLE  KEYS";
			String enableKeys4 = "ALTER TABLE tbl_publisher ENABLE  KEYS";
			String enableKeys5 = "ALTER TABLE tbl_dblp_document ENABLE  KEYS";
			String enableKeys6 = "ALTER TABLE tbl_author_document_mapping ENABLE  KEYS";
			Statement eKeys1 = connection.createStatement();
			Statement eKeys2 = connection.createStatement();
			Statement eKeys3 = connection.createStatement();
			Statement eKeys4 = connection.createStatement();
			Statement eKeys5 = connection.createStatement();
			Statement eKeys6 = connection.createStatement();
			eKeys1.execute(enableKeys1);
			eKeys2.execute(enableKeys2);
			eKeys3.execute(enableKeys3);
			eKeys4.execute(enableKeys4);
			eKeys5.execute(enableKeys5);
			eKeys6.execute(enableKeys6);
			
			eKeys1.close();
			eKeys2.close();
			eKeys3.close();
			eKeys4.close();
			eKeys5.close();
			eKeys6.close();
			keys1.close();
			keys2.close();
			keys3.close();
			keys4.close();
			keys5.close();
			keys6.close();
		}
		catch (SQLException sql) 
		{
			System.out.println("Error trying to populate database.");
			System.out.println(sql.getMessage());
			System.out.println();
			sql.printStackTrace();
		}
	}
	

	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//reset
		tempVal = "";
		if(qName.equalsIgnoreCase("article") || qName.equalsIgnoreCase("inproceedings") || qName.equalsIgnoreCase("proceedings") || qName.equalsIgnoreCase("book") || qName.equalsIgnoreCase("incollection") || qName.equalsIgnoreCase("phdthesis") || qName.equalsIgnoreCase("mastersthesis") || qName.equalsIgnoreCase("www")) {
			tempDoc = new Document();
			tempDoc.setGenre(qName.toLowerCase());
			if(!genresMap.containsKey(qName.toLowerCase())){
				genresMap.put(qName.toLowerCase(), genresMap.size()+1);
				genresList.add(qName.toLowerCase());
			}
		}
	}
	

	public void characters(char[] ch, int start, int length) throws SAXException{
		tempVal = new String(ch,start,length);
		tempVal = tempVal.trim();
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try{
			if(qName.equalsIgnoreCase("article") || qName.equalsIgnoreCase("inproceedings") || qName.equalsIgnoreCase("proceedings") || qName.equalsIgnoreCase("book") || qName.equalsIgnoreCase("incollection") || qName.equalsIgnoreCase("phdthesis") || qName.equalsIgnoreCase("mastersthesis") || qName.equalsIgnoreCase("www")){
				documents.add(tempDoc);			
			}
			else if (qName.equalsIgnoreCase("author")){
				if(!peopleMap.containsKey(tempVal)){
					peopleMap.put(tempVal, peopleMap.size()+1);
					peopleList.add(tempVal);
					tempDoc.setAuthor(peopleMap.size());
				}
				else
					tempDoc.setAuthor(Integer.parseInt(peopleMap.get(tempVal).toString()));
			}
			else if (qName.equalsIgnoreCase("editor")){
				if(!peopleMap.containsKey(tempVal)){
					peopleMap.put(tempVal, peopleMap.size()+1);
					peopleList.add(tempVal);
					tempDoc.setEditor(peopleMap.size());
				}
				else
					tempDoc.setEditor(Integer.parseInt(peopleMap.get(tempVal).toString()));
			}
			else if (qName.equalsIgnoreCase("title")){
				tempDoc.setTitle(tempVal);
			}
			else if (qName.equalsIgnoreCase("booktitle")){
				if(!booksMap.containsKey(tempVal)){
					booksMap.put(tempVal, booksMap.size()+1);
					booksList.add(tempVal.replace("\"", "\\\""));
					tempDoc.setBookTitle(booksMap.size());
				}
				else
					tempDoc.setBookTitle(Integer.parseInt(booksMap.get(tempVal).toString()));
			}
			else if (qName.equalsIgnoreCase("publisher")){
				if(!publishersMap.containsKey(tempVal)){
					publishersMap.put(tempVal, publishersMap.size()+1);
					publishersList.add(tempVal);
					tempDoc.setPublisher(publishersMap.size());
				}
				else
					tempDoc.setPublisher(Integer.parseInt(publishersMap.get(tempVal).toString()));
			}
			else if (qName.equalsIgnoreCase("pages")) {
				String[] temp = tempVal.split("-");
				if(temp[0].equals("v"))
					temp[0] = "5";
				if(temp[0].equals("ix"))
					temp[0] = "9";
				if(temp[0].equals("28l"))
					temp[0] = "281";
					
				tempDoc.setStart_page(Integer.parseInt(temp[0]));
				
				if(temp.length>1){
					if(temp[1].equals("vi"))
						temp[1] = "6";
					if(temp[1].equals("xvi"))
						temp[1] = "16";
					if(temp[1].equals("vii"))
						temp[1] = "7";
					if(temp[1].equals("xv"))
						temp[1] = "15";
						
					tempDoc.setEnd_page(Integer.parseInt(temp[1]));
				}
			}
			else if (qName.equalsIgnoreCase("year")) 
				tempDoc.setYear(Integer.parseInt(tempVal));
				
			else if (qName.equalsIgnoreCase("volume")){
				int tempNumber;
				try{
					tempNumber = Integer.parseInt(tempVal);
				}
				catch(NumberFormatException nfe){
					tempNumber = extractNumber(tempVal);
				}
				tempDoc.setVolume(tempNumber);
			}
			else if (qName.equalsIgnoreCase("number")) 
				tempDoc.setNumber(Integer.parseInt(tempVal));
				
			else if (qName.equalsIgnoreCase("url")) 
				tempDoc.setUrl(tempVal);
				
			else if (qName.equalsIgnoreCase("ee")) 
				tempDoc.setEe(tempVal);
				
			else if (qName.equalsIgnoreCase("cdrom")) 
				tempDoc.setCdrom(tempVal);
				
			else if (qName.equalsIgnoreCase("cite")) 
				tempDoc.setCite(tempVal);
		
			else if (qName.equalsIgnoreCase("crossref")) 
				tempDoc.setCrossref(tempVal);

			else if (qName.equalsIgnoreCase("isbn")) 
				tempDoc.setIsbn(tempVal);
	
			else if (qName.equalsIgnoreCase("series")) 
				tempDoc.setSeries(tempVal);
		}
		catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public int extractNumber(String number){
		String[] split = number.split("-");
		int tempNumber = -1;
		try{
			tempNumber = Integer.parseInt(split[1]);
		}
		catch(NumberFormatException nfe){
		}
		return tempNumber;
	}
}