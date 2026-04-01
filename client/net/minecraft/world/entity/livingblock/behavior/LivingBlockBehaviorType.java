package net.minecraft.world.entity.livingblock.behavior;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.cognition.Agency;
import net.minecraft.world.entity.livingblock.cognition.AgencyProbe;
import net.minecraft.world.entity.livingblock.cognition.EntityAgency;

public record LivingBlockBehaviorType(Function<LivingBlock, LivingBlockBehaviorEntry<LivingBlock>> factory) {
   public LivingBlockBehaviorType {
      super();
   }

   public void addToBehaviorList(final LivingBlock entity, final Collection<LivingBlockBehaviorEntry<?>> behaviors) {
      behaviors.add((LivingBlockBehaviorEntry)this.factory.apply(entity));
   }

   public static <T extends LivingBlock> LivingBlockBehaviorType behaviorType(final Supplier<LivingBlockBehavior> factory) {
      return new LivingBlockBehaviorType((entity) -> new LivingBlockBehaviorEntry(entity, (LivingBlockBehavior)factory.get(), 0, 0));
   }

   public static <T extends LivingBlock> LivingBlockBehaviorType behaviorType(final Function<Agency, LivingBlockBehavior> factory) {
      AgencyProbe probe = new AgencyProbe();
      factory.apply(probe);
      int mutex = probe.getMutex();
      int pursuedDesires = probe.getPursuedDesires();
      return new LivingBlockBehaviorType((entity) -> new LivingBlockBehaviorEntry(entity, (LivingBlockBehavior)factory.apply(new EntityAgency(entity)), mutex, pursuedDesires));
   }
}
