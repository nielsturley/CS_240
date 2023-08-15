package generator.xml;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import domain.jackson.CDFactory;
import domain.jackson.Catalog;

import java.io.File;
import java.io.IOException;

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
public class XmlObjectSerializationExample {

    public static void main(String [] args) {
        XmlObjectSerializationExample example = new XmlObjectSerializationExample();

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
        System.out.println("USAGE: java XmlObjectSerializationExample outputFilePath");
    }

    private void generate(Catalog catalog, File file) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(file, catalog);

        // Re-writing so I can print it to the console. The above code is all that is necessary to generate the xml file.
        java.io.StringWriter stringWriter = new java.io.StringWriter();
        mapper.writeValue(stringWriter, catalog);
        System.out.println(stringWriter.toString());
    }
}
