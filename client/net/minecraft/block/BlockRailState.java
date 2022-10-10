package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRailState {
   private final World field_196920_a;
   private final BlockPos field_196921_b;
   private final BlockRailBase field_196922_c;
   private IBlockState field_196923_d;
   private final boolean field_208513_e;
   private final List<BlockPos> field_196924_e = Lists.newArrayList();

   public BlockRailState(World var1, BlockPos var2, IBlockState var3) {
      super();
      this.field_196920_a = var1;
      this.field_196921_b = var2;
      this.field_196923_d = var3;
      this.field_196922_c = (BlockRailBase)var3.func_177230_c();
      RailShape var4 = (RailShape)var3.func_177229_b(this.field_196922_c.func_176560_l());
      this.field_208513_e = this.field_196922_c.func_208490_b();
      this.func_208509_a(var4);
   }

   public List<BlockPos> func_196907_a() {
      return this.field_196924_e;
   }

   private void func_208509_a(RailShape var1) {
      this.field_196924_e.clear();
      switch(var1) {
      case NORTH_SOUTH:
         this.field_196924_e.add(this.field_196921_b.func_177978_c());
         this.field_196924_e.add(this.field_196921_b.func_177968_d());
         break;
      case EAST_WEST:
         this.field_196924_e.add(this.field_196921_b.func_177976_e());
         this.field_196924_e.add(this.field_196921_b.func_177974_f());
         break;
      case ASCENDING_EAST:
         this.field_196924_e.add(this.field_196921_b.func_177976_e());
         this.field_196924_e.add(this.field_196921_b.func_177974_f().func_177984_a());
         break;
      case ASCENDING_WEST:
         this.field_196924_e.add(this.field_196921_b.func_177976_e().func_177984_a());
         this.field_196924_e.add(this.field_196921_b.func_177974_f());
         break;
      case ASCENDING_NORTH:
         this.field_196924_e.add(this.field_196921_b.func_177978_c().func_177984_a());
         this.field_196924_e.add(this.field_196921_b.func_177968_d());
         break;
      case ASCENDING_SOUTH:
         this.field_196924_e.add(this.field_196921_b.func_177978_c());
         this.field_196924_e.add(this.field_196921_b.func_177968_d().func_177984_a());
         break;
      case SOUTH_EAST:
         this.field_196924_e.add(this.field_196921_b.func_177974_f());
         this.field_196924_e.add(this.field_196921_b.func_177968_d());
         break;
      case SOUTH_WEST:
         this.field_196924_e.add(this.field_196921_b.func_177976_e());
         this.field_196924_e.add(this.field_196921_b.func_177968_d());
         break;
      case NORTH_WEST:
         this.field_196924_e.add(this.field_196921_b.func_177976_e());
         this.field_196924_e.add(this.field_196921_b.func_177978_c());
         break;
      case NORTH_EAST:
         this.field_196924_e.add(this.field_196921_b.func_177974_f());
         this.field_196924_e.add(this.field_196921_b.func_177978_c());
      }

   }

   private void func_196903_f() {
      for(int var1 = 0; var1 < this.field_196924_e.size(); ++var1) {
         BlockRailState var2 = this.func_196908_a((BlockPos)this.field_196924_e.get(var1));
         if (var2 != null && var2.func_196919_b(this)) {
            this.field_196924_e.set(var1, var2.field_196921_b);
         } else {
            this.field_196924_e.remove(var1--);
         }
      }

   }

   private boolean func_196902_d(BlockPos var1) {
      return BlockRailBase.func_208488_a(this.field_196920_a, var1) || BlockRailBase.func_208488_a(this.field_196920_a, var1.func_177984_a()) || BlockRailBase.func_208488_a(this.field_196920_a, var1.func_177977_b());
   }

   @Nullable
   private BlockRailState func_196908_a(BlockPos var1) {
      IBlockState var3 = this.field_196920_a.func_180495_p(var1);
      if (BlockRailBase.func_208487_j(var3)) {
         return new BlockRailState(this.field_196920_a, var1, var3);
      } else {
         BlockPos var2 = var1.func_177984_a();
         var3 = this.field_196920_a.func_180495_p(var2);
         if (BlockRailBase.func_208487_j(var3)) {
            return new BlockRailState(this.field_196920_a, var2, var3);
         } else {
            var2 = var1.func_177977_b();
            var3 = this.field_196920_a.func_180495_p(var2);
            return BlockRailBase.func_208487_j(var3) ? new BlockRailState(this.field_196920_a, var2, var3) : null;
         }
      }
   }

   private boolean func_196919_b(BlockRailState var1) {
      return this.func_196904_b(var1.field_196921_b);
   }

   private boolean func_196904_b(BlockPos var1) {
      for(int var2 = 0; var2 < this.field_196924_e.size(); ++var2) {
         BlockPos var3 = (BlockPos)this.field_196924_e.get(var2);
         if (var3.func_177958_n() == var1.func_177958_n() && var3.func_177952_p() == var1.func_177952_p()) {
            return true;
         }
      }

      return false;
   }

   protected int func_196910_b() {
      int var1 = 0;
      Iterator var2 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var2.hasNext()) {
         EnumFacing var3 = (EnumFacing)var2.next();
         if (this.func_196902_d(this.field_196921_b.func_177972_a(var3))) {
            ++var1;
         }
      }

      return var1;
   }

   private boolean func_196905_c(BlockRailState var1) {
      return this.func_196919_b(var1) || this.field_196924_e.size() != 2;
   }

   private void func_208510_c(BlockRailState var1) {
      this.field_196924_e.add(var1.field_196921_b);
      BlockPos var2 = this.field_196921_b.func_177978_c();
      BlockPos var3 = this.field_196921_b.func_177968_d();
      BlockPos var4 = this.field_196921_b.func_177976_e();
      BlockPos var5 = this.field_196921_b.func_177974_f();
      boolean var6 = this.func_196904_b(var2);
      boolean var7 = this.func_196904_b(var3);
      boolean var8 = this.func_196904_b(var4);
      boolean var9 = this.func_196904_b(var5);
      RailShape var10 = null;
      if (var6 || var7) {
         var10 = RailShape.NORTH_SOUTH;
      }

      if (var8 || var9) {
         var10 = RailShape.EAST_WEST;
      }

      if (!this.field_208513_e) {
         if (var7 && var9 && !var6 && !var8) {
            var10 = RailShape.SOUTH_EAST;
         }

         if (var7 && var8 && !var6 && !var9) {
            var10 = RailShape.SOUTH_WEST;
         }

         if (var6 && var8 && !var7 && !var9) {
            var10 = RailShape.NORTH_WEST;
         }

         if (var6 && var9 && !var7 && !var8) {
            var10 = RailShape.NORTH_EAST;
         }
      }

      if (var10 == RailShape.NORTH_SOUTH) {
         if (BlockRailBase.func_208488_a(this.field_196920_a, var2.func_177984_a())) {
            var10 = RailShape.ASCENDING_NORTH;
         }

         if (BlockRailBase.func_208488_a(this.field_196920_a, var3.func_177984_a())) {
            var10 = RailShape.ASCENDING_SOUTH;
         }
      }

      if (var10 == RailShape.EAST_WEST) {
         if (BlockRailBase.func_208488_a(this.field_196920_a, var5.func_177984_a())) {
            var10 = RailShape.ASCENDING_EAST;
         }

         if (BlockRailBase.func_208488_a(this.field_196920_a, var4.func_177984_a())) {
            var10 = RailShape.ASCENDING_WEST;
         }
      }

      if (var10 == null) {
         var10 = RailShape.NORTH_SOUTH;
      }

      this.field_196923_d = (IBlockState)this.field_196923_d.func_206870_a(this.field_196922_c.func_176560_l(), var10);
      this.field_196920_a.func_180501_a(this.field_196921_b, this.field_196923_d, 3);
   }

   private boolean func_208512_d(BlockPos var1) {
      BlockRailState var2 = this.func_196908_a(var1);
      if (var2 == null) {
         return false;
      } else {
         var2.func_196903_f();
         return var2.func_196905_c(this);
      }
   }

   public BlockRailState func_208511_a(boolean var1, boolean var2) {
      BlockPos var3 = this.field_196921_b.func_177978_c();
      BlockPos var4 = this.field_196921_b.func_177968_d();
      BlockPos var5 = this.field_196921_b.func_177976_e();
      BlockPos var6 = this.field_196921_b.func_177974_f();
      boolean var7 = this.func_208512_d(var3);
      boolean var8 = this.func_208512_d(var4);
      boolean var9 = this.func_208512_d(var5);
      boolean var10 = this.func_208512_d(var6);
      RailShape var11 = null;
      if ((var7 || var8) && !var9 && !var10) {
         var11 = RailShape.NORTH_SOUTH;
      }

      if ((var9 || var10) && !var7 && !var8) {
         var11 = RailShape.EAST_WEST;
      }

      if (!this.field_208513_e) {
         if (var8 && var10 && !var7 && !var9) {
            var11 = RailShape.SOUTH_EAST;
         }

         if (var8 && var9 && !var7 && !var10) {
            var11 = RailShape.SOUTH_WEST;
         }

         if (var7 && var9 && !var8 && !var10) {
            var11 = RailShape.NORTH_WEST;
         }

         if (var7 && var10 && !var8 && !var9) {
            var11 = RailShape.NORTH_EAST;
         }
      }

      if (var11 == null) {
         if (var7 || var8) {
            var11 = RailShape.NORTH_SOUTH;
         }

         if (var9 || var10) {
            var11 = RailShape.EAST_WEST;
         }

         if (!this.field_208513_e) {
            if (var1) {
               if (var8 && var10) {
                  var11 = RailShape.SOUTH_EAST;
               }

               if (var9 && var8) {
                  var11 = RailShape.SOUTH_WEST;
               }

               if (var10 && var7) {
                  var11 = RailShape.NORTH_EAST;
               }

               if (var7 && var9) {
                  var11 = RailShape.NORTH_WEST;
               }
            } else {
               if (var7 && var9) {
                  var11 = RailShape.NORTH_WEST;
               }

               if (var10 && var7) {
                  var11 = RailShape.NORTH_EAST;
               }

               if (var9 && var8) {
                  var11 = RailShape.SOUTH_WEST;
               }

               if (var8 && var10) {
                  var11 = RailShape.SOUTH_EAST;
               }
            }
         }
      }

      if (var11 == RailShape.NORTH_SOUTH) {
         if (BlockRailBase.func_208488_a(this.field_196920_a, var3.func_177984_a())) {
            var11 = RailShape.ASCENDING_NORTH;
         }

         if (BlockRailBase.func_208488_a(this.field_196920_a, var4.func_177984_a())) {
            var11 = RailShape.ASCENDING_SOUTH;
         }
      }

      if (var11 == RailShape.EAST_WEST) {
         if (BlockRailBase.func_208488_a(this.field_196920_a, var6.func_177984_a())) {
            var11 = RailShape.ASCENDING_EAST;
         }

         if (BlockRailBase.func_208488_a(this.field_196920_a, var5.func_177984_a())) {
            var11 = RailShape.ASCENDING_WEST;
         }
      }

      if (var11 == null) {
         var11 = RailShape.NORTH_SOUTH;
      }

      this.func_208509_a(var11);
      this.field_196923_d = (IBlockState)this.field_196923_d.func_206870_a(this.field_196922_c.func_176560_l(), var11);
      if (var2 || this.field_196920_a.func_180495_p(this.field_196921_b) != this.field_196923_d) {
         this.field_196920_a.func_180501_a(this.field_196921_b, this.field_196923_d, 3);

         for(int var12 = 0; var12 < this.field_196924_e.size(); ++var12) {
            BlockRailState var13 = this.func_196908_a((BlockPos)this.field_196924_e.get(var12));
            if (var13 != null) {
               var13.func_196903_f();
               if (var13.func_196905_c(this)) {
                  var13.func_208510_c(this);
               }
            }
         }
      }

      return this;
   }

   public IBlockState func_196916_c() {
      return this.field_196923_d;
   }
}
