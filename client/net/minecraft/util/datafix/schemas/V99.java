package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import org.slf4j.Logger;

public class V99 extends Schema {
   private static final Logger LOGGER = LogUtils.getLogger();
   static final Map<String, String> ITEM_TO_BLOCKENTITY = (Map)DataFixUtils.make(Maps.newHashMap(), (var0) -> {
      var0.put("minecraft:furnace", "Furnace");
      var0.put("minecraft:lit_furnace", "Furnace");
      var0.put("minecraft:chest", "Chest");
      var0.put("minecraft:trapped_chest", "Chest");
      var0.put("minecraft:ender_chest", "EnderChest");
      var0.put("minecraft:jukebox", "RecordPlayer");
      var0.put("minecraft:dispenser", "Trap");
      var0.put("minecraft:dropper", "Dropper");
      var0.put("minecraft:sign", "Sign");
      var0.put("minecraft:mob_spawner", "MobSpawner");
      var0.put("minecraft:noteblock", "Music");
      var0.put("minecraft:brewing_stand", "Cauldron");
      var0.put("minecraft:enhanting_table", "EnchantTable");
      var0.put("minecraft:command_block", "CommandBlock");
      var0.put("minecraft:beacon", "Beacon");
      var0.put("minecraft:skull", "Skull");
      var0.put("minecraft:daylight_detector", "DLDetector");
      var0.put("minecraft:hopper", "Hopper");
      var0.put("minecraft:banner", "Banner");
      var0.put("minecraft:flower_pot", "FlowerPot");
      var0.put("minecraft:repeating_command_block", "CommandBlock");
      var0.put("minecraft:chain_command_block", "CommandBlock");
      var0.put("minecraft:standing_sign", "Sign");
      var0.put("minecraft:wall_sign", "Sign");
      var0.put("minecraft:piston_head", "Piston");
      var0.put("minecraft:daylight_detector_inverted", "DLDetector");
      var0.put("minecraft:unpowered_comparator", "Comparator");
      var0.put("minecraft:powered_comparator", "Comparator");
      var0.put("minecraft:wall_banner", "Banner");
      var0.put("minecraft:standing_banner", "Banner");
      var0.put("minecraft:structure_block", "Structure");
      var0.put("minecraft:end_portal", "Airportal");
      var0.put("minecraft:end_gateway", "EndGateway");
      var0.put("minecraft:shield", "Banner");
   });
   public static final Map<String, String> ITEM_TO_ENTITY = Map.of("minecraft:armor_stand", "ArmorStand", "minecraft:painting", "Painting");
   protected static final Hook.HookFunction ADD_NAMES = new Hook.HookFunction() {
      public <T> T apply(DynamicOps<T> var1, T var2) {
         return (T)V99.addNames(new Dynamic(var1, var2), V99.ITEM_TO_BLOCKENTITY, V99.ITEM_TO_ENTITY);
      }
   };

   public V99(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static void registerThrowableProjectile(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> DSL.optionalFields("inTile", References.BLOCK_NAME.in(var0)));
   }

