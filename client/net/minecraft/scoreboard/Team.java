package net.minecraft.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public abstract class Team {
   public Team() {
      super();
   }

   public boolean func_142054_a(@Nullable Team var1) {
      if (var1 == null) {
         return false;
      } else {
         return this == var1;
      }
   }

   public abstract String func_96661_b();

   public abstract ITextComponent func_200540_a(ITextComponent var1);

   public abstract boolean func_98297_h();

   public abstract boolean func_96665_g();

   public abstract Team.EnumVisible func_178770_i();

   public abstract TextFormatting func_178775_l();

   public abstract Collection<String> func_96670_d();

   public abstract Team.EnumVisible func_178771_j();

   public abstract Team.CollisionRule func_186681_k();

   public static enum CollisionRule {
      ALWAYS("always", 0),
      NEVER("never", 1),
      PUSH_OTHER_TEAMS("pushOtherTeams", 2),
      PUSH_OWN_TEAM("pushOwnTeam", 3);

      private static final Map<String, Team.CollisionRule> field_186695_g = (Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
         return var0.field_186693_e;
      }, (var0) -> {
         return var0;
      }));
      public final String field_186693_e;
      public final int field_186694_f;

      @Nullable
      public static Team.CollisionRule func_186686_a(String var0) {
         return (Team.CollisionRule)field_186695_g.get(var0);
      }

      private CollisionRule(String var3, int var4) {
         this.field_186693_e = var3;
         this.field_186694_f = var4;
      }

      public ITextComponent func_197907_b() {
         return new TextComponentTranslation("team.collision." + this.field_186693_e, new Object[0]);
      }
   }

   public static enum EnumVisible {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      private static final Map<String, Team.EnumVisible> field_186697_g = (Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
         return var0.field_178830_e;
      }, (var0) -> {
         return var0;
      }));
      public final String field_178830_e;
      public final int field_178827_f;

      @Nullable
      public static Team.EnumVisible func_178824_a(String var0) {
         return (Team.EnumVisible)field_186697_g.get(var0);
      }

      private EnumVisible(String var3, int var4) {
         this.field_178830_e = var3;
         this.field_178827_f = var4;
      }

      public ITextComponent func_197910_b() {
         return new TextComponentTranslation("team.visibility." + this.field_178830_e, new Object[0]);
      }
   }
}
