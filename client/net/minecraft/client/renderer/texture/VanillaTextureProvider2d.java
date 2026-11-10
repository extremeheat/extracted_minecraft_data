package net.minecraft.client.renderer.texture;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class VanillaTextureProvider2d extends TextureProvider2d {
   public VanillaTextureProvider2d() {
      super();
   }

   public TextureContents prepareState(final ResourceManager resourceManager, final Identifier identifier) throws IOException {
      return TextureContents.load(Minecraft.getInstance().getVanillaPackResources().asResourceManager(), identifier);
   }
}
