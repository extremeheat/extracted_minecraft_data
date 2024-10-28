package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.fixes.References;

public enum DataFixTypes {
   LEVEL(References.LEVEL),
   PLAYER(References.PLAYER),
   CHUNK(References.CHUNK),
   HOTBAR(References.HOTBAR),
   OPTIONS(References.OPTIONS),
   STRUCTURE(References.STRUCTURE),
   STATS(References.STATS),
   SAVED_DATA_COMMAND_STORAGE(References.SAVED_DATA_COMMAND_STORAGE),
   SAVED_DATA_FORCED_CHUNKS(References.SAVED_DATA_FORCED_CHUNKS),
   SAVED_DATA_MAP_DATA(References.SAVED_DATA_MAP_DATA),
   SAVED_DATA_MAP_INDEX(References.SAVED_DATA_MAP_INDEX),
   SAVED_DATA_RAIDS(References.SAVED_DATA_RAIDS),
   SAVED_DATA_RANDOM_SEQUENCES(References.SAVED_DATA_RANDOM_SEQUENCES),
   SAVED_DATA_SCOREBOARD(References.SAVED_DATA_SCOREBOARD),
   SAVED_DATA_STRUCTURE_FEATURE_INDICES(References.SAVED_DATA_STRUCTURE_FEATURE_INDICES),
   ADVANCEMENTS(References.ADVANCEMENTS),
   POI_CHUNK(References.POI_CHUNK),
   WORLD_GEN_SETTINGS(References.WORLD_GEN_SETTINGS),
   ENTITY_CHUNK(References.ENTITY_CHUNK);

   public static final Set<DSL.TypeReference> TYPES_FOR_LEVEL_LIST = Set.of(LEVEL.type);
   private final DSL.TypeReference type;

   private DataFixTypes(final DSL.TypeReference var3) {
      this.type = var3;
   }

   static int currentVersion() {
      return SharedConstants.getCurrentVersion().getDataVersion().getVersion();
   }

   public <A> Codec<A> wrapCodec(final Codec<A> var1, final DataFixer var2, final int var3) {
      return new Codec<A>() {
         public <T> DataResult<T> encode(A var1x, DynamicOps<T> var2x, T var3x) {
            return var1.encode(var1x, var2x, var3x).flatMap((var1xx) -> {
               return var2x.mergeToMap(var1xx, var2x.createString("DataVersion"), var2x.createInt(DataFixTypes.currentVersion()));
            });
         }

         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1x, T var2x) {
            DataResult var10000 = var1x.get(var2x, "DataVersion");
            Objects.requireNonNull(var1x);
            int var3x = (Integer)var10000.flatMap(var1x::getNumberValue).map(Number::intValue).result().orElse(var3);
            Dynamic var4 = new Dynamic(var1x, var1x.remove(var2x, "DataVersion"));
            Dynamic var5 = DataFixTypes.this.updateToCurrentVersion(var2, var4, var3x);
            return var1.decode(var5);
         }
      };
   }

   public <T> Dynamic<T> update(DataFixer var1, Dynamic<T> var2, int var3, int var4) {
      return var1.update(this.type, var2, var3, var4);
   }

   public <T> Dynamic<T> updateToCurrentVersion(DataFixer var1, Dynamic<T> var2, int var3) {
      return this.update(var1, var2, var3, currentVersion());
   }

   public CompoundTag update(DataFixer var1, CompoundTag var2, int var3, int var4) {
      return (CompoundTag)this.update(var1, new Dynamic(NbtOps.INSTANCE, var2), var3, var4).getValue();
   }

   public CompoundTag updateToCurrentVersion(DataFixer var1, CompoundTag var2, int var3) {
      return this.update(var1, var2, var3, currentVersion());
   }

   // $FF: synthetic method
   private static DataFixTypes[] $values() {
      return new DataFixTypes[]{LEVEL, PLAYER, CHUNK, HOTBAR, OPTIONS, STRUCTURE, STATS, SAVED_DATA_COMMAND_STORAGE, SAVED_DATA_FORCED_CHUNKS, SAVED_DATA_MAP_DATA, SAVED_DATA_MAP_INDEX, SAVED_DATA_RAIDS, SAVED_DATA_RANDOM_SEQUENCES, SAVED_DATA_SCOREBOARD, SAVED_DATA_STRUCTURE_FEATURE_INDICES, ADVANCEMENTS, POI_CHUNK, WORLD_GEN_SETTINGS, ENTITY_CHUNK};
   }
}
