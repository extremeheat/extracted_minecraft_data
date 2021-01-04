package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.Pack;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.UnopenedPack;

public class UnopenedResourcePack extends UnopenedPack {
   @Nullable
   private NativeImage icon;
   @Nullable
   private ResourceLocation iconLocation;

   public UnopenedResourcePack(String var1, boolean var2, Supplier<Pack> var3, Pack var4, PackMetadataSection var5, UnopenedPack.Position var6) {
      super(var1, var2, var3, var4, var5, var6);
      NativeImage var7 = null;

      try {
         InputStream var8 = var4.getRootResource("pack.png");
         Throwable var9 = null;

         try {
            var7 = NativeImage.read(var8);
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

      this.icon = var7;
   }

   public UnopenedResourcePack(String var1, boolean var2, Supplier<Pack> var3, Component var4, Component var5, PackCompatibility var6, UnopenedPack.Position var7, boolean var8, @Nullable NativeImage var9) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      this.icon = var9;
   }

   public void bindIcon(TextureManager var1) {
      if (this.iconLocation == null) {
         if (this.icon == null) {
            this.iconLocation = new ResourceLocation("textures/misc/unknown_pack.png");
         } else {
            this.iconLocation = var1.register("texturepackicon", new DynamicTexture(this.icon));
         }
      }

      var1.bind(this.iconLocation);
   }

   public void close() {
      super.close();
      if (this.icon != null) {
         this.icon.close();
         this.icon = null;
      }

   }
}
