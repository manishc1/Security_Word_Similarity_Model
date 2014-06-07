package sweto;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import org.apache.commons.lang.StringEscapeUtils;

import edu.uga.cs.lsdis.semdis.model.UgaOntologyModel;

import edu.uga.cs.lsdis.semdisds.ILiteral;
import edu.uga.cs.lsdis.semdisds.ILiteralStatement;
import edu.uga.cs.lsdis.semdisds.IOntologyModel;


/**
 * <p>Sweto-Dblp creates RDF from DBLP + other sources.
 * This class extends DefaultHandler to parse the XML version of DBLP.</p>
 *
 * <p>Other sources (specified in constructor) include:
 *   (i) RDF data of academic organizations (schools, universities, etc) 
 *   (ii) RDF data of publishing companies (some of these might include universities)
 *   (iii) RDF data of Series, such as CEUR-WS or book series
 * </p>
 *
 * @author  <a href="http://lsdis.cs.uga.edu/~aleman/">Boanerges Aleman-Meza</a>
 *
 * @version  0.1  - First release
 *
 */


public class SwetoDblp extends DefaultHandler {


  /** constant for 'article' */
  public static final String ARTICLE = "article";

  /** constant for 'book' */
  public static final String BOOK = "book";

  /** constant for 'incollection' */
  public static final String INCOLLECTION = "incollection";

  /** constant for 'proceedings' */
  public static final String PROCEEDINGS = "proceedings";

  /** constant for 'mastersthesis' */
  public static final String MASTERSTHESIS = "mastersthesis";

  /** constant for 'phdthesis' */
  public static final String PHDTHESIS = "phdthesis";

  /** constant for 'inproceedings' */
  public static final String INPROCEEDINGS = "inproceedings";

  /** constant for 'www' */
  public static final String WWW = "www";

  /** constant for minimum filter vale */
  public final int MIN_FILTER = 0;

  /** constant for maximum filter vale */
  public final int MAX_FILTER = Integer.MAX_VALUE;

  /** constant for 'author' */
  public final String AUTHOR = "author";

  /** constant for 'booktitle' */
  public final String BOOKTITLE = "booktitle";

  /** constant for 'cdrom' */
  public final String CDROM = "cdrom";

  /** constant for 'chapter' */
  public final String CHAPTER = "chapter";

  /** constant for 'cites' */
  public final String CITES = "cite";

  /** constant for 'crossref' */
  public final String CROSSREF = "crossref";

  /** constant for 'db/' */
  public final String DB_ = "db/";

  /** constant for dblp namespace */
  public final String DBLP = "http://www.informatik.uni-trier.de/~ley/";

  /** constant for dblp bibtex */
  public final String DBLP_BIBTEX = "http://dblp.uni-trier.de/rec/bibtex/";

  /** constant for dblp2 namespace */
  public final String DBLP_AUTHOR_INDICES = DBLP + "db/indices/a-tree/";

  /** constant for 'editor' */
  public final String EDITOR = "editor";

  /** constant for 'ee' */
  public final String EE = "ee";

  /** constant for 'foaf:Document' class */
  public final String FOAF_DOCUMENT = "foaf:Document";

  /** constant for 'foaf:homepage' property */
  public final String FOAF_HOMEPAGE = "foaf:homepage";

  /** constant for 'foaf:workplaceHomepage' property */
  public final String FOAF_WORKPLACE_HOMEPAGE = "foaf:workplaceHomepage";

  /** constant for 'homepages/' */
  public final String HOMEPAGES = "homepages/";

  /** constant for 'href' attribute name */
  public final String HREF = "href";

  /** constant for 'isbn' */
  public final String ISBN = "isbn";

  /** constant for 'journal' */
  public final String JOURNAL = "journal";

  /** constant for 'key' */
  public final String KEY = "key";

  /** constant for 'mdate' */
  public final String MDATE = "mdate";

  /** constant for 'month' */
  public final String MONTH = "month";

  /** constant for 'note' */
  public final String NOTE = "note";

  /** constant for 'number' */
  public final String NUMBER = "number";

  /** constant for 'opus:article_in_journal' relationship */
  public final String OPUS_ARTICLE_IN_JOURNAL = "opus:article_in_journal";

  /** constant for 'opus:chapter_of' relationship */
  public final String OPUS_CHAPTER_OF = "opus:chapter_of";

  /** constant for 'opus:contained_in_proceedings' relationship */
  public final String OPUS_CONTAINED_IN_PROCEEDINGS = "opus:isIncludedIn";

  /** constant for 'opus:Webpage' class */
  public final String OPUS_WEBPAGE = "opus:Webpage";

  /** constant for a 'owl:sameAs' property */
  public final String OWL_SAME_AS = "owl:sameAs";

  /** constant for 'opus:in-series' relationship */
  public final String OPUS_IN_SERIES = "opus:in_series";

  /** constant for 'opus:gMonth' relationship */
  public final String OPUS_GMONTH = "opus:gMonth";

  /** constant for 'pages' */
  public final String PAGES = "pages";

  /** constant for 'publisher' */
  public final String PUBLISHER = "publisher";

  /** constant for 'rdfs:label' property */
  public final String RDFS_LABEL = "rdfs:label";

  /** constant for 'dc:relation' property */
  public final String DC_RELATION = "dc:relation";

  /** constant for 'school' */
  public final String SCHOOL = "school";

  /** constant for 'series' */
  public final String SERIES = "series";

  /** constant for 'title' */
  public final String TITLE = "title";

  /** constant for 'url' */
  public final String URL = "url";

  /** constant for 'volume' */
  public final String VOLUME = "volume";

  /** constant for 'www/' */
  public final String WWW_SLASH = "www/";

  /** constant for 'year' */
  public final String YEAR = "year";

  private int numberArticle = 0;
  private int numberInProceedings = 0;
  private int numberProceedings = 0;
  private int numberPublisher = 0;
  private int numberBook = 0;
  private int numberInCollection = 0;
  private int numberPhdThesis = 0;
  private int numberMasterThesis = 0;
  private int numberWww = 0;
  private int filterCount = 0;

  private boolean flag = false;

  private boolean wwwFlag;  // we used this flag for special handling of www-entity

  // filter that indicates which data to process
  private final String filterString;

  // the text content of an element
  private String text;

  // the 'href' attribute value of last element read
  private String hrefAttribute;

  private String latestPublicationItemRead;

  // keeps reference to the current xml tag being read
  private String currentTag;

  // mapping of an author's uri to her/his name
  private Map<String, String> authorUriToNameMap;

  // mapping of an author's uri to her/his webpage(s)
  private Map<String, Set<String>> authorUriToWebpageMap;

  // mapping of an author's uri to her/his affiliation(s)
  private Map<String, Set<String>> authorUriToAffiliationMap;

  // mapping of an author's uri to 'sameAs' uri's of himself/herself
  private Map<String, List<String>> authorUriToSameAsMap;

  // mapping of dblp xml entity names to a specific ontology (i.e., opus)
  private final Map<String, String> identifiersMap;

  // list of properties (key,values) of a 'www' xml entity (which have special rdf output)
  private final List<String[]> wwwList;

