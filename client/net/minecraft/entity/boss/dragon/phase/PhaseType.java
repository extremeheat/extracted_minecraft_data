package net.minecraft.entity.boss.dragon.phase;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import net.minecraft.entity.boss.EntityDragon;

public class PhaseType<T extends IPhase> {
   private static PhaseType<?>[] field_188752_l = new PhaseType[0];
   public static final PhaseType<PhaseHoldingPattern> field_188741_a = func_188735_a(PhaseHoldingPattern.class, "HoldingPattern");
   public static final PhaseType<PhaseStrafePlayer> field_188742_b = func_188735_a(PhaseStrafePlayer.class, "StrafePlayer");
   public static final PhaseType<PhaseLandingApproach> field_188743_c = func_188735_a(PhaseLandingApproach.class, "LandingApproach");
   public static final PhaseType<PhaseLanding> field_188744_d = func_188735_a(PhaseLanding.class, "Landing");
   public static final PhaseType<PhaseTakeoff> field_188745_e = func_188735_a(PhaseTakeoff.class, "Takeoff");
   public static final PhaseType<PhaseSittingFlaming> field_188746_f = func_188735_a(PhaseSittingFlaming.class, "SittingFlaming");
   public static final PhaseType<PhaseSittingScanning> field_188747_g = func_188735_a(PhaseSittingScanning.class, "SittingScanning");
   public static final PhaseType<PhaseSittingAttacking> field_188748_h = func_188735_a(PhaseSittingAttacking.class, "SittingAttacking");
   public static final PhaseType<PhaseChargingPlayer> field_188749_i = func_188735_a(PhaseChargingPlayer.class, "ChargingPlayer");
   public static final PhaseType<PhaseDying> field_188750_j = func_188735_a(PhaseDying.class, "Dying");
   public static final PhaseType<PhaseHover> field_188751_k = func_188735_a(PhaseHover.class, "Hover");
   private final Class<? extends IPhase> field_188753_m;
   private final int field_188754_n;
   private final String field_188755_o;

   private PhaseType(int var1, Class<? extends IPhase> var2, String var3) {
      super();
      this.field_188754_n = var1;
      this.field_188753_m = var2;
      this.field_188755_o = var3;
   }

   public IPhase func_188736_a(EntityDragon var1) {
      try {
         Constructor var2 = this.func_188737_a();
         return (IPhase)var2.newInstance(var1);
      } catch (Exception var3) {
         throw new Error(var3);
      }
   }

   protected Constructor<? extends IPhase> func_188737_a() throws NoSuchMethodException {
      return this.field_188753_m.getConstructor(EntityDragon.class);
   }

   public int func_188740_b() {
      return this.field_188754_n;
   }

   public String toString() {
      return this.field_188755_o + " (#" + this.field_188754_n + ")";
   }

   public static PhaseType<?> func_188738_a(int var0) {
      return var0 >= 0 && var0 < field_188752_l.length ? field_188752_l[var0] : field_188741_a;
   }

   public static int func_188739_c() {
      return field_188752_l.length;
   }

   private static <T extends IPhase> PhaseType<T> func_188735_a(Class<T> var0, String var1) {
      PhaseType var2 = new PhaseType(field_188752_l.length, var0, var1);
      field_188752_l = (PhaseType[])Arrays.copyOf(field_188752_l, field_188752_l.length + 1);
      field_188752_l[var2.func_188740_b()] = var2;
      return var2;
   }
}
