package net.minecraft.world.level.block.state.predicate;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockStatePredicate implements Predicate<BlockState> {
   public static final Predicate<BlockState> ANY = var0 -> true;
   private final StateDefinition<Block, BlockState> definition;
   private final Map<Property<?>, Predicate<Object>> properties = Maps.newHashMap();

   private BlockStatePredicate(StateDefinition<Block, BlockState> var1) {
      super();
      this.definition = var1;
   }

   public static BlockStatePredicate forBlock(Block var0) {
      return new BlockStatePredicate(var0.getStateDefinition());
   }

   public boolean test(@Nullable BlockState var1) {
      if (var1 != null && var1.getBlock().equals(this.definition.getOwner())) {
         if (this.properties.isEmpty()) {
            return true;
         } else {
            for (Entry var3 : this.properties.entrySet()) {
               if (!this.applies(var1, (Property)var3.getKey(), (Predicate<Object>)var3.getValue())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   protected <T extends Comparable<T>> boolean applies(BlockState var1, Property<T> var2, Predicate<Object> var3) {
      Comparable var4 = var1.getValue(var2);
      return var3.test(var4);
   }

   public <V extends Comparable<V>> BlockStatePredicate where(Property<V> var1, Predicate<Object> var2) {
      if (!this.definition.getProperties().contains(var1)) {
         throw new IllegalArgumentException(this.definition + " cannot support property " + var1);
      } else {
         this.properties.put(var1, var2);
         return this;
      }
   }
}
