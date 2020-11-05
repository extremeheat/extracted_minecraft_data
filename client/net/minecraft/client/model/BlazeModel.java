package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class BlazeModel<T extends Entity> extends HierarchicalModel<T> {
   private final ModelPart root;
   private final ModelPart[] upperBodyParts;
   private final ModelPart head;

   public BlazeModel(ModelPart var1) {
      super();
      this.root = var1;
      this.head = var1.getChild("head");
      this.upperBodyParts = new ModelPart[12];
      Arrays.setAll(this.upperBodyParts, (var1x) -> {
         return var1.getChild(getPartName(var1x));
      });
   }

   private static String getPartName(int var0) {
      return "part" + var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      float var2 = 0.0F;
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);

      int var4;
      float var5;
      float var6;
      float var7;
      for(var4 = 0; var4 < 4; ++var4) {
         var5 = Mth.cos(var2) * 9.0F;
         var6 = -2.0F + Mth.cos((float)(var4 * 2) * 0.25F);
         var7 = Mth.sin(var2) * 9.0F;
         var1.addOrReplaceChild(getPartName(var4), var3, PartPose.offset(var5, var6, var7));
         ++var2;
      }

      var2 = 0.7853982F;

      for(var4 = 4; var4 < 8; ++var4) {
         var5 = Mth.cos(var2) * 7.0F;
         var6 = 2.0F + Mth.cos((float)(var4 * 2) * 0.25F);
         var7 = Mth.sin(var2) * 7.0F;
         var1.addOrReplaceChild(getPartName(var4), var3, PartPose.offset(var5, var6, var7));
         ++var2;
      }

      var2 = 0.47123894F;

      for(var4 = 8; var4 < 12; ++var4) {
         var5 = Mth.cos(var2) * 5.0F;
         var6 = 11.0F + Mth.cos((float)var4 * 1.5F * 0.5F);
         var7 = Mth.sin(var2) * 5.0F;
         var1.addOrReplaceChild(getPartName(var4), var3, PartPose.offset(var5, var6, var7));
         ++var2;
      }

      return LayerDefinition.create(var0, 64, 32);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var4 * 3.1415927F * -0.1F;

      int var8;
      for(var8 = 0; var8 < 4; ++var8) {
         this.upperBodyParts[var8].y = -2.0F + Mth.cos(((float)(var8 * 2) + var4) * 0.25F);
         this.upperBodyParts[var8].x = Mth.cos(var7) * 9.0F;
         this.upperBodyParts[var8].z = Mth.sin(var7) * 9.0F;
         ++var7;
      }

      var7 = 0.7853982F + var4 * 3.1415927F * 0.03F;

      for(var8 = 4; var8 < 8; ++var8) {
         this.upperBodyParts[var8].y = 2.0F + Mth.cos(((float)(var8 * 2) + var4) * 0.25F);
         this.upperBodyParts[var8].x = Mth.cos(var7) * 7.0F;
         this.upperBodyParts[var8].z = Mth.sin(var7) * 7.0F;
         ++var7;
      }

      var7 = 0.47123894F + var4 * 3.1415927F * -0.05F;

      for(var8 = 8; var8 < 12; ++var8) {
         this.upperBodyParts[var8].y = 11.0F + Mth.cos(((float)var8 * 1.5F + var4) * 0.5F);
         this.upperBodyParts[var8].x = Mth.cos(var7) * 5.0F;
         this.upperBodyParts[var8].z = Mth.sin(var7) * 5.0F;
         ++var7;
      }

      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
   }
}
