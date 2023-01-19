package net.minecraft.client.gui.screens.social;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.reporting.ChatReportScreen;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;

public class PlayerEntry extends ContainerObjectSelectionList.Entry<PlayerEntry> {
   private static final ResourceLocation REPORT_BUTTON_LOCATION = new ResourceLocation("textures/gui/report_button.png");
   private static final int TOOLTIP_DELAY = 10;
   private static final int TOOLTIP_MAX_WIDTH = 150;
   private final Minecraft minecraft;
   private final List<AbstractWidget> children;
   private final UUID id;
   private final String playerName;
   private final Supplier<ResourceLocation> skinGetter;
   private boolean isRemoved;
   private boolean hasRecentMessages;
   private final boolean reportingEnabled;
   private final boolean playerReportable;
   @Nullable
   private Button hideButton;
   @Nullable
   private Button showButton;
   @Nullable
   private Button reportButton;
   final List<FormattedCharSequence> hideTooltip;
   final List<FormattedCharSequence> showTooltip;
   List<FormattedCharSequence> reportTooltip;
   float tooltipHoverTime;
   private static final Component HIDDEN = Component.translatable("gui.socialInteractions.status_hidden").withStyle(ChatFormatting.ITALIC);
   private static final Component BLOCKED = Component.translatable("gui.socialInteractions.status_blocked").withStyle(ChatFormatting.ITALIC);
   private static final Component OFFLINE = Component.translatable("gui.socialInteractions.status_offline").withStyle(ChatFormatting.ITALIC);
   private static final Component HIDDEN_OFFLINE = Component.translatable("gui.socialInteractions.status_hidden_offline").withStyle(ChatFormatting.ITALIC);
   private static final Component BLOCKED_OFFLINE = Component.translatable("gui.socialInteractions.status_blocked_offline").withStyle(ChatFormatting.ITALIC);
   private static final Component REPORT_DISABLED_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.report.disabled");
   private static final Component NOT_REPORTABLE_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.report.not_reportable");
   private static final Component HIDE_TEXT_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.hide");
   private static final Component SHOW_TEXT_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.show");
   private static final Component REPORT_PLAYER_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.report");
   private static final int SKIN_SIZE = 24;
   private static final int PADDING = 4;
   private static final int CHAT_TOGGLE_ICON_SIZE = 20;
   private static final int CHAT_TOGGLE_ICON_X = 0;
   private static final int CHAT_TOGGLE_ICON_Y = 38;
   public static final int SKIN_SHADE = FastColor.ARGB32.color(190, 0, 0, 0);
   public static final int BG_FILL = FastColor.ARGB32.color(255, 74, 74, 74);
   public static final int BG_FILL_REMOVED = FastColor.ARGB32.color(255, 48, 48, 48);
   public static final int PLAYERNAME_COLOR = FastColor.ARGB32.color(255, 255, 255, 255);
   public static final int PLAYER_STATUS_COLOR = FastColor.ARGB32.color(140, 255, 255, 255);

