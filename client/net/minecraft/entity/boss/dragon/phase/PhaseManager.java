package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.EntityDragon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhaseManager {
   private static final Logger field_188759_a = LogManager.getLogger();
   private final EntityDragon field_188760_b;
   private final IPhase[] field_188761_c = new IPhase[PhaseType.func_188739_c()];
   private IPhase field_188762_d;

   public PhaseManager(EntityDragon var1) {
      super();
      this.field_188760_b = var1;
      this.func_188758_a(PhaseType.field_188751_k);
   }

   public void func_188758_a(PhaseType<?> var1) {
      if (this.field_188762_d == null || var1 != this.field_188762_d.func_188652_i()) {
         if (this.field_188762_d != null) {
            this.field_188762_d.func_188658_e();
         }

         this.field_188762_d = this.func_188757_b(var1);
         if (!this.field_188760_b.field_70170_p.field_72995_K) {
            this.field_188760_b.func_184212_Q().func_187227_b(EntityDragon.field_184674_a, var1.func_188740_b());
         }

         field_188759_a.debug("Dragon is now in phase {} on the {}", var1, this.field_188760_b.field_70170_p.field_72995_K ? "client" : "server");
         this.field_188762_d.func_188660_d();
      }
   }

   public IPhase func_188756_a() {
      return this.field_188762_d;
   }

   public <T extends IPhase> T func_188757_b(PhaseType<T> var1) {
      int var2 = var1.func_188740_b();
      if (this.field_188761_c[var2] == null) {
         this.field_188761_c[var2] = var1.func_188736_a(this.field_188760_b);
      }

      return this.field_188761_c[var2];
   }
}
