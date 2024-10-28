package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1460 extends NamespacedSchema {
   public V1460(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static void registerMob(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return V100.equipment(var0);
      });
   }

   protected static void registerInventory(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var0)));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      var1.register(var2, "minecraft:area_effect_cloud", (var1x) -> {
         return DSL.optionalFields("Particle", References.PARTICLE.in(var1));
      });
      registerMob(var1, var2, "minecraft:armor_stand");
      var1.register(var2, "minecraft:arrow", (var1x) -> {
         return DSL.optionalFields("inBlockState", References.BLOCK_STATE.in(var1));
      });
      registerMob(var1, var2, "minecraft:bat");
      registerMob(var1, var2, "minecraft:blaze");
      var1.registerSimple(var2, "minecraft:boat");
      registerMob(var1, var2, "minecraft:cave_spider");
      var1.register(var2, "minecraft:chest_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1), "Items", DSL.list(References.ITEM_STACK.in(var1)));
      });
      registerMob(var1, var2, "minecraft:chicken");
      var1.register(var2, "minecraft:commandblock_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1));
      });
      registerMob(var1, var2, "minecraft:cow");
      registerMob(var1, var2, "minecraft:creeper");
      var1.register(var2, "minecraft:donkey", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)), "SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      var1.registerSimple(var2, "minecraft:dragon_fireball");
      var1.registerSimple(var2, "minecraft:egg");
      registerMob(var1, var2, "minecraft:elder_guardian");
      var1.registerSimple(var2, "minecraft:ender_crystal");
      registerMob(var1, var2, "minecraft:ender_dragon");
      var1.register(var2, "minecraft:enderman", (var1x) -> {
         return DSL.optionalFields("carriedBlockState", References.BLOCK_STATE.in(var1), V100.equipment(var1));
      });
      registerMob(var1, var2, "minecraft:endermite");
      var1.registerSimple(var2, "minecraft:ender_pearl");
      var1.registerSimple(var2, "minecraft:evocation_fangs");
      registerMob(var1, var2, "minecraft:evocation_illager");
      var1.registerSimple(var2, "minecraft:eye_of_ender_signal");
      var1.register(var2, "minecraft:falling_block", (var1x) -> {
         return DSL.optionalFields("BlockState", References.BLOCK_STATE.in(var1), "TileEntityData", References.BLOCK_ENTITY.in(var1));
      });
      var1.registerSimple(var2, "minecraft:fireball");
      var1.register(var2, "minecraft:fireworks_rocket", (var1x) -> {
         return DSL.optionalFields("FireworksItem", References.ITEM_STACK.in(var1));
      });
      var1.register(var2, "minecraft:furnace_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1));
      });
      registerMob(var1, var2, "minecraft:ghast");
      registerMob(var1, var2, "minecraft:giant");
      registerMob(var1, var2, "minecraft:guardian");
      var1.register(var2, "minecraft:hopper_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1), "Items", DSL.list(References.ITEM_STACK.in(var1)));
      });
      var1.register(var2, "minecraft:horse", (var1x) -> {
         return DSL.optionalFields("ArmorItem", References.ITEM_STACK.in(var1), "SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      registerMob(var1, var2, "minecraft:husk");
      var1.registerSimple(var2, "minecraft:illusion_illager");
      var1.register(var2, "minecraft:item", (var1x) -> {
         return DSL.optionalFields("Item", References.ITEM_STACK.in(var1));
      });
      var1.register(var2, "minecraft:item_frame", (var1x) -> {
         return DSL.optionalFields("Item", References.ITEM_STACK.in(var1));
      });
      var1.registerSimple(var2, "minecraft:leash_knot");
      var1.register(var2, "minecraft:llama", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)), "SaddleItem", References.ITEM_STACK.in(var1), "DecorItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      var1.registerSimple(var2, "minecraft:llama_spit");
      registerMob(var1, var2, "minecraft:magma_cube");
      var1.register(var2, "minecraft:minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1));
      });
      registerMob(var1, var2, "minecraft:mooshroom");
      var1.register(var2, "minecraft:mule", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)), "SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      registerMob(var1, var2, "minecraft:ocelot");
      var1.registerSimple(var2, "minecraft:painting");
      var1.registerSimple(var2, "minecraft:parrot");
      registerMob(var1, var2, "minecraft:pig");
      registerMob(var1, var2, "minecraft:polar_bear");
      var1.register(var2, "minecraft:potion", (var1x) -> {
         return DSL.optionalFields("Potion", References.ITEM_STACK.in(var1));
      });
      registerMob(var1, var2, "minecraft:rabbit");
      registerMob(var1, var2, "minecraft:sheep");
      registerMob(var1, var2, "minecraft:shulker");
      var1.registerSimple(var2, "minecraft:shulker_bullet");
      registerMob(var1, var2, "minecraft:silverfish");
      registerMob(var1, var2, "minecraft:skeleton");
      var1.register(var2, "minecraft:skeleton_horse", (var1x) -> {
         return DSL.optionalFields("SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      registerMob(var1, var2, "minecraft:slime");
      var1.registerSimple(var2, "minecraft:small_fireball");
      var1.registerSimple(var2, "minecraft:snowball");
      registerMob(var1, var2, "minecraft:snowman");
      var1.register(var2, "minecraft:spawner_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1), References.UNTAGGED_SPAWNER.in(var1));
      });
      var1.register(var2, "minecraft:spectral_arrow", (var1x) -> {
         return DSL.optionalFields("inBlockState", References.BLOCK_STATE.in(var1));
      });
      registerMob(var1, var2, "minecraft:spider");
      registerMob(var1, var2, "minecraft:squid");
      registerMob(var1, var2, "minecraft:stray");
      var1.registerSimple(var2, "minecraft:tnt");
      var1.register(var2, "minecraft:tnt_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1));
      });
      registerMob(var1, var2, "minecraft:vex");
      var1.register(var2, "minecraft:villager", (var1x) -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(var1)), "Offers", DSL.optionalFields("Recipes", DSL.list(References.VILLAGER_TRADE.in(var1))), V100.equipment(var1));
      });
      registerMob(var1, var2, "minecraft:villager_golem");
      registerMob(var1, var2, "minecraft:vindication_illager");
      registerMob(var1, var2, "minecraft:witch");
      registerMob(var1, var2, "minecraft:wither");
      registerMob(var1, var2, "minecraft:wither_skeleton");
      var1.registerSimple(var2, "minecraft:wither_skull");
      registerMob(var1, var2, "minecraft:wolf");
      var1.registerSimple(var2, "minecraft:xp_bottle");
      var1.registerSimple(var2, "minecraft:xp_orb");
      registerMob(var1, var2, "minecraft:zombie");
      var1.register(var2, "minecraft:zombie_horse", (var1x) -> {
         return DSL.optionalFields("SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      registerMob(var1, var2, "minecraft:zombie_pigman");
      var1.register(var2, "minecraft:zombie_villager", (var1x) -> {
         return DSL.optionalFields("Offers", DSL.optionalFields("Recipes", DSL.list(References.VILLAGER_TRADE.in(var1))), V100.equipment(var1));
      });
      return var2;
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      registerInventory(var1, var2, "minecraft:furnace");
      registerInventory(var1, var2, "minecraft:chest");
      registerInventory(var1, var2, "minecraft:trapped_chest");
      var1.registerSimple(var2, "minecraft:ender_chest");
      var1.register(var2, "minecraft:jukebox", (var1x) -> {
         return DSL.optionalFields("RecordItem", References.ITEM_STACK.in(var1));
      });
      registerInventory(var1, var2, "minecraft:dispenser");
      registerInventory(var1, var2, "minecraft:dropper");
      var1.registerSimple(var2, "minecraft:sign");
      var1.register(var2, "minecraft:mob_spawner", (var1x) -> {
         return References.UNTAGGED_SPAWNER.in(var1);
      });
      var1.register(var2, "minecraft:piston", (var1x) -> {
         return DSL.optionalFields("blockState", References.BLOCK_STATE.in(var1));
      });
      registerInventory(var1, var2, "minecraft:brewing_stand");
      var1.registerSimple(var2, "minecraft:enchanting_table");
      var1.registerSimple(var2, "minecraft:end_portal");
      var1.registerSimple(var2, "minecraft:beacon");
      var1.registerSimple(var2, "minecraft:skull");
      var1.registerSimple(var2, "minecraft:daylight_detector");
      registerInventory(var1, var2, "minecraft:hopper");
      var1.registerSimple(var2, "minecraft:comparator");
      var1.registerSimple(var2, "minecraft:banner");
      var1.registerSimple(var2, "minecraft:structure_block");
      var1.registerSimple(var2, "minecraft:end_gateway");
      var1.registerSimple(var2, "minecraft:command_block");
      registerInventory(var1, var2, "minecraft:shulker_box");
      var1.registerSimple(var2, "minecraft:bed");
      return var2;
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      var1.registerType(false, References.LEVEL, DSL::remainder);
      var1.registerType(false, References.RECIPE, () -> {
         return DSL.constType(namespacedString());
      });
      var1.registerType(false, References.PLAYER, () -> {
         return DSL.optionalFields(new Pair[]{Pair.of("RootVehicle", DSL.optionalFields("Entity", References.ENTITY_TREE.in(var1))), Pair.of("Inventory", DSL.list(References.ITEM_STACK.in(var1))), Pair.of("EnderItems", DSL.list(References.ITEM_STACK.in(var1))), Pair.of("ShoulderEntityLeft", References.ENTITY_TREE.in(var1)), Pair.of("ShoulderEntityRight", References.ENTITY_TREE.in(var1)), Pair.of("recipeBook", DSL.optionalFields("recipes", DSL.list(References.RECIPE.in(var1)), "toBeDisplayed", DSL.list(References.RECIPE.in(var1))))});
      });
      var1.registerType(false, References.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(var1)), "TileEntities", DSL.list(DSL.or(References.BLOCK_ENTITY.in(var1), DSL.remainder())), "TileTicks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(var1))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(References.BLOCK_STATE.in(var1))))));
      });
      var1.registerType(true, References.BLOCK_ENTITY, () -> {
         return DSL.optionalFields("components", References.DATA_COMPONENTS.in(var1), DSL.taggedChoiceLazy("id", namespacedString(), var3));
      });
      var1.registerType(true, References.ENTITY_TREE, () -> {
         return DSL.optionalFields("Passengers", DSL.list(References.ENTITY_TREE.in(var1)), References.ENTITY.in(var1));
      });
      var1.registerType(true, References.ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", namespacedString(), var2);
      });
      var1.registerType(true, References.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", References.ITEM_NAME.in(var1), "tag", DSL.optionalFields(new Pair[]{Pair.of("EntityTag", References.ENTITY_TREE.in(var1)), Pair.of("BlockEntityTag", References.BLOCK_ENTITY.in(var1)), Pair.of("CanDestroy", DSL.list(References.BLOCK_NAME.in(var1))), Pair.of("CanPlaceOn", DSL.list(References.BLOCK_NAME.in(var1))), Pair.of("Items", DSL.list(References.ITEM_STACK.in(var1))), Pair.of("ChargedProjectiles", DSL.list(References.ITEM_STACK.in(var1)))})), V705.ADD_NAMES, HookFunction.IDENTITY);
      });
      var1.registerType(false, References.HOTBAR, () -> {
         return DSL.compoundList(DSL.list(References.ITEM_STACK.in(var1)));
      });
      var1.registerType(false, References.OPTIONS, DSL::remainder);
      var1.registerType(false, References.STRUCTURE, () -> {
         return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", References.ENTITY_TREE.in(var1))), "blocks", DSL.list(DSL.optionalFields("nbt", References.BLOCK_ENTITY.in(var1))), "palette", DSL.list(References.BLOCK_STATE.in(var1)));
      });
      var1.registerType(false, References.BLOCK_NAME, () -> {
         return DSL.constType(namespacedString());
      });
      var1.registerType(false, References.ITEM_NAME, () -> {
         return DSL.constType(namespacedString());
      });
      var1.registerType(false, References.BLOCK_STATE, DSL::remainder);
      var1.registerType(false, References.FLAT_BLOCK_STATE, DSL::remainder);
      Supplier var4 = () -> {
         return DSL.compoundList(References.ITEM_NAME.in(var1), DSL.constType(DSL.intType()));
      };
      var1.registerType(false, References.STATS, () -> {
         return DSL.optionalFields("stats", DSL.optionalFields(new Pair[]{Pair.of("minecraft:mined", DSL.compoundList(References.BLOCK_NAME.in(var1), DSL.constType(DSL.intType()))), Pair.of("minecraft:crafted", (TypeTemplate)var4.get()), Pair.of("minecraft:used", (TypeTemplate)var4.get()), Pair.of("minecraft:broken", (TypeTemplate)var4.get()), Pair.of("minecraft:picked_up", (TypeTemplate)var4.get()), Pair.of("minecraft:dropped", (TypeTemplate)var4.get()), Pair.of("minecraft:killed", DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.intType()))), Pair.of("minecraft:killed_by", DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.intType()))), Pair.of("minecraft:custom", DSL.compoundList(DSL.constType(namespacedString()), DSL.constType(DSL.intType())))}));
      });
      var1.registerType(false, References.SAVED_DATA_COMMAND_STORAGE, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA_FORCED_CHUNKS, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA_MAP_DATA, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA_MAP_INDEX, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA_RAIDS, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA_RANDOM_SEQUENCES, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA_SCOREBOARD, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("Objectives", DSL.list(References.OBJECTIVE.in(var1)), "Teams", DSL.list(References.TEAM.in(var1))));
      });
      var1.registerType(false, References.SAVED_DATA_STRUCTURE_FEATURE_INDICES, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(References.STRUCTURE_FEATURE.in(var1))));
      });
      var1.registerType(false, References.STRUCTURE_FEATURE, DSL::remainder);
      Map var5 = V1451_6.createCriterionTypes(var1);
      var1.registerType(false, References.OBJECTIVE, () -> {
         return DSL.hook(DSL.optionalFields("CriteriaType", DSL.taggedChoiceLazy("type", DSL.string(), var5)), V1451_6.UNPACK_OBJECTIVE_ID, V1451_6.REPACK_OBJECTIVE_ID);
      });
      var1.registerType(false, References.TEAM, DSL::remainder);
      var1.registerType(true, References.UNTAGGED_SPAWNER, () -> {
         return DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", References.ENTITY_TREE.in(var1))), "SpawnData", References.ENTITY_TREE.in(var1));
      });
      var1.registerType(false, References.ADVANCEMENTS, () -> {
         return DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(References.BIOME.in(var1), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.string()))));
      });
      var1.registerType(false, References.BIOME, () -> {
         return DSL.constType(namespacedString());
      });
      var1.registerType(false, References.ENTITY_NAME, () -> {
         return DSL.constType(namespacedString());
      });
      var1.registerType(false, References.POI_CHUNK, DSL::remainder);
      var1.registerType(false, References.WORLD_GEN_SETTINGS, DSL::remainder);
      var1.registerType(false, References.ENTITY_CHUNK, () -> {
         return DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(var1)));
      });
      var1.registerType(true, References.DATA_COMPONENTS, DSL::remainder);
      var1.registerType(true, References.VILLAGER_TRADE, () -> {
         return DSL.optionalFields("buy", References.ITEM_STACK.in(var1), "buyB", References.ITEM_STACK.in(var1), "sell", References.ITEM_STACK.in(var1));
      });
      var1.registerType(true, References.PARTICLE, () -> {
         return DSL.constType(DSL.string());
      });
   }
}
