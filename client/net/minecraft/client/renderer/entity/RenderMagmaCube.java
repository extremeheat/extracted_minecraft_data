package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderMagmaCube extends RenderLiving {
   private static final ResourceLocation field_110873_a = new ResourceLocation("textures/entity/slime/magmacube.png");

   public RenderMagmaCube() {
      super(new ModelMagmaCube(), 0.25F);
   }

   protected ResourceLocation func_110775_a(EntityMagmaCube var1) {
      return field_110873_a;
   }

   protected void func_77041_b(EntityMagmaCube var1, float var2) {
      int var3 = var1.func_70809_q();
      float var4 = (var1.field_70812_c + (var1.field_70811_b - var1.field_70812_c) * var2) / ((float)var3 * 0.5F + 1.0F);
      float var5 = 1.0F / (var4 + 1.0F);
      float var6 = (float)var3;
      GL11.glScalef(var5 * var6, 1.0F / var5 * var6, var5 * var6);
   }
}
