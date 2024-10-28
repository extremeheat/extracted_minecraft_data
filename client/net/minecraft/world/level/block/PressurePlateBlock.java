package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class PressurePlateBlock extends BasePressurePlateBlock {
   public static final MapCodec<PressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter((var0x) -> {
         return var0x.type;
      }), propertiesCodec()).apply(var0, PressurePlateBlock::new);
   });
   public static final BooleanProperty POWERED;

   public MapCodec<PressurePlateBlock> codec() {
      return CODEC;
   }

   protected PressurePlateBlock(BlockSetType var1, BlockBehaviour.Properties var2) {
      super(var2, var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false));
   }

   protected int getSignalForState(BlockState var1) {
      return (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   protected BlockState setSignalForState(BlockState var1, int var2) {
      return (BlockState)var1.setValue(POWERED, var2 > 0);
   }

   protected int getSignalStrength(Level var1, BlockPos var2) {
      Class var10000;
      switch (this.type.pressurePlateSensitivity()) {
         case EVERYTHING -> var10000 = Entity.class;
         case MOBS -> var10000 = LivingEntity.class;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      Class var3 = var10000;
      return getEntityCount(var1, TOUCH_AABB.move(var2), var3) > 0 ? 15 : 0;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(POWERED);
   }

   static {
      POWERED = BlockStateProperties.POWERED;
   }
}
