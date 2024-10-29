package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4067 extends NamespacedSchema {
   public V4067(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var2.remove("minecraft:boat");
      var2.remove("minecraft:chest_boat");
      this.registerSimple(var2, "minecraft:oak_boat");
      this.registerSimple(var2, "minecraft:spruce_boat");
      this.registerSimple(var2, "minecraft:birch_boat");
      this.registerSimple(var2, "minecraft:jungle_boat");
      this.registerSimple(var2, "minecraft:acacia_boat");
      this.registerSimple(var2, "minecraft:cherry_boat");
      this.registerSimple(var2, "minecraft:dark_oak_boat");
      this.registerSimple(var2, "minecraft:mangrove_boat");
      this.registerSimple(var2, "minecraft:bamboo_raft");
      this.registerChestBoat(var2, "minecraft:oak_chest_boat");
      this.registerChestBoat(var2, "minecraft:spruce_chest_boat");
      this.registerChestBoat(var2, "minecraft:birch_chest_boat");
      this.registerChestBoat(var2, "minecraft:jungle_chest_boat");
      this.registerChestBoat(var2, "minecraft:acacia_chest_boat");
      this.registerChestBoat(var2, "minecraft:cherry_chest_boat");
      this.registerChestBoat(var2, "minecraft:dark_oak_chest_boat");
      this.registerChestBoat(var2, "minecraft:mangrove_chest_boat");
      this.registerChestBoat(var2, "minecraft:bamboo_chest_raft");
      return var2;
   }

   private void registerChestBoat(Map<String, Supplier<TypeTemplate>> var1, String var2) {
      this.register(var1, var2, (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(this)));
      });
   }
}
