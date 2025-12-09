package net.minecraft.client.model.animal.ghast;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.monster.ghast.GhastModel;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;

public class HappyGhastModel extends EntityModel<HappyGhastRenderState> {
   public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.2375F);
   private static final float BODY_SQUEEZE = 0.9375F;
   private final ModelPart[] tentacles = new ModelPart[9];
   private final ModelPart body;

   public HappyGhastModel(ModelPart var1) {
      super(var1);
      this.body = var1.getChild("body");

      for(int var2 = 0; var2 < this.tentacles.length; ++var2) {
         this.tentacles[var2] = this.body.getChild(PartNames.tentacle(var2));
      }

   }

   public static LayerDefinition createBodyLayer(boolean var0, CubeDeformation var1) {
      MeshDefinition var2 = new MeshDefinition();
      PartDefinition var3 = var2.getRoot();
      PartDefinition var4 = var3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, var1), PartPose.offset(0.0F, 16.0F, 0.0F));
      if (var0) {
         var4.addOrReplaceChild("inner_body", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, var1.extend(-0.5F)), PartPose.offset(0.0F, 8.0F, 0.0F));
      }

      var4.addOrReplaceChild(PartNames.tentacle(0), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, var1), PartPose.offset(-3.75F, 7.0F, -5.0F));
      var4.addOrReplaceChild(PartNames.tentacle(1), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, var1), PartPose.offset(1.25F, 7.0F, -5.0F));
      var4.addOrReplaceChild(PartNames.tentacle(2), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, var1), PartPose.offset(6.25F, 7.0F, -5.0F));
      var4.addOrReplaceChild(PartNames.tentacle(3), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, var1), PartPose.offset(-6.25F, 7.0F, 0.0F));
      var4.addOrReplaceChild(PartNames.tentacle(4), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, var1), PartPose.offset(-1.25F, 7.0F, 0.0F));
      var4.addOrReplaceChild(PartNames.tentacle(5), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, var1), PartPose.offset(3.75F, 7.0F, 0.0F));
      var4.addOrReplaceChild(PartNames.tentacle(6), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, var1), PartPose.offset(-3.75F, 7.0F, 5.0F));
      var4.addOrReplaceChild(PartNames.tentacle(7), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, var1), PartPose.offset(1.25F, 7.0F, 5.0F));
      var4.addOrReplaceChild(PartNames.tentacle(8), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, var1), PartPose.offset(6.25F, 7.0F, 5.0F));
      return LayerDefinition.create(var2, 64, 64).apply(MeshTransformer.scaling(4.0F));
   }

   public void setupAnim(HappyGhastRenderState var1) {
      super.setupAnim(var1);
      if (!var1.bodyItem.isEmpty()) {
         this.body.xScale = 0.9375F;
         this.body.yScale = 0.9375F;
         this.body.zScale = 0.9375F;
      }

      GhastModel.animateTentacles(var1, this.tentacles);
   }
}
