package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSpade extends ItemTool {
   private static final Set<Block> field_150916_c;
   protected static final Map<Block, IBlockState> field_195955_e;

   public ItemSpade(IItemTier var1, float var2, float var3, Item.Properties var4) {
      super(var2, var3, var1, field_150916_c, var4);
   }

   public boolean func_150897_b(IBlockState var1) {
      Block var2 = var1.func_177230_c();
      return var2 == Blocks.field_150433_aE || var2 == Blocks.field_196604_cC;
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      if (var1.func_196000_l() != EnumFacing.DOWN && var2.func_180495_p(var3.func_177984_a()).func_196958_f()) {
         IBlockState var4 = (IBlockState)field_195955_e.get(var2.func_180495_p(var3).func_177230_c());
         if (var4 != null) {
            EntityPlayer var5 = var1.func_195999_j();
            var2.func_184133_a(var5, var3, SoundEvents.field_187771_eN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!var2.field_72995_K) {
               var2.func_180501_a(var3, var4, 11);
               if (var5 != null) {
                  var1.func_195996_i().func_77972_a(1, var5);
               }
            }

            return EnumActionResult.SUCCESS;
         }
      }

      return EnumActionResult.PASS;
   }

   static {
      field_150916_c = Sets.newHashSet(new Block[]{Blocks.field_150435_aG, Blocks.field_150346_d, Blocks.field_196660_k, Blocks.field_196661_l, Blocks.field_150458_ak, Blocks.field_196658_i, Blocks.field_150351_n, Blocks.field_150391_bh, Blocks.field_150354_m, Blocks.field_196611_F, Blocks.field_196604_cC, Blocks.field_150433_aE, Blocks.field_150425_aM, Blocks.field_185774_da, Blocks.field_196860_iS, Blocks.field_196862_iT, Blocks.field_196864_iU, Blocks.field_196866_iV, Blocks.field_196868_iW, Blocks.field_196870_iX, Blocks.field_196872_iY, Blocks.field_196874_iZ, Blocks.field_196877_ja, Blocks.field_196878_jb, Blocks.field_196879_jc, Blocks.field_196880_jd, Blocks.field_196881_je, Blocks.field_196882_jf, Blocks.field_196883_jg, Blocks.field_196884_jh});
      field_195955_e = Maps.newHashMap(ImmutableMap.of(Blocks.field_196658_i, Blocks.field_185774_da.func_176223_P()));
   }
}
