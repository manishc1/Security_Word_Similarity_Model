package edu.umbc.dblp;

import java.io.Serializable;
import java.util.ArrayList;

public class DBLP implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
    private String Type;
    private ArrayList<String> authors;
    private ArrayList<String> editors;
    private ArrayList<String> cites;
    private String title;
    private String bookTitle;
    private String pages;
    private String year;
    private String address;
    private String journal;
    private String volume;
    private String number;
    private String month;
    private String url;
    private String ee;
    private String cdrom;
    private String publisher;
    private String note;
    private String crossref;
    private String isbn;
    private String series;
    private String school;
    private String chapter;
    private String dkey;
    
    public long getId() {
        return id;
    } //- getId
    public void setId(long id) {
        this.id = id;
    } //- setId
    public String getType() {
        return Type;
    } //- getType
    public void setType(String type) {
        Type = type;
    } //- setType
    public ArrayList<String> getAuthors() {
        return authors;
    } //- getAuthors
    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    } //- setAuthors
    public int getNumAuthors() {
        return authors.size();
    } //- getNumAuthors
    public ArrayList<String> getEditors() {
        return editors;
    } //- getEditor
    public void setEditors(ArrayList<String> editors) {
        this.editors = editors;
    } //- setEditor
    public int getNumEditors() {
        return editors.size();
    } //- getNumAuthors
    public String getTitle() {
        return title;
    } //- getTitle
    public void setTitle(String title) {
        this.title = title;
    } //- setTitle
    public String getBookTitle() {
        return bookTitle;
    } //- getBookTitle
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    } //- setBookTitle
    public String getPages() {
        return pages;
    } //- getPages
    public void setPages(String pages) {
        this.pages = pages;
    } //- setPages
    public String getYear() {
        return year;
    } //- getYear
    public void setYear(String year) {
        this.year = year;
    } //- setYear
    public String getAddress() {
        return address;
    } //- getAddress
    public void setAddress(String address) {
        this.address = address;
    } //- setAddress
    public String getJournal() {
        return journal;
    } //- getJournal
    public void setJournal(String journal) {
        this.journal = journal;
    } //- setJournal
    public String getVolume() {
        return volume;
    } //- getVolume
    public void setVolume(String volume) {
        this.volume = volume;
    } //- setVolume
    public String getNumber() {
        return number;
    } //- getNumber
    public void setNumber(String number) {
        this.number = number;
    } //- setNumber
    public String getMonth() {
        return month;
    } //- getMonth
    public void setMonth(String month) {
        this.month = month;
    } //- setMonth
    public String getUrl() {
        return url;
    } //- getUrl
    public void setUrl(String url) {
        this.url = url;
    } //- setUrl
    public String getEe() {
        return ee;
    } //- getEe
    public void setEe(String ee) {
        this.ee = ee;
    } //- setEe
    public String getCdrom() {
        return cdrom;
    } //- getCdrom
    public void setCdrom(String cdrom) {
        this.cdrom = cdrom;
    } //- setCdrom
    public ArrayList<String> getCites() {
        return cites;
    } //- getCite
    public void setCites(ArrayList<String> cites) {
        this.cites = cites;
    } //- setCite
    public int getNumCites() {
        return cites.size();
    } //- getNumCites
    public String getPublisher() {
        return publisher;
    } //- getPublisher
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    } //- setPublisher
    public String getNote() {
        return note;
    } //- getNote
    public void setNote(String note) {
        this.note = note;
    } //- setNote
    public String getCrossref() {
        return crossref;
    } //- getCrossref
    public void setCrossref(String crossref) {
        this.crossref = crossref;
    } //- setCrossref
    public String getIsbn() {
        return isbn;
    } //- getIsbn
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    } //- setIsbn
    public String getSeries() {
        return series;
    } //- getSeries
    public void setSeries(String series) {
        this.series = series;
    } //- setSeries
    public String getSchool() {
        return school;
    } //- getSchool
    public void setSchool(String school) {
        this.school = school;
    } //- setSchool
    public String getChapter() {
        return chapter;
    } //- getChapter
    public void setChapter(String chapter) {
        this.chapter = chapter;
    } //- setChapter
    public String getDkey() {
        return dkey;
    } //- getDkey
    public void setDkey(String dkey) {
        this.dkey = dkey;
    } //- setDkey
    
} //- class DBLP