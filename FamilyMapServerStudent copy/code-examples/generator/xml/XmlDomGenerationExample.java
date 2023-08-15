package generator.xml;

import domain.CD;
import domain.CDFactory;
import domain.Catalog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class XmlDomGenerationExample {

    public static void main(String [] args) {
        XmlDomGenerationExample example = new XmlDomGenerationExample();

        if(args.length == 1) {
            try {
                Catalog catalog = new Catalog(CDFactory.getCDs());
                example.generate(catalog, new File(args[0]));
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                System.out.printf("Unable to generate file %s because of exception: %s\n", args[0], ex.toString());
            }
        } else {
            example.printUsage();
        }
    }

    private void printUsage() {
        System.out.println("USAGE: java XmlDomGenerationExample outputFilePath");
    }

    private void generate(Catalog catalog, File file) throws IOException, ParserConfigurationException, TransformerException {
        try(FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();
            doc.appendChild( buildCatalog(catalog, doc) );

            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(doc), new StreamResult(bufferedWriter));


            // Re-writing so I can print it to the console. The above code is all that is necessary to generate the xml file.
            java.io.StringWriter stringWriter = new java.io.StringWriter();
            t.transform(new DOMSource(doc), new StreamResult(stringWriter));
            System.out.println(stringWriter);
        }
    }

    private static Element buildCatalog(Catalog catalog, Document doc) {
        Element catalogElement = doc.createElement("CATALOG");

        for (CD cd : catalog.getCds()) {
            catalogElement.appendChild( buildCD(cd, doc) );
        }

        return catalogElement;
    }

    private static Element buildCD(CD cd, Document doc) {
        Element cdElem = doc.createElement("CD");

        cdElem.appendChild( buildTextElem("TITLE", cd.getTitle(), doc) );
        cdElem.appendChild( buildTextElem("ARTIST", cd.getArtist(), doc) );
        cdElem.appendChild( buildTextElem("COUNTRY", cd.getCountry(), doc) );
        cdElem.appendChild( buildTextElem("COMPANY", cd.getCompany(), doc) );
        cdElem.appendChild( buildTextElem("PRICE", Float.toString(cd.getPrice()), doc) );
        cdElem.appendChild( buildTextElem("YEAR", Integer.toString(cd.getYear()), doc) );

        return cdElem;
    }

    private static Element buildTextElem(String elementName, String elementText, Document doc) {
        Element textElem = doc.createElement(elementName);
        textElem.appendChild(doc.createTextNode(elementText));
        return textElem;
    }
}
