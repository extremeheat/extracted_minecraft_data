package net.minecraft.world.entity.livingblock.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class LivingBlockBehaviorEntry<T extends LivingBlock> {
   private final T entity;
   public final LivingBlockBehavior instance;
   private final int mutex;
   private final int pursuedDesires;
   private boolean active;

   public LivingBlockBehaviorEntry(final T entity, final LivingBlockBehavior instance, final int mutex, final int pursuedDesires) {
      super();
      this.entity = entity;
      this.instance = instance;
      this.mutex = mutex;
      this.pursuedDesires = pursuedDesires;
   }

   public int mutex() {
      return this.mutex;
   }

   public boolean isActive() {
      return this.active;
   }

   public boolean tick(final ServerLevel level, final int tickCount) {
      if ((this.active || this.start()) && this.entity.hopesAndDreams.hasDesires(this.pursuedDesires) && this.instance.tick(this.entity, level, tickCount)) {
         return true;
      } else {
         this.stop();
         return false;
      }
   }

   private boolean start() {
      if (!this.active && this.entity.hopesAndDreams.hasDesires(this.pursuedDesires) && this.instance.canStartUsing(this.entity)) {
         this.active = true;
         this.instance.onStart(this.entity);
         return true;
      } else {
         return false;
      }
   }

   public void stop() {
      if (this.active) {
         this.active = false;
         this.instance.onStop(this.entity);
      }

   }

   public void save(final ValueOutput output) {
      String dataTag = this.instance.getDataTag();
      if (!dataTag.isEmpty()) {
         ValueOutput behaviorOutput = output.child(dataTag);
         this.instance.save(behaviorOutput, this.entity);
      }
   }

   public void loadData(final ValueInput input) {
      String dataTag = this.instance.getDataTag();
      if (!dataTag.isEmpty()) {
         ValueInput behaviorInput = input.childOrEmpty(dataTag);
         this.instance.loadData(behaviorInput);
      }
   }

   public void onDeath(final ServerLevel level) {
      this.instance.onDeath(this.entity, level);
   }

   public void onRemoval(final ServerLevel level) {
      this.instance.onRemoval(this.entity, level);
   }
}
