package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatRenderer extends EntityRenderer {
   private static final ResourceLocation[] BOAT_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/boat/oak.png"), new ResourceLocation("textures/entity/boat/spruce.png"), new ResourceLocation("textures/entity/boat/birch.png"), new ResourceLocation("textures/entity/boat/jungle.png"), new ResourceLocation("textures/entity/boat/acacia.png"), new ResourceLocation("textures/entity/boat/dark_oak.png")};
   protected final BoatModel model = new BoatModel();

   public BoatRenderer(EntityRenderDispatcher var1) {
      super(var1);
      this.shadowRadius = 0.8F;
   }

   public void render(Boat var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      var4.translate(0.0D, 0.375D, 0.0D);
      var4.mulPose(Vector3f.YP.rotationDegrees(180.0F - var2));
      float var7 = (float)var1.getHurtTime() - var3;
      float var8 = var1.getDamage() - var3;
      if (var8 < 0.0F) {
         var8 = 0.0F;
      }

      if (var7 > 0.0F) {
         var4.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(var7) * var7 * var8 / 10.0F * (float)var1.getHurtDir()));
      }

      float var9 = var1.getBubbleAngle(var3);
      if (!Mth.equal(var9, 0.0F)) {
         var4.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), var1.getBubbleAngle(var3), true));
      }

      var4.scale(-1.0F, -1.0F, 1.0F);
      var4.mulPose(Vector3f.YP.rotationDegrees(90.0F));
      this.model.setupAnim(var1, var3, 0.0F, -0.1F, 0.0F, 0.0F);
      VertexConsumer var10 = var5.getBuffer(this.model.renderType(this.getTextureLocation(var1)));
      this.model.renderToBuffer(var4, var10, var6, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      VertexConsumer var11 = var5.getBuffer(RenderType.waterMask());
      this.model.waterPatch().render(var4, var11, var6, OverlayTexture.NO_OVERLAY);
      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(Boat var1) {
      return BOAT_TEXTURE_LOCATIONS[var1.getBoatType().ordinal()];
   }
}