  // tags which are exceptions as they will convert from <tag>text</tag> to <tag rdf:resource="(text)" />
  private final Set<String> tagExceptionsSet;

  // tags which must not have new line before the text, such as <rdfs:label>text</rdfs:label>
  private final Set<String> newlineExceptionsSet;

  // keeps trak of publishers found
  private final Set<String> publishersFoundSet;

  // keeps track of Series found
  private final Set<String> seriesFoundSet;

  // keeps track of schools found
  private final Set<String> schoolsFoundSet;

  // list of tags, used in 'endElement' method
  private final List<String> tagList = new ArrayList<String>();

  // ontologyModel for Schools and Universities
  private final IOntologyModel universitiesOntologyModel;

  // ontologyModel for Publishers
  private final IOntologyModel publishersOntologyModel;

  // ontologyModel for Series (such as LNCS, CEUR Workshop Proceedings, etc)
  private final IOntologyModel seriesOntologyModel;

  // lines for debugging/fixing/etc
  private final Set<String> debuggingSet;

  // flag to figure out if we're looking at first/last author
  private boolean authorsFlag;

  // keeps a reference to last author relation
  private String lastAuthorRelationship;

  // mapping of literal properties which have an applicable xml-schema datatype
  private Map<String, String> xmlDatatypeMap;


  /**
   * Constructor
   * @param  filter  the filter string.
   *         Either of ARTICLE, BOOK, INCOLLECTION, PROCEEDINGS, MASTERSTHESIS, PHDTHESIS, INPROCEEDINGS, WWW.
   *         If null, then it considers any of the previous.
   * @param  universitiesOntologyModel  the 'universities' ontology model to use (containing universities, schools, etc)
   * @param  publishersOntologyModel  the 'publishers' ontology model to use
   * @param  seriesOntologyModel  the 'series' ontology model to use
   */
  public SwetoDblp( String filter, IOntologyModel universitiesOntologyModel,
      IOntologyModel publishersOntologyModel, IOntologyModel seriesOntologyModel ) {
    tagExceptionsSet = new TreeSet<String>();
    tagExceptionsSet.add( AUTHOR );
    tagExceptionsSet.add( CITES );
    tagExceptionsSet.add( CROSSREF );
    tagExceptionsSet.add( EDITOR );
    tagExceptionsSet.add( NOTE );
    tagExceptionsSet.add( PUBLISHER );
    tagExceptionsSet.add( SCHOOL );
    tagExceptionsSet.add( SERIES );
    tagExceptionsSet.add( URL );
    tagExceptionsSet.add( "address" );  // 'address' is ignored (rarely appears)

    authorUriToNameMap = new TreeMap<String, String>();
    authorUriToWebpageMap = new TreeMap<String, Set<String>>();
    authorUriToAffiliationMap = new TreeMap<String, Set<String>>();
    authorUriToSameAsMap = new TreeMap<String, List<String>>();
    publishersFoundSet = new TreeSet<String>();
    seriesFoundSet = new TreeSet<String>();
    schoolsFoundSet = new TreeSet<String>();
    wwwList = new ArrayList<String[]>();
    debuggingSet = new TreeSet<String>();

    newlineExceptionsSet = new TreeSet<String>();
    newlineExceptionsSet.add( BOOKTITLE );
    newlineExceptionsSet.add( CDROM );
    newlineExceptionsSet.add( CHAPTER );
    newlineExceptionsSet.add( EE );
    newlineExceptionsSet.add( ISBN );
    newlineExceptionsSet.add( JOURNAL );
    newlineExceptionsSet.add( MONTH );
    newlineExceptionsSet.add( NUMBER );
    newlineExceptionsSet.add( PAGES );
    newlineExceptionsSet.add( SERIES );
    newlineExceptionsSet.add( TITLE );
    newlineExceptionsSet.add( VOLUME );
    newlineExceptionsSet.add( YEAR );

    // the mapping of dblp xml entity names to a specific ontology (e.g., dc, opus, foaf)
    identifiersMap = new TreeMap<String, String>();
    identifiersMap.put( ARTICLE, "opus:Article" );
    identifiersMap.put( AUTHOR, "opus:author" );
    identifiersMap.put( BOOK, "opus:Book" );
    identifiersMap.put( BOOKTITLE, "opus:book_title" );
    identifiersMap.put( CDROM, "opus:cdrom" );
    identifiersMap.put( CHAPTER, "opus:chapter" );
    identifiersMap.put( CITES, "opus:cites" );
    identifiersMap.put( EDITOR, "opus:editor" );
    identifiersMap.put( EE, "opus:ee" );
    identifiersMap.put( ISBN, "opus:isbn" );
    identifiersMap.put( INCOLLECTION, "opus:Book_Chapter" );
    identifiersMap.put( INPROCEEDINGS, "opus:Article_in_Proceedings" );
    identifiersMap.put( JOURNAL, "opus:journal_name" );
    identifiersMap.put( MASTERSTHESIS, "opus:Masters_Thesis" );
    identifiersMap.put( MDATE, "opus:last_modified_date" );
    identifiersMap.put( MONTH, "opus:month" );
    identifiersMap.put( NUMBER, "opus:number" );
    identifiersMap.put( PAGES, "opus:pages" );
    identifiersMap.put( PHDTHESIS, "opus:Doctoral_Dissertation" );
    identifiersMap.put( PROCEEDINGS, "opus:Proceedings" );
    identifiersMap.put( SCHOOL, "opus:at_university" );
    identifiersMap.put( SERIES, "opus:Series" );
    identifiersMap.put( TITLE, RDFS_LABEL );
    identifiersMap.put( URL, "dc:identifier" );
    identifiersMap.put( VOLUME, "opus:volume" );
    identifiersMap.put( WWW, FOAF_DOCUMENT );
    identifiersMap.put( YEAR, "opus:year" );

    // the mapping of xml-schema datatypes for the properties for which it is applicable
    xmlDatatypeMap = new TreeMap<String, String>();
    xmlDatatypeMap.put( CHAPTER, "&xsd;integer" );
    xmlDatatypeMap.put( MDATE,   "&xsd;date" );
    xmlDatatypeMap.put( YEAR,   "&xsd;gYear" );
    // note: MONTH is a special case handled somewhere in the code

    wwwFlag = false;
    authorsFlag = false;

    this.filterString = filter;
    this.universitiesOntologyModel = universitiesOntologyModel;
    this.publishersOntologyModel = publishersOntologyModel;
    this.seriesOntologyModel = seriesOntologyModel;
  }; // constructor


  /**
   * Determines whether a filter condition is met
   * @param  localName  the current start-tag being read
   * @return  true/false depending upon filter condition
   */
  public boolean getFilterFlag( String localName ) {
    if( localName == null ) {
      return false;
    }; // if
    if( filterString == null ) {
      filterCount++;
      if( filterCount >= MIN_FILTER && filterCount <= MAX_FILTER ) {
        return true;
      }; // if
      return false;
    }; // if
    if( filterString.equals( localName ) ) {
      filterCount++;
      if( filterCount >= MIN_FILTER && filterCount <= MAX_FILTER ) {
        return true;
      }; // if
    }; // if
    return false;
  }; // getFilterFlag


