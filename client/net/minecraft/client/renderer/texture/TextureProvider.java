package net.minecraft.client.renderer.texture;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.io.IOException;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public interface TextureProvider<T extends UncheckedAutoCloseable> {
   T prepareState(ResourceManager resourceManager, Identifier identifier) throws IOException;

   T missingState();

   TextureResources createTexture(T state, Identifier identifier);
}
