package net.minecraft.block;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockVine extends Block {
   public static final BooleanProperty field_176277_a;
   public static final BooleanProperty field_176273_b;
   public static final BooleanProperty field_176278_M;
   public static final BooleanProperty field_176279_N;
   public static final BooleanProperty field_176280_O;
   public static final Map<EnumFacing, BooleanProperty> field_196546_A;
   protected static final VoxelShape field_185757_g;
   protected static final VoxelShape field_185753_B;
   protected static final VoxelShape field_185754_C;
   protected static final VoxelShape field_185755_D;
   protected static final VoxelShape field_185756_E;

   public BlockVine(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176277_a, false)).func_206870_a(field_176273_b, false)).func_206870_a(field_176278_M, false)).func_206870_a(field_176279_N, false)).func_206870_a(field_176280_O, false));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      VoxelShape var4 = VoxelShapes.func_197880_a();
      if ((Boolean)var1.func_177229_b(field_176277_a)) {
         var4 = VoxelShapes.func_197872_a(var4, field_185757_g);
      }

      if ((Boolean)var1.func_177229_b(field_176273_b)) {
         var4 = VoxelShapes.func_197872_a(var4, field_185755_D);
      }

      if ((Boolean)var1.func_177229_b(field_176278_M)) {
         var4 = VoxelShapes.func_197872_a(var4, field_185754_C);
      }

      if ((Boolean)var1.func_177229_b(field_176279_N)) {
         var4 = VoxelShapes.func_197872_a(var4, field_185756_E);
      }

      if ((Boolean)var1.func_177229_b(field_176280_O)) {
         var4 = VoxelShapes.func_197872_a(var4, field_185753_B);
      }

      return var4;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      return this.func_196543_i(this.func_196545_h(var1, var2, var3));
   }

   private boolean func_196543_i(IBlockState var1) {
      return this.func_208496_w(var1) > 0;
   }

   private int func_208496_w(IBlockState var1) {
      int var2 = 0;
      Iterator var3 = field_196546_A.values().iterator();

      while(var3.hasNext()) {
         BooleanProperty var4 = (BooleanProperty)var3.next();
         if ((Boolean)var1.func_177229_b(var4)) {
            ++var2;
         }
      }

      return var2;
   }

   private boolean func_196541_a(IBlockReader var1, BlockPos var2, EnumFacing var3) {
      if (var3 == EnumFacing.DOWN) {
         return false;
      } else {
         BlockPos var4 = var2.func_177972_a(var3);
         if (this.func_196542_b(var1, var4, var3)) {
            return true;
         } else if (var3.func_176740_k() == EnumFacing.Axis.Y) {
            return false;
         } else {
            BooleanProperty var5 = (BooleanProperty)field_196546_A.get(var3);
            IBlockState var6 = var1.func_180495_p(var2.func_177984_a());
            return var6.func_177230_c() == this && (Boolean)var6.func_177229_b(var5);
         }
      }
   }

   private boolean func_196542_b(IBlockReader var1, BlockPos var2, EnumFacing var3) {
      IBlockState var4 = var1.func_180495_p(var2);
      return var4.func_193401_d(var1, var2, var3.func_176734_d()) == BlockFaceShape.SOLID && !func_193397_e(var4.func_177230_c());
   }

   protected static boolean func_193397_e(Block var0) {
      return var0 instanceof BlockShulkerBox || var0 instanceof BlockStainedGlass || var0 == Blocks.field_150461_bJ || var0 == Blocks.field_150383_bp || var0 == Blocks.field_150359_w || var0 == Blocks.field_150331_J || var0 == Blocks.field_150320_F || var0 == Blocks.field_150332_K || var0.func_203417_a(BlockTags.field_212186_k);
   }

   private IBlockState func_196545_h(IBlockState var1, IBlockReader var2, BlockPos var3) {
      BlockPos var4 = var3.func_177984_a();
      if ((Boolean)var1.func_177229_b(field_176277_a)) {
         var1 = (IBlockState)var1.func_206870_a(field_176277_a, this.func_196542_b(var2, var4, EnumFacing.DOWN));
      }

      IBlockState var5 = null;
      Iterator var6 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(true) {
         EnumFacing var7;
         BooleanProperty var8;
         do {
            if (!var6.hasNext()) {
               return var1;
            }

            var7 = (EnumFacing)var6.next();
            var8 = func_176267_a(var7);
         } while(!(Boolean)var1.func_177229_b(var8));

         boolean var9 = this.func_196541_a(var2, var3, var7);
         if (!var9) {
            if (var5 == null) {
               var5 = var2.func_180495_p(var4);
            }

            var9 = var5.func_177230_c() == this && (Boolean)var5.func_177229_b(var8);
         }

         var1 = (IBlockState)var1.func_206870_a(var8, var9);
      }
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (var2 == EnumFacing.DOWN) {
         return super.func_196271_a(var1, var2, var3, var4, var5, var6);
      } else {
         IBlockState var7 = this.func_196545_h(var1, var4, var5);
         return !this.func_196543_i(var7) ? Blocks.field_150350_a.func_176223_P() : var7;
      }
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var2.field_72995_K) {
         IBlockState var5 = this.func_196545_h(var1, var2, var3);
         if (var5 != var1) {
            if (this.func_196543_i(var5)) {
               var2.func_180501_a(var3, var5, 2);
            } else {
               var1.func_196949_c(var2, var3, 0);
               var2.func_175698_g(var3);
            }

         } else if (var2.field_73012_v.nextInt(4) == 0) {
            EnumFacing var6 = EnumFacing.func_176741_a(var4);
            BlockPos var7 = var3.func_177984_a();
            BlockPos var8;
            IBlockState var9;
            EnumFacing var19;
            if (var6.func_176740_k().func_176722_c() && !(Boolean)var1.func_177229_b(func_176267_a(var6))) {
               if (this.func_196539_a(var2, var3)) {
                  var8 = var3.func_177972_a(var6);
                  var9 = var2.func_180495_p(var8);
                  if (var9.func_196958_f()) {
                     var19 = var6.func_176746_e();
                     EnumFacing var20 = var6.func_176735_f();
                     boolean var12 = (Boolean)var1.func_177229_b(func_176267_a(var19));
                     boolean var13 = (Boolean)var1.func_177229_b(func_176267_a(var20));
                     BlockPos var14 = var8.func_177972_a(var19);
                     BlockPos var15 = var8.func_177972_a(var20);
                     if (var12 && this.func_196542_b(var2, var14, var19)) {
                        var2.func_180501_a(var8, (IBlockState)this.func_176223_P().func_206870_a(func_176267_a(var19), true), 2);
                     } else if (var13 && this.func_196542_b(var2, var15, var20)) {
                        var2.func_180501_a(var8, (IBlockState)this.func_176223_P().func_206870_a(func_176267_a(var20), true), 2);
                     } else {
                        EnumFacing var16 = var6.func_176734_d();
                        if (var12 && var2.func_175623_d(var14) && this.func_196542_b(var2, var3.func_177972_a(var19), var16)) {
                           var2.func_180501_a(var14, (IBlockState)this.func_176223_P().func_206870_a(func_176267_a(var16), true), 2);
                        } else if (var13 && var2.func_175623_d(var15) && this.func_196542_b(var2, var3.func_177972_a(var20), var16)) {
                           var2.func_180501_a(var15, (IBlockState)this.func_176223_P().func_206870_a(func_176267_a(var16), true), 2);
                        } else if ((double)var2.field_73012_v.nextFloat() < 0.05D && this.func_196542_b(var2, var8.func_177984_a(), EnumFacing.UP)) {
                           var2.func_180501_a(var8, (IBlockState)this.func_176223_P().func_206870_a(field_176277_a, true), 2);
                        }
                     }
                  } else if (this.func_196542_b(var2, var8, var6)) {
                     var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(func_176267_a(var6), true), 2);
                  }

               }
            } else {
               if (var6 == EnumFacing.UP && var3.func_177956_o() < 255) {
                  if (this.func_196541_a(var2, var3, var6)) {
                     var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176277_a, true), 2);
                     return;
                  }

                  if (var2.func_175623_d(var7)) {
                     if (!this.func_196539_a(var2, var3)) {
                        return;
                     }

                     IBlockState var17 = var1;
                     Iterator var18 = EnumFacing.Plane.HORIZONTAL.iterator();

                     while(true) {
                        do {
                           if (!var18.hasNext()) {
                              if (this.func_196540_x(var17)) {
                                 var2.func_180501_a(var7, var17, 2);
                              }

                              return;
                           }

                           var19 = (EnumFacing)var18.next();
                        } while(!var4.nextBoolean() && this.func_196542_b(var2, var7.func_177972_a(var19), EnumFacing.UP));

                        var17 = (IBlockState)var17.func_206870_a(func_176267_a(var19), false);
                     }
                  }
               }

               if (var3.func_177956_o() > 0) {
                  var8 = var3.func_177977_b();
                  var9 = var2.func_180495_p(var8);
                  if (var9.func_196958_f() || var9.func_177230_c() == this) {
                     IBlockState var10 = var9.func_196958_f() ? this.func_176223_P() : var9;
                     IBlockState var11 = this.func_196544_a(var1, var10, var4);
                     if (var10 != var11 && this.func_196540_x(var11)) {
                        var2.func_180501_a(var8, var11, 2);
                     }
                  }
               }

            }
         }
      }
   }

   private IBlockState func_196544_a(IBlockState var1, IBlockState var2, Random var3) {
      Iterator var4 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         EnumFacing var5 = (EnumFacing)var4.next();
         if (var3.nextBoolean()) {
            BooleanProperty var6 = func_176267_a(var5);
            if ((Boolean)var1.func_177229_b(var6)) {
               var2 = (IBlockState)var2.func_206870_a(var6, true);
            }
         }
      }

      return var2;
   }

   private boolean func_196540_x(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_176273_b) || (Boolean)var1.func_177229_b(field_176278_M) || (Boolean)var1.func_177229_b(field_176279_N) || (Boolean)var1.func_177229_b(field_176280_O);
   }

   private boolean func_196539_a(IBlockReader var1, BlockPos var2) {
      boolean var3 = true;
      Iterable var4 = BlockPos.MutableBlockPos.func_191531_b(var2.func_177958_n() - 4, var2.func_177956_o() - 1, var2.func_177952_p() - 4, var2.func_177958_n() + 4, var2.func_177956_o() + 1, var2.func_177952_p() + 4);
      int var5 = 5;
      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         BlockPos var7 = (BlockPos)var6.next();
         if (var1.func_180495_p(var7).func_177230_c() == this) {
            --var5;
            if (var5 <= 0) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean func_196253_a(IBlockState var1, BlockItemUseContext var2) {
      IBlockState var3 = var2.func_195991_k().func_180495_p(var2.func_195995_a());
      if (var3.func_177230_c() == this) {
         return this.func_208496_w(var3) < field_196546_A.size();
      } else {
         return super.func_196253_a(var1, var2);
      }
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = var1.func_195991_k().func_180495_p(var1.func_195995_a());
      boolean var3 = var2.func_177230_c() == this;
      IBlockState var4 = var3 ? var2 : this.func_176223_P();
      EnumFacing[] var5 = var1.func_196009_e();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         EnumFacing var8 = var5[var7];
         if (var8 != EnumFacing.DOWN) {
            BooleanProperty var9 = func_176267_a(var8);
            boolean var10 = var3 && (Boolean)var2.func_177229_b(var9);
            if (!var10 && this.func_196541_a(var1.func_195991_k(), var1.func_195995_a(), var8)) {
               return (IBlockState)var4.func_206870_a(var9, true);
            }
         }
      }

      return var3 ? var4 : null;
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      if (!var1.field_72995_K && var6.func_77973_b() == Items.field_151097_aZ) {
         var2.func_71029_a(StatList.field_188065_ae.func_199076_b(this));
         var2.func_71020_j(0.005F);
         func_180635_a(var1, var3, new ItemStack(Blocks.field_150395_bd));
      } else {
         super.func_180657_a(var1, var2, var3, var4, var5, var6);
      }

   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176277_a, field_176273_b, field_176278_M, field_176279_N, field_176280_O);
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      switch(var2) {
      case CLOCKWISE_180:
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_176273_b, var1.func_177229_b(field_176279_N))).func_206870_a(field_176278_M, var1.func_177229_b(field_176280_O))).func_206870_a(field_176279_N, var1.func_177229_b(field_176273_b))).func_206870_a(field_176280_O, var1.func_177229_b(field_176278_M));
      case COUNTERCLOCKWISE_90:
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_176273_b, var1.func_177229_b(field_176278_M))).func_206870_a(field_176278_M, var1.func_177229_b(field_176279_N))).func_206870_a(field_176279_N, var1.func_177229_b(field_176280_O))).func_206870_a(field_176280_O, var1.func_177229_b(field_176273_b));
      case CLOCKWISE_90:
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_176273_b, var1.func_177229_b(field_176280_O))).func_206870_a(field_176278_M, var1.func_177229_b(field_176273_b))).func_206870_a(field_176279_N, var1.func_177229_b(field_176278_M))).func_206870_a(field_176280_O, var1.func_177229_b(field_176279_N));
      default:
         return var1;
      }
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      switch(var2) {
      case LEFT_RIGHT:
         return (IBlockState)((IBlockState)var1.func_206870_a(field_176273_b, var1.func_177229_b(field_176279_N))).func_206870_a(field_176279_N, var1.func_177229_b(field_176273_b));
      case FRONT_BACK:
         return (IBlockState)((IBlockState)var1.func_206870_a(field_176278_M, var1.func_177229_b(field_176280_O))).func_206870_a(field_176280_O, var1.func_177229_b(field_176278_M));
      default:
         return super.func_185471_a(var1, var2);
      }
   }

   public static BooleanProperty func_176267_a(EnumFacing var0) {
      return (BooleanProperty)field_196546_A.get(var0);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   static {
      field_176277_a = BlockSixWay.field_196496_z;
      field_176273_b = BlockSixWay.field_196488_a;
      field_176278_M = BlockSixWay.field_196490_b;
      field_176279_N = BlockSixWay.field_196492_c;
      field_176280_O = BlockSixWay.field_196495_y;
      field_196546_A = (Map)BlockSixWay.field_196491_B.entrySet().stream().filter((var0) -> {
         return var0.getKey() != EnumFacing.DOWN;
      }).collect(Util.func_199749_a());
      field_185757_g = Block.func_208617_a(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      field_185753_B = Block.func_208617_a(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
      field_185754_C = Block.func_208617_a(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      field_185755_D = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
      field_185756_E = Block.func_208617_a(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
   }
}
