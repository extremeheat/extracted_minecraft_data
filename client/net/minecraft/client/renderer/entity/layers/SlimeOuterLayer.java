package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;

public class SlimeOuterLayer<T extends LivingEntity> extends RenderLayer<T, SlimeModel<T>> {
   private final EntityModel<T> model;

   public SlimeOuterLayer(RenderLayerParent<T, SlimeModel<T>> var1, EntityModelSet var2) {
      super(var1);
      this.model = new SlimeModel(var2.bakeLayer(ModelLayers.SLIME_OUTER));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      Minecraft var11 = Minecraft.getInstance();
      boolean var12 = var11.shouldEntityAppearGlowing(var4) && var4.isInvisible();
      if (!var4.isInvisible() || var12) {
         VertexConsumer var13;
         if (var12) {
            var13 = var2.getBuffer(RenderType.outline(this.getTextureLocation(var4)));
         } else {
            var13 = var2.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(var4)));
         }

         ((SlimeModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.prepareMobModel(var4, var5, var6, var7);
         this.model.setupAnim(var4, var5, var6, var8, var9, var10);
         this.model.renderToBuffer(var1, var13, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F));
      }
   }
}
