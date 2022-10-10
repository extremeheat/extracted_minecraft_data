package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.stats.StatType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextFormatting;

public class ScoreCriteria {
   public static final Map<String, ScoreCriteria> field_96643_a = Maps.newHashMap();
   public static final ScoreCriteria field_96641_b = new ScoreCriteria("dummy");
   public static final ScoreCriteria field_178791_c = new ScoreCriteria("trigger");
   public static final ScoreCriteria field_96642_c = new ScoreCriteria("deathCount");
   public static final ScoreCriteria field_96639_d = new ScoreCriteria("playerKillCount");
   public static final ScoreCriteria field_96640_e = new ScoreCriteria("totalKillCount");
   public static final ScoreCriteria field_96638_f;
   public static final ScoreCriteria field_186698_h;
   public static final ScoreCriteria field_186699_i;
   public static final ScoreCriteria field_186700_j;
   public static final ScoreCriteria field_186701_k;
   public static final ScoreCriteria field_186702_l;
   public static final ScoreCriteria[] field_197913_m;
   public static final ScoreCriteria[] field_197914_n;
   private final String field_197915_o;
   private final boolean field_197916_p;
   private final ScoreCriteria.RenderType field_197917_q;

   public ScoreCriteria(String var1) {
      this(var1, false, ScoreCriteria.RenderType.INTEGER);
   }

   protected ScoreCriteria(String var1, boolean var2, ScoreCriteria.RenderType var3) {
      super();
      this.field_197915_o = var1;
      this.field_197916_p = var2;
      this.field_197917_q = var3;
      field_96643_a.put(var1, this);
   }

   @Nullable
   public static ScoreCriteria func_197911_a(String var0) {
      if (field_96643_a.containsKey(var0)) {
         return (ScoreCriteria)field_96643_a.get(var0);
      } else {
         int var1 = var0.indexOf(58);
         if (var1 < 0) {
            return null;
         } else {
            StatType var2 = (StatType)IRegistry.field_212634_w.func_212608_b(ResourceLocation.func_195828_a(var0.substring(0, var1), '.'));
            return var2 == null ? null : func_197912_a(var2, ResourceLocation.func_195828_a(var0.substring(var1 + 1), '.'));
         }
      }
   }

   @Nullable
   private static <T> ScoreCriteria func_197912_a(StatType<T> var0, ResourceLocation var1) {
      IRegistry var2 = var0.func_199080_a();
      return var2.func_212607_c(var1) ? var0.func_199076_b(var2.func_212608_b(var1)) : null;
   }

   public String func_96636_a() {
      return this.field_197915_o;
   }

   public boolean func_96637_b() {
      return this.field_197916_p;
   }

   public ScoreCriteria.RenderType func_178790_c() {
      return this.field_197917_q;
   }

   static {
      field_96638_f = new ScoreCriteria("health", true, ScoreCriteria.RenderType.HEARTS);
      field_186698_h = new ScoreCriteria("food", true, ScoreCriteria.RenderType.INTEGER);
      field_186699_i = new ScoreCriteria("air", true, ScoreCriteria.RenderType.INTEGER);
      field_186700_j = new ScoreCriteria("armor", true, ScoreCriteria.RenderType.INTEGER);
      field_186701_k = new ScoreCriteria("xp", true, ScoreCriteria.RenderType.INTEGER);
      field_186702_l = new ScoreCriteria("level", true, ScoreCriteria.RenderType.INTEGER);
      field_197913_m = new ScoreCriteria[]{new ScoreCriteria("teamkill." + TextFormatting.BLACK.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.DARK_BLUE.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.DARK_GREEN.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.DARK_AQUA.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.DARK_RED.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.DARK_PURPLE.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.GOLD.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.GRAY.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.DARK_GRAY.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.BLUE.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.GREEN.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.AQUA.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.RED.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.LIGHT_PURPLE.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.YELLOW.func_96297_d()), new ScoreCriteria("teamkill." + TextFormatting.WHITE.func_96297_d())};
      field_197914_n = new ScoreCriteria[]{new ScoreCriteria("killedByTeam." + TextFormatting.BLACK.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_BLUE.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_GREEN.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_AQUA.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_RED.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_PURPLE.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.GOLD.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.GRAY.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_GRAY.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.BLUE.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.GREEN.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.AQUA.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.RED.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.LIGHT_PURPLE.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.YELLOW.func_96297_d()), new ScoreCriteria("killedByTeam." + TextFormatting.WHITE.func_96297_d())};
   }

   public static enum RenderType {
      INTEGER("integer"),
      HEARTS("hearts");

      private final String field_211840_c;
      private static final Map<String, ScoreCriteria.RenderType> field_211841_d;

      private RenderType(String var3) {
         this.field_211840_c = var3;
      }

      public String func_211838_a() {
         return this.field_211840_c;
      }

      public static ScoreCriteria.RenderType func_211839_a(String var0) {
         return (ScoreCriteria.RenderType)field_211841_d.getOrDefault(var0, INTEGER);
      }

      static {
         Builder var0 = ImmutableMap.builder();
         ScoreCriteria.RenderType[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            ScoreCriteria.RenderType var4 = var1[var3];
            var0.put(var4.field_211840_c, var4);
         }

         field_211841_d = var0.build();
      }
   }
}
