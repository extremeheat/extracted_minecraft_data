package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractArmorLayer extends RenderLayer {
   protected final HumanoidModel innerModel;
   protected final HumanoidModel outerModel;
   private static final Map ARMOR_LOCATION_CACHE = Maps.newHashMap();

   protected AbstractArmorLayer(RenderLayerParent var1, HumanoidModel var2, HumanoidModel var3) {
      super(var1);
      this.innerModel = var2;
      this.outerModel = var3;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, LivingEntity var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      this.renderArmorPiece(var1, var2, var4, var5, var6, var7, var8, var9, var10, EquipmentSlot.CHEST, var3);
      this.renderArmorPiece(var1, var2, var4, var5, var6, var7, var8, var9, var10, EquipmentSlot.LEGS, var3);
      this.renderArmorPiece(var1, var2, var4, var5, var6, var7, var8, var9, var10, EquipmentSlot.FEET, var3);
      this.renderArmorPiece(var1, var2, var4, var5, var6, var7, var8, var9, var10, EquipmentSlot.HEAD, var3);
   }

   private void renderArmorPiece(PoseStack var1, MultiBufferSource var2, LivingEntity var3, float var4, float var5, float var6, float var7, float var8, float var9, EquipmentSlot var10, int var11) {
      ItemStack var12 = var3.getItemBySlot(var10);
      if (var12.getItem() instanceof ArmorItem) {
         ArmorItem var13 = (ArmorItem)var12.getItem();
         if (var13.getSlot() == var10) {
            HumanoidModel var14 = this.getArmorModel(var10);
            ((HumanoidModel)this.getParentModel()).copyPropertiesTo(var14);
            var14.prepareMobModel(var3, var4, var5, var6);
            this.setPartVisibility(var14, var10);
            var14.setupAnim(var3, var4, var5, var7, var8, var9);
            boolean var15 = this.usesInnerModel(var10);
            boolean var16 = var12.hasFoil();
            if (var13 instanceof DyeableArmorItem) {
               int var17 = ((DyeableArmorItem)var13).getColor(var12);
               float var18 = (float)(var17 >> 16 & 255) / 255.0F;
               float var19 = (float)(var17 >> 8 & 255) / 255.0F;
               float var20 = (float)(var17 & 255) / 255.0F;
               this.renderModel(var1, var2, var11, var13, var16, var14, var15, var18, var19, var20, (String)null);
               this.renderModel(var1, var2, var11, var13, var16, var14, var15, 1.0F, 1.0F, 1.0F, "overlay");
            } else {
               this.renderModel(var1, var2, var11, var13, var16, var14, var15, 1.0F, 1.0F, 1.0F, (String)null);
            }

         }
      }
   }

   private void renderModel(PoseStack var1, MultiBufferSource var2, int var3, ArmorItem var4, boolean var5, HumanoidModel var6, boolean var7, float var8, float var9, float var10, @Nullable String var11) {
      VertexConsumer var12 = ItemRenderer.getFoilBuffer(var2, RenderType.entityCutoutNoCull(this.getArmorLocation(var4, var7, var11)), false, var5);
      var6.renderToBuffer(var1, var12, var3, OverlayTexture.NO_OVERLAY, var8, var9, var10, 1.0F);
   }

   public HumanoidModel getArmorModel(EquipmentSlot var1) {
      return this.usesInnerModel(var1) ? this.innerModel : this.outerModel;
   }

   private boolean usesInnerModel(EquipmentSlot var1) {
      return var1 == EquipmentSlot.LEGS;
   }

   private ResourceLocation getArmorLocation(ArmorItem var1, boolean var2, @Nullable String var3) {
      String var4 = "textures/models/armor/" + var1.getMaterial().getName() + "_layer_" + (var2 ? 2 : 1) + (var3 == null ? "" : "_" + var3) + ".png";
      return (ResourceLocation)ARMOR_LOCATION_CACHE.computeIfAbsent(var4, ResourceLocation::new);
   }

   protected abstract void setPartVisibility(HumanoidModel var1, EquipmentSlot var2);

   protected abstract void hideAllArmor(HumanoidModel var1);
}
