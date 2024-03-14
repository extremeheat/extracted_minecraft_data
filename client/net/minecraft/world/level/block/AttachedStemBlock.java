package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AttachedStemBlock extends BushBlock {
   public static final MapCodec<AttachedStemBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ResourceKey.codec(Registries.BLOCK).fieldOf("fruit").forGetter(var0x -> var0x.fruit),
               ResourceKey.codec(Registries.BLOCK).fieldOf("stem").forGetter(var0x -> var0x.stem),
               ResourceKey.codec(Registries.ITEM).fieldOf("seed").forGetter(var0x -> var0x.seed),
               propertiesCodec()
            )
            .apply(var0, AttachedStemBlock::new)
   );
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   protected static final float AABB_OFFSET = 2.0F;
   private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(
      ImmutableMap.of(
         Direction.SOUTH,
         Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 16.0),
         Direction.WEST,
         Block.box(0.0, 0.0, 6.0, 10.0, 10.0, 10.0),
         Direction.NORTH,
         Block.box(6.0, 0.0, 0.0, 10.0, 10.0, 10.0),
         Direction.EAST,
         Block.box(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)
      )
   );
   private final ResourceKey<Block> fruit;
   private final ResourceKey<Block> stem;
   private final ResourceKey<Item> seed;

   @Override
   public MapCodec<AttachedStemBlock> codec() {
      return CODEC;
   }

   protected AttachedStemBlock(ResourceKey<Block> var1, ResourceKey<Block> var2, ResourceKey<Item> var3, BlockBehaviour.Properties var4) {
      super(var4);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
      this.stem = var1;
      this.fruit = var2;
      this.seed = var3;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return AABBS.get(var1.getValue(FACING));
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!var3.is(this.fruit) && var2 == var1.getValue(FACING)) {
         Optional var7 = var4.registryAccess().registryOrThrow(Registries.BLOCK).getOptional(this.stem);
         if (var7.isPresent()) {
            return ((Block)var7.get()).defaultBlockState().trySetValue(StemBlock.AGE, Integer.valueOf(7));
         }
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(Blocks.FARMLAND);
   }

   @Override
   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return new ItemStack((ItemLike)DataFixUtils.orElse(var1.registryAccess().registryOrThrow(Registries.ITEM).getOptional(this.seed), this));
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }
}