  /**
   * Receive notification of the start of an element.
   * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
   * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
   * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
   * @param atts - The attributes attached to the element. If there are no attributes, it shall be an empty Attributes object.
   * @throws SAXException - Any SAX exception, possibly wrapping another exception.
   */
  public void startElement( String namespaceURI, String localName, String qName, Attributes atts )
      throws SAXException {

    String mappedName = null;
    List<String> attributesToRelationshipsList = null;

    // reset the 'text' variable
    text = null;

    // set the 'href' attribute, if any
    hrefAttribute = atts.getValue( HREF );

    if( ARTICLE.equals( localName ) ) {
      numberArticle++;
      flag = getFilterFlag( localName );
      authorsFlag = false;
      latestPublicationItemRead = localName;
    }; // if

    if( INPROCEEDINGS.equals( localName ) ) {
      numberInProceedings++;
      flag = getFilterFlag( localName );
      authorsFlag = false;
      latestPublicationItemRead = localName;
    }; // if

    if( PROCEEDINGS.equals( localName ) ) {
      numberProceedings++;
      flag = getFilterFlag( localName );
      authorsFlag = false;
      latestPublicationItemRead = localName;
    }; // if

    if( BOOK.equals( localName ) ) {
      numberBook++;
      flag = getFilterFlag( localName );
      authorsFlag = false;
      latestPublicationItemRead = localName;
    }; // if

    if( INCOLLECTION.equals( localName ) ) {
      numberInCollection++;
      flag = getFilterFlag( localName );
      authorsFlag = false;
      latestPublicationItemRead = localName;
    }; // if

    if( PHDTHESIS.equals( localName ) ) {
      numberPhdThesis++;
      flag = getFilterFlag( localName );
      authorsFlag = false;
      latestPublicationItemRead = localName;
    }; // if

    if( MASTERSTHESIS.equals( localName ) ) {
      numberMasterThesis++;
      flag = getFilterFlag( localName );
      authorsFlag = false;
      latestPublicationItemRead = localName;
    }; // if

    if( WWW.equals( localName ) ) {
      numberWww++;
      flag = true;  // because we want to get sameAs relationships of authors all the time
      // 'www' is a special case
      if( flag == true ) {
        // we clear the map that keeps info of www entities
        wwwList.clear();
        wwwFlag = true;
      }; // if
      latestPublicationItemRead = localName;
    }; // if

    if( PUBLISHER.equals( localName ) ) {
      if( flag == true ) {
        numberPublisher++;
      }; // if
    }; // if

    String line = "";      // the textline to output
    String tmpLine = null;
    String xmlDatatype = null;
    currentTag = localName;

    if( flag == true ) {
      if( tagExceptionsSet.contains( localName ) ) {
        // donothing
      }
      else {
        // get the mapped name, if any
        mappedName = identifiersMap.get( localName );
        if( mappedName == null ) {
          debuggingSet.add( "Debugging, encoding <" + localName
              + "> as '" + StringEscapeUtils.escapeXml( localName ) + "'" );
          System.out.print( StringEscapeUtils.escapeXml( "<" + localName + ">" ) );
          return;
        }; // if
        line += "<" + mappedName;

        // deal with the (xml) attributes
        for( int i = 0; i < atts.getLength(); i++ ) {
          // is it a 'key' attribute?
          if( KEY.equals( atts.getLocalName( i ) ) ) {
            line += " rdf:about=\"" + DBLP_BIBTEX + atts.getValue( i ) + "\"";
            if( wwwFlag == true ) {
              wwwList.add( new String[] { KEY, atts.getValue( i ) } );
            }; // if
          }
          else {
            // figure out xml-attributes that will become rdf (data) properties
            mappedName = identifiersMap.get( atts.getLocalName( i ) );
            if( mappedName != null ) {
              if( attributesToRelationshipsList == null ) {
                attributesToRelationshipsList = new ArrayList<String>();
              }; // if

              tmpLine = "<" + mappedName;
              // add xml-schema datatypes, if applicable for this property-name
              xmlDatatype = xmlDatatypeMap.get( atts.getLocalName( i ) );
              if( xmlDatatype != null ) {
                tmpLine += " rdf:datatype=\"" + xmlDatatype + "\"";
              }; // if
              tmpLine += ">" + atts.getValue( i ) + "</" + mappedName + ">";
              attributesToRelationshipsList.add( tmpLine );
              if( wwwFlag == true ) {
                wwwList.add( new String[] { atts.getLocalName( i ), atts.getValue( i ) } );
              }; // if
            }
            else {
              // verify if it is an attribute to be ignored; currently 'rating' and 'reviewid' are ignored
              if( "rating".equals( atts.getLocalName( i ) ) || "reviewid".equals( atts.getLocalName( i ) ) ) {
                // do-nothing
              }
              else {
                System.err.println( " WHATISTHIS1?-->" + atts.getLocalName( i ) + "='" + atts.getValue( i ) + "'" );
              }; // if
            }; // if
          }; // if
        }; // for

        if( wwwFlag == false ) {
          // add xml-schema datatypes, if applicable for this property-name
          xmlDatatype = xmlDatatypeMap.get( localName );
          if( xmlDatatype != null ) {
            line += " rdf:datatype=\"" + xmlDatatype + "\"";
          }; // if

          line += ">";  // closing the tag

          // take care of the 'newline' exceptions
          if( newlineExceptionsSet.contains( localName ) == false ) {
            line += "\n";
          }; // if

          // verify if we were outputing authors
          if( authorsFlag == true && AUTHOR.equals( localName ) == false && EDITOR.equals( localName ) == false ) {
            authorsFlag = false;
            System.out.println( " </rdf:Seq>" );
            System.out.println( "</" + lastAuthorRelationship + ">" );
          }; // if

          System.out.print( line );

          // output, if any, xml-attributes that became properties in rdf
          if( attributesToRelationshipsList != null ) {
            for( String each : attributesToRelationshipsList ) {
              System.out.println( each );
            }; // for
          }; // if

        }
        else {
          // verify if we were outputing authors
          if( authorsFlag == true && AUTHOR.equals( localName ) == false && EDITOR.equals( localName ) == false ) {
            authorsFlag = false;
            wwwList.add( new String[] { null, " </rdf:Seq>" } );
            wwwList.add( new String[] { null, "</" + lastAuthorRelationship + ">" } );
          }; // if
        }; // if

      }; // if
    }; // if

  }; // startElement


  /**
   * Receive notification of character data inside an element.
   * @param ch - The characters.
   * @param start - The start position in the character array.
   * @param length - The number of characters to use from the character array.
   * @throws SAXException - Any SAX exception, possibly wrapping another exception.
   */
  public void characters( char[] ch, int start, int length )
      throws SAXException {
    if( text == null ) {
      text = new String( ch, start, length );
    }
    else {
      text += new String( ch, start, length );
    }; // if
  }; // characters


