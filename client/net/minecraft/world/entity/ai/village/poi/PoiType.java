package net.minecraft.world.entity.ai.village.poi;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;

public record PoiType(Set<BlockState> b, int c, int d) {
   private final Set<BlockState> matchingStates;
   private final int maxTickets;
   private final int validRange;
   public static final Predicate<Holder<PoiType>> NONE = (var0) -> {
      return false;
   };

   public PoiType(Set<BlockState> var1, int var2, int var3) {
      super();
      var1 = Set.copyOf(var1);
      this.matchingStates = var1;
      this.maxTickets = var2;
      this.validRange = var3;
   }

   public boolean is(BlockState var1) {
      return this.matchingStates.contains(var1);
   }

   public Set<BlockState> matchingStates() {
      return this.matchingStates;
   }

   public int maxTickets() {
      return this.maxTickets;
   }

   public int validRange() {
      return this.validRange;
   }
}
