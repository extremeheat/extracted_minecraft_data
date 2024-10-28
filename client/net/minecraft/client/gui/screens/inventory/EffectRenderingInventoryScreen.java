package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class EffectRenderingInventoryScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
   private static final ResourceLocation EFFECT_BACKGROUND_LARGE_SPRITE = ResourceLocation.withDefaultNamespace("container/inventory/effect_background_large");
   private static final ResourceLocation EFFECT_BACKGROUND_SMALL_SPRITE = ResourceLocation.withDefaultNamespace("container/inventory/effect_background_small");

   public EffectRenderingInventoryScreen(T var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderEffects(var1, var2, var3);
   }

   public boolean canSeeEffects() {
      int var1 = this.leftPos + this.imageWidth + 2;
      int var2 = this.width - var1;
      return var2 >= 32;
   }

   private void renderEffects(GuiGraphics var1, int var2, int var3) {
      int var4 = this.leftPos + this.imageWidth + 2;
      int var5 = this.width - var4;
      Collection var6 = this.minecraft.player.getActiveEffects();
      if (!var6.isEmpty() && var5 >= 32) {
         boolean var7 = var5 >= 120;
         int var8 = 33;
         if (var6.size() > 5) {
            var8 = 132 / (var6.size() - 1);
         }

         List var9 = Ordering.natural().sortedCopy(var6);
         this.renderBackgrounds(var1, var4, var8, var9, var7);
         this.renderIcons(var1, var4, var8, var9, var7);
         if (var7) {
            this.renderLabels(var1, var4, var8, var9);
         } else if (var2 >= var4 && var2 <= var4 + 33) {
            int var10 = this.topPos;
            MobEffectInstance var11 = null;

            for(Iterator var12 = var9.iterator(); var12.hasNext(); var10 += var8) {
               MobEffectInstance var13 = (MobEffectInstance)var12.next();
               if (var3 >= var10 && var3 <= var10 + var8) {
                  var11 = var13;
               }
            }

            if (var11 != null) {
               List var14 = List.of(this.getEffectName(var11), MobEffectUtil.formatDuration(var11, 1.0F, this.minecraft.level.tickRateManager().tickrate()));
               var1.renderTooltip(this.font, var14, Optional.empty(), var2, var3);
            }
         }

      }
   }

   private void renderBackgrounds(GuiGraphics var1, int var2, int var3, Iterable<MobEffectInstance> var4, boolean var5) {
      int var6 = this.topPos;

      for(Iterator var7 = var4.iterator(); var7.hasNext(); var6 += var3) {
         MobEffectInstance var8 = (MobEffectInstance)var7.next();
         if (var5) {
            var1.blitSprite(EFFECT_BACKGROUND_LARGE_SPRITE, var2, var6, 120, 32);
         } else {
            var1.blitSprite(EFFECT_BACKGROUND_SMALL_SPRITE, var2, var6, 32, 32);
         }
      }

   }

   private void renderIcons(GuiGraphics var1, int var2, int var3, Iterable<MobEffectInstance> var4, boolean var5) {
      MobEffectTextureManager var6 = this.minecraft.getMobEffectTextures();
      int var7 = this.topPos;

      for(Iterator var8 = var4.iterator(); var8.hasNext(); var7 += var3) {
         MobEffectInstance var9 = (MobEffectInstance)var8.next();
         Holder var10 = var9.getEffect();
         TextureAtlasSprite var11 = var6.get(var10);
         var1.blit(var2 + (var5 ? 6 : 7), var7 + 7, 0, 18, 18, var11);
      }

   }

   private void renderLabels(GuiGraphics var1, int var2, int var3, Iterable<MobEffectInstance> var4) {
      int var5 = this.topPos;

      for(Iterator var6 = var4.iterator(); var6.hasNext(); var5 += var3) {
         MobEffectInstance var7 = (MobEffectInstance)var6.next();
         Component var8 = this.getEffectName(var7);
         var1.drawString(this.font, var8, var2 + 10 + 18, var5 + 6, 16777215);
         Component var9 = MobEffectUtil.formatDuration(var7, 1.0F, this.minecraft.level.tickRateManager().tickrate());
         var1.drawString(this.font, var9, var2 + 10 + 18, var5 + 6 + 10, 8355711);
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