  /**
   * Receive notification of the end of an element.
   * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
   * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
   * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
   * @throws SAXException - Any SAX exception, possibly wrapping another exception.
   */
  public void endElement( String namespaceURI, String localName, String qName )
      throws SAXException {

    boolean previousFlag = flag;
    boolean tmpFlag = false;
    String escapedText = null;
    String mappedName = identifiersMap.get( localName );

    if( mappedName == null ) {
      mappedName = localName;
    }; // if

    if( text != null ) {
      escapedText = StringEscapeUtils.escapeXml( text.trim() );
    }; // if

    // reset the tag set
    tagList.clear();

    // deal with 'author' and 'editor'
    if( AUTHOR.equals( localName ) || EDITOR.equals( localName ) ) {
      if( flag == true ) {
        // verify if we're looking at the first author/editor (in order to start the rdf:Seq)
        if( authorsFlag == false ) {
          authorsFlag = true;
          if( wwwFlag == true ) {
            wwwList.add( new String[] { null, " <" + mappedName + ">" } );
            wwwList.add( new String[] { null, "  <rdf:Seq>" } );
          }
          else {
            tagList.add( "<" + mappedName + ">" );
            tagList.add( " <rdf:Seq>" );
          }; // if
          lastAuthorRelationship = mappedName;
        }; // if
        String uri = null;
        String[] dblpUriAndName = null;  // [0] contains uri, [1] contains name
        try {
          dblpUriAndName = Util.getDblpUrlEnding( text.trim() );
          uri = DBLP_AUTHOR_INDICES + dblpUriAndName[ 0 ] + ".html";
          authorUriToNameMap.put( uri, dblpUriAndName[ 1 ] );
        }
        catch( Exception e ) {
          System.err.println( "[OutputHandler::endElement] Error calling Util.getDblpUrlEnding with -->" + text + "<--" );
          e.printStackTrace( System.err );
        }; // try
        // handle 'www' special case
        if( wwwFlag == true ) {
          wwwList.add( new String[] { localName, uri } );
        }
        else {
          tagList.add( "  <rdf:li rdf:resource=\"" + uri + "\" />" );
        }; // if
      }; // if
    }; // if

    // deal with 'url'
    if( URL.equals( localName ) && flag == true ) {
      if( text == null ) {
        text = "WARNING, content in 'url' is null!";
      };
      if( text.startsWith( DB_ ) ) {
        if( authorsFlag == true ) {
          authorsFlag = false;
          tagList.add( " </rdf:Seq>" );
          tagList.add( "</" + lastAuthorRelationship + ">" );
        }; // if
        tagList.add( "<" + DC_RELATION + ">" + DBLP + escapedText + "</" + DC_RELATION + ">" );
      }
      else {
        // we just use it as regular url
        if( wwwFlag == false ) {
          tagList.add( "<" + mappedName + ">" + escapedText + "</" + mappedName + ">" );
        }
        else {
          wwwList.add( new String[] { URL, escapedText } );
        }; // if
      }; // if
    }; // if

    // deal with 'cites'
    if( CITES.equals( localName ) && flag == true ) {
      if( "...".equals( text ) ) {
        tagList.add( "" );
      }
      else {
        tagList.add( "<" + mappedName + " rdf:resource=\"" + DBLP_BIBTEX + text + "\" />" );
      }; // if
    }; // if

    // deal with 'crossref'
    if( CROSSREF.equals( localName ) && flag == true ) {
      if( INCOLLECTION.equals( latestPublicationItemRead ) ) {
        tagList.add( "<" + OPUS_CHAPTER_OF + " rdf:resource=\"" + DBLP_BIBTEX + text + "\" />" );
      }
      else {
        if( ARTICLE.equals( latestPublicationItemRead ) ) {
          tagList.add( "<" + OPUS_ARTICLE_IN_JOURNAL + " rdf:resource=\"" + DBLP_BIBTEX + text + "\" />" );
        }
        else {
          if( PROCEEDINGS.equals( latestPublicationItemRead ) ) {
            // do-nothing (only 11 such cases in my last test, it would point to itself)
            tagList.add( "" );
          }
          else {
            if( INPROCEEDINGS.equals( latestPublicationItemRead ) ) {
              tagList.add( "<" + OPUS_CONTAINED_IN_PROCEEDINGS + " rdf:resource=\"" + DBLP_BIBTEX + text + "\" />" );
            }
            else {
              tagList.add( "WHATISTHIS-crossref<" + mappedName + " rdf:resource=\"" + DBLP_BIBTEX + text + "\" />" );
              tagList.add( "WHATISTHIS-crossref<latestPublicationItemRead>" + latestPublicationItemRead + "</latestPublicationItemRead>" );
            }; // if
          }; // if
        }; // if
      }; // if
    }; // if

    // deal with 'publisher'
    if( PUBLISHER.equals( localName ) && flag == true ) {
      if( text != null ) {
        if( "".equals( escapedText ) == false && "?".equals( escapedText ) == false ) {
          ILiteral literal = publishersOntologyModel.getLiteral( text.trim() );
          if( literal != null ) {
            for( ILiteralStatement literalStatement : literal.getLiteralStatements() ) {
              tagList.add( "<dc:" + PUBLISHER + " rdf:resource=\""
                  + StringEscapeUtils.escapeHtml( literalStatement.getSubject().getURI() ) + "\" />" );
              publishersFoundSet.add( literalStatement.getSubject().getURI() );
            }; // for
          }; // if

          // if at this point tagList is empty, then no uri was found
          if( tagList.isEmpty() == true ) {
            System.err.println( "<--WHATISTHISPUBLISHER?" + localName + "=(" + text.trim() + ")-->" );
          }; // if
        }
        else {
          tagList.add( "" );
        }; // if
      }; // if
    }; // if

    // deal with 'school'
    if( SCHOOL.equals( localName ) && flag == true ) {
      if( text != null ) {
        if( "".equals( text ) == false ) {
          tmpFlag = false;
          ILiteral literal = universitiesOntologyModel.getLiteral( text );
          if( literal != null ) {
            for( ILiteralStatement literalStatement : literal.getLiteralStatements() ) {
              tagList.add( "<" + mappedName + " rdf:resource=\"" + literalStatement.getSubject().getURI() + "\" />" );
              tmpFlag = true;
              schoolsFoundSet.add( literalStatement.getSubject().getURI() );
            }; // for
          }; // if
          if( tmpFlag == false ) {
            System.err.println( "<--WHATISTHIS2?" + localName + "=(" + text + ")-->" );
          }; //if
        }; // if
      }; // if
    }; // if

    // deal with 'note'
    if( NOTE.equals( localName ) && flag == true ) {
      if( text != null ) {
        if( wwwFlag == true ) {
          if( "".equals( text ) == false ) {
            tmpFlag = false;
            ILiteral literal = universitiesOntologyModel.getLiteral( text );
            if( literal != null ) {
              for( ILiteralStatement literalStatement : literal.getLiteralStatements() ) {
                wwwList.add( new String[] { SCHOOL, literalStatement.getSubject().getURI() } );
                tmpFlag = true;
                schoolsFoundSet.add( literalStatement.getSubject().getURI() );
                tagList.add( "" );  // this lines helps to avoid (later on) getting 'note' added to wwwList
              }; // for
            }; // if
            if( tmpFlag == false ) {
              // handle some exceptions (some improvements could be done here, such as dealing with companies)
              if( "1951 - 2005".equals( escapedText ) || "&#33487;&#24314;&#25991;".equals( escapedText )
                  || "1928 - 2004".equals( escapedText )
                  || "25 March 1940 - 26 January 2007".equals( escapedText )
                  || "Dec. 3, 1953 - Dec. 20, 1995".equals( escapedText )
                  || "!!! plagiarised papers !!!".equals( escapedText )
                  || "&#34081;&#24535;&#24378;".equals( escapedText )
                  || "&#20309;&#19993;&#32988;".equals( escapedText )
                  || "&#40644;&#26234;&#21191;".equals( escapedText )
                  || "&#21608;&#26195;&#26041;".equals( escapedText )
                  || "&#38515;&#37528;&#25010;".equals( escapedText )
                  || "&#29579;&#32032;&#29748;&#21103;&#25945;&#25480;".equals( escapedText )
                  || "&#44608;&#54032;&#44396;".equals( escapedText )
                  || "&#1513;&#1512;&#1497;&#1488;&#1500; &#1492;&#1512;-&#1508;&#1500;&#1491;".equals( escapedText )
                  || "&#21525;&#32487;&#24378;".equals( escapedText )
                  || "&#26446;&#38555;&#20553;".equals( escapedText )
                  || "&#26519;&#22025;&#25991;".equals( escapedText )
                  || "&#35997;&#34311;&#31456;".equals( escapedText )
                  || "&#26045;&#24013;&#26494;".equals( escapedText )
                  || "&#20110;&#26093;".equals( escapedText )
                  || "&#24352;&#30922;, Microsoft Research Asia, Beijing".equals( escapedText )
                  || "Amal Shehan Perera".equals( escapedText )
                  || "AMEC Offshore Services, Minto Drive, Aberdeen AB12 3LW, UK".equals( escapedText )
                  || "University of Oregon, Ph.D. UCLA".equals( escapedText )
                  || "Ph.D. University of Washington, 2005".equals( escapedText )
                  || "PhD from Laboratoire de Recherche en Informatique (Dominique Sotteau)".equals( escapedText )
                  || "Randy Howard Katz".equals( escapedText )
                  || "Southern Illinois University, Ph.D: Pennsylvania State University".equals( escapedText )
                  || "Ond&#345;ej &#352;er&#253;".equals( escapedText )
                  || "J&#225;nos Csirik (without a middle initial) is his father".equals( escapedText )
                  || "J&#225;nos A. Csirik (with a middle initial) is his son".equals( escapedText )
                  || "March 26, 1913 - September 20, 1996".equals( escapedText )
                  || "1938 - 14 November 2005".equals( escapedText )
                  || "born in  Budapest, Hungary, in 1936 / died on Dec. 17, 2005 in Moab, Utah".equals( escapedText )
                  || "(Formerly spelled &apos;Ofer Shtrichman&apos;)".equals( escapedText )
                  || "Philips Research, Eindhoven, The Netherlands".equals( escapedText )
                  || "STMicroelectronics, Catania, Italy".equals( escapedText )
                  || "Conference held 9-14 October, 1960, in Chicago, Illinois".equals( escapedText )
                  || "Sponsored by: Commission of the European Communities, Comisi&#243;n Interministral de Ciencia y Tecnolog&#237;a; Organized by: Universitat de les Illes Balears, Universidad Polit&#233;cnica de Madrid, Institut d&apos;Investigaci&#243; en Intelligencia Artificial".equals( escapedText )
                  || "Thomas Troels Hildebrandt".equals( escapedText )
                  || "Hewlett-Packard".equals( escapedText )
                  || "IBM Almaden Research Center".equals( escapedText )
                  || "IBM China Research Lab.".equals( escapedText )
                  || "IBM".equals( escapedText )
                  || "IBM Research".equals( escapedText )
                  || "Institute for Systems Biology, Seattle, WA, USA".equals( escapedText )
                  || "Konrad-Zuse-Zentrum f&#252;r Informationstechnik Berlin (ZIB)".equals( escapedText )
                  || "David Atienza Alonso".equals( escapedText )
                  || "Edinburgh, Australia".equals( escapedText )
                  || "Fraunhofer Gesellschaft, Institute for Secure Information Technology".equals( escapedText )
                  || "Honeywell".equals( escapedText )
                  || "Woodrow (&quot;Woody&quot;) W. Bledsoe".equals( escapedText )
                  || "Bioinformatics Institute, Singapore".equals( escapedText )
                  || "Erlangen-N&#252;rnberg / T-Systems".equals( escapedText )
                  || "LSR-IMAG, France".equals( escapedText )
                  || "Naval Research Laboratory, Washington, DC, USA".equals( escapedText )
                  || "Mar&#237;a Jos&#233; Carreira Nouche".equals( escapedText )
                  || "National Institute of Advanced Industrial Science and Technology, until 2006: University of Tokyo".equals( escapedText )
                  || "Paulo Jorge Esteves Ver&#237;ssimo".equals( escapedText )
                  || "Pedro Morillo Tena".equals( escapedText )
                  || "Ronald M. Baecker".equals( escapedText )
                  || "Jos&#233; Fonseca de Nuno Oliveira".equals( escapedText )
                  || "formerly: Nicole S&#226;ntean".equals( escapedText )
                  || "Irvine, CA, USA".equals( escapedText )
                  || "Microsoft Research Silicon Valley".equals( escapedText )
                  || "Microsoft Corporation".equals( escapedText ) ) {
                tagList.add( "" );  // that is, do nothing
              }
              else {
                System.err.println( "<--WHATISTHIS-note?" + localName + "=(" + escapedText + ")-->" );
              }; // if
            }; //if
          }; // if
        }
        else {
          // handle some exceptions (some improvements could be done here, such as dealing with companies)
          if( "Conference held 9-14 October, 1960, in Chicago, Illinois".equals( escapedText )
              || "IBM Research Report RJ 1333, San Jose, California".equals( escapedText )
              || "Abstract only".equals( escapedText )
              || "Invited talk".equals( escapedText )
              || "invited talk".equals( escapedText )
              || "To appear in TCS".equals( escapedText )
              || "panel".equals( escapedText )
              || "note this paper contains plagiarised material".equals( escapedText )
              || "experience/applicaton paper".equals( escapedText )
              || "invited paper, author of best impact paper of VLDB&apos;86".equals( escapedText )
              || "Sponsored by: Commission of the European Communities, Comisi&#243;n Interministral de Ciencia y Tecnolog&#237;a; Organized by: Universitat de les Illes Balears, Universidad Polit&#233;cnica de Madrid, Institut d&apos;Investigaci&#243; en Intelligencia Artificial".equals( escapedText ) ) {
            tagList.add( "" );  // that is, do nothing
          }
          else {
            System.err.println( "<--WHATISTHIS3?" + localName + "=(" + escapedText + ")-->" );
          }; // if
        }; // if
      }; // if
    }; // if

    // deal with 'series'
    if( SERIES.equals( localName ) && flag == true ) {
      if( text != null ) {
        if( "".equals( escapedText ) == false ) {
          String uri = null;
          if( hrefAttribute == null ) {
            ILiteral literal = seriesOntologyModel.getLiteral( text.trim() );
            if( literal != null ) {
              for( ILiteralStatement literalStatement : literal.getLiteralStatements() ) {
                uri = literalStatement.getSubject().getURI();
              }; // for
            }; // if
          }
          else {
            uri = DBLP + hrefAttribute;
          }; // if
          // verify if uri was found for the Series
          if( uri != null ) {
            tagList.add( "<" + OPUS_IN_SERIES + " rdf:resource=\"" + StringEscapeUtils.escapeHtml( uri ) + "\" />" );
            seriesFoundSet.add( uri );
          }
          else {
            System.err.println( "<--WHATISTHISSERIES?" + localName + "=(" + text.trim() + ")-->" );
          }; // if
        }; // if
      }; // if
    }; // if

    // deal with 'www'
    if( WWW.equals( localName ) && flag == true ) {
      String uri[] = null;
      String[] key = null;
      String authorUri = null; // this one is used as a flag (null/not null) to identify 'sameAs' authors

      tagList.add( "" );  // this line avoids the case of something like 'null</tag>'

      // first we get the uri (the URL), and remove the first found from the list
      for( int i = wwwList.size() - 1; i >= 0; i-- ) {
        uri = wwwList.get( i );
        if( URL.equals( uri[ 0 ] ) ) {
          wwwList.remove( i );
          break;
        }; // if
        uri = null;
      }; // for

      // now we get the 'key', and remove the first found from the list
      for( int i = wwwList.size() - 1; i >= 0; i-- ) {
        key = wwwList.get( i );
        if( KEY.equals( key[ 0 ] ) ) {
          wwwList.remove( i );
          break;
        }; // if
      }; // for

      // whenever either key or url is null, then do nothing
      if( key == null ) {
        System.err.println( " WHATISTHIS4?-->key=" + key + ", url=" + uri );
      }
      else {
        // there are two cases: (i) Personal Webpage; (ii) Some document's webpage

        // case1: Some document's webpage
        if( key[ 1 ].startsWith( HOMEPAGES ) ) {
          String authorName = "";
          if( uri == null ) {
            // there's no url link for webpage, yet need to get 'sameAs' relationships
            for( String[] each : wwwList ) {
              if( MDATE.equals( each[ 0 ] ) || TITLE.equals( each[ 0 ] ) ) {
                // do nothing (mdate: because the subject is the url, not the dblp-bibtex-element)
                // do nothing (title: because the homepage label is created with the author's name included)
              }
              else {
                if( AUTHOR.equals( each[ 0 ] ) ) {
                  List<String> tmpList = null;
                  // needed to figure out if we have a 'sameAs' case
                  if( authorUri == null ) {
                    authorUri = each[ 1 ];
                  }; // if
                  if( authorUri.equals( each[ 1 ] ) == false ) {
                    // this means we've got a 'sameAs' author uri
                    tmpList = authorUriToSameAsMap.get( authorUri );
                    if( tmpList == null ) {
                      tmpList = new ArrayList<String>();
                      authorUriToSameAsMap.put( authorUri, tmpList );
                    }; // if
                    tmpList.add( each[ 1 ] );
                  }; // if
                }
                else {
                  if( SCHOOL.equals( each[ 0 ] ) ) {
                    Set<String> tmpSet = authorUriToAffiliationMap.get( authorUri );
                    if( tmpSet == null ) {
                      tmpSet = new TreeSet<String>();
                      authorUriToAffiliationMap.put( authorUri, tmpSet );
                    }; // if
                    tmpSet.add( each[ 1 ] );
                  }
                  else {
                    if( each[ 0 ] != null ) {
                      tagList.add( " WHATISTHIS-SCHOOL - each[0]=" + each[ 0 ]
                        + ", each[1]=" + each[ 1 ] + " !" );
                    }; // if
                  }; // if
                }; // if
              }; // if
            }; // for
          }
          else {
            // there's a url link for webpage
            tagList.add( "<" + OPUS_WEBPAGE + " rdf:about=\"" + uri[ 1 ] + "\">" );
            authorUri = null;
            for( String[] each : wwwList ) {
              if( each[ 0 ] == null ) {
                // do nothing
                //(debugging)  tagList.add( " + " + each[ 1 ] );
              }
              else {
                if( MDATE.equals( each[ 0 ] ) || TITLE.equals( each[ 0 ] ) ) {
                  // do nothing (mdate: because the subject is the url, not the dblp-bibtex-element)
                  // do nothing (title: because the homepage label is created with the author's name included)
                }
                else {
                  if( URL.equals( each[ 0 ] ) ) {
                    if( authorsFlag == true ) {
                      authorsFlag = false;
                      tagList.add( " </rdf:Seq>" );
                      tagList.add( "</" + lastAuthorRelationship + ">" );
                    }; // if
                    // commented out because only one person has two url's, so there's no point
                    //tagList.add( "  <" + OWL_SAME_AS + "  rdf:resource=\"" + each[ 1 ] + "\" />" );
                  }
                  else {
                    if( AUTHOR.equals( each[ 0 ] ) ) {
                      List<String> tmpList = null;
                      Set<String> tmpSet = null;
                      // needed to figure out if we have a 'sameAs' case
                      if( authorUri == null ) {
                        authorUri = each[ 1 ];
                      }; // if
                      if( authorUri.equals( each[ 1 ] ) == false ) {
                        // this means we've got a 'sameAs' author uri
                        tmpList = authorUriToSameAsMap.get( authorUri );
                        if( tmpList == null ) {
                          tmpList = new ArrayList<String>();
                          authorUriToSameAsMap.put( authorUri, tmpList );
                        }; // if
                        tmpList.add( each[ 1 ] );
                      }; // if

                      authorName = authorUriToNameMap.get( authorUri );
                      tmpSet = authorUriToWebpageMap.get( authorUri );
                      if( tmpSet == null ) {
                        tmpSet = new TreeSet<String>();
                        authorUriToWebpageMap.put( authorUri, tmpSet );
                      }; // if
                      tmpSet.add( uri[ 1 ] );
                    }
                    else {
                      if( SCHOOL.equals( each[ 0 ] ) ) {
                        if( authorUri == null ) {
                          System.err.println( " WHATISTHIS(author==null)?-->school=" + each[ 0 ] + ", each[1]=" + each[ 1 ] );
                        }
                        else {
                          Set<String> tmpSet = authorUriToAffiliationMap.get( authorUri );
                          if( tmpSet == null ) {
                            tmpSet = new TreeSet<String>();
                            authorUriToAffiliationMap.put( authorUri, tmpSet );
                          }; // if
                          tmpSet.add( each[ 1 ] );
                        }; // if
                      }
                      else {
                        tagList.add( "  <" + identifiersMap.get( each[ 0 ] )
                            + ">" + each[ 1 ] + "</" + identifiersMap.get( each[ 0 ] ) + ">" );
                      }; // if
                    }; // if
                  }; // if
                }; // if
              }; // if
            }; // for
            tagList.add( "  <" + RDFS_LABEL + ">Home Page of " + authorName + "</" + RDFS_LABEL + ">" );
            tagList.add( "</" + OPUS_WEBPAGE + ">" );
          }; // if
        }; // if

        // case2: Some document's webpage
        if( key[ 1 ].startsWith( WWW_SLASH ) ) {
          tagList.add( "<" + FOAF_DOCUMENT + " rdf:about=\"" + uri[ 1 ] + "\">" );
          for( String[] each : wwwList ) {
            if( each[ 0 ] == null ) {
              tagList.add( "  " + each[ 1 ] );
            }
            else {
              if( MDATE.equals( each[ 0 ] ) ) {
                // do nothing (because te subject is the url, not the dblp-bibtex-element)
              }
              else {
                if( URL.equals( each[ 0 ] ) ) {
                  if( authorsFlag == true ) {
                    authorsFlag = false;
                    tagList.add( " </rdf:Seq>" );
                    tagList.add( "</" + lastAuthorRelationship + ">" );
                  }; // if
                  tagList.add( "  <" + OWL_SAME_AS + " rdf:resource=\"" + each[ 1 ] + "\" />" );
                }
                else {
                  if( EDITOR.equals( each[ 0 ] ) || AUTHOR.equals( each[ 0 ] ) ) {
                    tagList.add( "    <rdf:li rdf:resource=\"" + each[ 1 ] + "\" />" );
                  }
                  else {
                    if( tagExceptionsSet.contains( each[ 0 ] ) ) {
                      tagList.add( "  <" + identifiersMap.get( each[ 0 ] )
                          + " rdf:resource=\"" + each[ 1 ] + "\" />" );
                    }
                    else {
                      tagList.add( "  <" + identifiersMap.get( each[ 0 ] )
                          + ">" + each[ 1 ] + "</" + identifiersMap.get( each[ 0 ] ) + ">" );
                    }; // if
                  }; // if
                }; // if
              }; // if
            }; // if
          }; // for
          tagList.add( "</" + FOAF_DOCUMENT + ">" );
        }; // if

      }; // if

      // turn-off the wwwFlag
      wwwFlag = false;
    }; // if

    // change the flag to 'false' only if localName is a match
    flag = ARTICLE.equals( localName ) ? false : flag;
    flag = BOOK.equals( localName ) ? false : flag;
    flag = INCOLLECTION.equals( localName ) ? false : flag;
    flag = INPROCEEDINGS.equals( localName ) ? false : flag;
    flag = MASTERSTHESIS.equals( localName ) ? false : flag;
    flag = PHDTHESIS.equals( localName ) ? false : flag;
    flag = PROCEEDINGS.equals( localName ) ? false : flag;
    flag = WWW.equals( localName ) ? false : flag;

    String tmpValue = null;
    String mLine = null;

    // verify switch of value of flag
    if( previousFlag == true ) {

      if( tagList.isEmpty() ) {
        // output the mapped name, if any
        if( tagExceptionsSet.contains( currentTag ) ) {
          if( wwwFlag == false && "address".equals( mappedName ) == false ) {
            
            // added oct-07,2006
            if( authorsFlag == true ) {
              authorsFlag = false;
              System.out.println( " </rdf:Seq>" );
              System.out.println( "</" + lastAuthorRelationship + ">" );
            }; // if

            System.out.println( "</" + mappedName + ">" );
          }; // if
        }
        else {
          if( wwwFlag == true ) {
            wwwList.add( new String[] { localName, text } );
          }
          else {
            escapedText = escapedText == null ? "" : escapedText;
            // gotta verify this as easy hack to avoid things such as <sup>abc</sup>
            if( localName.equals( mappedName ) ) {
              // this is the rare case
              System.out.print( escapedText + StringEscapeUtils.escapeXml( "</" + mappedName + ">" ) );
            }
            else {
              // this is the common case
              System.out.println( escapedText + "</" + mappedName + ">" );
            }; // if

            // handle special case of 'month' to opus:gMonth
            if( MONTH.equals( localName ) == true ) {
              tmpValue = Util.getGMonth( escapedText );
              if( tmpValue != null ) {
                mLine = "<" + OPUS_GMONTH + " rdf:datatype=\"&xsd;gMonth\">" + tmpValue + "</" + OPUS_GMONTH + ">";
                System.out.println( mLine );
              }; // if
            }; // if

          }; // if
        }; // if
      }
      else {
        for( String tag : tagList ) {
          if( "".equals( tag ) == false ) {
            if( wwwFlag == true ) {
              wwwList.add( new String[] { null, tag } );
            }
            else {
              System.out.println( tag );
            }; // if
          }; // if
        }; // for

      }; // if
      if( flag == false ) {
        System.out.println();
      }; // if
    }; // if

    text = null;
  }; // endElement


