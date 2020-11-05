package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Strider;

public class StriderModel<T extends Strider> extends ListModel<T> {
   private final ModelPart rightLeg;
   private final ModelPart leftLeg;
   private final ModelPart body;
   private final ModelPart bristle0;
   private final ModelPart bristle1;
   private final ModelPart bristle2;
   private final ModelPart bristle3;
   private final ModelPart bristle4;
   private final ModelPart bristle5;

   public StriderModel() {
      super();
      this.texWidth = 64;
      this.texHeight = 128;
      this.rightLeg = new ModelPart(this, 0, 32);
      this.rightLeg.setPos(-4.0F, 8.0F, 0.0F);
      this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F, 0.0F);
      this.leftLeg = new ModelPart(this, 0, 55);
      this.leftLeg.setPos(4.0F, 8.0F, 0.0F);
      this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F, 0.0F);
      this.body = new ModelPart(this, 0, 0);
      this.body.setPos(0.0F, 1.0F, 0.0F);
      this.body.addBox(-8.0F, -6.0F, -8.0F, 16.0F, 14.0F, 16.0F, 0.0F);
      this.bristle0 = new ModelPart(this, 16, 65);
      this.bristle0.setPos(-8.0F, 4.0F, -8.0F);
      this.bristle0.addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, 0.0F, true);
      this.setRotationAngle(this.bristle0, 0.0F, 0.0F, -1.2217305F);
      this.bristle1 = new ModelPart(this, 16, 49);
      this.bristle1.setPos(-8.0F, -1.0F, -8.0F);
      this.bristle1.addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, 0.0F, true);
      this.setRotationAngle(this.bristle1, 0.0F, 0.0F, -1.134464F);
      this.bristle2 = new ModelPart(this, 16, 33);
      this.bristle2.setPos(-8.0F, -5.0F, -8.0F);
      this.bristle2.addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, 0.0F, true);
      this.setRotationAngle(this.bristle2, 0.0F, 0.0F, -0.87266463F);
      this.bristle3 = new ModelPart(this, 16, 33);
      this.bristle3.setPos(8.0F, -6.0F, -8.0F);
      this.bristle3.addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, 0.0F);
      this.setRotationAngle(this.bristle3, 0.0F, 0.0F, 0.87266463F);
      this.bristle4 = new ModelPart(this, 16, 49);
      this.bristle4.setPos(8.0F, -2.0F, -8.0F);
      this.bristle4.addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, 0.0F);
      this.setRotationAngle(this.bristle4, 0.0F, 0.0F, 1.134464F);
      this.bristle5 = new ModelPart(this, 16, 65);
      this.bristle5.setPos(8.0F, 3.0F, -8.0F);
      this.bristle5.addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, 0.0F);
      this.setRotationAngle(this.bristle5, 0.0F, 0.0F, 1.2217305F);
      this.body.addChild(this.bristle0);
      this.body.addChild(this.bristle1);
      this.body.addChild(this.bristle2);
      this.body.addChild(this.bristle3);
      this.body.addChild(this.bristle4);
      this.body.addChild(this.bristle5);
   }

   public void setupAnim(Strider var1, float var2, float var3, float var4, float var5, float var6) {
      var3 = Math.min(0.25F, var3);
      if (var1.getPassengers().size() <= 0) {
         this.body.xRot = var6 * 0.017453292F;
         this.body.yRot = var5 * 0.017453292F;
      } else {
         this.body.xRot = 0.0F;
         this.body.yRot = 0.0F;
      }

      float var7 = 1.5F;
      this.body.zRot = 0.1F * Mth.sin(var2 * 1.5F) * 4.0F * var3;
      this.body.y = 2.0F;
      ModelPart var10000 = this.body;
      var10000.y -= 2.0F * Mth.cos(var2 * 1.5F) * 2.0F * var3;
      this.leftLeg.xRot = Mth.sin(var2 * 1.5F * 0.5F) * 2.0F * var3;
      this.rightLeg.xRot = Mth.sin(var2 * 1.5F * 0.5F + 3.1415927F) * 2.0F * var3;
      this.leftLeg.zRot = 0.17453292F * Mth.cos(var2 * 1.5F * 0.5F) * var3;
      this.rightLeg.zRot = 0.17453292F * Mth.cos(var2 * 1.5F * 0.5F + 3.1415927F) * var3;
      this.leftLeg.y = 8.0F + 2.0F * Mth.sin(var2 * 1.5F * 0.5F + 3.1415927F) * 2.0F * var3;
      this.rightLeg.y = 8.0F + 2.0F * Mth.sin(var2 * 1.5F * 0.5F) * 2.0F * var3;
      this.bristle0.zRot = -1.2217305F;
      this.bristle1.zRot = -1.134464F;
      this.bristle2.zRot = -0.87266463F;
      this.bristle3.zRot = 0.87266463F;
      this.bristle4.zRot = 1.134464F;
      this.bristle5.zRot = 1.2217305F;
      float var8 = Mth.cos(var2 * 1.5F + 3.1415927F) * var3;
      var10000 = this.bristle0;
      var10000.zRot += var8 * 1.3F;
      var10000 = this.bristle1;
      var10000.zRot += var8 * 1.2F;
      var10000 = this.bristle2;
      var10000.zRot += var8 * 0.6F;
      var10000 = this.bristle3;
      var10000.zRot += var8 * 0.6F;
      var10000 = this.bristle4;
      var10000.zRot += var8 * 1.2F;
      var10000 = this.bristle5;
      var10000.zRot += var8 * 1.3F;
      float var9 = 1.0F;
      float var10 = 1.0F;
      var10000 = this.bristle0;
      var10000.zRot += 0.05F * Mth.sin(var4 * 1.0F * -0.4F);
      var10000 = this.bristle1;
      var10000.zRot += 0.1F * Mth.sin(var4 * 1.0F * 0.2F);
      var10000 = this.bristle2;
      var10000.zRot += 0.1F * Mth.sin(var4 * 1.0F * 0.4F);
      var10000 = this.bristle3;
      var10000.zRot += 0.1F * Mth.sin(var4 * 1.0F * 0.4F);
      var10000 = this.bristle4;
      var10000.zRot += 0.1F * Mth.sin(var4 * 1.0F * 0.2F);
      var10000 = this.bristle5;
      var10000.zRot += 0.05F * Mth.sin(var4 * 1.0F * -0.4F);
   }

   public void setRotationAngle(ModelPart var1, float var2, float var3, float var4) {
      var1.xRot = var2;
      var1.yRot = var3;
      var1.zRot = var4;
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.body, this.leftLeg, this.rightLeg);
   }
}
