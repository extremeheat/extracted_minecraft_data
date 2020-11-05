package net.minecraft.client.gui.screens.social;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;

public class PlayerEntry extends ContainerObjectSelectionList.Entry<PlayerEntry> {
   private final Minecraft minecraft;
   private final List<GuiEventListener> children;
   private final UUID id;
   private final String playerName;
   private final Supplier<ResourceLocation> skinGetter;
   private boolean isRemoved;
   @Nullable
   private Button hideButton;
   @Nullable
   private Button showButton;
   private final List<FormattedCharSequence> hideTooltip;
   private final List<FormattedCharSequence> showTooltip;
   private float tooltipHoverTime;
   private static final Component HIDDEN;
   private static final Component BLOCKED;
   private static final Component OFFLINE;
   private static final Component HIDDEN_OFFLINE;
   private static final Component BLOCKED_OFFLINE;
   public static final int SKIN_SHADE;
   public static final int BG_FILL;
   public static final int BG_FILL_REMOVED;
   public static final int PLAYERNAME_COLOR;
   public static final int PLAYER_STATUS_COLOR;

   public PlayerEntry(Minecraft var1, SocialInteractionsScreen var2, UUID var3, String var4, Supplier<ResourceLocation> var5) {
      super();
      this.minecraft = var1;
      this.id = var3;
      this.playerName = var4;
      this.skinGetter = var5;
      this.hideTooltip = var1.font.split(new TranslatableComponent("gui.socialInteractions.tooltip.hide", new Object[]{var4}), 150);
      this.showTooltip = var1.font.split(new TranslatableComponent("gui.socialInteractions.tooltip.show", new Object[]{var4}), 150);
      PlayerSocialManager var6 = var1.getPlayerSocialManager();
      if (!var1.player.getGameProfile().getId().equals(var3) && !var6.isBlocked(var3)) {
         this.hideButton = new ImageButton(0, 0, 20, 20, 0, 38, 20, SocialInteractionsScreen.SOCIAL_INTERACTIONS_LOCATION, 256, 256, (var4x) -> {
            var6.hidePlayer(var3);
            this.onHiddenOrShown(true, new TranslatableComponent("gui.socialInteractions.hidden_in_chat", new Object[]{var4}));
         }, (var3x, var4x, var5x, var6x) -> {
            this.tooltipHoverTime += var1.getDeltaFrameTime();
            if (this.tooltipHoverTime >= 10.0F) {
               var2.setPostRenderRunnable(() -> {
                  postRenderTooltip(var2, var4x, this.hideTooltip, var5x, var6x);
               });
            }

         }, new TranslatableComponent("gui.socialInteractions.hide")) {
            protected MutableComponent createNarrationMessage() {
               return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
            }
         };
         this.showButton = new ImageButton(0, 0, 20, 20, 20, 38, 20, SocialInteractionsScreen.SOCIAL_INTERACTIONS_LOCATION, 256, 256, (var4x) -> {
            var6.showPlayer(var3);
            this.onHiddenOrShown(false, new TranslatableComponent("gui.socialInteractions.shown_in_chat", new Object[]{var4}));
         }, (var3x, var4x, var5x, var6x) -> {
            this.tooltipHoverTime += var1.getDeltaFrameTime();
            if (this.tooltipHoverTime >= 10.0F) {
               var2.setPostRenderRunnable(() -> {
                  postRenderTooltip(var2, var4x, this.showTooltip, var5x, var6x);
               });
            }

         }, new TranslatableComponent("gui.socialInteractions.show")) {
            protected MutableComponent createNarrationMessage() {
               return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
            }
         };
         this.showButton.visible = var6.isHidden(var3);
         this.hideButton.visible = !this.showButton.visible;
         this.children = ImmutableList.of(this.hideButton, this.showButton);
      } else {
         this.children = ImmutableList.of();
      }

   }

