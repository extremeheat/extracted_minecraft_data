package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V0704 extends Schema {
   protected static final Map<String, String> field_206647_b = (Map)DataFixUtils.make(Maps.newHashMap(), (var0) -> {
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
   });
   protected static final HookFunction field_206648_c = new HookFunction() {
      public <T> T apply(DynamicOps<T> var1, T var2) {
         return V0099.func_209869_a(new Dynamic(var1, var2), V0704.field_206647_b, "ArmorStand");
      }
   };

   public V0704(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static void func_206645_a(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var0)));
      });
   }

   public Type<?> getChoiceType(TypeReference var1, String var2) {
      return Objects.equals(var1.typeName(), TypeReferences.field_211294_j.typeName()) ? super.getChoiceType(var1, NamespacedSchema.func_206477_f(var2)) : super.getChoiceType(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      func_206645_a(var1, var2, "minecraft:furnace");
      func_206645_a(var1, var2, "minecraft:chest");
      var1.registerSimple(var2, "minecraft:ender_chest");
      var1.register(var2, "minecraft:jukebox", (var1x) -> {
         return DSL.optionalFields("RecordItem", TypeReferences.field_211295_k.in(var1));
      });
      func_206645_a(var1, var2, "minecraft:dispenser");
      func_206645_a(var1, var2, "minecraft:dropper");
      var1.registerSimple(var2, "minecraft:sign");
      var1.register(var2, "minecraft:mob_spawner", (var1x) -> {
         return TypeReferences.field_211302_r.in(var1);
      });
      var1.registerSimple(var2, "minecraft:noteblock");
      var1.registerSimple(var2, "minecraft:piston");
      func_206645_a(var1, var2, "minecraft:brewing_stand");
      var1.registerSimple(var2, "minecraft:enchanting_table");
      var1.registerSimple(var2, "minecraft:end_portal");
      var1.registerSimple(var2, "minecraft:beacon");
      var1.registerSimple(var2, "minecraft:skull");
      var1.registerSimple(var2, "minecraft:daylight_detector");
      func_206645_a(var1, var2, "minecraft:hopper");
      var1.registerSimple(var2, "minecraft:comparator");
      var1.register(var2, "minecraft:flower_pot", (var1x) -> {
         return DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.field_211301_q.in(var1)));
      });
      var1.registerSimple(var2, "minecraft:banner");
      var1.registerSimple(var2, "minecraft:structure_block");
      var1.registerSimple(var2, "minecraft:end_gateway");
      var1.registerSimple(var2, "minecraft:command_block");
      return var2;
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, TypeReferences.field_211294_j, () -> {
         return DSL.taggedChoiceLazy("id", DSL.namespacedString(), var3);
      });
      var1.registerType(true, TypeReferences.field_211295_k, () -> {
         return DSL.hook(DSL.optionalFields("id", TypeReferences.field_211301_q.in(var1), "tag", DSL.optionalFields("EntityTag", TypeReferences.field_211298_n.in(var1), "BlockEntityTag", TypeReferences.field_211294_j.in(var1), "CanDestroy", DSL.list(TypeReferences.field_211300_p.in(var1)), "CanPlaceOn", DSL.list(TypeReferences.field_211300_p.in(var1)))), field_206648_c, HookFunction.IDENTITY);
      });
   }
}
