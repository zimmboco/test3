import static java.util.stream.Collectors.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class FirstTest {
    private  ExecutorService executorService = Executors.newFixedThreadPool(10);

    public FirstTest(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void calculate(String fileName) {
        List<CompletableFuture<Map<String, Double>>> allAmounts = getAllAmounts(fileName);
        Map<String, Double> collect = allAmounts.stream()
                .map(CompletableFuture::join)
                .flatMap(map -> map.entrySet().stream())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, Double::sum));
        writeToXML(collect, "resultTestCountAllPriceFine.xml");
    }

    private List<CompletableFuture<Map<String, Double>>> getAllAmounts(String fileName) {
        List<CompletableFuture<Map<String, Double>>> futuresList = new ArrayList<>();
            List<Path> paths = getAllFile(fileName);
            for (Path value : paths) {
                CompletableFuture<Map<String, Double>> completableFuture =
                        CompletableFuture.supplyAsync(() -> parseFile(value), executorService);
                futuresList.add(completableFuture);
            }
        return futuresList;
    }

    private static List<Path> getAllFile(String directoryName) {
        try (Stream<Path> pathStream = Files.walk(Paths.get(directoryName))) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .collect(toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Double> parseFile(Path value) {
        File file = value.toFile();
        if (file.length() == 0) {
           return null;
        }
        Map<String, Double> map = Collections.synchronizedMap(new HashMap<>());
        JSONArray array = null;
        try {
            JSONParser parser = new JSONParser();
            array = (JSONArray) parser.parse(new FileReader(file));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        for (Object o : array) {
            JSONObject fines = (JSONObject) o;
            String fine = (String) fines.get("type");
            Double price = (Double) fines.get("fine_amount");
            map.compute(fine, (k, v) -> v == null ? price : v + price);
        }
        return map;
    }

    private static void writeToXML(Map<String, Double> fines, String fileName) {
        Document doc = new Document();
        doc.setRootElement(new Element("Fines"));
        List<Element> collect = fines.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).map(it -> {
                    Element fineElement = new Element("fine");
                    fineElement.setAttribute("fine_name", it.getKey());
                    fineElement.setAttribute("count", it.getValue().toString());
                    return fineElement;
                })
                .collect(toList());
        doc.getRootElement().addContent(collect);
        XMLOutputter xmlWriter = new XMLOutputter(Format.getPrettyFormat());
        try {
            xmlWriter.output(doc, new FileOutputStream(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
