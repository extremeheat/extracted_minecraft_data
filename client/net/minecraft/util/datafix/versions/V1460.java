package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1460 extends NamespacedSchema {
   public V1460(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static void func_206557_a(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return V0100.func_206605_a(var0);
      });
   }

   protected static void func_206531_b(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var0)));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      var1.registerSimple(var2, "minecraft:area_effect_cloud");
      func_206557_a(var1, var2, "minecraft:armor_stand");
      var1.register(var2, "minecraft:arrow", (var1x) -> {
         return DSL.optionalFields("inBlockState", TypeReferences.field_211296_l.in(var1));
      });
      func_206557_a(var1, var2, "minecraft:bat");
      func_206557_a(var1, var2, "minecraft:blaze");
      var1.registerSimple(var2, "minecraft:boat");
      func_206557_a(var1, var2, "minecraft:cave_spider");
      var1.register(var2, "minecraft:chest_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.field_211296_l.in(var1), "Items", DSL.list(TypeReferences.field_211295_k.in(var1)));
      });
      func_206557_a(var1, var2, "minecraft:chicken");
      var1.register(var2, "minecraft:commandblock_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.field_211296_l.in(var1));
      });
      func_206557_a(var1, var2, "minecraft:cow");
      func_206557_a(var1, var2, "minecraft:creeper");
      var1.register(var2, "minecraft:donkey", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var1)), "SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      var1.registerSimple(var2, "minecraft:dragon_fireball");
      var1.registerSimple(var2, "minecraft:egg");
      func_206557_a(var1, var2, "minecraft:elder_guardian");
      var1.registerSimple(var2, "minecraft:ender_crystal");
      func_206557_a(var1, var2, "minecraft:ender_dragon");
      var1.register(var2, "minecraft:enderman", (var1x) -> {
         return DSL.optionalFields("carriedBlockState", TypeReferences.field_211296_l.in(var1), V0100.func_206605_a(var1));
      });
      func_206557_a(var1, var2, "minecraft:endermite");
      var1.registerSimple(var2, "minecraft:ender_pearl");
      var1.registerSimple(var2, "minecraft:evocation_fangs");
      func_206557_a(var1, var2, "minecraft:evocation_illager");
      var1.registerSimple(var2, "minecraft:eye_of_ender_signal");
      var1.register(var2, "minecraft:falling_block", (var1x) -> {
         return DSL.optionalFields("BlockState", TypeReferences.field_211296_l.in(var1), "TileEntityData", TypeReferences.field_211294_j.in(var1));
      });
      var1.registerSimple(var2, "minecraft:fireball");
      var1.register(var2, "minecraft:fireworks_rocket", (var1x) -> {
         return DSL.optionalFields("FireworksItem", TypeReferences.field_211295_k.in(var1));
      });
      var1.register(var2, "minecraft:furnace_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.field_211296_l.in(var1));
      });
      func_206557_a(var1, var2, "minecraft:ghast");
      func_206557_a(var1, var2, "minecraft:giant");
      func_206557_a(var1, var2, "minecraft:guardian");
      var1.register(var2, "minecraft:hopper_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.field_211296_l.in(var1), "Items", DSL.list(TypeReferences.field_211295_k.in(var1)));
      });
      var1.register(var2, "minecraft:horse", (var1x) -> {
         return DSL.optionalFields("ArmorItem", TypeReferences.field_211295_k.in(var1), "SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      func_206557_a(var1, var2, "minecraft:husk");
      var1.registerSimple(var2, "minecraft:illusion_illager");
      var1.register(var2, "minecraft:item", (var1x) -> {
         return DSL.optionalFields("Item", TypeReferences.field_211295_k.in(var1));
      });
      var1.register(var2, "minecraft:item_frame", (var1x) -> {
         return DSL.optionalFields("Item", TypeReferences.field_211295_k.in(var1));
      });
      var1.registerSimple(var2, "minecraft:leash_knot");
      var1.register(var2, "minecraft:llama", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var1)), "SaddleItem", TypeReferences.field_211295_k.in(var1), "DecorItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      var1.registerSimple(var2, "minecraft:llama_spit");
      func_206557_a(var1, var2, "minecraft:magma_cube");
      var1.register(var2, "minecraft:minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.field_211296_l.in(var1));
      });
      func_206557_a(var1, var2, "minecraft:mooshroom");
      var1.register(var2, "minecraft:mule", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var1)), "SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      func_206557_a(var1, var2, "minecraft:ocelot");
      var1.registerSimple(var2, "minecraft:painting");
      var1.registerSimple(var2, "minecraft:parrot");
      func_206557_a(var1, var2, "minecraft:pig");
      func_206557_a(var1, var2, "minecraft:polar_bear");
      var1.register(var2, "minecraft:potion", (var1x) -> {
         return DSL.optionalFields("Potion", TypeReferences.field_211295_k.in(var1));
      });
      func_206557_a(var1, var2, "minecraft:rabbit");
      func_206557_a(var1, var2, "minecraft:sheep");
      func_206557_a(var1, var2, "minecraft:shulker");
      var1.registerSimple(var2, "minecraft:shulker_bullet");
      func_206557_a(var1, var2, "minecraft:silverfish");
      func_206557_a(var1, var2, "minecraft:skeleton");
      var1.register(var2, "minecraft:skeleton_horse", (var1x) -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      func_206557_a(var1, var2, "minecraft:slime");
      var1.registerSimple(var2, "minecraft:small_fireball");
      var1.registerSimple(var2, "minecraft:snowball");
      func_206557_a(var1, var2, "minecraft:snowman");
      var1.register(var2, "minecraft:spawner_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.field_211296_l.in(var1), TypeReferences.field_211302_r.in(var1));
      });
      var1.register(var2, "minecraft:spectral_arrow", (var1x) -> {
         return DSL.optionalFields("inBlockState", TypeReferences.field_211296_l.in(var1));
      });
      func_206557_a(var1, var2, "minecraft:spider");
      func_206557_a(var1, var2, "minecraft:squid");
      func_206557_a(var1, var2, "minecraft:stray");
      var1.registerSimple(var2, "minecraft:tnt");
      var1.register(var2, "minecraft:tnt_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.field_211296_l.in(var1));
      });
      func_206557_a(var1, var2, "minecraft:vex");
      var1.register(var2, "minecraft:villager", (var1x) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.field_211295_k.in(var1)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.field_211295_k.in(var1), "buyB", TypeReferences.field_211295_k.in(var1), "sell", TypeReferences.field_211295_k.in(var1)))), V0100.func_206605_a(var1));
      });
      func_206557_a(var1, var2, "minecraft:villager_golem");
      func_206557_a(var1, var2, "minecraft:vindication_illager");
      func_206557_a(var1, var2, "minecraft:witch");
      func_206557_a(var1, var2, "minecraft:wither");
      func_206557_a(var1, var2, "minecraft:wither_skeleton");
      var1.registerSimple(var2, "minecraft:wither_skull");
      func_206557_a(var1, var2, "minecraft:wolf");
      var1.registerSimple(var2, "minecraft:xp_bottle");
      var1.registerSimple(var2, "minecraft:xp_orb");
      func_206557_a(var1, var2, "minecraft:zombie");
      var1.register(var2, "minecraft:zombie_horse", (var1x) -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      func_206557_a(var1, var2, "minecraft:zombie_pigman");
      func_206557_a(var1, var2, "minecraft:zombie_villager");
      return var2;
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      func_206531_b(var1, var2, "minecraft:furnace");
      func_206531_b(var1, var2, "minecraft:chest");
      func_206531_b(var1, var2, "minecraft:trapped_chest");
      var1.registerSimple(var2, "minecraft:ender_chest");
      var1.register(var2, "minecraft:jukebox", (var1x) -> {
         return DSL.optionalFields("RecordItem", TypeReferences.field_211295_k.in(var1));
      });
      func_206531_b(var1, var2, "minecraft:dispenser");
      func_206531_b(var1, var2, "minecraft:dropper");
      var1.registerSimple(var2, "minecraft:sign");
      var1.register(var2, "minecraft:mob_spawner", (var1x) -> {
         return TypeReferences.field_211302_r.in(var1);
      });
      var1.register(var2, "minecraft:piston", (var1x) -> {
         return DSL.optionalFields("blockState", TypeReferences.field_211296_l.in(var1));
      });
      func_206531_b(var1, var2, "minecraft:brewing_stand");
      var1.registerSimple(var2, "minecraft:enchanting_table");
      var1.registerSimple(var2, "minecraft:end_portal");
      var1.registerSimple(var2, "minecraft:beacon");
      var1.registerSimple(var2, "minecraft:skull");
      var1.registerSimple(var2, "minecraft:daylight_detector");
      func_206531_b(var1, var2, "minecraft:hopper");
      var1.registerSimple(var2, "minecraft:comparator");
      var1.registerSimple(var2, "minecraft:banner");
      var1.registerSimple(var2, "minecraft:structure_block");
      var1.registerSimple(var2, "minecraft:end_gateway");
      var1.registerSimple(var2, "minecraft:command_block");
      func_206531_b(var1, var2, "minecraft:shulker_box");
      var1.registerSimple(var2, "minecraft:bed");
      return var2;
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      var1.registerType(false, TypeReferences.field_211285_a, DSL::remainder);
      var1.registerType(false, TypeReferences.field_211304_t, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      var1.registerType(false, TypeReferences.field_211286_b, () -> {
         return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", TypeReferences.field_211298_n.in(var1)), "Inventory", DSL.list(TypeReferences.field_211295_k.in(var1)), "EnderItems", DSL.list(TypeReferences.field_211295_k.in(var1)), DSL.optionalFields("ShoulderEntityLeft", TypeReferences.field_211298_n.in(var1), "ShoulderEntityRight", TypeReferences.field_211298_n.in(var1), "recipeBook", DSL.optionalFields("recipes", DSL.list(TypeReferences.field_211304_t.in(var1)), "toBeDisplayed", DSL.list(TypeReferences.field_211304_t.in(var1)))));
      });
      var1.registerType(false, TypeReferences.field_211287_c, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.field_211298_n.in(var1)), "TileEntities", DSL.list(TypeReferences.field_211294_j.in(var1)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.field_211300_p.in(var1))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.field_211296_l.in(var1))))));
      });
      var1.registerType(true, TypeReferences.field_211294_j, () -> {
         return DSL.taggedChoiceLazy("id", DSL.namespacedString(), var3);
      });
      var1.registerType(true, TypeReferences.field_211298_n, () -> {
         return DSL.optionalFields("Passengers", DSL.list(TypeReferences.field_211298_n.in(var1)), TypeReferences.field_211299_o.in(var1));
      });
      var1.registerType(true, TypeReferences.field_211299_o, () -> {
         return DSL.taggedChoiceLazy("id", DSL.namespacedString(), var2);
      });
      var1.registerType(true, TypeReferences.field_211295_k, () -> {
         return DSL.hook(DSL.optionalFields("id", TypeReferences.field_211301_q.in(var1), "tag", DSL.optionalFields("EntityTag", TypeReferences.field_211298_n.in(var1), "BlockEntityTag", TypeReferences.field_211294_j.in(var1), "CanDestroy", DSL.list(TypeReferences.field_211300_p.in(var1)), "CanPlaceOn", DSL.list(TypeReferences.field_211300_p.in(var1)))), V0705.field_206597_b, HookFunction.IDENTITY);
      });
      var1.registerType(false, TypeReferences.field_211288_d, () -> {
         return DSL.compoundList(DSL.list(TypeReferences.field_211295_k.in(var1)));
      });
      var1.registerType(false, TypeReferences.field_211289_e, DSL::remainder);
      var1.registerType(false, TypeReferences.field_211290_f, () -> {
         return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.field_211298_n.in(var1))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.field_211294_j.in(var1))), "palette", DSL.list(TypeReferences.field_211296_l.in(var1)));
      });
      var1.registerType(false, TypeReferences.field_211300_p, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      var1.registerType(false, TypeReferences.field_211301_q, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      var1.registerType(false, TypeReferences.field_211296_l, DSL::remainder);
      Supplier var4 = () -> {
         return DSL.compoundList(TypeReferences.field_211301_q.in(var1), DSL.constType(DSL.intType()));
      };
      var1.registerType(false, TypeReferences.field_211291_g, () -> {
         return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(TypeReferences.field_211300_p.in(var1), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate)var4.get(), "minecraft:used", (TypeTemplate)var4.get(), "minecraft:broken", (TypeTemplate)var4.get(), "minecraft:picked_up", (TypeTemplate)var4.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate)var4.get(), "minecraft:killed", DSL.compoundList(TypeReferences.field_211297_m.in(var1), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(TypeReferences.field_211297_m.in(var1), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType())))));
      });
      var1.registerType(false, TypeReferences.field_211292_h, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.field_211303_s.in(var1)), "Objectives", DSL.list(TypeReferences.field_211873_t.in(var1)), "Teams", DSL.list(TypeReferences.field_211874_u.in(var1))));
      });
      var1.registerType(false, TypeReferences.field_211303_s, () -> {
         return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.field_211296_l.in(var1), "CB", TypeReferences.field_211296_l.in(var1), "CC", TypeReferences.field_211296_l.in(var1), "CD", TypeReferences.field_211296_l.in(var1))));
      });
      var1.registerType(false, TypeReferences.field_211873_t, DSL::remainder);
      var1.registerType(false, TypeReferences.field_211874_u, DSL::remainder);
      var1.registerType(true, TypeReferences.field_211302_r, () -> {
         return DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", TypeReferences.field_211298_n.in(var1))), "SpawnData", TypeReferences.field_211298_n.in(var1));
      });
      var1.registerType(false, TypeReferences.field_211293_i, () -> {
         return DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.field_211305_u.in(var1), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.field_211297_m.in(var1), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.field_211297_m.in(var1), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.field_211297_m.in(var1), DSL.constType(DSL.string()))));
      });
      var1.registerType(false, TypeReferences.field_211305_u, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      var1.registerType(false, TypeReferences.field_211297_m, () -> {
         return DSL.constType(DSL.namespacedString());
      });
   }
}
