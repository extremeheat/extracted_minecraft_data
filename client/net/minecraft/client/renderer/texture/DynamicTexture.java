package net.minecraft.client.renderer.texture;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.resources.IResourceManager;

public class DynamicTexture extends AbstractTexture implements AutoCloseable {
   @Nullable
   private NativeImage field_110566_b;

   public DynamicTexture(NativeImage var1) {
      super();
      this.field_110566_b = var1;
      TextureUtil.func_110991_a(this.func_110552_b(), this.field_110566_b.func_195702_a(), this.field_110566_b.func_195714_b());
      this.func_110564_a();
   }

   public DynamicTexture(int var1, int var2, boolean var3) {
      super();
      this.field_110566_b = new NativeImage(var1, var2, var3);
      TextureUtil.func_110991_a(this.func_110552_b(), this.field_110566_b.func_195702_a(), this.field_110566_b.func_195714_b());
   }

   public void func_195413_a(IResourceManager var1) throws IOException {
   }

   public void func_110564_a() {
      this.func_195412_h();
      this.field_110566_b.func_195697_a(0, 0, 0, false);
   }

   @Nullable
   public NativeImage func_195414_e() {
      return this.field_110566_b;
   }

   public void func_195415_a(NativeImage var1) throws Exception {
      this.field_110566_b.close();
      this.field_110566_b = var1;
   }

   public void close() {
      this.field_110566_b.close();
      this.func_147631_c();
      this.field_110566_b = null;
   }
}
