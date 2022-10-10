package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockPistonBase extends BlockDirectional {
   public static final BooleanProperty field_176320_b;
   protected static final VoxelShape field_185648_b;
   protected static final VoxelShape field_185649_c;
   protected static final VoxelShape field_185650_d;
   protected static final VoxelShape field_185651_e;
   protected static final VoxelShape field_185652_f;
   protected static final VoxelShape field_185653_g;
   private final boolean field_150082_a;

   public BlockPistonBase(boolean var1, Block.Properties var2) {
      super(var2);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176387_N, EnumFacing.NORTH)).func_206870_a(field_176320_b, false));
      this.field_150082_a = var1;
   }

   public boolean func_176214_u(IBlockState var1) {
      return !(Boolean)var1.func_177229_b(field_176320_b);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      if ((Boolean)var1.func_177229_b(field_176320_b)) {
         switch((EnumFacing)var1.func_177229_b(field_176387_N)) {
         case DOWN:
            return field_185653_g;
         case UP:
         default:
            return field_185652_f;
         case NORTH:
            return field_185651_e;
         case SOUTH:
            return field_185650_d;
         case WEST:
            return field_185649_c;
         case EAST:
            return field_185648_b;
         }
      } else {
         return VoxelShapes.func_197868_b();
      }
   }

   public boolean func_185481_k(IBlockState var1) {
      return !(Boolean)var1.func_177229_b(field_176320_b) || var1.func_177229_b(field_176387_N) == EnumFacing.DOWN;
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (!var1.field_72995_K) {
         this.func_176316_e(var1, var2, var3);
      }

   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if (!var2.field_72995_K) {
         this.func_176316_e(var2, var3, var1);
      }

   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var4.func_177230_c() != var1.func_177230_c()) {
         if (!var2.field_72995_K && var2.func_175625_s(var3) == null) {
            this.func_176316_e(var2, var3, var1);
         }

      }
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_176387_N, var1.func_196010_d().func_176734_d())).func_206870_a(field_176320_b, false);
   }

   private void func_176316_e(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176387_N);
      boolean var5 = this.func_176318_b(var1, var2, var4);
      if (var5 && !(Boolean)var3.func_177229_b(field_176320_b)) {
         if ((new BlockPistonStructureHelper(var1, var2, var4, true)).func_177253_a()) {
            var1.func_175641_c(var2, this, 0, var4.func_176745_a());
         }
      } else if (!var5 && (Boolean)var3.func_177229_b(field_176320_b)) {
         BlockPos var6 = var2.func_177967_a(var4, 2);
         IBlockState var7 = var1.func_180495_p(var6);
         byte var8 = 1;
         if (var7.func_177230_c() == Blocks.field_196603_bb && var7.func_177229_b(field_176387_N) == var4) {
            TileEntity var9 = var1.func_175625_s(var6);
            if (var9 instanceof TileEntityPiston) {
               TileEntityPiston var10 = (TileEntityPiston)var9;
               if (var10.func_145868_b() && (var10.func_145860_a(0.0F) < 0.5F || var1.func_82737_E() == var10.func_211146_k() || ((WorldServer)var1).func_211158_j_())) {
                  var8 = 2;
               }
            }
         }

         var1.func_175641_c(var2, this, var8, var4.func_176745_a());
      }

   }

   private boolean func_176318_b(World var1, BlockPos var2, EnumFacing var3) {
      EnumFacing[] var4 = EnumFacing.values();
      int var5 = var4.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         EnumFacing var7 = var4[var6];
         if (var7 != var3 && var1.func_175709_b(var2.func_177972_a(var7), var7)) {
            return true;
         }
      }

      if (var1.func_175709_b(var2, EnumFacing.DOWN)) {
         return true;
      } else {
         BlockPos var9 = var2.func_177984_a();
         EnumFacing[] var10 = EnumFacing.values();
         var6 = var10.length;

         for(int var11 = 0; var11 < var6; ++var11) {
            EnumFacing var8 = var10[var11];
            if (var8 != EnumFacing.DOWN && var1.func_175709_b(var9.func_177972_a(var8), var8)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean func_189539_a(IBlockState var1, World var2, BlockPos var3, int var4, int var5) {
      EnumFacing var6 = (EnumFacing)var1.func_177229_b(field_176387_N);
      if (!var2.field_72995_K) {
         boolean var7 = this.func_176318_b(var2, var3, var6);
         if (var7 && (var4 == 1 || var4 == 2)) {
            var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176320_b, true), 2);
            return false;
         }

         if (!var7 && var4 == 0) {
            return false;
         }
      }

      if (var4 == 0) {
         if (!this.func_176319_a(var2, var3, var6, true)) {
            return false;
         }

         var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176320_b, true), 67);
         var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_187715_dR, SoundCategory.BLOCKS, 0.5F, var2.field_73012_v.nextFloat() * 0.25F + 0.6F);
      } else if (var4 == 1 || var4 == 2) {
         TileEntity var14 = var2.func_175625_s(var3.func_177972_a(var6));
         if (var14 instanceof TileEntityPiston) {
            ((TileEntityPiston)var14).func_145866_f();
         }

         var2.func_180501_a(var3, (IBlockState)((IBlockState)Blocks.field_196603_bb.func_176223_P().func_206870_a(BlockPistonMoving.field_196344_a, var6)).func_206870_a(BlockPistonMoving.field_196345_b, this.field_150082_a ? PistonType.STICKY : PistonType.DEFAULT), 3);
         var2.func_175690_a(var3, BlockPistonMoving.func_196343_a((IBlockState)this.func_176223_P().func_206870_a(field_176387_N, EnumFacing.func_82600_a(var5 & 7)), var6, false, true));
         if (this.field_150082_a) {
            BlockPos var8 = var3.func_177982_a(var6.func_82601_c() * 2, var6.func_96559_d() * 2, var6.func_82599_e() * 2);
            IBlockState var9 = var2.func_180495_p(var8);
            Block var10 = var9.func_177230_c();
            boolean var11 = false;
            if (var10 == Blocks.field_196603_bb) {
               TileEntity var12 = var2.func_175625_s(var8);
               if (var12 instanceof TileEntityPiston) {
                  TileEntityPiston var13 = (TileEntityPiston)var12;
                  if (var13.func_212363_d() == var6 && var13.func_145868_b()) {
                     var13.func_145866_f();
                     var11 = true;
                  }
               }
            }

            if (!var11) {
               if (var4 != 1 || var9.func_196958_f() || !func_185646_a(var9, var2, var8, var6.func_176734_d(), false, var6) || var9.func_185905_o() != EnumPushReaction.NORMAL && var10 != Blocks.field_150331_J && var10 != Blocks.field_150320_F) {
                  var2.func_175698_g(var3.func_177972_a(var6));
               } else {
                  this.func_176319_a(var2, var3, var6, false);
               }
            }
         } else {
            var2.func_175698_g(var3.func_177972_a(var6));
         }

         var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_187712_dQ, SoundCategory.BLOCKS, 0.5F, var2.field_73012_v.nextFloat() * 0.15F + 0.6F);
      }

      return true;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public static boolean func_185646_a(IBlockState var0, World var1, BlockPos var2, EnumFacing var3, boolean var4, EnumFacing var5) {
      Block var6 = var0.func_177230_c();
      if (var6 == Blocks.field_150343_Z) {
         return false;
      } else if (!var1.func_175723_af().func_177746_a(var2)) {
         return false;
      } else if (var2.func_177956_o() < 0 || var3 == EnumFacing.DOWN && var2.func_177956_o() == 0) {
         return false;
      } else if (var2.func_177956_o() <= var1.func_72800_K() - 1 && (var3 != EnumFacing.UP || var2.func_177956_o() != var1.func_72800_K() - 1)) {
         if (var6 != Blocks.field_150331_J && var6 != Blocks.field_150320_F) {
            if (var0.func_185887_b(var1, var2) == -1.0F) {
               return false;
            }

            switch(var0.func_185905_o()) {
            case BLOCK:
               return false;
            case DESTROY:
               return var4;
            case PUSH_ONLY:
               return var3 == var5;
            }
         } else if ((Boolean)var0.func_177229_b(field_176320_b)) {
            return false;
         }

         return !var6.func_149716_u();
      } else {
         return false;
      }
   }

   private boolean func_176319_a(World var1, BlockPos var2, EnumFacing var3, boolean var4) {
      BlockPos var5 = var2.func_177972_a(var3);
      if (!var4 && var1.func_180495_p(var5).func_177230_c() == Blocks.field_150332_K) {
         var1.func_180501_a(var5, Blocks.field_150350_a.func_176223_P(), 20);
      }

      BlockPistonStructureHelper var6 = new BlockPistonStructureHelper(var1, var2, var3, var4);
      if (!var6.func_177253_a()) {
         return false;
      } else {
         List var7 = var6.func_177254_c();
         ArrayList var8 = Lists.newArrayList();

         for(int var9 = 0; var9 < var7.size(); ++var9) {
            BlockPos var10 = (BlockPos)var7.get(var9);
            var8.add(var1.func_180495_p(var10));
         }

         List var17 = var6.func_177252_d();
         int var18 = var7.size() + var17.size();
         IBlockState[] var11 = new IBlockState[var18];
         EnumFacing var12 = var4 ? var3 : var3.func_176734_d();
         HashSet var13 = Sets.newHashSet(var7);

         int var14;
         BlockPos var15;
         IBlockState var16;
         for(var14 = var17.size() - 1; var14 >= 0; --var14) {
            var15 = (BlockPos)var17.get(var14);
            var16 = var1.func_180495_p(var15);
            var16.func_196949_c(var1, var15, 0);
            var1.func_180501_a(var15, Blocks.field_150350_a.func_176223_P(), 18);
            --var18;
            var11[var18] = var16;
         }

         for(var14 = var7.size() - 1; var14 >= 0; --var14) {
            var15 = (BlockPos)var7.get(var14);
            var16 = var1.func_180495_p(var15);
            var15 = var15.func_177972_a(var12);
            var13.remove(var15);
            var1.func_180501_a(var15, (IBlockState)Blocks.field_196603_bb.func_176223_P().func_206870_a(field_176387_N, var3), 68);
            var1.func_175690_a(var15, BlockPistonMoving.func_196343_a((IBlockState)var8.get(var14), var3, var4, false));
            --var18;
            var11[var18] = var16;
         }

         IBlockState var21;
         if (var4) {
            PistonType var19 = this.field_150082_a ? PistonType.STICKY : PistonType.DEFAULT;
            var21 = (IBlockState)((IBlockState)Blocks.field_150332_K.func_176223_P().func_206870_a(BlockPistonExtension.field_176387_N, var3)).func_206870_a(BlockPistonExtension.field_176325_b, var19);
            var16 = (IBlockState)((IBlockState)Blocks.field_196603_bb.func_176223_P().func_206870_a(BlockPistonMoving.field_196344_a, var3)).func_206870_a(BlockPistonMoving.field_196345_b, this.field_150082_a ? PistonType.STICKY : PistonType.DEFAULT);
            var13.remove(var5);
            var1.func_180501_a(var5, var16, 68);
            var1.func_175690_a(var5, BlockPistonMoving.func_196343_a(var21, var3, true, true));
         }

         Iterator var20 = var13.iterator();

         while(var20.hasNext()) {
            var15 = (BlockPos)var20.next();
            var1.func_180501_a(var15, Blocks.field_150350_a.func_176223_P(), 66);
         }

         for(var14 = var17.size() - 1; var14 >= 0; --var14) {
            var21 = var11[var18++];
            BlockPos var22 = (BlockPos)var17.get(var14);
            var21.func_196948_b(var1, var22, 2);
            var1.func_195593_d(var22, var21.func_177230_c());
         }

         for(var14 = var7.size() - 1; var14 >= 0; --var14) {
            var1.func_195593_d((BlockPos)var7.get(var14), var11[var18++].func_177230_c());
         }

         if (var4) {
            var1.func_195593_d(var5, Blocks.field_150332_K);
         }

         return true;
      }
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176387_N, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176387_N)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_176387_N)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176387_N, field_176320_b);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return var2.func_177229_b(field_176387_N) != var4.func_176734_d() && (Boolean)var2.func_177229_b(field_176320_b) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
   }

   public int func_200011_d(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return 0;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176320_b = BlockStateProperties.field_208181_h;
      field_185648_b = Block.func_208617_a(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
      field_185649_c = Block.func_208617_a(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      field_185650_d = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
      field_185651_e = Block.func_208617_a(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D);
      field_185652_f = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
      field_185653_g = Block.func_208617_a(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   }
}
