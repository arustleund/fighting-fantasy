package rustleund.nightdragon.main;

import java.io.File;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rustleund.nightdragon.framework.closures.TestFlagClosure;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;

public class DiceRollerTest {

	public static void main(String[] args) {

		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document targetPageDocument = documentBuilder.parse(new File("pages/" + 37 + ".xml"));

			Element immediateElement = (Element) targetPageDocument.getElementsByTagName("immediate").item(0);
			Element test14 = (Element) immediateElement.getElementsByTagName("testFlag").item(0);
			Element test14success = (Element) test14.getElementsByTagName("successful").item(0);
//			Element test14successTest12 = (Element) test14success.getElementsByTagName("testFlag").item(0);
//			Element test14successTest12Unsuccess = (Element) test14successTest12.getElementsByTagName("unsuccessful").item(0);
			

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(targetPageDocument);

			StringWriter writer = new StringWriter();
			StreamResult streamResult = new StreamResult(writer);

			transformer.transform(source, streamResult);

			System.out.println(writer.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
