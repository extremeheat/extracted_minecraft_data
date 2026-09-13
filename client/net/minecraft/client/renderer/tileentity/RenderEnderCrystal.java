package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEnderCrystal extends Render {
   private static final ResourceLocation field_110787_a = new ResourceLocation("textures/entity/endercrystal/endercrystal.png");
   private ModelBase field_76995_b;

   public RenderEnderCrystal() {
      super();
      this.field_76989_e = 0.5F;
      this.field_76995_b = new ModelEnderCrystal(0.0F, true);
   }

   public void func_76986_a(EntityEnderCrystal var1, double var2, double var4, double var6, float var8, float var9) {
      float var10 = (float)var1.field_70261_a + var9;
      GL11.glPushMatrix();
      GL11.glTranslatef((float)var2, (float)var4, (float)var6);
      this.func_110776_a(field_110787_a);
      float var11 = MathHelper.func_76126_a(var10 * 0.2F) / 2.0F + 0.5F;
      var11 = var11 * var11 + var11;
      this.field_76995_b.func_78088_a(var1, 0.0F, var10 * 3.0F, var11 * 0.2F, 0.0F, 0.0F, 0.0625F);
      GL11.glPopMatrix();
   }

   protected ResourceLocation func_110775_a(EntityEnderCrystal var1) {
      return field_110787_a;
   }
}
