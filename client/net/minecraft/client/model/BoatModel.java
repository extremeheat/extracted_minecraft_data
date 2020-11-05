package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatModel extends ListModel<Boat> {
   private final ModelPart leftPaddle;
   private final ModelPart rightPaddle;
   private final ModelPart waterPatch;
   private final ImmutableList<ModelPart> parts;

   public BoatModel(ModelPart var1) {
      super();
      this.leftPaddle = var1.getChild("left_paddle");
      this.rightPaddle = var1.getChild("right_paddle");
      this.waterPatch = var1.getChild("water_patch");
      this.parts = ImmutableList.of(var1.getChild("bottom"), var1.getChild("back"), var1.getChild("front"), var1.getChild("right"), var1.getChild("left"), this.leftPaddle, this.rightPaddle);
   }

   public static LayerDefinition createBodyModel() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      boolean var2 = true;
      boolean var3 = true;
      boolean var4 = true;
      boolean var5 = true;
      boolean var6 = true;
      var1.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 3.0F, 1.0F, 1.5707964F, 0.0F, 0.0F));
      var1.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 19).addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(-15.0F, 4.0F, 4.0F, 0.0F, 4.712389F, 0.0F));
      var1.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 27).addBox(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(15.0F, 4.0F, 0.0F, 0.0F, 1.5707964F, 0.0F));
      var1.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 35).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 4.0F, -9.0F, 0.0F, 3.1415927F, 0.0F));
      var1.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 43).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), PartPose.offset(0.0F, 4.0F, 9.0F));
      boolean var7 = true;
      boolean var8 = true;
      boolean var9 = true;
      float var10 = -5.0F;
      var1.addOrReplaceChild("left_paddle", CubeListBuilder.create().texOffs(62, 0).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F), PartPose.offsetAndRotation(3.0F, -5.0F, 9.0F, 0.0F, 0.0F, 0.19634955F));
      var1.addOrReplaceChild("right_paddle", CubeListBuilder.create().texOffs(62, 20).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F), PartPose.offsetAndRotation(3.0F, -5.0F, -9.0F, 0.0F, 3.1415927F, 0.19634955F));
      var1.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, 1.5707964F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 128, 64);
   }

   public void setupAnim(Boat var1, float var2, float var3, float var4, float var5, float var6) {
      animatePaddle(var1, 0, this.leftPaddle, var2);
      animatePaddle(var1, 1, this.rightPaddle, var2);
   }

   public ImmutableList<ModelPart> parts() {
      return this.parts;
   }

   public ModelPart waterPatch() {
      return this.waterPatch;
   }

   private static void animatePaddle(Boat var0, int var1, ModelPart var2, float var3) {
      float var4 = var0.getRowingTime(var1, var3);
      var2.xRot = (float)Mth.clampedLerp(-1.0471975803375244D, -0.2617993950843811D, (double)((Mth.sin(-var4) + 1.0F) / 2.0F));
      var2.yRot = (float)Mth.clampedLerp(-0.7853981852531433D, 0.7853981852531433D, (double)((Mth.sin(-var4 + 1.0F) + 1.0F) / 2.0F));
      if (var1 == 1) {
         var2.yRot = 3.1415927F - var2.yRot;
      }

   }

   // $FF: synthetic method
   public Iterable parts() {
      return this.parts();
   }
}
