package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class DolphinModel<T extends Entity> extends ListModel<T> {
   private final ModelPart body;
   private final ModelPart tail;
   private final ModelPart tailFin;

   public DolphinModel() {
      super();
      this.texWidth = 64;
      this.texHeight = 64;
      float var1 = 18.0F;
      float var2 = -8.0F;
      this.body = new ModelPart(this, 22, 0);
      this.body.addBox(-4.0F, -7.0F, 0.0F, 8.0F, 7.0F, 13.0F);
      this.body.setPos(0.0F, 22.0F, -5.0F);
      ModelPart var3 = new ModelPart(this, 51, 0);
      var3.addBox(-0.5F, 0.0F, 8.0F, 1.0F, 4.0F, 5.0F);
      var3.xRot = 1.0471976F;
      this.body.addChild(var3);
      ModelPart var4 = new ModelPart(this, 48, 20);
      var4.mirror = true;
      var4.addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F);
      var4.setPos(2.0F, -2.0F, 4.0F);
      var4.xRot = 1.0471976F;
      var4.zRot = 2.0943952F;
      this.body.addChild(var4);
      ModelPart var5 = new ModelPart(this, 48, 20);
      var5.addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F);
      var5.setPos(-2.0F, -2.0F, 4.0F);
      var5.xRot = 1.0471976F;
      var5.zRot = -2.0943952F;
      this.body.addChild(var5);
      this.tail = new ModelPart(this, 0, 19);
      this.tail.addBox(-2.0F, -2.5F, 0.0F, 4.0F, 5.0F, 11.0F);
      this.tail.setPos(0.0F, -2.5F, 11.0F);
      this.tail.xRot = -0.10471976F;
      this.body.addChild(this.tail);
      this.tailFin = new ModelPart(this, 19, 20);
      this.tailFin.addBox(-5.0F, -0.5F, 0.0F, 10.0F, 1.0F, 6.0F);
      this.tailFin.setPos(0.0F, 0.0F, 9.0F);
      this.tailFin.xRot = 0.0F;
      this.tail.addChild(this.tailFin);
      ModelPart var6 = new ModelPart(this, 0, 0);
      var6.addBox(-4.0F, -3.0F, -3.0F, 8.0F, 7.0F, 6.0F);
      var6.setPos(0.0F, -4.0F, -3.0F);
      ModelPart var7 = new ModelPart(this, 0, 13);
      var7.addBox(-1.0F, 2.0F, -7.0F, 2.0F, 2.0F, 4.0F);
      var6.addChild(var7);
      this.body.addChild(var6);
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.body);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.body.xRot = var6 * 0.017453292F;
      this.body.yRot = var5 * 0.017453292F;
      if (Entity.getHorizontalDistanceSqr(var1.getDeltaMovement()) > 1.0E-7D) {
         ModelPart var10000 = this.body;
         var10000.xRot += -0.05F + -0.05F * Mth.cos(var4 * 0.3F);
         this.tail.xRot = -0.1F * Mth.cos(var4 * 0.3F);
         this.tailFin.xRot = -0.2F * Mth.cos(var4 * 0.3F);
      }

   }
}
