package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class V0099 extends Schema {
   private static final Logger field_206692_c = LogManager.getLogger();
   private static final Map<String, String> field_206693_d = (Map)DataFixUtils.make(Maps.newHashMap(), (var0) -> {
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
   protected static final HookFunction field_206691_b = new HookFunction() {
      public <T> T apply(DynamicOps<T> var1, T var2) {
         return V0099.func_209869_a(new Dynamic(var1, var2), V0099.field_206693_d, "ArmorStand");
      }
   };

   public V0099(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static TypeTemplate func_206658_a(Schema var0) {
      return DSL.optionalFields("Equipment", DSL.list(TypeReferences.field_211295_k.in(var0)));
   }

   protected static void func_206690_a(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return func_206658_a(var0);
      });
   }

   protected static void func_206668_b(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("inTile", TypeReferences.field_211300_p.in(var0));
      });
   }

   protected static void func_206674_c(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.field_211300_p.in(var0));
      });
   }

   protected static void func_206680_d(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var0)));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      var1.register(var2, "Item", (var1x) -> {
         return DSL.optionalFields("Item", TypeReferences.field_211295_k.in(var1));
      });
      var1.registerSimple(var2, "XPOrb");
      func_206668_b(var1, var2, "ThrownEgg");
      var1.registerSimple(var2, "LeashKnot");
      var1.registerSimple(var2, "Painting");
      var1.register(var2, "Arrow", (var1x) -> {
         return DSL.optionalFields("inTile", TypeReferences.field_211300_p.in(var1));
      });
      var1.register(var2, "TippedArrow", (var1x) -> {
         return DSL.optionalFields("inTile", TypeReferences.field_211300_p.in(var1));
      });
      var1.register(var2, "SpectralArrow", (var1x) -> {
         return DSL.optionalFields("inTile", TypeReferences.field_211300_p.in(var1));
      });
      func_206668_b(var1, var2, "Snowball");
      func_206668_b(var1, var2, "Fireball");
      func_206668_b(var1, var2, "SmallFireball");
      func_206668_b(var1, var2, "ThrownEnderpearl");
      var1.registerSimple(var2, "EyeOfEnderSignal");
      var1.register(var2, "ThrownPotion", (var1x) -> {
         return DSL.optionalFields("inTile", TypeReferences.field_211300_p.in(var1), "Potion", TypeReferences.field_211295_k.in(var1));
      });
      func_206668_b(var1, var2, "ThrownExpBottle");
      var1.register(var2, "ItemFrame", (var1x) -> {
         return DSL.optionalFields("Item", TypeReferences.field_211295_k.in(var1));
      });
      func_206668_b(var1, var2, "WitherSkull");
      var1.registerSimple(var2, "PrimedTnt");
      var1.register(var2, "FallingSand", (var1x) -> {
         return DSL.optionalFields("Block", TypeReferences.field_211300_p.in(var1), "TileEntityData", TypeReferences.field_211294_j.in(var1));
      });
      var1.register(var2, "FireworksRocketEntity", (var1x) -> {
         return DSL.optionalFields("FireworksItem", TypeReferences.field_211295_k.in(var1));
      });
      var1.registerSimple(var2, "Boat");
      var1.register(var2, "Minecart", () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.field_211300_p.in(var1), "Items", DSL.list(TypeReferences.field_211295_k.in(var1)));
      });
      func_206674_c(var1, var2, "MinecartRideable");
      var1.register(var2, "MinecartChest", (var1x) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.field_211300_p.in(var1), "Items", DSL.list(TypeReferences.field_211295_k.in(var1)));
      });
      func_206674_c(var1, var2, "MinecartFurnace");
      func_206674_c(var1, var2, "MinecartTNT");
      var1.register(var2, "MinecartSpawner", () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.field_211300_p.in(var1), TypeReferences.field_211302_r.in(var1));
      });
      var1.register(var2, "MinecartHopper", (var1x) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.field_211300_p.in(var1), "Items", DSL.list(TypeReferences.field_211295_k.in(var1)));
      });
      func_206674_c(var1, var2, "MinecartCommandBlock");
      func_206690_a(var1, var2, "ArmorStand");
      func_206690_a(var1, var2, "Creeper");
      func_206690_a(var1, var2, "Skeleton");
      func_206690_a(var1, var2, "Spider");
      func_206690_a(var1, var2, "Giant");
      func_206690_a(var1, var2, "Zombie");
      func_206690_a(var1, var2, "Slime");
      func_206690_a(var1, var2, "Ghast");
      func_206690_a(var1, var2, "PigZombie");
      var1.register(var2, "Enderman", (var1x) -> {
         return DSL.optionalFields("carried", TypeReferences.field_211300_p.in(var1), func_206658_a(var1));
      });
      func_206690_a(var1, var2, "CaveSpider");
      func_206690_a(var1, var2, "Silverfish");
      func_206690_a(var1, var2, "Blaze");
      func_206690_a(var1, var2, "LavaSlime");
      func_206690_a(var1, var2, "EnderDragon");
      func_206690_a(var1, var2, "WitherBoss");
      func_206690_a(var1, var2, "Bat");
      func_206690_a(var1, var2, "Witch");
      func_206690_a(var1, var2, "Endermite");
      func_206690_a(var1, var2, "Guardian");
      func_206690_a(var1, var2, "Pig");
      func_206690_a(var1, var2, "Sheep");
      func_206690_a(var1, var2, "Cow");
      func_206690_a(var1, var2, "Chicken");
      func_206690_a(var1, var2, "Squid");
      func_206690_a(var1, var2, "Wolf");
      func_206690_a(var1, var2, "MushroomCow");
      func_206690_a(var1, var2, "SnowMan");
      func_206690_a(var1, var2, "Ozelot");
      func_206690_a(var1, var2, "VillagerGolem");
      var1.register(var2, "EntityHorse", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var1)), "ArmorItem", TypeReferences.field_211295_k.in(var1), "SaddleItem", TypeReferences.field_211295_k.in(var1), func_206658_a(var1));
      });
      func_206690_a(var1, var2, "Rabbit");
      var1.register(var2, "Villager", (var1x) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.field_211295_k.in(var1)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.field_211295_k.in(var1), "buyB", TypeReferences.field_211295_k.in(var1), "sell", TypeReferences.field_211295_k.in(var1)))), func_206658_a(var1));
      });
      var1.registerSimple(var2, "EnderCrystal");
      var1.registerSimple(var2, "AreaEffectCloud");
      var1.registerSimple(var2, "ShulkerBullet");
      func_206690_a(var1, var2, "Shulker");
      return var2;
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      func_206680_d(var1, var2, "Furnace");
      func_206680_d(var1, var2, "Chest");
      var1.registerSimple(var2, "EnderChest");
      var1.register(var2, "RecordPlayer", (var1x) -> {
         return DSL.optionalFields("RecordItem", TypeReferences.field_211295_k.in(var1));
      });
      func_206680_d(var1, var2, "Trap");
      func_206680_d(var1, var2, "Dropper");
      var1.registerSimple(var2, "Sign");
      var1.register(var2, "MobSpawner", (var1x) -> {
         return TypeReferences.field_211302_r.in(var1);
      });
      var1.registerSimple(var2, "Music");
      var1.registerSimple(var2, "Piston");
      func_206680_d(var1, var2, "Cauldron");
      var1.registerSimple(var2, "EnchantTable");
      var1.registerSimple(var2, "Airportal");
      var1.registerSimple(var2, "Control");
      var1.registerSimple(var2, "Beacon");
      var1.registerSimple(var2, "Skull");
      var1.registerSimple(var2, "DLDetector");
      func_206680_d(var1, var2, "Hopper");
      var1.registerSimple(var2, "Comparator");
      var1.register(var2, "FlowerPot", (var1x) -> {
         return DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.field_211301_q.in(var1)));
      });
      var1.registerSimple(var2, "Banner");
      var1.registerSimple(var2, "Structure");
      var1.registerSimple(var2, "EndGateway");
      return var2;
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      var1.registerType(false, TypeReferences.field_211285_a, DSL::remainder);
      var1.registerType(false, TypeReferences.field_211286_b, () -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.field_211295_k.in(var1)), "EnderItems", DSL.list(TypeReferences.field_211295_k.in(var1)));
      });
      var1.registerType(false, TypeReferences.field_211287_c, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.field_211298_n.in(var1)), "TileEntities", DSL.list(TypeReferences.field_211294_j.in(var1)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.field_211300_p.in(var1)))));
      });
      var1.registerType(true, TypeReferences.field_211294_j, () -> {
         return DSL.taggedChoiceLazy("id", DSL.string(), var3);
      });
      var1.registerType(true, TypeReferences.field_211298_n, () -> {
         return DSL.optionalFields("Riding", TypeReferences.field_211298_n.in(var1), TypeReferences.field_211299_o.in(var1));
      });
      var1.registerType(false, TypeReferences.field_211297_m, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      var1.registerType(true, TypeReferences.field_211299_o, () -> {
         return DSL.taggedChoiceLazy("id", DSL.string(), var2);
      });
      var1.registerType(true, TypeReferences.field_211295_k, () -> {
         return DSL.hook(DSL.optionalFields("id", DSL.or(DSL.constType(DSL.intType()), TypeReferences.field_211301_q.in(var1)), "tag", DSL.optionalFields("EntityTag", TypeReferences.field_211298_n.in(var1), "BlockEntityTag", TypeReferences.field_211294_j.in(var1), "CanDestroy", DSL.list(TypeReferences.field_211300_p.in(var1)), "CanPlaceOn", DSL.list(TypeReferences.field_211300_p.in(var1)))), field_206691_b, HookFunction.IDENTITY);
      });
      var1.registerType(false, TypeReferences.field_211289_e, DSL::remainder);
      var1.registerType(false, TypeReferences.field_211300_p, () -> {
         return DSL.or(DSL.constType(DSL.intType()), DSL.constType(DSL.namespacedString()));
      });
      var1.registerType(false, TypeReferences.field_211301_q, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      var1.registerType(false, TypeReferences.field_211291_g, DSL::remainder);
      var1.registerType(false, TypeReferences.field_211292_h, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.field_211303_s.in(var1)), "Objectives", DSL.list(TypeReferences.field_211873_t.in(var1)), "Teams", DSL.list(TypeReferences.field_211874_u.in(var1))));
      });
      var1.registerType(false, TypeReferences.field_211303_s, DSL::remainder);
      var1.registerType(false, TypeReferences.field_211873_t, DSL::remainder);
      var1.registerType(false, TypeReferences.field_211874_u, DSL::remainder);
      var1.registerType(true, TypeReferences.field_211302_r, DSL::remainder);
   }

   protected static <T> T func_209869_a(Dynamic<T> var0, Map<String, String> var1, String var2) {
      return var0.update("tag", (var3) -> {
         return var3.update("BlockEntityTag", (var2x) -> {
            String var3 = var0.getString("id");
            String var4 = (String)var1.get(NamespacedSchema.func_206477_f(var3));
            if (var4 == null) {
               field_206692_c.warn("Unable to resolve BlockEntity for ItemStack: {}", var3);
               return var2x;
            } else {
               return var2x.set("id", var0.createString(var4));
            }
         }).update("EntityTag", (var2x) -> {
            String var3 = var0.getString("id");
            return Objects.equals(NamespacedSchema.func_206477_f(var3), "minecraft:armor_stand") ? var2x.set("id", var0.createString(var2)) : var2x;
         });
      }).getValue();
   }
}
