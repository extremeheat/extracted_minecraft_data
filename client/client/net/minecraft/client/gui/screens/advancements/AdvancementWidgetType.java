package net.minecraft.client.gui.screens.advancements;

import net.minecraft.advancements.AdvancementType;
import net.minecraft.resources.ResourceLocation;

public enum AdvancementWidgetType {
   OBTAINED(
      new ResourceLocation("advancements/box_obtained"),
      new ResourceLocation("advancements/task_frame_obtained"),
      new ResourceLocation("advancements/challenge_frame_obtained"),
      new ResourceLocation("advancements/goal_frame_obtained")
   ),
   UNOBTAINED(
      new ResourceLocation("advancements/box_unobtained"),
      new ResourceLocation("advancements/task_frame_unobtained"),
      new ResourceLocation("advancements/challenge_frame_unobtained"),
      new ResourceLocation("advancements/goal_frame_unobtained")
   );

   private final ResourceLocation boxSprite;
   private final ResourceLocation taskFrameSprite;
   private final ResourceLocation challengeFrameSprite;
   private final ResourceLocation goalFrameSprite;

   private AdvancementWidgetType(final ResourceLocation param3, final ResourceLocation param4, final ResourceLocation param5, final ResourceLocation param6) {
      this.boxSprite = nullxx;
      this.taskFrameSprite = nullxxx;
      this.challengeFrameSprite = nullxxxx;
      this.goalFrameSprite = nullxxxxx;
   }

   public ResourceLocation boxSprite() {
      return this.boxSprite;
   }

   public ResourceLocation frameSprite(AdvancementType var1) {
      return switch (var1) {
         case TASK -> this.taskFrameSprite;
         case CHALLENGE -> this.challengeFrameSprite;
         case GOAL -> this.goalFrameSprite;
         default -> throw new MatchException(null, null);
      };
   }
}
