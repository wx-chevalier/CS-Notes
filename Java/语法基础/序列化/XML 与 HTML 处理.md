# XSD Validator

```java
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
...

URL schemaFile = new URL("http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd");
Source xmlFile = new StreamSource(new File("web.xml"));
SchemaFactory schemaFactory = SchemaFactory
    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
Schema schema = schemaFactory.newSchema(schemaFile);
Validator validator = schema.newValidator();
try {
  validator.validate(xmlFile);
  System.out.println(xmlFile.getSystemId() + " is valid");
} catch (SAXException e) {
  System.out.println(xmlFile.getSystemId() + " is NOT valid");
  System.out.println("Reason: " + e.getLocalizedMessage());
}
```
