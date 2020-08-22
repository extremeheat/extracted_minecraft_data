package net.minecraft.world.entity.boss.enderdragon.phases;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public class EnderDragonPhase {
   private static EnderDragonPhase[] phases = new EnderDragonPhase[0];
   public static final EnderDragonPhase HOLDING_PATTERN = create(DragonHoldingPatternPhase.class, "HoldingPattern");
   public static final EnderDragonPhase STRAFE_PLAYER = create(DragonStrafePlayerPhase.class, "StrafePlayer");
   public static final EnderDragonPhase LANDING_APPROACH = create(DragonLandingApproachPhase.class, "LandingApproach");
   public static final EnderDragonPhase LANDING = create(DragonLandingPhase.class, "Landing");
   public static final EnderDragonPhase TAKEOFF = create(DragonTakeoffPhase.class, "Takeoff");
   public static final EnderDragonPhase SITTING_FLAMING = create(DragonSittingFlamingPhase.class, "SittingFlaming");
   public static final EnderDragonPhase SITTING_SCANNING = create(DragonSittingScanningPhase.class, "SittingScanning");
   public static final EnderDragonPhase SITTING_ATTACKING = create(DragonSittingAttackingPhase.class, "SittingAttacking");
   public static final EnderDragonPhase CHARGING_PLAYER = create(DragonChargePlayerPhase.class, "ChargingPlayer");
   public static final EnderDragonPhase DYING = create(DragonDeathPhase.class, "Dying");
   public static final EnderDragonPhase HOVERING = create(DragonHoverPhase.class, "Hover");
   private final Class instanceClass;
   private final int id;
   private final String name;

   private EnderDragonPhase(int var1, Class var2, String var3) {
      this.id = var1;
      this.instanceClass = var2;
      this.name = var3;
   }

   public DragonPhaseInstance createInstance(EnderDragon var1) {
      try {
         Constructor var2 = this.getConstructor();
         return (DragonPhaseInstance)var2.newInstance(var1);
      } catch (Exception var3) {
         throw new Error(var3);
      }
   }

   protected Constructor getConstructor() throws NoSuchMethodException {
      return this.instanceClass.getConstructor(EnderDragon.class);
   }

   public int getId() {
      return this.id;
   }

   public String toString() {
      return this.name + " (#" + this.id + ")";
   }

   public static EnderDragonPhase getById(int var0) {
      return var0 >= 0 && var0 < phases.length ? phases[var0] : HOLDING_PATTERN;
   }

   public static int getCount() {
      return phases.length;
   }

   private static EnderDragonPhase create(Class var0, String var1) {
      EnderDragonPhase var2 = new EnderDragonPhase(phases.length, var0, var1);
      phases = (EnderDragonPhase[])Arrays.copyOf(phases, phases.length + 1);
      phases[var2.getId()] = var2;
      return var2;
   }
}
