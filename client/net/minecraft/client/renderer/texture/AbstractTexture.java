package net.minecraft.client.renderer.texture;

public abstract class AbstractTexture implements ITextureObject {
   protected int field_110553_a = -1;

   public AbstractTexture() {
      super();
   }

   @Override
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
