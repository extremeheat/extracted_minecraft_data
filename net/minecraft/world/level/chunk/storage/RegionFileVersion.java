package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;

public class RegionFileVersion {
   private static final Int2ObjectMap VERSIONS = new Int2ObjectOpenHashMap();
   public static final RegionFileVersion VERSION_GZIP = register(new RegionFileVersion(1, GZIPInputStream::new, GZIPOutputStream::new));
   public static final RegionFileVersion VERSION_DEFLATE = register(new RegionFileVersion(2, InflaterInputStream::new, DeflaterOutputStream::new));
   public static final RegionFileVersion VERSION_NONE = register(new RegionFileVersion(3, (var0) -> {
      return var0;
   }, (var0) -> {
      return var0;
   }));
   private final int id;
   private final RegionFileVersion.StreamWrapper inputWrapper;
   private final RegionFileVersion.StreamWrapper outputWrapper;

   private RegionFileVersion(int var1, RegionFileVersion.StreamWrapper var2, RegionFileVersion.StreamWrapper var3) {
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
      return (OutputStream)this.outputWrapper.wrap(var1);
   }

   public InputStream wrap(InputStream var1) throws IOException {
      return (InputStream)this.inputWrapper.wrap(var1);
   }

   @FunctionalInterface
   interface StreamWrapper {
      Object wrap(Object var1) throws IOException;
   }
}
