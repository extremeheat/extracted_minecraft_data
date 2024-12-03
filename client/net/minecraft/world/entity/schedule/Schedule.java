package net.minecraft.world.entity.schedule;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class Schedule {
   public static final int WORK_START_TIME = 2000;
   public static final int TOTAL_WORK_TIME = 7000;
   public static final Schedule EMPTY;
   public static final Schedule SIMPLE;
   public static final Schedule VILLAGER_BABY;
   public static final Schedule VILLAGER_DEFAULT;
   private final Map<Activity, Timeline> timelines = Maps.newHashMap();

   public Schedule() {
      super();
   }

   protected static ScheduleBuilder register(String var0) {
      Schedule var1 = (Schedule)Registry.register(BuiltInRegistries.SCHEDULE, (String)var0, new Schedule());
      return new ScheduleBuilder(var1);
   }

   protected void ensureTimelineExistsFor(Activity var1) {
      if (!this.timelines.containsKey(var1)) {
         this.timelines.put(var1, new Timeline());
      }

   }

   protected Timeline getTimelineFor(Activity var1) {
      return (Timeline)this.timelines.get(var1);
   }

   protected List<Timeline> getAllTimelinesExceptFor(Activity var1) {
      return (List)this.timelines.entrySet().stream().filter((var1x) -> var1x.getKey() != var1).map(Map.Entry::getValue).collect(Collectors.toList());
   }

   public Activity getActivityAt(int var1) {
      return (Activity)this.timelines.entrySet().stream().max(Comparator.comparingDouble((var1x) -> (double)((Timeline)var1x.getValue()).getValueAt(var1))).map(Map.Entry::getKey).orElse(Activity.IDLE);
   }

   static {
      EMPTY = register("empty").changeActivityAt(0, Activity.IDLE).build();
      SIMPLE = register("simple").changeActivityAt(5000, Activity.WORK).changeActivityAt(11000, Activity.REST).build();
      VILLAGER_BABY = register("villager_baby").changeActivityAt(10, Activity.IDLE).changeActivityAt(3000, Activity.PLAY).changeActivityAt(6000, Activity.IDLE).changeActivityAt(10000, Activity.PLAY).changeActivityAt(12000, Activity.REST).build();
      VILLAGER_DEFAULT = register("villager_default").changeActivityAt(10, Activity.IDLE).changeActivityAt(2000, Activity.WORK).changeActivityAt(9000, Activity.MEET).changeActivityAt(11000, Activity.IDLE).changeActivityAt(12000, Activity.REST).build();
   }
}
