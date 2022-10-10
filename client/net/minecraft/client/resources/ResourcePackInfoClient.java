package net.minecraft.client.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ResourcePackInfoClient extends ResourcePackInfo {
   @Nullable
   private final NativeImage field_195809_a;
   @Nullable
   private ResourceLocation field_195810_b;

   public ResourcePackInfoClient(String var1, boolean var2, Supplier<IResourcePack> var3, IResourcePack var4, PackMetadataSection var5, ResourcePackInfo.Priority var6) {
      super(var1, var2, var3, var4, var5, var6);
      NativeImage var7 = null;

      try {
         InputStream var8 = var4.func_195763_b("pack.png");
         Throwable var9 = null;

         try {
            var7 = NativeImage.func_195713_a(var8);
         } catch (Throwable var19) {
            var9 = var19;
            throw var19;
         } finally {
            if (var8 != null) {
               if (var9 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var18) {
                     var9.addSuppressed(var18);
                  }
               } else {
                  var8.close();
               }
            }

         }
      } catch (IllegalArgumentException | IOException var21) {
      }

      this.field_195809_a = var7;
   }

   public ResourcePackInfoClient(String var1, boolean var2, Supplier<IResourcePack> var3, ITextComponent var4, ITextComponent var5, PackCompatibility var6, ResourcePackInfo.Priority var7, boolean var8, @Nullable NativeImage var9) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      this.field_195809_a = var9;
   }

   public void func_195808_a(TextureManager var1) {
      if (this.field_195810_b == null) {
         if (this.field_195809_a == null) {
            this.field_195810_b = new ResourceLocation("textures/misc/unknown_pack.png");
         } else {
            this.field_195810_b = var1.func_110578_a("texturepackicon", new DynamicTexture(this.field_195809_a));
         }
      }

      var1.func_110577_a(this.field_195810_b);
   }
}
