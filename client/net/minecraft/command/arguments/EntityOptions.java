package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.MinMaxBoundsWrapped;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

public class EntityOptions {
   private static final Map<String, EntityOptions.IOptionHandler> field_197478_k = Maps.newHashMap();
   public static final DynamicCommandExceptionType field_197468_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.entity.options.unknown", new Object[]{var0});
   });
   public static final DynamicCommandExceptionType field_202058_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.entity.options.inapplicable", new Object[]{var0});
   });
   public static final SimpleCommandExceptionType field_197469_b = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.options.distance.negative", new Object[0]));
   public static final SimpleCommandExceptionType field_197471_d = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.options.level.negative", new Object[0]));
   public static final SimpleCommandExceptionType field_197472_e = new SimpleCommandExceptionType(new TextComponentTranslation("argument.entity.options.limit.toosmall", new Object[0]));
   public static final DynamicCommandExceptionType field_197475_h = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.entity.options.sort.irreversible", new Object[]{var0});
   });
   public static final DynamicCommandExceptionType field_197476_i = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.entity.options.mode.invalid", new Object[]{var0});
   });
   public static final DynamicCommandExceptionType field_197477_j = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.entity.options.type.invalid", new Object[]{var0});
   });

   private static void func_202024_a(String var0, EntityOptions.Filter var1, Predicate<EntitySelectorParser> var2, ITextComponent var3) {
      field_197478_k.put(var0, new EntityOptions.IOptionHandler(var1, var2, var3));
   }

   public static void func_197445_a() {
      if (field_197478_k.isEmpty()) {
         func_202024_a("name", (var0) -> {
            int var1 = var0.func_197398_f().getCursor();
            boolean var2 = var0.func_197378_e();
            String var3 = var0.func_197398_f().readString();
            if (var0.func_201997_v() && !var2) {
               var0.func_197398_f().setCursor(var1);
               throw field_202058_b.createWithContext(var0.func_197398_f(), "name");
            } else {
               if (var2) {
                  var0.func_201998_d(true);
               } else {
                  var0.func_201990_c(true);
               }

               var0.func_197401_a((var2x) -> {
                  return var2x.func_200200_C_().func_150261_e().equals(var3) != var2;
               });
            }
         }, (var0) -> {
            return !var0.func_201984_u();
         }, new TextComponentTranslation("argument.entity.options.name.description", new Object[0]));
         func_202024_a("distance", (var0) -> {
            int var1 = var0.func_197398_f().getCursor();
            MinMaxBounds.FloatBound var2 = MinMaxBounds.FloatBound.func_211357_a(var0.func_197398_f());
            if ((var2.func_196973_a() == null || (Float)var2.func_196973_a() >= 0.0F) && (var2.func_196977_b() == null || (Float)var2.func_196977_b() >= 0.0F)) {
               var0.func_197397_a(var2);
               var0.func_197365_g();
            } else {
               var0.func_197398_f().setCursor(var1);
               throw field_197469_b.createWithContext(var0.func_197398_f());
            }
         }, (var0) -> {
            return var0.func_197370_h().func_211335_c();
         }, new TextComponentTranslation("argument.entity.options.distance.description", new Object[0]));
         func_202024_a("level", (var0) -> {
            int var1 = var0.func_197398_f().getCursor();
            MinMaxBounds.IntBound var2 = MinMaxBounds.IntBound.func_211342_a(var0.func_197398_f());
            if ((var2.func_196973_a() == null || (Integer)var2.func_196973_a() >= 0) && (var2.func_196977_b() == null || (Integer)var2.func_196977_b() >= 0)) {
               var0.func_197399_b(var2);
               var0.func_197373_a(false);
            } else {
               var0.func_197398_f().setCursor(var1);
               throw field_197471_d.createWithContext(var0.func_197398_f());
            }
         }, (var0) -> {
            return var0.func_197394_i().func_211335_c();
         }, new TextComponentTranslation("argument.entity.options.level.description", new Object[0]));
         func_202024_a("x", (var0) -> {
            var0.func_197365_g();
            var0.func_197384_a(var0.func_197398_f().readDouble());
         }, (var0) -> {
            return var0.func_201965_l() == null;
         }, new TextComponentTranslation("argument.entity.options.x.description", new Object[0]));
         func_202024_a("y", (var0) -> {
            var0.func_197365_g();
            var0.func_197395_b(var0.func_197398_f().readDouble());
         }, (var0) -> {
            return var0.func_201991_m() == null;
         }, new TextComponentTranslation("argument.entity.options.y.description", new Object[0]));
         func_202024_a("z", (var0) -> {
            var0.func_197365_g();
            var0.func_197372_c(var0.func_197398_f().readDouble());
         }, (var0) -> {
            return var0.func_201983_n() == null;
         }, new TextComponentTranslation("argument.entity.options.z.description", new Object[0]));
         func_202024_a("dx", (var0) -> {
            var0.func_197365_g();
            var0.func_197377_d(var0.func_197398_f().readDouble());
         }, (var0) -> {
            return var0.func_201977_o() == null;
         }, new TextComponentTranslation("argument.entity.options.dx.description", new Object[0]));
         func_202024_a("dy", (var0) -> {
            var0.func_197365_g();
            var0.func_197391_e(var0.func_197398_f().readDouble());
         }, (var0) -> {
            return var0.func_201971_p() == null;
         }, new TextComponentTranslation("argument.entity.options.dy.description", new Object[0]));
         func_202024_a("dz", (var0) -> {
            var0.func_197365_g();
            var0.func_197405_f(var0.func_197398_f().readDouble());
         }, (var0) -> {
            return var0.func_201962_q() == null;
         }, new TextComponentTranslation("argument.entity.options.dz.description", new Object[0]));
         func_202024_a("x_rotation", (var0) -> {
            var0.func_197389_c(MinMaxBoundsWrapped.func_207921_a(var0.func_197398_f(), true, MathHelper::func_76142_g));
         }, (var0) -> {
            return var0.func_201968_j() == MinMaxBoundsWrapped.field_207926_a;
         }, new TextComponentTranslation("argument.entity.options.x_rotation.description", new Object[0]));
         func_202024_a("y_rotation", (var0) -> {
            var0.func_197387_d(MinMaxBoundsWrapped.func_207921_a(var0.func_197398_f(), true, MathHelper::func_76142_g));
         }, (var0) -> {
            return var0.func_201980_k() == MinMaxBoundsWrapped.field_207926_a;
         }, new TextComponentTranslation("argument.entity.options.y_rotation.description", new Object[0]));
         func_202024_a("limit", (var0) -> {
            int var1 = var0.func_197398_f().getCursor();
            int var2 = var0.func_197398_f().readInt();
            if (var2 < 1) {
               var0.func_197398_f().setCursor(var1);
               throw field_197472_e.createWithContext(var0.func_197398_f());
            } else {
               var0.func_197388_a(var2);
               var0.func_201979_e(true);
            }
         }, (var0) -> {
            return !var0.func_197381_m() && !var0.func_201967_w();
         }, new TextComponentTranslation("argument.entity.options.limit.description", new Object[0]));
         func_202024_a("sort", (var0) -> {
            int var1 = var0.func_197398_f().getCursor();
            String var2 = var0.func_197398_f().readUnquotedString();
            var0.func_201978_a((var0x, var1x) -> {
               return ISuggestionProvider.func_197005_b(Arrays.asList("nearest", "furthest", "random", "arbitrary"), var0x);
            });
            byte var5 = -1;
            switch(var2.hashCode()) {
            case -938285885:
               if (var2.equals("random")) {
                  var5 = 2;
               }
               break;
            case 1510793967:
               if (var2.equals("furthest")) {
                  var5 = 1;
               }
               break;
            case 1780188658:
               if (var2.equals("arbitrary")) {
                  var5 = 3;
               }
               break;
            case 1825779806:
               if (var2.equals("nearest")) {
                  var5 = 0;
               }
            }

            BiConsumer var3;
            switch(var5) {
            case 0:
               var3 = EntitySelectorParser.field_197414_g;
               break;
            case 1:
               var3 = EntitySelectorParser.field_197415_h;
               break;
            case 2:
               var3 = EntitySelectorParser.field_197416_i;
               break;
            case 3:
               var3 = EntitySelectorParser.field_197413_f;
               break;
            default:
               var0.func_197398_f().setCursor(var1);
               throw field_197475_h.createWithContext(var0.func_197398_f(), var2);
            }

            var0.func_197376_a(var3);
            var0.func_201986_f(true);
         }, (var0) -> {
            return !var0.func_197381_m() && !var0.func_201976_x();
         }, new TextComponentTranslation("argument.entity.options.sort.description", new Object[0]));
         func_202024_a("gamemode", (var0) -> {
            var0.func_201978_a((var1x, var2x) -> {
               String var3 = var1x.getRemaining().toLowerCase(Locale.ROOT);
               boolean var4 = !var0.func_201961_z();
               boolean var5 = true;
               if (!var3.isEmpty()) {
                  if (var3.charAt(0) == '!') {
                     var4 = false;
                     var3 = var3.substring(1);
                  } else {
                     var5 = false;
                  }
               }

               GameType[] var6 = GameType.values();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  GameType var9 = var6[var8];
                  if (var9 != GameType.NOT_SET && var9.func_77149_b().toLowerCase(Locale.ROOT).startsWith(var3)) {
                     if (var5) {
                        var1x.suggest('!' + var9.func_77149_b());
                     }

                     if (var4) {
                        var1x.suggest(var9.func_77149_b());
                     }
                  }
               }

               return var1x.buildFuture();
            });
            int var1 = var0.func_197398_f().getCursor();
            boolean var2 = var0.func_197378_e();
            if (var0.func_201961_z() && !var2) {
               var0.func_197398_f().setCursor(var1);
               throw field_202058_b.createWithContext(var0.func_197398_f(), "gamemode");
            } else {
               String var3 = var0.func_197398_f().readUnquotedString();
               GameType var4 = GameType.func_185328_a(var3, GameType.NOT_SET);
               if (var4 == GameType.NOT_SET) {
                  var0.func_197398_f().setCursor(var1);
                  throw field_197476_i.createWithContext(var0.func_197398_f(), var3);
               } else {
                  var0.func_197373_a(false);
                  var0.func_197401_a((var2x) -> {
                     if (!(var2x instanceof EntityPlayerMP)) {
                        return false;
                     } else {
                        GameType var3 = ((EntityPlayerMP)var2x).field_71134_c.func_73081_b();
                        return var2 ? var3 != var4 : var3 == var4;
                     }
                  });
                  if (var2) {
                     var0.func_201973_h(true);
                  } else {
                     var0.func_201988_g(true);
                  }

               }
            }
         }, (var0) -> {
            return !var0.func_201987_y();
         }, new TextComponentTranslation("argument.entity.options.gamemode.description", new Object[0]));
         func_202024_a("team", (var0) -> {
            boolean var1 = var0.func_197378_e();
            String var2 = var0.func_197398_f().readUnquotedString();
            var0.func_197401_a((var2x) -> {
               if (!(var2x instanceof EntityLivingBase)) {
                  return false;
               } else {
                  Team var3 = var2x.func_96124_cp();
                  String var4 = var3 == null ? "" : var3.func_96661_b();
                  return var4.equals(var2) != var1;
               }
            });
            if (var1) {
               var0.func_201958_j(true);
            } else {
               var0.func_201975_i(true);
            }

         }, (var0) -> {
            return !var0.func_201960_A();
         }, new TextComponentTranslation("argument.entity.options.team.description", new Object[0]));
         func_202024_a("type", (var0) -> {
            var0.func_201978_a((var1x, var2x) -> {
               ISuggestionProvider.func_197006_a(IRegistry.field_212629_r.func_148742_b(), var1x, String.valueOf('!'));
               if (!var0.func_201985_F()) {
                  ISuggestionProvider.func_197014_a(IRegistry.field_212629_r.func_148742_b(), var1x);
               }

               return var1x.buildFuture();
            });
            int var1 = var0.func_197398_f().getCursor();
            boolean var2 = var0.func_197378_e();
            if (var0.func_201985_F() && !var2) {
               var0.func_197398_f().setCursor(var1);
               throw field_202058_b.createWithContext(var0.func_197398_f(), "type");
            } else {
               ResourceLocation var3 = ResourceLocation.func_195826_a(var0.func_197398_f());
               EntityType var4 = (EntityType)IRegistry.field_212629_r.func_212608_b(var3);
               if (var4 == null) {
                  var0.func_197398_f().setCursor(var1);
                  throw field_197477_j.createWithContext(var0.func_197398_f(), var3.toString());
               } else {
                  if (Objects.equals(EntityType.field_200729_aH, var4) && !var2) {
                     var0.func_197373_a(false);
                  }

                  var0.func_197401_a((var2x) -> {
                     return Objects.equals(var4, var2x.func_200600_R()) != var2;
                  });
                  if (var2) {
                     var0.func_201982_C();
                  } else {
                     var0.func_201964_a(var4.func_201760_c());
                  }

               }
            }
         }, (var0) -> {
            return !var0.func_201963_E();
         }, new TextComponentTranslation("argument.entity.options.type.description", new Object[0]));
         func_202024_a("tag", (var0) -> {
            boolean var1 = var0.func_197378_e();
            String var2 = var0.func_197398_f().readUnquotedString();
            var0.func_197401_a((var2x) -> {
               if ("".equals(var2)) {
                  return var2x.func_184216_O().isEmpty() != var1;
               } else {
                  return var2x.func_184216_O().contains(var2) != var1;
               }
            });
         }, (var0) -> {
            return true;
         }, new TextComponentTranslation("argument.entity.options.tag.description", new Object[0]));
         func_202024_a("nbt", (var0) -> {
            boolean var1 = var0.func_197378_e();
            NBTTagCompound var2 = (new JsonToNBT(var0.func_197398_f())).func_193593_f();
            var0.func_197401_a((var2x) -> {
               NBTTagCompound var3 = var2x.func_189511_e(new NBTTagCompound());
               if (var2x instanceof EntityPlayerMP) {
                  ItemStack var4 = ((EntityPlayerMP)var2x).field_71071_by.func_70448_g();
                  if (!var4.func_190926_b()) {
                     var3.func_74782_a("SelectedItem", var4.func_77955_b(new NBTTagCompound()));
                  }
               }

               return NBTUtil.func_181123_a(var2, var3, true) != var1;
            });
         }, (var0) -> {
            return true;
         }, new TextComponentTranslation("argument.entity.options.nbt.description", new Object[0]));
         func_202024_a("scores", (var0) -> {
            StringReader var1 = var0.func_197398_f();
            HashMap var2 = Maps.newHashMap();
            var1.expect('{');
            var1.skipWhitespace();

            while(var1.canRead() && var1.peek() != '}') {
               var1.skipWhitespace();
               String var3 = var1.readUnquotedString();
               var1.skipWhitespace();
               var1.expect('=');
               var1.skipWhitespace();
               MinMaxBounds.IntBound var4 = MinMaxBounds.IntBound.func_211342_a(var1);
               var2.put(var3, var4);
               var1.skipWhitespace();
               if (var1.canRead() && var1.peek() == ',') {
                  var1.skip();
               }
            }

            var1.expect('}');
            if (!var2.isEmpty()) {
               var0.func_197401_a((var1x) -> {
                  ServerScoreboard var2x = var1x.func_184102_h().func_200251_aP();
                  String var3 = var1x.func_195047_I_();
                  Iterator var4 = var2.entrySet().iterator();

                  Entry var5;
                  int var8;
                  do {
                     if (!var4.hasNext()) {
                        return true;
                     }

                     var5 = (Entry)var4.next();
                     ScoreObjective var6 = var2x.func_96518_b((String)var5.getKey());
                     if (var6 == null) {
                        return false;
                     }

                     if (!var2x.func_178819_b(var3, var6)) {
                        return false;
                     }

                     Score var7 = var2x.func_96529_a(var3, var6);
                     var8 = var7.func_96652_c();
                  } while(((MinMaxBounds.IntBound)var5.getValue()).func_211339_d(var8));

                  return false;
               });
            }

            var0.func_201970_k(true);
         }, (var0) -> {
            return !var0.func_201995_G();
         }, new TextComponentTranslation("argument.entity.options.scores.description", new Object[0]));
         func_202024_a("advancements", (var0) -> {
            StringReader var1 = var0.func_197398_f();
            HashMap var2 = Maps.newHashMap();
            var1.expect('{');
            var1.skipWhitespace();

            while(var1.canRead() && var1.peek() != '}') {
               var1.skipWhitespace();
               ResourceLocation var3 = ResourceLocation.func_195826_a(var1);
               var1.skipWhitespace();
               var1.expect('=');
               var1.skipWhitespace();
               if (var1.canRead() && var1.peek() == '{') {
                  HashMap var7 = Maps.newHashMap();
                  var1.skipWhitespace();
                  var1.expect('{');
                  var1.skipWhitespace();

                  while(var1.canRead() && var1.peek() != '}') {
                     var1.skipWhitespace();
                     String var5 = var1.readUnquotedString();
                     var1.skipWhitespace();
                     var1.expect('=');
                     var1.skipWhitespace();
                     boolean var6 = var1.readBoolean();
                     var7.put(var5, (var1x) -> {
                        return var1x.func_192151_a() == var6;
                     });
                     var1.skipWhitespace();
                     if (var1.canRead() && var1.peek() == ',') {
                        var1.skip();
                     }
                  }

                  var1.skipWhitespace();
                  var1.expect('}');
                  var1.skipWhitespace();
                  var2.put(var3, (var1x) -> {
                     Iterator var2 = var7.entrySet().iterator();

                     Entry var3;
                     CriterionProgress var4;
                     do {
                        if (!var2.hasNext()) {
                           return true;
                        }

                        var3 = (Entry)var2.next();
                        var4 = var1x.func_192106_c((String)var3.getKey());
                     } while(var4 != null && ((Predicate)var3.getValue()).test(var4));

                     return false;
                  });
               } else {
                  boolean var4 = var1.readBoolean();
                  var2.put(var3, (var1x) -> {
                     return var1x.func_192105_a() == var4;
                  });
               }

               var1.skipWhitespace();
               if (var1.canRead() && var1.peek() == ',') {
                  var1.skip();
               }
            }

            var1.expect('}');
            if (!var2.isEmpty()) {
               var0.func_197401_a((var1x) -> {
                  if (!(var1x instanceof EntityPlayerMP)) {
                     return false;
                  } else {
                     EntityPlayerMP var2x = (EntityPlayerMP)var1x;
                     PlayerAdvancements var3 = var2x.func_192039_O();
                     AdvancementManager var4 = var2x.func_184102_h().func_191949_aK();
                     Iterator var5 = var2.entrySet().iterator();

                     Entry var6;
                     Advancement var7;
                     do {
                        if (!var5.hasNext()) {
                           return true;
                        }

                        var6 = (Entry)var5.next();
                        var7 = var4.func_192778_a((ResourceLocation)var6.getKey());
                     } while(var7 != null && ((Predicate)var6.getValue()).test(var3.func_192747_a(var7)));

                     return false;
                  }
               });
               var0.func_197373_a(false);
            }

            var0.func_201992_l(true);
         }, (var0) -> {
            return !var0.func_201966_H();
         }, new TextComponentTranslation("argument.entity.options.advancements.description", new Object[0]));
      }
   }

   public static EntityOptions.Filter func_202017_a(EntitySelectorParser var0, String var1, int var2) throws CommandSyntaxException {
      EntityOptions.IOptionHandler var3 = (EntityOptions.IOptionHandler)field_197478_k.get(var1);
      if (var3 != null) {
         if (var3.field_202013_b.test(var0)) {
            return var3.field_202012_a;
         } else {
            throw field_202058_b.createWithContext(var0.func_197398_f(), var1);
         }
      } else {
         var0.func_197398_f().setCursor(var2);
         throw field_197468_a.createWithContext(var0.func_197398_f(), var1);
      }
   }

   public static void func_202049_a(EntitySelectorParser var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      Iterator var3 = field_197478_k.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (((EntityOptions.IOptionHandler)var4.getValue()).field_202013_b.test(var0) && ((String)var4.getKey()).toLowerCase(Locale.ROOT).startsWith(var2)) {
            var1.suggest((String)var4.getKey() + '=', ((EntityOptions.IOptionHandler)var4.getValue()).field_202014_c);
         }
      }

   }

   static class IOptionHandler {
      public final EntityOptions.Filter field_202012_a;
      public final Predicate<EntitySelectorParser> field_202013_b;
      public final ITextComponent field_202014_c;

      private IOptionHandler(EntityOptions.Filter var1, Predicate<EntitySelectorParser> var2, ITextComponent var3) {
         super();
         this.field_202012_a = var1;
         this.field_202013_b = var2;
         this.field_202014_c = var3;
      }

      // $FF: synthetic method
      IOptionHandler(EntityOptions.Filter var1, Predicate var2, ITextComponent var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   public interface Filter {
      void handle(EntitySelectorParser var1) throws CommandSyntaxException;
   }
}
