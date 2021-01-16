package org.apache.commons.codec.language.bm;

public enum NameType {
   ASHKENAZI("ash"),
   GENERIC("gen"),
   SEPHARDIC("sep");

   private final String name;

   private NameType(String var3) {
      this.name = var3;
   }

   public String getName() {
      return this.name;
   }
}
