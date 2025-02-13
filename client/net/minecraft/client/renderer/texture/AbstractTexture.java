package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import javax.annotation.Nullable;
import net.minecraft.util.TriState;

public abstract class AbstractTexture implements AutoCloseable {
   @Nullable
   protected GpuTexture texture;
   protected boolean defaultBlur;

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

   public void setFilter(TriState var1, boolean var2) {
      this.setFilter(var1.toBoolean(this.defaultBlur), var2);
   }

   public void setFilter(boolean var1, boolean var2) {
      if (this.texture == null) {
         throw new IllegalStateException("Texture does not exist, can't get change its filter before something initializes it");
      } else {
         this.texture.setTextureFilter(var1 ? FilterMode.LINEAR : FilterMode.NEAREST, var2);
      }
   }

   public int getId() {
      if (this.texture == null) {
         throw new IllegalStateException("Texture does not exist, can't get its ID before something initializes it");
      } else {
         return this.texture.glId();
      }
   }

   public void releaseId() {
      if (this.texture != null) {
         this.texture.close();
         this.texture = null;
      }

   }

   public void bind() {
      if (this.texture == null) {
         throw new IllegalStateException("Texture does not exist, can't bind it before something initializes it");
      } else {
         this.texture.bind();
      }
   }

   public void close() {
   }

   public GpuTexture getTexture() {
      if (this.texture == null) {
         throw new IllegalStateException("Texture does not exist, can't get it before something initializes it");
      } else {
         return this.texture;
      }
   }
}
