package net.minecraft.client.multiplayer.chat.report;

import java.util.Locale;

public enum ReportType {
   CHAT("chat"),
   SKIN("skin"),
   USERNAME("username");

   private final String backendName;

   private ReportType(String var3) {
      this.backendName = var3.toUpperCase(Locale.ROOT);
   }

   public String backendName() {
      return this.backendName;
   }
}