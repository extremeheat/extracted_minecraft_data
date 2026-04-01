package net.minecraft.world.entity.livingblock.cognition;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Objects;
import net.minecraft.util.Unit;
import org.jspecify.annotations.Nullable;

public class HopesAndDreams {
   private final Int2ObjectMap<Object> desires = new Int2ObjectArrayMap();
   private int mask;

   public HopesAndDreams() {
      super();
   }

   public <V> void desire(final Desire<V> desire, final V value) {
      int i = desire.index();
      this.desires.put(i, Objects.requireNonNull(value, "value"));
      this.mask |= 1 << i;
   }

   public void desire(final Desire<Unit> desire) {
      this.desire(desire, Unit.INSTANCE);
   }

   public void forget(final Desire<?> desire) {
      int i = desire.index();
      this.desires.remove(i);
      this.mask &= ~(1 << i);
   }

   public boolean hasDesire(final Desire<?> desire) {
      return this.desires.containsKey(desire.index());
   }

   public boolean hasDesires(final int desiresMask) {
      return (this.mask & desiresMask) == desiresMask;
   }

   public <V> @Nullable V getDesire(final Desire<V> desire) {
      return (V)this.desires.get(desire.index());
   }

   public void stopDesiringAnything() {
      this.desires.clear();
      this.mask = 0;
   }
}
