package org.apache.logging.log4j.message;

import java.util.Map;
import org.apache.logging.log4j.util.EnglishEnums;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;

@AsynchronouslyFormattable
public class StructuredDataMessage extends MapMessage implements StringBuilderFormattable {
   private static final long serialVersionUID = 1703221292892071920L;
   private static final int MAX_LENGTH = 32;
   private static final int HASHVAL = 31;
   private StructuredDataId id;
   private String message;
   private String type;

   public StructuredDataMessage(String var1, String var2, String var3) {
      super();
      this.id = new StructuredDataId(var1, (String[])null, (String[])null);
      this.message = var2;
      this.type = var3;
   }

   public StructuredDataMessage(String var1, String var2, String var3, Map<String, String> var4) {
      super(var4);
      this.id = new StructuredDataId(var1, (String[])null, (String[])null);
      this.message = var2;
      this.type = var3;
   }

   public StructuredDataMessage(StructuredDataId var1, String var2, String var3) {
      super();
      this.id = var1;
      this.message = var2;
      this.type = var3;
   }

   public StructuredDataMessage(StructuredDataId var1, String var2, String var3, Map<String, String> var4) {
      super(var4);
      this.id = var1;
      this.message = var2;
      this.type = var3;
   }

   private StructuredDataMessage(StructuredDataMessage var1, Map<String, String> var2) {
      super(var2);
      this.id = var1.id;
      this.message = var1.message;
      this.type = var1.type;
   }

   protected StructuredDataMessage() {
      super();
   }

   public StructuredDataMessage with(String var1, String var2) {
      this.put(var1, var2);
      return this;
   }

   public String[] getFormats() {
      String[] var1 = new String[StructuredDataMessage.Format.values().length];
      int var2 = 0;
      StructuredDataMessage.Format[] var3 = StructuredDataMessage.Format.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         StructuredDataMessage.Format var6 = var3[var5];
         var1[var2++] = var6.name();
      }

      return var1;
   }

   public StructuredDataId getId() {
      return this.id;
   }

   protected void setId(String var1) {
      this.id = new StructuredDataId(var1, (String[])null, (String[])null);
   }

   protected void setId(StructuredDataId var1) {
      this.id = var1;
   }

   public String getType() {
      return this.type;
   }

   protected void setType(String var1) {
      if (var1.length() > 32) {
         throw new IllegalArgumentException("structured data type exceeds maximum length of 32 characters: " + var1);
      } else {
         this.type = var1;
      }
   }

   public void formatTo(StringBuilder var1) {
      this.asString(StructuredDataMessage.Format.FULL, (StructuredDataId)null, var1);
   }

   public String getFormat() {
      return this.message;
   }

   protected void setMessageFormat(String var1) {
      this.message = var1;
   }

   protected void validate(String var1, String var2) {
      this.validateKey(var1);
   }

   private void validateKey(String var1) {
      if (var1.length() > 32) {
         throw new IllegalArgumentException("Structured data keys are limited to 32 characters. key: " + var1);
      } else {
         for(int var2 = 0; var2 < var1.length(); ++var2) {
            char var3 = var1.charAt(var2);
            if (var3 < '!' || var3 > '~' || var3 == '=' || var3 == ']' || var3 == '"') {
               throw new IllegalArgumentException("Structured data keys must contain printable US ASCII charactersand may not contain a space, =, ], or \"");
            }
         }

      }
   }

   public String asString() {
      return this.asString(StructuredDataMessage.Format.FULL, (StructuredDataId)null);
   }

   public String asString(String var1) {
      try {
         return this.asString((StructuredDataMessage.Format)EnglishEnums.valueOf(StructuredDataMessage.Format.class, var1), (StructuredDataId)null);
      } catch (IllegalArgumentException var3) {
         return this.asString();
      }
   }

   public final String asString(StructuredDataMessage.Format var1, StructuredDataId var2) {
      StringBuilder var3 = new StringBuilder();
      this.asString(var1, var2, var3);
      return var3.toString();
   }

   public final void asString(StructuredDataMessage.Format var1, StructuredDataId var2, StringBuilder var3) {
      boolean var4 = StructuredDataMessage.Format.FULL.equals(var1);
      if (var4) {
         String var5 = this.getType();
         if (var5 == null) {
            return;
         }

         var3.append(this.getType()).append(' ');
      }

      StructuredDataId var7 = this.getId();
      if (var7 != null) {
         var7 = var7.makeId(var2);
      } else {
         var7 = var2;
      }

      if (var7 != null && var7.getName() != null) {
         var3.append('[');
         StringBuilders.appendValue(var3, var7);
         var3.append(' ');
         this.appendMap(var3);
         var3.append(']');
         if (var4) {
            String var6 = this.getFormat();
            if (var6 != null) {
               var3.append(' ').append(var6);
            }
         }

      }
   }

   public String getFormattedMessage() {
      return this.asString(StructuredDataMessage.Format.FULL, (StructuredDataId)null);
   }

   public String getFormattedMessage(String[] var1) {
      if (var1 != null && var1.length > 0) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            String var3 = var1[var2];
            if (StructuredDataMessage.Format.XML.name().equalsIgnoreCase(var3)) {
               return this.asXml();
            }

            if (StructuredDataMessage.Format.FULL.name().equalsIgnoreCase(var3)) {
               return this.asString(StructuredDataMessage.Format.FULL, (StructuredDataId)null);
            }
         }

         return this.asString((StructuredDataMessage.Format)null, (StructuredDataId)null);
      } else {
         return this.asString(StructuredDataMessage.Format.FULL, (StructuredDataId)null);
      }
   }

   private String asXml() {
      StringBuilder var1 = new StringBuilder();
      StructuredDataId var2 = this.getId();
      if (var2 != null && var2.getName() != null && this.type != null) {
         var1.append("<StructuredData>\n");
         var1.append("<type>").append(this.type).append("</type>\n");
         var1.append("<id>").append(var2).append("</id>\n");
         super.asXml(var1);
         var1.append("</StructuredData>\n");
         return var1.toString();
      } else {
         return var1.toString();
      }
   }

   public String toString() {
      return this.asString((StructuredDataMessage.Format)null, (StructuredDataId)null);
   }

   public MapMessage newInstance(Map<String, String> var1) {
      return new StructuredDataMessage(this, var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         StructuredDataMessage var2 = (StructuredDataMessage)var1;
         if (!super.equals(var1)) {
            return false;
         } else {
            label48: {
               if (this.type != null) {
                  if (this.type.equals(var2.type)) {
                     break label48;
                  }
               } else if (var2.type == null) {
                  break label48;
               }

               return false;
            }

            if (this.id != null) {
               if (!this.id.equals(var2.id)) {
                  return false;
               }
            } else if (var2.id != null) {
               return false;
            }

            if (this.message != null) {
               if (!this.message.equals(var2.message)) {
                  return false;
               }
            } else if (var2.message != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + (this.type != null ? this.type.hashCode() : 0);
      var1 = 31 * var1 + (this.id != null ? this.id.hashCode() : 0);
      var1 = 31 * var1 + (this.message != null ? this.message.hashCode() : 0);
      return var1;
   }

   public static enum Format {
      XML,
      FULL;

      private Format() {
      }
   }
}
