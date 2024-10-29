package net.minecraft.world.entity.vehicle;

import java.util.function.Supplier;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ChestRaft extends AbstractChestBoat {
   public ChestRaft(EntityType<? extends ChestRaft> var1, Level var2, Supplier<Item> var3) {
      super(var1, var2, var3);
   }

   protected double rideHeight(EntityDimensions var1) {
      return (double)(var1.height() * 0.8888889F);
   }
}
