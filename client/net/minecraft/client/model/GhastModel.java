package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

public class GhastModel<T extends Entity> extends HierarchicalModel<T> {
   private final ModelPart root;
   private final ModelPart[] tentacles = new ModelPart[9];

   public GhastModel(ModelPart var1) {
      super();
      this.root = var1;

      for(int var2 = 0; var2 < this.tentacles.length; ++var2) {
         this.tentacles[var2] = var1.getChild(createTentacleName(var2));
      }

   }

   private static String createTentacleName(int var0) {
      return "tentacle" + var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.offset(0.0F, 17.6F, 0.0F));
      RandomSource var2 = RandomSource.create(1660L);

      for(int var3 = 0; var3 < 9; ++var3) {
         float var4 = (((float)(var3 % 3) - (float)(var3 / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
         float var5 = ((float)(var3 / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
         int var6 = var2.nextInt(7) + 8;
         var1.addOrReplaceChild(createTentacleName(var3), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, (float)var6, 2.0F), PartPose.offset(var4, 24.6F, var5));
      }

      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      for(int var7 = 0; var7 < this.tentacles.length; ++var7) {
         this.tentacles[var7].xRot = 0.2F * Mth.sin(var4 * 0.3F + (float)var7) + 0.4F;
      }

   }

   public ModelPart root() {
      return this.root;
   }
}
