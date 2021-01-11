package net.minecraft.client.renderer;

import java.util.Arrays;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class RegionRenderCache extends ChunkCache {
   private static final IBlockState field_175632_f;
   private final BlockPos field_175633_g;
   private int[] field_175634_h;
   private IBlockState[] field_175635_i;

   public RegionRenderCache(World var1, BlockPos var2, BlockPos var3, int var4) {
      super(var1, var2, var3, var4);
      this.field_175633_g = var2.func_177973_b(new Vec3i(var4, var4, var4));
      boolean var5 = true;
      this.field_175634_h = new int[8000];
      Arrays.fill(this.field_175634_h, -1);
      this.field_175635_i = new IBlockState[8000];
   }

   public TileEntity func_175625_s(BlockPos var1) {
      int var2 = (var1.func_177958_n() >> 4) - this.field_72818_a;
      int var3 = (var1.func_177952_p() >> 4) - this.field_72816_b;
      return this.field_72817_c[var2][var3].func_177424_a(var1, Chunk.EnumCreateEntityType.QUEUED);
   }

   public int func_175626_b(BlockPos var1, int var2) {
      int var3 = this.func_175630_e(var1);
      int var4 = this.field_175634_h[var3];
      if (var4 == -1) {
         var4 = super.func_175626_b(var1, var2);
         this.field_175634_h[var3] = var4;
      }

      return var4;
   }

   public IBlockState func_180495_p(BlockPos var1) {
      int var2 = this.func_175630_e(var1);
      IBlockState var3 = this.field_175635_i[var2];
      if (var3 == null) {
         var3 = this.func_175631_c(var1);
         this.field_175635_i[var2] = var3;
      }

      return var3;
   }

   private IBlockState func_175631_c(BlockPos var1) {
      if (var1.func_177956_o() >= 0 && var1.func_177956_o() < 256) {
         int var2 = (var1.func_177958_n() >> 4) - this.field_72818_a;
         int var3 = (var1.func_177952_p() >> 4) - this.field_72816_b;
         return this.field_72817_c[var2][var3].func_177435_g(var1);
      } else {
         return field_175632_f;
      }
   }

   private int func_175630_e(BlockPos var1) {
      int var2 = var1.func_177958_n() - this.field_175633_g.func_177958_n();
      int var3 = var1.func_177956_o() - this.field_175633_g.func_177956_o();
      int var4 = var1.func_177952_p() - this.field_175633_g.func_177952_p();
      return var2 * 400 + var4 * 20 + var3;
   }

   static {
      field_175632_f = Blocks.field_150350_a.func_176223_P();
   }
}
