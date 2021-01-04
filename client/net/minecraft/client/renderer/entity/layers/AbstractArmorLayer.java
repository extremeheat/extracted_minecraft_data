package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
   protected static final ResourceLocation ENCHANT_GLINT_LOCATION = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   protected final A innerModel;
   protected final A outerModel;
   private float alpha = 1.0F;
   private float red = 1.0F;
   private float green = 1.0F;
   private float blue = 1.0F;
   private boolean colorized;
   private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();

   protected AbstractArmorLayer(RenderLayerParent<T, M> var1, A var2, A var3) {
      super(var1);
      this.innerModel = var2;
      this.outerModel = var3;
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.renderArmorPiece(var1, var2, var3, var4, var5, var6, var7, var8, EquipmentSlot.CHEST);
      this.renderArmorPiece(var1, var2, var3, var4, var5, var6, var7, var8, EquipmentSlot.LEGS);
      this.renderArmorPiece(var1, var2, var3, var4, var5, var6, var7, var8, EquipmentSlot.FEET);
      this.renderArmorPiece(var1, var2, var3, var4, var5, var6, var7, var8, EquipmentSlot.HEAD);
   }

   public boolean colorsOnDamage() {
      return false;
   }

   private void renderArmorPiece(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, EquipmentSlot var9) {
      ItemStack var10 = var1.getItemBySlot(var9);
      if (var10.getItem() instanceof ArmorItem) {
         ArmorItem var11 = (ArmorItem)var10.getItem();
         if (var11.getSlot() == var9) {
            HumanoidModel var12 = this.getArmorModel(var9);
            ((HumanoidModel)this.getParentModel()).copyPropertiesTo(var12);
            var12.prepareMobModel(var1, var2, var3, var4);
            this.setPartVisibility(var12, var9);
            boolean var13 = this.usesInnerModel(var9);
            this.bindTexture(this.getArmorLocation(var11, var13));
            if (var11 instanceof DyeableArmorItem) {
               int var14 = ((DyeableArmorItem)var11).getColor(var10);
               float var15 = (float)(var14 >> 16 & 255) / 255.0F;
               float var16 = (float)(var14 >> 8 & 255) / 255.0F;
               float var17 = (float)(var14 & 255) / 255.0F;
               GlStateManager.color4f(this.red * var15, this.green * var16, this.blue * var17, this.alpha);
               var12.render(var1, var2, var3, var5, var6, var7, var8);
               this.bindTexture(this.getArmorLocation(var11, var13, "overlay"));
            }

            GlStateManager.color4f(this.red, this.green, this.blue, this.alpha);
            var12.render(var1, var2, var3, var5, var6, var7, var8);
            if (!this.colorized && var10.isEnchanted()) {
               renderFoil(this::bindTexture, var1, var12, var2, var3, var4, var5, var6, var7, var8);
            }

         }
      }
   }

   public A getArmorModel(EquipmentSlot var1) {
      return this.usesInnerModel(var1) ? this.innerModel : this.outerModel;
   }

   private boolean usesInnerModel(EquipmentSlot var1) {
      return var1 == EquipmentSlot.LEGS;
   }

   public static <T extends Entity> void renderFoil(Consumer<ResourceLocation> var0, T var1, EntityModel<T> var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = (float)var1.tickCount + var5;
      var0.accept(ENCHANT_GLINT_LOCATION);
      GameRenderer var11 = Minecraft.getInstance().gameRenderer;
      var11.resetFogColor(true);
      GlStateManager.enableBlend();
      GlStateManager.depthFunc(514);
      GlStateManager.depthMask(false);
      float var12 = 0.5F;
      GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);

      for(int var13 = 0; var13 < 2; ++var13) {
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
         float var14 = 0.76F;
         GlStateManager.color4f(0.38F, 0.19F, 0.608F, 1.0F);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float var15 = 0.33333334F;
         GlStateManager.scalef(0.33333334F, 0.33333334F, 0.33333334F);
         GlStateManager.rotatef(30.0F - (float)var13 * 60.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.translatef(0.0F, var10 * (0.001F + (float)var13 * 0.003F) * 20.0F, 0.0F);
         GlStateManager.matrixMode(5888);
         var2.render(var1, var3, var4, var6, var7, var8, var9);
         GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      }

      GlStateManager.matrixMode(5890);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      GlStateManager.enableLighting();
      GlStateManager.depthMask(true);
      GlStateManager.depthFunc(515);
      GlStateManager.disableBlend();
      var11.resetFogColor(false);
   }

   private ResourceLocation getArmorLocation(ArmorItem var1, boolean var2) {
      return this.getArmorLocation(var1, var2, (String)null);
   }

   private ResourceLocation getArmorLocation(ArmorItem var1, boolean var2, @Nullable String var3) {
      String var4 = "textures/models/armor/" + var1.getMaterial().getName() + "_layer_" + (var2 ? 2 : 1) + (var3 == null ? "" : "_" + var3) + ".png";
      return (ResourceLocation)ARMOR_LOCATION_CACHE.computeIfAbsent(var4, ResourceLocation::new);
   }

   protected abstract void setPartVisibility(A var1, EquipmentSlot var2);

   protected abstract void hideAllArmor(A var1);
}
