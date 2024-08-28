package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.ShulkerRenderState;
import net.minecraft.util.Mth;

public class ShulkerModel extends EntityModel<ShulkerRenderState> {
   public static final String LID = "lid";
   private static final String BASE = "base";
   private final ModelPart lid;
   private final ModelPart head;

   public ShulkerModel(ModelPart var1) {
      super(var1, RenderType::entityCutoutNoCullZOffset);
      this.lid = var1.getChild("lid");
      this.head = var1.getChild("head");
   }

   private static MeshDefinition createShellMesh() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild(
         "lid", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 12.0F, 16.0F), PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      var1.addOrReplaceChild(
         "base", CubeListBuilder.create().texOffs(0, 28).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F), PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      return var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = createShellMesh();
      var0.getRoot()
         .addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 52).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.offset(0.0F, 12.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public static LayerDefinition createBoxLayer() {
      MeshDefinition var0 = createShellMesh();
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(ShulkerRenderState var1) {
      super.setupAnim(var1);
      float var2 = (0.5F + var1.peekAmount) * 3.1415927F;
      float var3 = -1.0F + Mth.sin(var2);
      float var4 = 0.0F;
      if (var2 > 3.1415927F) {
         var4 = Mth.sin(var1.ageInTicks * 0.1F) * 0.7F;
      }

      this.lid.setPos(0.0F, 16.0F + Mth.sin(var2) * 8.0F + var4, 0.0F);
      if (var1.peekAmount > 0.3F) {
         this.lid.yRot = var3 * var3 * var3 * var3 * 3.1415927F * 0.125F;
      } else {
         this.lid.yRot = 0.0F;
      }

      this.head.xRot = var1.xRot * 0.017453292F;
      this.head.yRot = (var1.yHeadRot - 180.0F - var1.yBodyRot) * 0.017453292F;
   }
}
