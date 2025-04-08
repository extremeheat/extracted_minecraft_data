package net.minecraft.client.gui.screens.advancements;

import net.minecraft.advancements.AdvancementType;
import net.minecraft.resources.ResourceLocation;

public enum AdvancementWidgetType {
   OBTAINED(ResourceLocation.withDefaultNamespace("advancements/box_obtained"), ResourceLocation.withDefaultNamespace("advancements/task_frame_obtained"), ResourceLocation.withDefaultNamespace("advancements/challenge_frame_obtained"), ResourceLocation.withDefaultNamespace("advancements/goal_frame_obtained")),
   UNOBTAINED(ResourceLocation.withDefaultNamespace("advancements/box_unobtained"), ResourceLocation.withDefaultNamespace("advancements/task_frame_unobtained"), ResourceLocation.withDefaultNamespace("advancements/challenge_frame_unobtained"), ResourceLocation.withDefaultNamespace("advancements/goal_frame_unobtained"));

   private final ResourceLocation boxSprite;
   private final ResourceLocation taskFrameSprite;
   private final ResourceLocation challengeFrameSprite;
   private final ResourceLocation goalFrameSprite;

   private AdvancementWidgetType(final ResourceLocation var3, final ResourceLocation var4, final ResourceLocation var5, final ResourceLocation var6) {
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
