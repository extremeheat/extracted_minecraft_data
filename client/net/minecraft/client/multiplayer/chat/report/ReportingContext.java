package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.UserApiService;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.ChatReportScreen;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.network.chat.Component;

public final class ReportingContext {
   private static final int LOG_CAPACITY = 1024;
   private final AbuseReportSender sender;
   private final ReportEnvironment environment;
   private final ChatLog chatLog;
   @Nullable
   private ChatReportBuilder.ChatReport chatReportDraft;

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

   public void draftReportHandled(Minecraft var1, @Nullable Screen var2, Runnable var3, boolean var4) {
      if (this.chatReportDraft != null) {
         ChatReportBuilder.ChatReport var5 = this.chatReportDraft.copy();
         var1.setScreen(
            new ConfirmScreen(
               var5x -> {
                  this.setChatReportDraft(null);
                  if (var5x) {
                     var1.setScreen(new ChatReportScreen(var2, this, var5));
                  } else {
                     var3.run();
                  }
               },
               Component.translatable(var4 ? "gui.chatReport.draft.quittotitle.title" : "gui.chatReport.draft.title"),
               Component.translatable(var4 ? "gui.chatReport.draft.quittotitle.content" : "gui.chatReport.draft.content"),
               Component.translatable("gui.chatReport.draft.edit"),
               Component.translatable("gui.chatReport.draft.discard")
            )
         );
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

   public void setChatReportDraft(@Nullable ChatReportBuilder.ChatReport var1) {
      this.chatReportDraft = var1;
   }

   public boolean hasDraftReport() {
      return this.chatReportDraft != null;
   }

   public boolean hasDraftReportFor(UUID var1) {
      return this.hasDraftReport() && this.chatReportDraft.isReportedPlayer(var1);
   }
}
