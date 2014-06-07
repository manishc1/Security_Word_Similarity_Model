package sweto;

import org.apache.commons.lang.StringEscapeUtils;

import edu.uga.cs.lsdis.semdisds.IInstanceNode;
import edu.uga.cs.lsdis.semdisds.ILiteralStatement;
import edu.uga.cs.lsdis.semdisds.IOntologyModel;
import edu.uga.cs.lsdis.semdisds.ISchemaClass;
import edu.uga.cs.lsdis.semdisds.IURI;


/**
 * <p>Util contains few 'utility' methods</p>
 * 
 * @author  <a href="http://lsdis.cs.uga.edu/~aleman/">Boanerges Aleman-Meza</a>
 * 
 * @version  0.1  - First release
 * 
 */


public class Util {

    
  /**
   * Converts a person name into dblp-like url ending
   * @param  name  the person's name (non-escaped)
   * @return  a url ending in [0], and the name in [1] (Html-escaped)
   */
  public static String[] getDblpUrlEnding( String name ) {
    if( name == null ) {
      return null;
    }; // if
    
    name = StringEscapeUtils.escapeHtml( name );
     
    String modifiedName = null;
    String retName = name;
    
    // get the index of the last space in the name
    int index = name.lastIndexOf( " " );

    if( index == -1 ) {
      modifiedName = name;
    }
    else {
      String lastName = name.substring( index + 1, name.length() );
      Integer number = null;
      try {
        number = new Integer( lastName );
      }
      catch( Exception e ) {
        number = null;
      }; // try
      if( number == null ) {
        modifiedName = lastName.substring( 0, 1 ).toLowerCase() + "/" + lastName + ":" + name.substring( 0, index );
      }
      else {
        //System.err.println( "HERE lastName: -->" + lastName + "<-- number: " + number );
        int index2 = name.lastIndexOf( " ", index - 1 );
        lastName = name.substring( index2 + 1, name.length() );
        modifiedName = lastName.substring( 0, 1 ).toLowerCase() + "/" + lastName + ":" + name.substring( 0, index2 );
        retName = name.substring( 0, index );
        //System.err.println( "HERE lastName: " + lastName + ", modifiedName: -->" + modifiedName + "<--" );
        //System.err.println( "HERE label: -->" + name.substring( 0, index ) + "<--" );
      }; // if
    }; // if
    
    modifiedName = modifiedName.replace( ' ', '_' );
    modifiedName = modifiedName.replace( '.', '=' );
    modifiedName = modifiedName.replace( '-', '=' );
    modifiedName = modifiedName.replace( '&', '=' );
    modifiedName = modifiedName.replace( ';', '=' );
    modifiedName = modifiedName.replace( '\'', '=' );
    
    return new String[] { modifiedName, 
        StringEscapeUtils.escapeXml( StringEscapeUtils.unescapeHtml( retName ) ) };
  }; // getDblpUrlEnding
    
  
  /**
   * Gets the base part of a website; should be used after doing SwetoDblp.normalize on the url
   * @param  url  the url of the website
   * @param  universitiesOntologyModel  the universities ontology model
   * @return  the base part of a website, if it exists in the universitiesOntologyModel, null otherwise
   */
  public static String getWebsiteBase( String url, IOntologyModel universitiesOntologyModel ) {
    if( url == null ) {
      return null;
    }; // if
    
    // the url would have to be at least 8 chars long (because it would have "http://" or "https://")
    int httpPosition = url.startsWith( "http://" ) ? 7 : -1;
    httpPosition = url.startsWith( "https://" ) ? 8 : httpPosition;
    if( httpPosition == -1 ) {
      System.out.println( "WHATISTHIS--url(1),getWebsiteBase: " + url );
      return null;
    }; // if
    
    int slashPosition = url.indexOf( "/", httpPosition );
    if( slashPosition == -1 ) {
      if( url.endsWith( "/" ) == false ) { 
        url += "/";
      }; // if
      slashPosition = url.indexOf( "/", httpPosition );
      if( slashPosition == -1 ) {
        System.out.println( "WHATISTHIS--url(2),getWebsiteBase: " + url );
        return null;
      }; // if
    }; // if
    
    int pos = 0;
    String ret = url.substring( 0, slashPosition );
    String portEnding[] = { ":8080", ":8000" };
    IInstanceNode instanceNode = null;
      
    // fix any url with useless port ending
    for( String port : portEnding ) {
      if( ret.endsWith( port ) ) {
        ret = ret.substring( 0, ret.length() - port.length() );
      }; // if
    }; // for
    
    // handle ".edu" ending
    String eduCases[] = { ".edu", ".edu.ar", ".edu.au", ".edu.br", ".edu.cn", ".edu.eg", 
        ".edu.hk", ".edu.kw", ".edu.lb", ".edu.my", 
        ".edu.ng", ".edu.om", ".edu.pl", ".edu.sg", ".edu.tr", ".edu.tw", ".edu.uy", 
        
        ".ac.ae", ".ac.at", ".ac.bd", ".ac.be", ".ac.cn", ".ac.cr", ".ac.cy", 
        ".ac.il", ".ac.in", ".ac.jp", ".ac.kr", ".ac.nz", ".ac.ru", ".ac.th", ".ac.uk", ".ac.yu", ".ac.za" };
    
    // search for possible 'edu/ac' match
    for( String edu : eduCases ) {
      if( ret.endsWith( edu ) ) {
        pos = ret.lastIndexOf( ".", ret.length() - edu.length() - 1 );
        pos = pos == -1 ? httpPosition : pos + 1;  // for cases such as http://uga.edu/~ana
        ret = "http://www." + ret.substring( pos ) + "/";
        
        // now, look it up in the universities
        instanceNode = universitiesOntologyModel.getInstanceNode( ret );
        if( instanceNode != null ) {
          return ret;
        }; // if
        //TODO
        //System.out.println( "WHATISTHIS--url(3),getWebsiteBase: " + ret );
        return null;
      }; // if
    }; // for
    
    // maybe there's a match on universitiesOntology
    String pieces[] = ret.substring( httpPosition ).split( "\\." );
    if( pieces != null && pieces.length >= 2 ) {
      String tmp = "http://www." + pieces[ pieces.length - 2 ] + "." + pieces[ pieces.length - 1 ] + "/";
      instanceNode = universitiesOntologyModel.getInstanceNode( tmp );
      if( instanceNode != null ) {
        return tmp;
      }; // if
    }; // if
    
    //TODO
    //System.out.println( "WHATISTHIS--url(4),getWebsiteBase: " + ret );
    return null;
  }; // getWebsiteBase
  
