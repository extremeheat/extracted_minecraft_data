package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class ItemSpade extends ItemTool {
   private static final Set<Block> field_150916_c;

   public ItemSpade(Item.ToolMaterial var1) {
      super(1.0F, var1, field_150916_c);
   }

   public boolean func_150897_b(Block var1) {
      if (var1 == Blocks.field_150431_aC) {
         return true;
      } else {
         return var1 == Blocks.field_150433_aE;
      }
   }

   static {
      field_150916_c = Sets.newHashSet(new Block[]{Blocks.field_150435_aG, Blocks.field_150346_d, Blocks.field_150458_ak, Blocks.field_150349_c, Blocks.field_150351_n, Blocks.field_150391_bh, Blocks.field_150354_m, Blocks.field_150433_aE, Blocks.field_150431_aC, Blocks.field_150425_aM});
   }
}
