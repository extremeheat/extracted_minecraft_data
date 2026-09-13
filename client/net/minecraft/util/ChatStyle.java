package net.minecraft.util;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;

public class ChatStyle {
   private ChatStyle field_150249_a;
   private EnumChatFormatting field_150247_b;
   private Boolean field_150248_c;
   private Boolean field_150245_d;
   private Boolean field_150246_e;
   private Boolean field_150243_f;
   private Boolean field_150244_g;
   private ClickEvent field_150251_h;
   private HoverEvent field_150252_i;
   private static final ChatStyle field_150250_j = new ChatStyle$1();

   public ChatStyle() {
      super();
   }

   public EnumChatFormatting func_150215_a() {
      return this.field_150247_b == null ? this.func_150224_n().func_150215_a() : this.field_150247_b;
   }

   public boolean func_150223_b() {
      return this.field_150248_c == null ? this.func_150224_n().func_150223_b() : this.field_150248_c;
   }

   public boolean func_150242_c() {
      return this.field_150245_d == null ? this.func_150224_n().func_150242_c() : this.field_150245_d;
   }

   public boolean func_150236_d() {
      return this.field_150243_f == null ? this.func_150224_n().func_150236_d() : this.field_150243_f;
   }

   public boolean func_150234_e() {
      return this.field_150246_e == null ? this.func_150224_n().func_150234_e() : this.field_150246_e;
   }

   public boolean func_150233_f() {
      return this.field_150244_g == null ? this.func_150224_n().func_150233_f() : this.field_150244_g;
   }

   public boolean func_150229_g() {
      return this.field_150248_c == null
         && this.field_150245_d == null
         && this.field_150243_f == null
         && this.field_150246_e == null
         && this.field_150244_g == null
         && this.field_150247_b == null
         && this.field_150251_h == null
         && this.field_150252_i == null;
   }

   public ClickEvent func_150235_h() {
      return this.field_150251_h == null ? this.func_150224_n().func_150235_h() : this.field_150251_h;
   }

   public HoverEvent func_150210_i() {
      return this.field_150252_i == null ? this.func_150224_n().func_150210_i() : this.field_150252_i;
   }

   public ChatStyle func_150238_a(EnumChatFormatting var1) {
      this.field_150247_b = var1;
      return this;
   }

   public ChatStyle func_150227_a(Boolean var1) {
      this.field_150248_c = var1;
      return this;
   }

   public ChatStyle func_150217_b(Boolean var1) {
      this.field_150245_d = var1;
      return this;
   }

   public ChatStyle func_150225_c(Boolean var1) {
      this.field_150243_f = var1;
      return this;
   }

   public ChatStyle func_150228_d(Boolean var1) {
      this.field_150246_e = var1;
      return this;
   }

   public ChatStyle func_150237_e(Boolean var1) {
      this.field_150244_g = var1;
      return this;
   }

   public ChatStyle func_150241_a(ClickEvent var1) {
      this.field_150251_h = var1;
      return this;
   }

   public ChatStyle func_150209_a(HoverEvent var1) {
      this.field_150252_i = var1;
      return this;
   }

   public ChatStyle func_150221_a(ChatStyle var1) {
      this.field_150249_a = var1;
      return this;
   }

   public String func_150218_j() {
      if (this.func_150229_g()) {
         return this.field_150249_a != null ? this.field_150249_a.func_150218_j() : "";
      } else {
         StringBuilder var1 = new StringBuilder();
         if (this.func_150215_a() != null) {
            var1.append(this.func_150215_a());
         }

         if (this.func_150223_b()) {
            var1.append(EnumChatFormatting.BOLD);
         }

         if (this.func_150242_c()) {
            var1.append(EnumChatFormatting.ITALIC);
         }

         if (this.func_150234_e()) {
            var1.append(EnumChatFormatting.UNDERLINE);
         }

         if (this.func_150233_f()) {
            var1.append(EnumChatFormatting.OBFUSCATED);
         }

         if (this.func_150236_d()) {
            var1.append(EnumChatFormatting.STRIKETHROUGH);
         }

         return var1.toString();
      }
   }

   private ChatStyle func_150224_n() {
      return this.field_150249_a == null ? field_150250_j : this.field_150249_a;
   }

   @Override
   public String toString() {
      return "Style{hasParent="
         + (this.field_150249_a != null)
         + ", color="
         + this.field_150247_b
         + ", bold="
         + this.field_150248_c
         + ", italic="
         + this.field_150245_d
         + ", underlined="
         + this.field_150246_e
         + ", obfuscated="
         + this.field_150244_g
         + ", clickEvent="
         + this.func_150235_h()
         + ", hoverEvent="
         + this.func_150210_i()
         + '}';
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ChatStyle)) {
         return false;
      } else {
         ChatStyle var2 = (ChatStyle)var1;
         return this.func_150223_b() == var2.func_150223_b()
            && this.func_150215_a() == var2.func_150215_a()
            && this.func_150242_c() == var2.func_150242_c()
            && this.func_150233_f() == var2.func_150233_f()
            && this.func_150236_d() == var2.func_150236_d()
            && this.func_150234_e() == var2.func_150234_e()
            && (this.func_150235_h() != null ? this.func_150235_h().equals(var2.func_150235_h()) : var2.func_150235_h() == null)
            && (this.func_150210_i() != null ? this.func_150210_i().equals(var2.func_150210_i()) : var2.func_150210_i() == null);
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.field_150247_b.hashCode();
      var1 = 31 * var1 + this.field_150248_c.hashCode();
      var1 = 31 * var1 + this.field_150245_d.hashCode();
      var1 = 31 * var1 + this.field_150246_e.hashCode();
      var1 = 31 * var1 + this.field_150243_f.hashCode();
      var1 = 31 * var1 + this.field_150244_g.hashCode();
      var1 = 31 * var1 + this.field_150251_h.hashCode();
      return 31 * var1 + this.field_150252_i.hashCode();
   }

   public ChatStyle func_150232_l() {
      ChatStyle var1 = new ChatStyle();
      var1.field_150248_c = this.field_150248_c;
      var1.field_150245_d = this.field_150245_d;
      var1.field_150243_f = this.field_150243_f;
      var1.field_150246_e = this.field_150246_e;
      var1.field_150244_g = this.field_150244_g;
      var1.field_150247_b = this.field_150247_b;
      var1.field_150251_h = this.field_150251_h;
      var1.field_150252_i = this.field_150252_i;
      var1.field_150249_a = this.field_150249_a;
      return var1;
   }

   public ChatStyle func_150206_m() {
      ChatStyle var1 = new ChatStyle();
      var1.func_150227_a(this.func_150223_b());
      var1.func_150217_b(this.func_150242_c());
      var1.func_150225_c(this.func_150236_d());
      var1.func_150228_d(this.func_150234_e());
      var1.func_150237_e(this.func_150233_f());
      var1.func_150238_a(this.func_150215_a());
      var1.func_150241_a(this.func_150235_h());
      var1.func_150209_a(this.func_150210_i());
      return var1;
   }
}
