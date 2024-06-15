package net.minecraft.world.level.chunk.storage;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.minecraft.util.FastBufferedInputStream;
import org.slf4j.Logger;

public class RegionFileVersion {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Int2ObjectMap<RegionFileVersion> VERSIONS = new Int2ObjectOpenHashMap();
   private static final Object2ObjectMap<String, RegionFileVersion> VERSIONS_BY_NAME = new Object2ObjectOpenHashMap();
   public static final RegionFileVersion VERSION_GZIP = register(
      new RegionFileVersion(
         1, null, var0 -> new FastBufferedInputStream(new GZIPInputStream(var0)), var0 -> new BufferedOutputStream(new GZIPOutputStream(var0))
      )
   );
   public static final RegionFileVersion VERSION_DEFLATE = register(
      new RegionFileVersion(
         2, "deflate", var0 -> new FastBufferedInputStream(new InflaterInputStream(var0)), var0 -> new BufferedOutputStream(new DeflaterOutputStream(var0))
      )
   );
   public static final RegionFileVersion VERSION_NONE = register(new RegionFileVersion(3, "none", FastBufferedInputStream::new, BufferedOutputStream::new));
   public static final RegionFileVersion VERSION_LZ4 = register(
      new RegionFileVersion(
         4, "lz4", var0 -> new FastBufferedInputStream(new LZ4BlockInputStream(var0)), var0 -> new BufferedOutputStream(new LZ4BlockOutputStream(var0))
      )
   );
   public static final RegionFileVersion VERSION_CUSTOM = register(new RegionFileVersion(127, null, var0 -> {
      throw new UnsupportedOperationException();
   }, var0 -> {
      throw new UnsupportedOperationException();
   }));
   public static final RegionFileVersion DEFAULT = VERSION_DEFLATE;
   private static volatile RegionFileVersion selected = DEFAULT;
   private final int id;
   @Nullable
   private final String optionName;
   private final RegionFileVersion.StreamWrapper<InputStream> inputWrapper;
   private final RegionFileVersion.StreamWrapper<OutputStream> outputWrapper;

   private RegionFileVersion(
      int var1, @Nullable String var2, RegionFileVersion.StreamWrapper<InputStream> var3, RegionFileVersion.StreamWrapper<OutputStream> var4
   ) {
      super();
      this.id = var1;
      this.optionName = var2;
      this.inputWrapper = var3;
      this.outputWrapper = var4;
   }

   private static RegionFileVersion register(RegionFileVersion var0) {
      VERSIONS.put(var0.id, var0);
      if (var0.optionName != null) {
         VERSIONS_BY_NAME.put(var0.optionName, var0);
      }

      return var0;
   }

   @Nullable
   public static RegionFileVersion fromId(int var0) {
      return (RegionFileVersion)VERSIONS.get(var0);
   }

   public static void configure(String var0) {
      RegionFileVersion var1 = (RegionFileVersion)VERSIONS_BY_NAME.get(var0);
      if (var1 != null) {
         selected = var1;
      } else {
         LOGGER.error(
            "Invalid `region-file-compression` value `{}` in server.properties. Please use one of: {}", var0, String.join(", ", VERSIONS_BY_NAME.keySet())
         );
      }
   }

   public static RegionFileVersion getSelected() {
      return selected;
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
