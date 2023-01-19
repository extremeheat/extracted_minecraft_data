package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotModel extends HierarchicalModel<Parrot> {
   private static final String FEATHER = "feather";
   private final ModelPart root;
   private final ModelPart body;
   private final ModelPart tail;
   private final ModelPart leftWing;
   private final ModelPart rightWing;
   private final ModelPart head;
   private final ModelPart feather;
   private final ModelPart leftLeg;
   private final ModelPart rightLeg;

   public ParrotModel(ModelPart var1) {
      super();
      this.root = var1;
      this.body = var1.getChild("body");
      this.tail = var1.getChild("tail");
      this.leftWing = var1.getChild("left_wing");
      this.rightWing = var1.getChild("right_wing");
      this.head = var1.getChild("head");
      this.feather = this.head.getChild("feather");
      this.leftLeg = var1.getChild("left_leg");
      this.rightLeg = var1.getChild("right_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(2, 8).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offset(0.0F, 16.5F, -3.0F));
      var1.addOrReplaceChild(
         "tail", CubeListBuilder.create().texOffs(22, 1).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 21.07F, 1.16F)
      );
      var1.addOrReplaceChild(
         "left_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), PartPose.offset(1.5F, 16.94F, -2.76F)
      );
      var1.addOrReplaceChild(
         "right_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), PartPose.offset(-1.5F, 16.94F, -2.76F)
      );
      PartDefinition var2 = var1.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(2, 2).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(0.0F, 15.69F, -2.76F)
      );
      var2.addOrReplaceChild(
         "head2", CubeListBuilder.create().texOffs(10, 0).addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F), PartPose.offset(0.0F, -2.0F, -1.0F)
      );
      var2.addOrReplaceChild(
         "beak1", CubeListBuilder.create().texOffs(11, 7).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -0.5F, -1.5F)
      );
      var2.addOrReplaceChild(
         "beak2", CubeListBuilder.create().texOffs(16, 7).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -1.75F, -2.45F)
      );
      var2.addOrReplaceChild(
         "feather", CubeListBuilder.create().texOffs(2, 18).addBox(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F), PartPose.offset(0.0F, -2.15F, 0.15F)
      );
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(14, 18).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
      var1.addOrReplaceChild("left_leg", var3, PartPose.offset(1.0F, 22.0F, -1.05F));
      var1.addOrReplaceChild("right_leg", var3, PartPose.offset(-1.0F, 22.0F, -1.05F));
      return LayerDefinition.create(var0, 32, 32);
   }

   @Override
   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(Parrot var1, float var2, float var3, float var4, float var5, float var6) {
      this.setupAnim(getState(var1), var1.tickCount, var2, var3, var4, var5, var6);
   }

   public void prepareMobModel(Parrot var1, float var2, float var3, float var4) {
      this.prepare(getState(var1));
   }

   public void renderOnShoulder(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8, int var9) {
      this.prepare(ParrotModel.State.ON_SHOULDER);
      this.setupAnim(ParrotModel.State.ON_SHOULDER, var9, var5, var6, 0.0F, var7, var8);
      this.root.render(var1, var2, var3, var4);
   }

   private void setupAnim(ParrotModel.State var1, int var2, float var3, float var4, float var5, float var6, float var7) {
      this.head.xRot = var7 * 0.017453292F;
      this.head.yRot = var6 * 0.017453292F;
      this.head.zRot = 0.0F;
      this.head.x = 0.0F;
      this.body.x = 0.0F;
      this.tail.x = 0.0F;
      this.rightWing.x = -1.5F;
      this.leftWing.x = 1.5F;
      switch(var1) {
         case SITTING:
            break;
         case PARTY:
            float var8 = Mth.cos((float)var2);
            float var9 = Mth.sin((float)var2);
            this.head.x = var8;
            this.head.y = 15.69F + var9;
            this.head.xRot = 0.0F;
            this.head.yRot = 0.0F;
            this.head.zRot = Mth.sin((float)var2) * 0.4F;
            this.body.x = var8;
            this.body.y = 16.5F + var9;
            this.leftWing.zRot = -0.0873F - var5;
            this.leftWing.x = 1.5F + var8;
            this.leftWing.y = 16.94F + var9;
            this.rightWing.zRot = 0.0873F + var5;
            this.rightWing.x = -1.5F + var8;
            this.rightWing.y = 16.94F + var9;
            this.tail.x = var8;
            this.tail.y = 21.07F + var9;
            break;
         case STANDING:
            this.leftLeg.xRot += Mth.cos(var3 * 0.6662F) * 1.4F * var4;
            this.rightLeg.xRot += Mth.cos(var3 * 0.6662F + 3.1415927F) * 1.4F * var4;
         case FLYING:
         case ON_SHOULDER:
         default:
            float var10 = var5 * 0.3F;
            this.head.y = 15.69F + var10;
            this.tail.xRot = 1.015F + Mth.cos(var3 * 0.6662F) * 0.3F * var4;
            this.tail.y = 21.07F + var10;
            this.body.y = 16.5F + var10;
            this.leftWing.zRot = -0.0873F - var5;
            this.leftWing.y = 16.94F + var10;
            this.rightWing.zRot = 0.0873F + var5;
            this.rightWing.y = 16.94F + var10;
            this.leftLeg.y = 22.0F + var10;
            this.rightLeg.y = 22.0F + var10;
      }
   }

   private void prepare(ParrotModel.State var1) {
      this.feather.xRot = -0.2214F;
      this.body.xRot = 0.4937F;
      this.leftWing.xRot = -0.6981F;
      this.leftWing.yRot = -3.1415927F;
      this.rightWing.xRot = -0.6981F;
      this.rightWing.yRot = -3.1415927F;
      this.leftLeg.xRot = -0.0299F;
      this.rightLeg.xRot = -0.0299F;
      this.leftLeg.y = 22.0F;
      this.rightLeg.y = 22.0F;
      this.leftLeg.zRot = 0.0F;
      this.rightLeg.zRot = 0.0F;
      switch(var1) {
         case SITTING:
            float var2 = 1.9F;
            this.head.y = 17.59F;
            this.tail.xRot = 1.5388988F;
            this.tail.y = 22.97F;
            this.body.y = 18.4F;
            this.leftWing.zRot = -0.0873F;
            this.leftWing.y = 18.84F;
            this.rightWing.zRot = 0.0873F;
            this.rightWing.y = 18.84F;
            ++this.leftLeg.y;
            ++this.rightLeg.y;
            ++this.leftLeg.xRot;
            ++this.rightLeg.xRot;
            break;
         case PARTY:
            this.leftLeg.zRot = -0.34906584F;
            this.rightLeg.zRot = 0.34906584F;
         case STANDING:
         case ON_SHOULDER:
         default:
            break;
         case FLYING:
            this.leftLeg.xRot += 0.6981317F;
            this.rightLeg.xRot += 0.6981317F;
      }
   }

   private static ParrotModel.State getState(Parrot var0) {
      if (var0.isPartyParrot()) {
         return ParrotModel.State.PARTY;
      } else if (var0.isInSittingPose()) {
         return ParrotModel.State.SITTING;
      } else {
         return var0.isFlying() ? ParrotModel.State.FLYING : ParrotModel.State.STANDING;
      }
   }

   public static enum State {
      FLYING,
      STANDING,
      SITTING,
      PARTY,
      ON_SHOULDER;

      private State() {
      }
   }
}
