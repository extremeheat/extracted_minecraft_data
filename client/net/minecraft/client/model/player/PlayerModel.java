package net.minecraft.client.model.player;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.HumanoidArm;

public class PlayerModel extends HumanoidModel<AvatarRenderState> {
   protected static final String LEFT_SLEEVE = "left_sleeve";
   protected static final String RIGHT_SLEEVE = "right_sleeve";
   protected static final String LEFT_PANTS = "left_pants";
   protected static final String RIGHT_PANTS = "right_pants";
   private final List<ModelPart> bodyParts;
   public final ModelPart leftSleeve;
   public final ModelPart rightSleeve;
   public final ModelPart leftPants;
   public final ModelPart rightPants;
   public final ModelPart jacket;
   private final boolean slim;

   public PlayerModel(ModelPart var1, boolean var2) {
      super(var1, RenderTypes::entityTranslucent);
      this.slim = var2;
      this.leftSleeve = this.leftArm.getChild("left_sleeve");
      this.rightSleeve = this.rightArm.getChild("right_sleeve");
      this.leftPants = this.leftLeg.getChild("left_pants");
      this.rightPants = this.rightLeg.getChild("right_pants");
      this.jacket = this.body.getChild("jacket");
      this.bodyParts = List.of(this.head, this.body, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg);
   }

   public static MeshDefinition createMesh(CubeDeformation var0, boolean var1) {
      MeshDefinition var2 = HumanoidModel.createMesh(var0, 0.0F);
      PartDefinition var3 = var2.getRoot();
      float var4 = 0.25F;
      if (var1) {
         PartDefinition var5 = var3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var0), PartPose.offset(5.0F, 2.0F, 0.0F));
         PartDefinition var6 = var3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var0), PartPose.offset(-5.0F, 2.0F, 0.0F));
         var5.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO);
         var6.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO);
      } else {
         PartDefinition var8 = var3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0), PartPose.offset(5.0F, 2.0F, 0.0F));
         PartDefinition var10 = var3.getChild("right_arm");
         var8.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO);
         var10.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO);
      }

      PartDefinition var9 = var3.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0), PartPose.offset(1.9F, 12.0F, 0.0F));
      PartDefinition var11 = var3.getChild("right_leg");
      var9.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO);
      var11.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO);
      PartDefinition var7 = var3.getChild("body");
      var7.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO);
      return var2;
   }

   public static ArmorModelSet<MeshDefinition> createArmorMeshSet(CubeDeformation var0, CubeDeformation var1) {
      return HumanoidModel.createArmorMeshSet(var0, var1).<MeshDefinition>map((var0x) -> {
         PartDefinition var1 = var0x.getRoot();
         PartDefinition var2 = var1.getChild("left_arm");
         PartDefinition var3 = var1.getChild("right_arm");
         var2.addOrReplaceChild("left_sleeve", CubeListBuilder.create(), PartPose.ZERO);
         var3.addOrReplaceChild("right_sleeve", CubeListBuilder.create(), PartPose.ZERO);
         PartDefinition var4 = var1.getChild("left_leg");
         PartDefinition var5 = var1.getChild("right_leg");
         var4.addOrReplaceChild("left_pants", CubeListBuilder.create(), PartPose.ZERO);
         var5.addOrReplaceChild("right_pants", CubeListBuilder.create(), PartPose.ZERO);
         PartDefinition var6 = var1.getChild("body");
         var6.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.ZERO);
         return var0x;
      });
   }

   public void setupAnim(AvatarRenderState var1) {
      boolean var2 = !var1.isSpectator;
      this.body.visible = var2;
      this.rightArm.visible = var2;
      this.leftArm.visible = var2;
      this.rightLeg.visible = var2;
      this.leftLeg.visible = var2;
      this.hat.visible = var1.showHat;
      this.jacket.visible = var1.showJacket;
      this.leftPants.visible = var1.showLeftPants;
      this.rightPants.visible = var1.showRightPants;
      this.leftSleeve.visible = var1.showLeftSleeve;
      this.rightSleeve.visible = var1.showRightSleeve;
      super.setupAnim(var1);
   }

   public void setAllVisible(boolean var1) {
      super.setAllVisible(var1);
      this.leftSleeve.visible = var1;
      this.rightSleeve.visible = var1;
      this.leftPants.visible = var1;
      this.rightPants.visible = var1;
      this.jacket.visible = var1;
   }

   public void translateToHand(AvatarRenderState var1, HumanoidArm var2, PoseStack var3) {
      this.root().translateAndRotate(var3);
      ModelPart var4 = this.getArm(var2);
      if (this.slim) {
         float var5 = 0.5F * (float)(var2 == HumanoidArm.RIGHT ? 1 : -1);
         var4.x += var5;
         var4.translateAndRotate(var3);
         var4.x -= var5;
      } else {
         var4.translateAndRotate(var3);
      }

   }

   public ModelPart getRandomBodyPart(RandomSource var1) {
      return (ModelPart)Util.getRandom(this.bodyParts, var1);
   }
}
