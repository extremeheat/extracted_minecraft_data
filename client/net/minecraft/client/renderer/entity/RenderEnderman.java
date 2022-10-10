package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.layers.LayerEndermanEyes;
import net.minecraft.client.renderer.entity.layers.LayerHeldBlock;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelEnderman;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;

public class RenderEnderman extends RenderLiving<EntityEnderman> {
   private static final ResourceLocation field_110839_f = new ResourceLocation("textures/entity/enderman/enderman.png");
   private final Random field_77077_b = new Random();

   public RenderEnderman(RenderManager var1) {
      super(var1, new ModelEnderman(0.0F), 0.5F);
      this.func_177094_a(new LayerEndermanEyes(this));
      this.func_177094_a(new LayerHeldBlock(this));
   }

   public ModelEnderman func_177087_b() {
      return (ModelEnderman)super.func_177087_b();
   }

   public void func_76986_a(EntityEnderman var1, double var2, double var4, double var6, float var8, float var9) {
      IBlockState var10 = var1.func_195405_dq();
      ModelEnderman var11 = this.func_177087_b();
      var11.field_78126_a = var10 != null;
      var11.field_78125_b = var1.func_70823_r();
      if (var1.func_70823_r()) {
         double var12 = 0.02D;
         var2 += this.field_77077_b.nextGaussian() * 0.02D;
         var6 += this.field_77077_b.nextGaussian() * 0.02D;
      }

      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityEnderman var1) {
      return field_110839_f;
   }

   // $FF: synthetic method
   public ModelBase func_177087_b() {
      return this.func_177087_b();
   }
}
