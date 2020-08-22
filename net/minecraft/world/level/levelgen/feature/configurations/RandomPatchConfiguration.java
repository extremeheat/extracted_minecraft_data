package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacer;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;

public class RandomPatchConfiguration implements FeatureConfiguration {
   public final BlockStateProvider stateProvider;
   public final BlockPlacer blockPlacer;
   public final Set whitelist;
   public final Set blacklist;
   public final int tries;
   public final int xspread;
   public final int yspread;
   public final int zspread;
   public final boolean canReplace;
   public final boolean project;
   public final boolean needWater;

   private RandomPatchConfiguration(BlockStateProvider var1, BlockPlacer var2, Set var3, Set var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10, boolean var11) {
      this.stateProvider = var1;
      this.blockPlacer = var2;
      this.whitelist = var3;
      this.blacklist = var4;
      this.tries = var5;
      this.xspread = var6;
      this.yspread = var7;
      this.zspread = var8;
      this.canReplace = var9;
      this.project = var10;
      this.needWater = var11;
   }

   public Dynamic serialize(DynamicOps var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var1.createString("state_provider"), this.stateProvider.serialize(var1)).put(var1.createString("block_placer"), this.blockPlacer.serialize(var1)).put(var1.createString("whitelist"), var1.createList(this.whitelist.stream().map((var1x) -> {
         return BlockState.serialize(var1, var1x.defaultBlockState()).getValue();
      }))).put(var1.createString("blacklist"), var1.createList(this.blacklist.stream().map((var1x) -> {
         return BlockState.serialize(var1, var1x).getValue();
      }))).put(var1.createString("tries"), var1.createInt(this.tries)).put(var1.createString("xspread"), var1.createInt(this.xspread)).put(var1.createString("yspread"), var1.createInt(this.yspread)).put(var1.createString("zspread"), var1.createInt(this.zspread)).put(var1.createString("can_replace"), var1.createBoolean(this.canReplace)).put(var1.createString("project"), var1.createBoolean(this.project)).put(var1.createString("need_water"), var1.createBoolean(this.needWater));
      return new Dynamic(var1, var1.createMap(var2.build()));
   }

   public static RandomPatchConfiguration deserialize(Dynamic var0) {
      BlockStateProviderType var1 = (BlockStateProviderType)Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation((String)var0.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      BlockPlacerType var2 = (BlockPlacerType)Registry.BLOCK_PLACER_TYPES.get(new ResourceLocation((String)var0.get("block_placer").get("type").asString().orElseThrow(RuntimeException::new)));
      return new RandomPatchConfiguration(var1.deserialize(var0.get("state_provider").orElseEmptyMap()), var2.deserialize(var0.get("block_placer").orElseEmptyMap()), (Set)var0.get("whitelist").asList(BlockState::deserialize).stream().map(BlockState::getBlock).collect(Collectors.toSet()), Sets.newHashSet(var0.get("blacklist").asList(BlockState::deserialize)), var0.get("tries").asInt(128), var0.get("xspread").asInt(7), var0.get("yspread").asInt(3), var0.get("zspread").asInt(7), var0.get("can_replace").asBoolean(false), var0.get("project").asBoolean(true), var0.get("need_water").asBoolean(false));
   }

   // $FF: synthetic method
   RandomPatchConfiguration(BlockStateProvider var1, BlockPlacer var2, Set var3, Set var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10, boolean var11, Object var12) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static class GrassConfigurationBuilder {
      private final BlockStateProvider stateProvider;
      private final BlockPlacer blockPlacer;
      private Set whitelist = ImmutableSet.of();
      private Set blacklist = ImmutableSet.of();
      private int tries = 64;
      private int xspread = 7;
      private int yspread = 3;
      private int zspread = 7;
      private boolean canReplace;
      private boolean project = true;
      private boolean needWater = false;

      public GrassConfigurationBuilder(BlockStateProvider var1, BlockPlacer var2) {
         this.stateProvider = var1;
         this.blockPlacer = var2;
      }

      public RandomPatchConfiguration.GrassConfigurationBuilder whitelist(Set var1) {
         this.whitelist = var1;
         return this;
      }

      public RandomPatchConfiguration.GrassConfigurationBuilder blacklist(Set var1) {
         this.blacklist = var1;
         return this;
      }

      public RandomPatchConfiguration.GrassConfigurationBuilder tries(int var1) {
         this.tries = var1;
         return this;
      }

      public RandomPatchConfiguration.GrassConfigurationBuilder xspread(int var1) {
         this.xspread = var1;
         return this;
      }

      public RandomPatchConfiguration.GrassConfigurationBuilder yspread(int var1) {
         this.yspread = var1;
         return this;
      }

      public RandomPatchConfiguration.GrassConfigurationBuilder zspread(int var1) {
         this.zspread = var1;
         return this;
      }

      public RandomPatchConfiguration.GrassConfigurationBuilder canReplace() {
         this.canReplace = true;
         return this;
      }

      public RandomPatchConfiguration.GrassConfigurationBuilder noProjection() {
         this.project = false;
         return this;
      }

      public RandomPatchConfiguration.GrassConfigurationBuilder needWater() {
         this.needWater = true;
         return this;
      }

      public RandomPatchConfiguration build() {
         return new RandomPatchConfiguration(this.stateProvider, this.blockPlacer, this.whitelist, this.blacklist, this.tries, this.xspread, this.yspread, this.zspread, this.canReplace, this.project, this.needWater);
      }
   }
}
