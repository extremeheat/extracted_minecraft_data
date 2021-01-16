package org.apache.logging.log4j.core.layout;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import java.util.HashSet;
import javax.xml.stream.XMLStreamException;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.jackson.Log4jJsonObjectMapper;
import org.apache.logging.log4j.core.jackson.Log4jXmlObjectMapper;
import org.apache.logging.log4j.core.jackson.Log4jYamlObjectMapper;
import org.codehaus.stax2.XMLStreamWriter2;

abstract class JacksonFactory {
   JacksonFactory() {
      super();
   }

   protected abstract String getPropertNameForContextMap();

   protected abstract String getPropertNameForSource();

   protected abstract String getPropertNameForNanoTime();

   protected abstract PrettyPrinter newCompactPrinter();

   protected abstract ObjectMapper newObjectMapper();

   protected abstract PrettyPrinter newPrettyPrinter();

   ObjectWriter newWriter(boolean var1, boolean var2, boolean var3) {
      SimpleFilterProvider var4 = new SimpleFilterProvider();
      HashSet var5 = new HashSet(2);
      if (!var1) {
         var5.add(this.getPropertNameForSource());
      }

      if (!var2) {
         var5.add(this.getPropertNameForContextMap());
      }

      var5.add(this.getPropertNameForNanoTime());
      var4.addFilter(Log4jLogEvent.class.getName(), SimpleBeanPropertyFilter.serializeAllExcept(var5));
      ObjectWriter var6 = this.newObjectMapper().writer(var3 ? this.newCompactPrinter() : this.newPrettyPrinter());
      return var6.with(var4);
   }

   static class Log4jXmlPrettyPrinter extends DefaultXmlPrettyPrinter {
      private static final long serialVersionUID = 1L;

      Log4jXmlPrettyPrinter(int var1) {
         super();
         this._nesting = var1;
      }

      public void writePrologLinefeed(XMLStreamWriter2 var1) throws XMLStreamException {
      }

      public DefaultXmlPrettyPrinter createInstance() {
         return new JacksonFactory.Log4jXmlPrettyPrinter(1);
      }
   }

   static class YAML extends JacksonFactory {
      private final boolean includeStacktrace;

      public YAML(boolean var1) {
         super();
         this.includeStacktrace = var1;
      }

      protected String getPropertNameForContextMap() {
         return "contextMap";
      }

      protected String getPropertNameForSource() {
         return "source";
      }

      protected String getPropertNameForNanoTime() {
         return "nanoTime";
      }

      protected PrettyPrinter newCompactPrinter() {
         return new MinimalPrettyPrinter();
      }

      protected ObjectMapper newObjectMapper() {
         return new Log4jYamlObjectMapper(false, this.includeStacktrace);
      }

      protected PrettyPrinter newPrettyPrinter() {
         return new DefaultPrettyPrinter();
      }
   }

   static class XML extends JacksonFactory {
      static final int DEFAULT_INDENT = 1;
      private final boolean includeStacktrace;

      public XML(boolean var1) {
         super();
         this.includeStacktrace = var1;
      }

      protected String getPropertNameForContextMap() {
         return "ContextMap";
      }

      protected String getPropertNameForSource() {
         return "Source";
      }

      protected String getPropertNameForNanoTime() {
         return "nanoTime";
      }

      protected PrettyPrinter newCompactPrinter() {
         return null;
      }

      protected ObjectMapper newObjectMapper() {
         return new Log4jXmlObjectMapper(this.includeStacktrace);
      }

      protected PrettyPrinter newPrettyPrinter() {
         return new JacksonFactory.Log4jXmlPrettyPrinter(1);
      }
   }

   static class JSON extends JacksonFactory {
      private final boolean encodeThreadContextAsList;
      private final boolean includeStacktrace;

      public JSON(boolean var1, boolean var2) {
         super();
         this.encodeThreadContextAsList = var1;
         this.includeStacktrace = var2;
      }

      protected String getPropertNameForContextMap() {
         return "contextMap";
      }

      protected String getPropertNameForSource() {
         return "source";
      }

      protected String getPropertNameForNanoTime() {
         return "nanoTime";
      }

      protected PrettyPrinter newCompactPrinter() {
         return new MinimalPrettyPrinter();
      }

      protected ObjectMapper newObjectMapper() {
         return new Log4jJsonObjectMapper(this.encodeThreadContextAsList, this.includeStacktrace);
      }

      protected PrettyPrinter newPrettyPrinter() {
         return new DefaultPrettyPrinter();
      }
   }
}
