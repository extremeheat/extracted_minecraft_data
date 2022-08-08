package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class RealmsWorldSlotButton extends Button {
   public static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
   public static final ResourceLocation EMPTY_SLOT_LOCATION = new ResourceLocation("realms", "textures/gui/realms/empty_frame.png");
   public static final ResourceLocation CHECK_MARK_LOCATION = new ResourceLocation("realms", "textures/gui/realms/checkmark.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_1 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_0.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_2 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_2.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_3 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_3.png");
   private static final Component SLOT_ACTIVE_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.active");
   private static final Component SWITCH_TO_MINIGAME_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.minigame");
   private static final Component SWITCH_TO_WORLD_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip");
   private final Supplier<RealmsServer> serverDataProvider;
   private final Consumer<Component> toolTipSetter;
   private final int slotIndex;
   @Nullable
   private State state;

   public RealmsWorldSlotButton(int var1, int var2, int var3, int var4, Supplier<RealmsServer> var5, Consumer<Component> var6, int var7, Button.OnPress var8) {
      super(var1, var2, var3, var4, CommonComponents.EMPTY, var8);
      this.serverDataProvider = var5;
      this.slotIndex = var7;
      this.toolTipSetter = var6;
   }

   @Nullable
   public State getState() {
      return this.state;
   }

   public void tick() {
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
            var2 = var1.worldType == RealmsServer.WorldType.MINIGAME;
            var3 = "Minigame";
            var5 = (long)var1.minigameId;
            var7 = var1.minigameImage;
            var8 = var1.minigameId == -1;
         } else {
            var2 = var1.activeSlot == this.slotIndex && var1.worldType != RealmsServer.WorldType.MINIGAME;
            var3 = var4.getSlotName(this.slotIndex);
            var5 = var4.templateId;
            var7 = var4.templateImage;
            var8 = var4.empty;
         }

         Action var10 = getAction(var1, var2, var9);
         Pair var11 = this.getTooltipAndNarration(var1, var3, var8, var9, var10);
         this.state = new State(var2, var3, var5, var7, var8, var9, var10, (Component)var11.getFirst());
         this.setMessage((Component)var11.getSecond());
      }
   }

   private static Action getAction(RealmsServer var0, boolean var1, boolean var2) {
      if (var1) {
         if (!var0.expired && var0.state != RealmsServer.State.UNINITIALIZED) {
            return RealmsWorldSlotButton.Action.JOIN;
         }
      } else {
         if (!var2) {
            return RealmsWorldSlotButton.Action.SWITCH_SLOT;
         }

         if (!var0.expired) {
            return RealmsWorldSlotButton.Action.SWITCH_SLOT;
         }
      }

      return RealmsWorldSlotButton.Action.NOTHING;
   }

   private Pair<Component, Component> getTooltipAndNarration(RealmsServer var1, String var2, boolean var3, boolean var4, Action var5) {
      if (var5 == RealmsWorldSlotButton.Action.NOTHING) {
         return Pair.of((Object)null, Component.literal(var2));
      } else {
         Object var6;
         if (var4) {
            if (var3) {
               var6 = CommonComponents.EMPTY;
            } else {
               var6 = Component.literal(" ").append(var2).append(" ").append(var1.minigameName);
            }
         } else {
            var6 = Component.literal(" ").append(var2);
         }

         Component var7;
         if (var5 == RealmsWorldSlotButton.Action.JOIN) {
            var7 = SLOT_ACTIVE_TOOLTIP;
         } else {
            var7 = var4 ? SWITCH_TO_MINIGAME_SLOT_TOOLTIP : SWITCH_TO_WORLD_SLOT_TOOLTIP;
         }

         MutableComponent var8 = var7.copy().append((Component)var6);
         return Pair.of(var7, var8);
      }
   }

   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      if (this.state != null) {
         this.drawSlotFrame(var1, this.x, this.y, var2, var3, this.state.isCurrentlyActiveSlot, this.state.slotName, this.slotIndex, this.state.imageId, this.state.image, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
      }
   }

   private void drawSlotFrame(PoseStack var1, int var2, int var3, int var4, int var5, boolean var6, String var7, int var8, long var9, @Nullable String var11, boolean var12, boolean var13, Action var14, @Nullable Component var15) {
      boolean var16 = this.isHoveredOrFocused();
      if (this.isMouseOver((double)var4, (double)var5) && var15 != null) {
         this.toolTipSetter.accept(var15);
      }

      Minecraft var17 = Minecraft.getInstance();
      if (var13) {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(var9), var11);
      } else if (var12) {
         RenderSystem.setShaderTexture(0, EMPTY_SLOT_LOCATION);
      } else if (var11 != null && var9 != -1L) {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(var9), var11);
      } else if (var8 == 1) {
         RenderSystem.setShaderTexture(0, DEFAULT_WORLD_SLOT_1);
      } else if (var8 == 2) {
         RenderSystem.setShaderTexture(0, DEFAULT_WORLD_SLOT_2);
      } else if (var8 == 3) {
         RenderSystem.setShaderTexture(0, DEFAULT_WORLD_SLOT_3);
      }

      if (var6) {
         RenderSystem.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      blit(var1, var2 + 3, var3 + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      RenderSystem.setShaderTexture(0, SLOT_FRAME_LOCATION);
      boolean var18 = var16 && var14 != RealmsWorldSlotButton.Action.NOTHING;
      if (var18) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      } else if (var6) {
         RenderSystem.setShaderColor(0.8F, 0.8F, 0.8F, 1.0F);
      } else {
         RenderSystem.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
      }

      blit(var1, var2, var3, 0.0F, 0.0F, 80, 80, 80, 80);
      if (var6) {
         this.renderCheckMark(var1, var2, var3);
      }

      drawCenteredString(var1, var17.font, var7, var2 + 40, var3 + 66, 16777215);
   }

   private void renderCheckMark(PoseStack var1, int var2, int var3) {
      RenderSystem.setShaderTexture(0, CHECK_MARK_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      blit(var1, var2 + 67, var3 + 4, 0.0F, 0.0F, 9, 8, 9, 8);
      RenderSystem.disableBlend();
   }

   public static class State {
      final boolean isCurrentlyActiveSlot;
      final String slotName;
      final long imageId;
      @Nullable
      final String image;
      public final boolean empty;
      public final boolean minigame;
      public final Action action;
      @Nullable
      final Component actionPrompt;

      State(boolean var1, String var2, long var3, @Nullable String var5, boolean var6, boolean var7, Action var8, @Nullable Component var9) {
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

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{NOTHING, SWITCH_SLOT, JOIN};
      }
   }
}
