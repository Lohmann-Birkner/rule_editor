package de.checkpoint.server.xml;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.w3c.dom.*;

public class XMLDOMWriter
{
   private static  String
   PRINTWRITER_ENCODING = "UTF-8";
   private static String EXTERN_DTD;
   private static String MIME2JAVA_ENCODINGS[] =
    { "Default", "UTF-8", "US-ASCII", "ISO-8859-1", "ISO-8859-2", "ISO-8859-3", "ISO-8859-4",
      "ISO-8859-5", "ISO-8859-6", "ISO-8859-7", "ISO-8859-8", "ISO-8859-9", "ISO-2022-JP",
      "SHIFT_JIS", "EUC-JP","GB2312", "BIG5", "EUC-KR", "ISO-2022-KR", "KOI8-R", "EBCDIC-CP-US",
      "EBCDIC-CP-CA", "EBCDIC-CP-NL", "EBCDIC-CP-DK", "EBCDIC-CP-NO", "EBCDIC-CP-FI", "EBCDIC-CP-SE",
      "EBCDIC-CP-IT", "EBCDIC-CP-ES", "EBCDIC-CP-GB", "EBCDIC-CP-FR", "EBCDIC-CP-AR1",
      "EBCDIC-CP-HE", "EBCDIC-CP-CH", "EBCDIC-CP-ROECE","EBCDIC-CP-YU",
      "EBCDIC-CP-IS", "EBCDIC-CP-AR2", "UTF-16"
    };


   protected PrintWriter out;

   protected boolean canonical;

   public XMLDOMWriter(String encoding, boolean canonical)
   throws UnsupportedEncodingException {
      // Default fuer out ist Standard-Out
      out = new PrintWriter(new OutputStreamWriter(System.out, encoding));
      this.canonical = canonical;
   } // <init>(String,boolean)

   //
   // Constructors
   //

   /** Default constructor. */
   public XMLDOMWriter(boolean canonical) throws UnsupportedEncodingException {
      this( getWriterEncoding(), canonical);
   }

   public static String getWriterEncoding( ) {
      return (PRINTWRITER_ENCODING);
   }// getWriterEncoding

   public static void  setWriterEncoding( String encoding ) {
      if( encoding.equalsIgnoreCase( "DEFAULT" ) )
         PRINTWRITER_ENCODING  = "UTF-8";
      else if( encoding.equalsIgnoreCase( "UTF-16" ) )
         PRINTWRITER_ENCODING  = "Unicode";
      /*else
         PRINTWRITER_ENCODING = MIME2Java.convert( encoding );*/
   }// setWriterEncoding

   public static boolean isValidJavaEncoding( String encoding ) {
      for ( int i = 0; i < MIME2JAVA_ENCODINGS.length; i++ )
         if ( encoding.equals( MIME2JAVA_ENCODINGS[i] ) )
            return (true);

      return (false);
   }// isValidJavaEncoding

   /** Prints the resulting document tree. */
   public static void print(Document document, PrintWriter printer, boolean canonical )
   {
      try {
         XMLDOMWriter writer = new XMLDOMWriter(canonical);
         writer.setPrintWriter(printer);
         writer.print(document, 0);
      } catch ( Exception e ) {
         e.printStackTrace(System.err);
      }

   } // print(String,String,boolean)

   public void setPrintWriter(PrintWriter writer)
   {
        out = writer;
   }

