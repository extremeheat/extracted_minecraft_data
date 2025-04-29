package net.minecraft.world.entity.animal;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;

public abstract class ShoulderRidingEntity extends TamableAnimal {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int RIDE_COOLDOWN = 100;
   private int rideCooldownCounter;

   protected ShoulderRidingEntity(EntityType<? extends ShoulderRidingEntity> var1, Level var2) {
      super(var1, var2);
   }

   public boolean setEntityOnShoulder(ServerPlayer var1) {
      try (ProblemReporter.ScopedCollector var2 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
         TagValueOutput var3 = TagValueOutput.createWithContext(var2, this.registryAccess());
         this.saveWithoutId(var3);
         var3.putString("id", this.getEncodeId());
         if (var1.setEntityOnShoulder(var3.buildResult())) {
            this.discard();
            return true;
         }
      }

      return false;
   }

   public void tick() {
      ++this.rideCooldownCounter;
      super.tick();
   }

   public boolean canSitOnShoulder() {
      return this.rideCooldownCounter > 100;
   }
}
