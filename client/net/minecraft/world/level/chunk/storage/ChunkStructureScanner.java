package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public record ChunkStructureScanner(ChunkScanAccess storageAccess, RegistryAccess registryAccess, long levelSeed, DataFixer fixerUpper, ResourceKey<Level> dimension, Optional<Identifier> typeNameForDataFixer) {
   public ChunkStructureScanner {
      super();
   }

   public CompletableFuture<@Nullable SerializableChunkData.StructureData> scanStructures(final ChunkPos pos) {
      return SerializableChunkData.scanStructures(this, pos);
   }
}
