package domain.jackson;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CD {

    @JacksonXmlProperty(localName = "TITLE")
    private String title;

    @JacksonXmlProperty(localName = "ARTIST")
    private String artist;

    @JacksonXmlProperty(localName = "COUNTRY")
    private String country;

    @JacksonXmlProperty(localName = "COMPANY")
    private String company;

    @JacksonXmlProperty(localName = "PRICE")
    private float price;

    @JacksonXmlProperty(localName = "YEAR")
    private int year;

    public CD() {
    }

    public CD(String title, String artist, String country, String company, float price, int year) {
        this.title = title;
        this.artist = artist;
        this.country = country;
        this.company = company;
        this.price = price;
        this.year = year;
    }

    @Override
    public String toString() {
        return "CD{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", country='" + country + '\'' +
                ", company='" + company + '\'' +
                ", price=" + price +
                ", year=" + year +
                '}';
    }
}
