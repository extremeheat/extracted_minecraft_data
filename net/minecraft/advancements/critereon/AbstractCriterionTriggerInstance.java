package net.minecraft.advancements.critereon;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;

public class AbstractCriterionTriggerInstance implements CriterionTriggerInstance {
   private final ResourceLocation criterion;

   public AbstractCriterionTriggerInstance(ResourceLocation var1) {
      this.criterion = var1;
   }

   public ResourceLocation getCriterion() {
      return this.criterion;
   }

   public String toString() {
      return "AbstractCriterionInstance{criterion=" + this.criterion + '}';
   }
}
