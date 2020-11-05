package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class RandomPatchConfiguration implements FeatureConfiguration {
   public static final Codec<RandomPatchConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockStateProvider.CODEC.fieldOf("state_provider").forGetter((var0x) -> {
         return var0x.stateProvider;
      }), BlockPlacer.CODEC.fieldOf("block_placer").forGetter((var0x) -> {
         return var0x.blockPlacer;
      }), BlockState.CODEC.listOf().fieldOf("whitelist").forGetter((var0x) -> {
         return (List)var0x.whitelist.stream().map(Block::defaultBlockState).collect(Collectors.toList());
      }), BlockState.CODEC.listOf().fieldOf("blacklist").forGetter((var0x) -> {
         return ImmutableList.copyOf(var0x.blacklist);
      }), Codec.INT.fieldOf("tries").orElse(128).forGetter((var0x) -> {
         return var0x.tries;
      }), Codec.INT.fieldOf("xspread").orElse(7).forGetter((var0x) -> {
         return var0x.xspread;
      }), Codec.INT.fieldOf("yspread").orElse(3).forGetter((var0x) -> {
         return var0x.yspread;
      }), Codec.INT.fieldOf("zspread").orElse(7).forGetter((var0x) -> {
         return var0x.zspread;
      }), Codec.BOOL.fieldOf("can_replace").orElse(false).forGetter((var0x) -> {
         return var0x.canReplace;
      }), Codec.BOOL.fieldOf("project").orElse(true).forGetter((var0x) -> {
         return var0x.project;
      }), Codec.BOOL.fieldOf("need_water").orElse(false).forGetter((var0x) -> {
         return var0x.needWater;
      })).apply(var0, RandomPatchConfiguration::new);
   });
   public final BlockStateProvider stateProvider;
   public final BlockPlacer blockPlacer;
   public final Set<Block> whitelist;
   public final Set<BlockState> blacklist;
   public final int tries;
   public final int xspread;
   public final int yspread;
   public final int zspread;
   public final boolean canReplace;
   public final boolean project;
   public final boolean needWater;

   private RandomPatchConfiguration(BlockStateProvider var1, BlockPlacer var2, List<BlockState> var3, List<BlockState> var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10, boolean var11) {
      this(var1, var2, (Set)((Set)var3.stream().map(BlockBehaviour.BlockStateBase::getBlock).collect(Collectors.toSet())), (Set)ImmutableSet.copyOf(var4), var5, var6, var7, var8, var9, var10, var11);
   }

   private RandomPatchConfiguration(BlockStateProvider var1, BlockPlacer var2, Set<Block> var3, Set<BlockState> var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10, boolean var11) {
      super();
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

   // $FF: synthetic method
   RandomPatchConfiguration(BlockStateProvider var1, BlockPlacer var2, Set var3, Set var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10, boolean var11, Object var12) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static class GrassConfigurationBuilder {
      private final BlockStateProvider stateProvider;
      private final BlockPlacer blockPlacer;
      private Set<Block> whitelist = ImmutableSet.of();
      private Set<BlockState> blacklist = ImmutableSet.of();
      private int tries = 64;
      private int xspread = 7;
      private int yspread = 3;
      private int zspread = 7;
      private boolean canReplace;
      private boolean project = true;
      private boolean needWater;

      public GrassConfigurationBuilder(BlockStateProvider var1, BlockPlacer var2) {
         super();
         this.stateProvider = var1;
         this.blockPlacer = var2;
      }

      public RandomPatchConfiguration.GrassConfigurationBuilder whitelist(Set<Block> var1) {
         this.whitelist = var1;
         return this;
      }

      public RandomPatchConfiguration.GrassConfigurationBuilder blacklist(Set<BlockState> var1) {
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
