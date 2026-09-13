package net.minecraft.client.resources;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.util.ResourceLocation;

class SkinManager$2 implements IImageBuffer {
   SkinManager$2(SkinManager var1, IImageBuffer var2, SkinManager$SkinAvailableCallback var3, Type var4, ResourceLocation var5) {
      super();
      this.field_152639_e = var1;
      this.field_152635_a = var2;
      this.field_152636_b = var3;
      this.field_152637_c = var4;
      this.field_152638_d = var5;
   }

   @Override
   public BufferedImage func_78432_a(BufferedImage var1) {
      if (this.field_152635_a != null) {
         var1 = this.field_152635_a.func_78432_a(var1);
      }

      return var1;
   }

   @Override
   public void func_152634_a() {
      if (this.field_152635_a != null) {
         this.field_152635_a.func_152634_a();
      }

      if (this.field_152636_b != null) {
         this.field_152636_b.func_152121_a(this.field_152637_c, this.field_152638_d);
      }
   }
}
