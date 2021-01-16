package org.apache.logging.log4j.message;

import java.io.Serializable;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.Strings;

public class StructuredDataId implements Serializable, StringBuilderFormattable {
   public static final StructuredDataId TIME_QUALITY = new StructuredDataId("timeQuality", (String[])null, new String[]{"tzKnown", "isSynced", "syncAccuracy"});
   public static final StructuredDataId ORIGIN = new StructuredDataId("origin", (String[])null, new String[]{"ip", "enterpriseId", "software", "swVersion"});
   public static final StructuredDataId META = new StructuredDataId("meta", (String[])null, new String[]{"sequenceId", "sysUpTime", "language"});
   public static final int RESERVED = -1;
   private static final long serialVersionUID = 9031746276396249990L;
   private static final int MAX_LENGTH = 32;
   private static final String AT_SIGN = "@";
   private final String name;
   private final int enterpriseNumber;
   private final String[] required;
   private final String[] optional;

   protected StructuredDataId(String var1, String[] var2, String[] var3) {
      super();
      int var4 = -1;
      if (var1 != null) {
         if (var1.length() > 32) {
            throw new IllegalArgumentException(String.format("Length of id %s exceeds maximum of %d characters", var1, 32));
         }

         var4 = var1.indexOf("@");
      }

      if (var4 > 0) {
         this.name = var1.substring(0, var4);
         this.enterpriseNumber = Integer.parseInt(var1.substring(var4 + 1));
      } else {
         this.name = var1;
         this.enterpriseNumber = -1;
      }

      this.required = var2;
      this.optional = var3;
   }

   public StructuredDataId(String var1, int var2, String[] var3, String[] var4) {
      super();
      if (var1 == null) {
         throw new IllegalArgumentException("No structured id name was supplied");
      } else if (var1.contains("@")) {
         throw new IllegalArgumentException("Structured id name cannot contain an " + Strings.quote("@"));
      } else if (var2 <= 0) {
         throw new IllegalArgumentException("No enterprise number was supplied");
      } else {
         this.name = var1;
         this.enterpriseNumber = var2;
         String var5 = var1 + "@" + var2;
         if (var5.length() > 32) {
            throw new IllegalArgumentException("Length of id exceeds maximum of 32 characters: " + var5);
         } else {
            this.required = var3;
            this.optional = var4;
         }
      }
   }

   public StructuredDataId makeId(StructuredDataId var1) {
      return var1 == null ? this : this.makeId(var1.getName(), var1.getEnterpriseNumber());
   }

   public StructuredDataId makeId(String var1, int var2) {
      if (var2 <= 0) {
         return this;
      } else {
         String var3;
         String[] var4;
         String[] var5;
         if (this.name != null) {
            var3 = this.name;
            var4 = this.required;
            var5 = this.optional;
         } else {
            var3 = var1;
            var4 = null;
            var5 = null;
         }

         return new StructuredDataId(var3, var2, var4, var5);
      }
   }

   public String[] getRequired() {
      return this.required;
   }

   public String[] getOptional() {
      return this.optional;
   }

   public String getName() {
      return this.name;
   }

   public int getEnterpriseNumber() {
      return this.enterpriseNumber;
   }

   public boolean isReserved() {
      return this.enterpriseNumber <= 0;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(this.name.length() + 10);
      this.formatTo(var1);
      return var1.toString();
   }

   public void formatTo(StringBuilder var1) {
      if (this.isReserved()) {
         var1.append(this.name);
      } else {
         var1.append(this.name).append("@").append(this.enterpriseNumber);
      }

   }
}
