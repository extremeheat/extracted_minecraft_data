package org.apache.commons.io;

import java.io.Serializable;

public enum IOCase implements Serializable {
   SENSITIVE("Sensitive", true),
   INSENSITIVE("Insensitive", false),
   SYSTEM("System", !FilenameUtils.isSystemWindows());

   private static final long serialVersionUID = -6343169151696340687L;
   private final String name;
   private final transient boolean sensitive;

   public static IOCase forName(String var0) {
      IOCase[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         IOCase var4 = var1[var3];
         if (var4.getName().equals(var0)) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Invalid IOCase name: " + var0);
   }

   private IOCase(String var3, boolean var4) {
      this.name = var3;
      this.sensitive = var4;
   }

   private Object readResolve() {
      return forName(this.name);
   }

   public String getName() {
      return this.name;
   }

   public boolean isCaseSensitive() {
      return this.sensitive;
   }

   public int checkCompareTo(String var1, String var2) {
      if (var1 != null && var2 != null) {
         return this.sensitive ? var1.compareTo(var2) : var1.compareToIgnoreCase(var2);
      } else {
         throw new NullPointerException("The strings must not be null");
      }
   }

   public boolean checkEquals(String var1, String var2) {
      if (var1 != null && var2 != null) {
         return this.sensitive ? var1.equals(var2) : var1.equalsIgnoreCase(var2);
      } else {
         throw new NullPointerException("The strings must not be null");
      }
   }

   public boolean checkStartsWith(String var1, String var2) {
      return var1.regionMatches(!this.sensitive, 0, var2, 0, var2.length());
   }

   public boolean checkEndsWith(String var1, String var2) {
      int var3 = var2.length();
      return var1.regionMatches(!this.sensitive, var1.length() - var3, var2, 0, var3);
   }

   public int checkIndexOf(String var1, int var2, String var3) {
      int var4 = var1.length() - var3.length();
      if (var4 >= var2) {
         for(int var5 = var2; var5 <= var4; ++var5) {
            if (this.checkRegionMatches(var1, var5, var3)) {
               return var5;
            }
         }
      }

      return -1;
   }

   public boolean checkRegionMatches(String var1, int var2, String var3) {
      return var1.regionMatches(!this.sensitive, var2, var3, 0, var3.length());
   }

   public String toString() {
      return this.name;
   }
}
