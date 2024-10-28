package net.minecraft.client.gui.screens.advancements;

import net.minecraft.advancements.AdvancementType;
import net.minecraft.resources.ResourceLocation;

public enum AdvancementWidgetType {
   OBTAINED(new ResourceLocation("advancements/box_obtained"), new ResourceLocation("advancements/task_frame_obtained"), new ResourceLocation("advancements/challenge_frame_obtained"), new ResourceLocation("advancements/goal_frame_obtained")),
   UNOBTAINED(new ResourceLocation("advancements/box_unobtained"), new ResourceLocation("advancements/task_frame_unobtained"), new ResourceLocation("advancements/challenge_frame_unobtained"), new ResourceLocation("advancements/goal_frame_unobtained"));

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

   public ResourceLocation frameSprite(AdvancementType var1) {
      ResourceLocation var10000;
      switch (var1) {
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
