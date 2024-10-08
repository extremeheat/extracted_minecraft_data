package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.world.item.equipment.Equippable;

public class HumanoidArmorLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> extends RenderLayer<S, M> {
   private final A innerModel;
   private final A outerModel;
   private final A innerModelBaby;
   private final A outerModelBaby;
   private final EquipmentLayerRenderer equipmentRenderer;

   public HumanoidArmorLayer(RenderLayerParent<S, M> var1, A var2, A var3, EquipmentLayerRenderer var4) {
      this(var1, (A)var2, (A)var3, (A)var2, (A)var3, var4);
   }

   public HumanoidArmorLayer(RenderLayerParent<S, M> var1, A var2, A var3, A var4, A var5, EquipmentLayerRenderer var6) {
      super(var1);
      this.innerModel = (A)var2;
      this.outerModel = (A)var3;
      this.innerModelBaby = (A)var4;
      this.outerModelBaby = (A)var5;
      this.equipmentRenderer = var6;
   }

   public static boolean shouldRender(ItemStack var0, EquipmentSlot var1) {
      Equippable var2 = var0.get(DataComponents.EQUIPPABLE);
      return var2 != null && shouldRender(var2, var1);
   }

   private static boolean shouldRender(Equippable var0, EquipmentSlot var1) {
      return var0.model().isPresent() && var0.slot() == var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      this.renderArmorPiece(var1, var2, var4.chestItem, EquipmentSlot.CHEST, var3, this.getArmorModel((S)var4, EquipmentSlot.CHEST));
      this.renderArmorPiece(var1, var2, var4.legsItem, EquipmentSlot.LEGS, var3, this.getArmorModel((S)var4, EquipmentSlot.LEGS));
      this.renderArmorPiece(var1, var2, var4.feetItem, EquipmentSlot.FEET, var3, this.getArmorModel((S)var4, EquipmentSlot.FEET));
      this.renderArmorPiece(var1, var2, var4.headItem, EquipmentSlot.HEAD, var3, this.getArmorModel((S)var4, EquipmentSlot.HEAD));
   }

   private void renderArmorPiece(PoseStack var1, MultiBufferSource var2, ItemStack var3, EquipmentSlot var4, int var5, A var6) {
      Equippable var7 = var3.get(DataComponents.EQUIPPABLE);
      if (var7 != null && shouldRender(var7, var4)) {
         this.getParentModel().copyPropertiesTo(var6);
         this.setPartVisibility((A)var6, var4);
         ResourceLocation var8 = var7.model().orElseThrow();
         EquipmentModel.LayerType var9 = this.usesInnerModel(var4) ? EquipmentModel.LayerType.HUMANOID_LEGGINGS : EquipmentModel.LayerType.HUMANOID;
         this.equipmentRenderer.renderLayers(var9, var8, var6, var3, var1, var2, var5);
      }
   }

   protected void setPartVisibility(A var1, EquipmentSlot var2) {
      var1.setAllVisible(false);
      switch (var2) {
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

   private A getArmorModel(S var1, EquipmentSlot var2) {
      if (this.usesInnerModel(var2)) {
         return var1.isBaby ? this.innerModelBaby : this.innerModel;
      } else {
         return var1.isBaby ? this.outerModelBaby : this.outerModel;
      }
   }

   private boolean usesInnerModel(EquipmentSlot var1) {
      return var1 == EquipmentSlot.LEGS;
   }
}
