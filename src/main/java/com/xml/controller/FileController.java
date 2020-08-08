package com.xml.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.xml.service.TableService;

@RestController
@RequestMapping(value = "/xml-data")
public class FileController {
	
	@Autowired
	private TableService tableService;

	@RequestMapping(value = "/getTables", method = RequestMethod.GET)
	public Object tables() {
		return tableService.getAllTables();
	}
	
	@RequestMapping(value = "/doUpload", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public String upload(@RequestParam MultipartFile file) {
		
		try {
			ByteArrayInputStream stream = new   ByteArrayInputStream(file.getBytes());
			String myString = IOUtils.toString(stream, "UTF-8");
			tableService.geneateTables(convertStringToDocument(myString));
			return "Successful Ingestion";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Failed To Upload";
	}
	
	@RequestMapping(value = "/getTable/{tableName}", method = RequestMethod.GET)
	public Object table(@PathVariable(value = "tableName") String tableName) {
	
		return tableService.geneateXml(tableName);
	}

	public static Document loadXml(String xmlString) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputSource source = new InputSource(new StringReader(xmlString));
			Document doc = dBuilder.parse(source);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try  
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }
}
