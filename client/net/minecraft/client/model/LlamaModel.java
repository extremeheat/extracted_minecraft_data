package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

public class LlamaModel<T extends AbstractChestedHorse> extends QuadrupedModel<T> {
   private final ModelPart chest1;
   private final ModelPart chest2;

   public LlamaModel(float var1) {
      super(15, var1);
      this.texWidth = 128;
      this.texHeight = 64;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-2.0F, -14.0F, -10.0F, 4, 4, 9, var1);
      this.head.setPos(0.0F, 7.0F, -6.0F);
      this.head.texOffs(0, 14).addBox(-4.0F, -16.0F, -6.0F, 8, 18, 6, var1);
      this.head.texOffs(17, 0).addBox(-4.0F, -19.0F, -4.0F, 3, 3, 2, var1);
      this.head.texOffs(17, 0).addBox(1.0F, -19.0F, -4.0F, 3, 3, 2, var1);
      this.body = new ModelPart(this, 29, 0);
      this.body.addBox(-6.0F, -10.0F, -7.0F, 12, 18, 10, var1);
      this.body.setPos(0.0F, 5.0F, 2.0F);
      this.chest1 = new ModelPart(this, 45, 28);
      this.chest1.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3, var1);
      this.chest1.setPos(-8.5F, 3.0F, 3.0F);
      this.chest1.yRot = 1.5707964F;
      this.chest2 = new ModelPart(this, 45, 41);
      this.chest2.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3, var1);
      this.chest2.setPos(5.5F, 3.0F, 3.0F);
      this.chest2.yRot = 1.5707964F;
      boolean var2 = true;
      boolean var3 = true;
      this.leg0 = new ModelPart(this, 29, 29);
      this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4, 14, 4, var1);
      this.leg0.setPos(-2.5F, 10.0F, 6.0F);
      this.leg1 = new ModelPart(this, 29, 29);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 14, 4, var1);
      this.leg1.setPos(2.5F, 10.0F, 6.0F);
      this.leg2 = new ModelPart(this, 29, 29);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 14, 4, var1);
      this.leg2.setPos(-2.5F, 10.0F, -4.0F);
      this.leg3 = new ModelPart(this, 29, 29);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 14, 4, var1);
      this.leg3.setPos(2.5F, 10.0F, -4.0F);
      --this.leg0.x;
      ++this.leg1.x;
      ModelPart var10000 = this.leg0;
      var10000.z += 0.0F;
      var10000 = this.leg1;
      var10000.z += 0.0F;
      --this.leg2.x;
      ++this.leg3.x;
      --this.leg2.z;
      --this.leg3.z;
      this.zHeadOffs += 2.0F;
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      boolean var8 = !var1.isBaby() && var1.hasChest();
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      if (this.young) {
         float var9 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, this.yHeadOffs * var7, this.zHeadOffs * var7);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         float var10 = 0.7F;
         GlStateManager.scalef(0.71428573F, 0.64935064F, 0.7936508F);
         GlStateManager.translatef(0.0F, 21.0F * var7, 0.22F);
         this.head.render(var7);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         float var11 = 1.1F;
         GlStateManager.scalef(0.625F, 0.45454544F, 0.45454544F);
         GlStateManager.translatef(0.0F, 33.0F * var7, 0.0F);
         this.body.render(var7);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.45454544F, 0.41322312F, 0.45454544F);
         GlStateManager.translatef(0.0F, 33.0F * var7, 0.0F);
         this.leg0.render(var7);
         this.leg1.render(var7);
         this.leg2.render(var7);
         this.leg3.render(var7);
         GlStateManager.popMatrix();
      } else {
         this.head.render(var7);
         this.body.render(var7);
         this.leg0.render(var7);
         this.leg1.render(var7);
         this.leg2.render(var7);
         this.leg3.render(var7);
      }

      if (var8) {
         this.chest1.render(var7);
         this.chest2.render(var7);
      }

   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((AbstractChestedHorse)var1, var2, var3, var4, var5, var6, var7);
   }
}
