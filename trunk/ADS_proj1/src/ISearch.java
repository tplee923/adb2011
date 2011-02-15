import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ISearch {

	public static String formSearchURL(String s) {
		String yahooID = "rguvpRTV34EZev_Dpa.oJXPIUtqRiQglLqSaTSxw2I1vCIc6TobhS_NjLyINdlm.M2DV5KMbeoPN2QgUEa3nucgd5wcBvsE-";
		String[] keywords = s.split(" ");
		String finalStr = "";
		for (int i = 0; i < keywords.length; i++) {
			if (i == keywords.length - 1) {
				finalStr = finalStr + keywords[i];
			} else {
				finalStr = finalStr + keywords[i] + "%20";
			}
		}
		String urlString = "http://boss.yahooapis.com/ysearch/web/v1/"
				+ finalStr + "?appid=" + yahooID + "&format=xml";
		// System.out.println("Final URL:"+urlString);
		return urlString;
	}

	public static String search(String keywords) {
		String resultstring = "";
		try {
			URL url = new URL(formSearchURL(keywords));
			URLConnection connection = url.openConnection();
			connection.connect();
			InputStreamReader in = new InputStreamReader(
					connection.getInputStream());
			BufferedReader br = new BufferedReader(in);
			String s = br.readLine();
			while (s != null) {
				resultstring += s;
				s = br.readLine();
			}
			// System.out.println(resultstring);
			return resultstring;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return "";
	}

	public static HashMap AnalyzeResult(String xmlString) throws SAXException,
			IOException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource inStream = new org.xml.sax.InputSource();
		inStream.setCharacterStream(new java.io.StringReader(xmlString));

		Document doc = builder.parse(inStream);
		NodeList nodeList = doc.getElementsByTagName("result");
		// System.out.println(nodeList.getLength());
		HashMap<Integer, ResultObject> map = new HashMap(10);
		for (int index = 0; index < nodeList.getLength(); index++) {
			String theTitle = null;
			String theURL = null;
			String theSummary = null;

			Node node = nodeList.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;

				NodeList nameNode = element.getElementsByTagName("title");

				if (nameNode.item(0).getNodeType() == Node.ELEMENT_NODE) {
					Element nameElement = (Element) nameNode.item(0);
					theTitle = nameElement.getFirstChild().getNodeValue()
							.trim();
					// System.out.println("TITLE: " + theTitle);
				}

				nameNode = element.getElementsByTagName("url");

				if (nameNode.item(0).getNodeType() == Node.ELEMENT_NODE) {
					Element nameElement = (Element) nameNode.item(0);
					theURL = nameElement.getFirstChild().getNodeValue().trim();
					// System.out.println("URL: " + theURL);
				}

				nameNode = element.getElementsByTagName("abstract");

				if (nameNode.item(0).getNodeType() == Node.ELEMENT_NODE) {
					Element nameElement = (Element) nameNode.item(0);
					if (nameElement.getFirstChild() == null) {
						theSummary = "Not Available";
					} else {
						theSummary = nameElement.getFirstChild().getNodeValue();
					}
					// System.out.println("ABSTRACT: " + theSummary);
				}

				map.put(new Integer(index + 1), new ResultObject(theTitle,
						theURL, theSummary));
			}
		}
		return map;
	}

	private static String readUserInput(String prompt) {
		Scanner scanner = new Scanner(System.in);
		System.out.print(prompt);
		return scanner.nextLine();
	}

	public static String improveResult() {
		// TODO
		return "new search";
	}

	public static void doSearch(String keywords, int expectedPrecision) {
		String s = ISearch.search(keywords);
		try {
			HashMap map = ISearch.AnalyzeResult(s);
			if (map.size() == 10) {
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Integer key = (Integer) entry.getKey();
					ResultObject val = (ResultObject) entry.getValue();
					System.out.println(key);
					System.out.println(val);
				}
			}
			String feedback = readUserInput("Input format: index1 index2 index3\nPlease input indexes of right results:");
			String[] precisions = feedback.trim().split(" ");
			int actualResult = precisions.length;
			// System.out.println("Actual number of good results:" +
			// actualResult);
			if (actualResult < expectedPrecision) {
				System.out.println("NEED TO DO MORE SEARCH");
				String newKeywords = improveResult();
				doSearch(newKeywords, expectedPrecision);
			} else {
				System.out.println("Jobs Done, thanks for trying ISearch.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String str = readUserInput("Input format: keyword1 keyword2,precision\nPlease input string:");
		String[] array = str.split(",");
		String keywords = array[0];
		String strPrecisioin = array[1];
		int expectedPrecision = (int) (new Double(strPrecisioin).doubleValue() * 10);
		// System.out.println("Expected number of good results:" +
		// expectedPrecision);
		doSearch(keywords, expectedPrecision);
	}

}