   public void print(Node node, int level) {
      // is there anything to do?
      if ( node == null ) {
         return;
      }

      // Einruecken
      int type = node.getNodeType();
      switch ( type ) {
         // print document
         case Node.DOCUMENT_NODE: {
               if ( !canonical ) {
                  String  Encoding = this.getWriterEncoding();
                  if( Encoding.equalsIgnoreCase( "DEFAULT" ) )
                     Encoding = "UTF-8";
                  else if( Encoding.equalsIgnoreCase( "Unicode" ) )
                     Encoding = "UTF-16";
                  /*else
                     Encoding = MIME2Java.reverse( Encoding );*/

                  out.println("<?xml version=\"1.0\" encoding=\""+
                           Encoding + "\"?>");
               }

               DocumentType docType = ((Document) node).getDoctype();
               if (docType != null)
               {
                  /*if (docType.getSystemId() != null)
                  {
                      out.println("<!DOCTYPE " + docType.getName() + " SYSTEM \""
                                    + docType.getSystemId() + "\">");
                  }
                  else if (docType.getPublicId() != null)
                  {
                      out.println("<!DOCTYPE " + docType.getName() + " PUBLIC \""
                                    + docType.getPublicId() + "\">");
                  }*/
               }

               print(((Document)node).getDocumentElement(), level + 1);
               out.flush();
               break;
            }

            // print element with attributes
         case Node.ELEMENT_NODE: {
              out.print("\n");
              //for (int i = 0; i < level; i++)
              //  out.print("  ");
              out.print('<');
              out.print(node.getNodeName());
              Attr attrs[] = sortAttributes(node.getAttributes());
              for ( int i = 0; i < attrs.length; i++ )
              {
                  Attr attr = attrs[i];
                  out.print(' ');
                  out.print(attr.getNodeName());
                  out.print("=\"");
                  out.print(normalize(attr.getNodeValue()));
                  out.print('"');
              }
              NodeList children = node.getChildNodes();
              int len = children == null ? 0 : children.getLength();
              if (len == 0)
              {
                  out.print("/>");
              }
              else
              {
                  out.print(">");
                  for ( int i = 0; i < len; i++ ) {
                      print(children.item(i), level + 1);
                  }
                 out.print("</");
                 out.print(node.getNodeName());
                 out.print('>');
              }
              break;
            }
         case Node.ENTITY_REFERENCE_NODE: {
              //out.print("\n");
              for (int i = 0; i < level; i++)
                out.print("  ");
               if ( canonical ) {
                  NodeList children = node.getChildNodes();
                  if ( children != null ) {
                     int len = children.getLength();
                     for ( int i = 0; i < len; i++ ) {
                        print(children.item(i), level + 1);
                     }
                  }
               } else {
                  out.print('&');
                  out.print(node.getNodeName());
                  out.print(';');
               }
               break;
            }
         case Node.CDATA_SECTION_NODE: {
               if ( canonical ) {
                  out.print(normalize(node.getNodeValue()));
               } else {
                  out.print("<![CDATA[");
                  out.print(node.getNodeValue());
                  out.print("]]>");
               }
               break;
            }

         case Node.COMMENT_NODE: {
              //out.print("\n");
              for (int i = 0; i < level; i++)
                out.print("  ");
               if ( canonical ) {
                  out.print(normalize(node.getNodeValue()));
               } else {
                  out.print("<!--");
                  out.print(node.getNodeValue());
                  out.print("-->");
               }
               break;
            }

            // print text
         case Node.TEXT_NODE: {
               if (node.getNodeValue().trim().length() > 0)
                   out.print(normalize(node.getNodeValue()));
               break;
            }

            // print processing instruction
         case Node.PROCESSING_INSTRUCTION_NODE: {
              //out.print("\n");
              for (int i = 0; i < level; i++)
                out.print("  ");
               out.print("<?");
               out.print(node.getNodeName());
               String data = node.getNodeValue();
               if ( data != null && data.length() > 0 ) {
                  out.print(' ');
                  out.print(data);
               }
               out.print("?>");
               break;
            }
      }
      out.flush();
   } // print(Node)

   protected Attr[] sortAttributes(NamedNodeMap attrs) {

      int len = (attrs != null) ? attrs.getLength() : 0;
      Attr array[] = new Attr[len];
      for ( int i = 0; i < len; i++ ) {
         array[i] = (Attr)attrs.item(i);
      }
      for ( int i = 0; i < len - 1; i++ ) {
         String name  = array[i].getNodeName();
         int    index = i;
         for ( int j = i + 1; j < len; j++ ) {
            String curName = array[j].getNodeName();
            if ( curName.compareTo(name) < 0 ) {
               name  = curName;
               index = j;
            }
         }
         if ( index != i ) {
            Attr temp    = array[i];
            array[i]     = array[index];
            array[index] = temp;
         }
      }

      return (array);

   } // sortAttributes(NamedNodeMap):Attr[]

   protected String normalize(String s) {
     /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Potenzielle NP-Exception, weil s != null nicht abgefragt wird.  */
        if (s.startsWith("<![CDATA["))
            return s;
      StringBuffer str = new StringBuffer();
      int len = (s != null) ? s.length() : 0;
      for ( int i = 0; i < len; i++ ) {
         char ch = s.charAt(i);
         switch ( ch ) {
            case '<': {
//                  if (i > len - 11 || !s.substring(i, i + 9).equals("<![CDATA["))
                      str.append("&lt;");
//                  else
//                      str.append(ch);
                  break;
               }
            case '>': {
//                  if (i < 4 || !s.substring(i - 2, i + 1).equals("]]>"))
                      str.append("&gt;");
//                  else
//                      str.append(ch);
//                  break;
               }
            case '&': {
                  str.append("&amp;");
                  break;
               }
            case '"': {
                  str.append("&quot;");
                  break;
               }
            case '\r':
            case '\n': {
                  if ( canonical ) {
                     str.append("&#"+Integer.toString(ch)+";");
                     break;
                  }
                  // else, default append char
               }
            default: {
				  try {
					  str.append(ch);
				  } catch(Exception e) {}
               }
         }
      }

      return (str.toString());

   } // normalize(String):String

   private static void printValidJavaEncoding() {
      System.err.println( "    ENCODINGS:" );
      System.err.print( "   " );
      for( int i = 0;
                     i < MIME2JAVA_ENCODINGS.length; i++) {
         System.err.print( MIME2JAVA_ENCODINGS[i] + " " );
      if( (i % 7 ) == 0 ){
         System.err.println();
         System.err.print( "   " );
         }
      }
   } // printJavaEncoding()

   public static String getExternDTD( ) {
      return (EXTERN_DTD);
   }

   public static void  setExternDTD( String PathDTD ) {
        EXTERN_DTD = PathDTD;
   }// setWriterEncoding
}

