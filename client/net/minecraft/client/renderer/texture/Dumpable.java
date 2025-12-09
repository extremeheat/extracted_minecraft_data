package net.minecraft.client.renderer.texture;

import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.resources.Identifier;

public interface Dumpable {
   void dumpContents(Identifier var1, Path var2) throws IOException;
}
