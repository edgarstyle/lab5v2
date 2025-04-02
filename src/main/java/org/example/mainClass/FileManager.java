package org.example.mainClass;

import org.example.exception.*;
import org.example.models.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Класс для операций с файлами
 */
public class FileManager {
    private static File file;

    public FileManager(File file) {
        this.file = file;
    }

    public List<Flat> readElementsFromFile() throws FileNotFoundException, FileReadPermissionException {
        if (!file.exists()) {
            throw new FileNotFoundException("Файл не найден: " + file.getName());
        }
        if (!file.canRead()) {
            throw new FileReadPermissionException("Нет прав для чтения файла: " + file.getName());
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList flatNodes = doc.getElementsByTagName("flat");
            List<Flat> flats = new ArrayList<>();

            for (int i = 0; i < flatNodes.getLength(); i++) {
                Node node = flatNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element flatElement = (Element) node;
                    flats.add(parseFlatElement(flatElement));
                }
            }
            return flats;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении XML файла", e);
        }
    }

    private Flat parseFlatElement(Element flatElement) {
        // Парсинг всех полей Flat из XML
        String name = getElementText(flatElement, "name");

        // Парсинг Coordinates
        Element coordElement = (Element) flatElement.getElementsByTagName("coordinates").item(0);
        Float x = Float.parseFloat(getElementText(coordElement, "x"));
        Integer y = Integer.parseInt(getElementText(coordElement, "y"));
        Coordinates coordinates = new Coordinates(x, y);

        // Парсинг остальных полей
        Float area = Float.parseFloat(getElementText(flatElement, "area"));
        int numberOfRooms = Integer.parseInt(getElementText(flatElement, "numberOfRooms"));
        Furnish furnish = Furnish.valueOf(getElementText(flatElement, "furnish"));
        View view = View.valueOf(getElementText(flatElement, "view"));

        // Опциональные поля
        Transport transport = null;
        if (flatElement.getElementsByTagName("transport").getLength() > 0) {
            transport = Transport.valueOf(getElementText(flatElement, "transport"));
        }

        // Парсинг House (если есть)
        House house = null;
        if (flatElement.getElementsByTagName("house").getLength() > 0) {
            Element houseElement = (Element) flatElement.getElementsByTagName("house").item(0);
            String houseName = getElementText(houseElement, "name");
            int year = Integer.parseInt(getElementText(houseElement, "year"));
            long lifts = Long.parseLong(getElementText(houseElement, "numberOfLifts"));
            house = new House(houseName, year, lifts);
        }

        // Создание объекта Flat (id и creationDate генерируются автоматически)
        Flat flat = new Flat(name, coordinates, area, numberOfRooms, furnish, view, transport, house);

        // Установка ID и даты из XML (если нужно сохранить оригинальные значения)
        if (flatElement.getElementsByTagName("id").getLength() > 0) {
            long id = Long.parseLong(getElementText(flatElement, "id"));
            Flat.setNextId(id + 1); // Обновляем счетчик ID
        }

        return flat;
    }

    public void saveToFile(Set<Flat> flats) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Создаем корневой элемент
            Element rootElement = doc.createElement("flats");
            doc.appendChild(rootElement);

            // Добавляем все квартиры
            for (Flat flat : flats) {
                rootElement.appendChild(createFlatElement(doc, flat));
            }

            // Записываем в файл
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileWriter(file));
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении в XML файл", e);
        }
    }

    private Element createFlatElement(Document doc, Flat flat) {
        Element flatElement = doc.createElement("flat");

        // Добавляем простые поля
        addTextElement(doc, flatElement, "id", flat.getId().toString());
        addTextElement(doc, flatElement, "name", flat.getName());
        addTextElement(doc, flatElement, "area", flat.getArea().toString());
        addTextElement(doc, flatElement, "numberOfRooms", String.valueOf(flat.getNumberOfRooms()));
        addTextElement(doc, flatElement, "furnish", flat.getFurnish().name());
        addTextElement(doc, flatElement, "view", flat.getView().name());

        // Добавляем Coordinates
        Element coordElement = doc.createElement("coordinates");
        addTextElement(doc, coordElement, "x", flat.getCoordinates().getX().toString());
        addTextElement(doc, coordElement, "y", flat.getCoordinates().getY().toString());
        flatElement.appendChild(coordElement);

        // Добавляем Transport (если есть)
        if (flat.getTransport() != null) {
            addTextElement(doc, flatElement, "transport", flat.getTransport().name());
        }

        // Добавляем House (если есть)
        if (flat.getHouse() != null) {
            Element houseElement = doc.createElement("house");
            addTextElement(doc, houseElement, "name", flat.getHouse().getName());
            addTextElement(doc, houseElement, "year", String.valueOf(flat.getHouse().getYear()));
            addTextElement(doc, houseElement, "numberOfLifts", String.valueOf(flat.getHouse().getNumberOfLifts()));
            flatElement.appendChild(houseElement);
        }

        // Добавляем дату создания
        addTextElement(doc, flatElement, "creationDate",
                flat.getCreationDate().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

        return flatElement;
    }

    // Вспомогательные методы
    private String getElementText(Element parent, String tagName) {
        return parent.getElementsByTagName(tagName).item(0).getTextContent();
    }

    private void addTextElement(Document doc, Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(textContent));
        parent.appendChild(element);
    }
}