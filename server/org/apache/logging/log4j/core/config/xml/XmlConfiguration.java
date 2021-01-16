package org.apache.logging.log4j.core.config.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.ConfiguratonFileWatcher;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.apache.logging.log4j.core.config.status.StatusConfiguration;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.core.util.Throwables;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlConfiguration extends AbstractConfiguration implements Reconfigurable {
   private static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
   private static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
   private static final String[] VERBOSE_CLASSES = new String[]{ResolverUtil.class.getName()};
   private static final String LOG4J_XSD = "Log4j-config.xsd";
   private final List<XmlConfiguration.Status> status = new ArrayList();
   private Element rootElement;
   private boolean strict;
   private String schemaResource;

   public XmlConfiguration(LoggerContext var1, ConfigurationSource var2) {
      super(var1, var2);
      File var3 = var2.getFile();
      byte[] var4 = null;

      InputStream var5;
      try {
         var5 = var2.getInputStream();

         try {
            var4 = toByteArray(var5);
         } finally {
            Closer.closeSilently(var5);
         }

         InputSource var6 = new InputSource(new ByteArrayInputStream(var4));
         var6.setSystemId(var2.getLocation());
         DocumentBuilder var7 = newDocumentBuilder(true);

         Document var8;
         try {
            var8 = var7.parse(var6);
         } catch (Exception var48) {
            Throwable var10 = Throwables.getRootCause(var48);
            if (!(var10 instanceof UnsupportedOperationException)) {
               throw var48;
            }

            LOGGER.warn((String)"The DocumentBuilder {} does not support an operation: {}.Trying again without XInclude...", (Object)var7, (Object)var48);
            var8 = newDocumentBuilder(false).parse(var6);
         }

         this.rootElement = var8.getDocumentElement();
         Map var9 = this.processAttributes(this.rootNode, this.rootElement);
         StatusConfiguration var54 = (new StatusConfiguration()).withVerboseClasses(VERBOSE_CLASSES).withStatus(this.getDefaultStatus());
         Iterator var11 = var9.entrySet().iterator();

         while(var11.hasNext()) {
            Entry var12 = (Entry)var11.next();
            String var13 = (String)var12.getKey();
            String var14 = this.getStrSubstitutor().replace((String)var12.getValue());
            if ("status".equalsIgnoreCase(var13)) {
               var54.withStatus(var14);
            } else if ("dest".equalsIgnoreCase(var13)) {
               var54.withDestination(var14);
            } else if ("shutdownHook".equalsIgnoreCase(var13)) {
               this.isShutdownHookEnabled = !"disable".equalsIgnoreCase(var14);
            } else if ("shutdownTimeout".equalsIgnoreCase(var13)) {
               this.shutdownTimeoutMillis = Long.parseLong(var14);
            } else if ("verbose".equalsIgnoreCase(var13)) {
               var54.withVerbosity(var14);
            } else if ("packages".equalsIgnoreCase(var13)) {
               this.pluginPackages.addAll(Arrays.asList(var14.split(Patterns.COMMA_SEPARATOR)));
            } else if ("name".equalsIgnoreCase(var13)) {
               this.setName(var14);
            } else if ("strict".equalsIgnoreCase(var13)) {
               this.strict = Boolean.parseBoolean(var14);
            } else if ("schema".equalsIgnoreCase(var13)) {
               this.schemaResource = var14;
            } else if ("monitorInterval".equalsIgnoreCase(var13)) {
               int var15 = Integer.parseInt(var14);
               if (var15 > 0) {
                  this.getWatchManager().setIntervalSeconds(var15);
                  if (var3 != null) {
                     ConfiguratonFileWatcher var16 = new ConfiguratonFileWatcher(this, this.listeners);
                     this.getWatchManager().watchFile(var3, var16);
                  }
               }
            } else if ("advertiser".equalsIgnoreCase(var13)) {
               this.createAdvertiser(var14, var2, var4, "text/xml");
            }
         }

         var54.initialize();
      } catch (IOException | ParserConfigurationException | SAXException var49) {
         LOGGER.error((String)("Error parsing " + var2.getLocation()), (Throwable)var49);
      }

      if (this.strict && this.schemaResource != null && var4 != null) {
         try {
            var5 = Loader.getResourceAsStream(this.schemaResource, XmlConfiguration.class.getClassLoader());
            Throwable var50 = null;

            try {
               if (var5 != null) {
                  StreamSource var51 = new StreamSource(var5, "Log4j-config.xsd");
                  SchemaFactory var52 = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                  Schema var53 = null;

                  try {
                     var53 = var52.newSchema(var51);
                  } catch (SAXException var43) {
                     LOGGER.error((String)"Error parsing Log4j schema", (Throwable)var43);
                  }

                  if (var53 != null) {
                     Validator var55 = var53.newValidator();

                     try {
                        var55.validate(new StreamSource(new ByteArrayInputStream(var4)));
                     } catch (IOException var41) {
                        LOGGER.error((String)"Error reading configuration for validation", (Throwable)var41);
                     } catch (SAXException var42) {
                        LOGGER.error((String)"Error validating configuration", (Throwable)var42);
                     }
                  }
               }
            } catch (Throwable var44) {
               var50 = var44;
               throw var44;
            } finally {
               if (var5 != null) {
                  if (var50 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var40) {
                        var50.addSuppressed(var40);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (Exception var47) {
            LOGGER.error((String)"Unable to access schema {}", (Object)this.schemaResource, (Object)var47);
         }
      }

      if (this.getName() == null) {
         this.setName(var2.getLocation());
      }

   }

   static DocumentBuilder newDocumentBuilder(boolean var0) throws ParserConfigurationException {
      DocumentBuilderFactory var1 = DocumentBuilderFactory.newInstance();
      var1.setNamespaceAware(true);
      if (var0) {
         enableXInclude(var1);
      }

      return var1.newDocumentBuilder();
   }

   private static void enableXInclude(DocumentBuilderFactory var0) {
      try {
         var0.setXIncludeAware(true);
      } catch (UnsupportedOperationException var6) {
         LOGGER.warn((String)"The DocumentBuilderFactory [{}] does not support XInclude: {}", (Object)var0, (Object)var6);
      } catch (NoSuchMethodError | AbstractMethodError var7) {
         LOGGER.warn((String)"The DocumentBuilderFactory [{}] is out of date and does not support XInclude: {}", (Object)var0, (Object)var7);
      }

      try {
         var0.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", true);
      } catch (ParserConfigurationException var4) {
         LOGGER.warn((String)"The DocumentBuilderFactory [{}] does not support the feature [{}]: {}", (Object)var0, "http://apache.org/xml/features/xinclude/fixup-base-uris", var4);
      } catch (AbstractMethodError var5) {
         LOGGER.warn((String)"The DocumentBuilderFactory [{}] is out of date and does not support setFeature: {}", (Object)var0, (Object)var5);
      }

      try {
         var0.setFeature("http://apache.org/xml/features/xinclude/fixup-language", true);
      } catch (ParserConfigurationException var2) {
         LOGGER.warn((String)"The DocumentBuilderFactory [{}] does not support the feature [{}]: {}", (Object)var0, "http://apache.org/xml/features/xinclude/fixup-language", var2);
      } catch (AbstractMethodError var3) {
         LOGGER.warn((String)"The DocumentBuilderFactory [{}] is out of date and does not support setFeature: {}", (Object)var0, (Object)var3);
      }

   }

   public void setup() {
      if (this.rootElement == null) {
         LOGGER.error("No logging configuration");
      } else {
         this.constructHierarchy(this.rootNode, this.rootElement);
         if (this.status.size() <= 0) {
            this.rootElement = null;
         } else {
            Iterator var1 = this.status.iterator();

            while(var1.hasNext()) {
               XmlConfiguration.Status var2 = (XmlConfiguration.Status)var1.next();
               LOGGER.error((String)"Error processing element {} ({}): {}", (Object)var2.name, var2.element, var2.errorType);
            }

         }
      }
   }

   public Configuration reconfigure() {
      try {
         ConfigurationSource var1 = this.getConfigurationSource().resetInputStream();
         if (var1 == null) {
            return null;
         } else {
            XmlConfiguration var2 = new XmlConfiguration(this.getLoggerContext(), var1);
            return var2.rootElement == null ? null : var2;
         }
      } catch (IOException var3) {
         LOGGER.error((String)"Cannot locate file {}", (Object)this.getConfigurationSource(), (Object)var3);
         return null;
      }
   }

   private void constructHierarchy(Node var1, Element var2) {
      this.processAttributes(var1, var2);
      StringBuilder var3 = new StringBuilder();
      NodeList var4 = var2.getChildNodes();
      List var5 = var1.getChildren();

      for(int var6 = 0; var6 < var4.getLength(); ++var6) {
         org.w3c.dom.Node var7 = var4.item(var6);
         if (var7 instanceof Element) {
            Element var14 = (Element)var7;
            String var9 = this.getType(var14);
            PluginType var10 = this.pluginManager.getPluginType(var9);
            Node var11 = new Node(var1, var9, var10);
            this.constructHierarchy(var11, var14);
            if (var10 == null) {
               String var12 = var11.getValue();
               if (!var11.hasChildren() && var12 != null) {
                  var1.getAttributes().put(var9, var12);
               } else {
                  this.status.add(new XmlConfiguration.Status(var9, var2, XmlConfiguration.ErrorType.CLASS_NOT_FOUND));
               }
            } else {
               var5.add(var11);
            }
         } else if (var7 instanceof Text) {
            Text var8 = (Text)var7;
            var3.append(var8.getData());
         }
      }

      String var13 = var3.toString().trim();
      if (var13.length() > 0 || !var1.hasChildren() && !var1.isRoot()) {
         var1.setValue(var13);
      }

   }

   private String getType(Element var1) {
      if (this.strict) {
         NamedNodeMap var2 = var1.getAttributes();

         for(int var3 = 0; var3 < var2.getLength(); ++var3) {
            org.w3c.dom.Node var4 = var2.item(var3);
            if (var4 instanceof Attr) {
               Attr var5 = (Attr)var4;
               if (var5.getName().equalsIgnoreCase("type")) {
                  String var6 = var5.getValue();
                  var2.removeNamedItem(var5.getName());
                  return var6;
               }
            }
         }
      }

      return var1.getTagName();
   }

   private Map<String, String> processAttributes(Node var1, Element var2) {
      NamedNodeMap var3 = var2.getAttributes();
      Map var4 = var1.getAttributes();

      for(int var5 = 0; var5 < var3.getLength(); ++var5) {
         org.w3c.dom.Node var6 = var3.item(var5);
         if (var6 instanceof Attr) {
            Attr var7 = (Attr)var6;
            if (!var7.getName().equals("xml:base")) {
               var4.put(var7.getName(), var7.getValue());
            }
         }
      }

      return var4;
   }

   public String toString() {
      return this.getClass().getSimpleName() + "[location=" + this.getConfigurationSource() + "]";
   }

   private static class Status {
      private final Element element;
      private final String name;
      private final XmlConfiguration.ErrorType errorType;

      public Status(String var1, Element var2, XmlConfiguration.ErrorType var3) {
         super();
         this.name = var1;
         this.element = var2;
         this.errorType = var3;
      }

      public String toString() {
         return "Status [name=" + this.name + ", element=" + this.element + ", errorType=" + this.errorType + "]";
      }
   }

   private static enum ErrorType {
      CLASS_NOT_FOUND;

      private ErrorType() {
      }
   }
}
