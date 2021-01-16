package org.apache.logging.log4j.message;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.logging.log4j.util.EnglishEnums;
import org.apache.logging.log4j.util.IndexedReadOnlyStringMap;
import org.apache.logging.log4j.util.IndexedStringMap;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;

@AsynchronouslyFormattable
@PerformanceSensitive({"allocation"})
public class MapMessage implements MultiformatMessage, StringBuilderFormattable {
   private static final long serialVersionUID = -5031471831131487120L;
   private final IndexedStringMap data;

   public MapMessage() {
      super();
      this.data = new SortedArrayStringMap();
   }

   public MapMessage(Map<String, String> var1) {
      super();
      this.data = new SortedArrayStringMap(var1);
   }

   public String[] getFormats() {
      return MapMessage.MapFormat.names();
   }

   public Object[] getParameters() {
      Object[] var1 = new Object[this.data.size()];

      for(int var2 = 0; var2 < this.data.size(); ++var2) {
         var1[var2] = this.data.getValueAt(var2);
      }

      return var1;
   }

   public String getFormat() {
      return "";
   }

   public Map<String, String> getData() {
      TreeMap var1 = new TreeMap();

      for(int var2 = 0; var2 < this.data.size(); ++var2) {
         var1.put(this.data.getKeyAt(var2), (String)this.data.getValueAt(var2));
      }

      return Collections.unmodifiableMap(var1);
   }

   public IndexedReadOnlyStringMap getIndexedReadOnlyStringMap() {
      return this.data;
   }

   public void clear() {
      this.data.clear();
   }

   public MapMessage with(String var1, String var2) {
      this.put(var1, var2);
      return this;
   }

   public void put(String var1, String var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("No value provided for key " + var1);
      } else {
         this.validate(var1, var2);
         this.data.putValue(var1, var2);
      }
   }

   protected void validate(String var1, String var2) {
   }

   public void putAll(Map<String, String> var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.data.putValue((String)var3.getKey(), var3.getValue());
      }

   }

   public String get(String var1) {
      return (String)this.data.getValue(var1);
   }

   public String remove(String var1) {
      String var2 = (String)this.data.getValue(var1);
      this.data.remove(var1);
      return var2;
   }

   public String asString() {
      return this.format((MapMessage.MapFormat)null, new StringBuilder()).toString();
   }

   public String asString(String var1) {
      try {
         return this.format((MapMessage.MapFormat)EnglishEnums.valueOf(MapMessage.MapFormat.class, var1), new StringBuilder()).toString();
      } catch (IllegalArgumentException var3) {
         return this.asString();
      }
   }

   private StringBuilder format(MapMessage.MapFormat var1, StringBuilder var2) {
      if (var1 == null) {
         this.appendMap(var2);
      } else {
         switch(var1) {
         case XML:
            this.asXml(var2);
            break;
         case JSON:
            this.asJson(var2);
            break;
         case JAVA:
            this.asJava(var2);
            break;
         default:
            this.appendMap(var2);
         }
      }

      return var2;
   }

   public void asXml(StringBuilder var1) {
      var1.append("<Map>\n");

      for(int var2 = 0; var2 < this.data.size(); ++var2) {
         var1.append("  <Entry key=\"").append(this.data.getKeyAt(var2)).append("\">").append(this.data.getValueAt(var2)).append("</Entry>\n");
      }

      var1.append("</Map>");
   }

   public String getFormattedMessage() {
      return this.asString();
   }

   public String getFormattedMessage(String[] var1) {
      if (var1 != null && var1.length != 0) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            MapMessage.MapFormat var3 = MapMessage.MapFormat.lookupIgnoreCase(var1[var2]);
            if (var3 != null) {
               return this.format(var3, new StringBuilder()).toString();
            }
         }

         return this.asString();
      } else {
         return this.asString();
      }
   }

   protected void appendMap(StringBuilder var1) {
      for(int var2 = 0; var2 < this.data.size(); ++var2) {
         if (var2 > 0) {
            var1.append(' ');
         }

         StringBuilders.appendKeyDqValue(var1, this.data.getKeyAt(var2), this.data.getValueAt(var2));
      }

   }

   protected void asJson(StringBuilder var1) {
      var1.append('{');

      for(int var2 = 0; var2 < this.data.size(); ++var2) {
         if (var2 > 0) {
            var1.append(", ");
         }

         StringBuilders.appendDqValue(var1, this.data.getKeyAt(var2)).append(':');
         StringBuilders.appendDqValue(var1, this.data.getValueAt(var2));
      }

      var1.append('}');
   }

   protected void asJava(StringBuilder var1) {
      var1.append('{');

      for(int var2 = 0; var2 < this.data.size(); ++var2) {
         if (var2 > 0) {
            var1.append(", ");
         }

         StringBuilders.appendKeyDqValue(var1, this.data.getKeyAt(var2), this.data.getValueAt(var2));
      }

      var1.append('}');
   }

   public MapMessage newInstance(Map<String, String> var1) {
      return new MapMessage(var1);
   }

   public String toString() {
      return this.asString();
   }

   public void formatTo(StringBuilder var1) {
      this.format((MapMessage.MapFormat)null, var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         MapMessage var2 = (MapMessage)var1;
         return this.data.equals(var2.data);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.data.hashCode();
   }

   public Throwable getThrowable() {
      return null;
   }

   public static enum MapFormat {
      XML,
      JSON,
      JAVA;

      private MapFormat() {
      }

      public static MapMessage.MapFormat lookupIgnoreCase(String var0) {
         return XML.name().equalsIgnoreCase(var0) ? XML : (JSON.name().equalsIgnoreCase(var0) ? JSON : (JAVA.name().equalsIgnoreCase(var0) ? JAVA : null));
      }

      public static String[] names() {
         return new String[]{XML.name(), JSON.name(), JAVA.name()};
      }
   }
}
