package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.minecraft.util.FastBufferedInputStream;

public class RegionFileVersion {
   private static final Int2ObjectMap<RegionFileVersion> VERSIONS = new Int2ObjectOpenHashMap();
   public static final RegionFileVersion VERSION_GZIP = register(
      new RegionFileVersion(1, var0 -> new FastBufferedInputStream(new GZIPInputStream(var0)), var0 -> new BufferedOutputStream(new GZIPOutputStream(var0)))
   );
   public static final RegionFileVersion VERSION_DEFLATE = register(
      new RegionFileVersion(
         2, var0 -> new FastBufferedInputStream(new InflaterInputStream(var0)), var0 -> new BufferedOutputStream(new DeflaterOutputStream(var0))
      )
   );
   public static final RegionFileVersion VERSION_NONE = register(new RegionFileVersion(3, var0 -> var0, var0 -> var0));
   private final int id;
   private final RegionFileVersion.StreamWrapper<InputStream> inputWrapper;
   private final RegionFileVersion.StreamWrapper<OutputStream> outputWrapper;

   private RegionFileVersion(int var1, RegionFileVersion.StreamWrapper<InputStream> var2, RegionFileVersion.StreamWrapper<OutputStream> var3) {
      super();
      this.id = var1;
      this.inputWrapper = var2;
      this.outputWrapper = var3;
   }

   private static RegionFileVersion register(RegionFileVersion var0) {
      VERSIONS.put(var0.id, var0);
      return var0;
   }

   @Nullable
   public static RegionFileVersion fromId(int var0) {
      return (RegionFileVersion)VERSIONS.get(var0);
   }

   public static boolean isValidVersion(int var0) {
      return VERSIONS.containsKey(var0);
   }

   public int getId() {
      return this.id;
   }

   public OutputStream wrap(OutputStream var1) throws IOException {
      return this.outputWrapper.wrap(var1);
   }

   public InputStream wrap(InputStream var1) throws IOException {
      return this.inputWrapper.wrap(var1);
   }

   @FunctionalInterface
   interface StreamWrapper<O> {
      O wrap(O var1) throws IOException;
   }
}
