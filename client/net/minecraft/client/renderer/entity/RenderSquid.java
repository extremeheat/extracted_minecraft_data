package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSquid extends RenderLiving {
   private static final ResourceLocation field_110901_a = new ResourceLocation("textures/entity/squid.png");

   public RenderSquid(ModelBase var1, float var2) {
      super(var1, var2);
   }

   public void func_76986_a(EntitySquid var1, double var2, double var4, double var6, float var8, float var9) {
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntitySquid var1) {
      return field_110901_a;
   }

   protected void func_77043_a(EntitySquid var1, float var2, float var3, float var4) {
      float var5 = var1.field_70862_e + (var1.field_70861_d - var1.field_70862_e) * var4;
      float var6 = var1.field_70860_g + (var1.field_70859_f - var1.field_70860_g) * var4;
      GL11.glTranslatef(0.0F, 0.5F, 0.0F);
      GL11.glRotatef(180.0F - var3, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(var5, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(var6, 0.0F, 1.0F, 0.0F);
      GL11.glTranslatef(0.0F, -1.2F, 0.0F);
   }

   protected float func_77044_a(EntitySquid var1, float var2) {
      return var1.field_70865_by + (var1.field_70866_j - var1.field_70865_by) * var2;
   }
}
