package net.minecraft.client.model.monster.nautilus;

import net.minecraft.client.model.animal.nautilus.NautilusModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.NautilusRenderState;

public class ZombieNautilusCoralModel extends NautilusModel {
   private final ModelPart corals;

   public ZombieNautilusCoralModel(ModelPart var1) {
      super(var1);
      ModelPart var2 = this.nautilus.getChild("shell");
      this.corals = var2.getChild("corals");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = createBodyMesh();
      PartDefinition var1 = var0.getRoot().getChild("root").getChild("shell").addOrReplaceChild("corals", CubeListBuilder.create(), PartPose.offset(8.0F, 4.5F, -8.0F));
      PartDefinition var2 = var1.addOrReplaceChild("yellow_coral", CubeListBuilder.create(), PartPose.offset(0.0F, -11.0F, 11.0F));
      var2.addOrReplaceChild("yellow_coral_second", CubeListBuilder.create().texOffs(0, 85).addBox(-4.5F, -3.5F, 0.0F, 6.0F, 8.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, -0.7854F, 0.0F));
      var2.addOrReplaceChild("yellow_coral_first", CubeListBuilder.create().texOffs(0, 85).addBox(-4.5F, -3.5F, 0.0F, 6.0F, 8.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
      PartDefinition var3 = var1.addOrReplaceChild("pink_coral", CubeListBuilder.create().texOffs(-8, 94).addBox(-4.5F, 4.5F, 0.0F, 6.0F, 0.0F, 8.0F), PartPose.offset(-12.5F, -18.0F, 11.0F));
      var3.addOrReplaceChild("pink_coral_second", CubeListBuilder.create().texOffs(-8, 94).addBox(-3.0F, 0.0F, -4.0F, 6.0F, 0.0F, 8.0F), PartPose.offsetAndRotation(-1.5F, 4.5F, 4.0F, 0.0F, 0.0F, 1.5708F));
      PartDefinition var4 = var1.addOrReplaceChild("blue_coral", CubeListBuilder.create(), PartPose.offset(-14.0F, 0.0F, 5.5F));
      var4.addOrReplaceChild("blue_second", CubeListBuilder.create().texOffs(0, 102).addBox(-3.5F, -5.5F, 0.0F, 5.0F, 10.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.0F, 0.7854F, 0.0F));
      var4.addOrReplaceChild("blue_first", CubeListBuilder.create().texOffs(0, 102).addBox(-3.5F, -5.5F, 0.0F, 5.0F, 10.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
      PartDefinition var5 = var1.addOrReplaceChild("red_coral", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      var5.addOrReplaceChild("red_coral_second", CubeListBuilder.create().texOffs(0, 112).addBox(-2.5F, -5.5F, 0.0F, 4.0F, 10.0F, 0.0F), PartPose.offsetAndRotation(-0.5F, -1.0F, 1.5F, 0.0F, -0.829F, 0.0F));
      var5.addOrReplaceChild("red_coral_first", CubeListBuilder.create().texOffs(0, 112).addBox(-4.5F, -5.5F, 0.0F, 6.0F, 10.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
      return LayerDefinition.create(var0, 128, 128);
   }

   public void setupAnim(NautilusRenderState var1) {
      super.setupAnim(var1);
      this.corals.visible = var1.bodyArmorItem.isEmpty();
   }
}
