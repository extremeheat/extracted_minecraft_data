package net.minecraft.client.resources.palette;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.thread.ParallelMapTransform;
import org.slf4j.Logger;

public class Palette {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Palette EMPTY;
   public static final FileToIdConverter ID_CONVERTER;
   private final int[] colors;

   private Palette(final int[] colors) {
      super();
      this.colors = colors;
   }

   public static CompletableFuture<Map<Identifier, Palette>> listAndLoad(final ResourceManager resourceManager, final Executor taskExecutor) {
      return CompletableFuture.supplyAsync(() -> ID_CONVERTER.listMatchingResources(resourceManager), taskExecutor).thenCompose((resources) -> ParallelMapTransform.schedule(resources, Palette::tryLoad, taskExecutor)).thenApply((palettes) -> (Map)palettes.entrySet().stream().filter((entry) -> !((Palette)entry.getValue()).isEmpty()).collect(Collectors.toMap((entry) -> ID_CONVERTER.fileToId((Identifier)entry.getKey()), Map.Entry::getValue)));
   }

   private static Palette tryLoad(final Identifier id, final Resource resource) {
      try {
         return load(resource);
      } catch (IOException e) {
         LOGGER.error("Failed to load palette with id {}", id, e);
         return EMPTY;
      }
   }

   public static Palette load(final Resource resource) throws IOException {
      InputStream input = resource.open();

      Palette var3;
      try (NativeImage image = NativeImage.read(input)) {
         var3 = from(image);
      } catch (Throwable var8) {
         if (input != null) {
            try {
               input.close();
            } catch (Throwable var5) {
               var8.addSuppressed(var5);
            }
         }

         throw var8;
      }

      if (input != null) {
         input.close();
      }

      return var3;
   }

   public static Palette from(final NativeImage image) {
      return new Palette(image.getPixels());
   }

   @VisibleForTesting
   public static Palette of(final int... colors) {
      return new Palette(IntArrays.copy(colors));
   }

   public int get(final int index) {
      return this.colors[index];
   }

   public int size() {
      return this.colors.length;
   }

   public boolean isEmpty() {
      return this.colors.length == 0;
   }

   static {
      EMPTY = new Palette(IntArrays.EMPTY_ARRAY);
      ID_CONVERTER = new FileToIdConverter("textures/palettes", ".png");
   }
}
