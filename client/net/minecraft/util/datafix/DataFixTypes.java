package net.minecraft.util.datafix;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.serialization.Dynamic;
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
   SAVED_DATA(References.SAVED_DATA),
   ADVANCEMENTS(References.ADVANCEMENTS),
   POI_CHUNK(References.POI_CHUNK),
   WORLD_GEN_SETTINGS(References.WORLD_GEN_SETTINGS),
   ENTITY_CHUNK(References.ENTITY_CHUNK);

   public static final Set<TypeReference> TYPES_FOR_LEVEL_LIST;
   private final TypeReference type;

   private DataFixTypes(TypeReference var3) {
      this.type = var3;
   }

   private static int currentVersion() {
      return SharedConstants.getCurrentVersion().getDataVersion().getVersion();
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

   static {
      TYPES_FOR_LEVEL_LIST = Set.of(LEVEL.type);
   }
}
