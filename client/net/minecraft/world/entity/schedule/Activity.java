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
   public static final Activity FIGHT = register("fight");
   public static final Activity CELEBRATE = register("celebrate");
   public static final Activity ADMIRE_ITEM = register("admire_item");
   public static final Activity AVOID = register("avoid");
   public static final Activity RIDE = register("ride");
   private final String name;
   private final int hashCode;

   private Activity(String var1) {
      super();
      this.name = var1;
      this.hashCode = var1.hashCode();
   }

   public String getName() {
      return this.name;
   }

   private static Activity register(String var0) {
      return (Activity)Registry.register(Registry.ACTIVITY, (String)var0, new Activity(var0));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Activity var2 = (Activity)var1;
         return this.name.equals(var2.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.hashCode;
   }

   public String toString() {
      return this.getName();
   }
}
