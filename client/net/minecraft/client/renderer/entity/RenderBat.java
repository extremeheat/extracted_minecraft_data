package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBat;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBat extends RenderLiving {
   private static final ResourceLocation field_110835_a = new ResourceLocation("textures/entity/bat.png");
   private int field_82446_a = ((ModelBat)this.field_77045_g).func_82889_a();

   public RenderBat() {
      super(new ModelBat(), 0.25F);
   }

   public void func_76986_a(EntityBat var1, double var2, double var4, double var6, float var8, float var9) {
      int var10 = ((ModelBat)this.field_77045_g).func_82889_a();
      if (var10 != this.field_82446_a) {
         this.field_82446_a = var10;
         this.field_77045_g = new ModelBat();
      }

      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityBat var1) {
      return field_110835_a;
   }

   protected void func_77041_b(EntityBat var1, float var2) {
      GL11.glScalef(0.35F, 0.35F, 0.35F);
   }

   protected void func_77039_a(EntityBat var1, double var2, double var4, double var6) {
      super.func_77039_a(var1, var2, var4, var6);
   }

   protected void func_77043_a(EntityBat var1, float var2, float var3, float var4) {
      if (!var1.func_82235_h()) {
         GL11.glTranslatef(0.0F, MathHelper.func_76134_b(var2 * 0.3F) * 0.1F, 0.0F);
      } else {
         GL11.glTranslatef(0.0F, -0.1F, 0.0F);
      }

      super.func_77043_a(var1, var2, var3, var4);
   }
}
