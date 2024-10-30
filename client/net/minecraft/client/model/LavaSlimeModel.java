package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;

public class LavaSlimeModel extends EntityModel<SlimeRenderState> {
   private static final int SEGMENT_COUNT = 8;
   private final ModelPart[] bodyCubes = new ModelPart[8];

   public LavaSlimeModel(ModelPart var1) {
      super(var1);
      Arrays.setAll(this.bodyCubes, (var1x) -> {
         return var1.getChild(getSegmentName(var1x));
      });
   }

   private static String getSegmentName(int var0) {
      return "cube" + var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();

      for(int var2 = 0; var2 < 8; ++var2) {
         byte var3 = 0;
         int var4 = 0;
         if (var2 > 0 && var2 < 4) {
            var4 += 9 * var2;
         } else if (var2 > 3) {
            var3 = 32;
            var4 += 9 * var2 - 36;
         }

         var1.addOrReplaceChild(getSegmentName(var2), CubeListBuilder.create().texOffs(var3, var4).addBox(-4.0F, (float)(16 + var2), -4.0F, 8.0F, 1.0F, 8.0F), PartPose.ZERO);
      }

      var1.addOrReplaceChild("inside_cube", CubeListBuilder.create().texOffs(24, 40).addBox(-2.0F, 18.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(SlimeRenderState var1) {
      super.setupAnim(var1);
      float var2 = Math.max(0.0F, var1.squish);

      for(int var3 = 0; var3 < this.bodyCubes.length; ++var3) {
         this.bodyCubes[var3].y = (float)(-(4 - var3)) * var2 * 1.7F;
      }

   }
}
