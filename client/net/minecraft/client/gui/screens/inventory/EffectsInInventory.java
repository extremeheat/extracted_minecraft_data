package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;

public class EffectsInInventory {
   private static final ResourceLocation EFFECT_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("container/inventory/effect_background");
   private static final ResourceLocation EFFECT_BACKGROUND_AMBIENT_SPRITE = ResourceLocation.withDefaultNamespace("container/inventory/effect_background_ambient");
   private static final int ICON_SIZE = 18;
   public static final int SPACING = 7;
   private static final int TEXT_X_OFFSET = 32;
   public static final int SPRITE_SQUARE_SIZE = 32;
   private final AbstractContainerScreen<?> screen;
   private final Minecraft minecraft;

   public EffectsInInventory(AbstractContainerScreen<?> var1) {
      super();
      this.screen = var1;
      this.minecraft = Minecraft.getInstance();
   }

   public boolean canSeeEffects() {
      int var1 = this.screen.leftPos + this.screen.imageWidth + 2;
      int var2 = this.screen.width - var1;
      return var2 >= 32;
   }

   public void render(GuiGraphics var1, int var2, int var3) {
      int var4 = this.screen.leftPos + this.screen.imageWidth + 2;
      int var5 = this.screen.width - var4;
      Collection var6 = this.minecraft.player.getActiveEffects();
      if (!var6.isEmpty() && var5 >= 32) {
         int var7 = var5 >= 120 ? var5 - 7 : 32;
         int var8 = 33;
         if (var6.size() > 5) {
            var8 = 132 / (var6.size() - 1);
         }

         this.renderEffects(var1, var6, var4, var8, var2, var3, var7);
      }
   }

   private void renderEffects(GuiGraphics var1, Collection<MobEffectInstance> var2, int var3, int var4, int var5, int var6, int var7) {
      List var8 = Ordering.natural().sortedCopy(var2);
      int var9 = this.screen.topPos;
      Font var10 = this.screen.getFont();

      for(MobEffectInstance var12 : var8) {
         boolean var13 = var12.isAmbient();
         Component var14 = this.getEffectName(var12);
         int var15 = this.renderBackground(var1, var10, var14, var3, var9, var13, var7);
         this.renderText(var1, var12, var14, var10, var3, var9, var15, var4, var5, var6);
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)Gui.getMobEffectSprite(var12.getEffect()), var3 + 7, var9 + 7, 18, 18);
         var9 += var4;
      }

   }

   private int renderBackground(GuiGraphics var1, Font var2, Component var3, int var4, int var5, boolean var6, int var7) {
      int var8 = Math.min(var7, 32 + var2.width((FormattedText)var3) + 7);
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)(var6 ? EFFECT_BACKGROUND_AMBIENT_SPRITE : EFFECT_BACKGROUND_SPRITE), var4, var5, var8, 32);
      return var8;
   }

   private void renderText(GuiGraphics var1, MobEffectInstance var2, Component var3, Font var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      int var11 = var5 + 32;
      int var12 = var6 + 7;
      int var13 = var7 - 32 - 7;
      Component var14 = MobEffectUtil.formatDuration(var2, 1.0F, this.minecraft.level.tickRateManager().tickrate());
      boolean var15;
      if (var13 > 0) {
         boolean var16 = var4.width((FormattedText)var3) > var13;
         FormattedCharSequence var17 = var16 ? StringWidget.clipText(var3, var4, var13) : var3.getVisualOrderText();
         var1.drawString(var4, (FormattedCharSequence)var17, var11, var12, -1);
         Objects.requireNonNull(var4);
         var1.drawString(var4, var14, var11, var12 + 9, -8355712);
         var15 = var16;
      } else {
         var15 = true;
      }

      if (var15 && var9 >= var5 && var9 <= var5 + var7 && var10 >= var6 && var10 <= var6 + var8) {
         var1.setTooltipForNextFrame(this.screen.getFont(), List.of(var3, var14), Optional.empty(), var9, var10);
      }

   }

   private Component getEffectName(MobEffectInstance var1) {
      MutableComponent var2 = ((MobEffect)var1.getEffect().value()).getDisplayName().copy();
      if (var1.getAmplifier() >= 1 && var1.getAmplifier() <= 9) {
         MutableComponent var10000 = var2.append(CommonComponents.SPACE);
         int var10001 = var1.getAmplifier();
         var10000.append((Component)Component.translatable("enchantment.level." + (var10001 + 1)));
      }

      return var2;
   }
}
