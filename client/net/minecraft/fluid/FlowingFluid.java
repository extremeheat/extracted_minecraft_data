package net.minecraft.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class FlowingFluid extends Fluid {
   public static final BooleanProperty field_207209_a;
   public static final IntegerProperty field_207210_b;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> field_212756_e;

   public FlowingFluid() {
      super();
   }

   protected void func_207184_a(StateContainer.Builder<Fluid, IFluidState> var1) {
      var1.func_206894_a(field_207209_a);
   }

   public Vec3d func_205564_a(IWorldReaderBase var1, BlockPos var2, IFluidState var3) {
      double var4 = 0.0D;
      double var6 = 0.0D;
      BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var9 = null;

      try {
         Iterator var10 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var10.hasNext()) {
            EnumFacing var11 = (EnumFacing)var10.next();
            var8.func_189533_g(var2).func_189536_c(var11);
            IFluidState var12 = var1.func_204610_c(var8);
            if (this.func_212189_g(var12)) {
               float var13 = var12.func_206885_f();
               float var14 = 0.0F;
               if (var13 == 0.0F) {
                  if (!var1.func_180495_p(var8).func_185904_a().func_76230_c()) {
                     IFluidState var15 = var1.func_204610_c(var8.func_177977_b());
                     if (this.func_212189_g(var15)) {
                        var13 = var15.func_206885_f();
                        if (var13 > 0.0F) {
                           var14 = var3.func_206885_f() - (var13 - 0.8888889F);
                        }
                     }
                  }
               } else if (var13 > 0.0F) {
                  var14 = var3.func_206885_f() - var13;
               }

               if (var14 != 0.0F) {
                  var4 += (double)((float)var11.func_82601_c() * var14);
                  var6 += (double)((float)var11.func_82599_e() * var14);
               }
            }
         }

         Vec3d var25 = new Vec3d(var4, 0.0D, var6);
         if ((Boolean)var3.func_177229_b(field_207209_a)) {
            label164: {
               Iterator var26 = EnumFacing.Plane.HORIZONTAL.iterator();

               EnumFacing var28;
               do {
                  if (!var26.hasNext()) {
                     break label164;
                  }

                  var28 = (EnumFacing)var26.next();
                  var8.func_189533_g(var2).func_189536_c(var28);
               } while(!this.func_205573_a(var1, var8, var28) && !this.func_205573_a(var1, var8.func_177984_a(), var28));

               var25 = var25.func_72432_b().func_72441_c(0.0D, -6.0D, 0.0D);
            }
         }

         Vec3d var27 = var25.func_72432_b();
         return var27;
      } catch (Throwable var23) {
         var9 = var23;
         throw var23;
      } finally {
         if (var8 != null) {
            if (var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var22) {
                  var9.addSuppressed(var22);
               }
            } else {
               var8.close();
            }
         }

      }
   }

   private boolean func_212189_g(IFluidState var1) {
      return var1.func_206888_e() || var1.func_206886_c().func_207187_a(this);
   }

   protected boolean func_205573_a(IBlockReader var1, BlockPos var2, EnumFacing var3) {
      IBlockState var4 = var1.func_180495_p(var2);
      Block var5 = var4.func_177230_c();
      IFluidState var6 = var1.func_204610_c(var2);
      if (var6.func_206886_c().func_207187_a(this)) {
         return false;
      } else if (var3 == EnumFacing.UP) {
         return true;
      } else if (var4.func_185904_a() == Material.field_151588_w) {
         return false;
      } else {
         boolean var7 = Block.func_193382_c(var5) || var5 instanceof BlockStairs;
         return !var7 && var4.func_193401_d(var1, var2, var3) == BlockFaceShape.SOLID;
      }
   }

   protected void func_205575_a(IWorld var1, BlockPos var2, IFluidState var3) {
      if (!var3.func_206888_e()) {
         IBlockState var4 = var1.func_180495_p(var2);
         BlockPos var5 = var2.func_177977_b();
         IBlockState var6 = var1.func_180495_p(var5);
         IFluidState var7 = this.func_205576_a(var1, var5, var6);
         if (this.func_205570_b(var1, var2, var4, EnumFacing.DOWN, var5, var6, var1.func_204610_c(var5), var7.func_206886_c())) {
            this.func_205574_a(var1, var5, var6, EnumFacing.DOWN, var7);
            if (this.func_207936_a(var1, var2) >= 3) {
               this.func_207937_a(var1, var2, var3, var4);
            }
         } else if (var3.func_206889_d() || !this.func_211759_a(var1, var7.func_206886_c(), var2, var4, var5, var6)) {
            this.func_207937_a(var1, var2, var3, var4);
         }

      }
   }

   private void func_207937_a(IWorld var1, BlockPos var2, IFluidState var3, IBlockState var4) {
      int var5 = var3.func_206882_g() - this.func_204528_b(var1);
      if ((Boolean)var3.func_177229_b(field_207209_a)) {
         var5 = 7;
      }

      if (var5 > 0) {
         Map var6 = this.func_205572_b(var1, var2, var4);
         Iterator var7 = var6.entrySet().iterator();

         while(var7.hasNext()) {
            Entry var8 = (Entry)var7.next();
            EnumFacing var9 = (EnumFacing)var8.getKey();
            IFluidState var10 = (IFluidState)var8.getValue();
            BlockPos var11 = var2.func_177972_a(var9);
            IBlockState var12 = var1.func_180495_p(var11);
            if (this.func_205570_b(var1, var2, var4, var9, var11, var12, var1.func_204610_c(var11), var10.func_206886_c())) {
               this.func_205574_a(var1, var11, var12, var9, var10);
            }
         }

      }
   }

   protected IFluidState func_205576_a(IWorldReaderBase var1, BlockPos var2, IBlockState var3) {
      int var4 = 0;
      int var5 = 0;
      Iterator var6 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var6.hasNext()) {
         EnumFacing var7 = (EnumFacing)var6.next();
         BlockPos var8 = var2.func_177972_a(var7);
         IBlockState var9 = var1.func_180495_p(var8);
         IFluidState var10 = var9.func_204520_s();
         if (var10.func_206886_c().func_207187_a(this) && this.func_212751_a(var7, var1, var2, var3, var8, var9)) {
            if (var10.func_206889_d()) {
               ++var5;
            }

            var4 = Math.max(var4, var10.func_206882_g());
         }
      }

      if (this.func_205579_d() && var5 >= 2) {
         IBlockState var11 = var1.func_180495_p(var2.func_177977_b());
         IFluidState var13 = var11.func_204520_s();
         if (var11.func_185904_a().func_76220_a() || this.func_211758_g(var13)) {
            return this.func_207204_a(false);
         }
      }

      BlockPos var12 = var2.func_177984_a();
      IBlockState var14 = var1.func_180495_p(var12);
      IFluidState var15 = var14.func_204520_s();
      if (!var15.func_206888_e() && var15.func_206886_c().func_207187_a(this) && this.func_212751_a(EnumFacing.UP, var1, var2, var3, var12, var14)) {
         return this.func_207207_a(8, true);
      } else {
         int var16 = var4 - this.func_204528_b(var1);
         if (var16 <= 0) {
            return Fluids.field_204541_a.func_207188_f();
         } else {
            return this.func_207207_a(var16, false);
         }
      }
   }

   private boolean func_212751_a(EnumFacing var1, IBlockReader var2, BlockPos var3, IBlockState var4, BlockPos var5, IBlockState var6) {
      Object2ByteLinkedOpenHashMap var7;
      if (!var4.func_177230_c().func_208619_r() && !var6.func_177230_c().func_208619_r()) {
         var7 = (Object2ByteLinkedOpenHashMap)field_212756_e.get();
      } else {
         var7 = null;
      }

      Block.RenderSideCacheKey var8;
      if (var7 != null) {
         var8 = new Block.RenderSideCacheKey(var4, var6, var1);
         byte var9 = var7.getAndMoveToFirst(var8);
         if (var9 != 127) {
            return var9 != 0;
         }
      } else {
         var8 = null;
      }

      VoxelShape var12 = var4.func_196952_d(var2, var3);
      VoxelShape var10 = var6.func_196952_d(var2, var5);
      boolean var11 = !VoxelShapes.func_204642_b(var12, var10, var1);
      if (var7 != null) {
         if (var7.size() == 200) {
            var7.removeLastByte();
         }

         var7.putAndMoveToFirst(var8, (byte)(var11 ? 1 : 0));
      }

      return var11;
   }

   public abstract Fluid func_210197_e();

   public IFluidState func_207207_a(int var1, boolean var2) {
      return (IFluidState)((IFluidState)this.func_210197_e().func_207188_f().func_206870_a(field_207210_b, var1)).func_206870_a(field_207209_a, var2);
   }

   public abstract Fluid func_210198_f();

   public IFluidState func_207204_a(boolean var1) {
      return (IFluidState)this.func_210198_f().func_207188_f().func_206870_a(field_207209_a, var1);
   }

   protected abstract boolean func_205579_d();

   protected void func_205574_a(IWorld var1, BlockPos var2, IBlockState var3, EnumFacing var4, IFluidState var5) {
      if (var3.func_177230_c() instanceof ILiquidContainer) {
         ((ILiquidContainer)var3.func_177230_c()).func_204509_a(var1, var2, var3, var5);
      } else {
         if (!var3.func_196958_f()) {
            this.func_205580_a(var1, var2, var3);
         }

         var1.func_180501_a(var2, var5.func_206883_i(), 3);
      }

   }

   protected abstract void func_205580_a(IWorld var1, BlockPos var2, IBlockState var3);

   private static short func_212752_a(BlockPos var0, BlockPos var1) {
      int var2 = var1.func_177958_n() - var0.func_177958_n();
      int var3 = var1.func_177952_p() - var0.func_177952_p();
      return (short)((var2 + 128 & 255) << 8 | var3 + 128 & 255);
   }

   protected int func_205571_a(IWorldReaderBase var1, BlockPos var2, int var3, EnumFacing var4, IBlockState var5, BlockPos var6, Short2ObjectMap<Pair<IBlockState, IFluidState>> var7, Short2BooleanMap var8) {
      int var9 = 1000;
      Iterator var10 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var10.hasNext()) {
         EnumFacing var11 = (EnumFacing)var10.next();
         if (var11 != var4) {
            BlockPos var12 = var2.func_177972_a(var11);
            short var13 = func_212752_a(var6, var12);
            Pair var14 = (Pair)var7.computeIfAbsent(var13, (var2x) -> {
               IBlockState var3 = var1.func_180495_p(var12);
               return Pair.of(var3, var3.func_204520_s());
            });
            IBlockState var15 = (IBlockState)var14.getFirst();
            IFluidState var16 = (IFluidState)var14.getSecond();
            if (this.func_211760_a(var1, this.func_210197_e(), var2, var5, var11, var12, var15, var16)) {
               boolean var17 = var8.computeIfAbsent(var13, (var4x) -> {
                  BlockPos var5 = var12.func_177977_b();
                  IBlockState var6 = var1.func_180495_p(var5);
                  return this.func_211759_a(var1, this.func_210197_e(), var12, var15, var5, var6);
               });
               if (var17) {
                  return var3;
               }

               if (var3 < this.func_185698_b(var1)) {
                  int var18 = this.func_205571_a(var1, var12, var3 + 1, var11.func_176734_d(), var15, var6, var7, var8);
                  if (var18 < var9) {
                     var9 = var18;
                  }
               }
            }
         }
      }

      return var9;
   }

   private boolean func_211759_a(IBlockReader var1, Fluid var2, BlockPos var3, IBlockState var4, BlockPos var5, IBlockState var6) {
      if (!this.func_212751_a(EnumFacing.DOWN, var1, var3, var4, var5, var6)) {
         return false;
      } else {
         return var6.func_204520_s().func_206886_c().func_207187_a(this) ? true : this.func_211761_a(var1, var5, var6, var2);
      }
   }

   private boolean func_211760_a(IBlockReader var1, Fluid var2, BlockPos var3, IBlockState var4, EnumFacing var5, BlockPos var6, IBlockState var7, IFluidState var8) {
      return !this.func_211758_g(var8) && this.func_212751_a(var5, var1, var3, var4, var6, var7) && this.func_211761_a(var1, var6, var7, var2);
   }

   private boolean func_211758_g(IFluidState var1) {
      return var1.func_206886_c().func_207187_a(this) && var1.func_206889_d();
   }

   protected abstract int func_185698_b(IWorldReaderBase var1);

   private int func_207936_a(IWorldReaderBase var1, BlockPos var2) {
      int var3 = 0;
      Iterator var4 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         EnumFacing var5 = (EnumFacing)var4.next();
         BlockPos var6 = var2.func_177972_a(var5);
         IFluidState var7 = var1.func_204610_c(var6);
         if (this.func_211758_g(var7)) {
            ++var3;
         }
      }

      return var3;
   }

   protected Map<EnumFacing, IFluidState> func_205572_b(IWorldReaderBase var1, BlockPos var2, IBlockState var3) {
      int var4 = 1000;
      EnumMap var5 = Maps.newEnumMap(EnumFacing.class);
      Short2ObjectOpenHashMap var6 = new Short2ObjectOpenHashMap();
      Short2BooleanOpenHashMap var7 = new Short2BooleanOpenHashMap();
      Iterator var8 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var8.hasNext()) {
         EnumFacing var9 = (EnumFacing)var8.next();
         BlockPos var10 = var2.func_177972_a(var9);
         short var11 = func_212752_a(var2, var10);
         Pair var12 = (Pair)var6.computeIfAbsent(var11, (var2x) -> {
            IBlockState var3 = var1.func_180495_p(var10);
            return Pair.of(var3, var3.func_204520_s());
         });
         IBlockState var13 = (IBlockState)var12.getFirst();
         IFluidState var14 = (IFluidState)var12.getSecond();
         IFluidState var15 = this.func_205576_a(var1, var10, var13);
         if (this.func_211760_a(var1, var15.func_206886_c(), var2, var3, var9, var10, var13, var14)) {
            BlockPos var17 = var10.func_177977_b();
            boolean var18 = var7.computeIfAbsent(var11, (var5x) -> {
               IBlockState var6 = var1.func_180495_p(var17);
               return this.func_211759_a(var1, this.func_210197_e(), var10, var13, var17, var6);
            });
            int var16;
            if (var18) {
               var16 = 0;
            } else {
               var16 = this.func_205571_a(var1, var10, 1, var9.func_176734_d(), var13, var2, var6, var7);
            }

            if (var16 < var4) {
               var5.clear();
            }

            if (var16 <= var4) {
               var5.put(var9, var15);
               var4 = var16;
            }
         }
      }

      return var5;
   }

   private boolean func_211761_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      Block var5 = var3.func_177230_c();
      if (var5 instanceof ILiquidContainer) {
         return ((ILiquidContainer)var5).func_204510_a(var1, var2, var3, var4);
      } else if (!(var5 instanceof BlockDoor) && var5 != Blocks.field_196649_cc && var5 != Blocks.field_150468_ap && var5 != Blocks.field_196608_cF && var5 != Blocks.field_203203_C) {
         Material var6 = var3.func_185904_a();
         if (var6 != Material.field_151567_E && var6 != Material.field_189963_J && var6 != Material.field_203243_f && var6 != Material.field_204868_h) {
            return !var6.func_76230_c();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean func_205570_b(IBlockReader var1, BlockPos var2, IBlockState var3, EnumFacing var4, BlockPos var5, IBlockState var6, IFluidState var7, Fluid var8) {
      return var7.func_211725_a(var8, var4) && this.func_212751_a(var4, var1, var2, var3, var5, var6) && this.func_211761_a(var1, var5, var6, var8);
   }

   protected abstract int func_204528_b(IWorldReaderBase var1);

   protected int func_205578_a(World var1, IFluidState var2, IFluidState var3) {
      return this.func_205569_a(var1);
   }

   public void func_207191_a(World var1, BlockPos var2, IFluidState var3) {
      if (!var3.func_206889_d()) {
         IFluidState var4 = this.func_205576_a(var1, var2, var1.func_180495_p(var2));
         int var5 = this.func_205578_a(var1, var3, var4);
         if (var4.func_206888_e()) {
            var3 = var4;
            var1.func_180501_a(var2, Blocks.field_150350_a.func_176223_P(), 3);
         } else if (!var4.equals(var3)) {
            var3 = var4;
            IBlockState var6 = var4.func_206883_i();
            var1.func_180501_a(var2, var6, 2);
            var1.func_205219_F_().func_205360_a(var2, var4.func_206886_c(), var5);
            var1.func_195593_d(var2, var6.func_177230_c());
         }
      }

      this.func_205575_a(var1, var2, var3);
   }

   protected static int func_207205_e(IFluidState var0) {
      return var0.func_206889_d() ? 0 : 8 - Math.min(var0.func_206882_g(), 8) + ((Boolean)var0.func_177229_b(field_207209_a) ? 8 : 0);
   }

   public float func_207181_a(IFluidState var1) {
      return (float)var1.func_206882_g() / 9.0F;
   }

   static {
      field_207209_a = BlockStateProperties.field_208183_j;
      field_207210_b = BlockStateProperties.field_208131_af;
      field_212756_e = ThreadLocal.withInitial(() -> {
         Object2ByteLinkedOpenHashMap var0 = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(200) {
            protected void rehash(int var1) {
            }
         };
         var0.defaultReturnValue((byte)127);
         return var0;
      });
   }
}
