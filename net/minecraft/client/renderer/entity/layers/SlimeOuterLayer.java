package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;

public class SlimeOuterLayer extends RenderLayer {
   private final EntityModel model = new SlimeModel(0);

   public SlimeOuterLayer(RenderLayerParent var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, LivingEntity var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (!var4.isInvisible()) {
         ((SlimeModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.prepareMobModel(var4, var5, var6, var7);
         this.model.setupAnim(var4, var5, var6, var8, var9, var10);
         VertexConsumer var11 = var2.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(var4)));
         this.model.renderToBuffer(var1, var11, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
