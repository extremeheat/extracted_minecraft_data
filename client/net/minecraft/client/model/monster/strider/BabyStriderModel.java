package net.minecraft.client.model.monster.strider;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class BabyStriderModel extends StriderModel {
   private static final String FRONT_BRISTLE = "bristle2";
   private static final String MIDDLE_BRISTLE = "bristle1";
   private static final String BACK_BRISTLE = "bristle0";
   private final ModelPart frontBristle;
   private final ModelPart middleBristle;
   private final ModelPart bottomBristle;

   public BabyStriderModel(final ModelPart root) {
      super(root);
      this.frontBristle = this.body.getChild("bristle2");
      this.middleBristle = this.body.getChild("bristle1");
      this.bottomBristle = this.body.getChild("bristle0");
   }

   protected void customAnimations(final float animationPos, final float animationSpeed, final float ageInTicks) {
      this.body.y = 17.25F;
      ModelPart var10000 = this.body;
      var10000.y -= 1.0F * Mth.cos((double)(animationPos * 1.5F)) * 2.0F * animationSpeed;
      this.leftLeg.y = 20.0F + 2.0F * Mth.sin((double)(animationPos * 1.5F * 0.5F + 3.1415927F)) * 2.0F * animationSpeed;
      this.rightLeg.y = 20.0F + 2.0F * Mth.sin((double)(animationPos * 1.5F * 0.5F)) * 2.0F * animationSpeed;
      float bristleFlow = Mth.cos((double)(animationPos * 1.5F + 3.1415927F)) * animationSpeed;
      this.animateBristle(ageInTicks, bristleFlow, this.frontBristle, this.middleBristle, this.bottomBristle, (modelPart, rotation) -> modelPart.xRot += rotation);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.75F, -4.0F, 7.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.75F, 0.0F));
      root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 20.0F, 0.0F));
      root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(8, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 20.0F, 0.0F));
      body.addOrReplaceChild("bristle0", CubeListBuilder.create().texOffs(0, 21).addBox(-3.5F, -2.5F, 0.0F, 7.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.25F, 2.0F));
      body.addOrReplaceChild("bristle1", CubeListBuilder.create().texOffs(0, 18).addBox(-3.5F, -2.5F, 0.0F, 7.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.25F, 0.0F));
      body.addOrReplaceChild("bristle2", CubeListBuilder.create().texOffs(0, 15).addBox(-3.5F, -2.5F, 0.0F, 7.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.25F, -2.0F));
      return LayerDefinition.create(mesh, 32, 32);
   }
}
