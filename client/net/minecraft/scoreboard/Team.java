package net.minecraft.scoreboard;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;

public abstract class Team {
   public Team() {
      super();
   }

   public boolean func_142054_a(Team var1) {
      if (var1 == null) {
         return false;
      } else {
         return this == var1;
      }
   }

   public abstract String func_96661_b();

   public abstract String func_142053_d(String var1);

   public abstract boolean func_98297_h();

   public abstract boolean func_96665_g();

   public abstract Team.EnumVisible func_178770_i();

   public abstract Collection<String> func_96670_d();

   public abstract Team.EnumVisible func_178771_j();

   public static enum EnumVisible {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      private static Map<String, Team.EnumVisible> field_178828_g = Maps.newHashMap();
      public final String field_178830_e;
      public final int field_178827_f;

      public static String[] func_178825_a() {
         return (String[])field_178828_g.keySet().toArray(new String[field_178828_g.size()]);
      }

      public static Team.EnumVisible func_178824_a(String var0) {
         return (Team.EnumVisible)field_178828_g.get(var0);
      }

      private EnumVisible(String var3, int var4) {
         this.field_178830_e = var3;
         this.field_178827_f = var4;
      }

      static {
         Team.EnumVisible[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            Team.EnumVisible var3 = var0[var2];
            field_178828_g.put(var3.field_178830_e, var3);
         }

      }
   }
}
