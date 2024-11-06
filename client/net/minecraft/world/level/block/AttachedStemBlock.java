package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AttachedStemBlock extends BushBlock {
   public static final MapCodec<AttachedStemBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ResourceKey.codec(Registries.BLOCK).fieldOf("fruit").forGetter((var0x) -> {
         return var0x.fruit;
      }), ResourceKey.codec(Registries.BLOCK).fieldOf("stem").forGetter((var0x) -> {
         return var0x.stem;
      }), ResourceKey.codec(Registries.ITEM).fieldOf("seed").forGetter((var0x) -> {
         return var0x.seed;
      }), propertiesCodec()).apply(var0, AttachedStemBlock::new);
   });
   public static final EnumProperty<Direction> FACING;
   protected static final float AABB_OFFSET = 2.0F;
   private static final Map<Direction, VoxelShape> AABBS;
   private final ResourceKey<Block> fruit;
   private final ResourceKey<Block> stem;
   private final ResourceKey<Item> seed;

   public MapCodec<AttachedStemBlock> codec() {
      return CODEC;
   }

   protected AttachedStemBlock(ResourceKey<Block> var1, ResourceKey<Block> var2, ResourceKey<Item> var3, BlockBehaviour.Properties var4) {
      super(var4);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
      this.stem = var1;
      this.fruit = var2;
      this.seed = var3;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)AABBS.get(var1.getValue(FACING));
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if (!var7.is(this.fruit) && var5 == var1.getValue(FACING)) {
         Optional var9 = var2.registryAccess().lookupOrThrow(Registries.BLOCK).getOptional(this.stem);
         if (var9.isPresent()) {
            return (BlockState)((Block)var9.get()).defaultBlockState().trySetValue(StemBlock.AGE, 7);
         }
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(Blocks.FARMLAND);
   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      return new ItemStack((ItemLike)DataFixUtils.orElse(var1.registryAccess().lookupOrThrow(Registries.ITEM).getOptional(this.seed), this));
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.SOUTH, Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 16.0), Direction.WEST, Block.box(0.0, 0.0, 6.0, 10.0, 10.0, 10.0), Direction.NORTH, Block.box(6.0, 0.0, 0.0, 10.0, 10.0, 10.0), Direction.EAST, Block.box(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)));
   }
}
