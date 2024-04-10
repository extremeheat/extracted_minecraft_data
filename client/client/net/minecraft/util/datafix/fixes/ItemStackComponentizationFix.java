package net.minecraft.util.datafix.fixes;

import com.google.common.base.Splitter;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.ComponentDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemStackComponentizationFix extends DataFix {
   private static final int HIDE_ENCHANTMENTS = 1;
   private static final int HIDE_MODIFIERS = 2;
   private static final int HIDE_UNBREAKABLE = 4;
   private static final int HIDE_CAN_DESTROY = 8;
   private static final int HIDE_CAN_PLACE = 16;
   private static final int HIDE_ADDITIONAL = 32;
   private static final int HIDE_DYE = 64;
   private static final int HIDE_UPGRADES = 128;
   private static final Set<String> POTION_HOLDER_IDS = Set.of(
      "minecraft:potion", "minecraft:splash_potion", "minecraft:lingering_potion", "minecraft:tipped_arrow"
   );
   private static final Set<String> BUCKETED_MOB_IDS = Set.of(
      "minecraft:pufferfish_bucket",
      "minecraft:salmon_bucket",
      "minecraft:cod_bucket",
      "minecraft:tropical_fish_bucket",
      "minecraft:axolotl_bucket",
      "minecraft:tadpole_bucket"
   );
   private static final List<String> BUCKETED_MOB_TAGS = List.of(
      "NoAI", "Silent", "NoGravity", "Glowing", "Invulnerable", "Health", "Age", "Variant", "HuntingCooldown", "BucketVariantTag"
   );
   private static final Set<String> BOOLEAN_BLOCK_STATE_PROPERTIES = Set.of(
      "attached",
      "bottom",
      "conditional",
      "disarmed",
      "drag",
      "enabled",
      "extended",
      "eye",
      "falling",
      "hanging",
      "has_bottle_0",
      "has_bottle_1",
      "has_bottle_2",
      "has_record",
      "has_book",
      "inverted",
      "in_wall",
      "lit",
      "locked",
      "occupied",
      "open",
      "persistent",
      "powered",
      "short",
      "signal_fire",
      "snowy",
      "triggered",
      "unstable",
      "waterlogged",
      "berries",
      "bloom",
      "shrieking",
      "can_summon",
      "up",
      "down",
      "north",
      "east",
      "south",
      "west",
      "slot_0_occupied",
      "slot_1_occupied",
      "slot_2_occupied",
      "slot_3_occupied",
      "slot_4_occupied",
      "slot_5_occupied",
      "cracked",
      "crafting"
   );
   private static final Splitter PROPERTY_SPLITTER = Splitter.on(',');

   public ItemStackComponentizationFix(Schema var1) {
      super(var1, true);
   }

   private static void fixItemStack(ItemStackComponentizationFix.ItemStackData var0, Dynamic<?> var1) {
      int var2 = var0.removeTag("HideFlags").asInt(0);
      var0.moveTagToComponent("Damage", "minecraft:damage", var1.createInt(0));
      var0.moveTagToComponent("RepairCost", "minecraft:repair_cost", var1.createInt(0));
      var0.moveTagToComponent("CustomModelData", "minecraft:custom_model_data");
      var0.removeTag("BlockStateTag").result().ifPresent(var1x -> var0.setComponent("minecraft:block_state", fixBlockStateTag((Dynamic<?>)var1x)));
      var0.moveTagToComponent("EntityTag", "minecraft:entity_data");
      var0.fixSubTag("BlockEntityTag", false, var1x -> {
         String var2x = NamespacedSchema.ensureNamespaced(var1x.get("id").asString(""));
         var1x = fixBlockEntityTag(var0, var1x, var2x);
         Dynamic var3x = var1x.remove("id");
         return var3x.equals(var1x.emptyMap()) ? var3x : var1x;
      });
      var0.moveTagToComponent("BlockEntityTag", "minecraft:block_entity_data");
      if (var0.removeTag("Unbreakable").asBoolean(false)) {
         Dynamic var3 = var1.emptyMap();
         if ((var2 & 4) != 0) {
            var3 = var3.set("show_in_tooltip", var1.createBoolean(false));
         }

         var0.setComponent("minecraft:unbreakable", var3);
      }

      fixEnchantments(var0, var1, "Enchantments", "minecraft:enchantments", (var2 & 1) != 0);
      if (var0.is("minecraft:enchanted_book")) {
         fixEnchantments(var0, var1, "StoredEnchantments", "minecraft:stored_enchantments", (var2 & 32) != 0);
      }

      var0.fixSubTag("display", false, var2x -> fixDisplay(var0, var2x, var2));
      fixAdventureModeChecks(var0, var1, var2);
      fixAttributeModifiers(var0, var1, var2);
      Optional var5 = var0.removeTag("Trim").result();
      if (var5.isPresent()) {
         Dynamic var4 = (Dynamic)var5.get();
         if ((var2 & 128) != 0) {
            var4 = var4.set("show_in_tooltip", var4.createBoolean(false));
         }

         var0.setComponent("minecraft:trim", var4);
      }

      if ((var2 & 32) != 0) {
         var0.setComponent("minecraft:hide_additional_tooltip", var1.emptyMap());
      }

      if (var0.is("minecraft:crossbow")) {
         var0.removeTag("Charged");
         var0.moveTagToComponent("ChargedProjectiles", "minecraft:charged_projectiles", var1.createList(Stream.empty()));
      }

      if (var0.is("minecraft:bundle")) {
         var0.moveTagToComponent("Items", "minecraft:bundle_contents", var1.createList(Stream.empty()));
      }

      if (var0.is("minecraft:filled_map")) {
         var0.moveTagToComponent("map", "minecraft:map_id");
         Map var6 = var0.removeTag("Decorations")
            .asStream()
            .map(ItemStackComponentizationFix::fixMapDecoration)
            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond, (var0x, var1x) -> var0x));
         if (!var6.isEmpty()) {
            var0.setComponent("minecraft:map_decorations", var1.createMap(var6));
         }
      }

      if (var0.is(POTION_HOLDER_IDS)) {
         fixPotionContents(var0, var1);
      }

      if (var0.is("minecraft:writable_book")) {
         fixWritableBook(var0, var1);
      }

      if (var0.is("minecraft:written_book")) {
         fixWrittenBook(var0, var1);
      }

      if (var0.is("minecraft:suspicious_stew")) {
         var0.moveTagToComponent("effects", "minecraft:suspicious_stew_effects");
      }

      if (var0.is("minecraft:debug_stick")) {
         var0.moveTagToComponent("DebugProperty", "minecraft:debug_stick_state");
      }

      if (var0.is(BUCKETED_MOB_IDS)) {
         fixBucketedMobData(var0, var1);
      }

      if (var0.is("minecraft:goat_horn")) {
         var0.moveTagToComponent("instrument", "minecraft:instrument");
      }

      if (var0.is("minecraft:knowledge_book")) {
         var0.moveTagToComponent("Recipes", "minecraft:recipes");
      }

      if (var0.is("minecraft:compass")) {
         fixLodestoneTracker(var0, var1);
      }

      if (var0.is("minecraft:firework_rocket")) {
         fixFireworkRocket(var0);
      }

      if (var0.is("minecraft:firework_star")) {
         fixFireworkStar(var0);
      }

      if (var0.is("minecraft:player_head")) {
         var0.removeTag("SkullOwner").result().ifPresent(var1x -> var0.setComponent("minecraft:profile", fixProfile((Dynamic<?>)var1x)));
      }
   }

   private static Dynamic<?> fixBlockStateTag(Dynamic<?> var0) {
      return (Dynamic<?>)DataFixUtils.orElse(var0.asMapOpt().result().map(var0x -> var0x.collect(Collectors.toMap(Pair::getFirst, var0xx -> {
            String var1 = ((Dynamic)var0xx.getFirst()).asString("");
            Dynamic var2 = (Dynamic)var0xx.getSecond();
            if (BOOLEAN_BLOCK_STATE_PROPERTIES.contains(var1)) {
               Optional var3 = var2.asBoolean().result();
               if (var3.isPresent()) {
                  return var2.createString(String.valueOf(var3.get()));
               }
            }

            Optional var4 = var2.asNumber().result();
            return var4.isPresent() ? var2.createString(((Number)var4.get()).toString()) : var2;
         }))).map(var0::createMap), var0);
   }

   private static Dynamic<?> fixDisplay(ItemStackComponentizationFix.ItemStackData var0, Dynamic<?> var1, int var2) {
      var0.setComponent("minecraft:custom_name", var1.get("Name"));
      var0.setComponent("minecraft:lore", var1.get("Lore"));
      Optional var3 = var1.get("color").asNumber().result().map(Number::intValue);
      boolean var4 = (var2 & 64) != 0;
      if (var3.isPresent() || var4) {
         Dynamic var5 = var1.emptyMap().set("rgb", var1.createInt(var3.orElse(10511680)));
         if (var4) {
            var5 = var5.set("show_in_tooltip", var1.createBoolean(false));
         }

         var0.setComponent("minecraft:dyed_color", var5);
      }

      Optional var6 = var1.get("LocName").asString().result();
      if (var6.isPresent()) {
         var0.setComponent("minecraft:item_name", ComponentDataFixUtils.createTranslatableComponent(var1.getOps(), (String)var6.get()));
      }

      if (var0.is("minecraft:filled_map")) {
         var0.setComponent("minecraft:map_color", var1.get("MapColor"));
         var1 = var1.remove("MapColor");
      }

      return var1.remove("Name").remove("Lore").remove("color").remove("LocName");
   }

   private static <T> Dynamic<T> fixBlockEntityTag(ItemStackComponentizationFix.ItemStackData var0, Dynamic<T> var1, String var2) {
      var0.setComponent("minecraft:lock", var1.get("Lock"));
      var1 = var1.remove("Lock");
      Optional var3 = var1.get("LootTable").result();
      if (var3.isPresent()) {
         Dynamic var4 = var1.emptyMap().set("loot_table", (Dynamic)var3.get());
         long var5 = var1.get("LootTableSeed").asLong(0L);
         if (var5 != 0L) {
            var4 = var4.set("seed", var1.createLong(var5));
         }

         var0.setComponent("minecraft:container_loot", var4);
         var1 = var1.remove("LootTable").remove("LootTableSeed");
      }
      return switch (var2) {
         case "minecraft:skull" -> {
            var0.setComponent("minecraft:note_block_sound", var1.get("note_block_sound"));
            yield var1.remove("note_block_sound");
         }
         case "minecraft:decorated_pot" -> {
            var0.setComponent("minecraft:pot_decorations", var1.get("sherds"));
            Optional var10 = var1.get("item").result();
            if (var10.isPresent()) {
               var0.setComponent(
                  "minecraft:container", var1.createList(Stream.of(var1.emptyMap().set("slot", var1.createInt(0)).set("item", (Dynamic)var10.get())))
               );
            }

            yield var1.remove("sherds").remove("item");
         }
         case "minecraft:banner" -> {
            var0.setComponent("minecraft:banner_patterns", var1.get("patterns"));
            Optional var9 = var1.get("Base").asNumber().result();
            if (var9.isPresent()) {
               var0.setComponent("minecraft:base_color", var1.createString(BannerPatternFormatFix.fixColor(((Number)var9.get()).intValue())));
            }

            yield var1.remove("patterns").remove("Base");
         }
         case "minecraft:shulker_box", "minecraft:chest", "minecraft:trapped_chest", "minecraft:furnace", "minecraft:ender_chest", "minecraft:dispenser", "minecraft:dropper", "minecraft:brewing_stand", "minecraft:hopper", "minecraft:barrel", "minecraft:smoker", "minecraft:blast_furnace", "minecraft:campfire", "minecraft:chiseled_bookshelf", "minecraft:crafter" -> {
            List var6 = var1.get("Items")
               .asList(var0x -> var0x.emptyMap().set("slot", var0x.createInt(var0x.get("Slot").asByte((byte)0) & 255)).set("item", var0x.remove("Slot")));
            if (!var6.isEmpty()) {
               var0.setComponent("minecraft:container", var1.createList(var6.stream()));
            }

            yield var1.remove("Items");
         }
         case "minecraft:beehive" -> {
            var0.setComponent("minecraft:bees", var1.get("bees"));
            yield var1.remove("bees");
         }
         default -> var1;
      };
   }

   private static void fixEnchantments(ItemStackComponentizationFix.ItemStackData var0, Dynamic<?> var1, String var2, String var3, boolean var4) {
      OptionalDynamic var5 = var0.removeTag(var2);
      List var6 = var5.asList(Function.identity()).stream().flatMap(var0x -> parseEnchantment((Dynamic<?>)var0x).stream()).toList();
      if (!var6.isEmpty() || var4) {
         Dynamic var7 = var1.emptyMap();
         Dynamic var8 = var1.emptyMap();

         for (Pair var10 : var6) {
            var8 = var8.set((String)var10.getFirst(), var1.createInt((Integer)var10.getSecond()));
         }

         var7 = var7.set("levels", var8);
         if (var4) {
            var7 = var7.set("show_in_tooltip", var1.createBoolean(false));
         }

         var0.setComponent(var3, var7);
      }

      if (var5.result().isPresent() && var6.isEmpty()) {
         var0.setComponent("minecraft:enchantment_glint_override", var1.createBoolean(true));
      }
   }

   private static Optional<Pair<String, Integer>> parseEnchantment(Dynamic<?> var0) {
      return var0.get("id").asString().apply2stable((var0x, var1) -> Pair.of(var0x, Mth.clamp(var1.intValue(), 0, 255)), var0.get("lvl").asNumber()).result();
   }

   private static void fixAdventureModeChecks(ItemStackComponentizationFix.ItemStackData var0, Dynamic<?> var1, int var2) {
      fixBlockStatePredicates(var0, var1, "CanDestroy", "minecraft:can_break", (var2 & 8) != 0);
      fixBlockStatePredicates(var0, var1, "CanPlaceOn", "minecraft:can_place_on", (var2 & 16) != 0);
   }

   private static void fixBlockStatePredicates(ItemStackComponentizationFix.ItemStackData var0, Dynamic<?> var1, String var2, String var3, boolean var4) {
      Optional var5 = var0.removeTag(var2).result();
      if (!var5.isEmpty()) {
         Dynamic var6 = var1.emptyMap()
            .set(
               "predicates",
               var1.createList(
                  ((Dynamic)var5.get())
                     .asStream()
                     .map(
                        var0x -> (Dynamic)DataFixUtils.orElse(var0x.asString().map(var1x -> fixBlockStatePredicate((Dynamic<?>)var0x, var1x)).result(), var0x)
                     )
               )
            );
         if (var4) {
            var6 = var6.set("show_in_tooltip", var1.createBoolean(false));
         }

         var0.setComponent(var3, var6);
      }
   }

   private static Dynamic<?> fixBlockStatePredicate(Dynamic<?> var0, String var1) {
      int var2 = var1.indexOf(91);
      int var3 = var1.indexOf(123);
      int var4 = var1.length();
      if (var2 != -1) {
         var4 = var2;
      }

      if (var3 != -1) {
         var4 = Math.min(var4, var3);
      }

      String var5 = var1.substring(0, var4);
      Dynamic var6 = var0.emptyMap().set("blocks", var0.createString(var5.trim()));
      int var7 = var1.indexOf(93);
      if (var2 != -1 && var7 != -1) {
         Dynamic var8 = var0.emptyMap();

         for (String var11 : PROPERTY_SPLITTER.split(var1.substring(var2 + 1, var7))) {
            int var12 = var11.indexOf(61);
            if (var12 != -1) {
               String var13 = var11.substring(0, var12).trim();
               String var14 = var11.substring(var12 + 1).trim();
               var8 = var8.set(var13, var0.createString(var14));
            }
         }

         var6 = var6.set("state", var8);
      }

      int var15 = var1.indexOf(125);
      if (var3 != -1 && var15 != -1) {
         var6 = var6.set("nbt", var0.createString(var1.substring(var3, var15 + 1)));
      }

      return var6;
   }

   private static void fixAttributeModifiers(ItemStackComponentizationFix.ItemStackData var0, Dynamic<?> var1, int var2) {
      List var3 = var0.removeTag("AttributeModifiers").asList(ItemStackComponentizationFix::fixAttributeModifier);
      boolean var4 = (var2 & 2) != 0;
      if (!var3.isEmpty() || var4) {
         Dynamic var5 = var1.emptyMap().set("modifiers", var1.createList(var3.stream()));
         if (var4) {
            var5 = var5.set("show_in_tooltip", var1.createBoolean(false));
         }

         var0.setComponent("minecraft:attribute_modifiers", var5);
      }
   }

   private static Dynamic<?> fixAttributeModifier(Dynamic<?> var0) {
      Dynamic var1 = var0.emptyMap().set("name", var0.createString("")).set("amount", var0.createDouble(0.0)).set("operation", var0.createString("add_value"));
      var1 = Dynamic.copyField(var0, "AttributeName", var1, "type");
      var1 = Dynamic.copyField(var0, "Slot", var1, "slot");
      var1 = Dynamic.copyField(var0, "UUID", var1, "uuid");
      var1 = Dynamic.copyField(var0, "Name", var1, "name");
      var1 = Dynamic.copyField(var0, "Amount", var1, "amount");
      return Dynamic.copyAndFixField(var0, "Operation", var1, "operation", var0x -> {
         return var0x.createString(switch (var0x.asInt(0)) {
            case 1 -> "add_multiplied_base";
            case 2 -> "add_multiplied_total";
            default -> "add_value";
         });
      });
   }

   private static Pair<Dynamic<?>, Dynamic<?>> fixMapDecoration(Dynamic<?> var0) {
      Dynamic var1 = (Dynamic)DataFixUtils.orElseGet(var0.get("id").result(), () -> var0.createString(""));
      Dynamic var2 = var0.emptyMap()
         .set("type", var0.createString(fixMapDecorationType(var0.get("type").asInt(0))))
         .set("x", var0.createDouble(var0.get("x").asDouble(0.0)))
         .set("z", var0.createDouble(var0.get("z").asDouble(0.0)))
         .set("rotation", var0.createFloat((float)var0.get("rot").asDouble(0.0)));
      return Pair.of(var1, var2);
   }

   private static String fixMapDecorationType(int var0) {
      return switch (var0) {
         case 1 -> "frame";
         case 2 -> "red_marker";
         case 3 -> "blue_marker";
         case 4 -> "target_x";
         case 5 -> "target_point";
         case 6 -> "player_off_map";
         case 7 -> "player_off_limits";
         case 8 -> "mansion";
         case 9 -> "monument";
         case 10 -> "banner_white";
         case 11 -> "banner_orange";
         case 12 -> "banner_magenta";
         case 13 -> "banner_light_blue";
         case 14 -> "banner_yellow";
         case 15 -> "banner_lime";
         case 16 -> "banner_pink";
         case 17 -> "banner_gray";
         case 18 -> "banner_light_gray";
         case 19 -> "banner_cyan";
         case 20 -> "banner_purple";
         case 21 -> "banner_blue";
         case 22 -> "banner_brown";
         case 23 -> "banner_green";
         case 24 -> "banner_red";
         case 25 -> "banner_black";
         case 26 -> "red_x";
         case 27 -> "village_desert";
         case 28 -> "village_plains";
         case 29 -> "village_savanna";
         case 30 -> "village_snowy";
         case 31 -> "village_taiga";
         case 32 -> "jungle_temple";
         case 33 -> "swamp_hut";
         default -> "player";
      };
   }

   private static void fixPotionContents(ItemStackComponentizationFix.ItemStackData var0, Dynamic<?> var1) {
      Dynamic var2 = var1.emptyMap();
      Optional var3 = var0.removeTag("Potion").asString().result().filter(var0x -> !var0x.equals("minecraft:empty"));
      if (var3.isPresent()) {
         var2 = var2.set("potion", var1.createString((String)var3.get()));
      }

      var2 = var0.moveTagInto("CustomPotionColor", var2, "custom_color");
      var2 = var0.moveTagInto("custom_potion_effects", var2, "custom_effects");
      if (!var2.equals(var1.emptyMap())) {
         var0.setComponent("minecraft:potion_contents", var2);
      }
   }

   private static void fixWritableBook(ItemStackComponentizationFix.ItemStackData var0, Dynamic<?> var1) {
      Dynamic var2 = fixBookPages(var0, var1);
      if (var2 != null) {
         var0.setComponent("minecraft:writable_book_content", var1.emptyMap().set("pages", var2));
      }
   }

   private static void fixWrittenBook(ItemStackComponentizationFix.ItemStackData var0, Dynamic<?> var1) {
      Dynamic var2 = fixBookPages(var0, var1);
      String var3 = var0.removeTag("title").asString("");
      Optional var4 = var0.removeTag("filtered_title").asString().result();
      Dynamic var5 = var1.emptyMap();
      var5 = var5.set("title", createFilteredText(var1, var3, var4));
      var5 = var0.moveTagInto("author", var5, "author");
      var5 = var0.moveTagInto("resolved", var5, "resolved");
      var5 = var0.moveTagInto("generation", var5, "generation");
      if (var2 != null) {
         var5 = var5.set("pages", var2);
      }

      var0.setComponent("minecraft:written_book_content", var5);
   }

   @Nullable
   private static Dynamic<?> fixBookPages(ItemStackComponentizationFix.ItemStackData var0, Dynamic<?> var1) {
      List var2 = var0.removeTag("pages").asList(var0x -> var0x.asString(""));
      Map var3 = var0.removeTag("filtered_pages").asMap(var0x -> var0x.asString("0"), var0x -> var0x.asString(""));
      if (var2.isEmpty()) {
         return null;
      } else {
         ArrayList var4 = new ArrayList(var2.size());

         for (int var5 = 0; var5 < var2.size(); var5++) {
            String var6 = (String)var2.get(var5);
            String var7 = (String)var3.get(String.valueOf(var5));
            var4.add(createFilteredText(var1, var6, Optional.ofNullable(var7)));
         }

         return var1.createList(var4.stream());
      }
   }

   private static Dynamic<?> createFilteredText(Dynamic<?> var0, String var1, Optional<String> var2) {
      Dynamic var3 = var0.emptyMap().set("raw", var0.createString(var1));
      if (var2.isPresent()) {
         var3 = var3.set("filtered", var0.createString((String)var2.get()));
      }

      return var3;
   }

   private static void fixBucketedMobData(ItemStackComponentizationFix.ItemStackData var0, Dynamic<?> var1) {
      Dynamic var2 = var1.emptyMap();

      for (String var4 : BUCKETED_MOB_TAGS) {
         var2 = var0.moveTagInto(var4, var2, var4);
      }

      if (!var2.equals(var1.emptyMap())) {
         var0.setComponent("minecraft:bucket_entity_data", var2);
      }
   }

   private static void fixLodestoneTracker(ItemStackComponentizationFix.ItemStackData var0, Dynamic<?> var1) {
      Optional var2 = var0.removeTag("LodestonePos").result();
      Optional var3 = var0.removeTag("LodestoneDimension").result();
      if (!var2.isEmpty() || !var3.isEmpty()) {
         boolean var4 = var0.removeTag("LodestoneTracked").asBoolean(true);
         Dynamic var5 = var1.emptyMap();
         if (var2.isPresent() && var3.isPresent()) {
            var5 = var5.set("target", var1.emptyMap().set("pos", (Dynamic)var2.get()).set("dimension", (Dynamic)var3.get()));
         }

         if (!var4) {
            var5 = var5.set("tracked", var1.createBoolean(false));
         }

         var0.setComponent("minecraft:lodestone_tracker", var5);
      }
   }

   private static void fixFireworkStar(ItemStackComponentizationFix.ItemStackData var0) {
      var0.fixSubTag("Explosion", true, var1 -> {
         var0.setComponent("minecraft:firework_explosion", fixFireworkExplosion(var1));
         return var1.remove("Type").remove("Colors").remove("FadeColors").remove("Trail").remove("Flicker");
      });
   }

   private static void fixFireworkRocket(ItemStackComponentizationFix.ItemStackData var0) {
      var0.fixSubTag("Fireworks", true, var1 -> {
         Stream var2 = var1.get("Explosions").asStream().map(ItemStackComponentizationFix::fixFireworkExplosion);
         int var3 = var1.get("Flight").asInt(0);
         var0.setComponent("minecraft:fireworks", var1.emptyMap().set("explosions", var1.createList(var2)).set("flight_duration", var1.createByte((byte)var3)));
         return var1.remove("Explosions").remove("Flight");
      });
   }

   private static Dynamic<?> fixFireworkExplosion(Dynamic<?> var0) {
      var0 = var0.renameAndFixField("Type", "shape", var0x -> {
         return var0x.createString(switch (var0x.asInt(0)) {
            case 1 -> "large_ball";
            case 2 -> "star";
            case 3 -> "creeper";
            case 4 -> "burst";
            default -> "small_ball";
         });
      });
      var0 = var0.renameField("Colors", "colors");
      var0 = var0.renameField("FadeColors", "fade_colors");
      var0 = var0.renameField("Trail", "has_trail");
      return var0.renameField("Flicker", "has_twinkle");
   }

   public static Dynamic<?> fixProfile(Dynamic<?> var0) {
      Optional var1 = var0.asString().result();
      if (var1.isPresent()) {
         return isValidPlayerName((String)var1.get()) ? var0.emptyMap().set("name", var0.createString((String)var1.get())) : var0.emptyMap();
      } else {
         String var2 = var0.get("Name").asString("");
         Optional var3 = var0.get("Id").result();
         Dynamic var4 = fixProfileProperties(var0.get("Properties"));
         Dynamic var5 = var0.emptyMap();
         if (isValidPlayerName(var2)) {
            var5 = var5.set("name", var0.createString(var2));
         }

         if (var3.isPresent()) {
            var5 = var5.set("id", (Dynamic)var3.get());
         }

         if (var4 != null) {
            var5 = var5.set("properties", var4);
         }

         return var5;
      }
   }

   private static boolean isValidPlayerName(String var0) {
      return var0.length() > 16 ? false : var0.chars().filter(var0x -> var0x <= 32 || var0x >= 127).findAny().isEmpty();
   }

   @Nullable
   private static Dynamic<?> fixProfileProperties(OptionalDynamic<?> var0) {
      Map var1 = var0.asMap(var0x -> var0x.asString(""), var0x -> var0x.asList(var0xx -> {
            String var1x = var0xx.get("Value").asString("");
            Optional var2 = var0xx.get("Signature").asString().result();
            return Pair.of(var1x, var2);
         }));
      return var1.isEmpty() ? null : var0.createList(var1.entrySet().stream().flatMap(var1x -> ((List)var1x.getValue()).stream().map(var2 -> {
            Dynamic var3 = var0.emptyMap().set("name", var0.createString((String)var1x.getKey())).set("value", var0.createString((String)var2.getFirst()));
            Optional var4 = (Optional)var2.getSecond();
            return var4.isPresent() ? var3.set("signature", var0.createString((String)var4.get())) : var3;
         })));
   }

   protected TypeRewriteRule makeRule() {
      return this.writeFixAndRead(
         "ItemStack componentization", this.getInputSchema().getType(References.ITEM_STACK), this.getOutputSchema().getType(References.ITEM_STACK), var0 -> {
            Optional var1 = ItemStackComponentizationFix.ItemStackData.read(var0).map(var0x -> {
               fixItemStack(var0x, var0x.tag);
               return var0x.write();
            });
            return (Dynamic)DataFixUtils.orElse(var1, var0);
         }
      );
   }

   static class ItemStackData {
      private final String item;
      private final int count;
      private Dynamic<?> components;
      private final Dynamic<?> remainder;
      Dynamic<?> tag;

      private ItemStackData(String var1, int var2, Dynamic<?> var3) {
         super();
         this.item = NamespacedSchema.ensureNamespaced(var1);
         this.count = var2;
         this.components = var3.emptyMap();
         this.tag = var3.get("tag").orElseEmptyMap();
         this.remainder = var3.remove("tag");
      }

      public static Optional<ItemStackComponentizationFix.ItemStackData> read(Dynamic<?> var0) {
         return var0.get("id")
            .asString()
            .apply2stable(
               (var1, var2) -> new ItemStackComponentizationFix.ItemStackData(var1, var2.intValue(), var0.remove("id").remove("Count")),
               var0.get("Count").asNumber()
            )
            .result();
      }

      public OptionalDynamic<?> removeTag(String var1) {
         OptionalDynamic var2 = this.tag.get(var1);
         this.tag = this.tag.remove(var1);
         return var2;
      }

      public void setComponent(String var1, Dynamic<?> var2) {
         this.components = this.components.set(var1, var2);
      }

      public void setComponent(String var1, OptionalDynamic<?> var2) {
         var2.result().ifPresent(var2x -> this.components = this.components.set(var1, var2x));
      }

      public Dynamic<?> moveTagInto(String var1, Dynamic<?> var2, String var3) {
         Optional var4 = this.removeTag(var1).result();
         return var4.isPresent() ? var2.set(var3, (Dynamic)var4.get()) : var2;
      }

      public void moveTagToComponent(String var1, String var2, Dynamic<?> var3) {
         Optional var4 = this.removeTag(var1).result();
         if (var4.isPresent() && !((Dynamic)var4.get()).equals(var3)) {
            this.setComponent(var2, (Dynamic<?>)var4.get());
         }
      }

      public void moveTagToComponent(String var1, String var2) {
         this.removeTag(var1).result().ifPresent(var2x -> this.setComponent(var2, (Dynamic<?>)var2x));
      }

      public void fixSubTag(String var1, boolean var2, UnaryOperator<Dynamic<?>> var3) {
         OptionalDynamic var4 = this.tag.get(var1);
         if (!var2 || !var4.result().isEmpty()) {
            Dynamic var5 = var4.orElseEmptyMap();
            var5 = var3.apply(var5);
            if (var5.equals(var5.emptyMap())) {
               this.tag = this.tag.remove(var1);
            } else {
               this.tag = this.tag.set(var1, var5);
            }
         }
      }

      public Dynamic<?> write() {
         Dynamic var1 = this.tag.emptyMap().set("id", this.tag.createString(this.item)).set("count", this.tag.createInt(this.count));
         if (!this.tag.equals(this.tag.emptyMap())) {
            this.components = this.components.set("minecraft:custom_data", this.tag);
         }

         if (!this.components.equals(this.tag.emptyMap())) {
            var1 = var1.set("components", this.components);
         }

         return mergeRemainder(var1, this.remainder);
      }

      private static <T> Dynamic<T> mergeRemainder(Dynamic<T> var0, Dynamic<?> var1) {
         DynamicOps var2 = var0.getOps();
         return var2.getMap(var0.getValue())
            .flatMap(var2x -> var2.mergeToMap(var1.convert(var2).getValue(), var2x))
            .map(var1x -> new Dynamic(var2, var1x))
            .result()
            .orElse(var0);
      }

      public boolean is(String var1) {
         return this.item.equals(var1);
      }

      public boolean is(Set<String> var1) {
         return var1.contains(this.item);
      }

      public boolean hasComponent(String var1) {
         return this.components.get(var1).result().isPresent();
      }
   }
}
