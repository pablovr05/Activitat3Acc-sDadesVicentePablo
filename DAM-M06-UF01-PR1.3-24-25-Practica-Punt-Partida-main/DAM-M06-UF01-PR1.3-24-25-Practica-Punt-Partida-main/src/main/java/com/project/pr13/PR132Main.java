package com.project.pr13;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.project.pr13.format.AsciiTablePrinter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PR132Main {

    private final Path xmlFilePath;
    private static final Scanner scanner = new Scanner(System.in);

    public PR132Main(Path xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        Path xmlFilePath = Paths.get(userDir, "data", "pr13", "cursos.xml");

        PR132Main app = new PR132Main(xmlFilePath);
        app.executar();
    }

    public void executar() {
        boolean exit = false;
        while (!exit) {
            mostrarMenu();
            System.out.print("Escull una opció: ");
            int opcio = scanner.nextInt();
            scanner.nextLine();
            exit = processarOpcio(opcio);
        }
    }

    public boolean processarOpcio(int opcio) {
        String cursId;
        String nomAlumne;
        switch (opcio) {
            case 1:
                List<List<String>> cursos = llistarCursos();
                imprimirTaulaCursos(cursos);
                return false;
            case 2:
                System.out.print("Introdueix l'ID del curs per veure els seus mòduls: ");
                cursId = scanner.nextLine();
                List<List<String>> moduls = mostrarModuls(cursId);
                imprimirTaulaModuls(moduls);
                return false;
            case 3:
                System.out.print("Introdueix l'ID del curs per veure la llista d'alumnes: ");
                cursId = scanner.nextLine();
                List<String> alumnes = llistarAlumnes(cursId);
                imprimirLlistaAlumnes(alumnes);
                return false;
            case 4:
                System.out.print("Introdueix l'ID del curs on vols afegir l'alumne: ");
                cursId = scanner.nextLine();
                System.out.print("Introdueix el nom complet de l'alumne a afegir: ");
                nomAlumne = scanner.nextLine();
                afegirAlumne(cursId, nomAlumne);
                return false;
            case 5:
                System.out.print("Introdueix l'ID del curs on vols eliminar l'alumne: ");
                cursId = scanner.nextLine();
                System.out.print("Introdueix el nom complet de l'alumne a eliminar: ");
                nomAlumne = scanner.nextLine();
                eliminarAlumne(cursId, nomAlumne);
                return false;
            case 6:
                System.out.println("Sortint del programa...");
                return true;
            default:
                System.out.println("Opció no reconeguda. Si us plau, prova de nou.");
                return false;
        }
    }

    private void mostrarMenu() {
        System.out.println("\nMENÚ PRINCIPAL");
        System.out.println("1. Llistar IDs de cursos i tutors");
        System.out.println("2. Mostrar IDs i títols dels mòduls d'un curs");
        System.out.println("3. Llistar alumnes d’un curs");
        System.out.println("4. Afegir un alumne a un curs");
        System.out.println("5. Eliminar un alumne d'un curs");
        System.out.println("6. Sortir");
    }

    public List<List<String>> llistarCursos() {
        List<List<String>> cursos = new ArrayList<>();
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xpath = XPathFactory.newInstance().newXPath();

            NodeList cursosNodes = (NodeList) xpath.evaluate("/cursos/curs", document, XPathConstants.NODESET);

            for (int i = 0; i < cursosNodes.getLength(); i++) {
                Element cursElement = (Element) cursosNodes.item(i);
                String id = cursElement.getAttribute("id");
                String tutor = xpath.evaluate("tutor", cursElement);
                NodeList alumnes = (NodeList) xpath.evaluate("alumnes/alumne", cursElement, XPathConstants.NODESET);
                String totalAlumnes = String.valueOf(alumnes.getLength());

                cursos.add(List.of(id, tutor, totalAlumnes));
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return cursos;
    }

    public void imprimirTaulaCursos(List<List<String>> cursos) {
        List<String> capçaleres = List.of("ID", "Tutor", "Total Alumnes");
        AsciiTablePrinter.imprimirTaula(capçaleres, cursos);
    }

    public List<List<String>> mostrarModuls(String idCurs) {
        List<List<String>> moduls = new ArrayList<>();
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expression = String.format("/cursos/curs[@id='%s']/moduls/modul", idCurs);
            NodeList modulNodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

            for (int i = 0; i < modulNodes.getLength(); i++) {
                Element modulElement = (Element) modulNodes.item(i);
                String id = modulElement.getAttribute("id");
                String titol = xpath.evaluate("titol", modulElement);
                moduls.add(List.of(id, titol));
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return moduls;
    }

    public void imprimirTaulaModuls(List<List<String>> moduls) {
        List<String> capçaleres = List.of("ID Mòdul", "Títol");
        AsciiTablePrinter.imprimirTaula(capçaleres, moduls);
    }

    public List<String> llistarAlumnes(String idCurs) {
        List<String> alumnes = new ArrayList<>();
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expression = String.format("/cursos/curs[@id='%s']/alumnes/alumne", idCurs);
            NodeList alumneNodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

            for (int i = 0; i < alumneNodes.getLength(); i++) {
                Element alumneElement = (Element) alumneNodes.item(i);
                alumnes.add(alumneElement.getTextContent());
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return alumnes;
    }

    public void imprimirLlistaAlumnes(List<String> alumnes) {
        System.out.println("Llista d'alumnes:");
        alumnes.forEach(System.out::println);
    }

    public void afegirAlumne(String idCurs, String nomAlumne) {
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expression = String.format("/cursos/curs[@id='%s']/alumnes", idCurs);
            Node alumnesNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (alumnesNode != null) {
                Element nouAlumne = document.createElement("alumne");
                nouAlumne.setTextContent(nomAlumne);
                alumnesNode.appendChild(nouAlumne);
                guardarDocumentXML(document, xmlFilePath);
                System.out.println("Alumne afegit correctament.");
            } else {
                System.out.println("No s'ha trobat el curs especificat.");
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    public void eliminarAlumne(String idCurs, String nomAlumne) {
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expression = String.format("/cursos/curs[@id='%s']/alumnes/alumne[text()='%s']", idCurs, nomAlumne);
            Node alumneNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (alumneNode != null) {
                alumneNode.getParentNode().removeChild(alumneNode);
                guardarDocumentXML(document, xmlFilePath);
                System.out.println("Alumne eliminat correctament.");
            } else {
                System.out.println("No s'ha trobat l'alumne especificat.");
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    private Document carregarDocumentXML(Path xmlFilePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(xmlFilePath.toFile());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void guardarDocumentXML(Document document, Path xmlFilePath) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(xmlFilePath.toFile());
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
