package net.minecraft.world.entity.boss.enderdragon.phases;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class EnderDragonPhaseManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final EnderDragon dragon;
   private final @Nullable DragonPhaseInstance[] phases = new DragonPhaseInstance[EnderDragonPhase.getCount()];
   private @Nullable DragonPhaseInstance currentPhase;

   public EnderDragonPhaseManager(EnderDragon var1) {
      super();
      this.dragon = var1;
      this.setPhase(EnderDragonPhase.HOVERING);
   }

   public void setPhase(EnderDragonPhase<?> var1) {
      if (this.currentPhase == null || var1 != this.currentPhase.getPhase()) {
         if (this.currentPhase != null) {
            this.currentPhase.end();
         }

         this.currentPhase = this.getPhase(var1);
         if (!this.dragon.level().isClientSide()) {
            this.dragon.getEntityData().set(EnderDragon.DATA_PHASE, var1.getId());
         }

         LOGGER.debug("Dragon is now in phase {} on the {}", var1, this.dragon.level().isClientSide() ? "client" : "server");
         this.currentPhase.begin();
      }
   }

   public DragonPhaseInstance getCurrentPhase() {
      return (DragonPhaseInstance)Objects.requireNonNull(this.currentPhase);
   }

   public <T extends DragonPhaseInstance> T getPhase(EnderDragonPhase<T> var1) {
      int var2 = var1.getId();
      DragonPhaseInstance var3 = this.phases[var2];
      if (var3 == null) {
         var3 = var1.createInstance(this.dragon);
         this.phases[var2] = var3;
      }

      return (T)var3;
   }
}
