package net.minecraft.client.tutorial;

import java.util.function.Function;

public enum TutorialSteps {
   MOVEMENT("movement", MovementTutorialStepInstance::new),
   FIND_TREE("find_tree", FindTreeTutorialStepInstance::new),
   PUNCH_TREE("punch_tree", PunchTreeTutorialStepInstance::new),
   OPEN_INVENTORY("open_inventory", OpenInventoryTutorialStep::new),
   CRAFT_PLANKS("craft_planks", CraftPlanksTutorialStep::new),
   NONE("none", CompletedTutorialStepInstance::new);

   private final String name;
   private final Function<Tutorial, ? extends TutorialStepInstance> constructor;

   private <T extends TutorialStepInstance> TutorialSteps(final String param3, final Function<Tutorial, T> param4) {
      this.name = nullxx;
      this.constructor = nullxxx;
   }

   public TutorialStepInstance create(Tutorial var1) {
      return this.constructor.apply(var1);
   }

   public String getName() {
      return this.name;
   }

   public static TutorialSteps getByName(String var0) {
      for (TutorialSteps var4 : values()) {
         if (var4.name.equals(var0)) {
            return var4;
         }
      }

      return NONE;
   }
}
