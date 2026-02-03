package net.minecraft.client.multiplayer.chat.report;

import java.util.Locale;

public enum ReportType {
   CHAT("chat"),
   SKIN("skin"),
   USERNAME("username");

   private final String backendName;

   private ReportType(final String name) {
      this.backendName = name.toUpperCase(Locale.ROOT);
   }

   public String backendName() {
      return this.backendName;
   }

   // $FF: synthetic method
   private static ReportType[] $values() {
      return new ReportType[]{CHAT, SKIN, USERNAME};
   }
}