  /**
   * Gets the schools found
   * @return  Returns the schools found
   */
  public Iterable<String> getSchoolsFound() {
    return schoolsFoundSet;
  }; // getSchoolsFound


  /**
   * Gets the publishers found
   * @return  Returns the publishers found
   */
  public Iterable<String> getPublishersFound() {
    return publishersFoundSet;
  }; // getPublihersFoundSet


  /**
   * Gets the series found
   * @return  Returns the series found
   */
  public Iterable<String> getSeriesFound() {
    return seriesFoundSet;
  }; // getSeriesFoundSet


  /**
   * Returns numbers of 'article' found in XML file
   * @return number of article found - int
   */
  public int getNumberArticle() {
    return numberArticle;
  }; // getNumberArticle


  /**
   * Returns numbers of 'inproceedings' found in XML file
   * @return number of inproceedings found - int
   */
  public int getNumberInProceedings() {
    return numberInProceedings;
  }; // getNumberInProceedings


  /**
   * Returns numbers of 'publisher' found in XML file
   * @return number of publisher found - int
   */
  public int getNumberPublisher() {
    return numberPublisher;
  }; // getNumberPublisher


  /**
   * Returns numbers of 'proceedings' found in XML file
   * @return number of proceedings found - int
   */
  public int getNumberProceedings() {
    return numberProceedings;
  }; // getNumberProceedings


