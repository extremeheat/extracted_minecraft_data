package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EquipmentTable;

public record SpawnData(CompoundTag entityToSpawn, Optional<SpawnData.CustomSpawnRules> customSpawnRules, Optional<EquipmentTable> equipment) {
   public static final String ENTITY_TAG = "entity";
   public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               CompoundTag.CODEC.fieldOf("entity").forGetter(var0x -> var0x.entityToSpawn),
               SpawnData.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter(var0x -> var0x.customSpawnRules),
               EquipmentTable.CODEC.optionalFieldOf("equipment").forGetter(var0x -> var0x.equipment)
            )
            .apply(var0, SpawnData::new)
   );
   public static final Codec<SimpleWeightedRandomList<SpawnData>> LIST_CODEC = SimpleWeightedRandomList.wrappedCodecAllowingEmpty(CODEC);

   public SpawnData() {
      this(new CompoundTag(), Optional.empty(), Optional.empty());
   }

   public SpawnData(CompoundTag entityToSpawn, Optional<SpawnData.CustomSpawnRules> customSpawnRules, Optional<EquipmentTable> equipment) {
      super();
      if (entityToSpawn.contains("id")) {
         ResourceLocation var4 = ResourceLocation.tryParse(entityToSpawn.getString("id"));
         if (var4 != null) {
            entityToSpawn.putString("id", var4.toString());
         } else {
            entityToSpawn.remove("id");
         }
      }

      this.entityToSpawn = entityToSpawn;
      this.customSpawnRules = customSpawnRules;
      this.equipment = equipment;
   }

   public CompoundTag getEntityToSpawn() {
      return this.entityToSpawn;
   }

   public Optional<SpawnData.CustomSpawnRules> getCustomSpawnRules() {
      return this.customSpawnRules;
   }

   public Optional<EquipmentTable> getEquipment() {
      return this.equipment;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
