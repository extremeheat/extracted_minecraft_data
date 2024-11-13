package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public record EquipmentTable(ResourceKey<LootTable> lootTable, Map<EquipmentSlot, Float> slotDropChances) {
   public static final Codec<Map<EquipmentSlot, Float>> DROP_CHANCES_CODEC;
   public static final Codec<EquipmentTable> CODEC;

   public EquipmentTable(ResourceKey<LootTable> var1, float var2) {
      this(var1, createForAllSlots(var2));
   }

   public EquipmentTable(ResourceKey<LootTable> var1, Map<EquipmentSlot, Float> var2) {
      super();
      this.lootTable = var1;
      this.slotDropChances = var2;
   }

   private static Map<EquipmentSlot, Float> createForAllSlots(float var0) {
      return createForAllSlots(List.of(EquipmentSlot.values()), var0);
   }

   private static Map<EquipmentSlot, Float> createForAllSlots(List<EquipmentSlot> var0, float var1) {
      HashMap var2 = Maps.newHashMap();

      for(EquipmentSlot var4 : var0) {
         var2.put(var4, var1);
      }

      return var2;
   }

   static {
      DROP_CHANCES_CODEC = Codec.either(Codec.FLOAT, Codec.unboundedMap(EquipmentSlot.CODEC, Codec.FLOAT)).xmap((var0) -> (Map)var0.map(EquipmentTable::createForAllSlots, Function.identity()), (var0) -> {
         boolean var1 = var0.values().stream().distinct().count() == 1L;
         boolean var2 = var0.keySet().containsAll(EquipmentSlot.VALUES);
         return var1 && var2 ? Either.left((Float)var0.values().stream().findFirst().orElse(0.0F)) : Either.right(var0);
      });
      CODEC = RecordCodecBuilder.create((var0) -> var0.group(ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(EquipmentTable::lootTable), DROP_CHANCES_CODEC.optionalFieldOf("slot_drop_chances", Map.of()).forGetter(EquipmentTable::slotDropChances)).apply(var0, EquipmentTable::new));
   }
}
