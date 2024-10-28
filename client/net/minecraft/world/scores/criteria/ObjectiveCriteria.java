package net.minecraft.world.scores.criteria;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;
import net.minecraft.util.StringRepresentable;

public class ObjectiveCriteria {
   private static final Map<String, ObjectiveCriteria> CUSTOM_CRITERIA = Maps.newHashMap();
   private static final Map<String, ObjectiveCriteria> CRITERIA_CACHE = Maps.newHashMap();
   public static final ObjectiveCriteria DUMMY = registerCustom("dummy");
   public static final ObjectiveCriteria TRIGGER = registerCustom("trigger");
   public static final ObjectiveCriteria DEATH_COUNT = registerCustom("deathCount");
   public static final ObjectiveCriteria KILL_COUNT_PLAYERS = registerCustom("playerKillCount");
   public static final ObjectiveCriteria KILL_COUNT_ALL = registerCustom("totalKillCount");
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
   private final RenderType renderType;

   private static ObjectiveCriteria registerCustom(String var0, boolean var1, RenderType var2) {
      ObjectiveCriteria var3 = new ObjectiveCriteria(var0, var1, var2);
      CUSTOM_CRITERIA.put(var0, var3);
      return var3;
   }

   private static ObjectiveCriteria registerCustom(String var0) {
      return registerCustom(var0, false, ObjectiveCriteria.RenderType.INTEGER);
   }

   protected ObjectiveCriteria(String var1) {
      this(var1, false, ObjectiveCriteria.RenderType.INTEGER);
   }

   protected ObjectiveCriteria(String var1, boolean var2, RenderType var3) {
      super();
      this.name = var1;
      this.readOnly = var2;
      this.renderType = var3;
      CRITERIA_CACHE.put(var1, this);
   }

   public static Set<String> getCustomCriteriaNames() {
      return ImmutableSet.copyOf(CUSTOM_CRITERIA.keySet());
   }

   public static Optional<ObjectiveCriteria> byName(String var0) {
      ObjectiveCriteria var1 = (ObjectiveCriteria)CRITERIA_CACHE.get(var0);
      if (var1 != null) {
         return Optional.of(var1);
      } else {
         int var2 = var0.indexOf(58);
         return var2 < 0 ? Optional.empty() : BuiltInRegistries.STAT_TYPE.getOptional(ResourceLocation.bySeparator(var0.substring(0, var2), '.')).flatMap((var2x) -> {
            return getStat(var2x, ResourceLocation.bySeparator(var0.substring(var2 + 1), '.'));
         });
      }
   }

   private static <T> Optional<ObjectiveCriteria> getStat(StatType<T> var0, ResourceLocation var1) {
      Optional var10000 = var0.getRegistry().getOptional(var1);
      Objects.requireNonNull(var0);
      return var10000.map(var0::get);
   }

   public String getName() {
      return this.name;
   }

   public boolean isReadOnly() {
      return this.readOnly;
   }

   public RenderType getDefaultRenderType() {
      return this.renderType;
   }

   static {
      HEALTH = registerCustom("health", true, ObjectiveCriteria.RenderType.HEARTS);
      FOOD = registerCustom("food", true, ObjectiveCriteria.RenderType.INTEGER);
      AIR = registerCustom("air", true, ObjectiveCriteria.RenderType.INTEGER);
      ARMOR = registerCustom("armor", true, ObjectiveCriteria.RenderType.INTEGER);
      EXPERIENCE = registerCustom("xp", true, ObjectiveCriteria.RenderType.INTEGER);
      LEVEL = registerCustom("level", true, ObjectiveCriteria.RenderType.INTEGER);
      TEAM_KILL = new ObjectiveCriteria[]{registerCustom("teamkill." + ChatFormatting.BLACK.getName()), registerCustom("teamkill." + ChatFormatting.DARK_BLUE.getName()), registerCustom("teamkill." + ChatFormatting.DARK_GREEN.getName()), registerCustom("teamkill." + ChatFormatting.DARK_AQUA.getName()), registerCustom("teamkill." + ChatFormatting.DARK_RED.getName()), registerCustom("teamkill." + ChatFormatting.DARK_PURPLE.getName()), registerCustom("teamkill." + ChatFormatting.GOLD.getName()), registerCustom("teamkill." + ChatFormatting.GRAY.getName()), registerCustom("teamkill." + ChatFormatting.DARK_GRAY.getName()), registerCustom("teamkill." + ChatFormatting.BLUE.getName()), registerCustom("teamkill." + ChatFormatting.GREEN.getName()), registerCustom("teamkill." + ChatFormatting.AQUA.getName()), registerCustom("teamkill." + ChatFormatting.RED.getName()), registerCustom("teamkill." + ChatFormatting.LIGHT_PURPLE.getName()), registerCustom("teamkill." + ChatFormatting.YELLOW.getName()), registerCustom("teamkill." + ChatFormatting.WHITE.getName())};
      KILLED_BY_TEAM = new ObjectiveCriteria[]{registerCustom("killedByTeam." + ChatFormatting.BLACK.getName()), registerCustom("killedByTeam." + ChatFormatting.DARK_BLUE.getName()), registerCustom("killedByTeam." + ChatFormatting.DARK_GREEN.getName()), registerCustom("killedByTeam." + ChatFormatting.DARK_AQUA.getName()), registerCustom("killedByTeam." + ChatFormatting.DARK_RED.getName()), registerCustom("killedByTeam." + ChatFormatting.DARK_PURPLE.getName()), registerCustom("killedByTeam." + ChatFormatting.GOLD.getName()), registerCustom("killedByTeam." + ChatFormatting.GRAY.getName()), registerCustom("killedByTeam." + ChatFormatting.DARK_GRAY.getName()), registerCustom("killedByTeam." + ChatFormatting.BLUE.getName()), registerCustom("killedByTeam." + ChatFormatting.GREEN.getName()), registerCustom("killedByTeam." + ChatFormatting.AQUA.getName()), registerCustom("killedByTeam." + ChatFormatting.RED.getName()), registerCustom("killedByTeam." + ChatFormatting.LIGHT_PURPLE.getName()), registerCustom("killedByTeam." + ChatFormatting.YELLOW.getName()), registerCustom("killedByTeam." + ChatFormatting.WHITE.getName())};
   }

   public static enum RenderType implements StringRepresentable {
      INTEGER("integer"),
      HEARTS("hearts");

      private final String id;
      public static final StringRepresentable.EnumCodec<RenderType> CODEC = StringRepresentable.fromEnum(RenderType::values);

      private RenderType(final String var3) {
         this.id = var3;
      }

      public String getId() {
         return this.id;
      }

      public String getSerializedName() {
         return this.id;
      }

      public static RenderType byId(String var0) {
         return (RenderType)CODEC.byName(var0, INTEGER);
      }

      // $FF: synthetic method
      private static RenderType[] $values() {
         return new RenderType[]{INTEGER, HEARTS};
      }
   }
}
