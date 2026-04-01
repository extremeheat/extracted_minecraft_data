package net.minecraft.world.entity.livingblock.cognition;

public class AgencyProbe implements Agency {
   private int mutex;
   private int pursuedDesires;

   public AgencyProbe() {
      super();
   }

   public <V> Prize<V> inPursuitOf(final Desire<V> desire) {
      this.pursuedDesires |= 1 << desire.index();
      return new Prize.None<V>();
   }

   public <V> Intent<V> withIntentTo(final Action<V> action) {
      this.mutex |= 1 << action.index();
      return new Intent.None<V>();
   }

   public int getMutex() {
      return this.mutex;
   }

   public int getPursuedDesires() {
      return this.pursuedDesires;
   }
}
