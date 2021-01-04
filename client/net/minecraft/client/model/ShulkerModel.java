package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Shulker;

public class ShulkerModel<T extends Shulker> extends EntityModel<T> {
   private final ModelPart base;
   private final ModelPart lid;
   private final ModelPart head;

   public ShulkerModel() {
      super();
      this.texHeight = 64;
      this.texWidth = 64;
      this.lid = new ModelPart(this);
      this.base = new ModelPart(this);
      this.head = new ModelPart(this);
      this.lid.texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16, 12, 16);
      this.lid.setPos(0.0F, 24.0F, 0.0F);
      this.base.texOffs(0, 28).addBox(-8.0F, -8.0F, -8.0F, 16, 8, 16);
      this.base.setPos(0.0F, 24.0F, 0.0F);
      this.head.texOffs(0, 52).addBox(-3.0F, 0.0F, -3.0F, 6, 6, 6);
      this.head.setPos(0.0F, 12.0F, 0.0F);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = var4 - (float)var1.tickCount;
      float var9 = (0.5F + var1.getClientPeekAmount(var8)) * 3.1415927F;
      float var10 = -1.0F + Mth.sin(var9);
      float var11 = 0.0F;
      if (var9 > 3.1415927F) {
         var11 = Mth.sin(var4 * 0.1F) * 0.7F;
      }

      this.lid.setPos(0.0F, 16.0F + Mth.sin(var9) * 8.0F + var11, 0.0F);
      if (var1.getClientPeekAmount(var8) > 0.3F) {
         this.lid.yRot = var10 * var10 * var10 * var10 * 3.1415927F * 0.125F;
      } else {
         this.lid.yRot = 0.0F;
      }

      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.base.render(var7);
      this.lid.render(var7);
   }

   public ModelPart getBase() {
      return this.base;
   }

   public ModelPart getLid() {
      return this.lid;
   }

   public ModelPart getHead() {
      return this.head;
   }
}
