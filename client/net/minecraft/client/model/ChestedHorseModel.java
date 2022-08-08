package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class ChestedHorseModel<T extends AbstractChestedHorse> extends HorseModel<T> {
   private final ModelPart leftChest;
   private final ModelPart rightChest;

   public ChestedHorseModel(ModelPart var1) {
      super(var1);
      this.leftChest = this.body.getChild("left_chest");
      this.rightChest = this.body.getChild("right_chest");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = HorseModel.createBodyMesh(CubeDeformation.NONE);
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.getChild("body");
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(26, 21).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
      var2.addOrReplaceChild("left_chest", var3, PartPose.offsetAndRotation(6.0F, -8.0F, 0.0F, 0.0F, -1.5707964F, 0.0F));
      var2.addOrReplaceChild("right_chest", var3, PartPose.offsetAndRotation(-6.0F, -8.0F, 0.0F, 0.0F, 1.5707964F, 0.0F));
      PartDefinition var4 = var1.getChild("head_parts").getChild("head");
      CubeListBuilder var5 = CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
      var4.addOrReplaceChild("left_ear", var5, PartPose.offsetAndRotation(1.25F, -10.0F, 4.0F, 0.2617994F, 0.0F, 0.2617994F));
      var4.addOrReplaceChild("right_ear", var5, PartPose.offsetAndRotation(-1.25F, -10.0F, 4.0F, 0.2617994F, 0.0F, -0.2617994F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim((AbstractHorse)var1, var2, var3, var4, var5, var6);
      if (var1.hasChest()) {
         this.leftChest.visible = true;
         this.rightChest.visible = true;
      } else {
         this.leftChest.visible = false;
         this.rightChest.visible = false;
      }

   }
}
