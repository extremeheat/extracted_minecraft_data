package net.minecraft.world.level.block.state.predicate;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockStatePredicate implements Predicate {
   public static final Predicate ANY = (var0) -> {
      return true;
   };
   private final StateDefinition definition;
   private final Map properties = Maps.newHashMap();

   private BlockStatePredicate(StateDefinition var1) {
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
            Iterator var2 = this.properties.entrySet().iterator();

            Entry var3;
            do {
               if (!var2.hasNext()) {
                  return true;
               }

               var3 = (Entry)var2.next();
            } while(this.applies(var1, (Property)var3.getKey(), (Predicate)var3.getValue()));

            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean applies(BlockState var1, Property var2, Predicate var3) {
      Comparable var4 = var1.getValue(var2);
      return var3.test(var4);
   }

   public BlockStatePredicate where(Property var1, Predicate var2) {
      if (!this.definition.getProperties().contains(var1)) {
         throw new IllegalArgumentException(this.definition + " cannot support property " + var1);
      } else {
         this.properties.put(var1, var2);
         return this;
      }
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1) {
      return this.test((BlockState)var1);
   }
}
