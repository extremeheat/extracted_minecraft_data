package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
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
      Arrays.setAll(this.tentacles, var1x -> var1.getChild(createTentacleName(var1x)));
   }

   private static String createTentacleName(int var0) {
      return "tentacle" + var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      CubeDeformation var2 = new CubeDeformation(0.02F);
      boolean var3 = true;
      var1.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F, var2), PartPose.offset(0.0F, 8.0F, 0.0F)
      );
      boolean var4 = true;
      CubeListBuilder var5 = CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F);

      for(int var6 = 0; var6 < 8; ++var6) {
         double var7 = (double)var6 * 3.141592653589793 * 2.0 / 8.0;
         float var9 = (float)Math.cos(var7) * 5.0F;
         float var10 = 15.0F;
         float var11 = (float)Math.sin(var7) * 5.0F;
         var7 = (double)var6 * 3.141592653589793 * -2.0 / 8.0 + 1.5707963267948966;
         float var12 = (float)var7;
         var1.addOrReplaceChild(createTentacleName(var6), var5, PartPose.offsetAndRotation(var9, 15.0F, var11, 0.0F, var12, 0.0F));
      }

      return LayerDefinition.create(var0, 64, 32);
   }

   @Override
   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      for(ModelPart var10 : this.tentacles) {
         var10.xRot = var4;
      }
   }

   @Override
   public ModelPart root() {
      return this.root;
   }
}
