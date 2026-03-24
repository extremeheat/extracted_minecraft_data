package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class DragonHoverPhase extends AbstractDragonPhaseInstance {
   private @Nullable Vec3 targetLocation;

   public DragonHoverPhase(final EnderDragon dragon) {
      super(dragon);
   }

   public void doServerTick(final ServerLevel level) {
      if (this.targetLocation == null) {
         this.targetLocation = this.dragon.position();
      }

   }

   public boolean isSitting() {
      return true;
   }

   public void begin() {
      this.targetLocation = null;
   }

   public float getFlySpeed() {
      return 1.0F;
   }

   public @Nullable Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   public EnderDragonPhase<DragonHoverPhase> getPhase() {
      return EnderDragonPhase.HOVERING;
   }
}
