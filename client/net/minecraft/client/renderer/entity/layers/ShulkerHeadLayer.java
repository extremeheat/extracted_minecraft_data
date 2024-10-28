package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Shulker;

public class ShulkerHeadLayer extends RenderLayer<Shulker, ShulkerModel<Shulker>> {
   public ShulkerHeadLayer(RenderLayerParent<Shulker, ShulkerModel<Shulker>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Shulker var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      ResourceLocation var11 = ShulkerRenderer.getTextureLocation(var4.getColor());
      VertexConsumer var12 = var2.getBuffer(RenderType.entitySolid(var11));
      ((ShulkerModel)this.getParentModel()).getHead().render(var1, var12, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F));
   }
}
