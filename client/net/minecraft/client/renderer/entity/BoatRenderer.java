package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

public class BoatRenderer extends EntityRenderer<Boat, BoatRenderState> {
   private final Map<Boat.Type, Pair<ResourceLocation, EntityModel<BoatRenderState>>> boatResources;
   private final Model waterPatchModel;

   public BoatRenderer(EntityRendererProvider.Context var1, boolean var2) {
      super(var1);
      this.shadowRadius = 0.8F;
      this.boatResources = Stream.of(Boat.Type.values())
         .collect(ImmutableMap.toImmutableMap(var0 -> var0, var3 -> Pair.of(getTextureLocation(var3, var2), this.createBoatModel(var1, var3, var2))));
      this.waterPatchModel = new Model.Simple(var1.bakeLayer(ModelLayers.BOAT_WATER_PATCH), var0 -> RenderType.waterMask());
   }

   private EntityModel<BoatRenderState> createBoatModel(EntityRendererProvider.Context var1, Boat.Type var2, boolean var3) {
      ModelLayerLocation var4 = var3 ? ModelLayers.createChestBoatModelName(var2) : ModelLayers.createBoatModelName(var2);
      ModelPart var5 = var1.bakeLayer(var4);

      return (EntityModel<BoatRenderState>)(switch (var2) {
         case BAMBOO -> new RaftModel(var5);
         default -> new BoatModel(var5);
      });
   }

   private static ResourceLocation getTextureLocation(Boat.Type var0, boolean var1) {
      return var1
         ? ResourceLocation.withDefaultNamespace("textures/entity/chest_boat/" + var0.getName() + ".png")
         : ResourceLocation.withDefaultNamespace("textures/entity/boat/" + var0.getName() + ".png");
   }

   public void render(BoatRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.translate(0.0F, 0.375F, 0.0F);
      var2.mulPose(Axis.YP.rotationDegrees(180.0F - var1.yRot));
      float var5 = var1.hurtTime;
      if (var5 > 0.0F) {
         var2.mulPose(Axis.XP.rotationDegrees(Mth.sin(var5) * var5 * var1.damageTime / 10.0F * (float)var1.hurtDir));
      }

      if (!Mth.equal(var1.bubbleAngle, 0.0F)) {
         var2.mulPose(new Quaternionf().setAngleAxis(var1.bubbleAngle * 0.017453292F, 1.0F, 0.0F, 1.0F));
      }

      Pair var6 = this.boatResources.get(var1.variant);
      ResourceLocation var7 = (ResourceLocation)var6.getFirst();
      EntityModel var8 = (EntityModel)var6.getSecond();
      var2.scale(-1.0F, -1.0F, 1.0F);
      var2.mulPose(Axis.YP.rotationDegrees(90.0F));
      var8.setupAnim(var1);
      VertexConsumer var9 = var3.getBuffer(var8.renderType(var7));
      var8.renderToBuffer(var2, var9, var4, OverlayTexture.NO_OVERLAY);
      if (!var1.isUnderWater && var1.variant != Boat.Type.BAMBOO) {
         this.waterPatchModel.renderToBuffer(var2, var3.getBuffer(this.waterPatchModel.renderType(var7)), var4, OverlayTexture.NO_OVERLAY);
      }

      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   public BoatRenderState createRenderState() {
      return new BoatRenderState();
   }

   public void extractRenderState(Boat var1, BoatRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.yRot = var1.getYRot(var3);
      var2.hurtTime = (float)var1.getHurtTime() - var3;
      var2.hurtDir = var1.getHurtDir();
      var2.damageTime = Math.max(var1.getDamage() - var3, 0.0F);
      var2.bubbleAngle = var1.getBubbleAngle(var3);
      var2.isUnderWater = var1.isUnderWater();
      var2.variant = var1.getVariant();
      var2.rowingTimeLeft = var1.getRowingTime(0, var3);
      var2.rowingTimeRight = var1.getRowingTime(1, var3);
   }
}
