package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelSquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.ResourceLocation;

public class RenderSquid extends RenderLiving<EntitySquid> {
   private static final ResourceLocation field_110901_a = new ResourceLocation("textures/entity/squid.png");

   public RenderSquid(RenderManager var1) {
      super(var1, new ModelSquid(), 0.7F);
   }

   protected ResourceLocation func_110775_a(EntitySquid var1) {
      return field_110901_a;
   }

   protected void func_77043_a(EntitySquid var1, float var2, float var3, float var4) {
      float var5 = var1.field_70862_e + (var1.field_70861_d - var1.field_70862_e) * var4;
      float var6 = var1.field_70860_g + (var1.field_70859_f - var1.field_70860_g) * var4;
      GlStateManager.func_179109_b(0.0F, 0.5F, 0.0F);
      GlStateManager.func_179114_b(180.0F - var3, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(var5, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(var6, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179109_b(0.0F, -1.2F, 0.0F);
   }

   protected float func_77044_a(EntitySquid var1, float var2) {
      return var1.field_70865_by + (var1.field_70866_j - var1.field_70865_by) * var2;
   }

   // $FF: synthetic method
   protected float func_77044_a(EntityLivingBase var1, float var2) {
      return this.func_77044_a((EntitySquid)var1, var2);
   }
}
