package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class BookModel extends Model {
   private final ModelPart leftLid = (new ModelPart(this)).texOffs(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);
   private final ModelPart rightLid = (new ModelPart(this)).texOffs(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);
   private final ModelPart leftPages = (new ModelPart(this)).texOffs(0, 10).addBox(0.0F, -4.0F, -0.99F, 5, 8, 1);
   private final ModelPart rightPages = (new ModelPart(this)).texOffs(12, 10).addBox(0.0F, -4.0F, -0.01F, 5, 8, 1);
   private final ModelPart flipPage1 = (new ModelPart(this)).texOffs(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
   private final ModelPart flipPage2 = (new ModelPart(this)).texOffs(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
   private final ModelPart seam = (new ModelPart(this)).texOffs(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);

   public BookModel() {
      super();
      this.leftLid.setPos(0.0F, 0.0F, -1.0F);
      this.rightLid.setPos(0.0F, 0.0F, 1.0F);
      this.seam.yRot = 1.5707964F;
   }

   public void render(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.setupAnim(var1, var2, var3, var4, var5, var6);
      this.leftLid.render(var6);
      this.rightLid.render(var6);
      this.seam.render(var6);
      this.leftPages.render(var6);
      this.rightPages.render(var6);
      this.flipPage1.render(var6);
      this.flipPage2.render(var6);
   }

   private void setupAnim(float var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = (Mth.sin(var1 * 0.02F) * 0.1F + 1.25F) * var4;
      this.leftLid.yRot = 3.1415927F + var7;
      this.rightLid.yRot = -var7;
      this.leftPages.yRot = var7;
      this.rightPages.yRot = -var7;
      this.flipPage1.yRot = var7 - var7 * 2.0F * var2;
      this.flipPage2.yRot = var7 - var7 * 2.0F * var3;
      this.leftPages.x = Mth.sin(var7);
      this.rightPages.x = Mth.sin(var7);
      this.flipPage1.x = Mth.sin(var7);
      this.flipPage2.x = Mth.sin(var7);
   }
}
