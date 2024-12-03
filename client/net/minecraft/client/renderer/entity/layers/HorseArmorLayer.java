package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class HorseArmorLayer extends RenderLayer<HorseRenderState, HorseModel> {
   private final HorseModel adultModel;
   private final HorseModel babyModel;
   private final EquipmentLayerRenderer equipmentRenderer;

   public HorseArmorLayer(RenderLayerParent<HorseRenderState, HorseModel> var1, EntityModelSet var2, EquipmentLayerRenderer var3) {
      super(var1);
      this.equipmentRenderer = var3;
      this.adultModel = new HorseModel(var2.bakeLayer(ModelLayers.HORSE_ARMOR));
      this.babyModel = new HorseModel(var2.bakeLayer(ModelLayers.HORSE_BABY_ARMOR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, HorseRenderState var4, float var5, float var6) {
      ItemStack var7 = var4.bodyArmorItem;
      Equippable var8 = (Equippable)var7.get(DataComponents.EQUIPPABLE);
      if (var8 != null && !var8.assetId().isEmpty()) {
         HorseModel var9 = var4.isBaby ? this.babyModel : this.adultModel;
         var9.setupAnim(var4);
         this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.HORSE_BODY, (ResourceKey)var8.assetId().get(), var9, var7, var1, var2, var3);
      }
   }
}
