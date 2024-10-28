package net.minecraft.network;

import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.network.chat.Component;

public record DisconnectionDetails(Component reason, Optional<Path> report, Optional<String> bugReportLink) {
   public DisconnectionDetails(Component var1) {
      this(var1, Optional.empty(), Optional.empty());
   }

   public DisconnectionDetails(Component var1, Optional<Path> var2, Optional<String> var3) {
      super();
      this.reason = var1;
      this.report = var2;
      this.bugReportLink = var3;
   }

   public Component reason() {
      return this.reason;
   }

   public Optional<Path> report() {
      return this.report;
   }

   public Optional<String> bugReportLink() {
      return this.bugReportLink;
   }
}
