package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Slime;

public class LavaSlimeModel extends ListModel {
   private final ModelPart[] bodyCubes = new ModelPart[8];
   private final ModelPart insideCube;
   private final ImmutableList parts;

   public LavaSlimeModel() {
      for(int var1 = 0; var1 < this.bodyCubes.length; ++var1) {
         byte var2 = 0;
         int var3 = var1;
         if (var1 == 2) {
            var2 = 24;
            var3 = 10;
         } else if (var1 == 3) {
            var2 = 24;
            var3 = 19;
         }

         this.bodyCubes[var1] = new ModelPart(this, var2, var3);
         this.bodyCubes[var1].addBox(-4.0F, (float)(16 + var1), -4.0F, 8.0F, 1.0F, 8.0F);
      }

      this.insideCube = new ModelPart(this, 0, 16);
      this.insideCube.addBox(-2.0F, 18.0F, -2.0F, 4.0F, 4.0F, 4.0F);
      Builder var4 = ImmutableList.builder();
      var4.add(this.insideCube);
      var4.addAll(Arrays.asList(this.bodyCubes));
      this.parts = var4.build();
   }

   public void setupAnim(Slime var1, float var2, float var3, float var4, float var5, float var6) {
   }

   public void prepareMobModel(Slime var1, float var2, float var3, float var4) {
      float var5 = Mth.lerp(var4, var1.oSquish, var1.squish);
      if (var5 < 0.0F) {
         var5 = 0.0F;
      }

      for(int var6 = 0; var6 < this.bodyCubes.length; ++var6) {
         this.bodyCubes[var6].y = (float)(-(4 - var6)) * var5 * 1.7F;
      }

   }

   public ImmutableList parts() {
      return this.parts;
   }

   // $FF: synthetic method
   public Iterable parts() {
      return this.parts();
   }
}
