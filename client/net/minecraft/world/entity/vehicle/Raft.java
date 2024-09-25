package net.minecraft.world.entity.vehicle;

import java.util.function.Supplier;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class Raft extends AbstractBoat {
   public Raft(EntityType<? extends Raft> var1, Level var2, Supplier<Item> var3) {
      super(var1, var2, var3);
   }

   @Override
   protected double rideHeight(EntityDimensions var1) {
      return (double)(var1.height() * 0.8888889F);
   }
}
