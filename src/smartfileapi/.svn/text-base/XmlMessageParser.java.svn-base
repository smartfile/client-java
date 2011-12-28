package smartfileapi;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

// A quick and dirty SAX XML parser to get the API response
// message when an error occurs.
public class XmlMessageParser extends DefaultHandler {
    private boolean inMessage = false;
    private String message = null;

    public XmlMessageParser()
    {
	super();
    }

    public String getMessage()
    {
        return this.message;
    }

    @Override
    public void startElement(String uri, String name,
			     String qName, Attributes atts) {
        if (name.equals("message"))
            this.inMessage = true;
    }

    @Override
    public void endElement(String uri, String name, String qName) {
        if (this.inMessage && name.equals("message"))
            this.inMessage = false;
    }

    @Override
    public void characters(char ch[], int start, int length) {
        if (!this.inMessage)
            return;
        this.message = new String(ch, start, length);
    }
}
