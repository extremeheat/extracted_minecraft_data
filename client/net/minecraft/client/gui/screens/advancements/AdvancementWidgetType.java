package net.minecraft.client.gui.screens.advancements;

import net.minecraft.advancements.AdvancementType;
import net.minecraft.resources.Identifier;

public enum AdvancementWidgetType {
   OBTAINED(Identifier.withDefaultNamespace("advancements/box_obtained"), Identifier.withDefaultNamespace("advancements/task_frame_obtained"), Identifier.withDefaultNamespace("advancements/challenge_frame_obtained"), Identifier.withDefaultNamespace("advancements/goal_frame_obtained")),
   UNOBTAINED(Identifier.withDefaultNamespace("advancements/box_unobtained"), Identifier.withDefaultNamespace("advancements/task_frame_unobtained"), Identifier.withDefaultNamespace("advancements/challenge_frame_unobtained"), Identifier.withDefaultNamespace("advancements/goal_frame_unobtained"));

   private final Identifier boxSprite;
   private final Identifier taskFrameSprite;
   private final Identifier challengeFrameSprite;
   private final Identifier goalFrameSprite;

   private AdvancementWidgetType(final Identifier boxSprite, final Identifier taskFrameSprite, final Identifier challengeFrameSprite, final Identifier goalFrameSprite) {
      this.boxSprite = boxSprite;
      this.taskFrameSprite = taskFrameSprite;
      this.challengeFrameSprite = challengeFrameSprite;
      this.goalFrameSprite = goalFrameSprite;
   }

   public Identifier boxSprite() {
      return this.boxSprite;
   }

   public Identifier frameSprite(final AdvancementType type) {
      Identifier var10000;
      switch (type) {
         case TASK -> var10000 = this.taskFrameSprite;
         case CHALLENGE -> var10000 = this.challengeFrameSprite;
         case GOAL -> var10000 = this.goalFrameSprite;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static AdvancementWidgetType[] $values() {
      return new AdvancementWidgetType[]{OBTAINED, UNOBTAINED};
   }
}
