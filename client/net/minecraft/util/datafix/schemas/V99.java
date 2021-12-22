package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class V99 extends Schema {
   private static final Logger LOGGER = LogManager.getLogger();
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
   protected static final HookFunction ADD_NAMES = new HookFunction() {
      public <T> T apply(DynamicOps<T> var1, T var2) {
         return V99.addNames(new Dynamic(var1, var2), V99.ITEM_TO_BLOCKENTITY, "ArmorStand");
      }
   };

   public V99(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static TypeTemplate equipment(Schema var0) {
      return DSL.optionalFields("Equipment", DSL.list(References.ITEM_STACK.in(var0)));
   }

   protected static void registerMob(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return equipment(var0);
      });
   }

   protected static void registerThrowableProjectile(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(var0));
      });
   }

   protected static void registerMinecart(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var0));
      });
   }

   protected static void registerInventory(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var0)));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      var1.register(var2, "Item", (var1x) -> {
         return DSL.optionalFields("Item", References.ITEM_STACK.in(var1));
      });
      var1.registerSimple(var2, "XPOrb");
      registerThrowableProjectile(var1, var2, "ThrownEgg");
      var1.registerSimple(var2, "LeashKnot");
      var1.registerSimple(var2, "Painting");
      var1.register(var2, "Arrow", (var1x) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(var1));
      });
      var1.register(var2, "TippedArrow", (var1x) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(var1));
      });
      var1.register(var2, "SpectralArrow", (var1x) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(var1));
      });
      registerThrowableProjectile(var1, var2, "Snowball");
      registerThrowableProjectile(var1, var2, "Fireball");
      registerThrowableProjectile(var1, var2, "SmallFireball");
      registerThrowableProjectile(var1, var2, "ThrownEnderpearl");
      var1.registerSimple(var2, "EyeOfEnderSignal");
      var1.register(var2, "ThrownPotion", (var1x) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(var1), "Potion", References.ITEM_STACK.in(var1));
      });
      registerThrowableProjectile(var1, var2, "ThrownExpBottle");
      var1.register(var2, "ItemFrame", (var1x) -> {
         return DSL.optionalFields("Item", References.ITEM_STACK.in(var1));
      });
      registerThrowableProjectile(var1, var2, "WitherSkull");
      var1.registerSimple(var2, "PrimedTnt");
      var1.register(var2, "FallingSand", (var1x) -> {
         return DSL.optionalFields("Block", References.BLOCK_NAME.in(var1), "TileEntityData", References.BLOCK_ENTITY.in(var1));
      });
      var1.register(var2, "FireworksRocketEntity", (var1x) -> {
         return DSL.optionalFields("FireworksItem", References.ITEM_STACK.in(var1));
      });
      var1.registerSimple(var2, "Boat");
      var1.register(var2, "Minecart", () -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1), "Items", DSL.list(References.ITEM_STACK.in(var1)));
      });
      registerMinecart(var1, var2, "MinecartRideable");
      var1.register(var2, "MinecartChest", (var1x) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1), "Items", DSL.list(References.ITEM_STACK.in(var1)));
      });
      registerMinecart(var1, var2, "MinecartFurnace");
      registerMinecart(var1, var2, "MinecartTNT");
      var1.register(var2, "MinecartSpawner", () -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1), References.UNTAGGED_SPAWNER.in(var1));
      });
      var1.register(var2, "MinecartHopper", (var1x) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(var1), "Items", DSL.list(References.ITEM_STACK.in(var1)));
      });
      registerMinecart(var1, var2, "MinecartCommandBlock");
      registerMob(var1, var2, "ArmorStand");
      registerMob(var1, var2, "Creeper");
      registerMob(var1, var2, "Skeleton");
      registerMob(var1, var2, "Spider");
      registerMob(var1, var2, "Giant");
      registerMob(var1, var2, "Zombie");
      registerMob(var1, var2, "Slime");
      registerMob(var1, var2, "Ghast");
      registerMob(var1, var2, "PigZombie");
      var1.register(var2, "Enderman", (var1x) -> {
         return DSL.optionalFields("carried", References.BLOCK_NAME.in(var1), equipment(var1));
      });
      registerMob(var1, var2, "CaveSpider");
      registerMob(var1, var2, "Silverfish");
      registerMob(var1, var2, "Blaze");
      registerMob(var1, var2, "LavaSlime");
      registerMob(var1, var2, "EnderDragon");
      registerMob(var1, var2, "WitherBoss");
      registerMob(var1, var2, "Bat");
      registerMob(var1, var2, "Witch");
      registerMob(var1, var2, "Endermite");
      registerMob(var1, var2, "Guardian");
      registerMob(var1, var2, "Pig");
      registerMob(var1, var2, "Sheep");
      registerMob(var1, var2, "Cow");
      registerMob(var1, var2, "Chicken");
      registerMob(var1, var2, "Squid");
      registerMob(var1, var2, "Wolf");
      registerMob(var1, var2, "MushroomCow");
      registerMob(var1, var2, "SnowMan");
      registerMob(var1, var2, "Ozelot");
      registerMob(var1, var2, "VillagerGolem");
      var1.register(var2, "EntityHorse", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)), "ArmorItem", References.ITEM_STACK.in(var1), "SaddleItem", References.ITEM_STACK.in(var1), equipment(var1));
      });
      registerMob(var1, var2, "Rabbit");
      var1.register(var2, "Villager", (var1x) -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(var1)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", References.ITEM_STACK.in(var1), "buyB", References.ITEM_STACK.in(var1), "sell", References.ITEM_STACK.in(var1)))), equipment(var1));
      });
      var1.registerSimple(var2, "EnderCrystal");
      var1.registerSimple(var2, "AreaEffectCloud");
      var1.registerSimple(var2, "ShulkerBullet");
      registerMob(var1, var2, "Shulker");
      return var2;
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      registerInventory(var1, var2, "Furnace");
      registerInventory(var1, var2, "Chest");
      var1.registerSimple(var2, "EnderChest");
      var1.register(var2, "RecordPlayer", (var1x) -> {
         return DSL.optionalFields("RecordItem", References.ITEM_STACK.in(var1));
      });
      registerInventory(var1, var2, "Trap");
      registerInventory(var1, var2, "Dropper");
      var1.registerSimple(var2, "Sign");
      var1.register(var2, "MobSpawner", (var1x) -> {
         return References.UNTAGGED_SPAWNER.in(var1);
      });
      var1.registerSimple(var2, "Music");
      var1.registerSimple(var2, "Piston");
      registerInventory(var1, var2, "Cauldron");
      var1.registerSimple(var2, "EnchantTable");
      var1.registerSimple(var2, "Airportal");
      var1.registerSimple(var2, "Control");
      var1.registerSimple(var2, "Beacon");
      var1.registerSimple(var2, "Skull");
      var1.registerSimple(var2, "DLDetector");
      registerInventory(var1, var2, "Hopper");
      var1.registerSimple(var2, "Comparator");
      var1.register(var2, "FlowerPot", (var1x) -> {
         return DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), References.ITEM_NAME.in(var1)));
      });
      var1.registerSimple(var2, "Banner");
      var1.registerSimple(var2, "Structure");
      var1.registerSimple(var2, "EndGateway");
      return var2;
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      var1.registerType(false, References.LEVEL, DSL::remainder);
      var1.registerType(false, References.PLAYER, () -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(var1)), "EnderItems", DSL.list(References.ITEM_STACK.in(var1)));
      });
      var1.registerType(false, References.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(var1)), "TileEntities", DSL.list(References.BLOCK_ENTITY.in(var1)), "TileTicks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(var1)))));
      });
      var1.registerType(true, References.BLOCK_ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.string(), var3);
      });
      var1.registerType(true, References.ENTITY_TREE, () -> {
         return DSL.optionalFields("Riding", References.ENTITY_TREE.in(var1), References.ENTITY.in(var1));
      });
      var1.registerType(false, References.ENTITY_NAME, () -> {
         return DSL.constType(NamespacedSchema.namespacedString());
      });
      var1.registerType(true, References.ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.string(), var2);
      });
      var1.registerType(true, References.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", DSL.or(DSL.constType(DSL.intType()), References.ITEM_NAME.in(var1)), "tag", DSL.optionalFields("EntityTag", References.ENTITY_TREE.in(var1), "BlockEntityTag", References.BLOCK_ENTITY.in(var1), "CanDestroy", DSL.list(References.BLOCK_NAME.in(var1)), "CanPlaceOn", DSL.list(References.BLOCK_NAME.in(var1)), "Items", DSL.list(References.ITEM_STACK.in(var1)))), ADD_NAMES, HookFunction.IDENTITY);
      });
      var1.registerType(false, References.OPTIONS, DSL::remainder);
      var1.registerType(false, References.BLOCK_NAME, () -> {
         return DSL.or(DSL.constType(DSL.intType()), DSL.constType(NamespacedSchema.namespacedString()));
      });
      var1.registerType(false, References.ITEM_NAME, () -> {
         return DSL.constType(NamespacedSchema.namespacedString());
      });
      var1.registerType(false, References.STATS, DSL::remainder);
      var1.registerType(false, References.SAVED_DATA, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(References.STRUCTURE_FEATURE.in(var1)), "Objectives", DSL.list(References.OBJECTIVE.in(var1)), "Teams", DSL.list(References.TEAM.in(var1))));
      });
      var1.registerType(false, References.STRUCTURE_FEATURE, DSL::remainder);
      var1.registerType(false, References.OBJECTIVE, DSL::remainder);
      var1.registerType(false, References.TEAM, DSL::remainder);
      var1.registerType(true, References.UNTAGGED_SPAWNER, DSL::remainder);
      var1.registerType(false, References.POI_CHUNK, DSL::remainder);
      var1.registerType(true, References.WORLD_GEN_SETTINGS, DSL::remainder);
      var1.registerType(false, References.ENTITY_CHUNK, () -> {
         return DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(var1)));
      });
   }

   protected static <T> T addNames(Dynamic<T> var0, Map<String, String> var1, String var2) {
      return var0.update("tag", (var3) -> {
         return var3.update("BlockEntityTag", (var2x) -> {
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
            String var3 = var0.get("id").asString("");
            return "minecraft:armor_stand".equals(NamespacedSchema.ensureNamespaced(var3)) ? var2x.set("id", var0.createString(var2)) : var2x;
         });
      }).getValue();
   }
}
