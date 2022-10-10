package net.minecraft.block;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMaterialMatcher;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class BlockSkullWither extends BlockSkull {
   private static BlockPattern field_196300_c;
   private static BlockPattern field_196301_y;

   protected BlockSkullWither(Block.Properties var1) {
      super(BlockSkull.Types.WITHER_SKELETON, var1);
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, @Nullable EntityLivingBase var4, ItemStack var5) {
      super.func_180633_a(var1, var2, var3, var4, var5);
      TileEntity var6 = var1.func_175625_s(var2);
      if (var6 instanceof TileEntitySkull) {
         func_196298_a(var1, var2, (TileEntitySkull)var6);
      }

   }

   public static void func_196298_a(World var0, BlockPos var1, TileEntitySkull var2) {
      Block var3 = var2.func_195044_w().func_177230_c();
      boolean var4 = var3 == Blocks.field_196705_eO || var3 == Blocks.field_196704_eN;
      if (var4 && var1.func_177956_o() >= 2 && var0.func_175659_aa() != EnumDifficulty.PEACEFUL && !var0.field_72995_K) {
         BlockPattern var5 = func_196296_d();
         BlockPattern.PatternHelper var6 = var5.func_177681_a(var0, var1);
         if (var6 != null) {
            int var7;
            for(var7 = 0; var7 < 3; ++var7) {
               TileEntitySkull.func_195486_a(var0, var6.func_177670_a(var7, 0, 0).func_177508_d());
            }

            for(var7 = 0; var7 < var5.func_177684_c(); ++var7) {
               for(int var8 = 0; var8 < var5.func_177685_b(); ++var8) {
                  var0.func_180501_a(var6.func_177670_a(var7, var8, 0).func_177508_d(), Blocks.field_150350_a.func_176223_P(), 2);
               }
            }

            BlockPos var12 = var6.func_177670_a(1, 0, 0).func_177508_d();
            EntityWither var13 = new EntityWither(var0);
            BlockPos var9 = var6.func_177670_a(1, 2, 0).func_177508_d();
            var13.func_70012_b((double)var9.func_177958_n() + 0.5D, (double)var9.func_177956_o() + 0.55D, (double)var9.func_177952_p() + 0.5D, var6.func_177669_b().func_176740_k() == EnumFacing.Axis.X ? 0.0F : 90.0F, 0.0F);
            var13.field_70761_aq = var6.func_177669_b().func_176740_k() == EnumFacing.Axis.X ? 0.0F : 90.0F;
            var13.func_82206_m();
            Iterator var10 = var0.func_72872_a(EntityPlayerMP.class, var13.func_174813_aQ().func_186662_g(50.0D)).iterator();

            while(var10.hasNext()) {
               EntityPlayerMP var11 = (EntityPlayerMP)var10.next();
               CriteriaTriggers.field_192133_m.func_192229_a(var11, var13);
            }

            var0.func_72838_d(var13);

            int var14;
            for(var14 = 0; var14 < 120; ++var14) {
               var0.func_195594_a(Particles.field_197593_D, (double)var12.func_177958_n() + var0.field_73012_v.nextDouble(), (double)(var12.func_177956_o() - 2) + var0.field_73012_v.nextDouble() * 3.9D, (double)var12.func_177952_p() + var0.field_73012_v.nextDouble(), 0.0D, 0.0D, 0.0D);
            }

            for(var14 = 0; var14 < var5.func_177684_c(); ++var14) {
               for(int var15 = 0; var15 < var5.func_177685_b(); ++var15) {
                  var0.func_195592_c(var6.func_177670_a(var14, var15, 0).func_177508_d(), Blocks.field_150350_a);
               }
            }

         }
      }
   }

   public static boolean func_196299_b(World var0, BlockPos var1, ItemStack var2) {
      if (var2.func_77973_b() == Items.field_196183_dw && var1.func_177956_o() >= 2 && var0.func_175659_aa() != EnumDifficulty.PEACEFUL && !var0.field_72995_K) {
         return func_196297_e().func_177681_a(var0, var1) != null;
      } else {
         return false;
      }
   }

   protected static BlockPattern func_196296_d() {
      if (field_196300_c == null) {
         field_196300_c = FactoryBlockPattern.func_177660_a().func_177659_a("^^^", "###", "~#~").func_177662_a('#', BlockWorldState.func_177510_a(BlockStateMatcher.func_177638_a(Blocks.field_150425_aM))).func_177662_a('^', BlockWorldState.func_177510_a(BlockStateMatcher.func_177638_a(Blocks.field_196705_eO).or(BlockStateMatcher.func_177638_a(Blocks.field_196704_eN)))).func_177662_a('~', BlockWorldState.func_177510_a(BlockMaterialMatcher.func_189886_a(Material.field_151579_a))).func_177661_b();
      }

      return field_196300_c;
   }

   protected static BlockPattern func_196297_e() {
      if (field_196301_y == null) {
         field_196301_y = FactoryBlockPattern.func_177660_a().func_177659_a("   ", "###", "~#~").func_177662_a('#', BlockWorldState.func_177510_a(BlockStateMatcher.func_177638_a(Blocks.field_150425_aM))).func_177662_a('~', BlockWorldState.func_177510_a(BlockMaterialMatcher.func_189886_a(Material.field_151579_a))).func_177661_b();
      }

      return field_196301_y;
   }
}
