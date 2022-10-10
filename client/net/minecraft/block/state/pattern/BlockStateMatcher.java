package net.minecraft.block.state.pattern;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;

public class BlockStateMatcher implements Predicate<IBlockState> {
   public static final Predicate<IBlockState> field_185928_a = (var0) -> {
      return true;
   };
   private final StateContainer<Block, IBlockState> field_177641_a;
   private final Map<IProperty<?>, Predicate<Object>> field_177640_b = Maps.newHashMap();

   private BlockStateMatcher(StateContainer<Block, IBlockState> var1) {
      super();
      this.field_177641_a = var1;
   }

   public static BlockStateMatcher func_177638_a(Block var0) {
      return new BlockStateMatcher(var0.func_176194_O());
   }

   public boolean test(@Nullable IBlockState var1) {
      if (var1 != null && var1.func_177230_c().equals(this.field_177641_a.func_177622_c())) {
         if (this.field_177640_b.isEmpty()) {
            return true;
         } else {
            Iterator var2 = this.field_177640_b.entrySet().iterator();

            Entry var3;
            do {
               if (!var2.hasNext()) {
                  return true;
               }

               var3 = (Entry)var2.next();
            } while(this.func_185927_a(var1, (IProperty)var3.getKey(), (Predicate)var3.getValue()));

            return false;
         }
      } else {
         return false;
      }
   }

   protected <T extends Comparable<T>> boolean func_185927_a(IBlockState var1, IProperty<T> var2, Predicate<Object> var3) {
      Comparable var4 = var1.func_177229_b(var2);
      return var3.test(var4);
   }

   public <V extends Comparable<V>> BlockStateMatcher func_201028_a(IProperty<V> var1, Predicate<Object> var2) {
      if (!this.field_177641_a.func_177623_d().contains(var1)) {
         throw new IllegalArgumentException(this.field_177641_a + " cannot support property " + var1);
      } else {
         this.field_177640_b.put(var1, var2);
         return this;
      }
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1) {
      return this.test((IBlockState)var1);
   }
}
