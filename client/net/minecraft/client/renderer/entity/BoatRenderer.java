package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatRenderer extends EntityRenderer<Boat> {
   private final Map<Boat.Type, Pair<ResourceLocation, BoatModel>> boatResources;

   public BoatRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.8F;
      this.boatResources = (Map)Stream.of(Boat.Type.values()).collect(ImmutableMap.toImmutableMap((var0) -> {
         return var0;
      }, (var1x) -> {
         return Pair.of(new ResourceLocation("textures/entity/boat/" + var1x.getName() + ".png"), new BoatModel(var1.getLayer(ModelLayers.createBoatModelName(var1x))));
      }));
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

      Pair var10 = (Pair)this.boatResources.get(var1.getBoatType());
      ResourceLocation var11 = (ResourceLocation)var10.getFirst();
      BoatModel var12 = (BoatModel)var10.getSecond();
      var4.scale(-1.0F, -1.0F, 1.0F);
      var4.mulPose(Vector3f.YP.rotationDegrees(90.0F));
      var12.setupAnim(var1, var3, 0.0F, -0.1F, 0.0F, 0.0F);
      VertexConsumer var13 = var5.getBuffer(var12.renderType(var11));
      var12.renderToBuffer(var4, var13, var6, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      if (!var1.isUnderWater()) {
         VertexConsumer var14 = var5.getBuffer(RenderType.waterMask());
         var12.waterPatch().render(var4, var14, var6, OverlayTexture.NO_OVERLAY);
      }

      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(Boat var1) {
      return (ResourceLocation)((Pair)this.boatResources.get(var1.getBoatType())).getFirst();
   }
}
