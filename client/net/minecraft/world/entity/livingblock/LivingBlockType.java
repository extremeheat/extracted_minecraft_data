package net.minecraft.world.entity.livingblock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.behavior.LivingBlockBehaviorEntry;
import net.minecraft.world.entity.livingblock.behavior.LivingBlockBehaviorType;
import net.minecraft.world.entity.livingblock.hurt.OnHurt;
import net.minecraft.world.entity.livingblock.interact.ConsumeItem;
import net.minecraft.world.entity.livingblock.interact.OnInteract;
import net.minecraft.world.entity.livingblock.movement.MovementStrategy;
import net.minecraft.world.entity.livingblock.movement.RollingMovement;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class LivingBlockType {
   private final List<LivingBlockBehaviorType> behaviors;
   private final CollisionInteraction collision;
   private final Supplier<OnInteract> onInteract;
   private final Supplier<OnHurt> onHurt;
   private final Supplier<MovementStrategy<?>> movement;
   private final boolean fireImmune;

   private LivingBlockType(final List<LivingBlockBehaviorType> behaviors, final CollisionInteraction collision, final Supplier<OnInteract> onInteract, final Supplier<OnHurt> onHurt, final Supplier<MovementStrategy<?>> movement, final boolean fireImmune) {
      super();
      this.behaviors = behaviors;
      this.collision = collision;
      this.onInteract = onInteract;
      this.onHurt = onHurt;
      this.movement = movement;
      this.fireImmune = fireImmune;
   }

   public List<LivingBlockBehaviorEntry<?>> createBehaviorsFor(final LivingBlock entity) {
      List<LivingBlockBehaviorEntry<?>> destination = new ArrayList(this.behaviors.size());

      for(LivingBlockBehaviorType behaviorType : this.behaviors) {
         behaviorType.addToBehaviorList(entity, destination);
      }

      return destination;
   }

   public @Nullable LivingBlock create(final Level level, final BlockState blockState) {
      LivingBlock entity = EntityType.LIVING_BLOCK.create(level, EntitySpawnReason.NATURAL);
      if (entity == null) {
         return null;
      } else {
         entity.setFireImmune(this.fireImmune);
         entity.setBlockState(blockState);
         return entity;
      }
   }

   public @Nullable LivingBlock create(final Level level, final ItemStack itemStack) {
      LivingBlock entity = EntityType.LIVING_BLOCK.create(level, EntitySpawnReason.NATURAL);
      if (entity == null) {
         return null;
      } else {
         entity.setFireImmune(this.fireImmune);
         entity.setItemStack(itemStack);
         return entity;
      }
   }

   public CollisionInteraction getCollision() {
      return this.collision;
   }

   public OnInteract getOnInteract() {
      return (OnInteract)this.onInteract.get();
   }

   public OnHurt getOnHurt() {
      return (OnHurt)this.onHurt.get();
   }

   public MovementStrategy<?> getMovement() {
      return (MovementStrategy)this.movement.get();
   }

   public Builder toBuilder() {
      return new Builder(new ArrayList(this.behaviors), this.collision, this.onInteract, this.onHurt, this.movement);
   }

   public static Builder builder() {
      return new Builder(new ArrayList(), CollisionInteraction.BLOCK, ConsumeItem::new, () -> OnHurt.DO_NOTHING, RollingMovement::new);
   }

   public static final class Builder {
      private final List<LivingBlockBehaviorType> behaviors;
      private CollisionInteraction collision;
      private Supplier<OnInteract> onInteract;
      private Supplier<OnHurt> onHurt;
      private Supplier<MovementStrategy<?>> movement;
      private boolean fireImmune;

      private Builder(final List<LivingBlockBehaviorType> behaviors, final CollisionInteraction collision, final Supplier<OnInteract> onInteract, final Supplier<OnHurt> onHurt, final Supplier<MovementStrategy<?>> movement) {
         super();
         this.behaviors = behaviors;
         this.collision = collision;
         this.onHurt = onHurt;
         this.onInteract = onInteract;
         this.movement = movement;
      }

      public Builder apply(final UnaryOperator<Builder> preset) {
         preset.apply(this);
         return this;
      }

      public Builder onInteract(final Supplier<OnInteract> onInteract) {
         this.onInteract = onInteract;
         return this;
      }

      public Builder onHurt(final Supplier<OnHurt> onHurt) {
         this.onHurt = onHurt;
         return this;
      }

      public Builder moveUsing(final Supplier<MovementStrategy<?>> movement) {
         this.movement = movement;
         return this;
      }

      public Builder behavior(final LivingBlockBehaviorType behaviorType) {
         this.behaviors.add(behaviorType);
         return this;
      }

      public Builder collision(final CollisionInteraction collision) {
         this.collision = collision;
         return this;
      }

      public Builder fireImmune() {
         this.fireImmune = true;
         return this;
      }

      public LivingBlockType build() {
         return new LivingBlockType(List.copyOf(this.behaviors), this.collision, this.onInteract, this.onHurt, this.movement, this.fireImmune);
      }
   }
}
