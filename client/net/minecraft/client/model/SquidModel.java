package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;

public class SquidModel<T extends Entity> extends HierarchicalModel<T> {
   private final ModelPart[] tentacles = new ModelPart[8];
   private final ModelPart root;

   public SquidModel(ModelPart var1) {
      super();
      this.root = var1;
      Arrays.setAll(this.tentacles, (var1x) -> {
         return var1.getChild(createTentacleName(var1x));
      });
   }

   private static String createTentacleName(int var0) {
      return "tentacle" + var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      boolean var2 = true;
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
      boolean var3 = true;
      CubeListBuilder var4 = CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F);

      for(int var5 = 0; var5 < 8; ++var5) {
         double var6 = (double)var5 * 3.141592653589793D * 2.0D / 8.0D;
         float var8 = (float)Math.cos(var6) * 5.0F;
         float var9 = 15.0F;
         float var10 = (float)Math.sin(var6) * 5.0F;
         var6 = (double)var5 * 3.141592653589793D * -2.0D / 8.0D + 1.5707963267948966D;
         float var11 = (float)var6;
         var1.addOrReplaceChild(createTentacleName(var5), var4, PartPose.offsetAndRotation(var8, 15.0F, var10, 0.0F, var11, 0.0F));
      }

      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      ModelPart[] var7 = this.tentacles;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         ModelPart var10 = var7[var9];
         var10.xRot = var4;
      }

   }

   public ModelPart root() {
      return this.root;
   }
}
