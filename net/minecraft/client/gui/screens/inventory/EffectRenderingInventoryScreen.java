package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class EffectRenderingInventoryScreen extends AbstractContainerScreen {
   protected boolean doRenderEffects;

   public EffectRenderingInventoryScreen(AbstractContainerMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
   }

   protected void init() {
      super.init();
      this.checkEffectRendering();
   }

   protected void checkEffectRendering() {
      if (this.minecraft.player.getActiveEffects().isEmpty()) {
         this.leftPos = (this.width - this.imageWidth) / 2;
         this.doRenderEffects = false;
      } else {
         this.leftPos = 160 + (this.width - this.imageWidth - 200) / 2;
         this.doRenderEffects = true;
      }

   }

   public void render(int var1, int var2, float var3) {
      super.render(var1, var2, var3);
      if (this.doRenderEffects) {
         this.renderEffects();
      }

   }

   private void renderEffects() {
      int var1 = this.leftPos - 124;
      Collection var2 = this.minecraft.player.getActiveEffects();
      if (!var2.isEmpty()) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int var3 = 33;
         if (var2.size() > 5) {
            var3 = 132 / (var2.size() - 1);
         }

         List var4 = Ordering.natural().sortedCopy(var2);
         this.renderBackgrounds(var1, var3, var4);
         this.renderIcons(var1, var3, var4);
         this.renderLabels(var1, var3, var4);
      }
   }

   private void renderBackgrounds(int var1, int var2, Iterable var3) {
      this.minecraft.getTextureManager().bind(INVENTORY_LOCATION);
      int var4 = this.topPos;

      for(Iterator var5 = var3.iterator(); var5.hasNext(); var4 += var2) {
         MobEffectInstance var6 = (MobEffectInstance)var5.next();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.blit(var1, var4, 0, 166, 140, 32);
      }

   }

   private void renderIcons(int var1, int var2, Iterable var3) {
      MobEffectTextureManager var4 = this.minecraft.getMobEffectTextures();
      int var5 = this.topPos;

      for(Iterator var6 = var3.iterator(); var6.hasNext(); var5 += var2) {
         MobEffectInstance var7 = (MobEffectInstance)var6.next();
         MobEffect var8 = var7.getEffect();
         TextureAtlasSprite var9 = var4.get(var8);
         this.minecraft.getTextureManager().bind(var9.atlas().location());
         blit(var1 + 6, var5 + 7, this.getBlitOffset(), 18, 18, var9);
      }

   }

   private void renderLabels(int var1, int var2, Iterable var3) {
      int var4 = this.topPos;

      for(Iterator var5 = var3.iterator(); var5.hasNext(); var4 += var2) {
         MobEffectInstance var6 = (MobEffectInstance)var5.next();
         String var7 = I18n.get(var6.getEffect().getDescriptionId());
         if (var6.getAmplifier() >= 1 && var6.getAmplifier() <= 9) {
            var7 = var7 + ' ' + I18n.get("enchantment.level." + (var6.getAmplifier() + 1));
         }

         this.font.drawShadow(var7, (float)(var1 + 10 + 18), (float)(var4 + 6), 16777215);
         String var8 = MobEffectUtil.formatDuration(var6, 1.0F);
         this.font.drawShadow(var8, (float)(var1 + 10 + 18), (float)(var4 + 6 + 10), 8355711);
      }

   }
}
