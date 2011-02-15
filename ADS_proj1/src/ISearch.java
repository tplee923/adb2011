import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static String yahooID;
	private static String strPrecisioin;
	private static String[] keywordsArray;
	private static String outputStr;

	public static String formSearchURL(String[] keywordsArray) {
		// String yahooID =
		// "rguvpRTV34EZev_Dpa.oJXPIUtqRiQglLqSaTSxw2I1vCIc6TobhS_NjLyINdlm.M2DV5KMbeoPN2QgUEa3nucgd5wcBvsE-";
		String finalStr = "";
		outputStr = "";
		for (int i = 0; i < keywordsArray.length; i++) {
			outputStr = outputStr + " " + keywordsArray[i];
			if (i == keywordsArray.length - 1) {
				finalStr = finalStr + keywordsArray[i];
			} else {
				finalStr = finalStr + keywordsArray[i] + "%20";
			}
		}
		String urlString = "http://boss.yahooapis.com/ysearch/web/v1/"
				+ finalStr + "?appid=" + yahooID + "&format=xml";
		// System.out.println("Final URL:"+urlString);
		System.out.println("Parameters:\nClient key  =" + yahooID
				+ "\nQuery       =" + outputStr + "\nPrecision   = "
				+ strPrecisioin + "\nURL: " + urlString);
		System.out.println("Total no of results : 10\nYahoo! Search Results:\n"
				+ "======================");
		return urlString;
	}

	public static String search(String[] keywordsArray) {
		String resultstring = "";
		try {
			URL url = new URL(formSearchURL(keywordsArray));
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

	public static List<ResultObject> AnalyzeResult(String xmlString)
			throws SAXException, IOException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource inStream = new org.xml.sax.InputSource();
		inStream.setCharacterStream(new java.io.StringReader(xmlString));

		Document doc = builder.parse(inStream);
		NodeList nodeList = doc.getElementsByTagName("result");
		// System.out.println(nodeList.getLength());
		List<ResultObject> list = new ArrayList<ResultObject>();
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
						theSummary = "";
					} else {
						theSummary = nameElement.getFirstChild().getNodeValue();
					}
					// System.out.println("ABSTRACT: " + theSummary);
				}
				ResultObject ro = new ResultObject(theTitle, theURL, theSummary);
				int no = index + 1;
				System.out.println("Result" + no);
				System.out.println(ro);
				String userFeedback = readUserInput("Relevant (Y/N)?\n");
				if (userFeedback.equals("y")) {
					list.add(ro);
				}
			}
		}
		return list;
	}

	private static String readUserInput(String prompt) {
		Scanner scanner = new Scanner(System.in);
		System.out.print(prompt);
		return scanner.nextLine();
	}

	public static String[] improveResult() {
		// TODO
		return new String[1];
	}

	public static void doSearch(String[] keywordsArray, int expectedPrecision) {
		String s = ISearch.search(keywordsArray);
		try {
			List list = ISearch.AnalyzeResult(s);
			int actualResult = list.size();
			// System.out.println("Actual number of good results:" +
			// actualResult);
			if (actualResult < expectedPrecision) {
				System.out.println("FEEDBACK SUMMARY\n" + "Query" + outputStr
						+ "\nPrecision" + "0." + actualResult
						+ "\nStill below the desired precision of "
						+ strPrecisioin
						+ "\nIndexing results ....\nIndexing results ...."
						+ "\nAugmenting by" + "money owed");
				if (actualResult == 0) {
					System.out
							.println("Below desired precision, but can no longer augment the query");
					return;
				}
				String[] newKeywordsArray = improveResult();
				doSearch(newKeywordsArray, expectedPrecision);
			} else {
				System.out.println("Jobs Done, thanks for trying ISearch.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean isInputValid(String[] array) {
		int length = array.length;
		if (length < 3 || !(isFractionValid(array[length - 2]))) {
			System.out
					.println("Input format error,the correct input format is\n<keyword1> [keyword2] <precision> <yahooID>.");
			return false;
		}// else if array[length-2]
		return true;
	}

	public static boolean isFractionValid(String s) {
		Pattern p = Pattern.compile("0+.\\d{1}+");// the fraction should be 0.1
													// ~ 0.9
		Matcher m = p.matcher(s);
		// System.out.println(m.matches());
		return m.matches();
	}

	public static void main(String[] args) {

		/*
		 * for(int i=0; i<args.length; i++){ System.out.println(args[i]); }
		 */

		if (!(isInputValid(args))) {
			return; // input error, simply return.
		}

		int length = args.length;
		// String keywords = args[0];
		yahooID = args[length - 1]; // the last para should be yahooID (static
									// variable)
		strPrecisioin = args[length - 2]; // the 2nd last para should be
											// precision fraction
		keywordsArray = new String[length - 2];// create the array only
												// contains the keywords
		System.arraycopy(args, 0, keywordsArray, 0, length - 2);

		/*
		 * for(int i=0; i<keywordsArray.length; i++){
		 * System.out.println(keywordsArray[i]); }
		 */

		int expectedPrecision = (int) (new Double(strPrecisioin).doubleValue() * 10);
		// System.out.println("Expected number of good results:" +
		// expectedPrecision);
		doSearch(keywordsArray, expectedPrecision);

	}

}
