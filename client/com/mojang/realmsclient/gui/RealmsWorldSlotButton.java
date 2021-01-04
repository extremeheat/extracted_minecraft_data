package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsButtonProxy;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;

public class RealmsWorldSlotButton extends RealmsButton {
   private final Supplier<RealmsServer> serverDataProvider;
   private final Consumer<String> toolTipSetter;
   private final RealmsWorldSlotButton.Listener listener;
   private final int slotIndex;
   private int animTick;
   private RealmsWorldSlotButton.State state;

   public RealmsWorldSlotButton(int var1, int var2, int var3, int var4, Supplier<RealmsServer> var5, Consumer<String> var6, int var7, int var8, RealmsWorldSlotButton.Listener var9) {
      super(var7, var1, var2, var3, var4, "");
      this.serverDataProvider = var5;
      this.slotIndex = var8;
      this.toolTipSetter = var6;
      this.listener = var9;
   }

   public void render(int var1, int var2, float var3) {
      super.render(var1, var2, var3);
   }

   public void tick() {
      ++this.animTick;
      RealmsServer var1 = (RealmsServer)this.serverDataProvider.get();
      if (var1 != null) {
         RealmsWorldOptions var4 = (RealmsWorldOptions)var1.slots.get(this.slotIndex);
         boolean var9 = this.slotIndex == 4;
         boolean var2;
         String var3;
         long var5;
         String var7;
         boolean var8;
         if (var9) {
            var2 = var1.worldType.equals(RealmsServer.WorldType.MINIGAME);
            var3 = "Minigame";
            var5 = (long)var1.minigameId;
            var7 = var1.minigameImage;
            var8 = var1.minigameId == -1;
         } else {
            var2 = var1.activeSlot == this.slotIndex && !var1.worldType.equals(RealmsServer.WorldType.MINIGAME);
            var3 = var4.getSlotName(this.slotIndex);
            var5 = var4.templateId;
            var7 = var4.templateImage;
            var8 = var4.empty;
         }

         String var11 = null;
         RealmsWorldSlotButton.Action var10;
         if (var2) {
            boolean var12 = var1.state == RealmsServer.State.OPEN || var1.state == RealmsServer.State.CLOSED;
            if (!var1.expired && var12) {
               var10 = RealmsWorldSlotButton.Action.JOIN;
               var11 = Realms.getLocalizedString("mco.configure.world.slot.tooltip.active");
            } else {
               var10 = RealmsWorldSlotButton.Action.NOTHING;
            }
         } else if (var9) {
            if (var1.expired) {
               var10 = RealmsWorldSlotButton.Action.NOTHING;
            } else {
               var10 = RealmsWorldSlotButton.Action.SWITCH_SLOT;
               var11 = Realms.getLocalizedString("mco.configure.world.slot.tooltip.minigame");
            }
         } else {
            var10 = RealmsWorldSlotButton.Action.SWITCH_SLOT;
            var11 = Realms.getLocalizedString("mco.configure.world.slot.tooltip");
         }

         this.state = new RealmsWorldSlotButton.State(var2, var3, var5, var7, var8, var9, var10, var11);
         String var13;
         if (var10 == RealmsWorldSlotButton.Action.NOTHING) {
            var13 = var3;
         } else if (var9) {
            if (var8) {
               var13 = var11;
            } else {
               var13 = var11 + " " + var3 + " " + var1.minigameName;
            }
         } else {
            var13 = var11 + " " + var3;
         }

         this.setMessage(var13);
      }
   }

   public void renderButton(int var1, int var2, float var3) {
      if (this.state != null) {
         RealmsButtonProxy var4 = this.getProxy();
         this.drawSlotFrame(var4.x, var4.y, var1, var2, this.state.isCurrentlyActiveSlot, this.state.slotName, this.slotIndex, this.state.imageId, this.state.image, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
      }
   }

   private void drawSlotFrame(int var1, int var2, int var3, int var4, boolean var5, String var6, int var7, long var8, @Nullable String var10, boolean var11, boolean var12, RealmsWorldSlotButton.Action var13, @Nullable String var14) {
      boolean var15 = this.getProxy().isHovered();
      if (this.getProxy().isMouseOver((double)var3, (double)var4) && var14 != null) {
         this.toolTipSetter.accept(var14);
      }

      if (var12) {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(var8), var10);
      } else if (var11) {
         Realms.bind("realms:textures/gui/realms/empty_frame.png");
      } else if (var10 != null && var8 != -1L) {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(var8), var10);
      } else if (var7 == 1) {
         Realms.bind("textures/gui/title/background/panorama_0.png");
      } else if (var7 == 2) {
         Realms.bind("textures/gui/title/background/panorama_2.png");
      } else if (var7 == 3) {
         Realms.bind("textures/gui/title/background/panorama_3.png");
      }

      if (var5) {
         float var16 = 0.85F + 0.15F * RealmsMth.cos((float)this.animTick * 0.2F);
         GlStateManager.color4f(var16, var16, var16, 1.0F);
      } else {
         GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      }

      RealmsScreen.blit(var1 + 3, var2 + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      Realms.bind("realms:textures/gui/realms/slot_frame.png");
      boolean var17 = var15 && var13 != RealmsWorldSlotButton.Action.NOTHING;
      if (var17) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else if (var5) {
         GlStateManager.color4f(0.8F, 0.8F, 0.8F, 1.0F);
      } else {
         GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      }

      RealmsScreen.blit(var1, var2, 0.0F, 0.0F, 80, 80, 80, 80);
      this.drawCenteredString(var6, var1 + 40, var2 + 66, 16777215);
   }

   public void onPress() {
      this.listener.onSlotClick(this.slotIndex, this.state.action, this.state.minigame, this.state.empty);
   }

   public static class State {
      final boolean isCurrentlyActiveSlot;
      final String slotName;
      final long imageId;
      public final String image;
      public final boolean empty;
      final boolean minigame;
      public final RealmsWorldSlotButton.Action action;
      final String actionPrompt;

      State(boolean var1, String var2, long var3, @Nullable String var5, boolean var6, boolean var7, @Nonnull RealmsWorldSlotButton.Action var8, @Nullable String var9) {
         super();
         this.isCurrentlyActiveSlot = var1;
         this.slotName = var2;
         this.imageId = var3;
         this.image = var5;
         this.empty = var6;
         this.minigame = var7;
         this.action = var8;
         this.actionPrompt = var9;
      }
   }

   public static enum Action {
      NOTHING,
      SWITCH_SLOT,
      JOIN;

      private Action() {
      }
   }

   public interface Listener {
      void onSlotClick(int var1, @Nonnull RealmsWorldSlotButton.Action var2, boolean var3, boolean var4);
   }
}
