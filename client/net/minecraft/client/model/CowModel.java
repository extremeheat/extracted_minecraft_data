package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class CowModel extends QuadrupedModel<LivingEntityRenderState> {
   public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(false, 10.0F, 4.0F, Set.of("head"));

   public CowModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      byte var2 = 12;
      var1.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F)
            .texOffs(22, 0)
            .addBox("right_horn", -5.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F)
            .texOffs(22, 0)
            .addBox("left_horn", 4.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F),
         PartPose.offset(0.0F, 4.0F, -8.0F)
      );
      var1.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(18, 4).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F).texOffs(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F),
         PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, 1.5707964F, 0.0F, 0.0F)
      );
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      var1.addOrReplaceChild("right_hind_leg", var3, PartPose.offset(-4.0F, 12.0F, 7.0F));
      var1.addOrReplaceChild("left_hind_leg", var3, PartPose.offset(4.0F, 12.0F, 7.0F));
      var1.addOrReplaceChild("right_front_leg", var3, PartPose.offset(-4.0F, 12.0F, -6.0F));
      var1.addOrReplaceChild("left_front_leg", var3, PartPose.offset(4.0F, 12.0F, -6.0F));
      return LayerDefinition.create(var0, 64, 32);
   }

   public ModelPart getHead() {
      return this.head;
   }
}
