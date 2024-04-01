package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.animation.definitions.BatAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ambient.Batato;

public class BatatoModel extends HierarchicalModel<Batato> {
   private final ModelPart root;
   private final ModelPart body;
   private final ModelPart rightWing;
   private final ModelPart leftWing;
   private final ModelPart rightWingTip;
   private final ModelPart leftWingTip;
   private final ModelPart feet;

   public BatatoModel(ModelPart var1) {
      super(RenderType::entityCutout);
      this.root = var1;
      this.body = var1.getChild("body");
      this.rightWing = this.body.getChild("right_wing");
      this.rightWingTip = this.rightWing.getChild("right_wing_tip");
      this.leftWing = this.body.getChild("left_wing");
      this.leftWingTip = this.leftWing.getChild("left_wing_tip");
      this.feet = this.body.getChild("feet");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(19, 20)
            .addBox(-1.5F, -2.0F, 0.0F, 13.0F, 12.0F, 0.01F, Set.of(Direction.NORTH))
            .texOffs(6, 20)
            .mirror()
            .addBox(-1.5F, -2.0F, 0.0F, 13.0F, 12.0F, 0.01F, Set.of(Direction.SOUTH)),
         PartPose.offset(0.0F, 17.0F, 0.0F)
      );
      PartDefinition var3 = var2.addOrReplaceChild(
         "right_wing", CubeListBuilder.create().texOffs(12, 0).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F), PartPose.offset(-1.5F, 0.0F, 0.0F)
      );
      var3.addOrReplaceChild(
         "right_wing_tip", CubeListBuilder.create().texOffs(16, 0).addBox(-6.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F), PartPose.offset(-2.0F, 0.0F, 0.0F)
      );
      PartDefinition var4 = var2.addOrReplaceChild(
         "left_wing", CubeListBuilder.create().texOffs(12, 7).addBox(0.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F), PartPose.offset(11.5F, 2.0F, 0.0F)
      );
      var4.addOrReplaceChild(
         "left_wing_tip", CubeListBuilder.create().texOffs(16, 8).addBox(0.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F), PartPose.offset(2.0F, 0.0F, 0.0F)
      );
      var2.addOrReplaceChild("feet", CubeListBuilder.create().texOffs(16, 16).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F), PartPose.offset(3.0F, 10.0F, 0.0F));
      return LayerDefinition.create(var0, 32, 32);
   }

   @Override
   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(Batato var1, float var2, float var3, float var4, float var5, float var6) {
      this.root().getAllParts().forEach(ModelPart::resetPose);
      this.animate(var1.flyAnimationState, BatAnimation.BAT_FLYING, var4, 1.0F);
      this.animate(var1.restAnimationState, BatAnimation.BAT_RESTING, var4, 1.0F);
   }
}
