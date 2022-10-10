package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerIronGolemFlower;
import net.minecraft.client.renderer.entity.model.ModelIronGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;

public class RenderIronGolem extends RenderLiving<EntityIronGolem> {
   private static final ResourceLocation field_110899_a = new ResourceLocation("textures/entity/iron_golem.png");

   public RenderIronGolem(RenderManager var1) {
      super(var1, new ModelIronGolem(), 0.5F);
      this.func_177094_a(new LayerIronGolemFlower(this));
   }

   protected ResourceLocation func_110775_a(EntityIronGolem var1) {
      return field_110899_a;
   }

   protected void func_77043_a(EntityIronGolem var1, float var2, float var3, float var4) {
      super.func_77043_a(var1, var2, var3, var4);
      if ((double)var1.field_70721_aZ >= 0.01D) {
         float var5 = 13.0F;
         float var6 = var1.field_184619_aG - var1.field_70721_aZ * (1.0F - var4) + 6.0F;
         float var7 = (Math.abs(var6 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         GlStateManager.func_179114_b(6.5F * var7, 0.0F, 0.0F, 1.0F);
      }
   }
}
