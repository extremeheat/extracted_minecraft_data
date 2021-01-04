package net.minecraft.world.entity.schedule;

import net.minecraft.core.Registry;

public class Activity {
   public static final Activity CORE = register("core");
   public static final Activity IDLE = register("idle");
   public static final Activity WORK = register("work");
   public static final Activity PLAY = register("play");
   public static final Activity REST = register("rest");
   public static final Activity MEET = register("meet");
   public static final Activity PANIC = register("panic");
   public static final Activity RAID = register("raid");
   public static final Activity PRE_RAID = register("pre_raid");
   public static final Activity HIDE = register("hide");
   private final String name;

   private Activity(String var1) {
      super();
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   private static Activity register(String var0) {
      return (Activity)Registry.register(Registry.ACTIVITY, (String)var0, new Activity(var0));
   }

   public String toString() {
      return this.getName();
   }
}
