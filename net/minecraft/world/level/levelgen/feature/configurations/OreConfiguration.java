package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;

public class OreConfiguration implements FeatureConfiguration {
   public final OreConfiguration.Predicates target;
   public final int size;
   public final BlockState state;

   public OreConfiguration(OreConfiguration.Predicates var1, BlockState var2, int var3) {
      this.size = var3;
      this.state = var2;
      this.target = var1;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("size"), var1.createInt(this.size), var1.createString("target"), var1.createString(this.target.getName()), var1.createString("state"), BlockState.serialize(var1, this.state).getValue())));
   }

   public static OreConfiguration deserialize(Dynamic var0) {
      int var1 = var0.get("size").asInt(0);
      OreConfiguration.Predicates var2 = OreConfiguration.Predicates.byName(var0.get("target").asString(""));
      BlockState var3 = (BlockState)var0.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      return new OreConfiguration(var2, var3, var1);
   }

   public static enum Predicates {
      NATURAL_STONE("natural_stone", (var0) -> {
         if (var0 == null) {
            return false;
         } else {
            Block var1 = var0.getBlock();
            return var1 == Blocks.STONE || var1 == Blocks.GRANITE || var1 == Blocks.DIORITE || var1 == Blocks.ANDESITE;
         }
      }),
      NETHERRACK("netherrack", new BlockPredicate(Blocks.NETHERRACK));

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(OreConfiguration.Predicates::getName, (var0) -> {
         return var0;
      }));
      private final String name;
      private final Predicate predicate;

      private Predicates(String var3, Predicate var4) {
         this.name = var3;
         this.predicate = var4;
      }

      public String getName() {
         return this.name;
      }

      public static OreConfiguration.Predicates byName(String var0) {
         return (OreConfiguration.Predicates)BY_NAME.get(var0);
      }

      public Predicate getPredicate() {
         return this.predicate;
      }
   }
}
