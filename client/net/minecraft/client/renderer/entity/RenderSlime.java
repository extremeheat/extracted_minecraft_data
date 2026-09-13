package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSlime extends RenderLiving {
   private static final ResourceLocation field_110897_a = new ResourceLocation("textures/entity/slime/slime.png");
   private ModelBase field_77092_a;

   public RenderSlime(ModelBase var1, ModelBase var2, float var3) {
      super(var1, var3);
      this.field_77092_a = var2;
   }

   protected int func_77032_a(EntitySlime var1, int var2, float var3) {
      if (var1.func_82150_aj()) {
         return 0;
      } else if (var2 == 0) {
         this.func_77042_a(this.field_77092_a);
         GL11.glEnable(2977);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         return 1;
      } else {
         if (var2 == 1) {
            GL11.glDisable(3042);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         }

         return -1;
      }
   }

   protected void func_77041_b(EntitySlime var1, float var2) {
      float var3 = (float)var1.func_70809_q();
      float var4 = (var1.field_70812_c + (var1.field_70811_b - var1.field_70812_c) * var2) / (var3 * 0.5F + 1.0F);
      float var5 = 1.0F / (var4 + 1.0F);
      GL11.glScalef(var5 * var3, 1.0F / var5 * var3, var5 * var3);
   }

   protected ResourceLocation func_110775_a(EntitySlime var1) {
      return field_110897_a;
   }
}
