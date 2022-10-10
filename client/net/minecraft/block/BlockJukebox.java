package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityJukebox;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer {
   public static final BooleanProperty field_176432_a;

   protected BlockJukebox(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176432_a, false));
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if ((Boolean)var1.func_177229_b(field_176432_a)) {
         this.func_203419_a(var2, var3);
         var1 = (IBlockState)var1.func_206870_a(field_176432_a, false);
         var2.func_180501_a(var3, var1, 2);
         return true;
      } else {
         return false;
      }
   }

   public void func_176431_a(IWorld var1, BlockPos var2, IBlockState var3, ItemStack var4) {
      TileEntity var5 = var1.func_175625_s(var2);
      if (var5 instanceof TileEntityJukebox) {
         ((TileEntityJukebox)var5).func_195535_a(var4.func_77946_l());
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_176432_a, true), 2);
      }
   }

   private void func_203419_a(World var1, BlockPos var2) {
      if (!var1.field_72995_K) {
         TileEntity var3 = var1.func_175625_s(var2);
         if (var3 instanceof TileEntityJukebox) {
            TileEntityJukebox var4 = (TileEntityJukebox)var3;
            ItemStack var5 = var4.func_195537_c();
            if (!var5.func_190926_b()) {
               var1.func_175718_b(1010, var2, 0);
               var1.func_184149_a(var2, (SoundEvent)null);
               var4.func_195535_a(ItemStack.field_190927_a);
               float var6 = 0.7F;
               double var7 = (double)(var1.field_73012_v.nextFloat() * 0.7F) + 0.15000000596046448D;
               double var9 = (double)(var1.field_73012_v.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
               double var11 = (double)(var1.field_73012_v.nextFloat() * 0.7F) + 0.15000000596046448D;
               ItemStack var13 = var5.func_77946_l();
               EntityItem var14 = new EntityItem(var1, (double)var2.func_177958_n() + var7, (double)var2.func_177956_o() + var9, (double)var2.func_177952_p() + var11, var13);
               var14.func_174869_p();
               var1.func_72838_d(var14);
            }
         }
      }
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         this.func_203419_a(var2, var3);
         super.func_196243_a(var1, var2, var3, var4, var5);
      }
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      if (!var2.field_72995_K) {
         super.func_196255_a(var1, var2, var3, var4, 0);
      }
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityJukebox();
   }

   public boolean func_149740_M(IBlockState var1) {
      return true;
   }

   public int func_180641_l(IBlockState var1, World var2, BlockPos var3) {
      TileEntity var4 = var2.func_175625_s(var3);
      if (var4 instanceof TileEntityJukebox) {
         Item var5 = ((TileEntityJukebox)var4).func_195537_c().func_77973_b();
         if (var5 instanceof ItemRecord) {
            return ((ItemRecord)var5).func_195975_g();
         }
      }

      return 0;
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.MODEL;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176432_a);
   }

   static {
      field_176432_a = BlockStateProperties.field_208187_n;
   }
}
