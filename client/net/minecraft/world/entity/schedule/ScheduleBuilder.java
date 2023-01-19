package net.minecraft.world.entity.schedule;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleBuilder {
   private final Schedule schedule;
   private final List<ScheduleBuilder.ActivityTransition> transitions = Lists.newArrayList();

   public ScheduleBuilder(Schedule var1) {
      super();
      this.schedule = var1;
   }

   public ScheduleBuilder changeActivityAt(int var1, Activity var2) {
      this.transitions.add(new ScheduleBuilder.ActivityTransition(var1, var2));
      return this;
   }

   public Schedule build() {
      this.transitions
         .stream()
         .map(ScheduleBuilder.ActivityTransition::getActivity)
         .collect(Collectors.toSet())
         .forEach(this.schedule::ensureTimelineExistsFor);
      this.transitions.forEach(var1 -> {
         Activity var2 = var1.getActivity();
         this.schedule.getAllTimelinesExceptFor(var2).forEach(var1x -> var1x.addKeyframe(var1.getTime(), 0.0F));
         this.schedule.getTimelineFor(var2).addKeyframe(var1.getTime(), 1.0F);
      });
      return this.schedule;
   }

   static class ActivityTransition {
      private final int time;
      private final Activity activity;

      public ActivityTransition(int var1, Activity var2) {
         super();
         this.time = var1;
         this.activity = var2;
      }

      public int getTime() {
         return this.time;
      }

      public Activity getActivity() {
         return this.activity;
      }
   }
}