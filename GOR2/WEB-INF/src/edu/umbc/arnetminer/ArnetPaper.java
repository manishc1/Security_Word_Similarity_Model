package edu.umbc.arnetminer;

import java.io.Serializable;
import java.util.ArrayList;

public class ArnetPaper implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
    private ArrayList<String> authors;
    private ArrayList<String> cites;
    private String title;
    private String conf;
    private String year;
    private String paper_abstract;
    
    public String getId() {
        return id;
    } //- getId
    public void setId(String id) {
        this.id = id;
    } //- setId
    public ArrayList<String> getAuthors() {
        return authors;
    } //- getAuthors
    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    } //- setAuthors
    public int getNumAuthors() {
        return authors.size();
    } //- getNumAuthors
    public String getTitle() {
        return title;
    } //- getTitle
    public void setTitle(String title) {
        this.title = title;
    } //- setTitle
    public String getConference() {
        return conf;
    } //- getConference
    public void setConference(String conf) {
        this.conf = conf;
    } //- setConference
    public String getYear() {
        return year;
    } //- getYear
    public void setYear(String year) {
        this.year = year;
    } //- setYear
    public String getAbstract() {
        return paper_abstract;
    } //- getAbstract
    public void setAbstract(String paper_abstract) {
        this.paper_abstract = paper_abstract;
    } //- setAbstract
    public ArrayList<String> getCites() {
        return cites;
    } //- getCite
    public void setCites(ArrayList<String> cites) {
        this.cites = cites;
    } //- setCite
    public int getNumCites() {
        return cites.size();
    } //- getNumCites
    
} //- class ArnetPaper