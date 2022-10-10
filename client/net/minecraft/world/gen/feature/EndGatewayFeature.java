package net.minecraft.world.gen.feature;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class EndGatewayFeature extends Feature<EndGatewayConfig> {
   public EndGatewayFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, EndGatewayConfig var5) {
      Iterator var6 = BlockPos.func_177975_b(var4.func_177982_a(-1, -2, -1), var4.func_177982_a(1, 2, 1)).iterator();

      while(true) {
         while(var6.hasNext()) {
            BlockPos.MutableBlockPos var7 = (BlockPos.MutableBlockPos)var6.next();
            boolean var8 = var7.func_177958_n() == var4.func_177958_n();
            boolean var9 = var7.func_177956_o() == var4.func_177956_o();
            boolean var10 = var7.func_177952_p() == var4.func_177952_p();
            boolean var11 = Math.abs(var7.func_177956_o() - var4.func_177956_o()) == 2;
            if (var8 && var9 && var10) {
               BlockPos var12 = var7.func_185334_h();
               this.func_202278_a(var1, var12, Blocks.field_185775_db.func_176223_P());
               if (var5.func_209959_a()) {
                  TileEntity var13 = var1.func_175625_s(var12);
                  if (var13 instanceof TileEntityEndGateway) {
                     TileEntityEndGateway var14 = (TileEntityEndGateway)var13;
                     var14.func_195489_b(EndDimension.field_209958_g);
                  }
               }
            } else if (var9) {
               this.func_202278_a(var1, var7, Blocks.field_150350_a.func_176223_P());
            } else if (var11 && var8 && var10) {
               this.func_202278_a(var1, var7, Blocks.field_150357_h.func_176223_P());
            } else if ((var8 || var10) && !var11) {
               this.func_202278_a(var1, var7, Blocks.field_150357_h.func_176223_P());
            } else {
               this.func_202278_a(var1, var7, Blocks.field_150350_a.func_176223_P());
            }
         }

         return true;
      }
   }
}
