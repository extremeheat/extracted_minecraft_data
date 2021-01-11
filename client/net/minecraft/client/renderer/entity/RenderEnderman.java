package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.entity.layers.LayerEndermanEyes;
import net.minecraft.client.renderer.entity.layers.LayerHeldBlock;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;

public class RenderEnderman extends RenderLiving<EntityEnderman> {
   private static final ResourceLocation field_110839_f = new ResourceLocation("textures/entity/enderman/enderman.png");
   private ModelEnderman field_77078_a;
   private Random field_77077_b = new Random();

   public RenderEnderman(RenderManager var1) {
      super(var1, new ModelEnderman(0.0F), 0.5F);
      this.field_77078_a = (ModelEnderman)super.field_77045_g;
      this.func_177094_a(new LayerEndermanEyes(this));
      this.func_177094_a(new LayerHeldBlock(this));
   }

   public void func_76986_a(EntityEnderman var1, double var2, double var4, double var6, float var8, float var9) {
      this.field_77078_a.field_78126_a = var1.func_175489_ck().func_177230_c().func_149688_o() != Material.field_151579_a;
      this.field_77078_a.field_78125_b = var1.func_70823_r();
      if (var1.func_70823_r()) {
         double var10 = 0.02D;
         var2 += this.field_77077_b.nextGaussian() * var10;
         var6 += this.field_77077_b.nextGaussian() * var10;
      }

      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityEnderman var1) {
      return field_110839_f;
   }
}