  /**
   * Returns numbers of 'book' found in XML file
   * @return number of book found - int
   */
  public int getNumberBook() {
    return numberBook;
  }; // getNumberBook


  /**
   * Returns numbers of 'incollection' found in XML file
   * @return number of incollection found - int
   */
  public int getNumberInCollection() {
    return numberInCollection;
  }; // getNumberInCollection


  /**
   * Returns numbers of 'phdthesis' found in XML file
   * @return number of phd thesis found - int
   */
  public int getNumberPhdThesis() {
    return numberPhdThesis;
  }; // getNumberPhdThesis


  /**
   * Returns numbers of 'masterthesis' found in XML file
   * @return number of master thesis found - int
   */
  public int getNumberMasterThesis() {
    return numberMasterThesis;
  }; // getNumberMasterThesis


  /**
   * Returns numbers of 'www' found in XML file
   * @return number of www found - int
   */
  public int getNumberWww() {
    return numberWww;
  }; // getNumberWww


  /**
   * Outputs authors found
   * @param  tag  the tag to use
   */
  public void outputAuthors( String tag ) {
    String label = null;
    String affiliationUrl = null;
    Set<String> webpageSet = null;
    Set<String> schoolsSet = null;
    List<String> sameAsList = null;

    for( String uri : authorUriToNameMap.keySet() ) {
      label = authorUriToNameMap.get( uri );
      if( label == null ) {
        System.err.println( "[OutputHandler::outputAuthors] - No label exists for -->" + uri + "<--" );
      }
      else {
        System.out.println( "<" + tag + " rdf:about=\"" + uri + "\">" );
        System.out.println( "  <foaf:name>" + label + "</foaf:name>" );
        webpageSet = authorUriToWebpageMap.get( uri );
        if( webpageSet != null ) {
          for( String webpage : webpageSet ) {
            System.out.println( "  <" + FOAF_HOMEPAGE + " rdf:resource=\"" + webpage + "\" />" );
            // if possible, get the affiliation from the homepage information
            affiliationUrl = Util.normalizeWebpage( webpage );
            affiliationUrl = Util.getWebsiteBase( affiliationUrl, universitiesOntologyModel );
            if( affiliationUrl != null ) {
              System.out.println( "  <" + FOAF_WORKPLACE_HOMEPAGE +" rdf:resource=\"" + affiliationUrl + "\" />" );
              schoolsFoundSet.add( affiliationUrl );
            }; // if
          }; // for
        }; // if
        sameAsList = authorUriToSameAsMap.get( uri );
        if( sameAsList != null ) {
          for( String uri2 : sameAsList ) {
            System.out.println( "  <" + OWL_SAME_AS + " rdf:resource=\"" + uri2 + "\" />" );
          }; // for
        }; // if
        schoolsSet = authorUriToAffiliationMap.get( uri );
        if( schoolsSet != null ) {
          for( String uri2 : schoolsSet ) {
            System.out.println( "  <" + FOAF_WORKPLACE_HOMEPAGE + " rdf:resource=\"" + uri2 + "\" />" );
            schoolsFoundSet.add( uri2 );
          }; // for
        }; // if
        System.out.println( "</" + tag + ">" );
        System.out.println();
      }; // if
    }; // for
  }; // outputAuthors


