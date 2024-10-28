package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public class BookModel extends Model {
   private static final String LEFT_PAGES = "left_pages";
   private static final String RIGHT_PAGES = "right_pages";
   private static final String FLIP_PAGE_1 = "flip_page1";
   private static final String FLIP_PAGE_2 = "flip_page2";
   private final ModelPart root;
   private final ModelPart leftLid;
   private final ModelPart rightLid;
   private final ModelPart leftPages;
   private final ModelPart rightPages;
   private final ModelPart flipPage1;
   private final ModelPart flipPage2;

   public BookModel(ModelPart var1) {
      super(RenderType::entitySolid);
      this.root = var1;
      this.leftLid = var1.getChild("left_lid");
      this.rightLid = var1.getChild("right_lid");
      this.leftPages = var1.getChild("left_pages");
      this.rightPages = var1.getChild("right_pages");
      this.flipPage1 = var1.getChild("flip_page1");
      this.flipPage2 = var1.getChild("flip_page2");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("left_lid", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F), PartPose.offset(0.0F, 0.0F, -1.0F));
      var1.addOrReplaceChild("right_lid", CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F), PartPose.offset(0.0F, 0.0F, 1.0F));
      var1.addOrReplaceChild("seam", CubeListBuilder.create().texOffs(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 10.0F, 0.005F), PartPose.rotation(0.0F, 1.5707964F, 0.0F));
      var1.addOrReplaceChild("left_pages", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F), PartPose.ZERO);
      var1.addOrReplaceChild("right_pages", CubeListBuilder.create().texOffs(12, 10).addBox(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F), PartPose.ZERO);
      CubeListBuilder var2 = CubeListBuilder.create().texOffs(24, 10).addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
      var1.addOrReplaceChild("flip_page1", var2, PartPose.ZERO);
      var1.addOrReplaceChild("flip_page2", var2, PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 32);
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
      this.render(var1, var2, var3, var4, var5);
   }

   public void render(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
      this.root.render(var1, var2, var3, var4, var5);
   }

   public void setupAnim(float var1, float var2, float var3, float var4) {
      float var5 = (Mth.sin(var1 * 0.02F) * 0.1F + 1.25F) * var4;
      this.leftLid.yRot = 3.1415927F + var5;
      this.rightLid.yRot = -var5;
      this.leftPages.yRot = var5;
      this.rightPages.yRot = -var5;
      this.flipPage1.yRot = var5 - var5 * 2.0F * var2;
      this.flipPage2.yRot = var5 - var5 * 2.0F * var3;
      this.leftPages.x = Mth.sin(var5);
      this.rightPages.x = Mth.sin(var5);
      this.flipPage1.x = Mth.sin(var5);
      this.flipPage2.x = Mth.sin(var5);
   }
}
