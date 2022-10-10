package net.minecraft.client.renderer.texture;

import java.io.IOException;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.resources.IResourceManager;

public interface ITextureObject {
   void func_174936_b(boolean var1, boolean var2);

   void func_174935_a();

   void func_195413_a(IResourceManager var1) throws IOException;

   int func_110552_b();

   default void func_195412_h() {
      GlStateManager.func_179144_i(this.func_110552_b());
   }
}
