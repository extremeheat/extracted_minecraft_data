package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Slime;

public class LavaSlimeModel<T extends Slime> extends HierarchicalModel<T> {
   private static final int SEGMENT_COUNT = 8;
   private final ModelPart root;
   private final ModelPart[] bodyCubes = new ModelPart[8];

   public LavaSlimeModel(ModelPart var1) {
      super();
      this.root = var1;
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
         int var4 = var2;
         if (var2 == 2) {
            var3 = 24;
            var4 = 10;
         } else if (var2 == 3) {
            var3 = 24;
            var4 = 19;
         }

         var1.addOrReplaceChild(getSegmentName(var2), CubeListBuilder.create().texOffs(var3, var4).addBox(-4.0F, (float)(16 + var2), -4.0F, 8.0F, 1.0F, 8.0F), PartPose.ZERO);
      }

      var1.addOrReplaceChild("inside_cube", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 18.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      float var5 = Mth.lerp(var4, var1.oSquish, var1.squish);
      if (var5 < 0.0F) {
         var5 = 0.0F;
      }

      for(int var6 = 0; var6 < this.bodyCubes.length; ++var6) {
         this.bodyCubes[var6].y = (float)(-(4 - var6)) * var5 * 1.7F;
      }

   }

   public ModelPart root() {
      return this.root;
   }
}
