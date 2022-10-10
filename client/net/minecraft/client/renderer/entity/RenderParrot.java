package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelParrot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderParrot extends RenderLiving<EntityParrot> {
   public static final ResourceLocation[] field_192862_a = new ResourceLocation[]{new ResourceLocation("textures/entity/parrot/parrot_red_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_green.png"), new ResourceLocation("textures/entity/parrot/parrot_yellow_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_grey.png")};

   public RenderParrot(RenderManager var1) {
      super(var1, new ModelParrot(), 0.3F);
   }

   protected ResourceLocation func_110775_a(EntityParrot var1) {
      return field_192862_a[var1.func_191998_ds()];
   }

   public float func_77044_a(EntityParrot var1, float var2) {
      return this.func_192861_b(var1, var2);
   }

   private float func_192861_b(EntityParrot var1, float var2) {
      float var3 = var1.field_192011_bE + (var1.field_192008_bB - var1.field_192011_bE) * var2;
      float var4 = var1.field_192010_bD + (var1.field_192009_bC - var1.field_192010_bD) * var2;
      return (MathHelper.func_76126_a(var3) + 1.0F) * var4;
   }

   // $FF: synthetic method
   public float func_77044_a(EntityLivingBase var1, float var2) {
      return this.func_77044_a((EntityParrot)var1, var2);
   }
}
