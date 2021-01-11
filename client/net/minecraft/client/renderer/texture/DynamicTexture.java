package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.resources.IResourceManager;

public class DynamicTexture extends AbstractTexture {
   private final int[] field_110566_b;
   private final int field_94233_j;
   private final int field_94234_k;

   public DynamicTexture(BufferedImage var1) {
      this(var1.getWidth(), var1.getHeight());
      var1.getRGB(0, 0, var1.getWidth(), var1.getHeight(), this.field_110566_b, 0, var1.getWidth());
      this.func_110564_a();
   }

   public DynamicTexture(int var1, int var2) {
      super();
      this.field_94233_j = var1;
      this.field_94234_k = var2;
      this.field_110566_b = new int[var1 * var2];
      TextureUtil.func_110991_a(this.func_110552_b(), var1, var2);
   }

   public void func_110551_a(IResourceManager var1) throws IOException {
   }

   public void func_110564_a() {
      TextureUtil.func_110988_a(this.func_110552_b(), this.field_110566_b, this.field_94233_j, this.field_94234_k);
   }

   public int[] func_110565_c() {
      return this.field_110566_b;
   }
}
