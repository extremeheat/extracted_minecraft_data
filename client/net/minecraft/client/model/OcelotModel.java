package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class OcelotModel<T extends Entity> extends AgeableListModel<T> {
   private static final int CROUCH_STATE = 0;
   private static final int WALK_STATE = 1;
   private static final int SPRINT_STATE = 2;
   protected static final int SITTING_STATE = 3;
   private static final float XO = 0.0F;
   private static final float YO = 16.0F;
   private static final float ZO = -9.0F;
   private static final float HEAD_WALK_Y = 15.0F;
   private static final float HEAD_WALK_Z = -9.0F;
   private static final float BODY_WALK_Y = 12.0F;
   private static final float BODY_WALK_Z = -10.0F;
   private static final float TAIL_1_WALK_Y = 15.0F;
   private static final float TAIL_1_WALK_Z = 8.0F;
   private static final float TAIL_2_WALK_Y = 20.0F;
   private static final float TAIL_2_WALK_Z = 14.0F;
   protected static final float BACK_LEG_Y = 18.0F;
   protected static final float BACK_LEG_Z = 5.0F;
   protected static final float FRONT_LEG_Y = 14.1F;
   private static final float FRONT_LEG_Z = -5.0F;
   private static final String TAIL_1 = "tail1";
   private static final String TAIL_2 = "tail2";
   protected final ModelPart leftHindLeg;
   protected final ModelPart rightHindLeg;
   protected final ModelPart leftFrontLeg;
   protected final ModelPart rightFrontLeg;
   protected final ModelPart tail1;
   protected final ModelPart tail2;
   protected final ModelPart head;
   protected final ModelPart body;
   protected int state = 1;

   public OcelotModel(ModelPart var1) {
      super(true, 10.0F, 4.0F);
      this.head = var1.getChild("head");
      this.body = var1.getChild("body");
      this.tail1 = var1.getChild("tail1");
      this.tail2 = var1.getChild("tail2");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
   }

   public static MeshDefinition createBodyMesh(CubeDeformation var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      CubeDeformation var3 = new CubeDeformation(-0.02F);
      var2.addOrReplaceChild("head", CubeListBuilder.create().addBox("main", -2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F, var0).addBox("nose", -1.5F, -0.001F, -4.0F, 3, 2, 2, var0, 0, 24).addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, var0, 0, 10).addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, var0, 6, 10), PartPose.offset(0.0F, 15.0F, -9.0F));
      var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(20, 0).addBox(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, var0), PartPose.offsetAndRotation(0.0F, 12.0F, -10.0F, 1.5707964F, 0.0F, 0.0F));
      var2.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, var0), PartPose.offsetAndRotation(0.0F, 15.0F, 8.0F, 0.9F, 0.0F, 0.0F));
      var2.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(4, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, var3), PartPose.offset(0.0F, 20.0F, 14.0F));
      CubeListBuilder var4 = CubeListBuilder.create().texOffs(8, 13).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, var0);
      var2.addOrReplaceChild("left_hind_leg", var4, PartPose.offset(1.1F, 18.0F, 5.0F));
      var2.addOrReplaceChild("right_hind_leg", var4, PartPose.offset(-1.1F, 18.0F, 5.0F));
      CubeListBuilder var5 = CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, var0);
      var2.addOrReplaceChild("left_front_leg", var5, PartPose.offset(1.2F, 14.1F, -5.0F));
      var2.addOrReplaceChild("right_front_leg", var5, PartPose.offset(-1.2F, 14.1F, -5.0F));
      return var1;
   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body, this.leftHindLeg, this.rightHindLeg, this.leftFrontLeg, this.rightFrontLeg, this.tail1, this.tail2);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      if (this.state != 3) {
         this.body.xRot = 1.5707964F;
         if (this.state == 2) {
            this.leftHindLeg.xRot = Mth.cos(var2 * 0.6662F) * var3;
            this.rightHindLeg.xRot = Mth.cos(var2 * 0.6662F + 0.3F) * var3;
            this.leftFrontLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F + 0.3F) * var3;
            this.rightFrontLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * var3;
            this.tail2.xRot = 1.7278761F + 0.31415927F * Mth.cos(var2) * var3;
         } else {
            this.leftHindLeg.xRot = Mth.cos(var2 * 0.6662F) * var3;
            this.rightHindLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * var3;
            this.leftFrontLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * var3;
            this.rightFrontLeg.xRot = Mth.cos(var2 * 0.6662F) * var3;
            if (this.state == 1) {
               this.tail2.xRot = 1.7278761F + 0.7853982F * Mth.cos(var2) * var3;
            } else {
               this.tail2.xRot = 1.7278761F + 0.47123894F * Mth.cos(var2) * var3;
            }
         }
      }

   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      this.body.y = 12.0F;
      this.body.z = -10.0F;
      this.head.y = 15.0F;
      this.head.z = -9.0F;
      this.tail1.y = 15.0F;
      this.tail1.z = 8.0F;
      this.tail2.y = 20.0F;
      this.tail2.z = 14.0F;
      this.leftFrontLeg.y = 14.1F;
      this.leftFrontLeg.z = -5.0F;
      this.rightFrontLeg.y = 14.1F;
      this.rightFrontLeg.z = -5.0F;
      this.leftHindLeg.y = 18.0F;
      this.leftHindLeg.z = 5.0F;
      this.rightHindLeg.y = 18.0F;
      this.rightHindLeg.z = 5.0F;
      this.tail1.xRot = 0.9F;
      ModelPart var10000;
      if (var1.isCrouching()) {
         ++this.body.y;
         var10000 = this.head;
         var10000.y += 2.0F;
         ++this.tail1.y;
         var10000 = this.tail2;
         var10000.y += -4.0F;
         var10000 = this.tail2;
         var10000.z += 2.0F;
         this.tail1.xRot = 1.5707964F;
         this.tail2.xRot = 1.5707964F;
         this.state = 0;
      } else if (var1.isSprinting()) {
         this.tail2.y = this.tail1.y;
         var10000 = this.tail2;
         var10000.z += 2.0F;
         this.tail1.xRot = 1.5707964F;
         this.tail2.xRot = 1.5707964F;
         this.state = 2;
      } else {
         this.state = 1;
      }

   }
}
