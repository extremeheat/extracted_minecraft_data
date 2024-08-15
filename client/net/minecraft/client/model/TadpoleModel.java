package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class TadpoleModel extends EntityModel<LivingEntityRenderState> {
   private final ModelPart root;
   private final ModelPart tail;

   public TadpoleModel(ModelPart var1) {
      super(RenderType::entityCutoutNoCull);
      this.root = var1;
      this.tail = var1.getChild("tail");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      float var2 = 0.0F;
      float var3 = 22.0F;
      float var4 = -3.0F;
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 3.0F), PartPose.offset(0.0F, 22.0F, -3.0F));
      var1.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 7.0F), PartPose.offset(0.0F, 22.0F, 0.0F));
      return LayerDefinition.create(var0, 16, 16);
   }

   @Override
   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(LivingEntityRenderState var1) {
      float var2 = var1.isInWater ? 1.0F : 1.5F;
      this.tail.yRot = -var2 * 0.25F * Mth.sin(0.3F * var1.ageInTicks);
   }
}
