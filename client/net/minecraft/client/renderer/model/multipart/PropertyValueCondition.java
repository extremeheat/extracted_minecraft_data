package net.minecraft.client.renderer.model.multipart;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;

public class PropertyValueCondition implements ICondition {
   private static final Splitter field_188124_c = Splitter.on('|').omitEmptyStrings();
   private final String field_188125_d;
   private final String field_188126_e;

   public PropertyValueCondition(String var1, String var2) {
      super();
      this.field_188125_d = var1;
      this.field_188126_e = var2;
   }

   public Predicate<IBlockState> getPredicate(StateContainer<Block, IBlockState> var1) {
      IProperty var2 = var1.func_185920_a(this.field_188125_d);
      if (var2 == null) {
         throw new RuntimeException(String.format("Unknown property '%s' on '%s'", this.field_188125_d, ((Block)var1.func_177622_c()).toString()));
      } else {
         String var3 = this.field_188126_e;
         boolean var4 = !var3.isEmpty() && var3.charAt(0) == '!';
         if (var4) {
            var3 = var3.substring(1);
         }

         List var5 = field_188124_c.splitToList(var3);
         if (var5.isEmpty()) {
            throw new RuntimeException(String.format("Empty value '%s' for property '%s' on '%s'", this.field_188126_e, this.field_188125_d, ((Block)var1.func_177622_c()).toString()));
         } else {
            Predicate var6;
            if (var5.size() == 1) {
               var6 = this.func_212485_a(var1, var2, var3);
            } else {
               List var7 = (List)var5.stream().map((var3x) -> {
                  return this.func_212485_a(var1, var2, var3x);
               }).collect(Collectors.toList());
               var6 = (var1x) -> {
                  return var7.stream().anyMatch((var1) -> {
                     return var1.test(var1x);
                  });
               };
            }

            return var4 ? var6.negate() : var6;
         }
      }
   }

   private Predicate<IBlockState> func_212485_a(StateContainer<Block, IBlockState> var1, IProperty<?> var2, String var3) {
      Optional var4 = var2.func_185929_b(var3);
      if (!var4.isPresent()) {
         throw new RuntimeException(String.format("Unknown value '%s' for property '%s' on '%s' in '%s'", var3, this.field_188125_d, ((Block)var1.func_177622_c()).toString(), this.field_188126_e));
      } else {
         return (var2x) -> {
            return var2x.func_177229_b(var2).equals(var4.get());
         };
      }
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("key", this.field_188125_d).add("value", this.field_188126_e).toString();
   }
}
