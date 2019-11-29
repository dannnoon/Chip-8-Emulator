package pl.krysinski.chip8.presentation;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Properties {
	private static final String DEFAULT_XML_FILE_NAME = "properties.xml";

	private static final String XML_ROOT_ELEMENT = "properties";
	private static final String XML_EMULATION_PROPERTIES = "emulation-properties";
	private static final String XML_DELAY_TIME_ELEMENT = "delay-time";
	private static final String XML_FPS_LIMIT_ELEMENT = "fps-limit";
	private static final String XML_IS_DELAY_TIME_ON_ELEMENT = "is-delay-time-on";

	private static final String XML_DELAY_STATE_ON = "YES";
	private static final String XML_DELAY_STATE_OFF = "NO";

	private static final int DEFAULT_DELAY_TIME = 5;
	private static final int DEFAULT_FPS_LIMIT = 30;

	private int delayTime;
	private int fpsLimit;

	private boolean isDelayTimeOn = true;

	public Properties() {
		delayTime = DEFAULT_DELAY_TIME;
		fpsLimit = DEFAULT_FPS_LIMIT;
	}

	private void createXMLProperties() {
		Document doc = getDocument();

		Element rootElement = doc.createElement(XML_ROOT_ELEMENT);
		doc.appendChild(rootElement);

		Element emulationProperties = doc.createElement(XML_EMULATION_PROPERTIES);
		rootElement.appendChild(emulationProperties);

		Element delayTimeElement = doc.createElement(XML_DELAY_TIME_ELEMENT);
		emulationProperties.appendChild(delayTimeElement);

		Element fpsLimitElement = doc.createElement(XML_FPS_LIMIT_ELEMENT);
		emulationProperties.appendChild(fpsLimitElement);

		Element isDelayTimeOnElement = doc.createElement(XML_IS_DELAY_TIME_ON_ELEMENT);
		emulationProperties.appendChild(isDelayTimeOnElement);

		Transformer transformer = getTransformer();

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(DEFAULT_XML_FILE_NAME));

		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

	private void loadXMLProperties() {
		Document doc = getDocumentParser(DEFAULT_XML_FILE_NAME);
		doc.getDocumentElement().normalize();

		Element root = doc.getDocumentElement();

		NodeList nodes = root.getElementsByTagName(XML_EMULATION_PROPERTIES);

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node.getNodeType() == Element.ELEMENT_NODE) {
				Element element = (Element) node;

				switch (element.getTagName()) {
				case XML_DELAY_TIME_ELEMENT:
					delayTime = Integer.parseInt(element.getTextContent());
					break;
				case XML_FPS_LIMIT_ELEMENT:
					fpsLimit = Integer.parseInt(element.getTextContent());
					break;
				case XML_IS_DELAY_TIME_ON_ELEMENT:
					isDelayTimeOn = element.getTextContent().equals("YES") ? true : false;
					break;
				}
			}
		}
	}

	private Transformer getTransformer() {
		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			return tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Document getDocument() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			return builder.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Document getDocumentParser(String file) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			return builder.parse(file);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
