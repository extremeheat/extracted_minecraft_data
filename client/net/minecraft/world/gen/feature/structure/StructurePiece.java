package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class StructurePiece {
   protected static final IBlockState field_202556_l;
   protected MutableBoundingBox field_74887_e;
   @Nullable
   private EnumFacing field_74885_f;
   private Mirror field_186168_b;
   private Rotation field_186169_c;
   protected int field_74886_g;
   private static final Set<Block> field_211413_d;

   public StructurePiece() {
      super();
   }

   protected StructurePiece(int var1) {
      super();
      this.field_74886_g = var1;
   }

   public final NBTTagCompound func_143010_b() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74778_a("id", StructureIO.func_143036_a(this));
      var1.func_74782_a("BB", this.field_74887_e.func_151535_h());
      EnumFacing var2 = this.func_186165_e();
      var1.func_74768_a("O", var2 == null ? -1 : var2.func_176736_b());
      var1.func_74768_a("GD", this.field_74886_g);
      this.func_143012_a(var1);
      return var1;
   }

   protected abstract void func_143012_a(NBTTagCompound var1);

   public void func_143009_a(IWorld var1, NBTTagCompound var2) {
      if (var2.func_74764_b("BB")) {
         this.field_74887_e = new MutableBoundingBox(var2.func_74759_k("BB"));
      }

      int var3 = var2.func_74762_e("O");
      this.func_186164_a(var3 == -1 ? null : EnumFacing.func_176731_b(var3));
      this.field_74886_g = var2.func_74762_e("GD");
      this.func_143011_b(var2, var1.func_72860_G().func_186340_h());
   }

   protected abstract void func_143011_b(NBTTagCompound var1, TemplateManager var2);

   public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
   }

   public abstract boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4);

   public MutableBoundingBox func_74874_b() {
      return this.field_74887_e;
   }

   public int func_74877_c() {
      return this.field_74886_g;
   }

   public static StructurePiece func_74883_a(List<StructurePiece> var0, MutableBoundingBox var1) {
      Iterator var2 = var0.iterator();

      StructurePiece var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (StructurePiece)var2.next();
      } while(var3.func_74874_b() == null || !var3.func_74874_b().func_78884_a(var1));

      return var3;
   }

   protected boolean func_74860_a(IBlockReader var1, MutableBoundingBox var2) {
      int var3 = Math.max(this.field_74887_e.field_78897_a - 1, var2.field_78897_a);
      int var4 = Math.max(this.field_74887_e.field_78895_b - 1, var2.field_78895_b);
      int var5 = Math.max(this.field_74887_e.field_78896_c - 1, var2.field_78896_c);
      int var6 = Math.min(this.field_74887_e.field_78893_d + 1, var2.field_78893_d);
      int var7 = Math.min(this.field_74887_e.field_78894_e + 1, var2.field_78894_e);
      int var8 = Math.min(this.field_74887_e.field_78892_f + 1, var2.field_78892_f);
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

      int var10;
      int var11;
      for(var10 = var3; var10 <= var6; ++var10) {
         for(var11 = var5; var11 <= var8; ++var11) {
            if (var1.func_180495_p(var9.func_181079_c(var10, var4, var11)).func_185904_a().func_76224_d()) {
               return true;
            }

            if (var1.func_180495_p(var9.func_181079_c(var10, var7, var11)).func_185904_a().func_76224_d()) {
               return true;
            }
         }
      }

      for(var10 = var3; var10 <= var6; ++var10) {
         for(var11 = var4; var11 <= var7; ++var11) {
            if (var1.func_180495_p(var9.func_181079_c(var10, var11, var5)).func_185904_a().func_76224_d()) {
               return true;
            }

            if (var1.func_180495_p(var9.func_181079_c(var10, var11, var8)).func_185904_a().func_76224_d()) {
               return true;
            }
         }
      }

      for(var10 = var5; var10 <= var8; ++var10) {
         for(var11 = var4; var11 <= var7; ++var11) {
            if (var1.func_180495_p(var9.func_181079_c(var3, var11, var10)).func_185904_a().func_76224_d()) {
               return true;
            }

            if (var1.func_180495_p(var9.func_181079_c(var6, var11, var10)).func_185904_a().func_76224_d()) {
               return true;
            }
         }
      }

      return false;
   }

   protected int func_74865_a(int var1, int var2) {
      EnumFacing var3 = this.func_186165_e();
      if (var3 == null) {
         return var1;
      } else {
         switch(var3) {
         case NORTH:
         case SOUTH:
            return this.field_74887_e.field_78897_a + var1;
         case WEST:
            return this.field_74887_e.field_78893_d - var2;
         case EAST:
            return this.field_74887_e.field_78897_a + var2;
         default:
            return var1;
         }
      }
   }

   protected int func_74862_a(int var1) {
      return this.func_186165_e() == null ? var1 : var1 + this.field_74887_e.field_78895_b;
   }

   protected int func_74873_b(int var1, int var2) {
      EnumFacing var3 = this.func_186165_e();
      if (var3 == null) {
         return var2;
      } else {
         switch(var3) {
         case NORTH:
            return this.field_74887_e.field_78892_f - var2;
         case SOUTH:
            return this.field_74887_e.field_78896_c + var2;
         case WEST:
         case EAST:
            return this.field_74887_e.field_78896_c + var1;
         default:
            return var2;
         }
      }
   }

   protected void func_175811_a(IWorld var1, IBlockState var2, int var3, int var4, int var5, MutableBoundingBox var6) {
      BlockPos var7 = new BlockPos(this.func_74865_a(var3, var5), this.func_74862_a(var4), this.func_74873_b(var3, var5));
      if (var6.func_175898_b(var7)) {
         if (this.field_186168_b != Mirror.NONE) {
            var2 = var2.func_185902_a(this.field_186168_b);
         }

         if (this.field_186169_c != Rotation.NONE) {
            var2 = var2.func_185907_a(this.field_186169_c);
         }

         var1.func_180501_a(var7, var2, 2);
         IFluidState var8 = var1.func_204610_c(var7);
         if (!var8.func_206888_e()) {
            var1.func_205219_F_().func_205360_a(var7, var8.func_206886_c(), 0);
         }

         if (field_211413_d.contains(var2.func_177230_c())) {
            var1.func_205771_y(var7).func_201594_d(var7);
         }

      }
   }

   protected IBlockState func_175807_a(IBlockReader var1, int var2, int var3, int var4, MutableBoundingBox var5) {
      int var6 = this.func_74865_a(var2, var4);
      int var7 = this.func_74862_a(var3);
      int var8 = this.func_74873_b(var2, var4);
      BlockPos var9 = new BlockPos(var6, var7, var8);
      return !var5.func_175898_b(var9) ? Blocks.field_150350_a.func_176223_P() : var1.func_180495_p(var9);
   }

   protected boolean func_189916_b(IWorldReaderBase var1, int var2, int var3, int var4, MutableBoundingBox var5) {
      int var6 = this.func_74865_a(var2, var4);
      int var7 = this.func_74862_a(var3 + 1);
      int var8 = this.func_74873_b(var2, var4);
      BlockPos var9 = new BlockPos(var6, var7, var8);
      if (!var5.func_175898_b(var9)) {
         return false;
      } else {
         return var7 < var1.func_201676_a(Heightmap.Type.OCEAN_FLOOR_WG, var6, var8);
      }
   }

   protected void func_74878_a(IWorld var1, MutableBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      for(int var9 = var4; var9 <= var7; ++var9) {
         for(int var10 = var3; var10 <= var6; ++var10) {
            for(int var11 = var5; var11 <= var8; ++var11) {
               this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), var10, var9, var11, var2);
            }
         }
      }

   }

   protected void func_175804_a(IWorld var1, MutableBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, IBlockState var9, IBlockState var10, boolean var11) {
      for(int var12 = var4; var12 <= var7; ++var12) {
         for(int var13 = var3; var13 <= var6; ++var13) {
            for(int var14 = var5; var14 <= var8; ++var14) {
               if (!var11 || !this.func_175807_a(var1, var13, var12, var14, var2).func_196958_f()) {
                  if (var12 != var4 && var12 != var7 && var13 != var3 && var13 != var6 && var14 != var5 && var14 != var8) {
                     this.func_175811_a(var1, var10, var13, var12, var14, var2);
                  } else {
                     this.func_175811_a(var1, var9, var13, var12, var14, var2);
                  }
               }
            }
         }
      }

   }

   protected void func_74882_a(IWorld var1, MutableBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, Random var10, StructurePiece.BlockSelector var11) {
      for(int var12 = var4; var12 <= var7; ++var12) {
         for(int var13 = var3; var13 <= var6; ++var13) {
            for(int var14 = var5; var14 <= var8; ++var14) {
               if (!var9 || !this.func_175807_a(var1, var13, var12, var14, var2).func_196958_f()) {
                  var11.func_75062_a(var10, var13, var12, var14, var12 == var4 || var12 == var7 || var13 == var3 || var13 == var6 || var14 == var5 || var14 == var8);
                  this.func_175811_a(var1, var11.func_180780_a(), var13, var12, var14, var2);
               }
            }
         }
      }

   }

   protected void func_189914_a(IWorld var1, MutableBoundingBox var2, Random var3, float var4, int var5, int var6, int var7, int var8, int var9, int var10, IBlockState var11, IBlockState var12, boolean var13, boolean var14) {
      for(int var15 = var6; var15 <= var9; ++var15) {
         for(int var16 = var5; var16 <= var8; ++var16) {
            for(int var17 = var7; var17 <= var10; ++var17) {
               if (var3.nextFloat() <= var4 && (!var13 || !this.func_175807_a(var1, var16, var15, var17, var2).func_196958_f()) && (!var14 || this.func_189916_b(var1, var16, var15, var17, var2))) {
                  if (var15 != var6 && var15 != var9 && var16 != var5 && var16 != var8 && var17 != var7 && var17 != var10) {
                     this.func_175811_a(var1, var12, var16, var15, var17, var2);
                  } else {
                     this.func_175811_a(var1, var11, var16, var15, var17, var2);
                  }
               }
            }
         }
      }

   }

   protected void func_175809_a(IWorld var1, MutableBoundingBox var2, Random var3, float var4, int var5, int var6, int var7, IBlockState var8) {
      if (var3.nextFloat() < var4) {
         this.func_175811_a(var1, var8, var5, var6, var7, var2);
      }

   }

   protected void func_180777_a(IWorld var1, MutableBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, IBlockState var9, boolean var10) {
      float var11 = (float)(var6 - var3 + 1);
      float var12 = (float)(var7 - var4 + 1);
      float var13 = (float)(var8 - var5 + 1);
      float var14 = (float)var3 + var11 / 2.0F;
      float var15 = (float)var5 + var13 / 2.0F;

      for(int var16 = var4; var16 <= var7; ++var16) {
         float var17 = (float)(var16 - var4) / var12;

         for(int var18 = var3; var18 <= var6; ++var18) {
            float var19 = ((float)var18 - var14) / (var11 * 0.5F);

            for(int var20 = var5; var20 <= var8; ++var20) {
               float var21 = ((float)var20 - var15) / (var13 * 0.5F);
               if (!var10 || !this.func_175807_a(var1, var18, var16, var20, var2).func_196958_f()) {
                  float var22 = var19 * var19 + var17 * var17 + var21 * var21;
                  if (var22 <= 1.05F) {
                     this.func_175811_a(var1, var9, var18, var16, var20, var2);
                  }
               }
            }
         }
      }

   }

   protected void func_74871_b(IWorld var1, int var2, int var3, int var4, MutableBoundingBox var5) {
      BlockPos var6 = new BlockPos(this.func_74865_a(var2, var4), this.func_74862_a(var3), this.func_74873_b(var2, var4));
      if (var5.func_175898_b(var6)) {
         while(!var1.func_175623_d(var6) && var6.func_177956_o() < 255) {
            var1.func_180501_a(var6, Blocks.field_150350_a.func_176223_P(), 2);
            var6 = var6.func_177984_a();
         }

      }
   }

   protected void func_175808_b(IWorld var1, IBlockState var2, int var3, int var4, int var5, MutableBoundingBox var6) {
      int var7 = this.func_74865_a(var3, var5);
      int var8 = this.func_74862_a(var4);
      int var9 = this.func_74873_b(var3, var5);
      if (var6.func_175898_b(new BlockPos(var7, var8, var9))) {
         while((var1.func_175623_d(new BlockPos(var7, var8, var9)) || var1.func_180495_p(new BlockPos(var7, var8, var9)).func_185904_a().func_76224_d()) && var8 > 1) {
            var1.func_180501_a(new BlockPos(var7, var8, var9), var2, 2);
            --var8;
         }

      }
   }

   protected boolean func_186167_a(IWorld var1, MutableBoundingBox var2, Random var3, int var4, int var5, int var6, ResourceLocation var7) {
      BlockPos var8 = new BlockPos(this.func_74865_a(var4, var6), this.func_74862_a(var5), this.func_74873_b(var4, var6));
      return this.func_191080_a(var1, var2, var3, var8, var7, (IBlockState)null);
   }

   public static IBlockState func_197528_a(IBlockReader var0, BlockPos var1, IBlockState var2) {
      EnumFacing var3 = null;
      Iterator var4 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         EnumFacing var5 = (EnumFacing)var4.next();
         BlockPos var6 = var1.func_177972_a(var5);
         IBlockState var7 = var0.func_180495_p(var6);
         if (var7.func_177230_c() == Blocks.field_150486_ae) {
            return var2;
         }

         if (var7.func_200015_d(var0, var6)) {
            if (var3 != null) {
               var3 = null;
               break;
            }

            var3 = var5;
         }
      }

      if (var3 != null) {
         return (IBlockState)var2.func_206870_a(BlockHorizontal.field_185512_D, var3.func_176734_d());
      } else {
         EnumFacing var8 = (EnumFacing)var2.func_177229_b(BlockHorizontal.field_185512_D);
         BlockPos var9 = var1.func_177972_a(var8);
         if (var0.func_180495_p(var9).func_200015_d(var0, var9)) {
            var8 = var8.func_176734_d();
            var9 = var1.func_177972_a(var8);
         }

         if (var0.func_180495_p(var9).func_200015_d(var0, var9)) {
            var8 = var8.func_176746_e();
            var9 = var1.func_177972_a(var8);
         }

         if (var0.func_180495_p(var9).func_200015_d(var0, var9)) {
            var8 = var8.func_176734_d();
            var1.func_177972_a(var8);
         }

         return (IBlockState)var2.func_206870_a(BlockHorizontal.field_185512_D, var8);
      }
   }

   protected boolean func_191080_a(IWorld var1, MutableBoundingBox var2, Random var3, BlockPos var4, ResourceLocation var5, @Nullable IBlockState var6) {
      if (var2.func_175898_b(var4) && var1.func_180495_p(var4).func_177230_c() != Blocks.field_150486_ae) {
         if (var6 == null) {
            var6 = func_197528_a(var1, var4, Blocks.field_150486_ae.func_176223_P());
         }

         var1.func_180501_a(var4, var6, 2);
         TileEntity var7 = var1.func_175625_s(var4);
         if (var7 instanceof TileEntityChest) {
            ((TileEntityChest)var7).func_189404_a(var5, var3.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean func_189419_a(IWorld var1, MutableBoundingBox var2, Random var3, int var4, int var5, int var6, EnumFacing var7, ResourceLocation var8) {
      BlockPos var9 = new BlockPos(this.func_74865_a(var4, var6), this.func_74862_a(var5), this.func_74873_b(var4, var6));
      if (var2.func_175898_b(var9) && var1.func_180495_p(var9).func_177230_c() != Blocks.field_150367_z) {
         this.func_175811_a(var1, (IBlockState)Blocks.field_150367_z.func_176223_P().func_206870_a(BlockDispenser.field_176441_a, var7), var4, var5, var6, var2);
         TileEntity var10 = var1.func_175625_s(var9);
         if (var10 instanceof TileEntityDispenser) {
            ((TileEntityDispenser)var10).func_189404_a(var8, var3.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   protected void func_189915_a(IWorld var1, MutableBoundingBox var2, Random var3, int var4, int var5, int var6, EnumFacing var7, BlockDoor var8) {
      this.func_175811_a(var1, (IBlockState)var8.func_176223_P().func_206870_a(BlockDoor.field_176520_a, var7), var4, var5, var6, var2);
      this.func_175811_a(var1, (IBlockState)((IBlockState)var8.func_176223_P().func_206870_a(BlockDoor.field_176520_a, var7)).func_206870_a(BlockDoor.field_176523_O, DoubleBlockHalf.UPPER), var4, var5 + 1, var6, var2);
   }

   public void func_181138_a(int var1, int var2, int var3) {
      this.field_74887_e.func_78886_a(var1, var2, var3);
   }

   @Nullable
   public EnumFacing func_186165_e() {
      return this.field_74885_f;
   }

   public void func_186164_a(@Nullable EnumFacing var1) {
      this.field_74885_f = var1;
      if (var1 == null) {
         this.field_186169_c = Rotation.NONE;
         this.field_186168_b = Mirror.NONE;
      } else {
         switch(var1) {
         case SOUTH:
            this.field_186168_b = Mirror.LEFT_RIGHT;
            this.field_186169_c = Rotation.NONE;
            break;
         case WEST:
            this.field_186168_b = Mirror.LEFT_RIGHT;
            this.field_186169_c = Rotation.CLOCKWISE_90;
            break;
         case EAST:
            this.field_186168_b = Mirror.NONE;
            this.field_186169_c = Rotation.CLOCKWISE_90;
            break;
         default:
            this.field_186168_b = Mirror.NONE;
            this.field_186169_c = Rotation.NONE;
         }
      }

   }

   static {
      field_202556_l = Blocks.field_201941_jj.func_176223_P();
      field_211413_d = ImmutableSet.builder().add(Blocks.field_150386_bk).add(Blocks.field_150478_aa).add(Blocks.field_196591_bQ).add(Blocks.field_180407_aO).add(Blocks.field_180408_aP).add(Blocks.field_180406_aS).add(Blocks.field_180405_aT).add(Blocks.field_180404_aQ).add(Blocks.field_180403_aR).add(Blocks.field_150468_ap).add(Blocks.field_150411_aY).build();
   }

   public abstract static class BlockSelector {
      protected IBlockState field_151562_a;

      protected BlockSelector() {
         super();
         this.field_151562_a = Blocks.field_150350_a.func_176223_P();
      }

      public abstract void func_75062_a(Random var1, int var2, int var3, int var4, boolean var5);

      public IBlockState func_180780_a() {
         return this.field_151562_a;
      }
   }
}
