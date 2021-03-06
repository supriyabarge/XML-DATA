package com.xml.service;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Repository
@Transactional
public class TableService {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@PersistenceContext
	EntityManager entityManager;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllTables() {
		List<String> tables =new ArrayList<>();
		try {
		      SQLQuery query = getSession().createSQLQuery("Show tables ");
		      tables = query.list();
		      System.out.println("Tables in the current database: ");
		}catch(Exception e) {
			System.out.println(e);
		}
		return tables;
		
	}
	
	public Document geneateXml(String tableName) {
		Document doc = null;
		try {
			doc = generateXML(tableName);
		} catch (TransformerException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public void geneateTables(Document doc) {
		try {
			xmlToTable(doc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Document generateXML(String tblName) throws TransformerException, ParserConfigurationException {
		Session session = entityManager.unwrap(Session.class);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element results = doc.createElement("Table");
		doc.appendChild(results);
		String sql = "select * from " + tblName+" LIMIT 100";
		session.doWork(connection -> {
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql))  {
	        ResultSet rs = null;
			DOMSource domSource = null;
			
			rs = preparedStatement.executeQuery();

			System.out.println("Col count pre ");
			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();

			Element tableName = doc.createElement("TableName");
			tableName.appendChild(doc.createTextNode(rsmd.getTableName(1)));
			results.appendChild(tableName);

			Element structure = doc.createElement("TableStructure");
			results.appendChild(structure);

			Element col = null;
			for (int i = 1; i <= colCount; i++) {

				col = doc.createElement("Column" + i);
				results.appendChild(col);
				Element columnNode = doc.createElement("ColumnName");
				columnNode.appendChild(doc.createTextNode(rsmd.getColumnName(i)));
				col.appendChild(columnNode);

				Element typeNode = doc.createElement("ColumnType");
				typeNode.appendChild(doc.createTextNode(String.valueOf((rsmd.getColumnTypeName(i)))));
				col.appendChild(typeNode);

				Element lengthNode = doc.createElement("Length");
				lengthNode.appendChild(doc.createTextNode(String.valueOf((rsmd.getPrecision(i)))));
				col.appendChild(lengthNode);

				structure.appendChild(col);
			}

			System.out.println("Col count = " + colCount);

			Element productList = doc.createElement("TableData");
			results.appendChild(productList);

			int l = 0;
			while (rs.next()) {
				Element row = doc.createElement("Product" + (++l));
				results.appendChild(row);
				for (int i = 1; i <= colCount; i++) {
					String columnName = rsmd.getColumnName(i);
					Object value = rs.getObject(i);
					Element node = doc.createElement(columnName);
					node.appendChild(doc.createTextNode((value != null) ? value.toString() : ""));
					row.appendChild(node);
				}
				productList.appendChild(row);
			}

			domSource = new DOMSource(doc);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

			StringWriter sw = new StringWriter();
			StreamResult sr = new StreamResult(sw);
			transformer.transform(domSource, sr);

			System.out.println("Xml document 1" + sw.toString());

			System.out.println("********************************");

		} catch (SQLException sqlExp) {

			System.out.println("SQLExcp:" + sqlExp.toString());

		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		});
		return doc;

	}

	public void xmlToTable(Document doc) throws SQLException {
		Session session = entityManager.unwrap(Session.class);
		//Connection con = getConnection();

		System.out.println("Table Name= " + doc.getElementsByTagName("TableName").item(0).getTextContent());

		StringBuffer ddl = new StringBuffer(
				"create table " + doc.getElementsByTagName("TableName").item(0).getTextContent() + "1 (");

		StringBuffer dml = new StringBuffer(
				"insert into  " + doc.getElementsByTagName("TableName").item(0).getTextContent() + "1 (");

		NodeList tableStructure = doc.getElementsByTagName("TableStructure");

		int no_of_columns = tableStructure.item(0).getChildNodes().getLength();
		int columnCount = 0;
		for (int i = 0; i < no_of_columns; i++) {
			try {
				ddl.append(doc.getElementsByTagName("ColumnName").item(i).getTextContent() + " "
						+ doc.getElementsByTagName("ColumnType").item(i).getTextContent() + "("
						+ doc.getElementsByTagName("Length").item(i).getTextContent() + "),");
				dml.append(doc.getElementsByTagName("ColumnName").item(i).getTextContent() + ",");
				columnCount++;
			} catch (Exception e) {
				System.out.println("Error");
			}
		}

		System.out.println(" DDL " + ddl.toString());
		System.out.println(" dml " + dml.toString());

		ddl = ddl.replace(ddl.length() - 1, ddl.length(), ")");
		dml = dml.replace(dml.length() - 1, dml.length(), ") values(");

		System.out.println(" DDL " + ddl.toString());

		for (int k = 0; k < columnCount; k++)
			dml.append("?,");

		dml = dml.replace(dml.length() - 1, dml.length(), ")");

		System.out.println(" dml " + dml.toString());

		try {
			session.createSQLQuery(ddl.toString()).executeUpdate();
		} catch (Exception e) {
			System.out.println("Tables already created, skipping table creation process" + e.toString());
		}

		NodeList tableData = doc.getElementsByTagName("TableData");

		int tdlen = tableData.item(0).getChildNodes().getLength();
		String sql = dml.toString();

		session.doWork(connection -> {
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql))  {

		String colName = "";
		boolean isBatch =true;
		for (int i = 0; i < tdlen; i++) {
			System.out.println("Outer" + i);
			try {
				for (int j = 0; j < tableStructure.item(0).getChildNodes().getLength(); j++) {

					colName = doc.getElementsByTagName("ColumnName").item(j).getTextContent();
					preparedStatement.setString(j + 1, doc.getElementsByTagName(colName).item(i).getTextContent());

					System.out.println("Data  =" + doc.getElementsByTagName(colName).item(i).getTextContent());
				}
			} catch (Exception e) {
				System.out.println("Error");
			}
			preparedStatement.addBatch();
		}

		int[] numUpdates = preparedStatement.executeBatch();

		System.out.println(numUpdates + " records inserted");
			}
		});
	}
}
