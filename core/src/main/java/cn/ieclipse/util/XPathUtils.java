package cn.ieclipse.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 使用XPath解析xml
 *
 * @author Jamling
 * @since 2024-03-23
 */
public class XPathUtils {
    private static WeakReference<DocumentBuilder> documentBuilder;
    private static WeakReference<XPath> xpath;

    private XPathUtils() {
    }

    private static DocumentBuilder getDocumentBuilder() {
        if (documentBuilder == null || documentBuilder.get() == null) {
            try {
                documentBuilder = new WeakReference<>(DocumentBuilderFactory.newInstance().newDocumentBuilder());
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        return documentBuilder.get();
    }

    private static XPath getXpath() {
        if (xpath == null || xpath.get() == null) {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            xpath = new WeakReference<>(xPathFactory.newXPath());
        }
        return xpath.get();
    }

    /**
     * 从输入流中解析xml
     *
     * @param is 输入流
     * @return Document
     */
    public static Document parse(InputStream is) {
        Document document = null;
        try {
            document = getDocumentBuilder().parse(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 根据xpath查找单个节点
     *
     * @param node  Document or Element
     * @param xpath xpath表达式
     * @return Element
     */
    public static Element findElement(Node node, String xpath) {
        try {
            XPathExpression expression = getXpath().compile(xpath);
            NodeList nodeList = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
            if (nodeList.getLength() == 0) {
                return null;
            }
            return (Element) nodeList.item(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据xpath查找多个节点
     *
     * @param node  Document or Element
     * @param xpath xpath表达式
     * @return Element集合
     */
    public static List<Element> findElements(Node node, String xpath) {
        try {
            XPathExpression expression = getXpath().compile(xpath);
            NodeList nodeList = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
            List<Element> list = new ArrayList<>(nodeList.getLength());
            for (int i = 0; i < nodeList.getLength(); i++) {
                list.add((Element) nodeList.item(i));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
}
