package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatModel extends ListModel {
   private final ModelPart[] paddles = new ModelPart[2];
   private final ModelPart waterPatch;
   private final ImmutableList parts;

   public BoatModel() {
      ModelPart[] var1 = new ModelPart[]{(new ModelPart(this, 0, 0)).setTexSize(128, 64), (new ModelPart(this, 0, 19)).setTexSize(128, 64), (new ModelPart(this, 0, 27)).setTexSize(128, 64), (new ModelPart(this, 0, 35)).setTexSize(128, 64), (new ModelPart(this, 0, 43)).setTexSize(128, 64)};
      boolean var2 = true;
      boolean var3 = true;
      boolean var4 = true;
      boolean var5 = true;
      boolean var6 = true;
      var1[0].addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, 0.0F);
      var1[0].setPos(0.0F, 3.0F, 1.0F);
      var1[1].addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, 0.0F);
      var1[1].setPos(-15.0F, 4.0F, 4.0F);
      var1[2].addBox(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F, 0.0F);
      var1[2].setPos(15.0F, 4.0F, 0.0F);
      var1[3].addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, 0.0F);
      var1[3].setPos(0.0F, 4.0F, -9.0F);
      var1[4].addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, 0.0F);
      var1[4].setPos(0.0F, 4.0F, 9.0F);
      var1[0].xRot = 1.5707964F;
      var1[1].yRot = 4.712389F;
      var1[2].yRot = 1.5707964F;
      var1[3].yRot = 3.1415927F;
      this.paddles[0] = this.makePaddle(true);
      this.paddles[0].setPos(3.0F, -5.0F, 9.0F);
      this.paddles[1] = this.makePaddle(false);
      this.paddles[1].setPos(3.0F, -5.0F, -9.0F);
      this.paddles[1].yRot = 3.1415927F;
      this.paddles[0].zRot = 0.19634955F;
      this.paddles[1].zRot = 0.19634955F;
      this.waterPatch = (new ModelPart(this, 0, 0)).setTexSize(128, 64);
      this.waterPatch.addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, 0.0F);
      this.waterPatch.setPos(0.0F, -3.0F, 1.0F);
      this.waterPatch.xRot = 1.5707964F;
      Builder var7 = ImmutableList.builder();
      var7.addAll(Arrays.asList(var1));
      var7.addAll(Arrays.asList(this.paddles));
      this.parts = var7.build();
   }

   public void setupAnim(Boat var1, float var2, float var3, float var4, float var5, float var6) {
      this.animatePaddle(var1, 0, var2);
      this.animatePaddle(var1, 1, var2);
   }

   public ImmutableList parts() {
      return this.parts;
   }

   public ModelPart waterPatch() {
      return this.waterPatch;
   }

   protected ModelPart makePaddle(boolean var1) {
      ModelPart var2 = (new ModelPart(this, 62, var1 ? 0 : 20)).setTexSize(128, 64);
      boolean var3 = true;
      boolean var4 = true;
      boolean var5 = true;
      float var6 = -5.0F;
      var2.addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F);
      var2.addBox(var1 ? -1.001F : 0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F);
      return var2;
   }

   protected void animatePaddle(Boat var1, int var2, float var3) {
      float var4 = var1.getRowingTime(var2, var3);
      ModelPart var5 = this.paddles[var2];
      var5.xRot = (float)Mth.clampedLerp(-1.0471975803375244D, -0.2617993950843811D, (double)((Mth.sin(-var4) + 1.0F) / 2.0F));
      var5.yRot = (float)Mth.clampedLerp(-0.7853981852531433D, 0.7853981852531433D, (double)((Mth.sin(-var4 + 1.0F) + 1.0F) / 2.0F));
      if (var2 == 1) {
         var5.yRot = 3.1415927F - var5.yRot;
      }

   }

   // $FF: synthetic method
   public Iterable parts() {
      return this.parts();
   }
}
