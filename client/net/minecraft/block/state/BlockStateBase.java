package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;

public abstract class BlockStateBase implements IBlockState {
   private static final Joiner field_177234_a = Joiner.on(',');
   private static final Function<Entry<IProperty, Comparable>, String> field_177233_b = new Function<Entry<IProperty, Comparable>, String>() {
      public String apply(Entry<IProperty, Comparable> var1) {
         if (var1 == null) {
            return "<NULL>";
         } else {
            IProperty var2 = (IProperty)var1.getKey();
            return var2.func_177701_a() + "=" + var2.func_177702_a((Comparable)var1.getValue());
         }
      }

      // $FF: synthetic method
      public Object apply(Object var1) {
         return this.apply((Entry)var1);
      }
   };

   public BlockStateBase() {
      super();
   }

   public <T extends Comparable<T>> IBlockState func_177231_a(IProperty<T> var1) {
      return this.func_177226_a(var1, (Comparable)func_177232_a(var1.func_177700_c(), this.func_177229_b(var1)));
   }

   protected static <T> T func_177232_a(Collection<T> var0, T var1) {
      Iterator var2 = var0.iterator();

      do {
         if (!var2.hasNext()) {
            return var2.next();
         }
      } while(!var2.next().equals(var1));

      if (var2.hasNext()) {
         return var2.next();
      } else {
         return var0.iterator().next();
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(Block.field_149771_c.func_177774_c(this.func_177230_c()));
      if (!this.func_177228_b().isEmpty()) {
         var1.append("[");
         field_177234_a.appendTo(var1, Iterables.transform(this.func_177228_b().entrySet(), field_177233_b));
         var1.append("]");
      }

      return var1.toString();
   }
}
