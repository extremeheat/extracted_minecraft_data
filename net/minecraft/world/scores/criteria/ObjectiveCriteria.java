package net.minecraft.world.scores.criteria;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;

public class ObjectiveCriteria {
   public static final Map CRITERIA_BY_NAME = Maps.newHashMap();
   public static final ObjectiveCriteria DUMMY = new ObjectiveCriteria("dummy");
   public static final ObjectiveCriteria TRIGGER = new ObjectiveCriteria("trigger");
   public static final ObjectiveCriteria DEATH_COUNT = new ObjectiveCriteria("deathCount");
   public static final ObjectiveCriteria KILL_COUNT_PLAYERS = new ObjectiveCriteria("playerKillCount");
   public static final ObjectiveCriteria KILL_COUNT_ALL = new ObjectiveCriteria("totalKillCount");
   public static final ObjectiveCriteria HEALTH;
   public static final ObjectiveCriteria FOOD;
   public static final ObjectiveCriteria AIR;
   public static final ObjectiveCriteria ARMOR;
   public static final ObjectiveCriteria EXPERIENCE;
   public static final ObjectiveCriteria LEVEL;
   public static final ObjectiveCriteria[] TEAM_KILL;
   public static final ObjectiveCriteria[] KILLED_BY_TEAM;
   private final String name;
   private final boolean readOnly;
   private final ObjectiveCriteria.RenderType renderType;

   public ObjectiveCriteria(String var1) {
      this(var1, false, ObjectiveCriteria.RenderType.INTEGER);
   }

   protected ObjectiveCriteria(String var1, boolean var2, ObjectiveCriteria.RenderType var3) {
      this.name = var1;
      this.readOnly = var2;
      this.renderType = var3;
      CRITERIA_BY_NAME.put(var1, this);
   }

   public static Optional byName(String var0) {
      if (CRITERIA_BY_NAME.containsKey(var0)) {
         return Optional.of(CRITERIA_BY_NAME.get(var0));
      } else {
         int var1 = var0.indexOf(58);
         return var1 < 0 ? Optional.empty() : Registry.STAT_TYPE.getOptional(ResourceLocation.of(var0.substring(0, var1), '.')).flatMap((var2) -> {
            return getStat(var2, ResourceLocation.of(var0.substring(var1 + 1), '.'));
         });
      }
   }

   private static Optional getStat(StatType var0, ResourceLocation var1) {
      Optional var10000 = var0.getRegistry().getOptional(var1);
      var0.getClass();
      return var10000.map(var0::get);
   }

   public String getName() {
      return this.name;
   }

   public boolean isReadOnly() {
      return this.readOnly;
   }

   public ObjectiveCriteria.RenderType getDefaultRenderType() {
      return this.renderType;
   }

   static {
      HEALTH = new ObjectiveCriteria("health", true, ObjectiveCriteria.RenderType.HEARTS);
      FOOD = new ObjectiveCriteria("food", true, ObjectiveCriteria.RenderType.INTEGER);
      AIR = new ObjectiveCriteria("air", true, ObjectiveCriteria.RenderType.INTEGER);
      ARMOR = new ObjectiveCriteria("armor", true, ObjectiveCriteria.RenderType.INTEGER);
      EXPERIENCE = new ObjectiveCriteria("xp", true, ObjectiveCriteria.RenderType.INTEGER);
      LEVEL = new ObjectiveCriteria("level", true, ObjectiveCriteria.RenderType.INTEGER);
      TEAM_KILL = new ObjectiveCriteria[]{new ObjectiveCriteria("teamkill." + ChatFormatting.BLACK.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.DARK_BLUE.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.DARK_GREEN.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.DARK_AQUA.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.DARK_RED.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.DARK_PURPLE.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.GOLD.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.GRAY.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.DARK_GRAY.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.BLUE.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.GREEN.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.AQUA.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.RED.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.LIGHT_PURPLE.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.YELLOW.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.WHITE.getName())};
      KILLED_BY_TEAM = new ObjectiveCriteria[]{new ObjectiveCriteria("killedByTeam." + ChatFormatting.BLACK.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.DARK_BLUE.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.DARK_GREEN.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.DARK_AQUA.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.DARK_RED.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.DARK_PURPLE.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.GOLD.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.GRAY.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.DARK_GRAY.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.BLUE.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.GREEN.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.AQUA.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.RED.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.LIGHT_PURPLE.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.YELLOW.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.WHITE.getName())};
   }

   public static enum RenderType {
      INTEGER("integer"),
      HEARTS("hearts");

      private final String id;
      private static final Map BY_ID;

      private RenderType(String var3) {
         this.id = var3;
      }

      public String getId() {
         return this.id;
      }

      public static ObjectiveCriteria.RenderType byId(String var0) {
         return (ObjectiveCriteria.RenderType)BY_ID.getOrDefault(var0, INTEGER);
      }

      static {
         Builder var0 = ImmutableMap.builder();
         ObjectiveCriteria.RenderType[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            ObjectiveCriteria.RenderType var4 = var1[var3];
            var0.put(var4.id, var4);
         }

         BY_ID = var0.build();
      }
   }
}
