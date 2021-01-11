package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.util.IntegerCache;

public class PotionHelper {
   public static final String field_77924_a = null;
   public static final String field_77922_b;
   public static final String field_77923_c = "+0-1-2-3&4-4+13";
   public static final String field_77920_d;
   public static final String field_77921_e;
   public static final String field_77918_f;
   public static final String field_77919_g;
   public static final String field_77931_h;
   public static final String field_77932_i;
   public static final String field_77929_j;
   public static final String field_77930_k;
   public static final String field_82818_l;
   public static final String field_151423_m;
   public static final String field_179538_n;
   private static final Map<Integer, String> field_179539_o = Maps.newHashMap();
   private static final Map<Integer, String> field_179540_p = Maps.newHashMap();
   private static final Map<Integer, Integer> field_77925_n;
   private static final String[] field_77926_o;

   public static boolean func_77914_a(int var0, int var1) {
      return (var0 & 1 << var1) != 0;
   }

   private static int func_77910_c(int var0, int var1) {
      return func_77914_a(var0, var1) ? 1 : 0;
   }

   private static int func_77916_d(int var0, int var1) {
      return func_77914_a(var0, var1) ? 0 : 1;
   }

   public static int func_77909_a(int var0) {
      return func_77908_a(var0, 5, 4, 3, 2, 1);
   }

   public static int func_77911_a(Collection<PotionEffect> var0) {
      int var1 = 3694022;
      if (var0 != null && !var0.isEmpty()) {
         float var2 = 0.0F;
         float var3 = 0.0F;
         float var4 = 0.0F;
         float var5 = 0.0F;
         Iterator var6 = var0.iterator();

         while(true) {
            PotionEffect var7;
            do {
               if (!var6.hasNext()) {
                  if (var5 == 0.0F) {
                     return 0;
                  }

                  var2 = var2 / var5 * 255.0F;
                  var3 = var3 / var5 * 255.0F;
                  var4 = var4 / var5 * 255.0F;
                  return (int)var2 << 16 | (int)var3 << 8 | (int)var4;
               }

               var7 = (PotionEffect)var6.next();
            } while(!var7.func_180154_f());

            int var8 = Potion.field_76425_a[var7.func_76456_a()].func_76401_j();

            for(int var9 = 0; var9 <= var7.func_76458_c(); ++var9) {
               var2 += (float)(var8 >> 16 & 255) / 255.0F;
               var3 += (float)(var8 >> 8 & 255) / 255.0F;
               var4 += (float)(var8 >> 0 & 255) / 255.0F;
               ++var5;
            }
         }
      } else {
         return var1;
      }
   }

   public static boolean func_82817_b(Collection<PotionEffect> var0) {
      Iterator var1 = var0.iterator();

      PotionEffect var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (PotionEffect)var1.next();
      } while(var2.func_82720_e());

