package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class KeyValueCondition implements Condition {
   private static final Splitter PIPE_SPLITTER = Splitter.on('|').omitEmptyStrings();
   private final String key;
   private final String value;

   public KeyValueCondition(String var1, String var2) {
      super();
      this.key = var1;
      this.value = var2;
   }

   public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> var1) {
      Property var2 = var1.getProperty(this.key);
      if (var2 == null) {
         throw new RuntimeException(String.format(Locale.ROOT, "Unknown property '%s' on '%s'", this.key, var1.getOwner()));
      } else {
         String var3 = this.value;
         boolean var4 = !var3.isEmpty() && var3.charAt(0) == '!';
         if (var4) {
            var3 = var3.substring(1);
         }

         List var5 = PIPE_SPLITTER.splitToList(var3);
         if (var5.isEmpty()) {
            throw new RuntimeException(String.format(Locale.ROOT, "Empty value '%s' for property '%s' on '%s'", this.value, this.key, var1.getOwner()));
         } else {
            Predicate var6;
            if (var5.size() == 1) {
               var6 = this.getBlockStatePredicate(var1, var2, var3);
            } else {
               List var7 = (List)var5.stream().map((var3x) -> {
                  return this.getBlockStatePredicate(var1, var2, var3x);
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

   private Predicate<BlockState> getBlockStatePredicate(StateDefinition<Block, BlockState> var1, Property<?> var2, String var3) {
      Optional var4 = var2.getValue(var3);
      if (var4.isEmpty()) {
         throw new RuntimeException(String.format(Locale.ROOT, "Unknown value '%s' for property '%s' on '%s' in '%s'", var3, this.key, var1.getOwner(), this.value));
      } else {
         return (var2x) -> {
            return var2x.getValue(var2).equals(var4.get());
         };
      }
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.value).toString();
   }
}
