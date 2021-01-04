package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public interface TextureObject {
   void pushFilter(boolean var1, boolean var2);

   void popFilter();

   void load(ResourceManager var1) throws IOException;

   int getId();

   default void bind() {
      GlStateManager.bindTexture(this.getId());
   }

   default void reset(TextureManager var1, ResourceManager var2, ResourceLocation var3, Executor var4) {
      var1.register(var3, this);
   }
}
