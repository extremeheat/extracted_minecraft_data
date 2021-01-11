package net.minecraft.client.renderer.block.statemap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

public class DefaultStateMapper extends StateMapperBase {
   public DefaultStateMapper() {
      super();
   }

   protected ModelResourceLocation func_178132_a(IBlockState var1) {
      return new ModelResourceLocation((ResourceLocation)Block.field_149771_c.func_177774_c(var1.func_177230_c()), this.func_178131_a(var1.func_177228_b()));
   }
}
