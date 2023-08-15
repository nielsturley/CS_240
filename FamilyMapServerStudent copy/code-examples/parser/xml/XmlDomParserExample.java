package parser.xml;

import domain.CD;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlDomParserExample {

    public static void main(String [] args) {
        XmlDomParserExample example = new XmlDomParserExample();

        if(args.length == 1) {
            try {
                List<CD> cds = example.parse(new File(args[0]));
                for(CD cd : cds) {
                    System.out.println(cd);
                }
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                System.out.printf("Unable to parse file %s because of exception: %s\n", args[0], ex.toString());
            }
        } else {
            example.printUsage();
        }
    }

    private void printUsage() {
        System.out.println("USAGE: java XmlDomParserExample inputFilePath");
    }

    private List<CD> parse(File file) throws IOException, SAXException, ParserConfigurationException {
        List<CD> cds = new ArrayList<>();

        DocumentBuilder builder = DocumentBuilderFactory.
                newInstance().newDocumentBuilder();
        Document doc = builder.parse(file);

        NodeList cdList = doc.getElementsByTagName("CD");
        for (int i = 0; i < cdList.getLength(); ++i) {

            Element cdElem = (Element) cdList.item(i);

            Element titleElement = (Element) cdElem.getElementsByTagName("TITLE").item(0);
            Element artistElement = (Element) cdElem.getElementsByTagName("ARTIST").item(0);
            Element countryElement = (Element) cdElem.getElementsByTagName("COUNTRY").item(0);
            Element companyElement = (Element) cdElem.getElementsByTagName("COMPANY").item(0);
            Element priceElement = (Element) cdElem.getElementsByTagName("PRICE").item(0);
            Element yearElement = (Element) cdElem.getElementsByTagName("YEAR").item(0);

            String title = titleElement.getTextContent();
            String artist = artistElement.getTextContent();
            String country = countryElement.getTextContent();
            String company = companyElement.getTextContent();
            float price = Float.parseFloat(priceElement.getTextContent());
            int year = Integer.parseInt(yearElement.getTextContent());

            cds.add(new CD(title, artist, country, company, price, year));
        }

        return cds;
    }
}
