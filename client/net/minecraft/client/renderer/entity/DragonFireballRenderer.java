package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import org.joml.Quaternionfc;

public class DragonFireballRenderer extends EntityRenderer<DragonFireball, EntityRenderState> {
   private static final Identifier TEXTURE_LOCATION = Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon_fireball.png");
   private static final RenderType RENDER_TYPE;

   public DragonFireballRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   protected int getBlockLightLevel(DragonFireball var1, BlockPos var2) {
      return 15;
   }

   public void submit(EntityRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      var2.scale(2.0F, 2.0F, 2.0F);
      var2.mulPose((Quaternionfc)var4.orientation);
      var3.submitCustomGeometry(var2, RENDER_TYPE, (var1x, var2x) -> {
         vertex(var2x, var1x, var1.lightCoords, 0.0F, 0, 0, 1);
         vertex(var2x, var1x, var1.lightCoords, 1.0F, 0, 1, 1);
         vertex(var2x, var1x, var1.lightCoords, 1.0F, 1, 1, 0);
         vertex(var2x, var1x, var1.lightCoords, 0.0F, 1, 0, 0);
      });
      var2.popPose();
      super.submit(var1, var2, var3, var4);
   }

   private static void vertex(VertexConsumer var0, PoseStack.Pose var1, int var2, float var3, int var4, int var5, int var6) {
      var0.addVertex(var1, var3 - 0.5F, (float)var4 - 0.25F, 0.0F).setColor(-1).setUv((float)var5, (float)var6).setOverlay(OverlayTexture.NO_OVERLAY).setLight(var2).setNormal(var1, 0.0F, 1.0F, 0.0F);
   }

   public EntityRenderState createRenderState() {
      return new EntityRenderState();
   }

   static {
      RENDER_TYPE = RenderTypes.entityCutoutNoCull(TEXTURE_LOCATION);
   }
}
