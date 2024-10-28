package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V705 extends NamespacedSchema {
   static final Map<String, String> ITEM_TO_ENTITY = ImmutableMap.builder().put("minecraft:armor_stand", "minecraft:armor_stand").put("minecraft:painting", "minecraft:painting").put("minecraft:armadillo_spawn_egg", "minecraft:armadillo").put("minecraft:allay_spawn_egg", "minecraft:allay").put("minecraft:axolotl_spawn_egg", "minecraft:axolotl").put("minecraft:bat_spawn_egg", "minecraft:bat").put("minecraft:bee_spawn_egg", "minecraft:bee").put("minecraft:blaze_spawn_egg", "minecraft:blaze").put("minecraft:bogged_spawn_egg", "minecraft:bogged").put("minecraft:breeze_spawn_egg", "minecraft:breeze").put("minecraft:cat_spawn_egg", "minecraft:cat").put("minecraft:camel_spawn_egg", "minecraft:camel").put("minecraft:cave_spider_spawn_egg", "minecraft:cave_spider").put("minecraft:chicken_spawn_egg", "minecraft:chicken").put("minecraft:cod_spawn_egg", "minecraft:cod").put("minecraft:cow_spawn_egg", "minecraft:cow").put("minecraft:creeper_spawn_egg", "minecraft:creeper").put("minecraft:dolphin_spawn_egg", "minecraft:dolphin").put("minecraft:donkey_spawn_egg", "minecraft:donkey").put("minecraft:drowned_spawn_egg", "minecraft:drowned").put("minecraft:elder_guardian_spawn_egg", "minecraft:elder_guardian").put("minecraft:ender_dragon_spawn_egg", "minecraft:ender_dragon").put("minecraft:enderman_spawn_egg", "minecraft:enderman").put("minecraft:endermite_spawn_egg", "minecraft:endermite").put("minecraft:evoker_spawn_egg", "minecraft:evoker").put("minecraft:fox_spawn_egg", "minecraft:fox").put("minecraft:frog_spawn_egg", "minecraft:frog").put("minecraft:ghast_spawn_egg", "minecraft:ghast").put("minecraft:glow_squid_spawn_egg", "minecraft:glow_squid").put("minecraft:goat_spawn_egg", "minecraft:goat").put("minecraft:guardian_spawn_egg", "minecraft:guardian").put("minecraft:hoglin_spawn_egg", "minecraft:hoglin").put("minecraft:horse_spawn_egg", "minecraft:horse").put("minecraft:husk_spawn_egg", "minecraft:husk").put("minecraft:iron_golem_spawn_egg", "minecraft:iron_golem").put("minecraft:llama_spawn_egg", "minecraft:llama").put("minecraft:magma_cube_spawn_egg", "minecraft:magma_cube").put("minecraft:mooshroom_spawn_egg", "minecraft:mooshroom").put("minecraft:mule_spawn_egg", "minecraft:mule").put("minecraft:ocelot_spawn_egg", "minecraft:ocelot").put("minecraft:panda_spawn_egg", "minecraft:panda").put("minecraft:parrot_spawn_egg", "minecraft:parrot").put("minecraft:phantom_spawn_egg", "minecraft:phantom").put("minecraft:pig_spawn_egg", "minecraft:pig").put("minecraft:piglin_spawn_egg", "minecraft:piglin").put("minecraft:piglin_brute_spawn_egg", "minecraft:piglin_brute").put("minecraft:pillager_spawn_egg", "minecraft:pillager").put("minecraft:polar_bear_spawn_egg", "minecraft:polar_bear").put("minecraft:pufferfish_spawn_egg", "minecraft:pufferfish").put("minecraft:rabbit_spawn_egg", "minecraft:rabbit").put("minecraft:ravager_spawn_egg", "minecraft:ravager").put("minecraft:salmon_spawn_egg", "minecraft:salmon").put("minecraft:sheep_spawn_egg", "minecraft:sheep").put("minecraft:shulker_spawn_egg", "minecraft:shulker").put("minecraft:silverfish_spawn_egg", "minecraft:silverfish").put("minecraft:skeleton_spawn_egg", "minecraft:skeleton").put("minecraft:skeleton_horse_spawn_egg", "minecraft:skeleton_horse").put("minecraft:slime_spawn_egg", "minecraft:slime").put("minecraft:sniffer_spawn_egg", "minecraft:sniffer").put("minecraft:snow_golem_spawn_egg", "minecraft:snow_golem").put("minecraft:spider_spawn_egg", "minecraft:spider").put("minecraft:squid_spawn_egg", "minecraft:squid").put("minecraft:stray_spawn_egg", "minecraft:stray").put("minecraft:strider_spawn_egg", "minecraft:strider").put("minecraft:tadpole_spawn_egg", "minecraft:tadpole").put("minecraft:trader_llama_spawn_egg", "minecraft:trader_llama").put("minecraft:tropical_fish_spawn_egg", "minecraft:tropical_fish").put("minecraft:turtle_spawn_egg", "minecraft:turtle").put("minecraft:vex_spawn_egg", "minecraft:vex").put("minecraft:villager_spawn_egg", "minecraft:villager").put("minecraft:vindicator_spawn_egg", "minecraft:vindicator").put("minecraft:wandering_trader_spawn_egg", "minecraft:wandering_trader").put("minecraft:warden_spawn_egg", "minecraft:warden").put("minecraft:witch_spawn_egg", "minecraft:witch").put("minecraft:wither_spawn_egg", "minecraft:wither").put("minecraft:wither_skeleton_spawn_egg", "minecraft:wither_skeleton").put("minecraft:wolf_spawn_egg", "minecraft:wolf").put("minecraft:zoglin_spawn_egg", "minecraft:zoglin").put("minecraft:zombie_spawn_egg", "minecraft:zombie").put("minecraft:zombie_horse_spawn_egg", "minecraft:zombie_horse").put("minecraft:zombie_villager_spawn_egg", "minecraft:zombie_villager").put("minecraft:zombified_piglin_spawn_egg", "minecraft:zombified_piglin").put("minecraft:item_frame", "minecraft:item_frame").put("minecraft:boat", "minecraft:oak_boat").put("minecraft:oak_boat", "minecraft:oak_boat").put("minecraft:oak_chest_boat", "minecraft:oak_chest_boat").put("minecraft:spruce_boat", "minecraft:spruce_boat").put("minecraft:spruce_chest_boat", "minecraft:spruce_chest_boat").put("minecraft:birch_boat", "minecraft:birch_boat").put("minecraft:birch_chest_boat", "minecraft:birch_chest_boat").put("minecraft:jungle_boat", "minecraft:jungle_boat").put("minecraft:jungle_chest_boat", "minecraft:jungle_chest_boat").put("minecraft:acacia_boat", "minecraft:acacia_boat").put("minecraft:acacia_chest_boat", "minecraft:acacia_chest_boat").put("minecraft:cherry_boat", "minecraft:cherry_boat").put("minecraft:cherry_chest_boat", "minecraft:cherry_chest_boat").put("minecraft:dark_oak_boat", "minecraft:dark_oak_boat").put("minecraft:dark_oak_chest_boat", "minecraft:dark_oak_chest_boat").put("minecraft:mangrove_boat", "minecraft:mangrove_boat").put("minecraft:mangrove_chest_boat", "minecraft:mangrove_chest_boat").put("minecraft:bamboo_raft", "minecraft:bamboo_raft").put("minecraft:bamboo_chest_raft", "minecraft:bamboo_chest_raft").put("minecraft:minecart", "minecraft:minecart").put("minecraft:chest_minecart", "minecraft:chest_minecart").put("minecraft:furnace_minecart", "minecraft:furnace_minecart").put("minecraft:tnt_minecart", "minecraft:tnt_minecart").put("minecraft:hopper_minecart", "minecraft:hopper_minecart").build();
   protected static final Hook.HookFunction ADD_NAMES = new Hook.HookFunction() {
      public <T> T apply(DynamicOps<T> var1, T var2) {
         return V99.addNames(new Dynamic(var1, var2), V704.ITEM_TO_BLOCKENTITY, V705.ITEM_TO_ENTITY);
      }
   };

   public V705(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static void registerMob(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return V100.equipment(var0);
      });
   }

   protected static void registerThrowableProjectile(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(var0));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      var1.register(var2, "minecraft:area_effect_cloud", (var1x) -> {
         return DSL.optionalFields("Particle", References.PARTICLE.in(var1));
      });
      registerMob(var1, var2, "minecraft:armor_stand");
      var1.register(var2, "minecraft:arrow", (var1x) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(var1));
      });
      registerMob(var1, var2, "minecraft:bat");
      registerMob(var1, var2, "minecraft:blaze");
      var1.registerSimple(var2, "minecraft:boat");
      registerMob(var1, var2, "minecraft:cave_spider");
      var1.register(var2, "minecraft:chest_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1), "Items", DSL.list(References.ITEM_STACK.in(var1)));
      });
      registerMob(var1, var2, "minecraft:chicken");
      var1.register(var2, "minecraft:commandblock_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1));
      });
      registerMob(var1, var2, "minecraft:cow");
      registerMob(var1, var2, "minecraft:creeper");
      var1.register(var2, "minecraft:donkey", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)), "SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      var1.registerSimple(var2, "minecraft:dragon_fireball");
      registerThrowableProjectile(var1, var2, "minecraft:egg");
      registerMob(var1, var2, "minecraft:elder_guardian");
      var1.registerSimple(var2, "minecraft:ender_crystal");
      registerMob(var1, var2, "minecraft:ender_dragon");
      var1.register(var2, "minecraft:enderman", (var1x) -> {
         return DSL.optionalFields("carried", References.BLOCK_NAME.in(var1), V100.equipment(var1));
      });
      registerMob(var1, var2, "minecraft:endermite");
      registerThrowableProjectile(var1, var2, "minecraft:ender_pearl");
      var1.registerSimple(var2, "minecraft:eye_of_ender_signal");
      var1.register(var2, "minecraft:falling_block", (var1x) -> {
         return DSL.optionalFields("Block", References.BLOCK_NAME.in(var1), "TileEntityData", References.BLOCK_ENTITY.in(var1));
      });
      registerThrowableProjectile(var1, var2, "minecraft:fireball");
      var1.register(var2, "minecraft:fireworks_rocket", (var1x) -> {
         return DSL.optionalFields("FireworksItem", References.ITEM_STACK.in(var1));
      });
      var1.register(var2, "minecraft:furnace_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1));
      });
      registerMob(var1, var2, "minecraft:ghast");
      registerMob(var1, var2, "minecraft:giant");
      registerMob(var1, var2, "minecraft:guardian");
      var1.register(var2, "minecraft:hopper_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1), "Items", DSL.list(References.ITEM_STACK.in(var1)));
      });
      var1.register(var2, "minecraft:horse", (var1x) -> {
         return DSL.optionalFields("ArmorItem", References.ITEM_STACK.in(var1), "SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      registerMob(var1, var2, "minecraft:husk");
      var1.register(var2, "minecraft:item", (var1x) -> {
         return DSL.optionalFields("Item", References.ITEM_STACK.in(var1));
      });
      var1.register(var2, "minecraft:item_frame", (var1x) -> {
         return DSL.optionalFields("Item", References.ITEM_STACK.in(var1));
      });
      var1.registerSimple(var2, "minecraft:leash_knot");
      registerMob(var1, var2, "minecraft:magma_cube");
      var1.register(var2, "minecraft:minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1));
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
         return DSL.optionalFields("Potion", References.ITEM_STACK.in(var1), "inTile", References.BLOCK_NAME.in(var1));
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
      registerThrowableProjectile(var1, var2, "minecraft:small_fireball");
      registerThrowableProjectile(var1, var2, "minecraft:snowball");
      registerMob(var1, var2, "minecraft:snowman");
      var1.register(var2, "minecraft:spawner_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1), References.UNTAGGED_SPAWNER.in(var1));
      });
      var1.register(var2, "minecraft:spectral_arrow", (var1x) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(var1));
      });
      registerMob(var1, var2, "minecraft:spider");
      registerMob(var1, var2, "minecraft:squid");
      registerMob(var1, var2, "minecraft:stray");
      var1.registerSimple(var2, "minecraft:tnt");
      var1.register(var2, "minecraft:tnt_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1));
      });
      var1.register(var2, "minecraft:villager", (var1x) -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(var1)), "Offers", DSL.optionalFields("Recipes", DSL.list(References.VILLAGER_TRADE.in(var1))), V100.equipment(var1));
      });
      registerMob(var1, var2, "minecraft:villager_golem");
      registerMob(var1, var2, "minecraft:witch");
      registerMob(var1, var2, "minecraft:wither");
      registerMob(var1, var2, "minecraft:wither_skeleton");
      registerThrowableProjectile(var1, var2, "minecraft:wither_skull");
      registerMob(var1, var2, "minecraft:wolf");
      registerThrowableProjectile(var1, var2, "minecraft:xp_bottle");
      var1.registerSimple(var2, "minecraft:xp_orb");
      registerMob(var1, var2, "minecraft:zombie");
      var1.register(var2, "minecraft:zombie_horse", (var1x) -> {
         return DSL.optionalFields("SaddleItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      registerMob(var1, var2, "minecraft:zombie_pigman");
      var1.register(var2, "minecraft:zombie_villager", (var1x) -> {
         return DSL.optionalFields("Offers", DSL.optionalFields("Recipes", DSL.list(References.VILLAGER_TRADE.in(var1))), V100.equipment(var1));
      });
      var1.registerSimple(var2, "minecraft:evocation_fangs");
      registerMob(var1, var2, "minecraft:evocation_illager");
      var1.registerSimple(var2, "minecraft:illusion_illager");
      var1.register(var2, "minecraft:llama", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)), "SaddleItem", References.ITEM_STACK.in(var1), "DecorItem", References.ITEM_STACK.in(var1), V100.equipment(var1));
      });
      var1.registerSimple(var2, "minecraft:llama_spit");
      registerMob(var1, var2, "minecraft:vex");
      registerMob(var1, var2, "minecraft:vindication_illager");
      return var2;
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(true, References.ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", namespacedString(), var2);
      });
      var1.registerType(true, References.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", References.ITEM_NAME.in(var1), "tag", DSL.optionalFields(new Pair[]{Pair.of("EntityTag", References.ENTITY_TREE.in(var1)), Pair.of("BlockEntityTag", References.BLOCK_ENTITY.in(var1)), Pair.of("CanDestroy", DSL.list(References.BLOCK_NAME.in(var1))), Pair.of("CanPlaceOn", DSL.list(References.BLOCK_NAME.in(var1))), Pair.of("Items", DSL.list(References.ITEM_STACK.in(var1))), Pair.of("ChargedProjectiles", DSL.list(References.ITEM_STACK.in(var1)))})), ADD_NAMES, HookFunction.IDENTITY);
      });
   }
}
