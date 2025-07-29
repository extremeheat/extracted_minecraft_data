package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class HumanoidArmorLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> extends RenderLayer<S, M> {
   private final ArmorModelSet<A> modelSet;
   private final ArmorModelSet<A> babyModelSet;
   private final EquipmentLayerRenderer equipmentRenderer;

   public HumanoidArmorLayer(RenderLayerParent<S, M> var1, ArmorModelSet<A> var2, EquipmentLayerRenderer var3) {
      this(var1, var2, var2, var3);
   }

   public HumanoidArmorLayer(RenderLayerParent<S, M> var1, ArmorModelSet<A> var2, ArmorModelSet<A> var3, EquipmentLayerRenderer var4) {
      super(var1);
      this.modelSet = var2;
      this.babyModelSet = var3;
      this.equipmentRenderer = var4;
   }

   public static boolean shouldRender(ItemStack var0, EquipmentSlot var1) {
      Equippable var2 = (Equippable)var0.get(DataComponents.EQUIPPABLE);
      return var2 != null && shouldRender(var2, var1);
   }

   private static boolean shouldRender(Equippable var0, EquipmentSlot var1) {
      return var0.assetId().isPresent() && var0.slot() == var1;
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, S var4, float var5, float var6) {
      this.renderArmorPiece(var1, var2, var4.chestEquipment, EquipmentSlot.CHEST, var3, var4);
      this.renderArmorPiece(var1, var2, var4.legsEquipment, EquipmentSlot.LEGS, var3, var4);
      this.renderArmorPiece(var1, var2, var4.feetEquipment, EquipmentSlot.FEET, var3, var4);
      this.renderArmorPiece(var1, var2, var4.headEquipment, EquipmentSlot.HEAD, var3, var4);
   }

   private void renderArmorPiece(PoseStack var1, SubmitNodeCollector var2, ItemStack var3, EquipmentSlot var4, int var5, S var6) {
      Equippable var7 = (Equippable)var3.get(DataComponents.EQUIPPABLE);
      if (var7 != null && shouldRender(var7, var4)) {
         HumanoidModel var8 = this.getArmorModel(var6, var4);
         EquipmentClientInfo.LayerType var9 = this.usesInnerModel(var4) ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;
         this.equipmentRenderer.renderLayers(var9, (ResourceKey)var7.assetId().orElseThrow(), var8, var6, var3, var1, var2, var5, var6.outlineColor);
      }
   }

   private A getArmorModel(S var1, EquipmentSlot var2) {
      return (A)((var1.isBaby ? this.babyModelSet : this.modelSet).get(var2));
   }

   private boolean usesInnerModel(EquipmentSlot var1) {
      return var1 == EquipmentSlot.LEGS;
   }
}
