package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockContainer extends Block implements ITileEntityProvider {
   private static final Logger field_196284_a = LogManager.getLogger();

   protected BlockContainer(Block.Properties var1) {
      super(var1);
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         super.func_196243_a(var1, var2, var3, var4, var5);
         var2.func_175713_t(var3);
      }
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      if (var5 instanceof INameable && ((INameable)var5).func_145818_k_()) {
         var2.func_71029_a(StatList.field_188065_ae.func_199076_b(this));
         var2.func_71020_j(0.005F);
         if (var1.field_72995_K) {
            field_196284_a.debug("Never going to hit this!");
            return;
         }

         int var7 = EnchantmentHelper.func_77506_a(Enchantments.field_185308_t, var6);
         Item var8 = this.func_199769_a(var4, var1, var3, var7).func_199767_j();
         if (var8 == Items.field_190931_a) {
            return;
         }

         ItemStack var9 = new ItemStack(var8, this.func_196264_a(var4, var1.field_73012_v));
         var9.func_200302_a(((INameable)var5).func_200201_e());
         func_180635_a(var1, var3, var9);
      } else {
         super.func_180657_a(var1, var2, var3, var4, (TileEntity)null, var6);
      }

   }

   public boolean func_189539_a(IBlockState var1, World var2, BlockPos var3, int var4, int var5) {
      super.func_189539_a(var1, var2, var3, var4, var5);
      TileEntity var6 = var2.func_175625_s(var3);
      return var6 == null ? false : var6.func_145842_c(var4, var5);
   }
}
