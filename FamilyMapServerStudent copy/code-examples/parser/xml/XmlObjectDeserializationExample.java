package parser.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import domain.jackson.CD;
import domain.jackson.Catalog;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Requires the jackson library (.jar file). Get the .jar from Maven or Gradle:
 *
 * Maven
 *
 * <dependency>
 *     <groupId>com.fasterxml.jackson.dataformat</groupId>
 *     <artifactId>jackson-dataformat-xml</artifactId>
 *     <version>2.9.8</version>
 * </dependency>
 *
 * Gradle
 *
 * compile group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-xml', version: '2.9.8'
 *
 */
public class XmlObjectDeserializationExample {

    public static void main(String [] args) {
        XmlObjectDeserializationExample example = new XmlObjectDeserializationExample();

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
        System.out.println("USAGE: java XmlObjectDeserializationExample inputFilePath");
    }

    private List<CD> parse(File file) throws IOException, XMLStreamException {
        // Does not implement AutoCloseable, so we must close in a finally block
        XMLStreamReader xmlStreamReader = null;

        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            XMLInputFactory factory = XMLInputFactory.newInstance();
            xmlStreamReader = factory.createXMLStreamReader(bufferedReader);

            XmlMapper xmlMapper = new XmlMapper();
            Catalog catalog = xmlMapper.readValue(xmlStreamReader, Catalog.class);
            return catalog.getCds();
        } finally {
            if (xmlStreamReader != null) {
                xmlStreamReader.close();
            }
        }
    }
}
