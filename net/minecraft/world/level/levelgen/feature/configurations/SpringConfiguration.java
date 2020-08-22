package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class SpringConfiguration implements FeatureConfiguration {
   public final FluidState state;
   public final boolean requiresBlockBelow;
   public final int rockCount;
   public final int holeCount;
   public final Set validBlocks;

   public SpringConfiguration(FluidState var1, boolean var2, int var3, int var4, Set var5) {
      this.state = var1;
      this.requiresBlockBelow = var2;
      this.rockCount = var3;
      this.holeCount = var4;
      this.validBlocks = var5;
   }

   public Dynamic serialize(DynamicOps var1) {
      Object var10004 = var1.createString("state");
      Object var10005 = FluidState.serialize(var1, this.state).getValue();
      Object var10006 = var1.createString("requires_block_below");
      Object var10007 = var1.createBoolean(this.requiresBlockBelow);
      Object var10008 = var1.createString("rock_count");
      Object var10009 = var1.createInt(this.rockCount);
      Object var10010 = var1.createString("hole_count");
      Object var10011 = var1.createInt(this.holeCount);
      Object var10012 = var1.createString("valid_blocks");
      Stream var10014 = this.validBlocks.stream();
      DefaultedRegistry var10015 = Registry.BLOCK;
      var10015.getClass();
      var10014 = var10014.map(var10015::getKey).map(ResourceLocation::toString);
      var1.getClass();
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var10004, var10005, var10006, var10007, var10008, var10009, var10010, var10011, var10012, var1.createList(var10014.map(var1::createString)))));
   }

   public static SpringConfiguration deserialize(Dynamic var0) {
      return new SpringConfiguration((FluidState)var0.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.defaultFluidState()), var0.get("requires_block_below").asBoolean(true), var0.get("rock_count").asInt(4), var0.get("hole_count").asInt(1), ImmutableSet.copyOf(var0.get("valid_blocks").asList((var0x) -> {
         return (Block)Registry.BLOCK.get(new ResourceLocation(var0x.asString("minecraft:air")));
      })));
   }
}
