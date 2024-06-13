package net.minecraft.client.gui.screens.advancements;

import net.minecraft.advancements.AdvancementType;
import net.minecraft.resources.ResourceLocation;

public enum AdvancementWidgetType {
   OBTAINED(
      ResourceLocation.withDefaultNamespace("advancements/box_obtained"),
      ResourceLocation.withDefaultNamespace("advancements/task_frame_obtained"),
      ResourceLocation.withDefaultNamespace("advancements/challenge_frame_obtained"),
      ResourceLocation.withDefaultNamespace("advancements/goal_frame_obtained")
   ),
   UNOBTAINED(
      ResourceLocation.withDefaultNamespace("advancements/box_unobtained"),
      ResourceLocation.withDefaultNamespace("advancements/task_frame_unobtained"),
      ResourceLocation.withDefaultNamespace("advancements/challenge_frame_unobtained"),
      ResourceLocation.withDefaultNamespace("advancements/goal_frame_unobtained")
   );

   private final ResourceLocation boxSprite;
   private final ResourceLocation taskFrameSprite;
   private final ResourceLocation challengeFrameSprite;
   private final ResourceLocation goalFrameSprite;

   private AdvancementWidgetType(
      final ResourceLocation nullxx, final ResourceLocation nullxxx, final ResourceLocation nullxxxx, final ResourceLocation nullxxxxx
   ) {
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
      };
   }
}
