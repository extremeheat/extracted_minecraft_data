package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V704 extends Schema {
   protected static final Map<String, String> ITEM_TO_BLOCKENTITY = (Map)DataFixUtils.make(() -> {
      HashMap var0 = Maps.newHashMap();
      var0.put("minecraft:furnace", "minecraft:furnace");
      var0.put("minecraft:lit_furnace", "minecraft:furnace");
      var0.put("minecraft:chest", "minecraft:chest");
      var0.put("minecraft:trapped_chest", "minecraft:chest");
      var0.put("minecraft:ender_chest", "minecraft:ender_chest");
      var0.put("minecraft:jukebox", "minecraft:jukebox");
      var0.put("minecraft:dispenser", "minecraft:dispenser");
      var0.put("minecraft:dropper", "minecraft:dropper");
      var0.put("minecraft:sign", "minecraft:sign");
      var0.put("minecraft:mob_spawner", "minecraft:mob_spawner");
      var0.put("minecraft:spawner", "minecraft:mob_spawner");
      var0.put("minecraft:noteblock", "minecraft:noteblock");
      var0.put("minecraft:brewing_stand", "minecraft:brewing_stand");
      var0.put("minecraft:enhanting_table", "minecraft:enchanting_table");
      var0.put("minecraft:command_block", "minecraft:command_block");
      var0.put("minecraft:beacon", "minecraft:beacon");
      var0.put("minecraft:skull", "minecraft:skull");
      var0.put("minecraft:daylight_detector", "minecraft:daylight_detector");
      var0.put("minecraft:hopper", "minecraft:hopper");
      var0.put("minecraft:banner", "minecraft:banner");
      var0.put("minecraft:flower_pot", "minecraft:flower_pot");
      var0.put("minecraft:repeating_command_block", "minecraft:command_block");
      var0.put("minecraft:chain_command_block", "minecraft:command_block");
      var0.put("minecraft:shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:white_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:orange_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:magenta_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:light_blue_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:yellow_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:lime_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:pink_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:gray_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:silver_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:cyan_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:purple_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:blue_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:brown_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:green_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:red_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:black_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:bed", "minecraft:bed");
      var0.put("minecraft:light_gray_shulker_box", "minecraft:shulker_box");
      var0.put("minecraft:banner", "minecraft:banner");
      var0.put("minecraft:white_banner", "minecraft:banner");
      var0.put("minecraft:orange_banner", "minecraft:banner");
      var0.put("minecraft:magenta_banner", "minecraft:banner");
      var0.put("minecraft:light_blue_banner", "minecraft:banner");
      var0.put("minecraft:yellow_banner", "minecraft:banner");
      var0.put("minecraft:lime_banner", "minecraft:banner");
      var0.put("minecraft:pink_banner", "minecraft:banner");
      var0.put("minecraft:gray_banner", "minecraft:banner");
      var0.put("minecraft:silver_banner", "minecraft:banner");
      var0.put("minecraft:light_gray_banner", "minecraft:banner");
      var0.put("minecraft:cyan_banner", "minecraft:banner");
      var0.put("minecraft:purple_banner", "minecraft:banner");
      var0.put("minecraft:blue_banner", "minecraft:banner");
      var0.put("minecraft:brown_banner", "minecraft:banner");
      var0.put("minecraft:green_banner", "minecraft:banner");
      var0.put("minecraft:red_banner", "minecraft:banner");
      var0.put("minecraft:black_banner", "minecraft:banner");
      var0.put("minecraft:standing_sign", "minecraft:sign");
      var0.put("minecraft:wall_sign", "minecraft:sign");
      var0.put("minecraft:piston_head", "minecraft:piston");
      var0.put("minecraft:daylight_detector_inverted", "minecraft:daylight_detector");
      var0.put("minecraft:unpowered_comparator", "minecraft:comparator");
      var0.put("minecraft:powered_comparator", "minecraft:comparator");
      var0.put("minecraft:wall_banner", "minecraft:banner");
      var0.put("minecraft:standing_banner", "minecraft:banner");
      var0.put("minecraft:structure_block", "minecraft:structure_block");
      var0.put("minecraft:end_portal", "minecraft:end_portal");
      var0.put("minecraft:end_gateway", "minecraft:end_gateway");
      var0.put("minecraft:sign", "minecraft:sign");
      var0.put("minecraft:shield", "minecraft:banner");
      var0.put("minecraft:white_bed", "minecraft:bed");
      var0.put("minecraft:orange_bed", "minecraft:bed");
      var0.put("minecraft:magenta_bed", "minecraft:bed");
      var0.put("minecraft:light_blue_bed", "minecraft:bed");
      var0.put("minecraft:yellow_bed", "minecraft:bed");
      var0.put("minecraft:lime_bed", "minecraft:bed");
      var0.put("minecraft:pink_bed", "minecraft:bed");
      var0.put("minecraft:gray_bed", "minecraft:bed");
      var0.put("minecraft:silver_bed", "minecraft:bed");
      var0.put("minecraft:light_gray_bed", "minecraft:bed");
      var0.put("minecraft:cyan_bed", "minecraft:bed");
      var0.put("minecraft:purple_bed", "minecraft:bed");
      var0.put("minecraft:blue_bed", "minecraft:bed");
      var0.put("minecraft:brown_bed", "minecraft:bed");
      var0.put("minecraft:green_bed", "minecraft:bed");
      var0.put("minecraft:red_bed", "minecraft:bed");
      var0.put("minecraft:black_bed", "minecraft:bed");
      var0.put("minecraft:oak_sign", "minecraft:sign");
      var0.put("minecraft:spruce_sign", "minecraft:sign");
      var0.put("minecraft:birch_sign", "minecraft:sign");
      var0.put("minecraft:jungle_sign", "minecraft:sign");
      var0.put("minecraft:acacia_sign", "minecraft:sign");
      var0.put("minecraft:dark_oak_sign", "minecraft:sign");
      var0.put("minecraft:crimson_sign", "minecraft:sign");
      var0.put("minecraft:warped_sign", "minecraft:sign");
      var0.put("minecraft:skeleton_skull", "minecraft:skull");
      var0.put("minecraft:wither_skeleton_skull", "minecraft:skull");
      var0.put("minecraft:zombie_head", "minecraft:skull");
      var0.put("minecraft:player_head", "minecraft:skull");
      var0.put("minecraft:creeper_head", "minecraft:skull");
      var0.put("minecraft:dragon_head", "minecraft:skull");
      var0.put("minecraft:barrel", "minecraft:barrel");
      var0.put("minecraft:conduit", "minecraft:conduit");
      var0.put("minecraft:smoker", "minecraft:smoker");
      var0.put("minecraft:blast_furnace", "minecraft:blast_furnace");
      var0.put("minecraft:lectern", "minecraft:lectern");
      var0.put("minecraft:bell", "minecraft:bell");
      var0.put("minecraft:jigsaw", "minecraft:jigsaw");
      var0.put("minecraft:campfire", "minecraft:campfire");
      var0.put("minecraft:bee_nest", "minecraft:beehive");
      var0.put("minecraft:beehive", "minecraft:beehive");
      var0.put("minecraft:sculk_sensor", "minecraft:sculk_sensor");
      return ImmutableMap.copyOf(var0);
   });
   protected static final Hook.HookFunction ADD_NAMES = new Hook.HookFunction() {
      public <T> T apply(DynamicOps<T> var1, T var2) {
         return V99.addNames(new Dynamic(var1, var2), V704.ITEM_TO_BLOCKENTITY, "ArmorStand");
      }
   };

   public V704(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static void registerInventory(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var0)));
      });
   }

   public Type<?> getChoiceType(DSL.TypeReference var1, String var2) {
      return Objects.equals(var1.typeName(), References.BLOCK_ENTITY.typeName()) ? super.getChoiceType(var1, NamespacedSchema.ensureNamespaced(var2)) : super.getChoiceType(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      registerInventory(var1, var2, "minecraft:furnace");
      registerInventory(var1, var2, "minecraft:chest");
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
      var1.registerSimple(var2, "minecraft:noteblock");
      var1.registerSimple(var2, "minecraft:piston");
      registerInventory(var1, var2, "minecraft:brewing_stand");
      var1.registerSimple(var2, "minecraft:enchanting_table");
      var1.registerSimple(var2, "minecraft:end_portal");
      var1.registerSimple(var2, "minecraft:beacon");
      var1.registerSimple(var2, "minecraft:skull");
      var1.registerSimple(var2, "minecraft:daylight_detector");
      registerInventory(var1, var2, "minecraft:hopper");
      var1.registerSimple(var2, "minecraft:comparator");
      var1.register(var2, "minecraft:flower_pot", (var1x) -> {
         return DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), References.ITEM_NAME.in(var1)));
      });
      var1.registerSimple(var2, "minecraft:banner");
      var1.registerSimple(var2, "minecraft:structure_block");
      var1.registerSimple(var2, "minecraft:end_gateway");
      var1.registerSimple(var2, "minecraft:command_block");
      return var2;
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, References.BLOCK_ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", NamespacedSchema.namespacedString(), var3);
      });
      var1.registerType(true, References.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", References.ITEM_NAME.in(var1), "tag", DSL.optionalFields("EntityTag", References.ENTITY_TREE.in(var1), "BlockEntityTag", References.BLOCK_ENTITY.in(var1), "CanDestroy", DSL.list(References.BLOCK_NAME.in(var1)), "CanPlaceOn", DSL.list(References.BLOCK_NAME.in(var1)), "Items", DSL.list(References.ITEM_STACK.in(var1)))), ADD_NAMES, HookFunction.IDENTITY);
      });
   }
}
