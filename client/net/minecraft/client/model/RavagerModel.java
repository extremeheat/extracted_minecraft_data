package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Ravager;

public class RavagerModel extends HierarchicalModel<Ravager> {
   private final ModelPart root;
   private final ModelPart head;
   private final ModelPart mouth;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart neck;

   public RavagerModel(ModelPart var1) {
      super();
      this.root = var1;
      this.neck = var1.getChild("neck");
      this.head = this.neck.getChild("head");
      this.mouth = this.head.getChild("mouth");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      boolean var2 = true;
      PartDefinition var3 = var1.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(68, 73).addBox(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F), PartPose.offset(0.0F, -7.0F, 5.5F));
      PartDefinition var4 = var3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F).texOffs(0, 0).addBox(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F), PartPose.offset(0.0F, 16.0F, -17.0F));
      var4.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(74, 55).addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F), PartPose.offsetAndRotation(-10.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F));
      var4.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(74, 55).mirror().addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F), PartPose.offsetAndRotation(8.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F));
      var4.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F), PartPose.offset(0.0F, -2.0F, 2.0F));
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 55).addBox(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F).texOffs(0, 91).addBox(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F), PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, 1.5707964F, 0.0F, 0.0F));
      var1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(-8.0F, -13.0F, 18.0F));
      var1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(96, 0).mirror().addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(8.0F, -13.0F, 18.0F));
      var1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(64, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(-8.0F, -13.0F, -5.0F));
      var1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(64, 0).mirror().addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(8.0F, -13.0F, -5.0F));
      return LayerDefinition.create(var0, 128, 128);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(Ravager var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      float var7 = 0.4F * var3;
      this.rightHindLeg.xRot = Mth.cos(var2 * 0.6662F) * var7;
      this.leftHindLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * var7;
      this.rightFrontLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * var7;
      this.leftFrontLeg.xRot = Mth.cos(var2 * 0.6662F) * var7;
   }

   public void prepareMobModel(Ravager var1, float var2, float var3, float var4) {
      super.prepareMobModel(var1, var2, var3, var4);
      int var5 = var1.getStunnedTick();
      int var6 = var1.getRoarTick();
      boolean var7 = true;
      int var8 = var1.getAttackTick();
      boolean var9 = true;
      float var10;
      float var11;
      float var13;
      if (var8 > 0) {
         var10 = Mth.triangleWave((float)var8 - var4, 10.0F);
         var11 = (1.0F + var10) * 0.5F;
         float var12 = var11 * var11 * var11 * 12.0F;
         var13 = var12 * Mth.sin(this.neck.xRot);
         this.neck.z = -6.5F + var12;
         this.neck.y = -7.0F - var13;
         float var14 = Mth.sin(((float)var8 - var4) / 10.0F * 3.1415927F * 0.25F);
         this.mouth.xRot = 1.5707964F * var14;
         if (var8 > 5) {
            this.mouth.xRot = Mth.sin(((float)(-4 + var8) - var4) / 4.0F) * 3.1415927F * 0.4F;
         } else {
            this.mouth.xRot = 0.15707964F * Mth.sin(3.1415927F * ((float)var8 - var4) / 10.0F);
         }
      } else {
         var10 = -1.0F;
         var11 = -1.0F * Mth.sin(this.neck.xRot);
         this.neck.x = 0.0F;
         this.neck.y = -7.0F - var11;
         this.neck.z = 5.5F;
         boolean var15 = var5 > 0;
         this.neck.xRot = var15 ? 0.21991149F : 0.0F;
         this.mouth.xRot = 3.1415927F * (var15 ? 0.05F : 0.01F);
         if (var15) {
            double var16 = (double)var5 / 40.0D;
            this.neck.x = (float)Math.sin(var16 * 10.0D) * 3.0F;
         } else if (var6 > 0) {
            var13 = Mth.sin(((float)(20 - var6) - var4) / 20.0F * 3.1415927F * 0.25F);
            this.mouth.xRot = 1.5707964F * var13;
         }
      }

   }
}