  /**
   * Normalizes (fixes) a webpage url
   * @param  url  the url
   * @return  the fixed url (mostly it takes care of typos)
   */
  public static String normalizeWebpage( String url ) {
    if( url == null ) {
      return null;
    }; // if
    if( url.startsWith( "ttp://" ) ) {
      return "h" + url;
    }; // if
    if( url.startsWith( "www." ) ) {
      return "http://" + url;
    }; // if
    return url;
  }; // normalizeWebpage
  
  
  /**
   * Outputs RDF of specific instance nodes (uri's), just the rdfs:label, if it is there 
   * @param  uriIterable  the iterable, if null, it does nothing
   * @param  tag  the tag to use in output; if null, then it uses tag from ontologyModel
   * @param  ontologyModel  the ontologyModel
   * @return  the numbr of instances output
   */
  public static int outputInstanceNodes( Iterable<String> uriIterable, String tag, IOntologyModel ontologyModel ) {
    if( uriIterable == null || ontologyModel == null ) {
      return 0;
    }; // if
    
    int ret = 0;   
    String label = null;
    String actualTag = null;
    IInstanceNode instanceNode = null;
    
    for( String uri : uriIterable ) {
      instanceNode = ontologyModel.getInstanceNode( uri );
      if( instanceNode != null ) {
        // get the value for rdfs:label
        for( ILiteralStatement literalStatement : instanceNode.getLiterals() ) {
          if( IURI.RDFS_LABEL.equals( literalStatement.getPredicate().getURI() ) ) {
            label = literalStatement.getObject().getValue();
          }; // if
        }; // for
        // if needed, get the tag (i.e., type of this instance)
        if( tag == null ) {
          for( ISchemaClass schemaClass : instanceNode.getTypes() ) {
            actualTag = schemaClass.getShortName();
          }; // if
        }
        else {
          actualTag = tag;
        }; // if
        System.out.println( "<" + actualTag + " rdf:about=\"" + StringEscapeUtils.escapeHtml( uri ) + "\">" );
        ret++;
        if( label != null ) {
          System.out.println( "  <rdfs:label>" + StringEscapeUtils.escapeXml( label ) + "</rdfs:label>" );
        }; // if
        System.out.println( "</" + actualTag + ">" );
      }
      else {
        System.out.println( "[WHATISTHIS-NotFound:" + actualTag + " " + uri );
      }; // if
    }; // for
    return ret;
  }; // outputInstanceNodes

      
  /**
   * Returns a numeric gMonth value of a parameter, or null if none matched
   * @param  month  the month
   * @return  the numeric value, 01 for January, 02, for February, etc, or null if none did match
   */
  public static String getGMonth( String month ) {
    String ret = null;
    ret = "January".equals( month ) == true ?   "01" : ret;
    ret = "February".equals( month ) == true ?  "02" : ret;
    ret = "March".equals( month ) == true ?     "03" : ret;
    ret = "April".equals( month ) == true ?     "04" : ret;
    ret = "May".equals( month ) == true ?       "05" : ret;
    ret = "June".equals( month ) == true ?      "06" : ret;
    ret = "July".equals( month ) == true ?      "07" : ret;
    ret = "August".equals( month ) == true ?    "08" : ret;
    ret = "September".equals( month ) == true ? "09" : ret;
    ret = "October".equals( month ) == true ?   "10" : ret;
    ret = "November".equals( month ) == true ?  "11" : ret;
    ret = "December".equals( month ) == true ?  "12" : ret;
    return ret;
  }; // getGMonth


}; // class Util

