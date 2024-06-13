package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class RealmsWorldSlotButton extends Button {
   private static final ResourceLocation SLOT_FRAME_SPRITE = new ResourceLocation("widget/slot_frame");
   private static final ResourceLocation CHECKMARK_SPRITE = new ResourceLocation("icon/checkmark");
   public static final ResourceLocation EMPTY_SLOT_LOCATION = new ResourceLocation("textures/gui/realms/empty_frame.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_1 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_0.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_2 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_2.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_3 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_3.png");
   private static final Component SLOT_ACTIVE_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.active");
   private static final Component SWITCH_TO_MINIGAME_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.minigame");
   private static final Component SWITCH_TO_WORLD_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip");
   static final Component MINIGAME = Component.translatable("mco.worldSlot.minigame");
   private final int slotIndex;
   @Nullable
   private RealmsWorldSlotButton.State state;

   public RealmsWorldSlotButton(int var1, int var2, int var3, int var4, int var5, Button.OnPress var6) {
      super(var1, var2, var3, var4, CommonComponents.EMPTY, var6, DEFAULT_NARRATION);
      this.slotIndex = var5;
   }

   @Nullable
   public RealmsWorldSlotButton.State getState() {
      return this.state;
   }

   public void setServerData(RealmsServer var1) {
      this.state = new RealmsWorldSlotButton.State(var1, this.slotIndex);
      this.setTooltipAndNarration(this.state, var1.minigameName);
   }

   private void setTooltipAndNarration(RealmsWorldSlotButton.State var1, @Nullable String var2) {
      Component var3 = switch (var1.action) {
         case SWITCH_SLOT -> var1.minigame ? SWITCH_TO_MINIGAME_SLOT_TOOLTIP : SWITCH_TO_WORLD_SLOT_TOOLTIP;
         case JOIN -> SLOT_ACTIVE_TOOLTIP;
         default -> null;
      };
      if (var3 != null) {
         this.setTooltip(Tooltip.create(var3));
      }

      MutableComponent var4 = Component.literal(var1.slotName);
      if (var1.minigame && var2 != null) {
         var4 = var4.append(CommonComponents.SPACE).append(var2);
      }

      this.setMessage(var4);
   }

   static RealmsWorldSlotButton.Action getAction(RealmsServer var0, boolean var1, boolean var2) {
      if (var1 && !var0.expired && var0.state != RealmsServer.State.UNINITIALIZED) {
         return RealmsWorldSlotButton.Action.JOIN;
      } else {
         return var1 || var2 && var0.expired ? RealmsWorldSlotButton.Action.NOTHING : RealmsWorldSlotButton.Action.SWITCH_SLOT;
      }
   }

   @Override
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.state != null) {
         int var5 = this.getX();
         int var6 = this.getY();
         boolean var7 = this.isHoveredOrFocused();
         ResourceLocation var8;
         if (this.state.minigame) {
            var8 = RealmsTextureManager.worldTemplate(String.valueOf(this.state.imageId), this.state.image);
         } else if (this.state.empty) {
            var8 = EMPTY_SLOT_LOCATION;
         } else if (this.state.image != null && this.state.imageId != -1L) {
            var8 = RealmsTextureManager.worldTemplate(String.valueOf(this.state.imageId), this.state.image);
         } else if (this.slotIndex == 1) {
            var8 = DEFAULT_WORLD_SLOT_1;
         } else if (this.slotIndex == 2) {
            var8 = DEFAULT_WORLD_SLOT_2;
         } else if (this.slotIndex == 3) {
            var8 = DEFAULT_WORLD_SLOT_3;
         } else {
            var8 = EMPTY_SLOT_LOCATION;
         }

         if (this.state.isCurrentlyActiveSlot) {
            var1.setColor(0.56F, 0.56F, 0.56F, 1.0F);
         }

         var1.blit(var8, var5 + 3, var6 + 3, 0.0F, 0.0F, 74, 74, 74, 74);
         boolean var9 = var7 && this.state.action != RealmsWorldSlotButton.Action.NOTHING;
         if (var9) {
            var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         } else if (this.state.isCurrentlyActiveSlot) {
            var1.setColor(0.8F, 0.8F, 0.8F, 1.0F);
         } else {
            var1.setColor(0.56F, 0.56F, 0.56F, 1.0F);
         }

         var1.blitSprite(SLOT_FRAME_SPRITE, var5, var6, 80, 80);
         var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         if (this.state.isCurrentlyActiveSlot) {
            RenderSystem.enableBlend();
            var1.blitSprite(CHECKMARK_SPRITE, var5 + 67, var6 + 4, 9, 8);
            RenderSystem.disableBlend();
         }

         Font var10 = Minecraft.getInstance().font;
         var1.drawCenteredString(var10, this.state.slotName, var5 + 40, var6 + 66, -1);
         var1.drawCenteredString(
            var10, RealmsMainScreen.getVersionComponent(this.state.slotVersion, this.state.compatibility.isCompatible()), var5 + 40, var6 + 80 + 2, -1
         );
      }
   }

   public static enum Action {
      NOTHING,
      SWITCH_SLOT,
      JOIN;

      private Action() {
      }
   }

   public static class State {
      final boolean isCurrentlyActiveSlot;
      final String slotName;
      final String slotVersion;
      final RealmsServer.Compatibility compatibility;
      final long imageId;
      @Nullable
      final String image;
      public final boolean empty;
      public final boolean minigame;
      public final RealmsWorldSlotButton.Action action;

      public State(RealmsServer var1, int var2) {
         super();
         this.minigame = var2 == 4;
         if (this.minigame) {
            this.isCurrentlyActiveSlot = var1.worldType == RealmsServer.WorldType.MINIGAME;
            this.slotName = RealmsWorldSlotButton.MINIGAME.getString();
            this.imageId = (long)var1.minigameId;
            this.image = var1.minigameImage;
            this.empty = var1.minigameId == -1;
            this.slotVersion = "";
            this.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
         } else {
            RealmsWorldOptions var3 = var1.slots.get(var2);
            this.isCurrentlyActiveSlot = var1.activeSlot == var2 && var1.worldType != RealmsServer.WorldType.MINIGAME;
            this.slotName = var3.getSlotName(var2);
            this.imageId = var3.templateId;
            this.image = var3.templateImage;
            this.empty = var3.empty;
            this.slotVersion = var3.version;
            this.compatibility = var3.compatibility;
         }

         this.action = RealmsWorldSlotButton.getAction(var1, this.isCurrentlyActiveSlot, this.minigame);
      }
   }
}
