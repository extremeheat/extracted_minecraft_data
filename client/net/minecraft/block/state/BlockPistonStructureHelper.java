package net.minecraft.block.state;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockPistonStructureHelper {
   private final World field_177261_a;
   private final BlockPos field_177259_b;
   private final BlockPos field_177260_c;
   private final EnumFacing field_177257_d;
   private final List<BlockPos> field_177258_e = Lists.newArrayList();
   private final List<BlockPos> field_177256_f = Lists.newArrayList();

   public BlockPistonStructureHelper(World var1, BlockPos var2, EnumFacing var3, boolean var4) {
      super();
      this.field_177261_a = var1;
      this.field_177259_b = var2;
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
      Block var1 = this.field_177261_a.func_180495_p(this.field_177260_c).func_177230_c();
      if (!BlockPistonBase.func_180696_a(var1, this.field_177261_a, this.field_177260_c, this.field_177257_d, false)) {
         if (var1.func_149656_h() != 1) {
            return false;
         } else {
            this.field_177256_f.add(this.field_177260_c);
            return true;
         }
      } else if (!this.func_177251_a(this.field_177260_c)) {
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

   private boolean func_177251_a(BlockPos var1) {
      Block var2 = this.field_177261_a.func_180495_p(var1).func_177230_c();
      if (var2.func_149688_o() == Material.field_151579_a) {
         return true;
      } else if (!BlockPistonBase.func_180696_a(var2, this.field_177261_a, var1, this.field_177257_d, false)) {
         return true;
      } else if (var1.equals(this.field_177259_b)) {
         return true;
      } else if (this.field_177258_e.contains(var1)) {
         return true;
      } else {
         int var3 = 1;
         if (var3 + this.field_177258_e.size() > 12) {
            return false;
         } else {
            while(var2 == Blocks.field_180399_cE) {
               BlockPos var4 = var1.func_177967_a(this.field_177257_d.func_176734_d(), var3);
               var2 = this.field_177261_a.func_180495_p(var4).func_177230_c();
               if (var2.func_149688_o() == Material.field_151579_a || !BlockPistonBase.func_180696_a(var2, this.field_177261_a, var4, this.field_177257_d, false) || var4.equals(this.field_177259_b)) {
                  break;
               }

               ++var3;
               if (var3 + this.field_177258_e.size() > 12) {
                  return false;
               }
            }

            int var10 = 0;

            int var5;
            for(var5 = var3 - 1; var5 >= 0; --var5) {
               this.field_177258_e.add(var1.func_177967_a(this.field_177257_d.func_176734_d(), var5));
               ++var10;
            }

            var5 = 1;

            while(true) {
               BlockPos var6 = var1.func_177967_a(this.field_177257_d, var5);
               int var7 = this.field_177258_e.indexOf(var6);
               if (var7 > -1) {
                  this.func_177255_a(var10, var7);

                  for(int var8 = 0; var8 <= var7 + var10; ++var8) {
                     BlockPos var9 = (BlockPos)this.field_177258_e.get(var8);
                     if (this.field_177261_a.func_180495_p(var9).func_177230_c() == Blocks.field_180399_cE && !this.func_177250_b(var9)) {
                        return false;
                     }
                  }

                  return true;
               }

               var2 = this.field_177261_a.func_180495_p(var6).func_177230_c();
               if (var2.func_149688_o() == Material.field_151579_a) {
                  return true;
               }

               if (!BlockPistonBase.func_180696_a(var2, this.field_177261_a, var6, this.field_177257_d, true) || var6.equals(this.field_177259_b)) {
                  return false;
               }

               if (var2.func_149656_h() == 1) {
                  this.field_177256_f.add(var6);
                  return true;
               }

               if (this.field_177258_e.size() >= 12) {
                  return false;
               }

               this.field_177258_e.add(var6);
               ++var10;
               ++var5;
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
         if (var5.func_176740_k() != this.field_177257_d.func_176740_k() && !this.func_177251_a(var1.func_177972_a(var5))) {
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
