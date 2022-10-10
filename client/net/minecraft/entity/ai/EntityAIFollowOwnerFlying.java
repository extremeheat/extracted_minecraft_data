package net.minecraft.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;

public class EntityAIFollowOwnerFlying extends EntityAIFollowOwner {
   public EntityAIFollowOwnerFlying(EntityTameable var1, double var2, float var4, float var5) {
      super(var1, var2, var4, var5);
   }

   protected boolean func_192381_a(int var1, int var2, int var3, int var4, int var5) {
      IBlockState var6 = this.field_75342_a.func_180495_p(new BlockPos(var1 + var4, var3 - 1, var2 + var5));
      return (var6.func_185896_q() || var6.func_203425_a(BlockTags.field_206952_E)) && this.field_75342_a.func_175623_d(new BlockPos(var1 + var4, var3, var2 + var5)) && this.field_75342_a.func_175623_d(new BlockPos(var1 + var4, var3 + 1, var2 + var5));
   }
}
