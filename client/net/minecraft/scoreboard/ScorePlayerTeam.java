package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.util.EnumChatFormatting;

public class ScorePlayerTeam extends Team {
   private final Scoreboard field_96677_a;
   private final String field_96675_b;
   private final Set<String> field_96676_c = Sets.newHashSet();
   private String field_96673_d;
   private String field_96674_e = "";
   private String field_96671_f = "";
   private boolean field_96672_g = true;
   private boolean field_98301_h = true;
   private Team.EnumVisible field_178778_i;
   private Team.EnumVisible field_178776_j;
   private EnumChatFormatting field_178777_k;

   public ScorePlayerTeam(Scoreboard var1, String var2) {
      super();
      this.field_178778_i = Team.EnumVisible.ALWAYS;
      this.field_178776_j = Team.EnumVisible.ALWAYS;
      this.field_178777_k = EnumChatFormatting.RESET;
      this.field_96677_a = var1;
      this.field_96675_b = var2;
      this.field_96673_d = var2;
   }

   public String func_96661_b() {
      return this.field_96675_b;
   }

   public String func_96669_c() {
      return this.field_96673_d;
   }

   public void func_96664_a(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Name cannot be null");
      } else {
         this.field_96673_d = var1;
         this.field_96677_a.func_96538_b(this);
      }
   }

   public Collection<String> func_96670_d() {
      return this.field_96676_c;
   }

   public String func_96668_e() {
      return this.field_96674_e;
   }

   public void func_96666_b(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Prefix cannot be null");
      } else {
         this.field_96674_e = var1;
         this.field_96677_a.func_96538_b(this);
      }
   }

   public String func_96663_f() {
      return this.field_96671_f;
   }

   public void func_96662_c(String var1) {
      this.field_96671_f = var1;
      this.field_96677_a.func_96538_b(this);
   }

   public String func_142053_d(String var1) {
      return this.func_96668_e() + var1 + this.func_96663_f();
   }

   public static String func_96667_a(Team var0, String var1) {
      return var0 == null ? var1 : var0.func_142053_d(var1);
   }

   public boolean func_96665_g() {
      return this.field_96672_g;
   }

   public void func_96660_a(boolean var1) {
      this.field_96672_g = var1;
      this.field_96677_a.func_96538_b(this);
   }

   public boolean func_98297_h() {
      return this.field_98301_h;
   }

   public void func_98300_b(boolean var1) {
      this.field_98301_h = var1;
      this.field_96677_a.func_96538_b(this);
   }

   public Team.EnumVisible func_178770_i() {
      return this.field_178778_i;
   }

   public Team.EnumVisible func_178771_j() {
      return this.field_178776_j;
   }

   public void func_178772_a(Team.EnumVisible var1) {
      this.field_178778_i = var1;
      this.field_96677_a.func_96538_b(this);
   }

   public void func_178773_b(Team.EnumVisible var1) {
      this.field_178776_j = var1;
      this.field_96677_a.func_96538_b(this);
   }

   public int func_98299_i() {
      int var1 = 0;
      if (this.func_96665_g()) {
         var1 |= 1;
      }

      if (this.func_98297_h()) {
         var1 |= 2;
      }

      return var1;
   }

   public void func_98298_a(int var1) {
      this.func_96660_a((var1 & 1) > 0);
      this.func_98300_b((var1 & 2) > 0);
   }

   public void func_178774_a(EnumChatFormatting var1) {
      this.field_178777_k = var1;
   }

   public EnumChatFormatting func_178775_l() {
      return this.field_178777_k;
   }
}