  /**
   * Typical main method - for testing purposes only
   * @param  args  typical args parameter
   */
  public static void main( String[] args ) {

    // load the RDF with Universities, Schools, etc.
    IOntologyModel universitiesOntologyModel = new UgaOntologyModel();
    //universitiesOntologyModel.loadOntology( "file:///c://temp//Universities.rdf" );
    universitiesOntologyModel.loadOntology( "http://lsdis.cs.uga.edu/projects/semdis/swetodblp/october2006/2006-09-02__Universities__extracted.rdf" );
    universitiesOntologyModel.loadOntology( "http://lsdis.cs.uga.edu/projects/semdis/swetodblp/march2007/2007-03-01__Universities__extended.rdf" );

    // load the RDF with Publishers
    IOntologyModel publishersOntologyModel = new UgaOntologyModel();
    //publishersOntologyModel.loadOntology( "file:///c://temp//Publishers.rdf" );
    publishersOntologyModel.loadOntology( "http://lsdis.cs.uga.edu/projects/semdis/swetodblp/march2007/2007-03-01__Publishers.rdf" );
    //(debugging)UgaOntologyModel.outputOntology( publishersOntologyModel );

    // load the RDF with information about publication Series
    IOntologyModel seriesOntologyModel = new UgaOntologyModel();
    //seriesOntologyModel.loadOntology( "file:///c://temp//Series.rdf" );
    seriesOntologyModel.loadOntology( "http://lsdis.cs.uga.edu/projects/semdis/swetodblp/march2007/2007-03-01__Series.rdf" );

    SwetoDblp swetoDblp = new SwetoDblp( null, universitiesOntologyModel, publishersOntologyModel, seriesOntologyModel );

    /* this code is optional for output of found series, universities, and/or publishers
    // various flags to output content
    boolean outputSchoolsFlag = true;
    boolean outputPublishersFlag = true;
    boolean outputSeriesFlag = true;
    */

    // Create the parser
    CreateParser parser = new CreateParser( swetoDblp );

    File file = new File( "data" + File.separator + "dblp.xml" );
    InputStream inputStream = null;

    System.out.println( "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" );
    System.out.println( "<!DOCTYPE rdf:RDF [<!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\">]>" );
    System.out.println( "" );
    System.out.println( "<rdf:RDF" );
    System.out.println( "  xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"" );
    System.out.println( "  xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"" );
    System.out.println( "  xmlns:dc=\"http://purl.org/dc/elements/1.1/\"" );
    System.out.println( "  xmlns:foaf=\"http://xmlns.com/foaf/0.1/\"" );
    System.out.println( "  xmlns:opus=\"http://lsdis.cs.uga.edu/projects/semdis/opus#\"" );
    System.out.println( "  xmlns:owl=\"http://www.w3.org/2002/07/owl#\"" );
    System.out.println( "  xml:base=\"http://lsdis.cs.uga.edu/projects/semdis/opus#\" >" );
    System.out.println( "" );

    try {
      inputStream = new FileInputStream( file );
    }
    catch( Exception e ) {
      System.err.println( "Exception e = " + e );
      e.printStackTrace();
      return;
    }; // try

    // Parse the XML file, handler generates the output
    parser.parse( inputStream );

    swetoDblp.outputAuthors( "foaf:Person" );
    System.out.println( "" );

    /* this code is optional for output of found universities with
       their label, for now, such output is not produced.
    int universitiesCount = 0;
    if( outputSchoolsFlag == true ) {
      universitiesCount = Util.outputInstanceNodes( swetoDblp.getSchoolsFound(), "opus:University", universitiesOntologyModel );
      System.out.println();
    }; // if
    */

    /* this code is optional for output of found publishers with
       their label, for now, such output is not produced.
    if( outputPublishersFlag == true ) {
      Util.outputInstanceNodes( swetoDblp.getPublishersFound(), "opus:Publishing_Company", publishersOntologyModel );
      System.out.println();
    }; // if
    */

    /* this code is optional for output of found series with
       their label, for now, such output is not produced.
    if( outputSeriesFlag == true ) {
      Util.outputInstanceNodes( swetoDblp.getSeriesFound(), "opus:Series", seriesOntologyModel );
      System.out.println();
    }; // if
    */

    System.out.println( "</rdf:RDF>" );

    /*
    System.out.println( "" );
    System.out.println( "Articles:      " + swetoDblp.getNumberArticle() );
    System.out.println( "Books:         " + swetoDblp.getNumberBook() );
    System.out.println( "InCollection:  " + swetoDblp.getNumberInCollection() );
    System.out.println( "InProceedings: " + swetoDblp.getNumberInProceedings() );
    System.out.println( "PhdThesis:     " + swetoDblp.getNumberPhdThesis() );
    System.out.println( "Publishers:    " + swetoDblp.getNumberPublisher() );
    System.out.println( "WWW:           " + swetoDblp.getNumberWww() );
    */

  }; // main


}; // class SwetoDblp

