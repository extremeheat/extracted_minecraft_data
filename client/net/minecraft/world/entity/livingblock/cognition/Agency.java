package net.minecraft.world.entity.livingblock.cognition;

public interface Agency {
   <V> Prize<V> inPursuitOf(Desire<V> desire);

   <V> Intent<V> withIntentTo(Action<V> action);
}
