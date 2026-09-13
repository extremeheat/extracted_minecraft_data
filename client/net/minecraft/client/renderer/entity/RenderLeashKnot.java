package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelLeashKnot;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderLeashKnot extends Render {
   private static final ResourceLocation field_110802_a = new ResourceLocation("textures/entity/lead_knot.png");
   private ModelLeashKnot field_110801_f = new ModelLeashKnot();

   public RenderLeashKnot() {
      super();
   }

   public void func_76986_a(EntityLeashKnot var1, double var2, double var4, double var6, float var8, float var9) {
      GL11.glPushMatrix();
      GL11.glDisable(2884);
      GL11.glTranslatef((float)var2, (float)var4, (float)var6);
      float var10 = 0.0625F;
      GL11.glEnable(32826);
      GL11.glScalef(-1.0F, -1.0F, 1.0F);
      GL11.glEnable(3008);
      this.func_110777_b(var1);
      this.field_110801_f.func_78088_a(var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var10);
      GL11.glPopMatrix();
   }

   protected ResourceLocation func_110775_a(EntityLeashKnot var1) {
      return field_110802_a;
   }
}
