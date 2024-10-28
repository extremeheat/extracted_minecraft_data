package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkullBlock extends AbstractSkullBlock {
   public static final MapCodec<SkullBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(SkullBlock.Type.CODEC.fieldOf("kind").forGetter(AbstractSkullBlock::getType), propertiesCodec()).apply(var0, SkullBlock::new);
   });
   public static final int MAX = RotationSegment.getMaxSegmentIndex();
   private static final int ROTATIONS;
   public static final IntegerProperty ROTATION;
   protected static final VoxelShape SHAPE;
   protected static final VoxelShape PIGLIN_SHAPE;

   public MapCodec<? extends SkullBlock> codec() {
      return CODEC;
   }

   protected SkullBlock(Type var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(ROTATION, 0));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getType() == SkullBlock.Types.PIGLIN ? PIGLIN_SHAPE : SHAPE;
   }

   protected VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)super.getStateForPlacement(var1).setValue(ROTATION, RotationSegment.convertToSegment(var1.getRotation()));
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(ROTATION, var2.rotate((Integer)var1.getValue(ROTATION), ROTATIONS));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return (BlockState)var1.setValue(ROTATION, var2.mirror((Integer)var1.getValue(ROTATION), ROTATIONS));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      super.createBlockStateDefinition(var1);
      var1.add(ROTATION);
   }

   static {
      ROTATIONS = MAX + 1;
      ROTATION = BlockStateProperties.ROTATION_16;
      SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
      PIGLIN_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
   }

   public interface Type extends StringRepresentable {
      Map<String, Type> TYPES = new Object2ObjectArrayMap();
      Codec<Type> CODEC;

      static {
         Function var10000 = StringRepresentable::getSerializedName;
         Map var10001 = TYPES;
         Objects.requireNonNull(var10001);
         CODEC = Codec.stringResolver(var10000, var10001::get);
      }
   }

   public static enum Types implements Type {
      SKELETON("skeleton"),
      WITHER_SKELETON("wither_skeleton"),
      PLAYER("player"),
      ZOMBIE("zombie"),
      CREEPER("creeper"),
      PIGLIN("piglin"),
      DRAGON("dragon");

      private final String name;

      private Types(final String var3) {
         this.name = var3;
         TYPES.put(var3, this);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Types[] $values() {
         return new Types[]{SKELETON, WITHER_SKELETON, PLAYER, ZOMBIE, CREEPER, PIGLIN, DRAGON};
      }
   }
}
