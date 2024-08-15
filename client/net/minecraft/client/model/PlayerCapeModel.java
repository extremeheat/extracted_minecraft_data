package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.joml.Quaternionf;

public class PlayerCapeModel<T extends PlayerRenderState> extends HumanoidModel<T> {
   private static final String CAPE = "cape";
   private final ModelPart cape = this.body.getChild("cape");

   public PlayerCapeModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createCapeLayer() {
      MeshDefinition var0 = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.clearChild("head");
      var2.clearChild("hat");
      PartDefinition var3 = var1.clearChild("body");
      var1.clearChild("left_arm");
      var1.clearChild("right_arm");
      var1.clearChild("left_leg");
      var1.clearChild("right_leg");
      var3.addOrReplaceChild(
         "cape",
         CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 16.0F, 1.0F, CubeDeformation.NONE, 1.0F, 0.5F),
         PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, 3.1415927F, 0.0F)
      );
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(T var1) {
      super.setupAnim((T)var1);
      this.cape.resetPose();
      if (!var1.chestItem.isEmpty()) {
         this.cape.z++;
         this.cape.y -= 0.85F;
      }

      this.cape
         .rotateBy(
            new Quaternionf()
               .rotationX((6.0F + var1.capeLean / 2.0F + var1.capeFlap) * 0.017453292F)
               .rotateZ(var1.capeLean2 / 2.0F * 0.017453292F)
               .rotateY(-var1.capeLean2 / 2.0F * 0.017453292F)
         );
   }
}
