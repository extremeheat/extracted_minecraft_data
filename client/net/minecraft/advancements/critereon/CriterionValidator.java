package net.minecraft.advancements.critereon;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderGetter;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class CriterionValidator {
   private final ProblemReporter reporter;
   private final HolderGetter.Provider lootData;

   public CriterionValidator(ProblemReporter var1, HolderGetter.Provider var2) {
      super();
      this.reporter = var1;
      this.lootData = var2;
   }

   public void validateEntity(Optional<ContextAwarePredicate> var1, String var2) {
      var1.ifPresent((var2x) -> {
         this.validateEntity(var2x, var2);
      });
   }

   public void validateEntities(List<ContextAwarePredicate> var1, String var2) {
      this.validate(var1, LootContextParamSets.ADVANCEMENT_ENTITY, var2);
   }

   public void validateEntity(ContextAwarePredicate var1, String var2) {
      this.validate(var1, LootContextParamSets.ADVANCEMENT_ENTITY, var2);
   }

   public void validate(ContextAwarePredicate var1, LootContextParamSet var2, String var3) {
      var1.validate(new ValidationContext(this.reporter.forChild(var3), var2, this.lootData));
   }

   public void validate(List<ContextAwarePredicate> var1, LootContextParamSet var2, String var3) {
      for(int var4 = 0; var4 < var1.size(); ++var4) {
         ContextAwarePredicate var5 = (ContextAwarePredicate)var1.get(var4);
         var5.validate(new ValidationContext(this.reporter.forChild(var3 + "[" + var4 + "]"), var2, this.lootData));
      }

   }
}
