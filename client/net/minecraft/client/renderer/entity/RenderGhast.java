package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGhast;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderGhast extends RenderLiving {
   private static final ResourceLocation field_110869_a = new ResourceLocation("textures/entity/ghast/ghast.png");
   private static final ResourceLocation field_110868_f = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

   public RenderGhast() {
      super(new ModelGhast(), 0.5F);
   }

   protected ResourceLocation func_110775_a(EntityGhast var1) {
      return var1.func_110182_bF() ? field_110868_f : field_110869_a;
   }

   protected void func_77041_b(EntityGhast var1, float var2) {
      float var4 = ((float)var1.field_70794_e + (float)(var1.field_70791_f - var1.field_70794_e) * var2) / 20.0F;
      if (var4 < 0.0F) {
         var4 = 0.0F;
      }

      var4 = 1.0F / (var4 * var4 * var4 * var4 * var4 * 2.0F + 1.0F);
      float var5 = (8.0F + var4) / 2.0F;
      float var6 = (8.0F + 1.0F / var4) / 2.0F;
      GL11.glScalef(var6, var5, var6);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
