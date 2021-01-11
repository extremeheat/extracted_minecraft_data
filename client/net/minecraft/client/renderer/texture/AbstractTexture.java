package net.minecraft.client.renderer.texture;

import org.lwjgl.opengl.GL11;

public abstract class AbstractTexture implements ITextureObject {
   protected int field_110553_a = -1;
   protected boolean field_174940_b;
   protected boolean field_174941_c;
   protected boolean field_174938_d;
   protected boolean field_174939_e;

   public AbstractTexture() {
      super();
   }

   public void func_174937_a(boolean var1, boolean var2) {
      this.field_174940_b = var1;
      this.field_174941_c = var2;
      boolean var3 = true;
      boolean var4 = true;
      int var5;
      short var6;
      if (var1) {
         var5 = var2 ? 9987 : 9729;
         var6 = 9729;
      } else {
         var5 = var2 ? 9986 : 9728;
         var6 = 9728;
      }

      GL11.glTexParameteri(3553, 10241, var5);
      GL11.glTexParameteri(3553, 10240, var6);
   }

   public void func_174936_b(boolean var1, boolean var2) {
      this.field_174938_d = this.field_174940_b;
      this.field_174939_e = this.field_174941_c;
      this.func_174937_a(var1, var2);
   }

   public void func_174935_a() {
      this.func_174937_a(this.field_174938_d, this.field_174939_e);
   }

   public int func_110552_b() {
      if (this.field_110553_a == -1) {
         this.field_110553_a = TextureUtil.func_110996_a();
      }

      return this.field_110553_a;
   }

   public void func_147631_c() {
      if (this.field_110553_a != -1) {
         TextureUtil.func_147942_a(this.field_110553_a);
         this.field_110553_a = -1;
      }

   }
}
