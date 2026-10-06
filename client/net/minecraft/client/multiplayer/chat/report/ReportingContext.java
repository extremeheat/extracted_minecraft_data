package net.minecraft.client.multiplayer.chat.report;

import com.google.common.base.Predicates;
import com.mojang.authlib.minecraft.UserApiService;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.DiscardReportWarningScreen;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ReportingContext {
   private static final Component DRAFT_TITLE = Component.translatable("gui.abuseReport.draft.title.new");
   private static final Component DRAFT_MESSAGE = Component.translatable("gui.abuseReport.draft.content");
   private static final Component CONTINUE_EDITING = Component.translatable("gui.abuseReport.draft.edit");
   private static final Component DISCARD_AND_RESTART = Component.translatable("gui.abuseReport.draft.restart");
   private static final Component QUIT_TITLE = Component.translatable("gui.abuseReport.draft.quittotitle.title.new");
   private static final Component QUIT_MESSAGE = Component.translatable("gui.abuseReport.draft.quittotitle.content.new");
   private static final Component DISCARD_AND_QUIT = Component.translatable("gui.abuseReport.draft.quittotitle.discard");
   private static final Tooltip DISCARD_TOOLTIP = Tooltip.create(Component.translatable("gui.abuseReport.draft.quittotitle.tooltip"));
   private static final Tooltip DISCARD_TOOLTIP_CHAT = Tooltip.create(Component.translatable("gui.abuseReport.draft.quittotitle.tooltip.chat"));
   private static final int LOG_CAPACITY = 1024;
   private final AbuseReportSender sender;
   private final ReportEnvironment environment;
   private final ChatLog chatLog;
   private @Nullable Report draftReport;

   public ReportingContext(final AbuseReportSender sender, final ReportEnvironment environment, final ChatLog chatLog) {
      super();
      this.sender = sender;
      this.environment = environment;
      this.chatLog = chatLog;
   }

   public static ReportingContext create(final ReportEnvironment environment, final UserApiService userApiService) {
      ChatLog chatLog = new ChatLog(1024);
      AbuseReportSender sender = AbuseReportSender.create(environment, userApiService);
      return new ReportingContext(sender, environment, chatLog);
   }

   public void draftReportHandled(final Minecraft minecraft, final Screen lastScreen, final Runnable onDiscard, final boolean quitToTitle, final Predicate<Report> canHandle) {
      if (this.draftReport != null && canHandle.test(this.draftReport)) {
         Report report = this.draftReport.copy();
         minecraft.gui.setScreen(new DiscardReportWarningScreen(lastScreen, quitToTitle ? QUIT_TITLE : DRAFT_TITLE, quitToTitle ? QUIT_MESSAGE : DRAFT_MESSAGE, (builder) -> {
            builder.addButton(CONTINUE_EDITING, true, (var4) -> minecraft.gui.setScreen(report.createScreen(lastScreen, this)));
            Button discardButton = builder.addButton(quitToTitle ? DISCARD_AND_QUIT : DISCARD_AND_RESTART, false, (var2) -> {
               this.setReportDraft((Report)null);
               onDiscard.run();
            });
            if (quitToTitle) {
               discardButton.setTooltip(this.draftReport instanceof ChatReport ? DISCARD_TOOLTIP_CHAT : DISCARD_TOOLTIP);
            }

         }));
      } else {
         onDiscard.run();
      }

   }

   public void draftReportHandled(final Minecraft minecraft, final Screen lastScreen, final Runnable onDiscard, final boolean quitToTitle) {
      this.draftReportHandled(minecraft, lastScreen, onDiscard, quitToTitle, Predicates.alwaysTrue());
   }

   public AbuseReportSender sender() {
      return this.sender;
   }

   public ChatLog chatLog() {
      return this.chatLog;
   }

   public boolean matches(final ReportEnvironment environment) {
      return Objects.equals(this.environment, environment);
   }

   public void setReportDraft(final @Nullable Report draftReport) {
      this.draftReport = draftReport;
   }

   public boolean hasDraftReport() {
      return this.draftReport != null;
   }

   public @Nullable Report getDraftReport() {
      return this.draftReport;
   }

   public boolean hasDraftReportFor(final UUID playerId) {
      return this.hasDraftReport() && this.draftReport.isReportedPlayer(playerId);
   }
}