   public PlayerEntry(final Minecraft var1, final SocialInteractionsScreen var2, UUID var3, String var4, Supplier<ResourceLocation> var5, boolean var6) {
      super();
      this.minecraft = var1;
      this.id = var3;
      this.playerName = var4;
      this.skinGetter = var5;
      ReportingContext var7 = var1.getReportingContext();
      this.reportingEnabled = var7.sender().isEnabled();
      this.playerReportable = var6;
      final MutableComponent var8 = Component.translatable("gui.socialInteractions.narration.hide", var4);
      final MutableComponent var9 = Component.translatable("gui.socialInteractions.narration.show", var4);
      this.hideTooltip = var1.font.split(HIDE_TEXT_TOOLTIP, 150);
      this.showTooltip = var1.font.split(SHOW_TEXT_TOOLTIP, 150);
      this.reportTooltip = var1.font.split(this.getReportButtonText(false), 150);
      PlayerSocialManager var10 = var1.getPlayerSocialManager();
      boolean var11 = var1.getChatStatus().isChatAllowed(var1.isLocalServer());
      boolean var12 = !var1.player.getUUID().equals(var3);
      if (var12 && var11 && !var10.isBlocked(var3)) {
         this.reportButton = new ImageButton(
            0,
            0,
            20,
            20,
            0,
            0,
            20,
            REPORT_BUTTON_LOCATION,
            64,
            64,
            var3x -> var1.setScreen(new ChatReportScreen(var1.screen, var7, var3)),
            new Button.OnTooltip() {
               @Override
               public void onTooltip(Button var1x, PoseStack var2x, int var3, int var4) {
                  PlayerEntry.this.tooltipHoverTime += var1.getDeltaFrameTime();
                  if (PlayerEntry.this.tooltipHoverTime >= 10.0F) {
                     var2.setPostRenderRunnable(() -> PlayerEntry.postRenderTooltip(var2, var2x, PlayerEntry.this.reportTooltip, var3, var4));
                  }
               }
   
               @Override
               public void narrateTooltip(Consumer<Component> var1x) {
                  var1x.accept(PlayerEntry.this.getReportButtonText(true));
               }
            },
            Component.translatable("gui.socialInteractions.report")
         ) {
            @Override
            protected MutableComponent createNarrationMessage() {
               return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
            }
         };
         this.hideButton = new ImageButton(0, 0, 20, 20, 0, 38, 20, SocialInteractionsScreen.SOCIAL_INTERACTIONS_LOCATION, 256, 256, var4x -> {
            var10.hidePlayer(var3);
            this.onHiddenOrShown(true, Component.translatable("gui.socialInteractions.hidden_in_chat", var4));
         }, new Button.OnTooltip() {
            @Override
            public void onTooltip(Button var1x, PoseStack var2x, int var3, int var4) {
               PlayerEntry.this.tooltipHoverTime += var1.getDeltaFrameTime();
               if (PlayerEntry.this.tooltipHoverTime >= 10.0F) {
                  var2.setPostRenderRunnable(() -> PlayerEntry.postRenderTooltip(var2, var2x, PlayerEntry.this.hideTooltip, var3, var4));
               }
            }

            @Override
            public void narrateTooltip(Consumer<Component> var1x) {
               var1x.accept(var8);
            }
         }, Component.translatable("gui.socialInteractions.hide")) {
            @Override
            protected MutableComponent createNarrationMessage() {
               return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
            }
         };
         this.showButton = new ImageButton(0, 0, 20, 20, 20, 38, 20, SocialInteractionsScreen.SOCIAL_INTERACTIONS_LOCATION, 256, 256, var4x -> {
            var10.showPlayer(var3);
            this.onHiddenOrShown(false, Component.translatable("gui.socialInteractions.shown_in_chat", var4));
         }, new Button.OnTooltip() {
            @Override
            public void onTooltip(Button var1x, PoseStack var2x, int var3, int var4) {
               PlayerEntry.this.tooltipHoverTime += var1.getDeltaFrameTime();
               if (PlayerEntry.this.tooltipHoverTime >= 10.0F) {
                  var2.setPostRenderRunnable(() -> PlayerEntry.postRenderTooltip(var2, var2x, PlayerEntry.this.showTooltip, var3, var4));
               }
            }

            @Override
            public void narrateTooltip(Consumer<Component> var1x) {
               var1x.accept(var9);
            }
         }, Component.translatable("gui.socialInteractions.show")) {
            @Override
            protected MutableComponent createNarrationMessage() {
               return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
            }
         };
         this.showButton.visible = var10.isHidden(var3);
         this.hideButton.visible = !this.showButton.visible;
         this.reportButton.active = false;
         this.children = ImmutableList.of(this.hideButton, this.showButton, this.reportButton);
      } else {
         this.children = ImmutableList.of();
      }
   }

