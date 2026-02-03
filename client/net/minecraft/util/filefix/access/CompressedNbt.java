package net.minecraft.util.filefix.access;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.FileUtil;
import org.slf4j.Logger;

public abstract class CompressedNbt implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path path;
   private final MissingSeverity missingSeverity;

   public CompressedNbt(final Path path, final MissingSeverity missingSeverity) {
      super();
      this.path = path;
      this.missingSeverity = missingSeverity;
   }

   public abstract Optional<Dynamic<Tag>> read() throws IOException;

   protected final Optional<Dynamic<Tag>> readFile() throws IOException {
      try {
         return Optional.of(new Dynamic(NbtOps.INSTANCE, NbtIo.readCompressed(this.path, NbtAccounter.unlimitedHeap())));
      } catch (NoSuchFileException var2) {
         this.missingSeverity.log("Missing file: {}", this.path);
         return Optional.empty();
      }
   }

   public abstract <T> void write(final Dynamic<T> data);

   protected final <T> void writeFile(final Dynamic<T> data) {
      CompoundTag cast = (CompoundTag)data.cast(NbtOps.INSTANCE);

      try {
         FileUtil.createDirectoriesSafe(this.path.getParent());
         NbtIo.writeCompressed(cast, this.path);
      } catch (IOException e) {
         LOGGER.error("Failed to write to {}: {}", this.path, e);
      }

   }

   public Path path() {
      return this.path;
   }

   public void close() {
   }

   public static enum MissingSeverity {
      IMPORTANT,
      NEUTRAL,
      MINOR;

      private final BiConsumer<String, Object> logFunction;

      private MissingSeverity(final BiConsumer<String, Object> logFunction) {
         this.logFunction = logFunction;
      }

      public void log(final String message, final Path path) {
         this.logFunction.accept(message, path);
      }

      // $FF: synthetic method
      private static MissingSeverity[] $values() {
         return new MissingSeverity[]{IMPORTANT, NEUTRAL, MINOR};
      }

      static {
         Logger var10004 = CompressedNbt.LOGGER;
         Objects.requireNonNull(var10004);
         IMPORTANT = new MissingSeverity("IMPORTANT", 0, var10004::error);
         var10004 = CompressedNbt.LOGGER;
         Objects.requireNonNull(var10004);
         NEUTRAL = new MissingSeverity("NEUTRAL", 1, var10004::info);
         var10004 = CompressedNbt.LOGGER;
         Objects.requireNonNull(var10004);
         MINOR = new MissingSeverity("MINOR", 2, var10004::debug);
      }
   }
}
