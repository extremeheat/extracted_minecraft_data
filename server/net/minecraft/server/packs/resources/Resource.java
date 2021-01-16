package net.minecraft.server.packs.resources;

import java.io.Closeable;
import java.io.InputStream;

public interface Resource extends Closeable {
   InputStream getInputStream();

   String getSourceName();
}