      return false;
   }

   public static int func_77915_a(int var0, boolean var1) {
      Integer var2 = IntegerCache.func_181756_a(var0);
      if (!var1) {
         if (field_77925_n.containsKey(var2)) {
            return (Integer)field_77925_n.get(var2);
         } else {
            int var3 = func_77911_a(func_77917_b(var2, false));
            field_77925_n.put(var2, var3);
            return var3;
         }
      } else {
         return func_77911_a(func_77917_b(var2, true));
      }
   }

   public static String func_77905_c(int var0) {
      int var1 = func_77909_a(var0);
      return field_77926_o[var1];
   }

   private static int func_77904_a(boolean var0, boolean var1, boolean var2, int var3, int var4, int var5, int var6) {
      int var7 = 0;
      if (var0) {
         var7 = func_77916_d(var6, var4);
      } else if (var3 != -1) {
         if (var3 == 0 && func_77907_h(var6) == var4) {
            var7 = 1;
         } else if (var3 == 1 && func_77907_h(var6) > var4) {
            var7 = 1;
         } else if (var3 == 2 && func_77907_h(var6) < var4) {
            var7 = 1;
         }
      } else {
         var7 = func_77910_c(var6, var4);
      }

      if (var1) {
         var7 *= var5;
      }

      if (var2) {
         var7 *= -1;
      }

      return var7;
   }

   private static int func_77907_h(int var0) {
      int var1;
      for(var1 = 0; var0 > 0; ++var1) {
         var0 &= var0 - 1;
      }

      return var1;
   }

   private static int func_77912_a(String var0, int var1, int var2, int var3) {
      if (var1 < var0.length() && var2 >= 0 && var1 < var2) {
         int var4 = var0.indexOf(124, var1);
         int var5;
         int var17;
         if (var4 >= 0 && var4 < var2) {
            var5 = func_77912_a(var0, var1, var4 - 1, var3);
            if (var5 > 0) {
               return var5;
            } else {
               var17 = func_77912_a(var0, var4 + 1, var2, var3);
               return var17 > 0 ? var17 : 0;
            }
         } else {
            var5 = var0.indexOf(38, var1);
            if (var5 >= 0 && var5 < var2) {
               var17 = func_77912_a(var0, var1, var5 - 1, var3);
               if (var17 <= 0) {
                  return 0;
               } else {
                  int var18 = func_77912_a(var0, var5 + 1, var2, var3);
                  if (var18 <= 0) {
                     return 0;
                  } else {
                     return var17 > var18 ? var17 : var18;
                  }
               }
            } else {
               boolean var6 = false;
               boolean var7 = false;
               boolean var8 = false;
               boolean var9 = false;
               boolean var10 = false;
               byte var11 = -1;
               int var12 = 0;
               int var13 = 0;
               int var14 = 0;

               for(int var15 = var1; var15 < var2; ++var15) {
                  char var16 = var0.charAt(var15);
                  if (var16 >= '0' && var16 <= '9') {
                     if (var6) {
                        var13 = var16 - 48;
                        var7 = true;
                     } else {
                        var12 *= 10;
                        var12 += var16 - 48;
                        var8 = true;
                     }
                  } else if (var16 == '*') {
                     var6 = true;
                  } else if (var16 == '!') {
                     if (var8) {
                        var14 += func_77904_a(var9, var7, var10, var11, var12, var13, var3);
                        var9 = false;
                        var10 = false;
                        var6 = false;
                        var7 = false;
                        var8 = false;
                        var13 = 0;
                        var12 = 0;
                        var11 = -1;
                     }

                     var9 = true;
                  } else if (var16 == '-') {
                     if (var8) {
                        var14 += func_77904_a(var9, var7, var10, var11, var12, var13, var3);
                        var9 = false;
                        var10 = false;
                        var6 = false;
                        var7 = false;
                        var8 = false;
                        var13 = 0;
                        var12 = 0;
                        var11 = -1;
                     }

                     var10 = true;
                  } else if (var16 != '=' && var16 != '<' && var16 != '>') {
                     if (var16 == '+' && var8) {
                        var14 += func_77904_a(var9, var7, var10, var11, var12, var13, var3);
                        var9 = false;
                        var10 = false;
                        var6 = false;
                        var7 = false;
                        var8 = false;
                        var13 = 0;
                        var12 = 0;
                        var11 = -1;
                     }
                  } else {
                     if (var8) {
                        var14 += func_77904_a(var9, var7, var10, var11, var12, var13, var3);
                        var9 = false;
                        var10 = false;
                        var6 = false;
                        var7 = false;
                        var8 = false;
                        var13 = 0;
                        var12 = 0;
                        var11 = -1;
                     }

                     if (var16 == '=') {
                        var11 = 0;
                     } else if (var16 == '<') {
                        var11 = 2;
                     } else if (var16 == '>') {
                        var11 = 1;
                     }
                  }
               }

               if (var8) {
                  var14 += func_77904_a(var9, var7, var10, var11, var12, var13, var3);
               }

               return var14;
            }
         }
      } else {
         return 0;
      }
   }

   public static List<PotionEffect> func_77917_b(int var0, boolean var1) {
      ArrayList var2 = null;
      Potion[] var3 = Potion.field_76425_a;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Potion var6 = var3[var5];
         if (var6 != null && (!var6.func_76395_i() || var1)) {
            String var7 = (String)field_179539_o.get(var6.func_76396_c());
            if (var7 != null) {
               int var8 = func_77912_a(var7, 0, var7.length(), var0);
               if (var8 > 0) {
                  int var9 = 0;
                  String var10 = (String)field_179540_p.get(var6.func_76396_c());
                  if (var10 != null) {
                     var9 = func_77912_a(var10, 0, var10.length(), var0);
                     if (var9 < 0) {
                        var9 = 0;
                     }
                  }

                  if (var6.func_76403_b()) {
                     var8 = 1;
                  } else {
                     var8 = 1200 * (var8 * 3 + (var8 - 1) * 2);
                     var8 >>= var9;
                     var8 = (int)Math.round((double)var8 * var6.func_76388_g());
                     if ((var0 & 16384) != 0) {
                        var8 = (int)Math.round((double)var8 * 0.75D + 0.5D);
                     }
                  }

                  if (var2 == null) {
                     var2 = Lists.newArrayList();
                  }

                  PotionEffect var11 = new PotionEffect(var6.func_76396_c(), var8, var9);
                  if ((var0 & 16384) != 0) {
                     var11.func_82721_a(true);
                  }

                  var2.add(var11);
               }
            }
         }
      }

      return var2;
   }

   private static int func_77906_a(int var0, int var1, boolean var2, boolean var3, boolean var4) {
      if (var4) {
         if (!func_77914_a(var0, var1)) {
            return 0;
         }
      } else if (var2) {
         var0 &= ~(1 << var1);
      } else if (var3) {
         if ((var0 & 1 << var1) == 0) {
            var0 |= 1 << var1;
         } else {
            var0 &= ~(1 << var1);
         }
      } else {
         var0 |= 1 << var1;
      }

      return var0;
   }

   public static int func_77913_a(int var0, String var1) {
      byte var2 = 0;
      int var3 = var1.length();
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;
      int var8 = 0;

      for(int var9 = var2; var9 < var3; ++var9) {
         char var10 = var1.charAt(var9);
         if (var10 >= '0' && var10 <= '9') {
            var8 *= 10;
            var8 += var10 - 48;
            var4 = true;
         } else if (var10 == '!') {
            if (var4) {
               var0 = func_77906_a(var0, var8, var6, var5, var7);
               var7 = false;
               var5 = false;
               var6 = false;
               var4 = false;
               var8 = 0;
            }

            var5 = true;
         } else if (var10 == '-') {
            if (var4) {
               var0 = func_77906_a(var0, var8, var6, var5, var7);
               var7 = false;
               var5 = false;
               var6 = false;
               var4 = false;
               var8 = 0;
            }

            var6 = true;
         } else if (var10 == '+') {
            if (var4) {
               var0 = func_77906_a(var0, var8, var6, var5, var7);
               var7 = false;
               var5 = false;
               var6 = false;
               var4 = false;
               var8 = 0;
            }
         } else if (var10 == '&') {
            if (var4) {
               var0 = func_77906_a(var0, var8, var6, var5, var7);
               var7 = false;
               var5 = false;
               var6 = false;
               var4 = false;
               var8 = 0;
            }

            var7 = true;
         }
      }

      if (var4) {
         var0 = func_77906_a(var0, var8, var6, var5, var7);
      }

      return var0 & 32767;
   }

   public static int func_77908_a(int var0, int var1, int var2, int var3, int var4, int var5) {
      return (func_77914_a(var0, var1) ? 16 : 0) | (func_77914_a(var0, var2) ? 8 : 0) | (func_77914_a(var0, var3) ? 4 : 0) | (func_77914_a(var0, var4) ? 2 : 0) | (func_77914_a(var0, var5) ? 1 : 0);
   }

   static {
      field_179539_o.put(Potion.field_76428_l.func_76396_c(), "0 & !1 & !2 & !3 & 0+6");
      field_77922_b = "-0+1-2-3&4-4+13";
      field_179539_o.put(Potion.field_76424_c.func_76396_c(), "!0 & 1 & !2 & !3 & 1+6");
      field_77931_h = "+0+1-2-3&4-4+13";
      field_179539_o.put(Potion.field_76426_n.func_76396_c(), "0 & 1 & !2 & !3 & 0+6");
      field_77918_f = "+0-1+2-3&4-4+13";
      field_179539_o.put(Potion.field_76432_h.func_76396_c(), "0 & !1 & 2 & !3");
      field_77920_d = "-0-1+2-3&4-4+13";
      field_179539_o.put(Potion.field_76436_u.func_76396_c(), "!0 & !1 & 2 & !3 & 2+6");
      field_77921_e = "-0+3-4+13";
      field_179539_o.put(Potion.field_76437_t.func_76396_c(), "!0 & !1 & !2 & 3 & 3+6");
      field_179539_o.put(Potion.field_76433_i.func_76396_c(), "!0 & !1 & 2 & 3");
      field_179539_o.put(Potion.field_76421_d.func_76396_c(), "!0 & 1 & !2 & 3 & 3+6");
      field_77919_g = "+0-1-2+3&4-4+13";
      field_179539_o.put(Potion.field_76420_g.func_76396_c(), "0 & !1 & !2 & 3 & 3+6");
      field_82818_l = "-0+1+2-3+13&4-4";
      field_179539_o.put(Potion.field_76439_r.func_76396_c(), "!0 & 1 & 2 & !3 & 2+6");
      field_179539_o.put(Potion.field_76441_p.func_76396_c(), "!0 & 1 & 2 & 3 & 2+6");
      field_151423_m = "+0-1+2+3+13&4-4";
      field_179539_o.put(Potion.field_76427_o.func_76396_c(), "0 & !1 & 2 & 3 & 2+6");
      field_179538_n = "+0+1-2+3&4-4+13";
      field_179539_o.put(Potion.field_76430_j.func_76396_c(), "0 & 1 & !2 & 3 & 3+6");
      field_77929_j = "+5-6-7";
      field_179540_p.put(Potion.field_76424_c.func_76396_c(), "5");
      field_179540_p.put(Potion.field_76422_e.func_76396_c(), "5");
      field_179540_p.put(Potion.field_76420_g.func_76396_c(), "5");
      field_179540_p.put(Potion.field_76428_l.func_76396_c(), "5");
      field_179540_p.put(Potion.field_76433_i.func_76396_c(), "5");
      field_179540_p.put(Potion.field_76432_h.func_76396_c(), "5");
      field_179540_p.put(Potion.field_76429_m.func_76396_c(), "5");
      field_179540_p.put(Potion.field_76436_u.func_76396_c(), "5");
      field_179540_p.put(Potion.field_76430_j.func_76396_c(), "5");
      field_77932_i = "-5+6-7";
      field_77930_k = "+14&13-13";
      field_77925_n = Maps.newHashMap();
      field_77926_o = new String[]{"potion.prefix.mundane", "potion.prefix.uninteresting", "potion.prefix.bland", "potion.prefix.clear", "potion.prefix.milky", "potion.prefix.diffuse", "potion.prefix.artless", "potion.prefix.thin", "potion.prefix.awkward", "potion.prefix.flat", "potion.prefix.bulky", "potion.prefix.bungling", "potion.prefix.buttered", "potion.prefix.smooth", "potion.prefix.suave", "potion.prefix.debonair", "potion.prefix.thick", "potion.prefix.elegant", "potion.prefix.fancy", "potion.prefix.charming", "potion.prefix.dashing", "potion.prefix.refined", "potion.prefix.cordial", "potion.prefix.sparkling", "potion.prefix.potent", "potion.prefix.foul", "potion.prefix.odorless", "potion.prefix.rank", "potion.prefix.harsh", "potion.prefix.acrid", "potion.prefix.gross", "potion.prefix.stinky"};
   }
}
