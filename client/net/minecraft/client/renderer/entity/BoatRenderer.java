package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ChestRaftModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

public class BoatRenderer extends EntityRenderer<Boat> {
   private final Map<Boat.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

   public BoatRenderer(EntityRendererProvider.Context var1, boolean var2) {
      super(var1);
      this.shadowRadius = 0.8F;
      this.boatResources = (Map)Stream.of(Boat.Type.values()).collect(ImmutableMap.toImmutableMap((var0) -> {
         return var0;
      }, (var3) -> {
         return Pair.of(getTextureLocation(var3, var2), this.createBoatModel(var1, var3, var2));
      }));
   }

   private ListModel<Boat> createBoatModel(EntityRendererProvider.Context var1, Boat.Type var2, boolean var3) {
      ModelLayerLocation var4 = var3 ? ModelLayers.createChestBoatModelName(var2) : ModelLayers.createBoatModelName(var2);
      ModelPart var5 = var1.bakeLayer(var4);
      if (var2 == Boat.Type.BAMBOO) {
         return (ListModel)(var3 ? new ChestRaftModel(var5) : new RaftModel(var5));
      } else {
         return (ListModel)(var3 ? new ChestBoatModel(var5) : new BoatModel(var5));
      }
   }

   private static ResourceLocation getTextureLocation(Boat.Type var0, boolean var1) {
      return var1 ? ResourceLocation.withDefaultNamespace("textures/entity/chest_boat/" + var0.getName() + ".png") : ResourceLocation.withDefaultNamespace("textures/entity/boat/" + var0.getName() + ".png");
   }

   public void render(Boat var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      var4.translate(0.0F, 0.375F, 0.0F);
      var4.mulPose(Axis.YP.rotationDegrees(180.0F - var2));
      float var7 = (float)var1.getHurtTime() - var3;
      float var8 = var1.getDamage() - var3;
      if (var8 < 0.0F) {
         var8 = 0.0F;
      }

      if (var7 > 0.0F) {
         var4.mulPose(Axis.XP.rotationDegrees(Mth.sin(var7) * var7 * var8 / 10.0F * (float)var1.getHurtDir()));
      }

      float var9 = var1.getBubbleAngle(var3);
      if (!Mth.equal(var9, 0.0F)) {
         var4.mulPose((new Quaternionf()).setAngleAxis(var1.getBubbleAngle(var3) * 0.017453292F, 1.0F, 0.0F, 1.0F));
      }

      Pair var10 = (Pair)this.boatResources.get(var1.getVariant());
      ResourceLocation var11 = (ResourceLocation)var10.getFirst();
      ListModel var12 = (ListModel)var10.getSecond();
      var4.scale(-1.0F, -1.0F, 1.0F);
      var4.mulPose(Axis.YP.rotationDegrees(90.0F));
      var12.setupAnim(var1, var3, 0.0F, -0.1F, 0.0F, 0.0F);
      VertexConsumer var13 = var5.getBuffer(var12.renderType(var11));
      var12.renderToBuffer(var4, var13, var6, OverlayTexture.NO_OVERLAY);
      if (!var1.isUnderWater()) {
         VertexConsumer var14 = var5.getBuffer(RenderType.waterMask());
         if (var12 instanceof WaterPatchModel) {
            WaterPatchModel var15 = (WaterPatchModel)var12;
            var15.waterPatch().render(var4, var14, var6, OverlayTexture.NO_OVERLAY);
         }
      }

      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(Boat var1) {
      return (ResourceLocation)((Pair)this.boatResources.get(var1.getVariant())).getFirst();
   }
}
