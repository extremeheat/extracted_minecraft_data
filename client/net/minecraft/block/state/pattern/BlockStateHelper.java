package net.minecraft.block.state.pattern;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;

public class BlockStateHelper implements Predicate<IBlockState> {
   private final BlockState field_177641_a;
   private final Map<IProperty, Predicate> field_177640_b = Maps.newHashMap();

   private BlockStateHelper(BlockState var1) {
      super();
      this.field_177641_a = var1;
   }

   public static BlockStateHelper func_177638_a(Block var0) {
      return new BlockStateHelper(var0.func_176194_O());
   }

   public boolean apply(IBlockState var1) {
      if (var1 != null && var1.func_177230_c().equals(this.field_177641_a.func_177622_c())) {
         Iterator var2 = this.field_177640_b.entrySet().iterator();

         Entry var3;
         Comparable var4;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (Entry)var2.next();
            var4 = var1.func_177229_b((IProperty)var3.getKey());
         } while(((Predicate)var3.getValue()).apply(var4));

         return false;
      } else {
         return false;
      }
   }

   public <V extends Comparable<V>> BlockStateHelper func_177637_a(IProperty<V> var1, Predicate<? extends V> var2) {
      if (!this.field_177641_a.func_177623_d().contains(var1)) {
         throw new IllegalArgumentException(this.field_177641_a + " cannot support property " + var1);
      } else {
         this.field_177640_b.put(var1, var2);
         return this;
      }
   }

   // $FF: synthetic method
   public boolean apply(Object var1) {
      return this.apply((IBlockState)var1);
   }
}
