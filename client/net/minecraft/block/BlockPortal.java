package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockPortal extends Block {
   public static final EnumProperty<EnumFacing.Axis> field_176550_a;
   protected static final VoxelShape field_185683_b;
   protected static final VoxelShape field_185684_c;

   public BlockPortal(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176550_a, EnumFacing.Axis.X));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      switch((EnumFacing.Axis)var1.func_177229_b(field_176550_a)) {
      case Z:
         return field_185684_c;
      case X:
      default:
         return field_185683_b;
      }
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (var2.field_73011_w.func_76569_d() && var2.func_82736_K().func_82766_b("doMobSpawning") && var4.nextInt(2000) < var2.func_175659_aa().func_151525_a()) {
         int var5 = var3.func_177956_o();

         BlockPos var6;
         for(var6 = var3; !var2.func_180495_p(var6).func_185896_q() && var6.func_177956_o() > 0; var6 = var6.func_177977_b()) {
         }

         if (var5 > 0 && !var2.func_180495_p(var6.func_177984_a()).func_185915_l()) {
            Entity var7 = EntityType.field_200785_Y.func_208050_a(var2, (NBTTagCompound)null, (ITextComponent)null, (EntityPlayer)null, var6.func_177984_a(), false, false);
            if (var7 != null) {
               var7.field_71088_bW = var7.func_82147_ab();
            }
         }
      }

   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_176548_d(IWorld var1, BlockPos var2) {
      BlockPortal.Size var3 = this.func_201816_b(var1, var2);
      if (var3 != null) {
         var3.func_150859_c();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public BlockPortal.Size func_201816_b(IWorld var1, BlockPos var2) {
      BlockPortal.Size var3 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.X);
      if (var3.func_150860_b() && var3.field_150864_e == 0) {
         return var3;
      } else {
         BlockPortal.Size var4 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.Z);
         return var4.func_150860_b() && var4.field_150864_e == 0 ? var4 : null;
      }
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      EnumFacing.Axis var7 = var2.func_176740_k();
      EnumFacing.Axis var8 = (EnumFacing.Axis)var1.func_177229_b(field_176550_a);
      boolean var9 = var8 != var7 && var7.func_176722_c();
      return !var9 && var3.func_177230_c() != this && !(new BlockPortal.Size(var4, var5, var8)).func_208508_f() ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 0;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      if (!var4.func_184218_aH() && !var4.func_184207_aI() && var4.func_184222_aU()) {
         var4.func_181015_d(var3);
      }

   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (var4.nextInt(100) == 0) {
         var2.func_184134_a((double)var3.func_177958_n() + 0.5D, (double)var3.func_177956_o() + 0.5D, (double)var3.func_177952_p() + 0.5D, SoundEvents.field_187810_eg, SoundCategory.BLOCKS, 0.5F, var4.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int var5 = 0; var5 < 4; ++var5) {
         double var6 = (double)((float)var3.func_177958_n() + var4.nextFloat());
         double var8 = (double)((float)var3.func_177956_o() + var4.nextFloat());
         double var10 = (double)((float)var3.func_177952_p() + var4.nextFloat());
         double var12 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
         double var14 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
         double var16 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
         int var18 = var4.nextInt(2) * 2 - 1;
         if (var2.func_180495_p(var3.func_177976_e()).func_177230_c() != this && var2.func_180495_p(var3.func_177974_f()).func_177230_c() != this) {
            var6 = (double)var3.func_177958_n() + 0.5D + 0.25D * (double)var18;
            var12 = (double)(var4.nextFloat() * 2.0F * (float)var18);
         } else {
            var10 = (double)var3.func_177952_p() + 0.5D + 0.25D * (double)var18;
            var16 = (double)(var4.nextFloat() * 2.0F * (float)var18);
         }

         var2.func_195594_a(Particles.field_197599_J, var6, var8, var10, var12, var14, var16);
      }

   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return ItemStack.field_190927_a;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      switch(var2) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((EnumFacing.Axis)var1.func_177229_b(field_176550_a)) {
         case Z:
            return (IBlockState)var1.func_206870_a(field_176550_a, EnumFacing.Axis.X);
         case X:
            return (IBlockState)var1.func_206870_a(field_176550_a, EnumFacing.Axis.Z);
         default:
            return var1;
         }
      default:
         return var1;
      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176550_a);
   }

   public BlockPattern.PatternHelper func_181089_f(IWorld var1, BlockPos var2) {
      EnumFacing.Axis var3 = EnumFacing.Axis.Z;
      BlockPortal.Size var4 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.X);
      LoadingCache var5 = BlockPattern.func_181627_a(var1, true);
      if (!var4.func_150860_b()) {
         var3 = EnumFacing.Axis.X;
         var4 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.Z);
      }

      if (!var4.func_150860_b()) {
         return new BlockPattern.PatternHelper(var2, EnumFacing.NORTH, EnumFacing.UP, var5, 1, 1, 1);
      } else {
         int[] var6 = new int[EnumFacing.AxisDirection.values().length];
         EnumFacing var7 = var4.field_150866_c.func_176735_f();
         BlockPos var8 = var4.field_150861_f.func_177981_b(var4.func_181100_a() - 1);
         EnumFacing.AxisDirection[] var9 = EnumFacing.AxisDirection.values();
         int var10 = var9.length;

         int var11;
         for(var11 = 0; var11 < var10; ++var11) {
            EnumFacing.AxisDirection var12 = var9[var11];
            BlockPattern.PatternHelper var13 = new BlockPattern.PatternHelper(var7.func_176743_c() == var12 ? var8 : var8.func_177967_a(var4.field_150866_c, var4.func_181101_b() - 1), EnumFacing.func_181076_a(var12, var3), EnumFacing.UP, var5, var4.func_181101_b(), var4.func_181100_a(), 1);

            for(int var14 = 0; var14 < var4.func_181101_b(); ++var14) {
               for(int var15 = 0; var15 < var4.func_181100_a(); ++var15) {
                  BlockWorldState var16 = var13.func_177670_a(var14, var15, 1);
                  if (!var16.func_177509_a().func_196958_f()) {
                     ++var6[var12.ordinal()];
                  }
               }
            }
         }

         EnumFacing.AxisDirection var17 = EnumFacing.AxisDirection.POSITIVE;
         EnumFacing.AxisDirection[] var18 = EnumFacing.AxisDirection.values();
         var11 = var18.length;

         for(int var19 = 0; var19 < var11; ++var19) {
            EnumFacing.AxisDirection var20 = var18[var19];
            if (var6[var20.ordinal()] < var6[var17.ordinal()]) {
               var17 = var20;
            }
         }

         return new BlockPattern.PatternHelper(var7.func_176743_c() == var17 ? var8 : var8.func_177967_a(var4.field_150866_c, var4.func_181101_b() - 1), EnumFacing.func_181076_a(var17, var3), EnumFacing.UP, var5, var4.func_181101_b(), var4.func_181100_a(), 1);
      }
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   static {
      field_176550_a = BlockStateProperties.field_208199_z;
      field_185683_b = Block.func_208617_a(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
      field_185684_c = Block.func_208617_a(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
   }

   public static class Size {
      private final IWorld field_150867_a;
      private final EnumFacing.Axis field_150865_b;
      private final EnumFacing field_150866_c;
      private final EnumFacing field_150863_d;
      private int field_150864_e;
      private BlockPos field_150861_f;
      private int field_150862_g;
      private int field_150868_h;

      public Size(IWorld var1, BlockPos var2, EnumFacing.Axis var3) {
         super();
         this.field_150867_a = var1;
         this.field_150865_b = var3;
         if (var3 == EnumFacing.Axis.X) {
            this.field_150863_d = EnumFacing.EAST;
            this.field_150866_c = EnumFacing.WEST;
         } else {
            this.field_150863_d = EnumFacing.NORTH;
            this.field_150866_c = EnumFacing.SOUTH;
         }

         for(BlockPos var4 = var2; var2.func_177956_o() > var4.func_177956_o() - 21 && var2.func_177956_o() > 0 && this.func_196900_a(var1.func_180495_p(var2.func_177977_b())); var2 = var2.func_177977_b()) {
         }

         int var5 = this.func_180120_a(var2, this.field_150863_d) - 1;
         if (var5 >= 0) {
            this.field_150861_f = var2.func_177967_a(this.field_150863_d, var5);
            this.field_150868_h = this.func_180120_a(this.field_150861_f, this.field_150866_c);
            if (this.field_150868_h < 2 || this.field_150868_h > 21) {
               this.field_150861_f = null;
               this.field_150868_h = 0;
            }
         }

         if (this.field_150861_f != null) {
            this.field_150862_g = this.func_150858_a();
         }

      }

      protected int func_180120_a(BlockPos var1, EnumFacing var2) {
         int var3;
         for(var3 = 0; var3 < 22; ++var3) {
            BlockPos var4 = var1.func_177967_a(var2, var3);
            if (!this.func_196900_a(this.field_150867_a.func_180495_p(var4)) || this.field_150867_a.func_180495_p(var4.func_177977_b()).func_177230_c() != Blocks.field_150343_Z) {
               break;
            }
         }

         Block var5 = this.field_150867_a.func_180495_p(var1.func_177967_a(var2, var3)).func_177230_c();
         return var5 == Blocks.field_150343_Z ? var3 : 0;
      }

      public int func_181100_a() {
         return this.field_150862_g;
      }

      public int func_181101_b() {
         return this.field_150868_h;
      }

      protected int func_150858_a() {
         int var1;
         label56:
         for(this.field_150862_g = 0; this.field_150862_g < 21; ++this.field_150862_g) {
            for(var1 = 0; var1 < this.field_150868_h; ++var1) {
               BlockPos var2 = this.field_150861_f.func_177967_a(this.field_150866_c, var1).func_177981_b(this.field_150862_g);
               IBlockState var3 = this.field_150867_a.func_180495_p(var2);
               if (!this.func_196900_a(var3)) {
                  break label56;
               }

               Block var4 = var3.func_177230_c();
               if (var4 == Blocks.field_150427_aO) {
                  ++this.field_150864_e;
               }

               if (var1 == 0) {
                  var4 = this.field_150867_a.func_180495_p(var2.func_177972_a(this.field_150863_d)).func_177230_c();
                  if (var4 != Blocks.field_150343_Z) {
                     break label56;
                  }
               } else if (var1 == this.field_150868_h - 1) {
                  var4 = this.field_150867_a.func_180495_p(var2.func_177972_a(this.field_150866_c)).func_177230_c();
                  if (var4 != Blocks.field_150343_Z) {
                     break label56;
                  }
               }
            }
         }

         for(var1 = 0; var1 < this.field_150868_h; ++var1) {
            if (this.field_150867_a.func_180495_p(this.field_150861_f.func_177967_a(this.field_150866_c, var1).func_177981_b(this.field_150862_g)).func_177230_c() != Blocks.field_150343_Z) {
               this.field_150862_g = 0;
               break;
            }
         }

         if (this.field_150862_g <= 21 && this.field_150862_g >= 3) {
            return this.field_150862_g;
         } else {
            this.field_150861_f = null;
            this.field_150868_h = 0;
            this.field_150862_g = 0;
            return 0;
         }
      }

      protected boolean func_196900_a(IBlockState var1) {
         Block var2 = var1.func_177230_c();
         return var1.func_196958_f() || var2 == Blocks.field_150480_ab || var2 == Blocks.field_150427_aO;
      }

      public boolean func_150860_b() {
         return this.field_150861_f != null && this.field_150868_h >= 2 && this.field_150868_h <= 21 && this.field_150862_g >= 3 && this.field_150862_g <= 21;
      }

      public void func_150859_c() {
         for(int var1 = 0; var1 < this.field_150868_h; ++var1) {
            BlockPos var2 = this.field_150861_f.func_177967_a(this.field_150866_c, var1);

            for(int var3 = 0; var3 < this.field_150862_g; ++var3) {
               this.field_150867_a.func_180501_a(var2.func_177981_b(var3), (IBlockState)Blocks.field_150427_aO.func_176223_P().func_206870_a(BlockPortal.field_176550_a, this.field_150865_b), 18);
            }
         }

      }

      private boolean func_196899_f() {
         return this.field_150864_e >= this.field_150868_h * this.field_150862_g;
      }

      public boolean func_208508_f() {
         return this.func_150860_b() && this.func_196899_f();
      }
   }
}
