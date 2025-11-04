package net.minecraft.client.renderer.texture;

import java.io.IOException;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class SimpleTexture extends ReloadableTexture {
   public SimpleTexture(Identifier var1) {
      super(var1);
   }

   public TextureContents loadContents(ResourceManager var1) throws IOException {
      return TextureContents.load(var1, this.resourceId());
   }
}
