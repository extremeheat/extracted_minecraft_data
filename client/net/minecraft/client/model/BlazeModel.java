package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class BlazeModel extends EntityModel<LivingEntityRenderState> {
   private final ModelPart root;
   private final ModelPart[] upperBodyParts;
   private final ModelPart head;

   public BlazeModel(ModelPart var1) {
      super();
      this.root = var1;
      this.head = var1.getChild("head");
      this.upperBodyParts = new ModelPart[12];
      Arrays.setAll(this.upperBodyParts, var1x -> var1.getChild(getPartName(var1x)));
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

      for (int var4 = 0; var4 < 4; var4++) {
         float var5 = Mth.cos(var2) * 9.0F;
         float var6 = -2.0F + Mth.cos((float)(var4 * 2) * 0.25F);
         float var7 = Mth.sin(var2) * 9.0F;
         var1.addOrReplaceChild(getPartName(var4), var3, PartPose.offset(var5, var6, var7));
         var2++;
      }

      var2 = 0.7853982F;

      for (int var10 = 4; var10 < 8; var10++) {
         float var12 = Mth.cos(var2) * 7.0F;
         float var14 = 2.0F + Mth.cos((float)(var10 * 2) * 0.25F);
         float var16 = Mth.sin(var2) * 7.0F;
         var1.addOrReplaceChild(getPartName(var10), var3, PartPose.offset(var12, var14, var16));
         var2++;
      }

      var2 = 0.47123894F;

      for (int var11 = 8; var11 < 12; var11++) {
         float var13 = Mth.cos(var2) * 5.0F;
         float var15 = 11.0F + Mth.cos((float)var11 * 1.5F * 0.5F);
         float var17 = Mth.sin(var2) * 5.0F;
         var1.addOrReplaceChild(getPartName(var11), var3, PartPose.offset(var13, var15, var17));
         var2++;
      }

      return LayerDefinition.create(var0, 64, 32);
   }

   @Override
   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(LivingEntityRenderState var1) {
      float var2 = var1.ageInTicks * 3.1415927F * -0.1F;

      for (int var3 = 0; var3 < 4; var3++) {
         this.upperBodyParts[var3].y = -2.0F + Mth.cos(((float)(var3 * 2) + var1.ageInTicks) * 0.25F);
         this.upperBodyParts[var3].x = Mth.cos(var2) * 9.0F;
         this.upperBodyParts[var3].z = Mth.sin(var2) * 9.0F;
         var2++;
      }

      var2 = 0.7853982F + var1.ageInTicks * 3.1415927F * 0.03F;

      for (int var6 = 4; var6 < 8; var6++) {
         this.upperBodyParts[var6].y = 2.0F + Mth.cos(((float)(var6 * 2) + var1.ageInTicks) * 0.25F);
         this.upperBodyParts[var6].x = Mth.cos(var2) * 7.0F;
         this.upperBodyParts[var6].z = Mth.sin(var2) * 7.0F;
         var2++;
      }

      var2 = 0.47123894F + var1.ageInTicks * 3.1415927F * -0.05F;

      for (int var7 = 8; var7 < 12; var7++) {
         this.upperBodyParts[var7].y = 11.0F + Mth.cos(((float)var7 * 1.5F + var1.ageInTicks) * 0.5F);
         this.upperBodyParts[var7].x = Mth.cos(var2) * 5.0F;
         this.upperBodyParts[var7].z = Mth.sin(var2) * 5.0F;
         var2++;
      }

      this.head.yRot = var1.yRot * 0.017453292F;
      this.head.xRot = var1.xRot * 0.017453292F;
   }
}
