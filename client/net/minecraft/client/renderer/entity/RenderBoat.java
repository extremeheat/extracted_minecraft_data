package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBoat extends Render {
   private static final ResourceLocation field_110782_f = new ResourceLocation("textures/entity/boat.png");
   protected ModelBase field_76998_a;

   public RenderBoat() {
      super();
      this.field_76989_e = 0.5F;
      this.field_76998_a = new ModelBoat();
   }

   public void func_76986_a(EntityBoat var1, double var2, double var4, double var6, float var8, float var9) {
      GL11.glPushMatrix();
      GL11.glTranslatef((float)var2, (float)var4, (float)var6);
      GL11.glRotatef(180.0F - var8, 0.0F, 1.0F, 0.0F);
      float var10 = (float)var1.func_70268_h() - var9;
      float var11 = var1.func_70271_g() - var9;
      if (var11 < 0.0F) {
         var11 = 0.0F;
      }

      if (var10 > 0.0F) {
         GL11.glRotatef(MathHelper.func_76126_a(var10) * var10 * var11 / 10.0F * (float)var1.func_70267_i(), 1.0F, 0.0F, 0.0F);
      }

      float var12 = 0.75F;
      GL11.glScalef(var12, var12, var12);
      GL11.glScalef(1.0F / var12, 1.0F / var12, 1.0F / var12);
      this.func_110777_b(var1);
      GL11.glScalef(-1.0F, -1.0F, 1.0F);
      this.field_76998_a.func_78088_a(var1, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GL11.glPopMatrix();
   }

   protected ResourceLocation func_110775_a(EntityBoat var1) {
      return field_110782_f;
   }
}
