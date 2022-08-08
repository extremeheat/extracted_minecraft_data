package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.UserApiService;
import java.util.Objects;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.RollingMemoryChatLog;

public record ReportingContext(AbuseReportSender a, ReportEnvironment b, ChatLog c) {
   private final AbuseReportSender sender;
   private final ReportEnvironment environment;
   private final ChatLog chatLog;
   private static final int LOG_CAPACITY = 1024;

   public ReportingContext(AbuseReportSender var1, ReportEnvironment var2, ChatLog var3) {
      super();
      this.sender = var1;
      this.environment = var2;
      this.chatLog = var3;
   }

   public static ReportingContext create(ReportEnvironment var0, UserApiService var1) {
      RollingMemoryChatLog var2 = new RollingMemoryChatLog(1024);
      AbuseReportSender var3 = AbuseReportSender.create(var0, var1);
      return new ReportingContext(var3, var0, var2);
   }

   public boolean matches(ReportEnvironment var1) {
      return Objects.equals(this.environment, var1);
   }

   public AbuseReportSender sender() {
      return this.sender;
   }

   public ReportEnvironment environment() {
      return this.environment;
   }

   public ChatLog chatLog() {
      return this.chatLog;
   }
}
