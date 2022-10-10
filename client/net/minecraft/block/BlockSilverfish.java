package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSilverfish extends Block {
   private final Block field_196469_a;
   private static final Map<Block, Block> field_196470_b = Maps.newIdentityHashMap();

   public BlockSilverfish(Block var1, Block.Properties var2) {
      super(var2);
      this.field_196469_a = var1;
      field_196470_b.put(var1, this);
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 0;
   }

   public Block func_196468_d() {
      return this.field_196469_a;
   }

   public static boolean func_196466_i(IBlockState var0) {
      return field_196470_b.containsKey(var0.func_177230_c());
   }

   protected ItemStack func_180643_i(IBlockState var1) {
      return new ItemStack(this.field_196469_a);
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      if (!var2.field_72995_K && var2.func_82736_K().func_82766_b("doTileDrops")) {
         EntitySilverfish var6 = new EntitySilverfish(var2);
         var6.func_70012_b((double)var3.func_177958_n() + 0.5D, (double)var3.func_177956_o(), (double)var3.func_177952_p() + 0.5D, 0.0F, 0.0F);
         var2.func_72838_d(var6);
         var6.func_70656_aK();
      }

   }

   public static IBlockState func_196467_h(Block var0) {
      return ((Block)field_196470_b.get(var0)).func_176223_P();
   }
}
