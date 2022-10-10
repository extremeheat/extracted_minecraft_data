package net.minecraft.block;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockBed extends BlockHorizontal implements ITileEntityProvider {
   public static final EnumProperty<BedPart> field_176472_a;
   public static final BooleanProperty field_176471_b;
   protected static final VoxelShape field_196351_c;
   private final EnumDyeColor field_196352_y;

   public BlockBed(EnumDyeColor var1, Block.Properties var2) {
      super(var2);
      this.field_196352_y = var1;
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176472_a, BedPart.FOOT)).func_206870_a(field_176471_b, false));
   }

   public MaterialColor func_180659_g(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var1.func_177229_b(field_176472_a) == BedPart.FOOT ? this.field_196352_y.func_196055_e() : MaterialColor.field_151659_e;
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var2.field_72995_K) {
         return true;
      } else {
         if (var1.func_177229_b(field_176472_a) != BedPart.HEAD) {
            var3 = var3.func_177972_a((EnumFacing)var1.func_177229_b(field_185512_D));
            var1 = var2.func_180495_p(var3);
            if (var1.func_177230_c() != this) {
               return true;
            }
         }

         if (var2.field_73011_w.func_76567_e() && var2.func_180494_b(var3) != Biomes.field_76778_j) {
            if ((Boolean)var1.func_177229_b(field_176471_b)) {
               EntityPlayer var11 = this.func_176470_e(var2, var3);
               if (var11 != null) {
                  var4.func_146105_b(new TextComponentTranslation("block.minecraft.bed.occupied", new Object[0]), true);
                  return true;
               }

               var1 = (IBlockState)var1.func_206870_a(field_176471_b, false);
               var2.func_180501_a(var3, var1, 4);
            }

            EntityPlayer.SleepResult var12 = var4.func_180469_a(var3);
            if (var12 == EntityPlayer.SleepResult.OK) {
               var1 = (IBlockState)var1.func_206870_a(field_176471_b, true);
               var2.func_180501_a(var3, var1, 4);
               return true;
            } else {
               if (var12 == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
                  var4.func_146105_b(new TextComponentTranslation("block.minecraft.bed.no_sleep", new Object[0]), true);
               } else if (var12 == EntityPlayer.SleepResult.NOT_SAFE) {
                  var4.func_146105_b(new TextComponentTranslation("block.minecraft.bed.not_safe", new Object[0]), true);
               } else if (var12 == EntityPlayer.SleepResult.TOO_FAR_AWAY) {
                  var4.func_146105_b(new TextComponentTranslation("block.minecraft.bed.too_far_away", new Object[0]), true);
               }

               return true;
            }
         } else {
            var2.func_175698_g(var3);
            BlockPos var10 = var3.func_177972_a(((EnumFacing)var1.func_177229_b(field_185512_D)).func_176734_d());
            if (var2.func_180495_p(var10).func_177230_c() == this) {
               var2.func_175698_g(var10);
            }

            var2.func_211529_a((Entity)null, DamageSource.func_199683_a(), (double)var3.func_177958_n() + 0.5D, (double)var3.func_177956_o() + 0.5D, (double)var3.func_177952_p() + 0.5D, 5.0F, true, true);
            return true;
         }
      }
   }

   @Nullable
   private EntityPlayer func_176470_e(World var1, BlockPos var2) {
      Iterator var3 = var1.field_73010_i.iterator();

      EntityPlayer var4;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         var4 = (EntityPlayer)var3.next();
      } while(!var4.func_70608_bn() || !var4.field_71081_bT.equals(var2));

      return var4;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public void func_180658_a(World var1, BlockPos var2, Entity var3, float var4) {
      super.func_180658_a(var1, var2, var3, var4 * 0.5F);
   }

   public void func_176216_a(IBlockReader var1, Entity var2) {
      if (var2.func_70093_af()) {
         super.func_176216_a(var1, var2);
      } else if (var2.field_70181_x < 0.0D) {
         var2.field_70181_x = -var2.field_70181_x * 0.6600000262260437D;
         if (!(var2 instanceof EntityLivingBase)) {
            var2.field_70181_x *= 0.8D;
         }
      }

   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (var2 == func_208070_a((BedPart)var1.func_177229_b(field_176472_a), (EnumFacing)var1.func_177229_b(field_185512_D))) {
         return var3.func_177230_c() == this && var3.func_177229_b(field_176472_a) != var1.func_177229_b(field_176472_a) ? (IBlockState)var1.func_206870_a(field_176471_b, var3.func_177229_b(field_176471_b)) : Blocks.field_150350_a.func_176223_P();
      } else {
         return super.func_196271_a(var1, var2, var3, var4, var5, var6);
      }
   }

   private static EnumFacing func_208070_a(BedPart var0, EnumFacing var1) {
      return var0 == BedPart.FOOT ? var1 : var1.func_176734_d();
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      super.func_180657_a(var1, var2, var3, Blocks.field_150350_a.func_176223_P(), var5, var6);
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         super.func_196243_a(var1, var2, var3, var4, var5);
         var2.func_175713_t(var3);
      }
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      BedPart var5 = (BedPart)var3.func_177229_b(field_176472_a);
      boolean var6 = var5 == BedPart.HEAD;
      BlockPos var7 = var2.func_177972_a(func_208070_a(var5, (EnumFacing)var3.func_177229_b(field_185512_D)));
      IBlockState var8 = var1.func_180495_p(var7);
      if (var8.func_177230_c() == this && var8.func_177229_b(field_176472_a) != var5) {
         var1.func_180501_a(var7, Blocks.field_150350_a.func_176223_P(), 35);
         var1.func_180498_a(var4, 2001, var7, Block.func_196246_j(var8));
         if (!var1.field_72995_K && !var4.func_184812_l_()) {
            if (var6) {
               var3.func_196949_c(var1, var2, 0);
            } else {
               var8.func_196949_c(var1, var7, 0);
            }
         }

         var4.func_71029_a(StatList.field_188065_ae.func_199076_b(this));
      }

      super.func_176208_a(var1, var2, var3, var4);
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      EnumFacing var2 = var1.func_195992_f();
      BlockPos var3 = var1.func_195995_a();
      BlockPos var4 = var3.func_177972_a(var2);
      return var1.func_195991_k().func_180495_p(var4).func_196953_a(var1) ? (IBlockState)this.func_176223_P().func_206870_a(field_185512_D, var2) : null;
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return (IItemProvider)(var1.func_177229_b(field_176472_a) == BedPart.FOOT ? Items.field_190931_a : super.func_199769_a(var1, var2, var3, var4));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196351_c;
   }

   public boolean func_190946_v(IBlockState var1) {
      return true;
   }

   @Nullable
   public static BlockPos func_176468_a(IBlockReader var0, BlockPos var1, int var2) {
      EnumFacing var3 = (EnumFacing)var0.func_180495_p(var1).func_177229_b(field_185512_D);
      int var4 = var1.func_177958_n();
      int var5 = var1.func_177956_o();
      int var6 = var1.func_177952_p();

      for(int var7 = 0; var7 <= 1; ++var7) {
         int var8 = var4 - var3.func_82601_c() * var7 - 1;
         int var9 = var6 - var3.func_82599_e() * var7 - 1;
         int var10 = var8 + 2;
         int var11 = var9 + 2;

         for(int var12 = var8; var12 <= var10; ++var12) {
            for(int var13 = var9; var13 <= var11; ++var13) {
               BlockPos var14 = new BlockPos(var12, var5, var13);
               if (func_176469_d(var0, var14)) {
                  if (var2 <= 0) {
                     return var14;
                  }

                  --var2;
               }
            }
         }
      }

      return null;
   }

   protected static boolean func_176469_d(IBlockReader var0, BlockPos var1) {
      return var0.func_180495_p(var1.func_177977_b()).func_185896_q() && !var0.func_180495_p(var1).func_185904_a().func_76220_a() && !var0.func_180495_p(var1.func_177984_a()).func_185904_a().func_76220_a();
   }

   public EnumPushReaction func_149656_h(IBlockState var1) {
      return EnumPushReaction.DESTROY;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_185512_D, field_176472_a, field_176471_b);
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityBed(this.field_196352_y);
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, @Nullable EntityLivingBase var4, ItemStack var5) {
      super.func_180633_a(var1, var2, var3, var4, var5);
      if (!var1.field_72995_K) {
         BlockPos var6 = var2.func_177972_a((EnumFacing)var3.func_177229_b(field_185512_D));
         var1.func_180501_a(var6, (IBlockState)var3.func_206870_a(field_176472_a, BedPart.HEAD), 3);
         var1.func_195592_c(var2, Blocks.field_150350_a);
         var3.func_196946_a(var1, var2, 3);
      }

   }

   public EnumDyeColor func_196350_d() {
      return this.field_196352_y;
   }

   public long func_209900_a(IBlockState var1, BlockPos var2) {
      BlockPos var3 = var2.func_177967_a((EnumFacing)var1.func_177229_b(field_185512_D), var1.func_177229_b(field_176472_a) == BedPart.HEAD ? 0 : 1);
      return MathHelper.func_180187_c(var3.func_177958_n(), var2.func_177956_o(), var3.func_177952_p());
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176472_a = BlockStateProperties.field_208139_an;
      field_176471_b = BlockStateProperties.field_208192_s;
      field_196351_c = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
   }
}
