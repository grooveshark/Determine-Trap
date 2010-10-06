package com.grooveshark.determinetrap;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class TrapXMLHandler extends DefaultHandler
{
    private List<Trap> traps = new ArrayList<Trap>();
    private boolean parsingTrap;
    private String statement;
    private boolean isTrap;

    public List<Trap> getTraps()
    {
        return traps;
    }

    public void startElement(String namespace, String localName, String qName,
            Attributes attrs)
    {
        if (localName.equals("statement")) {
            parsingTrap = true;
            isTrap = Boolean.parseBoolean(attrs.getValue("is_trap"));
        }
    }

    public void characters(char ch[], int start, int length)
    {
        statement = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {
        if (parsingTrap) {
            traps.add(new Trap(statement, isTrap));
            statement = null;
            isTrap = false;
            parsingTrap = false;
        }
    }
}
