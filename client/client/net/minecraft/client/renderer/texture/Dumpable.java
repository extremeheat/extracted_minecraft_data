package net.minecraft.client.renderer.texture;

import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.resources.ResourceLocation;

public interface Dumpable {
   void dumpContents(ResourceLocation var1, Path var2) throws IOException;
}
