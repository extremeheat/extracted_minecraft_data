package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V0705 extends NamespacedSchema {
   protected static final HookFunction field_206597_b = new HookFunction() {
      public <T> T apply(DynamicOps<T> var1, T var2) {
         return V0099.func_209869_a(new Dynamic(var1, var2), V0704.field_206647_b, "minecraft:armor_stand");
      }
   };

   public V0705(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static void func_206596_a(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return V0100.func_206605_a(var0);
      });
   }

   protected static void func_206581_b(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("inTile", TypeReferences.field_211300_p.in(var0));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      HashMap var2 = Maps.newHashMap();
      var1.registerSimple(var2, "minecraft:area_effect_cloud");
      func_206596_a(var1, var2, "minecraft:armor_stand");
      var1.register(var2, "minecraft:arrow", (var1x) -> {
         return DSL.optionalFields("inTile", TypeReferences.field_211300_p.in(var1));
      });
      func_206596_a(var1, var2, "minecraft:bat");
      func_206596_a(var1, var2, "minecraft:blaze");
      var1.registerSimple(var2, "minecraft:boat");
      func_206596_a(var1, var2, "minecraft:cave_spider");
      var1.register(var2, "minecraft:chest_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.field_211300_p.in(var1), "Items", DSL.list(TypeReferences.field_211295_k.in(var1)));
      });
      func_206596_a(var1, var2, "minecraft:chicken");
      var1.register(var2, "minecraft:commandblock_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.field_211300_p.in(var1));
      });
      func_206596_a(var1, var2, "minecraft:cow");
      func_206596_a(var1, var2, "minecraft:creeper");
      var1.register(var2, "minecraft:donkey", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var1)), "SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      var1.registerSimple(var2, "minecraft:dragon_fireball");
      func_206581_b(var1, var2, "minecraft:egg");
      func_206596_a(var1, var2, "minecraft:elder_guardian");
      var1.registerSimple(var2, "minecraft:ender_crystal");
      func_206596_a(var1, var2, "minecraft:ender_dragon");
      var1.register(var2, "minecraft:enderman", (var1x) -> {
         return DSL.optionalFields("carried", TypeReferences.field_211300_p.in(var1), V0100.func_206605_a(var1));
      });
      func_206596_a(var1, var2, "minecraft:endermite");
      func_206581_b(var1, var2, "minecraft:ender_pearl");
      var1.registerSimple(var2, "minecraft:eye_of_ender_signal");
      var1.register(var2, "minecraft:falling_block", (var1x) -> {
         return DSL.optionalFields("Block", TypeReferences.field_211300_p.in(var1), "TileEntityData", TypeReferences.field_211294_j.in(var1));
      });
      func_206581_b(var1, var2, "minecraft:fireball");
      var1.register(var2, "minecraft:fireworks_rocket", (var1x) -> {
         return DSL.optionalFields("FireworksItem", TypeReferences.field_211295_k.in(var1));
      });
      var1.register(var2, "minecraft:furnace_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.field_211300_p.in(var1));
      });
      func_206596_a(var1, var2, "minecraft:ghast");
      func_206596_a(var1, var2, "minecraft:giant");
      func_206596_a(var1, var2, "minecraft:guardian");
      var1.register(var2, "minecraft:hopper_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.field_211300_p.in(var1), "Items", DSL.list(TypeReferences.field_211295_k.in(var1)));
      });
      var1.register(var2, "minecraft:horse", (var1x) -> {
         return DSL.optionalFields("ArmorItem", TypeReferences.field_211295_k.in(var1), "SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      func_206596_a(var1, var2, "minecraft:husk");
      var1.register(var2, "minecraft:item", (var1x) -> {
         return DSL.optionalFields("Item", TypeReferences.field_211295_k.in(var1));
      });
      var1.register(var2, "minecraft:item_frame", (var1x) -> {
         return DSL.optionalFields("Item", TypeReferences.field_211295_k.in(var1));
      });
      var1.registerSimple(var2, "minecraft:leash_knot");
      func_206596_a(var1, var2, "minecraft:magma_cube");
      var1.register(var2, "minecraft:minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.field_211300_p.in(var1));
      });
      func_206596_a(var1, var2, "minecraft:mooshroom");
      var1.register(var2, "minecraft:mule", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var1)), "SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      func_206596_a(var1, var2, "minecraft:ocelot");
      var1.registerSimple(var2, "minecraft:painting");
      var1.registerSimple(var2, "minecraft:parrot");
      func_206596_a(var1, var2, "minecraft:pig");
      func_206596_a(var1, var2, "minecraft:polar_bear");
      var1.register(var2, "minecraft:potion", (var1x) -> {
         return DSL.optionalFields("Potion", TypeReferences.field_211295_k.in(var1), "inTile", TypeReferences.field_211300_p.in(var1));
      });
      func_206596_a(var1, var2, "minecraft:rabbit");
      func_206596_a(var1, var2, "minecraft:sheep");
      func_206596_a(var1, var2, "minecraft:shulker");
      var1.registerSimple(var2, "minecraft:shulker_bullet");
      func_206596_a(var1, var2, "minecraft:silverfish");
      func_206596_a(var1, var2, "minecraft:skeleton");
      var1.register(var2, "minecraft:skeleton_horse", (var1x) -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      func_206596_a(var1, var2, "minecraft:slime");
      func_206581_b(var1, var2, "minecraft:small_fireball");
      func_206581_b(var1, var2, "minecraft:snowball");
      func_206596_a(var1, var2, "minecraft:snowman");
      var1.register(var2, "minecraft:spawner_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.field_211300_p.in(var1), TypeReferences.field_211302_r.in(var1));
      });
      var1.register(var2, "minecraft:spectral_arrow", (var1x) -> {
         return DSL.optionalFields("inTile", TypeReferences.field_211300_p.in(var1));
      });
      func_206596_a(var1, var2, "minecraft:spider");
      func_206596_a(var1, var2, "minecraft:squid");
      func_206596_a(var1, var2, "minecraft:stray");
      var1.registerSimple(var2, "minecraft:tnt");
      var1.register(var2, "minecraft:tnt_minecart", (var1x) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.field_211300_p.in(var1));
      });
      var1.register(var2, "minecraft:villager", (var1x) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.field_211295_k.in(var1)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.field_211295_k.in(var1), "buyB", TypeReferences.field_211295_k.in(var1), "sell", TypeReferences.field_211295_k.in(var1)))), V0100.func_206605_a(var1));
      });
      func_206596_a(var1, var2, "minecraft:villager_golem");
      func_206596_a(var1, var2, "minecraft:witch");
      func_206596_a(var1, var2, "minecraft:wither");
      func_206596_a(var1, var2, "minecraft:wither_skeleton");
      func_206581_b(var1, var2, "minecraft:wither_skull");
      func_206596_a(var1, var2, "minecraft:wolf");
      func_206581_b(var1, var2, "minecraft:xp_bottle");
      var1.registerSimple(var2, "minecraft:xp_orb");
      func_206596_a(var1, var2, "minecraft:zombie");
      var1.register(var2, "minecraft:zombie_horse", (var1x) -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      func_206596_a(var1, var2, "minecraft:zombie_pigman");
      func_206596_a(var1, var2, "minecraft:zombie_villager");
      var1.registerSimple(var2, "minecraft:evocation_fangs");
      func_206596_a(var1, var2, "minecraft:evocation_illager");
      var1.registerSimple(var2, "minecraft:illusion_illager");
      var1.register(var2, "minecraft:llama", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var1)), "SaddleItem", TypeReferences.field_211295_k.in(var1), "DecorItem", TypeReferences.field_211295_k.in(var1), V0100.func_206605_a(var1));
      });
      var1.registerSimple(var2, "minecraft:llama_spit");
      func_206596_a(var1, var2, "minecraft:vex");
      func_206596_a(var1, var2, "minecraft:vindication_illager");
      return var2;
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(true, TypeReferences.field_211299_o, () -> {
         return DSL.taggedChoiceLazy("id", DSL.namespacedString(), var2);
      });
      var1.registerType(true, TypeReferences.field_211295_k, () -> {
         return DSL.hook(DSL.optionalFields("id", TypeReferences.field_211301_q.in(var1), "tag", DSL.optionalFields("EntityTag", TypeReferences.field_211298_n.in(var1), "BlockEntityTag", TypeReferences.field_211294_j.in(var1), "CanDestroy", DSL.list(TypeReferences.field_211300_p.in(var1)), "CanPlaceOn", DSL.list(TypeReferences.field_211300_p.in(var1)))), field_206597_b, HookFunction.IDENTITY);
      });
   }
}
