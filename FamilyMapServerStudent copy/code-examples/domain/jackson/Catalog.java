package domain.jackson;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "CATALOG")
public class Catalog {

    @JacksonXmlElementWrapper(localName = "CD", useWrapping = false)
    @JacksonXmlProperty(localName = "CD")
    private List<CD> cds;

    // Used for deserializtion
    public Catalog() {
    }

    // Used to populate the catalog for serialization
    public Catalog(List<CD> cds) {
        this.cds = cds;
    }

    public List<CD> getCds() {
        return cds;
    }
}
