package net.minecraft.world.entity.ai.sensing;

import java.util.Random;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public abstract class Sensor {
   private static final Random RANDOM = new Random();
   private final int scanRate;
   private long timeToTick;

   public Sensor(int var1) {
      this.scanRate = var1;
      this.timeToTick = (long)RANDOM.nextInt(var1);
   }

   public Sensor() {
      this(20);
   }

   public final void tick(ServerLevel var1, LivingEntity var2) {
      if (--this.timeToTick <= 0L) {
         this.timeToTick = (long)this.scanRate;
         this.doTick(var1, var2);
      }

   }

   protected abstract void doTick(ServerLevel var1, LivingEntity var2);

   public abstract Set requires();
}
