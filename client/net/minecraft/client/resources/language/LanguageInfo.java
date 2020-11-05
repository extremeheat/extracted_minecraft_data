package net.minecraft.client.resources.language;

import com.mojang.bridge.game.Language;

public class LanguageInfo implements Language, Comparable<LanguageInfo> {
   private final String code;
   private final String region;
   private final String name;
   private final boolean bidirectional;

   public LanguageInfo(String var1, String var2, String var3, boolean var4) {
      super();
      this.code = var1;
      this.region = var2;
      this.name = var3;
      this.bidirectional = var4;
   }

   public String getCode() {
      return this.code;
   }

   public String getName() {
      return this.name;
   }

   public String getRegion() {
      return this.region;
   }

   public boolean isBidirectional() {
      return this.bidirectional;
   }

   public String toString() {
      return String.format("%s (%s)", this.name, this.region);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof LanguageInfo) ? false : this.code.equals(((LanguageInfo)var1).code);
      }
   }

   public int hashCode() {
      return this.code.hashCode();
   }

   public int compareTo(LanguageInfo var1) {
      return this.code.compareTo(var1.code);
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((LanguageInfo)var1);
   }
}
