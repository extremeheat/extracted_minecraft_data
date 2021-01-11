package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSkull extends BlockContainer {
   public static final PropertyDirection field_176418_a = PropertyDirection.func_177714_a("facing");
   public static final PropertyBool field_176417_b = PropertyBool.func_177716_a("nodrop");
   private static final Predicate<BlockWorldState> field_176419_M = new Predicate<BlockWorldState>() {
      public boolean apply(BlockWorldState var1) {
         return var1.func_177509_a() != null && var1.func_177509_a().func_177230_c() == Blocks.field_150465_bP && var1.func_177507_b() instanceof TileEntitySkull && ((TileEntitySkull)var1.func_177507_b()).func_145904_a() == 1;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((BlockWorldState)var1);
      }
   };
   private BlockPattern field_176420_N;
   private BlockPattern field_176421_O;

   protected BlockSkull() {
      super(Material.field_151594_q);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176418_a, EnumFacing.NORTH).func_177226_a(field_176417_b, false));
      this.func_149676_a(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a("tile.skull.skeleton.name");
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      switch((EnumFacing)var1.func_180495_p(var2).func_177229_b(field_176418_a)) {
      case UP:
      default:
         this.func_149676_a(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
         break;
      case NORTH:
         this.func_149676_a(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
         break;
      case SOUTH:
         this.func_149676_a(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
         break;
      case WEST:
         this.func_149676_a(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
         break;
      case EAST:
         this.func_149676_a(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
      }

   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      this.func_180654_a(var1, var2);
      return super.func_180640_a(var1, var2, var3);
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P().func_177226_a(field_176418_a, var8.func_174811_aO()).func_177226_a(field_176417_b, false);
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntitySkull();
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151144_bL;
   }

   public int func_176222_j(World var1, BlockPos var2) {
      TileEntity var3 = var1.func_175625_s(var2);
      return var3 instanceof TileEntitySkull ? ((TileEntitySkull)var3).func_145904_a() : super.func_176222_j(var1, var2);
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (var4.field_71075_bZ.field_75098_d) {
         var3 = var3.func_177226_a(field_176417_b, true);
         var1.func_180501_a(var2, var3, 4);
      }

      super.func_176208_a(var1, var2, var3, var4);
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.field_72995_K) {
         if (!(Boolean)var3.func_177229_b(field_176417_b)) {
            TileEntity var4 = var1.func_175625_s(var2);
            if (var4 instanceof TileEntitySkull) {
               TileEntitySkull var5 = (TileEntitySkull)var4;
               ItemStack var6 = new ItemStack(Items.field_151144_bL, 1, this.func_176222_j(var1, var2));
               if (var5.func_145904_a() == 3 && var5.func_152108_a() != null) {
                  var6.func_77982_d(new NBTTagCompound());
                  NBTTagCompound var7 = new NBTTagCompound();
                  NBTUtil.func_180708_a(var7, var5.func_152108_a());
                  var6.func_77978_p().func_74782_a("SkullOwner", var7);
               }

               func_180635_a(var1, var2, var6);
            }
         }

         super.func_180663_b(var1, var2, var3);
      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151144_bL;
   }

   public boolean func_176415_b(World var1, BlockPos var2, ItemStack var3) {
      if (var3.func_77960_j() == 1 && var2.func_177956_o() >= 2 && var1.func_175659_aa() != EnumDifficulty.PEACEFUL && !var1.field_72995_K) {
         return this.func_176414_j().func_177681_a(var1, var2) != null;
      } else {
         return false;
      }
   }

   public void func_180679_a(World var1, BlockPos var2, TileEntitySkull var3) {
      if (var3.func_145904_a() == 1 && var2.func_177956_o() >= 2 && var1.func_175659_aa() != EnumDifficulty.PEACEFUL && !var1.field_72995_K) {
         BlockPattern var4 = this.func_176416_l();
         BlockPattern.PatternHelper var5 = var4.func_177681_a(var1, var2);
         if (var5 != null) {
            int var6;
            for(var6 = 0; var6 < 3; ++var6) {
               BlockWorldState var7 = var5.func_177670_a(var6, 0, 0);
               var1.func_180501_a(var7.func_177508_d(), var7.func_177509_a().func_177226_a(field_176417_b, true), 2);
            }

            for(var6 = 0; var6 < var4.func_177684_c(); ++var6) {
               for(int var13 = 0; var13 < var4.func_177685_b(); ++var13) {
                  BlockWorldState var8 = var5.func_177670_a(var6, var13, 0);
                  var1.func_180501_a(var8.func_177508_d(), Blocks.field_150350_a.func_176223_P(), 2);
               }
            }

            BlockPos var12 = var5.func_177670_a(1, 0, 0).func_177508_d();
            EntityWither var14 = new EntityWither(var1);
            BlockPos var15 = var5.func_177670_a(1, 2, 0).func_177508_d();
            var14.func_70012_b((double)var15.func_177958_n() + 0.5D, (double)var15.func_177956_o() + 0.55D, (double)var15.func_177952_p() + 0.5D, var5.func_177669_b().func_176740_k() == EnumFacing.Axis.X ? 0.0F : 90.0F, 0.0F);
            var14.field_70761_aq = var5.func_177669_b().func_176740_k() == EnumFacing.Axis.X ? 0.0F : 90.0F;
            var14.func_82206_m();
            Iterator var9 = var1.func_72872_a(EntityPlayer.class, var14.func_174813_aQ().func_72314_b(50.0D, 50.0D, 50.0D)).iterator();

            while(var9.hasNext()) {
               EntityPlayer var10 = (EntityPlayer)var9.next();
               var10.func_71029_a(AchievementList.field_150963_I);
            }

            var1.func_72838_d(var14);

            int var16;
            for(var16 = 0; var16 < 120; ++var16) {
               var1.func_175688_a(EnumParticleTypes.SNOWBALL, (double)var12.func_177958_n() + var1.field_73012_v.nextDouble(), (double)(var12.func_177956_o() - 2) + var1.field_73012_v.nextDouble() * 3.9D, (double)var12.func_177952_p() + var1.field_73012_v.nextDouble(), 0.0D, 0.0D, 0.0D);
            }

            for(var16 = 0; var16 < var4.func_177684_c(); ++var16) {
               for(int var17 = 0; var17 < var4.func_177685_b(); ++var17) {
                  BlockWorldState var11 = var5.func_177670_a(var16, var17, 0);
                  var1.func_175722_b(var11.func_177508_d(), Blocks.field_150350_a);
               }
            }

         }
      }
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176418_a, EnumFacing.func_82600_a(var1 & 7)).func_177226_a(field_176417_b, (var1 & 8) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176418_a)).func_176745_a();
      if ((Boolean)var1.func_177229_b(field_176417_b)) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176418_a, field_176417_b});
   }

   protected BlockPattern func_176414_j() {
      if (this.field_176420_N == null) {
         this.field_176420_N = FactoryBlockPattern.func_177660_a().func_177659_a("   ", "###", "~#~").func_177662_a('#', BlockWorldState.func_177510_a(BlockStateHelper.func_177638_a(Blocks.field_150425_aM))).func_177662_a('~', BlockWorldState.func_177510_a(BlockStateHelper.func_177638_a(Blocks.field_150350_a))).func_177661_b();
      }

      return this.field_176420_N;
   }

   protected BlockPattern func_176416_l() {
      if (this.field_176421_O == null) {
         this.field_176421_O = FactoryBlockPattern.func_177660_a().func_177659_a("^^^", "###", "~#~").func_177662_a('#', BlockWorldState.func_177510_a(BlockStateHelper.func_177638_a(Blocks.field_150425_aM))).func_177662_a('^', field_176419_M).func_177662_a('~', BlockWorldState.func_177510_a(BlockStateHelper.func_177638_a(Blocks.field_150350_a))).func_177661_b();
      }

      return this.field_176421_O;
   }
}
