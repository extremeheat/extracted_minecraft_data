package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.world.item.equipment.EquipmentModels;
import net.minecraft.world.item.equipment.Equippable;

public class LlamaDecorLayer extends RenderLayer<LlamaRenderState, LlamaModel> {
   private final LlamaModel adultModel;
   private final LlamaModel babyModel;
   private final EquipmentLayerRenderer equipmentRenderer;

   public LlamaDecorLayer(RenderLayerParent<LlamaRenderState, LlamaModel> var1, EntityModelSet var2, EquipmentLayerRenderer var3) {
      super(var1);
      this.equipmentRenderer = var3;
      this.adultModel = new LlamaModel(var2.bakeLayer(ModelLayers.LLAMA_DECOR));
      this.babyModel = new LlamaModel(var2.bakeLayer(ModelLayers.LLAMA_BABY_DECOR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, LlamaRenderState var4, float var5, float var6) {
      ItemStack var7 = var4.bodyItem;
      Equippable var8 = (Equippable)var7.get(DataComponents.EQUIPPABLE);
      if (var8 != null && var8.model().isPresent()) {
         this.renderEquipment(var1, var2, var4, var7, (ResourceLocation)var8.model().get(), var3);
      } else if (var4.isTraderLlama) {
         this.renderEquipment(var1, var2, var4, ItemStack.EMPTY, EquipmentModels.TRADER_LLAMA, var3);
      }

   }

   private void renderEquipment(PoseStack var1, MultiBufferSource var2, LlamaRenderState var3, ItemStack var4, ResourceLocation var5, int var6) {
      LlamaModel var7 = var3.isBaby ? this.babyModel : this.adultModel;
      var7.setupAnim(var3);
      this.equipmentRenderer.renderLayers(EquipmentModel.LayerType.LLAMA_BODY, var5, var7, var4, var1, var2, var6);
   }
}