   Component getReportButtonText(boolean var1) {
      if (!this.playerReportable) {
         return NOT_REPORTABLE_TOOLTIP;
      } else if (!this.reportingEnabled) {
         return REPORT_DISABLED_TOOLTIP;
      } else if (!this.hasRecentMessages) {
         return Component.translatable("gui.socialInteractions.tooltip.report.no_messages", this.playerName);
      } else {
         return (Component)(var1 ? Component.translatable("gui.socialInteractions.narration.report", this.playerName) : REPORT_PLAYER_TOOLTIP);
      }
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
      int var11 = var4 + 4;
      int var12 = var3 + (var6 - 24) / 2;
      int var13 = var11 + 24 + 4;
      Component var15 = this.getStatusComponent();
      int var14;
      if (var15 == CommonComponents.EMPTY) {
         GuiComponent.fill(var1, var4, var3, var4 + var5, var3 + var6, BG_FILL);
         var14 = var3 + (var6 - 9) / 2;
      } else {
         GuiComponent.fill(var1, var4, var3, var4 + var5, var3 + var6, BG_FILL_REMOVED);
         var14 = var3 + (var6 - (9 + 9)) / 2;
         this.minecraft.font.draw(var1, var15, (float)var13, (float)(var14 + 12), PLAYER_STATUS_COLOR);
      }

      RenderSystem.setShaderTexture(0, this.skinGetter.get());
      PlayerFaceRenderer.draw(var1, var11, var12, 24);
      this.minecraft.font.draw(var1, this.playerName, (float)var13, (float)var14, PLAYERNAME_COLOR);
      if (this.isRemoved) {
         GuiComponent.fill(var1, var11, var12, var11 + 24, var12 + 24, SKIN_SHADE);
      }

      if (this.hideButton != null && this.showButton != null && this.reportButton != null) {
         float var16 = this.tooltipHoverTime;
         this.hideButton.x = var4 + (var5 - this.hideButton.getWidth() - 4) - 20 - 4;
         this.hideButton.y = var3 + (var6 - this.hideButton.getHeight()) / 2;
         this.hideButton.render(var1, var7, var8, var10);
         this.showButton.x = var4 + (var5 - this.showButton.getWidth() - 4) - 20 - 4;
         this.showButton.y = var3 + (var6 - this.showButton.getHeight()) / 2;
         this.showButton.render(var1, var7, var8, var10);
         this.reportButton.x = var4 + (var5 - this.showButton.getWidth() - 4);
         this.reportButton.y = var3 + (var6 - this.showButton.getHeight()) / 2;
         this.reportButton.render(var1, var7, var8, var10);
         if (var16 == this.tooltipHoverTime) {
            this.tooltipHoverTime = 0.0F;
         }
      }
   }

   @Override
   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   @Override
   public List<? extends NarratableEntry> narratables() {
      return this.children;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public UUID getPlayerId() {
      return this.id;
   }

   public void setRemoved(boolean var1) {
      this.isRemoved = var1;
   }

   public boolean isRemoved() {
      return this.isRemoved;
   }

   public void setHasRecentMessages(boolean var1) {
      this.hasRecentMessages = var1;
      if (this.reportButton != null) {
         this.reportButton.active = this.reportingEnabled && this.playerReportable && var1;
      }

      this.reportTooltip = this.minecraft.font.split(this.getReportButtonText(false), 150);
   }

   public boolean hasRecentMessages() {
      return this.hasRecentMessages;
   }

   private void onHiddenOrShown(boolean var1, Component var2) {
      this.showButton.visible = var1;
      this.hideButton.visible = !var1;
      this.minecraft.gui.getChat().addMessage(var2);
      this.minecraft.getNarrator().sayNow(var2);
   }

   MutableComponent getEntryNarationMessage(MutableComponent var1) {
      Component var2 = this.getStatusComponent();
      return var2 == CommonComponents.EMPTY
         ? Component.literal(this.playerName).append(", ").append(var1)
         : Component.literal(this.playerName).append(", ").append(var2).append(", ").append(var1);
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

   static void postRenderTooltip(SocialInteractionsScreen var0, PoseStack var1, List<FormattedCharSequence> var2, int var3, int var4) {
      var0.renderTooltip(var1, var2, var3, var4);
      var0.setPostRenderRunnable(null);
   }
}
