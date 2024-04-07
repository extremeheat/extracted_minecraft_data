package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
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
   public static final MapCodec<SkullBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(SkullBlock.Type.CODEC.fieldOf("kind").forGetter(AbstractSkullBlock::getType), propertiesCodec()).apply(var0, SkullBlock::new)
   );
   public static final int MAX = RotationSegment.getMaxSegmentIndex();
   private static final int ROTATIONS = MAX + 1;
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
   protected static final VoxelShape PIGLIN_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);

   @Override
   public MapCodec<? extends SkullBlock> codec() {
      return CODEC;
   }

   protected SkullBlock(SkullBlock.Type var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
      this.registerDefaultState(this.defaultBlockState().setValue(ROTATION, Integer.valueOf(0)));
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getType() == SkullBlock.Types.PIGLIN ? PIGLIN_SHAPE : SHAPE;
   }

   @Override
   protected VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return super.getStateForPlacement(var1).setValue(ROTATION, Integer.valueOf(RotationSegment.convertToSegment(var1.getRotation())));
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(ROTATION, Integer.valueOf(var2.rotate(var1.getValue(ROTATION), ROTATIONS)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.setValue(ROTATION, Integer.valueOf(var2.mirror(var1.getValue(ROTATION), ROTATIONS)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      super.createBlockStateDefinition(var1);
      var1.add(ROTATION);
   }

   public interface Type extends StringRepresentable {
      Map<String, SkullBlock.Type> TYPES = new Object2ObjectArrayMap();
      Codec<SkullBlock.Type> CODEC = Codec.stringResolver(StringRepresentable::getSerializedName, TYPES::get);
   }

   public static enum Types implements SkullBlock.Type {
      SKELETON("skeleton"),
      WITHER_SKELETON("wither_skeleton"),
      PLAYER("player"),
      ZOMBIE("zombie"),
      CREEPER("creeper"),
      PIGLIN("piglin"),
      DRAGON("dragon");

      private final String name;

      private Types(String var3) {
         this.name = var3;
         TYPES.put(var3, this);
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
