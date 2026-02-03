package com.mojang.realmsclient;

import java.util.Locale;

public enum Unit {
   B,
   KB,
   MB,
   GB;

   private static final int BASE_UNIT = 1024;

   private Unit() {
   }

   public static Unit getLargest(final long bytes) {
      if (bytes < 1024L) {
         return B;
      } else {
         try {
            int exp = (int)(Math.log((double)bytes) / Math.log(1024.0));
            String pre = String.valueOf("KMGTPE".charAt(exp - 1));
            return valueOf(pre + "B");
         } catch (Exception var4) {
            return GB;
         }
      }
   }

   public static double convertTo(final long bytes, final Unit unit) {
      return unit == B ? (double)bytes : (double)bytes / Math.pow(1024.0, (double)unit.ordinal());
   }

   public static String humanReadable(final long bytes) {
      int unit = 1024;
      if (bytes < 1024L) {
         return bytes + " B";
      } else {
         int exp = (int)(Math.log((double)bytes) / Math.log(1024.0));
         String pre = "" + "KMGTPE".charAt(exp - 1);
         return String.format(Locale.ROOT, "%.1f %sB", (double)bytes / Math.pow(1024.0, (double)exp), pre);
      }
   }

   public static String humanReadable(final long bytes, final Unit unit) {
      return String.format(Locale.ROOT, "%." + (unit == GB ? "1" : "0") + "f %s", convertTo(bytes, unit), unit.name());
   }

   // $FF: synthetic method
   private static Unit[] $values() {
      return new Unit[]{B, KB, MB, GB};
   }
}
