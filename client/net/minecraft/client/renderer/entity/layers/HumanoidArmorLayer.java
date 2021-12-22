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

public class HumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
   private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
   private final A innerModel;
   private final A outerModel;

   public HumanoidArmorLayer(RenderLayerParent<T, M> var1, A var2, A var3) {
      super(var1);
      this.innerModel = var2;
      this.outerModel = var3;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      this.renderArmorPiece(var1, var2, var4, EquipmentSlot.CHEST, var3, this.getArmorModel(EquipmentSlot.CHEST));
      this.renderArmorPiece(var1, var2, var4, EquipmentSlot.LEGS, var3, this.getArmorModel(EquipmentSlot.LEGS));
      this.renderArmorPiece(var1, var2, var4, EquipmentSlot.FEET, var3, this.getArmorModel(EquipmentSlot.FEET));
      this.renderArmorPiece(var1, var2, var4, EquipmentSlot.HEAD, var3, this.getArmorModel(EquipmentSlot.HEAD));
   }

   private void renderArmorPiece(PoseStack var1, MultiBufferSource var2, T var3, EquipmentSlot var4, int var5, A var6) {
      ItemStack var7 = var3.getItemBySlot(var4);
      if (var7.getItem() instanceof ArmorItem) {
         ArmorItem var8 = (ArmorItem)var7.getItem();
         if (var8.getSlot() == var4) {
            ((HumanoidModel)this.getParentModel()).copyPropertiesTo(var6);
            this.setPartVisibility(var6, var4);
            boolean var9 = this.usesInnerModel(var4);
            boolean var10 = var7.hasFoil();
            if (var8 instanceof DyeableArmorItem) {
               int var11 = ((DyeableArmorItem)var8).getColor(var7);
               float var12 = (float)(var11 >> 16 & 255) / 255.0F;
               float var13 = (float)(var11 >> 8 & 255) / 255.0F;
               float var14 = (float)(var11 & 255) / 255.0F;
               this.renderModel(var1, var2, var5, var8, var10, var6, var9, var12, var13, var14, (String)null);
               this.renderModel(var1, var2, var5, var8, var10, var6, var9, 1.0F, 1.0F, 1.0F, "overlay");
            } else {
               this.renderModel(var1, var2, var5, var8, var10, var6, var9, 1.0F, 1.0F, 1.0F, (String)null);
            }

         }
      }
   }

   protected void setPartVisibility(A var1, EquipmentSlot var2) {
      var1.setAllVisible(false);
      switch(var2) {
      case HEAD:
         var1.head.visible = true;
         var1.hat.visible = true;
         break;
      case CHEST:
         var1.body.visible = true;
         var1.rightArm.visible = true;
         var1.leftArm.visible = true;
         break;
      case LEGS:
         var1.body.visible = true;
         var1.rightLeg.visible = true;
         var1.leftLeg.visible = true;
         break;
      case FEET:
         var1.rightLeg.visible = true;
         var1.leftLeg.visible = true;
      }

   }

   private void renderModel(PoseStack var1, MultiBufferSource var2, int var3, ArmorItem var4, boolean var5, A var6, boolean var7, float var8, float var9, float var10, @Nullable String var11) {
      VertexConsumer var12 = ItemRenderer.getArmorFoilBuffer(var2, RenderType.armorCutoutNoCull(this.getArmorLocation(var4, var7, var11)), false, var5);
      var6.renderToBuffer(var1, var12, var3, OverlayTexture.NO_OVERLAY, var8, var9, var10, 1.0F);
   }

   private A getArmorModel(EquipmentSlot var1) {
      return this.usesInnerModel(var1) ? this.innerModel : this.outerModel;
   }

   private boolean usesInnerModel(EquipmentSlot var1) {
      return var1 == EquipmentSlot.LEGS;
   }

   private ResourceLocation getArmorLocation(ArmorItem var1, boolean var2, @Nullable String var3) {
      String var10000 = var1.getMaterial().getName();
      String var4 = "textures/models/armor/" + var10000 + "_layer_" + (var2 ? 2 : 1) + (var3 == null ? "" : "_" + var3) + ".png";
      return (ResourceLocation)ARMOR_LOCATION_CACHE.computeIfAbsent(var4, ResourceLocation::new);
   }
}
