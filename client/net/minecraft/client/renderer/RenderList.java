package net.minecraft.client.renderer;

import java.nio.Buffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;

public class RenderList {
   public int field_78429_a;
   public int field_78427_b;
   public int field_78428_c;
   private double field_78425_d;
   private double field_78426_e;
   private double field_78423_f;
   private IntBuffer field_78424_g = GLAllocation.func_74527_f(65536);
   private boolean field_78430_h;
   private boolean field_78431_i;

   public RenderList() {
      super();
   }

   public void func_78422_a(int var1, int var2, int var3, double var4, double var6, double var8) {
      this.field_78430_h = true;
      ((Buffer)this.field_78424_g).clear();
      this.field_78429_a = var1;
      this.field_78427_b = var2;
      this.field_78428_c = var3;
      this.field_78425_d = var4;
      this.field_78426_e = var6;
      this.field_78423_f = var8;
   }

   public boolean func_78418_a(int var1, int var2, int var3) {
      if (!this.field_78430_h) {
         return false;
      } else {
         return var1 == this.field_78429_a && var2 == this.field_78427_b && var3 == this.field_78428_c;
      }
   }

   public void func_78420_a(int var1) {
      this.field_78424_g.put(var1);
      if (this.field_78424_g.remaining() == 0) {
         this.func_78419_a();
      }
   }

   public void func_78419_a() {
      if (this.field_78430_h) {
         if (!this.field_78431_i) {
            ((Buffer)this.field_78424_g).flip();
            this.field_78431_i = true;
         }

         if (this.field_78424_g.remaining() > 0) {
            GL11.glPushMatrix();
            GL11.glTranslatef(
               (float)((double)this.field_78429_a - this.field_78425_d),
               (float)((double)this.field_78427_b - this.field_78426_e),
               (float)((double)this.field_78428_c - this.field_78423_f)
            );
            GL11.glCallLists(this.field_78424_g);
            GL11.glPopMatrix();
         }
      }
   }

   public void func_78421_b() {
      this.field_78430_h = false;
      this.field_78431_i = false;
   }
}
