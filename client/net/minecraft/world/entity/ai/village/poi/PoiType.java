package net.minecraft.world.entity.ai.village.poi;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;

public record PoiType(Set<BlockState> matchingStates, int maxTickets, int validRange) {
   public static final Predicate<Holder<PoiType>> NONE = (var0) -> {
      return false;
   };

   public PoiType(Set<BlockState> matchingStates, int maxTickets, int validRange) {
      super();
      matchingStates = Set.copyOf(matchingStates);
      this.matchingStates = matchingStates;
      this.maxTickets = maxTickets;
      this.validRange = validRange;
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
