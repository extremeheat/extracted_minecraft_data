package net.minecraft.world.level.block.grower;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public final class TreeGrower {
   private static final Map<String, TreeGrower> GROWERS = new Object2ObjectArrayMap();
   public static final Codec<TreeGrower> CODEC;
   public static final TreeGrower OAK;
   public static final TreeGrower SPRUCE;
   public static final TreeGrower MANGROVE;
   public static final TreeGrower AZALEA;
   public static final TreeGrower BIRCH;
   public static final TreeGrower JUNGLE;
   public static final TreeGrower ACACIA;
   public static final TreeGrower CHERRY;
   public static final TreeGrower DARK_OAK;
   private final String name;
   private final float secondaryChance;
   private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> megaTree;
   private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> secondaryMegaTree;
   private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> tree;
   private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> secondaryTree;
   private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> flowers;
   private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> secondaryFlowers;

   public TreeGrower(String var1, Optional<ResourceKey<ConfiguredFeature<?, ?>>> var2, Optional<ResourceKey<ConfiguredFeature<?, ?>>> var3, Optional<ResourceKey<ConfiguredFeature<?, ?>>> var4) {
      this(var1, 0.0F, var2, Optional.empty(), var3, Optional.empty(), var4, Optional.empty());
   }

   public TreeGrower(String var1, float var2, Optional<ResourceKey<ConfiguredFeature<?, ?>>> var3, Optional<ResourceKey<ConfiguredFeature<?, ?>>> var4, Optional<ResourceKey<ConfiguredFeature<?, ?>>> var5, Optional<ResourceKey<ConfiguredFeature<?, ?>>> var6, Optional<ResourceKey<ConfiguredFeature<?, ?>>> var7, Optional<ResourceKey<ConfiguredFeature<?, ?>>> var8) {
      super();
      this.name = var1;
      this.secondaryChance = var2;
      this.megaTree = var3;
      this.secondaryMegaTree = var4;
      this.tree = var5;
      this.secondaryTree = var6;
      this.flowers = var7;
      this.secondaryFlowers = var8;
      GROWERS.put(var1, this);
   }

   @Nullable
   private ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource var1, boolean var2) {
      if (var1.nextFloat() < this.secondaryChance) {
         if (var2 && this.secondaryFlowers.isPresent()) {
            return (ResourceKey)this.secondaryFlowers.get();
         }

         if (this.secondaryTree.isPresent()) {
            return (ResourceKey)this.secondaryTree.get();
         }
      }

      return var2 && this.flowers.isPresent() ? (ResourceKey)this.flowers.get() : (ResourceKey)this.tree.orElse((Object)null);
   }

   @Nullable
   private ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource var1) {
      return this.secondaryMegaTree.isPresent() && var1.nextFloat() < this.secondaryChance ? (ResourceKey)this.secondaryMegaTree.get() : (ResourceKey)this.megaTree.orElse((Object)null);
   }

   public boolean growTree(ServerLevel var1, ChunkGenerator var2, BlockPos var3, BlockState var4, RandomSource var5) {
      ResourceKey var6 = this.getConfiguredMegaFeature(var5);
      if (var6 != null) {
         Holder var7 = (Holder)var1.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(var6).orElse((Object)null);
         if (var7 != null) {
            for(int var8 = 0; var8 >= -1; --var8) {
               for(int var9 = 0; var9 >= -1; --var9) {
                  if (isTwoByTwoSapling(var4, var1, var3, var8, var9)) {
                     ConfiguredFeature var10 = (ConfiguredFeature)var7.value();
                     BlockState var11 = Blocks.AIR.defaultBlockState();
                     var1.setBlock(var3.offset(var8, 0, var9), var11, 4);
                     var1.setBlock(var3.offset(var8 + 1, 0, var9), var11, 4);
                     var1.setBlock(var3.offset(var8, 0, var9 + 1), var11, 4);
                     var1.setBlock(var3.offset(var8 + 1, 0, var9 + 1), var11, 4);
                     if (var10.place(var1, var2, var5, var3.offset(var8, 0, var9))) {
                        return true;
                     }

                     var1.setBlock(var3.offset(var8, 0, var9), var4, 4);
                     var1.setBlock(var3.offset(var8 + 1, 0, var9), var4, 4);
                     var1.setBlock(var3.offset(var8, 0, var9 + 1), var4, 4);
                     var1.setBlock(var3.offset(var8 + 1, 0, var9 + 1), var4, 4);
                     return false;
                  }
               }
            }
         }
      }

      ResourceKey var12 = this.getConfiguredFeature(var5, this.hasFlowers(var1, var3));
      if (var12 == null) {
         return false;
      } else {
         Holder var13 = (Holder)var1.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(var12).orElse((Object)null);
         if (var13 == null) {
            return false;
         } else {
            ConfiguredFeature var14 = (ConfiguredFeature)var13.value();
            BlockState var15 = var1.getFluidState(var3).createLegacyBlock();
            var1.setBlock(var3, var15, 4);
            if (var14.place(var1, var2, var5, var3)) {
               if (var1.getBlockState(var3) == var15) {
                  var1.sendBlockUpdated(var3, var4, var15, 2);
               }

               return true;
            } else {
               var1.setBlock(var3, var4, 4);
               return false;
            }
         }
      }
   }

   private static boolean isTwoByTwoSapling(BlockState var0, BlockGetter var1, BlockPos var2, int var3, int var4) {
      Block var5 = var0.getBlock();
      return var1.getBlockState(var2.offset(var3, 0, var4)).is(var5) && var1.getBlockState(var2.offset(var3 + 1, 0, var4)).is(var5) && var1.getBlockState(var2.offset(var3, 0, var4 + 1)).is(var5) && var1.getBlockState(var2.offset(var3 + 1, 0, var4 + 1)).is(var5);
   }

   private boolean hasFlowers(LevelAccessor var1, BlockPos var2) {
      Iterator var3 = BlockPos.MutableBlockPos.betweenClosed(var2.below().north(2).west(2), var2.above().south(2).east(2)).iterator();

      BlockPos var4;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         var4 = (BlockPos)var3.next();
      } while(!var1.getBlockState(var4).is(BlockTags.FLOWERS));

      return true;
   }

   static {
      Function var10000 = (var0) -> {
         return var0.name;
      };
      Map var10001 = GROWERS;
      Objects.requireNonNull(var10001);
      CODEC = Codec.stringResolver(var10000, var10001::get);
      OAK = new TreeGrower("oak", 0.1F, Optional.empty(), Optional.empty(), Optional.of(TreeFeatures.OAK), Optional.of(TreeFeatures.FANCY_OAK), Optional.of(TreeFeatures.OAK_BEES_005), Optional.of(TreeFeatures.FANCY_OAK_BEES_005));
      SPRUCE = new TreeGrower("spruce", 0.5F, Optional.of(TreeFeatures.MEGA_SPRUCE), Optional.of(TreeFeatures.MEGA_PINE), Optional.of(TreeFeatures.SPRUCE), Optional.empty(), Optional.empty(), Optional.empty());
      MANGROVE = new TreeGrower("mangrove", 0.85F, Optional.empty(), Optional.empty(), Optional.of(TreeFeatures.MANGROVE), Optional.of(TreeFeatures.TALL_MANGROVE), Optional.empty(), Optional.empty());
      AZALEA = new TreeGrower("azalea", Optional.empty(), Optional.of(TreeFeatures.AZALEA_TREE), Optional.empty());
      BIRCH = new TreeGrower("birch", Optional.empty(), Optional.of(TreeFeatures.BIRCH), Optional.of(TreeFeatures.BIRCH_BEES_005));
      JUNGLE = new TreeGrower("jungle", Optional.of(TreeFeatures.MEGA_JUNGLE_TREE), Optional.of(TreeFeatures.JUNGLE_TREE_NO_VINE), Optional.empty());
      ACACIA = new TreeGrower("acacia", Optional.empty(), Optional.of(TreeFeatures.ACACIA), Optional.empty());
      CHERRY = new TreeGrower("cherry", Optional.empty(), Optional.of(TreeFeatures.CHERRY), Optional.of(TreeFeatures.CHERRY_BEES_005));
      DARK_OAK = new TreeGrower("dark_oak", Optional.of(TreeFeatures.DARK_OAK), Optional.empty(), Optional.empty());
   }
}
