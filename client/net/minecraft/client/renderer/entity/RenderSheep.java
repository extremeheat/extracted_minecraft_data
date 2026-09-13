package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSheep extends RenderLiving {
   private static final ResourceLocation field_110885_a = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
   private static final ResourceLocation field_110884_f = new ResourceLocation("textures/entity/sheep/sheep.png");

   public RenderSheep(ModelBase var1, ModelBase var2, float var3) {
      super(var1, var3);
      this.func_77042_a(var2);
   }

   protected int func_77032_a(EntitySheep var1, int var2, float var3) {
      if (var2 == 0 && !var1.func_70892_o()) {
         this.func_110776_a(field_110885_a);
         if (var1.func_94056_bM() && "jeb_".equals(var1.func_94057_bL())) {
            boolean var9 = true;
            int var5 = var1.field_70173_aa / 25 + var1.func_145782_y();
            int var6 = var5 % EntitySheep.field_70898_d.length;
            int var7 = (var5 + 1) % EntitySheep.field_70898_d.length;
            float var8 = ((float)(var1.field_70173_aa % 25) + var3) / 25.0F;
            GL11.glColor3f(
               EntitySheep.field_70898_d[var6][0] * (1.0F - var8) + EntitySheep.field_70898_d[var7][0] * var8,
               EntitySheep.field_70898_d[var6][1] * (1.0F - var8) + EntitySheep.field_70898_d[var7][1] * var8,
               EntitySheep.field_70898_d[var6][2] * (1.0F - var8) + EntitySheep.field_70898_d[var7][2] * var8
            );
         } else {
            int var4 = var1.func_70896_n();
            GL11.glColor3f(EntitySheep.field_70898_d[var4][0], EntitySheep.field_70898_d[var4][1], EntitySheep.field_70898_d[var4][2]);
         }

         return 1;
      } else {
         return -1;
      }
   }

   protected ResourceLocation func_110775_a(EntitySheep var1) {
      return field_110884_f;
   }
}
