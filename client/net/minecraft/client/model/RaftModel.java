package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

public class RaftModel extends ListModel<Boat> {
   private static final String LEFT_PADDLE = "left_paddle";
   private static final String RIGHT_PADDLE = "right_paddle";
   private static final String BOTTOM = "bottom";
   private final ModelPart leftPaddle;
   private final ModelPart rightPaddle;
   private final ImmutableList<ModelPart> parts;

   public RaftModel(ModelPart var1) {
      super();
      this.leftPaddle = var1.getChild("left_paddle");
      this.rightPaddle = var1.getChild("right_paddle");
      this.parts = this.createPartsBuilder(var1).build();
   }

   protected Builder<ModelPart> createPartsBuilder(ModelPart var1) {
      Builder var2 = new Builder();
      var2.add(new ModelPart[]{var1.getChild("bottom"), this.leftPaddle, this.rightPaddle});
      return var2;
   }

   public static void createChildren(PartDefinition var0) {
      var0.addOrReplaceChild(
         "bottom",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-14.0F, -11.0F, -4.0F, 28.0F, 20.0F, 4.0F)
            .texOffs(0, 0)
            .addBox(-14.0F, -9.0F, -8.0F, 28.0F, 16.0F, 4.0F),
         PartPose.offsetAndRotation(0.0F, -2.1F, 1.0F, 1.5708F, 0.0F, 0.0F)
      );
      byte var1 = 20;
      byte var2 = 7;
      byte var3 = 6;
      float var4 = -5.0F;
      var0.addOrReplaceChild(
         "left_paddle",
         CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
         PartPose.offsetAndRotation(3.0F, -4.0F, 9.0F, 0.0F, 0.0F, 0.19634955F)
      );
      var0.addOrReplaceChild(
         "right_paddle",
         CubeListBuilder.create().texOffs(40, 24).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
         PartPose.offsetAndRotation(3.0F, -4.0F, -9.0F, 0.0F, 3.1415927F, 0.19634955F)
      );
   }

   public static LayerDefinition createBodyModel() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      createChildren(var1);
      return LayerDefinition.create(var0, 128, 64);
   }

   public void setupAnim(Boat var1, float var2, float var3, float var4, float var5, float var6) {
      animatePaddle(var1, 0, this.leftPaddle, var2);
      animatePaddle(var1, 1, this.rightPaddle, var2);
   }

   public ImmutableList<ModelPart> parts() {
      return this.parts;
   }

   private static void animatePaddle(Boat var0, int var1, ModelPart var2, float var3) {
      float var4 = var0.getRowingTime(var1, var3);
      var2.xRot = Mth.clampedLerp(-1.0471976F, -0.2617994F, (Mth.sin(-var4) + 1.0F) / 2.0F);
      var2.yRot = Mth.clampedLerp(-0.7853982F, 0.7853982F, (Mth.sin(-var4 + 1.0F) + 1.0F) / 2.0F);
      if (var1 == 1) {
         var2.yRot = 3.1415927F - var2.yRot;
      }
   }
}
