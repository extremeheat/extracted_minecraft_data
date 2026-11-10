package net.minecraft.client.renderer.texture;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public interface TextureProvider<ResourcesType extends TextureProvider.IntermediaryResources, HandleType extends TextureHandle> {
   ResourcesType prepareState(ResourceManager resourceManager, Identifier identifier) throws IOException;

   ResourcesType missingState();

   HandleType createTexture(ResourcesType state, Identifier identifier);

   void dumpContents(Identifier identifier, Path outputDirectory, HandleType texture);

   void releaseTexture(HandleType texture);

   public interface IntermediaryResources extends UncheckedAutoCloseable {
   }
}
