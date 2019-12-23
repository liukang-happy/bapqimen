package com.jm.bapkp.utils;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * 用于将Map和List转换成xml，或者将xml转成Map和List，无论由谁向谁转换，xml格式必须固定如上面才行。
 */
public class XmlUtil
{
	private static final Logger log= Logger.getLogger(XmlUtil.class);

	public static Element getRootElement(String  xml)
	{
		try {
			SAXBuilder builder = new SAXBuilder();
			// 字符串转化为XML对象
			Document document = builder.build(new StringReader(xml));
			return document.getRootElement();
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 根据根元素，获得业务ID
	 * 
	 * @param root
	 * @return
	 * @author qibo 2011-6-21 下午04:37:15
	 */
	public static String getBussineId(Element root)
	{
		if (null != root) {
			return root.getAttributeValue("id");
		}
		return null;
	}
	
	public static String getBussineId(String xml)
	{
		return getBussineId(getRootElement(xml));
	}
	
	public static Map getMapFromXml(String xml, String tagName)
	{
		Map map = new HashMap();
		Element rootElement = XmlUtil.getRootElement(xml);
		Element mapElement = rootElement.getChild(tagName);
		List childList = mapElement.getChildren();
		for (int i = 0; i < childList.size(); i++)
		{
			Element child = (Element) childList.get(i);
			String childName = child.getName();
			String value = child.getText();
			value = value == null ? "" : value;
			map.put(childName.trim(), value.trim());
		}
		return map;
	}

	public static Map getMapFromElement(Element element)
	{
		Map map = new HashMap();
		List childList = element.getChildren();
		for (int i = 0; i < childList.size(); i++)
		{
			Element child = (Element) childList.get(i);
			String childName = child.getName();
			String value = child.getText();
			value = value == null ? "" : value;
			map.put(childName, value.trim());
		}
		return map;
	}
	
	public static Map getMapFromElement(Element element,String childTagName)
	{
		Map map = new HashMap();
		Element childElement = element.getChild(childTagName);
		List childList = childElement.getChildren();
		for (int i = 0; i < childList.size(); i++)
		{
			Element child = (Element) childList.get(i);
			String childName = child.getName();
			String value = child.getText();
			value = value == null ? "" : value;
			map.put(childName, value.trim());
		}
		return map;
	}
	
	public static List getListFromElement(Element element)
	{
		List list = new ArrayList();
		List childList = element.getChildren();
		for (int i = 0; i < childList.size(); i++)
		{
			Element child = (Element) childList.get(i);
			String value = child.getText();
			value = value == null ? "" : value;
			list.add(value.trim());
		}
		return list;
	}
	
	/*
	 * 里面每个map只有一个键值对
	 */
	public static List getListFromElement(Element element, String listTag)
	{
		List list = new ArrayList();
		// listTag = listTag.toUpperCase();
		Element listElement = element.getChild(listTag);
		List mapList = listElement.getChildren();
		Map map = null;
		for (int i = 0; i < mapList.size(); i++)
		{
			map = new HashMap();
			Element child = (Element) mapList.get(i);
			String childName = child.getName();
			String value = child.getText();
			map.put(childName, value.trim());
			list.add(map);
		}
		return list;
	}
	
	public static List getListFromElement2(Element element, String listTag)
	{
		List list = new ArrayList();
		Element listElement = element.getChild(listTag);
		List mapList = listElement.getChildren();
		Map map = null;
		for (int i = 0; i < mapList.size(); i++)
		{
			map = new HashMap();
			Element mapElement = (Element) mapList.get(i);
			List list2= mapElement.getChildren();
			for (int j = 0; j < list2.size(); j++){
				Element child = (Element) list2.get(j);
				String childName = child.getName();
				String value = child.getText();
				map.put(childName, value.trim());
			}
			list.add(map);
		}
		return list;
	}

	public static Element getElementFromXml(String  xml,String tagName)
	{
		try {
			Element rootEle = getRootElement(xml);
			return rootEle.getChild(tagName);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Element getElementFromXml(String  xml,String firstTagName,String secondTagName)
	{
		try {
			Element rootEle = getRootElement(xml);
			return rootEle.getChild(firstTagName).getChild(secondTagName);
		} catch (Exception e) {
			return null;
		}
	}
}
