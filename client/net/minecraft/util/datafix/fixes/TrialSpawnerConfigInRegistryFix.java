package net.minecraft.util.datafix.fixes;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class TrialSpawnerConfigInRegistryFix extends NamedEntityFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public TrialSpawnerConfigInRegistryFix(Schema var1) {
      super(var1, false, "TrialSpawnerConfigInRegistryFix", References.BLOCK_ENTITY, "minecraft:trial_spawner");
   }

   public Dynamic<?> fixTag(Dynamic<Tag> var1) {
      Optional var2 = var1.get("normal_config").result();
      if (var2.isEmpty()) {
         return var1;
      } else {
         Optional var3 = var1.get("ominous_config").result();
         if (var3.isEmpty()) {
            return var1;
         } else {
            ResourceLocation var4 = (ResourceLocation)TrialSpawnerConfigInRegistryFix.VanillaTrialChambers.CONFIGS_TO_KEY.get(Pair.of((Dynamic)var2.get(), (Dynamic)var3.get()));
            return var4 == null ? var1 : var1.set("normal_config", var1.createString(var4.withSuffix("/normal").toString())).set("ominous_config", var1.createString(var4.withSuffix("/ominous").toString()));
         }
      }
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var1x) -> {
         DynamicOps var2 = var1x.getOps();
         Dynamic var3 = this.fixTag(var1x.convert(NbtOps.INSTANCE));
         return var3.convert(var2);
      });
   }

   private static final class VanillaTrialChambers {
      public static final Map<Pair<Dynamic<Tag>, Dynamic<Tag>>, ResourceLocation> CONFIGS_TO_KEY = new HashMap();

      private VanillaTrialChambers() {
         super();
      }

      private static void register(ResourceLocation var0, String var1, String var2) {
         try {
            CompoundTag var3 = parse(var1);
            CompoundTag var4 = parse(var2);
            CompoundTag var5 = var3.copy().merge(var4);
            CompoundTag var6 = removeDefaults(var5.copy());
            Dynamic var7 = asDynamic(var3);
            CONFIGS_TO_KEY.put(Pair.of(var7, asDynamic(var4)), var0);
            CONFIGS_TO_KEY.put(Pair.of(var7, asDynamic(var5)), var0);
            CONFIGS_TO_KEY.put(Pair.of(var7, asDynamic(var6)), var0);
         } catch (RuntimeException var8) {
            throw new IllegalStateException("Failed to parse NBT for " + String.valueOf(var0), var8);
         }
      }

      private static Dynamic<Tag> asDynamic(CompoundTag var0) {
         return new Dynamic(NbtOps.INSTANCE, var0);
      }

      private static CompoundTag parse(String var0) {
         try {
            return TagParser.parseTag(var0);
         } catch (CommandSyntaxException var2) {
            throw new IllegalArgumentException("Failed to parse Trial Spawner NBT config: " + var0, var2);
         }
      }

      private static CompoundTag removeDefaults(CompoundTag var0) {
         if (var0.getInt("spawn_range") == 4) {
            var0.remove("spawn_range");
         }

         if (var0.getFloat("total_mobs") == 6.0F) {
            var0.remove("total_mobs");
         }

         if (var0.getFloat("simultaneous_mobs") == 2.0F) {
            var0.remove("simultaneous_mobs");
         }

         if (var0.getFloat("total_mobs_added_per_player") == 2.0F) {
            var0.remove("total_mobs_added_per_player");
         }

         if (var0.getFloat("simultaneous_mobs_added_per_player") == 1.0F) {
            var0.remove("simultaneous_mobs_added_per_player");
         }

         if (var0.getInt("ticks_between_spawn") == 40) {
            var0.remove("ticks_between_spawn");
         }

         return var0;
      }

      static {
         register(ResourceLocation.withDefaultNamespace("trial_chamber/breeze"), "{simultaneous_mobs: 1.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:breeze\"}}, weight: 1}], ticks_between_spawn: 20, total_mobs: 2.0f, total_mobs_added_per_player: 1.0f}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], simultaneous_mobs: 2.0f, total_mobs: 4.0f}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/melee/husk"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:husk\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:husk\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_melee\", slot_drop_chances: 0.0f}}, weight: 1}]}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/melee/spider"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:spider\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}],simultaneous_mobs: 4.0f, total_mobs: 12.0f}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/melee/zombie"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:zombie\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}],spawn_potentials: [{data: {entity: {id: \"minecraft:zombie\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_melee\", slot_drop_chances: 0.0f}}, weight: 1}]}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/ranged/poison_skeleton"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:bogged\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}],spawn_potentials: [{data: {entity: {id: \"minecraft:bogged\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/ranged/skeleton"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:skeleton\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:skeleton\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/ranged/stray"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:stray\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:stray\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/slow_ranged/poison_skeleton"), "{simultaneous_mobs: 4.0f, simultaneous_mobs_added_per_player: 2.0f, spawn_potentials: [{data: {entity: {id: \"minecraft:bogged\"}}, weight: 1}], ticks_between_spawn: 160}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:bogged\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/slow_ranged/skeleton"), "{simultaneous_mobs: 4.0f, simultaneous_mobs_added_per_player: 2.0f, spawn_potentials: [{data: {entity: {id: \"minecraft:skeleton\"}}, weight: 1}], ticks_between_spawn: 160}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:skeleton\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/slow_ranged/stray"), "{simultaneous_mobs: 4.0f, simultaneous_mobs_added_per_player: 2.0f, spawn_potentials: [{data: {entity: {id: \"minecraft:stray\"}}, weight: 1}], ticks_between_spawn: 160}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}],spawn_potentials: [{data: {entity: {id: \"minecraft:stray\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/small_melee/baby_zombie"), "{simultaneous_mobs: 2.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {IsBaby: 1b, id: \"minecraft:zombie\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {IsBaby: 1b, id: \"minecraft:zombie\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_melee\", slot_drop_chances: 0.0f}}, weight: 1}]}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/small_melee/cave_spider"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:cave_spider\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], simultaneous_mobs: 4.0f, total_mobs: 12.0f}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/small_melee/silverfish"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:silverfish\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], simultaneous_mobs: 4.0f, total_mobs: 12.0f}");
         register(ResourceLocation.withDefaultNamespace("trial_chamber/small_melee/slime"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {Size: 1, id: \"minecraft:slime\"}}, weight: 3}, {data: {entity: {Size: 2, id: \"minecraft:slime\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], simultaneous_mobs: 4.0f, total_mobs: 12.0f}");
      }
   }
}
