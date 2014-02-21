import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

public class Document {
	private String title;
	private int bookTitle;
	private int start_page;
	private int end_page;
	private int year;
	private int volume;
	private int number;
	private String url;
	private String ee;
	private String cdrom;
	private String cite;
	private String crossref;
	private String isbn;
	private String series;
	private String genre;
	private ArrayList<Integer> author;
	private int editor;
	private int publisher;
	
	public Document(){
		title = "NA";
		bookTitle = -1;
		start_page = -1;
		end_page = -1;
		year = -1;
		volume = -1;
		number = -1;
		url = "NA";
		ee = "NA";
		cdrom = "NA";
		cite = "NA";
		crossref = "NA";
		isbn = "NA";
		series = "NA";
		genre = "NA";
		author = new ArrayList<Integer>();
		editor = -1;
		publisher = -1;
	}
	
	public Document(String title, int bookTitle, int start_page, int end_page, int year,
			int volume, int number, String url, String ee, String cdrom,
			String cite, String crossref, String isbn, String series,
			String genre, int author, int editor, int publisher) {
		this.title = title;
		this.bookTitle = bookTitle;
		this.start_page = start_page;
		this.end_page = end_page;
		this.year = year;
		this.volume = volume;
		this.number = number;
		this.url = url;
		this.ee = ee;
		this.cdrom = cdrom;
		this.cite = cite;
		this.crossref = crossref;
		this.isbn = isbn;
		this.series = series;
		this.genre = genre;
		this.author = new ArrayList<Integer>();
		this.author.add(author);
		this.editor = editor;
		this.publisher = publisher;
	}
	
	public String getInfo() {
		StringBuilder stringX = new StringBuilder("('"+title.replace("'", "''")+"', "+start_page+", "+end_page+", "+year+", "+volume+", "+number+", '"+url.replace("'", "''")+"', '"+ee.replace("'", "''")+"', '"+cdrom.replace("'", "''")+"', '"+cite.replace("'", "''")+"', '"+crossref.replace("'", "''")+"', '"+isbn.replace("'", "''")+"', '"+series.replace("'", "''")+"', "+editor+", "+bookTitle+", "+publisher+")");
		return stringX.toString();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) throws IOException {
		byte[] array = title.getBytes("UTF-8");
		this.title = new String(array);
		int length = this.title.length();
		if(length>=300)
			length = 300;
		this.title = this.title.substring(0,length);
	}
	
	public int getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(int bookTitle) {
		this.bookTitle = bookTitle;
	}

	public int getStart_page() {
		return start_page;
	}

	public void setStart_page(int start_page) {
		this.start_page = start_page;
	}

	public int getEnd_page() {
		return end_page;
	}

	public void setEnd_page(int end_page) {
		this.end_page = end_page;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		int length = url.length();
		if(length>=200)
			length = 200;
		this.url = url.substring(0, length);
	}

	public String getEe() {
		return ee;
	}

	public void setEe(String ee) {
		int length = ee.length();
		if(length>=100)
			length = 100;
		this.ee = ee.substring(0,length);
	}

	public String getCdrom() {
		return cdrom;
	}

	public void setCdrom(String cdrom) {
		int length = cdrom.length();
		if(length>=75)
			length = 75;
		this.cdrom = cdrom.substring(0,length);
	}

	public String getCite() {
		return cite;
	}

	public void setCite(String cite) {
		int length = cite.length();
		if(length>=75)
			length = 75;
		this.cite = cite.substring(0,length);
	}

	public String getCrossref() {
		return crossref;
	}

	public void setCrossref(String crossref) {
		int length = crossref.length();
		if(length>=75)
			length = 75;
		this.crossref = crossref.substring(0,length);
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		int length = isbn.length();
		if(length>=21)
			length = 21;
		this.isbn = isbn.substring(0,length);
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		int length = series.length();
		if(length>=100)
			length = 100;
		this.series = series.substring(0,length);
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		int length = genre.length();
		if(length>=20)
			length = 20;
		this.genre = genre.substring(0,length);
	}

	public ArrayList<Integer> getAuthor() {
		return author;
	}

	public void setAuthor(int author) throws IOException {
		this.author.add(author);
	}

	public int getEditor() {
		return editor;
	}

	public void setEditor(int editor) {
		this.editor = editor;
	}

	public int getPublisher() {
		return publisher;
	}

	public void setPublisher(int publisher) {
		this.publisher = publisher;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Document [getTitle()=");
		builder.append(getTitle());
		builder.append(", getStart_page()=");
		builder.append(getStart_page());
		builder.append(", getEnd_page()=");
		builder.append(getEnd_page());
		builder.append(", getYear()=");
		builder.append(getYear());
		builder.append(", getVolume()=");
		builder.append(getVolume());
		builder.append(", getNumber()=");
		builder.append(getNumber());
		builder.append(", getUrl()=");
		builder.append(getUrl());
		builder.append(", getEe()=");
		builder.append(getEe());
		builder.append(", getCdrom()=");
		builder.append(getCdrom());
		builder.append(", getCite()=");
		builder.append(getCite());
		builder.append(", getCrossref()=");
		builder.append(getCrossref());
		builder.append(", getIsbn()=");
		builder.append(getIsbn());
		builder.append(", getSeries()=");
		builder.append(getSeries());
		builder.append(", getGenre()=");
		builder.append(getGenre());
		builder.append(", getAuthor()=");
		Iterator it1 = author.iterator();
		while(it1.hasNext()) {
			builder.append(it1.next().toString());
		}
		builder.append(", getEditor()=");
		builder.append(getEditor());
		builder.append(", getPublisher()=");
		builder.append(getPublisher());
		builder.append("]");
		return builder.toString();
	}
}
