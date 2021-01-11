package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class EntityBlockDustFX extends EntityDiggingFX {
   protected EntityBlockDustFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, IBlockState var14) {
      super(var1, var2, var4, var6, var8, var10, var12, var14);
      this.field_70159_w = var8;
      this.field_70181_x = var10;
      this.field_70179_y = var12;
   }

   public static class Factory implements IParticleFactory {
      public Factory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         IBlockState var16 = Block.func_176220_d(var15[0]);
         return var16.func_177230_c().func_149645_b() == -1 ? null : (new EntityBlockDustFX(var2, var3, var5, var7, var9, var11, var13, var16)).func_174845_l();
      }
   }
}
