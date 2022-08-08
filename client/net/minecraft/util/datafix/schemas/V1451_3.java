package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1451_3 extends NamespacedSchema {
   public V1451_3(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var1.registerSimple(var2, "minecraft:egg");
      var1.registerSimple(var2, "minecraft:ender_pearl");
      var1.registerSimple(var2, "minecraft:fireball");
      var1.register(var2, "minecraft:potion", (var1x) -> {
         return DSL.optionalFields("Potion", References.ITEM_STACK.in(var1));
      });
      var1.registerSimple(var2, "minecraft:small_fireball");
      var1.registerSimple(var2, "minecraft:snowball");
      var1.registerSimple(var2, "minecraft:wither_skull");
      var1.registerSimple(var2, "minecraft:xp_bottle");
      var1.register(var2, "minecraft:arrow", () -> {
         return DSL.optionalFields("inBlockState", References.BLOCK_STATE.in(var1));
      });
      var1.register(var2, "minecraft:enderman", () -> {
         return DSL.optionalFields("carriedBlockState", References.BLOCK_STATE.in(var1), V100.equipment(var1));
      });
      var1.register(var2, "minecraft:falling_block", () -> {
         return DSL.optionalFields("BlockState", References.BLOCK_STATE.in(var1), "TileEntityData", References.BLOCK_ENTITY.in(var1));
      });
      var1.register(var2, "minecraft:spectral_arrow", () -> {
         return DSL.optionalFields("inBlockState", References.BLOCK_STATE.in(var1));
      });
      var1.register(var2, "minecraft:chest_minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1), "Items", DSL.list(References.ITEM_STACK.in(var1)));
      });
      var1.register(var2, "minecraft:commandblock_minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1));
      });
      var1.register(var2, "minecraft:furnace_minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1));
      });
      var1.register(var2, "minecraft:hopper_minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1), "Items", DSL.list(References.ITEM_STACK.in(var1)));
      });
      var1.register(var2, "minecraft:minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1));
      });
      var1.register(var2, "minecraft:spawner_minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1), References.UNTAGGED_SPAWNER.in(var1));
      });
      var1.register(var2, "minecraft:tnt_minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(var1));
      });
      return var2;
   }
}
