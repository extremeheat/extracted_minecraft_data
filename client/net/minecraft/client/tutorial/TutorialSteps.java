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

   private <T extends TutorialStepInstance> TutorialSteps(String var3, Function<Tutorial, T> var4) {
      this.name = var3;
      this.constructor = var4;
   }

   public TutorialStepInstance create(Tutorial var1) {
      return (TutorialStepInstance)this.constructor.apply(var1);
   }

   public String getName() {
      return this.name;
   }

   public static TutorialSteps getByName(String var0) {
      TutorialSteps[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         TutorialSteps var4 = var1[var3];
         if (var4.name.equals(var0)) {
            return var4;
         }
      }

      return NONE;
   }

   // $FF: synthetic method
   private static TutorialSteps[] $values() {
      return new TutorialSteps[]{MOVEMENT, FIND_TREE, PUNCH_TREE, OPEN_INVENTORY, CRAFT_PLANKS, NONE};
   }
}
