package net.minecraft.client.gui.screens.advancements;

import net.minecraft.advancements.FrameType;
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

   private AdvancementWidgetType(ResourceLocation var3, ResourceLocation var4, ResourceLocation var5, ResourceLocation var6) {
      this.boxSprite = var3;
      this.taskFrameSprite = var4;
      this.challengeFrameSprite = var5;
      this.goalFrameSprite = var6;
   }

   public ResourceLocation boxSprite() {
      return this.boxSprite;
   }

   public ResourceLocation frameSprite(FrameType var1) {
      return switch(var1) {
         case TASK -> this.taskFrameSprite;
         case CHALLENGE -> this.challengeFrameSprite;
         case GOAL -> this.goalFrameSprite;
      };
   }
}
