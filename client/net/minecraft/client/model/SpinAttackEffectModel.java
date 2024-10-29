package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.util.Mth;

public class SpinAttackEffectModel extends EntityModel<PlayerRenderState> {
   private static final int BOX_COUNT = 2;
   private final ModelPart[] boxes = new ModelPart[2];

   public SpinAttackEffectModel(ModelPart var1) {
      super(var1);

      for(int var2 = 0; var2 < 2; ++var2) {
         this.boxes[var2] = var1.getChild(boxName(var2));
      }

   }

   private static String boxName(int var0) {
      return "box" + var0;
   }

   public static LayerDefinition createLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();

      for(int var2 = 0; var2 < 2; ++var2) {
         float var3 = -3.2F + 9.6F * (float)(var2 + 1);
         float var4 = 0.75F * (float)(var2 + 1);
         var1.addOrReplaceChild(boxName(var2), CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F + var3, -8.0F, 16.0F, 32.0F, 16.0F), PartPose.ZERO.withScale(var4));
      }

      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(PlayerRenderState var1) {
      super.setupAnim(var1);

      for(int var2 = 0; var2 < this.boxes.length; ++var2) {
         float var3 = var1.ageInTicks * (float)(-(45 + (var2 + 1) * 5));
         this.boxes[var2].yRot = Mth.wrapDegrees(var3) * 0.017453292F;
      }

   }
}