   protected static void registerMinecart(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var0)));
   }

   protected static void registerInventory(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var0))));
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      var1.register(var2, "Item", (var1x) -> DSL.optionalFields("Item", References.ITEM_STACK.in(var1)));
      var1.registerSimple(var2, "XPOrb");
      registerThrowableProjectile(var1, var2, "ThrownEgg");
      var1.registerSimple(var2, "LeashKnot");
      var1.registerSimple(var2, "Painting");
      var1.register(var2, "Arrow", (var1x) -> DSL.optionalFields("inTile", References.BLOCK_NAME.in(var1)));
      var1.register(var2, "TippedArrow", (var1x) -> DSL.optionalFields("inTile", References.BLOCK_NAME.in(var1)));
      var1.register(var2, "SpectralArrow", (var1x) -> DSL.optionalFields("inTile", References.BLOCK_NAME.in(var1)));
      registerThrowableProjectile(var1, var2, "Snowball");
      registerThrowableProjectile(var1, var2, "Fireball");
      registerThrowableProjectile(var1, var2, "SmallFireball");
      registerThrowableProjectile(var1, var2, "ThrownEnderpearl");
      var1.registerSimple(var2, "EyeOfEnderSignal");
      var1.register(var2, "ThrownPotion", (var1x) -> DSL.optionalFields("inTile", References.BLOCK_NAME.in(var1), "Potion", References.ITEM_STACK.in(var1)));
      registerThrowableProjectile(var1, var2, "ThrownExpBottle");
      var1.register(var2, "ItemFrame", (var1x) -> DSL.optionalFields("Item", References.ITEM_STACK.in(var1)));
      registerThrowableProjectile(var1, var2, "WitherSkull");
      var1.registerSimple(var2, "PrimedTnt");
      var1.register(var2, "FallingSand", (var1x) -> DSL.optionalFields("Block", References.BLOCK_NAME.in(var1), "TileEntityData", References.BLOCK_ENTITY.in(var1)));
      var1.register(var2, "FireworksRocketEntity", (var1x) -> DSL.optionalFields("FireworksItem", References.ITEM_STACK.in(var1)));
      var1.registerSimple(var2, "Boat");
      var1.register(var2, "Minecart", () -> DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1), "Items", DSL.list(References.ITEM_STACK.in(var1))));
      registerMinecart(var1, var2, "MinecartRideable");
      var1.register(var2, "MinecartChest", (var1x) -> DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1), "Items", DSL.list(References.ITEM_STACK.in(var1))));
      registerMinecart(var1, var2, "MinecartFurnace");
      registerMinecart(var1, var2, "MinecartTNT");
      var1.register(var2, "MinecartSpawner", () -> DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1), References.UNTAGGED_SPAWNER.in(var1)));
      var1.register(var2, "MinecartHopper", (var1x) -> DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1), "Items", DSL.list(References.ITEM_STACK.in(var1))));
      var1.register(var2, "MinecartCommandBlock", () -> DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1), "LastOutput", References.TEXT_COMPONENT.in(var1)));
      var1.registerSimple(var2, "ArmorStand");
      var1.registerSimple(var2, "Creeper");
      var1.registerSimple(var2, "Skeleton");
      var1.registerSimple(var2, "Spider");
      var1.registerSimple(var2, "Giant");
      var1.registerSimple(var2, "Zombie");
      var1.registerSimple(var2, "Slime");
      var1.registerSimple(var2, "Ghast");
      var1.registerSimple(var2, "PigZombie");
      var1.register(var2, "Enderman", (var1x) -> DSL.optionalFields("carried", References.BLOCK_NAME.in(var1)));
      var1.registerSimple(var2, "CaveSpider");
      var1.registerSimple(var2, "Silverfish");
      var1.registerSimple(var2, "Blaze");
      var1.registerSimple(var2, "LavaSlime");
      var1.registerSimple(var2, "EnderDragon");
      var1.registerSimple(var2, "WitherBoss");
      var1.registerSimple(var2, "Bat");
      var1.registerSimple(var2, "Witch");
      var1.registerSimple(var2, "Endermite");
      var1.registerSimple(var2, "Guardian");
      var1.registerSimple(var2, "Pig");
      var1.registerSimple(var2, "Sheep");
      var1.registerSimple(var2, "Cow");
      var1.registerSimple(var2, "Chicken");
      var1.registerSimple(var2, "Squid");
      var1.registerSimple(var2, "Wolf");
      var1.registerSimple(var2, "MushroomCow");
      var1.registerSimple(var2, "SnowMan");
      var1.registerSimple(var2, "Ozelot");
      var1.registerSimple(var2, "VillagerGolem");
      var1.register(var2, "EntityHorse", (var1x) -> DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)), "ArmorItem", References.ITEM_STACK.in(var1), "SaddleItem", References.ITEM_STACK.in(var1)));
      var1.registerSimple(var2, "Rabbit");
      var1.register(var2, "Villager", (var1x) -> DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(var1)), "Offers", DSL.optionalFields("Recipes", DSL.list(References.VILLAGER_TRADE.in(var1)))));
      var1.registerSimple(var2, "EnderCrystal");
      var1.register(var2, "AreaEffectCloud", (var1x) -> DSL.optionalFields("Particle", References.PARTICLE.in(var1)));
      var1.registerSimple(var2, "ShulkerBullet");
      var1.registerSimple(var2, "DragonFireball");
      var1.registerSimple(var2, "Shulker");
      return var2;
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      registerInventory(var1, var2, "Furnace");
      registerInventory(var1, var2, "Chest");
      var1.registerSimple(var2, "EnderChest");
      var1.register(var2, "RecordPlayer", (var1x) -> DSL.optionalFields("RecordItem", References.ITEM_STACK.in(var1)));
      registerInventory(var1, var2, "Trap");
      registerInventory(var1, var2, "Dropper");
      var1.register(var2, "Sign", () -> sign(var1));
      var1.register(var2, "MobSpawner", (var1x) -> References.UNTAGGED_SPAWNER.in(var1));
      var1.registerSimple(var2, "Music");
      var1.registerSimple(var2, "Piston");
      registerInventory(var1, var2, "Cauldron");
      var1.registerSimple(var2, "EnchantTable");
      var1.registerSimple(var2, "Airportal");
      var1.register(var2, "Control", () -> DSL.optionalFields("LastOutput", References.TEXT_COMPONENT.in(var1)));
      var1.registerSimple(var2, "Beacon");
      var1.register(var2, "Skull", () -> DSL.optionalFields("custom_name", References.TEXT_COMPONENT.in(var1)));
      var1.registerSimple(var2, "DLDetector");
      registerInventory(var1, var2, "Hopper");
      var1.registerSimple(var2, "Comparator");
      var1.register(var2, "FlowerPot", (var1x) -> DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), References.ITEM_NAME.in(var1))));
      var1.register(var2, "Banner", () -> DSL.optionalFields("CustomName", References.TEXT_COMPONENT.in(var1)));
      var1.registerSimple(var2, "Structure");
      var1.registerSimple(var2, "EndGateway");
      return var2;
   }

   public static TypeTemplate sign(Schema var0) {
      return DSL.optionalFields(new Pair[]{Pair.of("Text1", References.TEXT_COMPONENT.in(var0)), Pair.of("Text2", References.TEXT_COMPONENT.in(var0)), Pair.of("Text3", References.TEXT_COMPONENT.in(var0)), Pair.of("Text4", References.TEXT_COMPONENT.in(var0)), Pair.of("FilteredText1", References.TEXT_COMPONENT.in(var0)), Pair.of("FilteredText2", References.TEXT_COMPONENT.in(var0)), Pair.of("FilteredText3", References.TEXT_COMPONENT.in(var0)), Pair.of("FilteredText4", References.TEXT_COMPONENT.in(var0))});
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      var1.registerType(false, References.LEVEL, () -> DSL.optionalFields("CustomBossEvents", DSL.compoundList(DSL.optionalFields("Name", References.TEXT_COMPONENT.in(var1))), References.LIGHTWEIGHT_LEVEL.in(var1)));
      var1.registerType(false, References.LIGHTWEIGHT_LEVEL, DSL::remainder);
      var1.registerType(false, References.PLAYER, () -> DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(var1)), "EnderItems", DSL.list(References.ITEM_STACK.in(var1))));
      var1.registerType(false, References.CHUNK, () -> DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(var1)), "TileEntities", DSL.list(DSL.or(References.BLOCK_ENTITY.in(var1), DSL.remainder())), "TileTicks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(var1))))));
      var1.registerType(true, References.BLOCK_ENTITY, () -> DSL.optionalFields("components", References.DATA_COMPONENTS.in(var1), DSL.taggedChoiceLazy("id", DSL.string(), var3)));
      var1.registerType(true, References.ENTITY_TREE, () -> DSL.optionalFields("Riding", References.ENTITY_TREE.in(var1), References.ENTITY.in(var1)));
      var1.registerType(false, References.ENTITY_NAME, () -> DSL.constType(NamespacedSchema.namespacedString()));
      var1.registerType(true, References.ENTITY, () -> DSL.and(References.ENTITY_EQUIPMENT.in(var1), DSL.optionalFields("CustomName", DSL.constType(DSL.string()), DSL.taggedChoiceLazy("id", DSL.string(), var2))));
      var1.registerType(true, References.ITEM_STACK, () -> DSL.hook(DSL.optionalFields("id", DSL.or(DSL.constType(DSL.intType()), References.ITEM_NAME.in(var1)), "tag", itemStackTag(var1)), ADD_NAMES, HookFunction.IDENTITY));
      var1.registerType(false, References.OPTIONS, DSL::remainder);
      var1.registerType(false, References.BLOCK_NAME, () -> DSL.or(DSL.constType(DSL.intType()), DSL.constType(NamespacedSchema.namespacedString())));
      var1.registerType(false, References.ITEM_NAME, () -> DSL.constType(NamespacedSchema.namespacedString()));
      var1.registerType(false, References.STATS, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA_COMMAND_STORAGE, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA_TICKETS, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA_MAP_DATA, () -> DSL.optionalFields("data", DSL.optionalFields("banners", DSL.list(DSL.optionalFields("Name", References.TEXT_COMPONENT.in(var1))))));
      var1.registerType(false, References.SAVED_DATA_MAP_INDEX, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA_RAIDS, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA_RANDOM_SEQUENCES, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA_SCOREBOARD, () -> DSL.optionalFields("data", DSL.optionalFields("Objectives", DSL.list(References.OBJECTIVE.in(var1)), "Teams", DSL.list(References.TEAM.in(var1)), "PlayerScores", DSL.list(DSL.optionalFields("display", References.TEXT_COMPONENT.in(var1))))));
      var1.registerType(false, References.SAVED_DATA_STRUCTURE_FEATURE_INDICES, () -> DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(References.STRUCTURE_FEATURE.in(var1)))));
      var1.registerType(false, References.STRUCTURE_FEATURE, DSL::remainder);
      var1.registerType(false, References.OBJECTIVE, DSL::remainder);
      var1.registerType(false, References.TEAM, () -> DSL.optionalFields("MemberNamePrefix", References.TEXT_COMPONENT.in(var1), "MemberNameSuffix", References.TEXT_COMPONENT.in(var1), "DisplayName", References.TEXT_COMPONENT.in(var1)));
      var1.registerType(true, References.UNTAGGED_SPAWNER, DSL::remainder);
      var1.registerType(false, References.POI_CHUNK, DSL::remainder);
      var1.registerType(false, References.WORLD_GEN_SETTINGS, DSL::remainder);
      var1.registerType(false, References.ENTITY_CHUNK, () -> DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(var1))));
      var1.registerType(true, References.DATA_COMPONENTS, DSL::remainder);
      var1.registerType(true, References.VILLAGER_TRADE, () -> DSL.optionalFields("buy", References.ITEM_STACK.in(var1), "buyB", References.ITEM_STACK.in(var1), "sell", References.ITEM_STACK.in(var1)));
      var1.registerType(true, References.PARTICLE, () -> DSL.constType(DSL.string()));
      var1.registerType(true, References.TEXT_COMPONENT, () -> DSL.constType(DSL.string()));
      var1.registerType(false, References.STRUCTURE, () -> DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", References.ENTITY_TREE.in(var1))), "blocks", DSL.list(DSL.optionalFields("nbt", References.BLOCK_ENTITY.in(var1))), "palette", DSL.list(References.BLOCK_STATE.in(var1))));
      var1.registerType(false, References.BLOCK_STATE, DSL::remainder);
      var1.registerType(false, References.FLAT_BLOCK_STATE, DSL::remainder);
      var1.registerType(true, References.ENTITY_EQUIPMENT, () -> DSL.optional(DSL.field("Equipment", DSL.list(References.ITEM_STACK.in(var1)))));
   }

   public static TypeTemplate itemStackTag(Schema var0) {
      return DSL.optionalFields(new Pair[]{Pair.of("EntityTag", References.ENTITY_TREE.in(var0)), Pair.of("BlockEntityTag", References.BLOCK_ENTITY.in(var0)), Pair.of("CanDestroy", DSL.list(References.BLOCK_NAME.in(var0))), Pair.of("CanPlaceOn", DSL.list(References.BLOCK_NAME.in(var0))), Pair.of("Items", DSL.list(References.ITEM_STACK.in(var0))), Pair.of("ChargedProjectiles", DSL.list(References.ITEM_STACK.in(var0))), Pair.of("pages", DSL.list(References.TEXT_COMPONENT.in(var0))), Pair.of("filtered_pages", DSL.compoundList(References.TEXT_COMPONENT.in(var0))), Pair.of("display", DSL.optionalFields("Name", References.TEXT_COMPONENT.in(var0), "Lore", DSL.list(References.TEXT_COMPONENT.in(var0))))});
   }

   protected static <T> T addNames(Dynamic<T> var0, Map<String, String> var1, Map<String, String> var2) {
      return (T)var0.update("tag", (var3) -> var3.update("BlockEntityTag", (var2x) -> {
            String var3 = (String)var0.get("id").asString().result().map(NamespacedSchema::ensureNamespaced).orElse("minecraft:air");
            if (!"minecraft:air".equals(var3)) {
               String var4 = (String)var1.get(var3);
               if (var4 != null) {
                  return var2x.set("id", var0.createString(var4));
               }

               LOGGER.warn("Unable to resolve BlockEntity for ItemStack: {}", var3);
            }

            return var2x;
         }).update("EntityTag", (var2x) -> {
            if (var2x.get("id").result().isPresent()) {
               return var2x;
            } else {
               String var3 = NamespacedSchema.ensureNamespaced(var0.get("id").asString(""));
               String var4 = (String)var2.get(var3);
               return var4 != null ? var2x.set("id", var0.createString(var4)) : var2x;
            }
         })).getValue();
   }
}
