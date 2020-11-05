package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Ravager;

public class RavagerModel extends ListModel<Ravager> {
   private final ModelPart head;
   private final ModelPart mouth;
   private final ModelPart body;
   private final ModelPart leg0;
   private final ModelPart leg1;
   private final ModelPart leg2;
   private final ModelPart leg3;
   private final ModelPart neck;

   public RavagerModel() {
      super();
      this.texWidth = 128;
      this.texHeight = 128;
      boolean var1 = true;
      float var2 = 0.0F;
      this.neck = new ModelPart(this);
      this.neck.setPos(0.0F, -7.0F, -1.5F);
      this.neck.texOffs(68, 73).addBox(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F, 0.0F);
      this.head = new ModelPart(this);
      this.head.setPos(0.0F, 16.0F, -17.0F);
      this.head.texOffs(0, 0).addBox(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F, 0.0F);
      this.head.texOffs(0, 0).addBox(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F, 0.0F);
      ModelPart var3 = new ModelPart(this);
      var3.setPos(-10.0F, -14.0F, -8.0F);
      var3.texOffs(74, 55).addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F, 0.0F);
      var3.xRot = 1.0995574F;
      this.head.addChild(var3);
      ModelPart var4 = new ModelPart(this);
      var4.mirror = true;
      var4.setPos(8.0F, -14.0F, -8.0F);
      var4.texOffs(74, 55).addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F, 0.0F);
      var4.xRot = 1.0995574F;
      this.head.addChild(var4);
      this.mouth = new ModelPart(this);
      this.mouth.setPos(0.0F, -2.0F, 2.0F);
      this.mouth.texOffs(0, 36).addBox(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F, 0.0F);
      this.head.addChild(this.mouth);
      this.neck.addChild(this.head);
      this.body = new ModelPart(this);
      this.body.texOffs(0, 55).addBox(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F, 0.0F);
      this.body.texOffs(0, 91).addBox(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F, 0.0F);
      this.body.setPos(0.0F, 1.0F, 2.0F);
      this.leg0 = new ModelPart(this, 96, 0);
      this.leg0.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.leg0.setPos(-8.0F, -13.0F, 18.0F);
      this.leg1 = new ModelPart(this, 96, 0);
      this.leg1.mirror = true;
      this.leg1.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.leg1.setPos(8.0F, -13.0F, 18.0F);
      this.leg2 = new ModelPart(this, 64, 0);
      this.leg2.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.leg2.setPos(-8.0F, -13.0F, -5.0F);
      this.leg3 = new ModelPart(this, 64, 0);
      this.leg3.mirror = true;
      this.leg3.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.leg3.setPos(8.0F, -13.0F, -5.0F);
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.neck, this.body, this.leg0, this.leg1, this.leg2, this.leg3);
   }

   public void setupAnim(Ravager var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      this.body.xRot = 1.5707964F;
      float var7 = 0.4F * var3;
      this.leg0.xRot = Mth.cos(var2 * 0.6662F) * var7;
      this.leg1.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * var7;
      this.leg2.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * var7;
      this.leg3.xRot = Mth.cos(var2 * 0.6662F) * var7;
   }

   public void prepareMobModel(Ravager var1, float var2, float var3, float var4) {
      super.prepareMobModel(var1, var2, var3, var4);
      int var5 = var1.getStunnedTick();
      int var6 = var1.getRoarTick();
      boolean var7 = true;
      int var8 = var1.getAttackTick();
      boolean var9 = true;
      float var10;
      float var11;
      float var13;
      if (var8 > 0) {
         var10 = Mth.triangleWave((float)var8 - var4, 10.0F);
         var11 = (1.0F + var10) * 0.5F;
         float var12 = var11 * var11 * var11 * 12.0F;
         var13 = var12 * Mth.sin(this.neck.xRot);
         this.neck.z = -6.5F + var12;
         this.neck.y = -7.0F - var13;
         float var14 = Mth.sin(((float)var8 - var4) / 10.0F * 3.1415927F * 0.25F);
         this.mouth.xRot = 1.5707964F * var14;
         if (var8 > 5) {
            this.mouth.xRot = Mth.sin(((float)(-4 + var8) - var4) / 4.0F) * 3.1415927F * 0.4F;
         } else {
            this.mouth.xRot = 0.15707964F * Mth.sin(3.1415927F * ((float)var8 - var4) / 10.0F);
         }
      } else {
         var10 = -1.0F;
         var11 = -1.0F * Mth.sin(this.neck.xRot);
         this.neck.x = 0.0F;
         this.neck.y = -7.0F - var11;
         this.neck.z = 5.5F;
         boolean var15 = var5 > 0;
         this.neck.xRot = var15 ? 0.21991149F : 0.0F;
         this.mouth.xRot = 3.1415927F * (var15 ? 0.05F : 0.01F);
         if (var15) {
            double var16 = (double)var5 / 40.0D;
            this.neck.x = (float)Math.sin(var16 * 10.0D) * 3.0F;
         } else if (var6 > 0) {
            var13 = Mth.sin(((float)(20 - var6) - var4) / 20.0F * 3.1415927F * 0.25F);
            this.mouth.xRot = 1.5707964F * var13;
         }
      }

   }
}
