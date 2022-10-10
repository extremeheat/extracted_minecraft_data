package net.minecraft.block;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {
   protected static final VoxelShape field_185590_a = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   protected static final VoxelShape field_190959_b = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final boolean field_196277_c;

   public static boolean func_208488_a(World var0, BlockPos var1) {
      return func_208487_j(var0.func_180495_p(var1));
   }

   public static boolean func_208487_j(IBlockState var0) {
      return var0.func_203425_a(BlockTags.field_203437_y);
   }

   protected BlockRailBase(boolean var1, Block.Properties var2) {
      super(var2);
      this.field_196277_c = var1;
   }

   public boolean func_208490_b() {
      return this.field_196277_c;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      RailShape var4 = var1.func_177230_c() == this ? (RailShape)var1.func_177229_b(this.func_176560_l()) : null;
      return var4 != null && var4.func_208092_c() ? field_190959_b : field_185590_a;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      return var2.func_180495_p(var3.func_177977_b()).func_185896_q();
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var4.func_177230_c() != var1.func_177230_c()) {
         if (!var2.field_72995_K) {
            var1 = this.func_208489_a(var2, var3, var1, true);
            if (this.field_196277_c) {
               var1.func_189546_a(var2, var3, this, var3);
            }
         }

      }
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if (!var2.field_72995_K) {
         RailShape var6 = (RailShape)var1.func_177229_b(this.func_176560_l());
         boolean var7 = false;
         if (!var2.func_180495_p(var3.func_177977_b()).func_185896_q()) {
            var7 = true;
         }

         if (var6 == RailShape.ASCENDING_EAST && !var2.func_180495_p(var3.func_177974_f()).func_185896_q()) {
            var7 = true;
         } else if (var6 == RailShape.ASCENDING_WEST && !var2.func_180495_p(var3.func_177976_e()).func_185896_q()) {
            var7 = true;
         } else if (var6 == RailShape.ASCENDING_NORTH && !var2.func_180495_p(var3.func_177978_c()).func_185896_q()) {
            var7 = true;
         } else if (var6 == RailShape.ASCENDING_SOUTH && !var2.func_180495_p(var3.func_177968_d()).func_185896_q()) {
            var7 = true;
         }

         if (var7 && !var2.func_175623_d(var3)) {
            var1.func_196941_a(var2, var3, 1.0F, 0);
            var2.func_175698_g(var3);
         } else {
            this.func_189541_b(var1, var2, var3, var4);
         }

      }
   }

   protected void func_189541_b(IBlockState var1, World var2, BlockPos var3, Block var4) {
   }

   protected IBlockState func_208489_a(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return var1.field_72995_K ? var3 : (new BlockRailState(var1, var2, var3)).func_208511_a(var1.func_175640_z(var2), var4).func_196916_c();
   }

   public EnumPushReaction func_149656_h(IBlockState var1) {
      return EnumPushReaction.NORMAL;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (!var5) {
         super.func_196243_a(var1, var2, var3, var4, var5);
         if (((RailShape)var1.func_177229_b(this.func_176560_l())).func_208092_c()) {
            var2.func_195593_d(var3.func_177984_a(), this);
         }

         if (this.field_196277_c) {
            var2.func_195593_d(var3, this);
            var2.func_195593_d(var3.func_177977_b(), this);
         }

      }
   }

   public abstract IProperty<RailShape> func_176560_l();
}
