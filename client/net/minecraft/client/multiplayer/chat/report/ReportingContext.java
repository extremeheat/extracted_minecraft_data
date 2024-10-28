package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.UserApiService;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.network.chat.Component;

public final class ReportingContext {
   private static final int LOG_CAPACITY = 1024;
   private final AbuseReportSender sender;
   private final ReportEnvironment environment;
   private final ChatLog chatLog;
   @Nullable
   private Report draftReport;

   public ReportingContext(AbuseReportSender var1, ReportEnvironment var2, ChatLog var3) {
      super();
      this.sender = var1;
      this.environment = var2;
      this.chatLog = var3;
   }

   public static ReportingContext create(ReportEnvironment var0, UserApiService var1) {
      ChatLog var2 = new ChatLog(1024);
      AbuseReportSender var3 = AbuseReportSender.create(var0, var1);
      return new ReportingContext(var3, var0, var2);
   }

   public void draftReportHandled(Minecraft var1, Screen var2, Runnable var3, boolean var4) {
      if (this.draftReport != null) {
         Report var5 = this.draftReport.copy();
         var1.setScreen(new ConfirmScreen((var5x) -> {
            this.setReportDraft((Report)null);
            if (var5x) {
               var1.setScreen(var5.createScreen(var2, this));
            } else {
               var3.run();
            }

         }, Component.translatable(var4 ? "gui.abuseReport.draft.quittotitle.title" : "gui.abuseReport.draft.title"), Component.translatable(var4 ? "gui.abuseReport.draft.quittotitle.content" : "gui.abuseReport.draft.content"), Component.translatable("gui.abuseReport.draft.edit"), Component.translatable("gui.abuseReport.draft.discard")));
      } else {
         var3.run();
      }

   }

   public AbuseReportSender sender() {
      return this.sender;
   }

   public ChatLog chatLog() {
      return this.chatLog;
   }

   public boolean matches(ReportEnvironment var1) {
      return Objects.equals(this.environment, var1);
   }

   public void setReportDraft(@Nullable Report var1) {
      this.draftReport = var1;
   }

   public boolean hasDraftReport() {
      return this.draftReport != null;
   }

   public boolean hasDraftReportFor(UUID var1) {
      return this.hasDraftReport() && this.draftReport.isReportedPlayer(var1);
   }
}
