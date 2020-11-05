package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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

public abstract class EffectRenderingInventoryScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
   protected boolean doRenderEffects;

   public EffectRenderingInventoryScreen(T var1, Inventory var2, Component var3) {
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

   public void render(PoseStack var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (this.doRenderEffects) {
         this.renderEffects(var1);
      }

   }

   private void renderEffects(PoseStack var1) {
      int var2 = this.leftPos - 124;
      Collection var3 = this.minecraft.player.getActiveEffects();
      if (!var3.isEmpty()) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int var4 = 33;
         if (var3.size() > 5) {
            var4 = 132 / (var3.size() - 1);
         }

         List var5 = Ordering.natural().sortedCopy(var3);
         this.renderBackgrounds(var1, var2, var4, var5);
         this.renderIcons(var1, var2, var4, var5);
         this.renderLabels(var1, var2, var4, var5);
      }
   }

   private void renderBackgrounds(PoseStack var1, int var2, int var3, Iterable<MobEffectInstance> var4) {
      this.minecraft.getTextureManager().bind(INVENTORY_LOCATION);
      int var5 = this.topPos;

      for(Iterator var6 = var4.iterator(); var6.hasNext(); var5 += var3) {
         MobEffectInstance var7 = (MobEffectInstance)var6.next();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.blit(var1, var2, var5, 0, 166, 140, 32);
      }

   }

   private void renderIcons(PoseStack var1, int var2, int var3, Iterable<MobEffectInstance> var4) {
      MobEffectTextureManager var5 = this.minecraft.getMobEffectTextures();
      int var6 = this.topPos;

      for(Iterator var7 = var4.iterator(); var7.hasNext(); var6 += var3) {
         MobEffectInstance var8 = (MobEffectInstance)var7.next();
         MobEffect var9 = var8.getEffect();
         TextureAtlasSprite var10 = var5.get(var9);
         this.minecraft.getTextureManager().bind(var10.atlas().location());
         blit(var1, var2 + 6, var6 + 7, this.getBlitOffset(), 18, 18, var10);
      }

   }

   private void renderLabels(PoseStack var1, int var2, int var3, Iterable<MobEffectInstance> var4) {
      int var5 = this.topPos;

      for(Iterator var6 = var4.iterator(); var6.hasNext(); var5 += var3) {
         MobEffectInstance var7 = (MobEffectInstance)var6.next();
         String var8 = I18n.get(var7.getEffect().getDescriptionId());
         if (var7.getAmplifier() >= 1 && var7.getAmplifier() <= 9) {
            var8 = var8 + ' ' + I18n.get("enchantment.level." + (var7.getAmplifier() + 1));
         }

         this.font.drawShadow(var1, var8, (float)(var2 + 10 + 18), (float)(var5 + 6), 16777215);
         String var9 = MobEffectUtil.formatDuration(var7, 1.0F);
         this.font.drawShadow(var1, var9, (float)(var2 + 10 + 18), (float)(var5 + 6 + 10), 8355711);
      }

   }
}
