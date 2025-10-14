package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.util.RealmsTextureManager;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class RealmsWorldSlotButton extends Button {
   private static final ResourceLocation SLOT_FRAME_SPRITE = ResourceLocation.withDefaultNamespace("widget/slot_frame");
   public static final ResourceLocation EMPTY_SLOT_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/empty_frame.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_1 = ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama_0.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_2 = ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama_2.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_3 = ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama_3.png");
   private static final Component SWITCH_TO_MINIGAME_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.minigame");
   private static final Component SWITCH_TO_WORLD_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip");
   static final Component MINIGAME = Component.translatable("mco.worldSlot.minigame");
   private static final int WORLD_NAME_MAX_WIDTH = 64;
   private static final String DOTS = "...";
   private final int slotIndex;
   private State state;

   public RealmsWorldSlotButton(int var1, int var2, int var3, int var4, int var5, RealmsServer var6, Button.OnPress var7) {
      super(var1, var2, var3, var4, CommonComponents.EMPTY, var7, DEFAULT_NARRATION);
      this.slotIndex = var5;
      this.state = this.setServerData(var6);
   }

   public State getState() {
      return this.state;
   }

   public State setServerData(RealmsServer var1) {
      this.state = new State(var1, this.slotIndex);
      this.setTooltipAndNarration(this.state, var1.minigameName);
      return this.state;
   }

   private void setTooltipAndNarration(State var1, @Nullable String var2) {
      Component var10000;
      switch (var1.action.ordinal()) {
         case 1 -> var10000 = var1.minigame ? SWITCH_TO_MINIGAME_SLOT_TOOLTIP : SWITCH_TO_WORLD_SLOT_TOOLTIP;
         default -> var10000 = null;
      }

      Component var3 = var10000;
      if (var3 != null) {
         this.setTooltip(Tooltip.create(var3));
      }

      MutableComponent var4 = Component.literal(var1.slotName);
      if (var1.minigame && var2 != null) {
         var4 = var4.append(CommonComponents.SPACE).append(var2);
      }

      this.setMessage(var4);
   }

   static Action getAction(boolean var0, boolean var1, boolean var2) {
      return var0 || var1 && var2 ? RealmsWorldSlotButton.Action.NOTHING : RealmsWorldSlotButton.Action.SWITCH_SLOT;
   }

   public boolean isActive() {
      return this.state.action != RealmsWorldSlotButton.Action.NOTHING && super.isActive();
   }

   public void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
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

      int var9 = -1;
      if (!this.state.activeSlot) {
         var9 = ARGB.colorFromFloat(1.0F, 0.56F, 0.56F, 0.56F);
      }

      var1.blit(RenderPipelines.GUI_TEXTURED, var8, var5 + 1, var6 + 1, 0.0F, 0.0F, this.width - 2, this.height - 2, 74, 74, 74, 74, var9);
      if (var7 && this.state.action != RealmsWorldSlotButton.Action.NOTHING) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_SPRITE, var5, var6, this.width, this.height);
      } else if (this.state.activeSlot) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_SPRITE, var5, var6, this.width, this.height, ARGB.colorFromFloat(1.0F, 0.8F, 0.8F, 0.8F));
      } else {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_SPRITE, var5, var6, this.width, this.height, ARGB.colorFromFloat(1.0F, 0.56F, 0.56F, 0.56F));
      }

      if (this.state.hardcore) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)RealmsMainScreen.HARDCORE_MODE_SPRITE, var5 + 3, var6 + 4, 9, 8);
      }

      Font var10 = Minecraft.getInstance().font;
      String var11 = this.state.slotName;
      if (var10.width(var11) > 64) {
         String var10000 = var10.plainSubstrByWidth(var11, 64 - var10.width("..."));
         var11 = var10000 + "...";
      }

      var1.drawCenteredString(var10, (String)var11, var5 + this.width / 2, var6 + this.height - 14, -1);
      if (this.state.activeSlot) {
         var1.drawCenteredString(var10, (Component)RealmsMainScreen.getVersionComponent(this.state.slotVersion, this.state.compatibility.isCompatible()), var5 + this.width / 2, var6 + this.height + 2, -1);
      }

   }

   public static enum Action {
      NOTHING,
      SWITCH_SLOT;

      private Action() {
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{NOTHING, SWITCH_SLOT};
      }
   }

   public static class State {
      final String slotName;
      final String slotVersion;
      final RealmsServer.Compatibility compatibility;
      final long imageId;
      @Nullable
      final String image;
      public final boolean empty;
      public final boolean minigame;
      public final Action action;
      public final boolean hardcore;
      public final boolean activeSlot;

      public State(RealmsServer var1, int var2) {
         super();
         this.minigame = var2 == 4;
         if (this.minigame) {
            this.slotName = RealmsWorldSlotButton.MINIGAME.getString();
            this.imageId = (long)var1.minigameId;
            this.image = var1.minigameImage;
            this.empty = var1.minigameId == -1;
            this.slotVersion = "";
            this.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
            this.hardcore = false;
            this.activeSlot = var1.isMinigameActive();
         } else {
            RealmsSlot var3 = (RealmsSlot)var1.slots.get(var2);
            this.slotName = var3.options.getSlotName(var2);
            this.imageId = var3.options.templateId;
            this.image = var3.options.templateImage;
            this.empty = var3.options.empty;
            this.slotVersion = var3.options.version;
            this.compatibility = var3.options.compatibility;
            this.hardcore = var3.isHardcore();
            this.activeSlot = var1.activeSlot == var2 && !var1.isMinigameActive();
         }

         this.action = RealmsWorldSlotButton.getAction(this.activeSlot, this.empty, var1.expired);
      }
   }
}
