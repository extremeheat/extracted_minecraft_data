package com.mojang.realmsclient;

import java.util.Locale;

public enum Unit {
   // $FF: renamed from: B com.mojang.realmsclient.Unit
   field_212,
   // $FF: renamed from: KB com.mojang.realmsclient.Unit
   field_213,
   // $FF: renamed from: MB com.mojang.realmsclient.Unit
   field_214,
   // $FF: renamed from: GB com.mojang.realmsclient.Unit
   field_215;

   private static final int BASE_UNIT = 1024;

   private Unit() {
   }

   public static Unit getLargest(long var0) {
      if (var0 < 1024L) {
         return field_212;
      } else {
         try {
            int var2 = (int)(Math.log((double)var0) / Math.log(1024.0D));
            String var3 = String.valueOf("KMGTPE".charAt(var2 - 1));
            return valueOf(var3 + "B");
         } catch (Exception var4) {
            return field_215;
         }
      }
   }

   public static double convertTo(long var0, Unit var2) {
      return var2 == field_212 ? (double)var0 : (double)var0 / Math.pow(1024.0D, (double)var2.ordinal());
   }

   public static String humanReadable(long var0) {
      boolean var2 = true;
      if (var0 < 1024L) {
         return var0 + " B";
      } else {
         int var3 = (int)(Math.log((double)var0) / Math.log(1024.0D));
         String var4 = "KMGTPE".charAt(var3 - 1).makeConcatWithConstants<invokedynamic>("KMGTPE".charAt(var3 - 1));
         return String.format(Locale.ROOT, "%.1f %sB", (double)var0 / Math.pow(1024.0D, (double)var3), var4);
      }
   }

   public static String humanReadable(long var0, Unit var2) {
      return String.format("%." + (var2 == field_215 ? "1" : "0") + "f %s", convertTo(var0, var2), var2.name());
   }

   // $FF: synthetic method
   private static Unit[] $values() {
      return new Unit[]{field_212, field_213, field_214, field_215};
   }
}
