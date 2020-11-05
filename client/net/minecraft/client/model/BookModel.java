package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public class BookModel extends Model {
   private final ModelPart leftLid = (new ModelPart(64, 32, 0, 0)).addBox(-6.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F);
   private final ModelPart rightLid = (new ModelPart(64, 32, 16, 0)).addBox(0.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F);
   private final ModelPart leftPages = (new ModelPart(64, 32, 0, 10)).addBox(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F);
   private final ModelPart rightPages = (new ModelPart(64, 32, 12, 10)).addBox(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F);
   private final ModelPart flipPage1 = (new ModelPart(64, 32, 24, 10)).addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
   private final ModelPart flipPage2 = (new ModelPart(64, 32, 24, 10)).addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
   private final ModelPart seam = (new ModelPart(64, 32, 12, 0)).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 10.0F, 0.005F);
   private final List<ModelPart> parts;

   public BookModel() {
      super(RenderType::entitySolid);
      this.parts = ImmutableList.of(this.leftLid, this.rightLid, this.seam, this.leftPages, this.rightPages, this.flipPage1, this.flipPage2);
      this.leftLid.setPos(0.0F, 0.0F, -1.0F);
      this.rightLid.setPos(0.0F, 0.0F, 1.0F);
      this.seam.yRot = 1.5707964F;
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      this.render(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void render(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      this.parts.forEach((var8x) -> {
         var8x.render(var1, var2, var3, var4, var5, var6, var7, var8);
      });
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
