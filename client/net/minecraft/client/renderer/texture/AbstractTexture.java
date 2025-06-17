package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import javax.annotation.Nullable;

public abstract class AbstractTexture implements AutoCloseable {
   @Nullable
   protected GpuTexture texture;
   @Nullable
   protected GpuTextureView textureView;

   public AbstractTexture() {
      super();
   }

   public void setClamp(boolean var1) {
      if (this.texture == null) {
         throw new IllegalStateException("Texture does not exist, can't change its clamp before something initializes it");
      } else {
         this.texture.setAddressMode(var1 ? AddressMode.CLAMP_TO_EDGE : AddressMode.REPEAT);
      }
   }

   public void setFilter(boolean var1, boolean var2) {
      if (this.texture == null) {
         throw new IllegalStateException("Texture does not exist, can't get change its filter before something initializes it");
      } else {
         this.texture.setTextureFilter(var1 ? FilterMode.LINEAR : FilterMode.NEAREST, var2);
      }
   }

   public void setUseMipmaps(boolean var1) {
      if (this.texture == null) {
         throw new IllegalStateException("Texture does not exist, can't get change its filter before something initializes it");
      } else {
         this.texture.setUseMipmaps(var1);
      }
   }

   public void close() {
      if (this.texture != null) {
         this.texture.close();
         this.texture = null;
      }

      if (this.textureView != null) {
         this.textureView.close();
         this.textureView = null;
      }

   }

   public GpuTexture getTexture() {
      if (this.texture == null) {
         throw new IllegalStateException("Texture does not exist, can't get it before something initializes it");
      } else {
         return this.texture;
      }
   }

   public GpuTextureView getTextureView() {
      if (this.textureView == null) {
         throw new IllegalStateException("Texture view does not exist, can't get it before something initializes it");
      } else {
         return this.textureView;
      }
   }
}
