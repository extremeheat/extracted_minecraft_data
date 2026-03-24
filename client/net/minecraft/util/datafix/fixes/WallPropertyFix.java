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

   public WallPropertyFix(final Schema outputSchema, final boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("WallPropertyFix", this.getInputSchema().getType(References.BLOCK_STATE), (input) -> input.update(DSL.remainderFinder(), WallPropertyFix::upgradeBlockStateTag));
   }

   private static String mapProperty(final String value) {
      return "true".equals(value) ? "low" : "none";
   }

   private static <T> Dynamic<T> fixWallProperty(final Dynamic<T> state, final String property) {
      return state.update(property, (value) -> {
         Optional var10000 = value.asString().result().map(WallPropertyFix::mapProperty);
         Objects.requireNonNull(value);
         return (Dynamic)DataFixUtils.orElse(var10000.map(value::createString), value);
      });
   }

   private static <T> Dynamic<T> upgradeBlockStateTag(final Dynamic<T> state) {
      Optional var10000 = state.get("Name").asString().result();
      Set var10001 = WALL_BLOCKS;
      Objects.requireNonNull(var10001);
      boolean isWall = var10000.filter(var10001::contains).isPresent();
      return !isWall ? state : state.update("Properties", (properties) -> {
         Dynamic<?> newState = fixWallProperty(properties, "east");
         newState = fixWallProperty(newState, "west");
         newState = fixWallProperty(newState, "north");
         return fixWallProperty(newState, "south");
      });
   }
}
