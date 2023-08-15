package parser.xml;

import domain.CD;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlStreamParserExample {
    public static void main(String[] args) {
        XmlStreamParserExample example = new XmlStreamParserExample();

        if (args.length == 1) {
            try {
                List<CD> cds = example.parse(new File(args[0]));
                for (CD cd : cds) {
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
        System.out.println("USAGE: java XmlStreamParserExample inputFilePath");
    }

    private List<CD> parse(File file) throws IOException, XMLStreamException {
        List<CD> cds = new ArrayList<>();

        try(FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(bufferedReader);

            String title = null;
            String artist = null;
            String country = null;
            String company = null;
            float price = 0.0f;
            int year = 0;

            while(xmlEventReader.hasNext()) {
                XMLEvent event = xmlEventReader.nextEvent();

                if(event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    if (startElement.getName().getLocalPart().equalsIgnoreCase("title")) {
                        event = xmlEventReader.nextEvent();
                        title = event.asCharacters().getData();
                    } else if (startElement.getName().getLocalPart().equalsIgnoreCase("artist")) {
                        event = xmlEventReader.nextEvent();
                        artist = event.asCharacters().getData();
                    } else if (startElement.getName().getLocalPart().equalsIgnoreCase("country")) {
                        event = xmlEventReader.nextEvent();
                        country = event.asCharacters().getData();
                    } else if (startElement.getName().getLocalPart().equalsIgnoreCase("company")) {
                        event = xmlEventReader.nextEvent();
                        company = event.asCharacters().getData();
                    } else if (startElement.getName().getLocalPart().equalsIgnoreCase("price")) {
                        event = xmlEventReader.nextEvent();
                        price = Float.parseFloat(event.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equalsIgnoreCase("year")) {
                        event = xmlEventReader.nextEvent();
                        year = Integer.parseInt(event.asCharacters().getData());
                    }
                }

                if(event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if(endElement.getName().getLocalPart().equalsIgnoreCase("cd")) {
                        cds.add(new CD(title, artist, country, company, price, year));

                        title = null;
                        artist = null;
                        country = null;
                        company = null;
                        price = 0.0f;
                        year = 0;
                    }
                }
            }
        }

        return cds;
    }
}