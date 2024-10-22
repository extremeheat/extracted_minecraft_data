package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.DragonFireball;

public class DragonFireballRenderer extends EntityRenderer<DragonFireball, EntityRenderState> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_fireball.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

   public DragonFireballRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   protected int getBlockLightLevel(DragonFireball var1, BlockPos var2) {
      return 15;
   }

   @Override
   public void render(EntityRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.scale(2.0F, 2.0F, 2.0F);
      var2.mulPose(this.entityRenderDispatcher.cameraOrientation());
      PoseStack.Pose var5 = var2.last();
      VertexConsumer var6 = var3.getBuffer(RENDER_TYPE);
      vertex(var6, var5, var4, 0.0F, 0, 0, 1);
      vertex(var6, var5, var4, 1.0F, 0, 1, 1);
      vertex(var6, var5, var4, 1.0F, 1, 1, 0);
      vertex(var6, var5, var4, 0.0F, 1, 0, 0);
      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   private static void vertex(VertexConsumer var0, PoseStack.Pose var1, int var2, float var3, int var4, int var5, int var6) {
      var0.addVertex(var1, var3 - 0.5F, (float)var4 - 0.25F, 0.0F)
         .setColor(-1)
         .setUv((float)var5, (float)var6)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(var2)
         .setNormal(var1, 0.0F, 1.0F, 0.0F);
   }

   @Override
   public EntityRenderState createRenderState() {
      return new EntityRenderState();
   }
}
