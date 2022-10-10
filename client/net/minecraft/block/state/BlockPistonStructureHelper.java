package net.minecraft.block.state;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPistonStructureHelper {
   private final World field_177261_a;
   private final BlockPos field_177259_b;
   private final boolean field_211724_c;
   private final BlockPos field_177260_c;
   private final EnumFacing field_177257_d;
   private final List<BlockPos> field_177258_e = Lists.newArrayList();
   private final List<BlockPos> field_177256_f = Lists.newArrayList();
   private final EnumFacing field_211906_h;

   public BlockPistonStructureHelper(World var1, BlockPos var2, EnumFacing var3, boolean var4) {
      super();
      this.field_177261_a = var1;
      this.field_177259_b = var2;
      this.field_211906_h = var3;
      this.field_211724_c = var4;
      if (var4) {
         this.field_177257_d = var3;
         this.field_177260_c = var2.func_177972_a(var3);
      } else {
         this.field_177257_d = var3.func_176734_d();
         this.field_177260_c = var2.func_177967_a(var3, 2);
      }

   }

   public boolean func_177253_a() {
      this.field_177258_e.clear();
      this.field_177256_f.clear();
      IBlockState var1 = this.field_177261_a.func_180495_p(this.field_177260_c);
      if (!BlockPistonBase.func_185646_a(var1, this.field_177261_a, this.field_177260_c, this.field_177257_d, false, this.field_211906_h)) {
         if (this.field_211724_c && var1.func_185905_o() == EnumPushReaction.DESTROY) {
            this.field_177256_f.add(this.field_177260_c);
            return true;
         } else {
            return false;
         }
      } else if (!this.func_177251_a(this.field_177260_c, this.field_177257_d)) {
         return false;
      } else {
         for(int var2 = 0; var2 < this.field_177258_e.size(); ++var2) {
            BlockPos var3 = (BlockPos)this.field_177258_e.get(var2);
            if (this.field_177261_a.func_180495_p(var3).func_177230_c() == Blocks.field_180399_cE && !this.func_177250_b(var3)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean func_177251_a(BlockPos var1, EnumFacing var2) {
      IBlockState var3 = this.field_177261_a.func_180495_p(var1);
      Block var4 = var3.func_177230_c();
      if (var3.func_196958_f()) {
         return true;
      } else if (!BlockPistonBase.func_185646_a(var3, this.field_177261_a, var1, this.field_177257_d, false, var2)) {
         return true;
      } else if (var1.equals(this.field_177259_b)) {
         return true;
      } else if (this.field_177258_e.contains(var1)) {
         return true;
      } else {
         int var5 = 1;
         if (var5 + this.field_177258_e.size() > 12) {
            return false;
         } else {
            while(var4 == Blocks.field_180399_cE) {
               BlockPos var6 = var1.func_177967_a(this.field_177257_d.func_176734_d(), var5);
               var3 = this.field_177261_a.func_180495_p(var6);
               var4 = var3.func_177230_c();
               if (var3.func_196958_f() || !BlockPistonBase.func_185646_a(var3, this.field_177261_a, var6, this.field_177257_d, false, this.field_177257_d.func_176734_d()) || var6.equals(this.field_177259_b)) {
                  break;
               }

               ++var5;
               if (var5 + this.field_177258_e.size() > 12) {
                  return false;
               }
            }

            int var12 = 0;

            int var7;
            for(var7 = var5 - 1; var7 >= 0; --var7) {
               this.field_177258_e.add(var1.func_177967_a(this.field_177257_d.func_176734_d(), var7));
               ++var12;
            }

            var7 = 1;

            while(true) {
               BlockPos var8 = var1.func_177967_a(this.field_177257_d, var7);
               int var9 = this.field_177258_e.indexOf(var8);
               if (var9 > -1) {
                  this.func_177255_a(var12, var9);

                  for(int var10 = 0; var10 <= var9 + var12; ++var10) {
                     BlockPos var11 = (BlockPos)this.field_177258_e.get(var10);
                     if (this.field_177261_a.func_180495_p(var11).func_177230_c() == Blocks.field_180399_cE && !this.func_177250_b(var11)) {
                        return false;
                     }
                  }

                  return true;
               }

               var3 = this.field_177261_a.func_180495_p(var8);
               if (var3.func_196958_f()) {
                  return true;
               }

               if (!BlockPistonBase.func_185646_a(var3, this.field_177261_a, var8, this.field_177257_d, true, this.field_177257_d) || var8.equals(this.field_177259_b)) {
                  return false;
               }

               if (var3.func_185905_o() == EnumPushReaction.DESTROY) {
                  this.field_177256_f.add(var8);
                  return true;
               }

               if (this.field_177258_e.size() >= 12) {
                  return false;
               }

               this.field_177258_e.add(var8);
               ++var12;
               ++var7;
            }
         }
      }
   }

   private void func_177255_a(int var1, int var2) {
      ArrayList var3 = Lists.newArrayList();
      ArrayList var4 = Lists.newArrayList();
      ArrayList var5 = Lists.newArrayList();
      var3.addAll(this.field_177258_e.subList(0, var2));
      var4.addAll(this.field_177258_e.subList(this.field_177258_e.size() - var1, this.field_177258_e.size()));
      var5.addAll(this.field_177258_e.subList(var2, this.field_177258_e.size() - var1));
      this.field_177258_e.clear();
      this.field_177258_e.addAll(var3);
      this.field_177258_e.addAll(var4);
      this.field_177258_e.addAll(var5);
   }

   private boolean func_177250_b(BlockPos var1) {
      EnumFacing[] var2 = EnumFacing.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing var5 = var2[var4];
         if (var5.func_176740_k() != this.field_177257_d.func_176740_k() && !this.func_177251_a(var1.func_177972_a(var5), var5)) {
            return false;
         }
      }

      return true;
   }

   public List<BlockPos> func_177254_c() {
      return this.field_177258_e;
   }

   public List<BlockPos> func_177252_d() {
      return this.field_177256_f;
   }
}
