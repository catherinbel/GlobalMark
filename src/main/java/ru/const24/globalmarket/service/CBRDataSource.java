package ru.const24.globalmarket.service;

import com.mashape.unirest.http.Unirest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.const24.globalmarket.model.CurrencyNominal;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;

public class CBRDataSource implements CurrencyDataSource {
    public static final String API_URL = ("http://www.cbr.ru/scripts/XML_daily.asp?date_req=15/11/2018");

    @Override
    public CurrencyNominal getCurrencyNominalByDate(LocalDate date) throws Exception {

        System.out.println("Получаем информацию о валюте из ЦБ РФ");

        double unitUSD = 1;
        double unitEUR = 0;
        double unitRUB = 0;

        String xmlStringResponse = Unirest.get(API_URL).asString().getBody();
//        System.out.println(xmlStringResponse);


// Создаем DocumentBuilder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();


// Создание документа из файлы
        ByteArrayInputStream input = new ByteArrayInputStream(
                xmlStringResponse.getBytes("windows-1251"));
        Document doc = builder.parse(input);
        doc.getDocumentElement().normalize();
        System.out.println(doc);


        NodeList nList = doc.getElementsByTagName("Valute");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("ID");

//EUR
                if (id.equals("R01239")) {
                    unitEUR = Double.parseDouble(element
                            .getElementsByTagName("Value")
                            .item(0)
                            .getTextContent()
                            .replace(",", "."));
                }

//USD
                if (id.equals("R01235")) {
                    unitUSD = Double.parseDouble(element
                            .getElementsByTagName("Value")
                            .item(0)
                            .getTextContent()
                            .replace(",", "."));
                }
            }
        }
        return new CurrencyNominal(1, unitUSD, unitUSD/unitEUR);
    }
}

