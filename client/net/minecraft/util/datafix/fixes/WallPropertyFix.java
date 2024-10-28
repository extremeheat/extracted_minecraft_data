package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class WallPropertyFix extends DataFix {
   private static final Set<String> WALL_BLOCKS = ImmutableSet.of("minecraft:andesite_wall", "minecraft:brick_wall", "minecraft:cobblestone_wall", "minecraft:diorite_wall", "minecraft:end_stone_brick_wall", "minecraft:granite_wall", new String[]{"minecraft:mossy_cobblestone_wall", "minecraft:mossy_stone_brick_wall", "minecraft:nether_brick_wall", "minecraft:prismarine_wall", "minecraft:red_nether_brick_wall", "minecraft:red_sandstone_wall", "minecraft:sandstone_wall", "minecraft:stone_brick_wall"});

   public WallPropertyFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("WallPropertyFix", this.getInputSchema().getType(References.BLOCK_STATE), (var0) -> {
         return var0.update(DSL.remainderFinder(), WallPropertyFix::upgradeBlockStateTag);
      });
   }

   private static String mapProperty(String var0) {
      return "true".equals(var0) ? "low" : "none";
   }

   private static <T> Dynamic<T> fixWallProperty(Dynamic<T> var0, String var1) {
      return var0.update(var1, (var0x) -> {
         Optional var10000 = var0x.asString().result().map(WallPropertyFix::mapProperty);
         Objects.requireNonNull(var0x);
         return (Dynamic)DataFixUtils.orElse(var10000.map(var0x::createString), var0x);
      });
   }

   private static <T> Dynamic<T> upgradeBlockStateTag(Dynamic<T> var0) {
      Optional var10000 = var0.get("Name").asString().result();
      Set var10001 = WALL_BLOCKS;
      Objects.requireNonNull(var10001);
      boolean var1 = var10000.filter(var10001::contains).isPresent();
      return !var1 ? var0 : var0.update("Properties", (var0x) -> {
         Dynamic var1 = fixWallProperty(var0x, "east");
         var1 = fixWallProperty(var1, "west");
         var1 = fixWallProperty(var1, "north");
         return fixWallProperty(var1, "south");
      });
   }
}
