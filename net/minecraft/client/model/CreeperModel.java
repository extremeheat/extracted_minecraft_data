package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class CreeperModel extends ListModel {
   private final ModelPart head;
   private final ModelPart hair;
   private final ModelPart body;
   private final ModelPart leg0;
   private final ModelPart leg1;
   private final ModelPart leg2;
   private final ModelPart leg3;

   public CreeperModel() {
      this(0.0F);
   }

   public CreeperModel(float var1) {
      boolean var2 = true;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var1);
      this.head.setPos(0.0F, 6.0F, 0.0F);
      this.hair = new ModelPart(this, 32, 0);
      this.hair.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var1 + 0.5F);
      this.hair.setPos(0.0F, 6.0F, 0.0F);
      this.body = new ModelPart(this, 16, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, var1);
      this.body.setPos(0.0F, 6.0F, 0.0F);
      this.leg0 = new ModelPart(this, 0, 16);
      this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, var1);
      this.leg0.setPos(-2.0F, 18.0F, 4.0F);
      this.leg1 = new ModelPart(this, 0, 16);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, var1);
      this.leg1.setPos(2.0F, 18.0F, 4.0F);
      this.leg2 = new ModelPart(this, 0, 16);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, var1);
      this.leg2.setPos(-2.0F, 18.0F, -4.0F);
      this.leg3 = new ModelPart(this, 0, 16);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, var1);
      this.leg3.setPos(2.0F, 18.0F, -4.0F);
   }

   public Iterable parts() {
      return ImmutableList.of(this.head, this.body, this.leg0, this.leg1, this.leg2, this.leg3);
   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      this.leg0.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      this.leg1.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.leg2.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.leg3.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
   }
}