   public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
      int var11 = var4 + 4;
      int var12 = var3 + (var6 - 24) / 2;
      int var13 = var11 + 24 + 4;
      Component var15 = this.getStatusComponent();
      int var14;
      if (var15 == TextComponent.EMPTY) {
         GuiComponent.fill(var1, var4, var3, var4 + var5, var3 + var6, BG_FILL);
         this.minecraft.font.getClass();
         var14 = var3 + (var6 - 9) / 2;
      } else {
         GuiComponent.fill(var1, var4, var3, var4 + var5, var3 + var6, BG_FILL_REMOVED);
         this.minecraft.font.getClass();
         this.minecraft.font.getClass();
         var14 = var3 + (var6 - (9 + 9)) / 2;
         this.minecraft.font.draw(var1, var15, (float)var13, (float)(var14 + 12), PLAYER_STATUS_COLOR);
      }

      this.minecraft.getTextureManager().bind((ResourceLocation)this.skinGetter.get());
      GuiComponent.blit(var1, var11, var12, 24, 24, 8.0F, 8.0F, 8, 8, 64, 64);
      RenderSystem.enableBlend();
      GuiComponent.blit(var1, var11, var12, 24, 24, 40.0F, 8.0F, 8, 8, 64, 64);
      RenderSystem.disableBlend();
      this.minecraft.font.draw(var1, this.playerName, (float)var13, (float)var14, PLAYERNAME_COLOR);
      if (this.isRemoved) {
         GuiComponent.fill(var1, var11, var12, var11 + 24, var12 + 24, SKIN_SHADE);
      }

      if (this.hideButton != null && this.showButton != null) {
         float var16 = this.tooltipHoverTime;
         this.hideButton.x = var4 + (var5 - this.hideButton.getWidth() - 4);
         this.hideButton.y = var3 + (var6 - this.hideButton.getHeight()) / 2;
         this.hideButton.render(var1, var7, var8, var10);
         this.showButton.x = var4 + (var5 - this.showButton.getWidth() - 4);
         this.showButton.y = var3 + (var6 - this.showButton.getHeight()) / 2;
         this.showButton.render(var1, var7, var8, var10);
         if (var16 == this.tooltipHoverTime) {
            this.tooltipHoverTime = 0.0F;
         }
      }

   }

   public List<? extends GuiEventListener> children() {
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

   private void onHiddenOrShown(boolean var1, Component var2) {
      this.showButton.visible = var1;
      this.hideButton.visible = !var1;
      this.minecraft.gui.getChat().addMessage(var2);
      NarratorChatListener.INSTANCE.sayNow(var2.getString());
   }

   private MutableComponent getEntryNarationMessage(MutableComponent var1) {
      Component var2 = this.getStatusComponent();
      return var2 == TextComponent.EMPTY ? (new TextComponent(this.playerName)).append(", ").append((Component)var1) : (new TextComponent(this.playerName)).append(", ").append(var2).append(", ").append((Component)var1);
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
         return this.isRemoved ? OFFLINE : TextComponent.EMPTY;
      }
   }

   private static void postRenderTooltip(SocialInteractionsScreen var0, PoseStack var1, List<FormattedCharSequence> var2, int var3, int var4) {
      var0.renderTooltip(var1, var2, var3, var4);
      var0.setPostRenderRunnable((Runnable)null);
   }

   static {
      HIDDEN = (new TranslatableComponent("gui.socialInteractions.status_hidden")).withStyle(ChatFormatting.ITALIC);
      BLOCKED = (new TranslatableComponent("gui.socialInteractions.status_blocked")).withStyle(ChatFormatting.ITALIC);
      OFFLINE = (new TranslatableComponent("gui.socialInteractions.status_offline")).withStyle(ChatFormatting.ITALIC);
      HIDDEN_OFFLINE = (new TranslatableComponent("gui.socialInteractions.status_hidden_offline")).withStyle(ChatFormatting.ITALIC);
      BLOCKED_OFFLINE = (new TranslatableComponent("gui.socialInteractions.status_blocked_offline")).withStyle(ChatFormatting.ITALIC);
      SKIN_SHADE = FastColor.ARGB32.color(190, 0, 0, 0);
      BG_FILL = FastColor.ARGB32.color(255, 74, 74, 74);
      BG_FILL_REMOVED = FastColor.ARGB32.color(255, 48, 48, 48);
      PLAYERNAME_COLOR = FastColor.ARGB32.color(255, 255, 255, 255);
      PLAYER_STATUS_COLOR = FastColor.ARGB32.color(140, 255, 255, 255);
   }
}
