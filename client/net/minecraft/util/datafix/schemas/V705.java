package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V705 extends NamespacedSchema {
   protected static final HookFunction ADD_NAMES = new HookFunction() {
      public <T> T apply(DynamicOps<T> var1, T var2) {
         return V99.addNames(new Dynamic(var1, var2), V704.ITEM_TO_BLOCKENTITY, "minecraft:armor_stand");
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
      var1.registerSimple(var2, "minecraft:area_effect_cloud");
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
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(var1)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", References.ITEM_STACK.in(var1), "buyB", References.ITEM_STACK.in(var1), "sell", References.ITEM_STACK.in(var1)))), V100.equipment(var1));
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
      registerMob(var1, var2, "minecraft:zombie_villager");
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
         return DSL.hook(DSL.optionalFields("id", References.ITEM_NAME.in(var1), "tag", DSL.optionalFields("EntityTag", References.ENTITY_TREE.in(var1), "BlockEntityTag", References.BLOCK_ENTITY.in(var1), "CanDestroy", DSL.list(References.BLOCK_NAME.in(var1)), "CanPlaceOn", DSL.list(References.BLOCK_NAME.in(var1)), "Items", DSL.list(References.ITEM_STACK.in(var1)))), ADD_NAMES, HookFunction.IDENTITY);
      });
   }
}
