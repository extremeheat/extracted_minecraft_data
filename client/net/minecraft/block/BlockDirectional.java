package net.minecraft.block;

import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;

public abstract class BlockDirectional extends Block {
   public static final DirectionProperty field_176387_N;

   protected BlockDirectional(Block.Properties var1) {
      super(var1);
   }

   static {
      field_176387_N = BlockStateProperties.field_208155_H;
   }
}
