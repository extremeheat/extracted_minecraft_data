package net.minecraft.command.arguments;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.MinMaxBoundsWrapped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;

public class EntitySelectorParser {
   public static final SimpleCommandExceptionType field_197408_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.invalid", new Object[0]));
   public static final DynamicCommandExceptionType field_197409_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.entity.selector.unknown", new Object[]{var0});
   });
   public static final SimpleCommandExceptionType field_210328_c = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.selector.not_allowed", new Object[0]));
   public static final SimpleCommandExceptionType field_197410_c = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.selector.missing", new Object[0]));
   public static final SimpleCommandExceptionType field_197411_d = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.options.unterminated", new Object[0]));
   public static final DynamicCommandExceptionType field_197412_e = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.entity.options.valueless", new Object[]{var0});
   });
   public static final BiConsumer<Vec3d, List<? extends Entity>> field_197413_f = (var0, var1) -> {
   };
   public static final BiConsumer<Vec3d, List<? extends Entity>> field_197414_g = (var0, var1) -> {
      var1.sort((var1x, var2) -> {
         return Doubles.compare(var1x.func_195048_a(var0), var2.func_195048_a(var0));
      });
   };
   public static final BiConsumer<Vec3d, List<? extends Entity>> field_197415_h = (var0, var1) -> {
      var1.sort((var1x, var2) -> {
         return Doubles.compare(var2.func_195048_a(var0), var1x.func_195048_a(var0));
      });
   };
   public static final BiConsumer<Vec3d, List<? extends Entity>> field_197416_i = (var0, var1) -> {
      Collections.shuffle(var1);
   };
   public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> field_201355_j = (var0, var1) -> {
      return var0.buildFuture();
   };
   private final StringReader field_197417_j;
   private final boolean field_210329_m;
   private int field_197418_k;
   private boolean field_197419_l;
   private boolean field_197420_m;
   private MinMaxBounds.FloatBound field_197421_n;
   private MinMaxBounds.IntBound field_197422_o;
   @Nullable
   private Double field_197423_p;
   @Nullable
   private Double field_197424_q;
   @Nullable
   private Double field_197425_r;
   @Nullable
   private Double field_197426_s;
   @Nullable
   private Double field_197427_t;
   @Nullable
   private Double field_197428_u;
   private MinMaxBoundsWrapped field_197429_v;
   private MinMaxBoundsWrapped field_197430_w;
   private Predicate<Entity> field_197431_x;
   private BiConsumer<Vec3d, List<? extends Entity>> field_197432_y;
   private boolean field_197433_z;
   @Nullable
   private String field_197406_A;
   private int field_201999_C;
   @Nullable
   private UUID field_197407_B;
   private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> field_201354_D;
   private boolean field_202000_F;
   private boolean field_202001_G;
   private boolean field_202002_H;
   private boolean field_202003_I;
   private boolean field_202004_J;
   private boolean field_202005_K;
   private boolean field_202006_L;
   private boolean field_202007_M;
   private Class<? extends Entity> field_202008_N;
   private boolean field_202009_O;
   private boolean field_202010_P;
   private boolean field_202011_Q;
   private boolean field_210327_T;

   public EntitySelectorParser(StringReader var1) {
      this(var1, true);
   }

   public EntitySelectorParser(StringReader var1, boolean var2) {
      super();
      this.field_197421_n = MinMaxBounds.FloatBound.field_211359_e;
      this.field_197422_o = MinMaxBounds.IntBound.field_211347_e;
      this.field_197429_v = MinMaxBoundsWrapped.field_207926_a;
      this.field_197430_w = MinMaxBoundsWrapped.field_207926_a;
      this.field_197431_x = (var0) -> {
         return true;
      };
      this.field_197432_y = field_197413_f;
      this.field_201354_D = field_201355_j;
      this.field_197417_j = var1;
      this.field_210329_m = var2;
   }

   public EntitySelector func_197400_a() {
      AxisAlignedBB var1;
      if (this.field_197426_s == null && this.field_197427_t == null && this.field_197428_u == null) {
         if (this.field_197421_n.func_196977_b() != null) {
            float var2 = (Float)this.field_197421_n.func_196977_b();
            var1 = new AxisAlignedBB((double)(-var2), (double)(-var2), (double)(-var2), (double)(var2 + 1.0F), (double)(var2 + 1.0F), (double)(var2 + 1.0F));
         } else {
            var1 = null;
         }
      } else {
         var1 = this.func_197390_a(this.field_197426_s == null ? 0.0D : this.field_197426_s, this.field_197427_t == null ? 0.0D : this.field_197427_t, this.field_197428_u == null ? 0.0D : this.field_197428_u);
      }

      Function var3;
      if (this.field_197423_p == null && this.field_197424_q == null && this.field_197425_r == null) {
         var3 = (var0) -> {
            return var0;
         };
      } else {
         var3 = (var1x) -> {
            return new Vec3d(this.field_197423_p == null ? var1x.field_72450_a : this.field_197423_p, this.field_197424_q == null ? var1x.field_72448_b : this.field_197424_q, this.field_197425_r == null ? var1x.field_72449_c : this.field_197425_r);
         };
      }

      return new EntitySelector(this.field_197418_k, this.field_197419_l, this.field_197420_m, this.field_197431_x, this.field_197421_n, var3, var1, this.field_197432_y, this.field_197433_z, this.field_197406_A, this.field_197407_B, this.field_202008_N == null ? Entity.class : this.field_202008_N, this.field_210327_T);
   }

   private AxisAlignedBB func_197390_a(double var1, double var3, double var5) {
      boolean var7 = var1 < 0.0D;
      boolean var8 = var3 < 0.0D;
      boolean var9 = var5 < 0.0D;
      double var10 = var7 ? var1 : 0.0D;
      double var12 = var8 ? var3 : 0.0D;
      double var14 = var9 ? var5 : 0.0D;
      double var16 = (var7 ? 0.0D : var1) + 1.0D;
      double var18 = (var8 ? 0.0D : var3) + 1.0D;
      double var20 = (var9 ? 0.0D : var5) + 1.0D;
      return new AxisAlignedBB(var10, var12, var14, var16, var18, var20);
   }

   private void func_197396_n() {
      if (this.field_197429_v != MinMaxBoundsWrapped.field_207926_a) {
         this.field_197431_x = this.field_197431_x.and(this.func_197366_a(this.field_197429_v, (var0) -> {
            return (double)var0.field_70125_A;
         }));
      }

      if (this.field_197430_w != MinMaxBoundsWrapped.field_207926_a) {
         this.field_197431_x = this.field_197431_x.and(this.func_197366_a(this.field_197430_w, (var0) -> {
            return (double)var0.field_70177_z;
         }));
      }

      if (!this.field_197422_o.func_211335_c()) {
         this.field_197431_x = this.field_197431_x.and((var1) -> {
            return !(var1 instanceof EntityPlayerMP) ? false : this.field_197422_o.func_211339_d(((EntityPlayerMP)var1).field_71068_ca);
         });
      }

   }

   private Predicate<Entity> func_197366_a(MinMaxBoundsWrapped var1, ToDoubleFunction<Entity> var2) {
      double var3 = (double)MathHelper.func_76142_g(var1.func_207923_a() == null ? 0.0F : var1.func_207923_a());
      double var5 = (double)MathHelper.func_76142_g(var1.func_207925_b() == null ? 359.0F : var1.func_207925_b());
      return (var5x) -> {
         double var6 = MathHelper.func_76138_g(var2.applyAsDouble(var5x));
         if (var3 > var5) {
            return var6 >= var3 || var6 <= var5;
         } else {
            return var6 >= var3 && var6 <= var5;
         }
      };
   }

   protected void func_197403_b() throws CommandSyntaxException {
      this.field_210327_T = true;
      this.field_201354_D = this::func_201959_d;
      if (!this.field_197417_j.canRead()) {
         throw field_197410_c.createWithContext(this.field_197417_j);
      } else {
         int var1 = this.field_197417_j.getCursor();
         char var2 = this.field_197417_j.read();
         if (var2 == 'p') {
            this.field_197418_k = 1;
            this.field_197419_l = false;
            this.field_197432_y = field_197414_g;
            this.func_201964_a(EntityPlayerMP.class);
         } else if (var2 == 'a') {
            this.field_197418_k = 2147483647;
            this.field_197419_l = false;
            this.field_197432_y = field_197413_f;
            this.func_201964_a(EntityPlayerMP.class);
         } else if (var2 == 'r') {
            this.field_197418_k = 1;
            this.field_197419_l = false;
            this.field_197432_y = field_197416_i;
            this.func_201964_a(EntityPlayerMP.class);
         } else if (var2 == 's') {
            this.field_197418_k = 1;
            this.field_197419_l = true;
            this.field_197433_z = true;
         } else {
            if (var2 != 'e') {
               this.field_197417_j.setCursor(var1);
               throw field_197409_b.createWithContext(this.field_197417_j, '@' + String.valueOf(var2));
            }

            this.field_197418_k = 2147483647;
            this.field_197419_l = true;
            this.field_197432_y = field_197413_f;
            this.field_197431_x = Entity::func_70089_S;
         }

         this.field_201354_D = this::func_201989_e;
         if (this.field_197417_j.canRead() && this.field_197417_j.peek() == '[') {
            this.field_197417_j.skip();
            this.field_201354_D = this::func_201996_f;
            this.func_197404_d();
         }

      }
   }

   protected void func_197382_c() throws CommandSyntaxException {
      if (this.field_197417_j.canRead()) {
         this.field_201354_D = this::func_201974_c;
      }

      int var1 = this.field_197417_j.getCursor();
      String var2 = this.field_197417_j.readString();

      try {
         this.field_197407_B = UUID.fromString(var2);
         this.field_197419_l = true;
      } catch (IllegalArgumentException var4) {
         if (var2.isEmpty() || var2.length() > 16) {
            this.field_197417_j.setCursor(var1);
            throw field_197408_a.createWithContext(this.field_197417_j);
         }

         this.field_197419_l = false;
         this.field_197406_A = var2;
      }

      this.field_197418_k = 1;
   }

   protected void func_197404_d() throws CommandSyntaxException {
      this.field_201354_D = this::func_201994_g;
      this.field_197417_j.skipWhitespace();

      while(this.field_197417_j.canRead() && this.field_197417_j.peek() != ']') {
         this.field_197417_j.skipWhitespace();
         int var1 = this.field_197417_j.getCursor();
         String var2 = this.field_197417_j.readString();
         EntityOptions.Filter var3 = EntityOptions.func_202017_a(this, var2, var1);
         this.field_197417_j.skipWhitespace();
         if (this.field_197417_j.canRead() && this.field_197417_j.peek() == '=') {
            this.field_197417_j.skip();
            this.field_197417_j.skipWhitespace();
            this.field_201354_D = field_201355_j;
            var3.handle(this);
            this.field_197417_j.skipWhitespace();
            this.field_201354_D = this::func_201969_h;
            if (!this.field_197417_j.canRead()) {
               continue;
            }

            if (this.field_197417_j.peek() == ',') {
               this.field_197417_j.skip();
               this.field_201354_D = this::func_201994_g;
               continue;
            }

            if (this.field_197417_j.peek() != ']') {
               throw field_197411_d.createWithContext(this.field_197417_j);
            }
            break;
         }

         this.field_197417_j.setCursor(var1);
         throw field_197412_e.createWithContext(this.field_197417_j, var2);
      }

      if (this.field_197417_j.canRead()) {
         this.field_197417_j.skip();
         this.field_201354_D = field_201355_j;
      } else {
         throw field_197411_d.createWithContext(this.field_197417_j);
      }
   }

   public boolean func_197378_e() {
      this.field_197417_j.skipWhitespace();
      if (this.field_197417_j.canRead() && this.field_197417_j.peek() == '!') {
         this.field_197417_j.skip();
         this.field_197417_j.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public StringReader func_197398_f() {
      return this.field_197417_j;
   }

   public void func_197401_a(Predicate<Entity> var1) {
      this.field_197431_x = this.field_197431_x.and(var1);
   }

   public void func_197365_g() {
      this.field_197420_m = true;
   }

   public MinMaxBounds.FloatBound func_197370_h() {
      return this.field_197421_n;
   }

   public void func_197397_a(MinMaxBounds.FloatBound var1) {
      this.field_197421_n = var1;
   }

   public MinMaxBounds.IntBound func_197394_i() {
      return this.field_197422_o;
   }

   public void func_197399_b(MinMaxBounds.IntBound var1) {
      this.field_197422_o = var1;
   }

   public MinMaxBoundsWrapped func_201968_j() {
      return this.field_197429_v;
   }

   public void func_197389_c(MinMaxBoundsWrapped var1) {
      this.field_197429_v = var1;
   }

   public MinMaxBoundsWrapped func_201980_k() {
      return this.field_197430_w;
   }

   public void func_197387_d(MinMaxBoundsWrapped var1) {
      this.field_197430_w = var1;
   }

   @Nullable
   public Double func_201965_l() {
      return this.field_197423_p;
   }

   @Nullable
   public Double func_201991_m() {
      return this.field_197424_q;
   }

   @Nullable
   public Double func_201983_n() {
      return this.field_197425_r;
   }

   public void func_197384_a(double var1) {
      this.field_197423_p = var1;
   }

   public void func_197395_b(double var1) {
      this.field_197424_q = var1;
   }

   public void func_197372_c(double var1) {
      this.field_197425_r = var1;
   }

   public void func_197377_d(double var1) {
      this.field_197426_s = var1;
   }

   public void func_197391_e(double var1) {
      this.field_197427_t = var1;
   }

   public void func_197405_f(double var1) {
      this.field_197428_u = var1;
   }

   @Nullable
   public Double func_201977_o() {
      return this.field_197426_s;
   }

   @Nullable
   public Double func_201971_p() {
      return this.field_197427_t;
   }

   @Nullable
   public Double func_201962_q() {
      return this.field_197428_u;
   }

   public void func_197388_a(int var1) {
      this.field_197418_k = var1;
   }

   public void func_197373_a(boolean var1) {
      this.field_197419_l = var1;
   }

   public void func_197376_a(BiConsumer<Vec3d, List<? extends Entity>> var1) {
      this.field_197432_y = var1;
   }

   public EntitySelector func_201345_m() throws CommandSyntaxException {
      this.field_201999_C = this.field_197417_j.getCursor();
      this.field_201354_D = this::func_201981_b;
      if (this.field_197417_j.canRead() && this.field_197417_j.peek() == '@') {
         if (!this.field_210329_m) {
            throw field_210328_c.createWithContext(this.field_197417_j);
         }

         this.field_197417_j.skip();
         this.func_197403_b();
      } else {
         this.func_197382_c();
      }

      this.func_197396_n();
      return this.func_197400_a();
   }

   private static void func_210326_a(SuggestionsBuilder var0) {
      var0.suggest("@p", new TextComponentTranslation("argument.entity.selector.nearestPlayer", new Object[0]));
      var0.suggest("@a", new TextComponentTranslation("argument.entity.selector.allPlayers", new Object[0]));
      var0.suggest("@r", new TextComponentTranslation("argument.entity.selector.randomPlayer", new Object[0]));
      var0.suggest("@s", new TextComponentTranslation("argument.entity.selector.self", new Object[0]));
      var0.suggest("@e", new TextComponentTranslation("argument.entity.selector.allEntities", new Object[0]));
   }

   private CompletableFuture<Suggestions> func_201981_b(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      var2.accept(var1);
      if (this.field_210329_m) {
         func_210326_a(var1);
      }

      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> func_201974_c(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      SuggestionsBuilder var3 = var1.createOffset(this.field_201999_C);
      var2.accept(var3);
      return var1.add(var3).buildFuture();
   }

   private CompletableFuture<Suggestions> func_201959_d(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      SuggestionsBuilder var3 = var1.createOffset(var1.getStart() - 1);
      func_210326_a(var3);
      var1.add(var3);
      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> func_201989_e(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      var1.suggest(String.valueOf('['));
      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> func_201996_f(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      var1.suggest(String.valueOf(']'));
      EntityOptions.func_202049_a(this, var1);
      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> func_201994_g(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      EntityOptions.func_202049_a(this, var1);
      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> func_201969_h(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      var1.suggest(String.valueOf(','));
      var1.suggest(String.valueOf(']'));
      return var1.buildFuture();
   }

   public boolean func_197381_m() {
      return this.field_197433_z;
   }

   public void func_201978_a(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> var1) {
      this.field_201354_D = var1;
   }

   public CompletableFuture<Suggestions> func_201993_a(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      return (CompletableFuture)this.field_201354_D.apply(var1.createOffset(this.field_197417_j.getCursor()), var2);
   }

   public boolean func_201984_u() {
      return this.field_202000_F;
   }

   public void func_201990_c(boolean var1) {
      this.field_202000_F = var1;
   }

   public boolean func_201997_v() {
      return this.field_202001_G;
   }

   public void func_201998_d(boolean var1) {
      this.field_202001_G = var1;
   }

   public boolean func_201967_w() {
      return this.field_202002_H;
   }

   public void func_201979_e(boolean var1) {
      this.field_202002_H = var1;
   }

   public boolean func_201976_x() {
      return this.field_202003_I;
   }

   public void func_201986_f(boolean var1) {
      this.field_202003_I = var1;
   }

   public boolean func_201987_y() {
      return this.field_202004_J;
   }

   public void func_201988_g(boolean var1) {
      this.field_202004_J = var1;
   }

   public boolean func_201961_z() {
      return this.field_202005_K;
   }

   public void func_201973_h(boolean var1) {
      this.field_202005_K = var1;
   }

   public boolean func_201960_A() {
      return this.field_202006_L;
   }

   public void func_201975_i(boolean var1) {
      this.field_202006_L = var1;
   }

   public void func_201958_j(boolean var1) {
      this.field_202007_M = var1;
   }

   public void func_201964_a(Class<? extends Entity> var1) {
      this.field_202008_N = var1;
   }

   public void func_201982_C() {
      this.field_202009_O = true;
   }

   public boolean func_201963_E() {
      return this.field_202008_N != null;
   }

   public boolean func_201985_F() {
      return this.field_202009_O;
   }

   public boolean func_201995_G() {
      return this.field_202010_P;
   }

   public void func_201970_k(boolean var1) {
      this.field_202010_P = var1;
   }

   public boolean func_201966_H() {
      return this.field_202011_Q;
   }

   public void func_201992_l(boolean var1) {
      this.field_202011_Q = var1;
   }
}
