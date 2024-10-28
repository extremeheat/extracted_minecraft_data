package net.minecraft.client.gui.screens.social;

import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.reporting.ReportPlayerScreen;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public class PlayerEntry extends ContainerObjectSelectionList.Entry<PlayerEntry> {
   private static final ResourceLocation DRAFT_REPORT_SPRITE = ResourceLocation.withDefaultNamespace("icon/draft_report");
   private static final Duration TOOLTIP_DELAY = Duration.ofMillis(500L);
   private static final WidgetSprites REPORT_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("social_interactions/report_button"), ResourceLocation.withDefaultNamespace("social_interactions/report_button_disabled"), ResourceLocation.withDefaultNamespace("social_interactions/report_button_highlighted"));
   private static final WidgetSprites MUTE_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("social_interactions/mute_button"), ResourceLocation.withDefaultNamespace("social_interactions/mute_button_highlighted"));
   private static final WidgetSprites UNMUTE_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("social_interactions/unmute_button"), ResourceLocation.withDefaultNamespace("social_interactions/unmute_button_highlighted"));
   private final Minecraft minecraft;
   private final List<AbstractWidget> children;
   private final UUID id;
   private final String playerName;
   private final Supplier<PlayerSkin> skinGetter;
   private boolean isRemoved;
   private boolean hasRecentMessages;
   private final boolean reportingEnabled;
   private final boolean hasDraftReport;
   private final boolean chatReportable;
   @Nullable
   private Button hideButton;
   @Nullable
   private Button showButton;
   @Nullable
   private Button reportButton;
   private float tooltipHoverTime;
   private static final Component HIDDEN;
   private static final Component BLOCKED;
   private static final Component OFFLINE;
   private static final Component HIDDEN_OFFLINE;
   private static final Component BLOCKED_OFFLINE;
   private static final Component REPORT_DISABLED_TOOLTIP;
   private static final Component HIDE_TEXT_TOOLTIP;
   private static final Component SHOW_TEXT_TOOLTIP;
   private static final Component REPORT_PLAYER_TOOLTIP;
   private static final int SKIN_SIZE = 24;
   private static final int PADDING = 4;
   public static final int SKIN_SHADE;
   private static final int CHAT_TOGGLE_ICON_SIZE = 20;
   public static final int BG_FILL;
   public static final int BG_FILL_REMOVED;
   public static final int PLAYERNAME_COLOR;
   public static final int PLAYER_STATUS_COLOR;

   public PlayerEntry(Minecraft var1, SocialInteractionsScreen var2, UUID var3, String var4, Supplier<PlayerSkin> var5, boolean var6) {
      super();
      this.minecraft = var1;
      this.id = var3;
      this.playerName = var4;
      this.skinGetter = var5;
      ReportingContext var7 = var1.getReportingContext();
      this.reportingEnabled = var7.sender().isEnabled();
      this.chatReportable = var6;
      this.hasDraftReport = var7.hasDraftReportFor(var3);
      MutableComponent var8 = Component.translatable("gui.socialInteractions.narration.hide", var4);
      MutableComponent var9 = Component.translatable("gui.socialInteractions.narration.show", var4);
      PlayerSocialManager var10 = var1.getPlayerSocialManager();
      boolean var11 = var1.getChatStatus().isChatAllowed(var1.isLocalServer());
      boolean var12 = !var1.player.getUUID().equals(var3);
      if (var12 && var11 && !var10.isBlocked(var3)) {
         this.reportButton = new ImageButton(0, 0, 20, 20, REPORT_BUTTON_SPRITES, (var4x) -> {
            var7.draftReportHandled(var1, var2, () -> {
               var1.setScreen(new ReportPlayerScreen(var2, var7, this));
            }, false);
         }, Component.translatable("gui.socialInteractions.report")) {
            protected MutableComponent createNarrationMessage() {
               return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
            }
         };
         this.reportButton.active = this.reportingEnabled;
         this.reportButton.setTooltip(this.createReportButtonTooltip());
         this.reportButton.setTooltipDelay(TOOLTIP_DELAY);
         this.hideButton = new ImageButton(0, 0, 20, 20, MUTE_BUTTON_SPRITES, (var4x) -> {
            var10.hidePlayer(var3);
            this.onHiddenOrShown(true, Component.translatable("gui.socialInteractions.hidden_in_chat", var4));
         }, Component.translatable("gui.socialInteractions.hide")) {
            protected MutableComponent createNarrationMessage() {
               return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
            }
         };
         this.hideButton.setTooltip(Tooltip.create(HIDE_TEXT_TOOLTIP, var8));
         this.hideButton.setTooltipDelay(TOOLTIP_DELAY);
         this.showButton = new ImageButton(0, 0, 20, 20, UNMUTE_BUTTON_SPRITES, (var4x) -> {
            var10.showPlayer(var3);
            this.onHiddenOrShown(false, Component.translatable("gui.socialInteractions.shown_in_chat", var4));
         }, Component.translatable("gui.socialInteractions.show")) {
            protected MutableComponent createNarrationMessage() {
               return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
            }
         };
         this.showButton.setTooltip(Tooltip.create(SHOW_TEXT_TOOLTIP, var9));
         this.showButton.setTooltipDelay(TOOLTIP_DELAY);
         this.children = new ArrayList();
         this.children.add(this.hideButton);
         this.children.add(this.reportButton);
         this.updateHideAndShowButton(var10.isHidden(this.id));
      } else {
         this.children = ImmutableList.of();
      }

   }

   private Tooltip createReportButtonTooltip() {
      return !this.reportingEnabled ? Tooltip.create(REPORT_DISABLED_TOOLTIP) : Tooltip.create(REPORT_PLAYER_TOOLTIP, Component.translatable("gui.socialInteractions.narration.report", this.playerName));
   }

   public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
      int var11 = var4 + 4;
      int var12 = var3 + (var6 - 24) / 2;
      int var13 = var11 + 24 + 4;
      Component var15 = this.getStatusComponent();
      int var14;
      if (var15 == CommonComponents.EMPTY) {
         var1.fill(var4, var3, var4 + var5, var3 + var6, BG_FILL);
         Objects.requireNonNull(this.minecraft.font);
         var14 = var3 + (var6 - 9) / 2;
      } else {
         var1.fill(var4, var3, var4 + var5, var3 + var6, BG_FILL_REMOVED);
         Objects.requireNonNull(this.minecraft.font);
         Objects.requireNonNull(this.minecraft.font);
         var14 = var3 + (var6 - (9 + 9)) / 2;
         var1.drawString(this.minecraft.font, var15, var13, var14 + 12, PLAYER_STATUS_COLOR, false);
      }

      PlayerFaceRenderer.draw(var1, (PlayerSkin)((PlayerSkin)this.skinGetter.get()), var11, var12, 24);
      var1.drawString(this.minecraft.font, this.playerName, var13, var14, PLAYERNAME_COLOR, false);
      if (this.isRemoved) {
         var1.fill(var11, var12, var11 + 24, var12 + 24, SKIN_SHADE);
      }

      if (this.hideButton != null && this.showButton != null && this.reportButton != null) {
         float var16 = this.tooltipHoverTime;
         this.hideButton.setX(var4 + (var5 - this.hideButton.getWidth() - 4) - 20 - 4);
         this.hideButton.setY(var3 + (var6 - this.hideButton.getHeight()) / 2);
         this.hideButton.render(var1, var7, var8, var10);
         this.showButton.setX(var4 + (var5 - this.showButton.getWidth() - 4) - 20 - 4);
         this.showButton.setY(var3 + (var6 - this.showButton.getHeight()) / 2);
         this.showButton.render(var1, var7, var8, var10);
         this.reportButton.setX(var4 + (var5 - this.showButton.getWidth() - 4));
         this.reportButton.setY(var3 + (var6 - this.showButton.getHeight()) / 2);
         this.reportButton.render(var1, var7, var8, var10);
         if (var16 == this.tooltipHoverTime) {
            this.tooltipHoverTime = 0.0F;
         }
      }

      if (this.hasDraftReport && this.reportButton != null) {
         var1.blitSprite(DRAFT_REPORT_SPRITE, this.reportButton.getX() + 5, this.reportButton.getY() + 1, 15, 15);
      }

   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   public List<? extends NarratableEntry> narratables() {
      return this.children;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public UUID getPlayerId() {
      return this.id;
   }

   public Supplier<PlayerSkin> getSkinGetter() {
      return this.skinGetter;
   }

   public void setRemoved(boolean var1) {
      this.isRemoved = var1;
   }

   public boolean isRemoved() {
      return this.isRemoved;
   }

   public void setHasRecentMessages(boolean var1) {
      this.hasRecentMessages = var1;
   }

   public boolean hasRecentMessages() {
      return this.hasRecentMessages;
   }

   public boolean isChatReportable() {
      return this.chatReportable;
   }

   private void onHiddenOrShown(boolean var1, Component var2) {
      this.updateHideAndShowButton(var1);
      this.minecraft.gui.getChat().addMessage(var2);
      this.minecraft.getNarrator().sayNow(var2);
   }

   private void updateHideAndShowButton(boolean var1) {
      this.showButton.visible = var1;
      this.hideButton.visible = !var1;
      this.children.set(0, var1 ? this.showButton : this.hideButton);
   }

   MutableComponent getEntryNarationMessage(MutableComponent var1) {
      Component var2 = this.getStatusComponent();
      return var2 == CommonComponents.EMPTY ? Component.literal(this.playerName).append(", ").append((Component)var1) : Component.literal(this.playerName).append(", ").append(var2).append(", ").append((Component)var1);
   }

   private Component getStatusComponent() {
      boolean var1 = this.minecraft.getPlayerSocialManager().isHidden(this.id);
      boolean var2 = this.minecraft.getPlayerSocialManager().isBlocked(this.id);
      if (var2 && this.isRemoved) {
         return BLOCKED_OFFLINE;
      } else if (var1 && this.isRemoved) {
         return HIDDEN_OFFLINE;
      } else if (var2) {
         return BLOCKED;
      } else if (var1) {
         return HIDDEN;
      } else {
         return this.isRemoved ? OFFLINE : CommonComponents.EMPTY;
      }
   }

   static {
      HIDDEN = Component.translatable("gui.socialInteractions.status_hidden").withStyle(ChatFormatting.ITALIC);
      BLOCKED = Component.translatable("gui.socialInteractions.status_blocked").withStyle(ChatFormatting.ITALIC);
      OFFLINE = Component.translatable("gui.socialInteractions.status_offline").withStyle(ChatFormatting.ITALIC);
      HIDDEN_OFFLINE = Component.translatable("gui.socialInteractions.status_hidden_offline").withStyle(ChatFormatting.ITALIC);
      BLOCKED_OFFLINE = Component.translatable("gui.socialInteractions.status_blocked_offline").withStyle(ChatFormatting.ITALIC);
      REPORT_DISABLED_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.report.disabled");
      HIDE_TEXT_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.hide");
      SHOW_TEXT_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.show");
      REPORT_PLAYER_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.report");
      SKIN_SHADE = FastColor.ARGB32.color(190, 0, 0, 0);
      BG_FILL = FastColor.ARGB32.color(255, 74, 74, 74);
      BG_FILL_REMOVED = FastColor.ARGB32.color(255, 48, 48, 48);
      PLAYERNAME_COLOR = FastColor.ARGB32.color(255, 255, 255, 255);
      PLAYER_STATUS_COLOR = FastColor.ARGB32.color(140, 255, 255, 255);
   }
}
