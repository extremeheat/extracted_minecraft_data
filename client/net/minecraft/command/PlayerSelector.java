package net.minecraft.command;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class PlayerSelector {
   private static final Pattern field_82389_a = Pattern.compile("^@([pare])(?:\\[([\\w=,!-]*)\\])?$");
   private static final Pattern field_82387_b = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
   private static final Pattern field_82388_c = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");
   private static final Set<String> field_179666_d = Sets.newHashSet(new String[]{"x", "y", "z", "dx", "dy", "dz", "rm", "r"});

   public static EntityPlayerMP func_82386_a(ICommandSender var0, String var1) {
      return (EntityPlayerMP)func_179652_a(var0, var1, EntityPlayerMP.class);
   }

   public static <T extends Entity> T func_179652_a(ICommandSender var0, String var1, Class<? extends T> var2) {
      List var3 = func_179656_b(var0, var1, var2);
      return var3.size() == 1 ? (Entity)var3.get(0) : null;
   }

   public static IChatComponent func_150869_b(ICommandSender var0, String var1) {
      List var2 = func_179656_b(var0, var1, Entity.class);
      if (var2.isEmpty()) {
         return null;
      } else {
         ArrayList var3 = Lists.newArrayList();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            Entity var5 = (Entity)var4.next();
            var3.add(var5.func_145748_c_());
         }

         return CommandBase.func_180530_a(var3);
      }
   }

   public static <T extends Entity> List<T> func_179656_b(ICommandSender var0, String var1, Class<? extends T> var2) {
      Matcher var3 = field_82389_a.matcher(var1);
      if (var3.matches() && var0.func_70003_b(1, "@")) {
         Map var4 = func_82381_h(var3.group(2));
         if (!func_179655_b(var0, var4)) {
            return Collections.emptyList();
         } else {
            String var5 = var3.group(1);
            BlockPos var6 = func_179664_b(var4, var0.func_180425_c());
            List var7 = func_179654_a(var0, var4);
            ArrayList var8 = Lists.newArrayList();
            Iterator var9 = var7.iterator();

            while(var9.hasNext()) {
               World var10 = (World)var9.next();
               if (var10 != null) {
                  ArrayList var11 = Lists.newArrayList();
                  var11.addAll(func_179663_a(var4, var5));
                  var11.addAll(func_179648_b(var4));
                  var11.addAll(func_179649_c(var4));
                  var11.addAll(func_179659_d(var4));
                  var11.addAll(func_179657_e(var4));
                  var11.addAll(func_179647_f(var4));
                  var11.addAll(func_180698_a(var4, var6));
                  var11.addAll(func_179662_g(var4));
                  var8.addAll(func_179660_a(var4, var2, var11, var5, var10, var6));
               }
            }

            return func_179658_a(var8, var4, var0, var2, var5, var6);
         }
      } else {
         return Collections.emptyList();
      }
   }

   private static List<World> func_179654_a(ICommandSender var0, Map<String, String> var1) {
      ArrayList var2 = Lists.newArrayList();
      if (func_179665_h(var1)) {
         var2.add(var0.func_130014_f_());
      } else {
         Collections.addAll(var2, MinecraftServer.func_71276_C().field_71305_c);
      }

      return var2;
   }

   private static <T extends Entity> boolean func_179655_b(ICommandSender var0, Map<String, String> var1) {
      String var2 = func_179651_b(var1, "type");
      var2 = var2 != null && var2.startsWith("!") ? var2.substring(1) : var2;
      if (var2 != null && !EntityList.func_180125_b(var2)) {
         ChatComponentTranslation var3 = new ChatComponentTranslation("commands.generic.entity.invalidType", new Object[]{var2});
         var3.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         var0.func_145747_a(var3);
         return false;
      } else {
         return true;
      }
   }

   private static List<Predicate<Entity>> func_179663_a(Map<String, String> var0, String var1) {
      ArrayList var2 = Lists.newArrayList();
      final String var3 = func_179651_b(var0, "type");
      final boolean var4 = var3 != null && var3.startsWith("!");
      if (var4) {
         var3 = var3.substring(1);
      }

      boolean var6 = !var1.equals("e");
      boolean var7 = var1.equals("r") && var3 != null;
      if ((var3 == null || !var1.equals("e")) && !var7) {
         if (var6) {
            var2.add(new Predicate<Entity>() {
               public boolean apply(Entity var1) {
                  return var1 instanceof EntityPlayer;
               }

               // $FF: synthetic method
               public boolean apply(Object var1) {
                  return this.apply((Entity)var1);
               }
            });
         }
      } else {
         var2.add(new Predicate<Entity>() {
            public boolean apply(Entity var1) {
               return EntityList.func_180123_a(var1, var3) != var4;
            }

            // $FF: synthetic method
            public boolean apply(Object var1) {
               return this.apply((Entity)var1);
            }
         });
      }

      return var2;
   }

   private static List<Predicate<Entity>> func_179648_b(Map<String, String> var0) {
      ArrayList var1 = Lists.newArrayList();
      final int var2 = func_179653_a(var0, "lm", -1);
      final int var3 = func_179653_a(var0, "l", -1);
      if (var2 > -1 || var3 > -1) {
         var1.add(new Predicate<Entity>() {
            public boolean apply(Entity var1) {
               if (!(var1 instanceof EntityPlayerMP)) {
                  return false;
               } else {
                  EntityPlayerMP var2x = (EntityPlayerMP)var1;
                  return (var2 <= -1 || var2x.field_71068_ca >= var2) && (var3 <= -1 || var2x.field_71068_ca <= var3);
               }
            }

            // $FF: synthetic method
            public boolean apply(Object var1) {
               return this.apply((Entity)var1);
            }
         });
      }

      return var1;
   }

   private static List<Predicate<Entity>> func_179649_c(Map<String, String> var0) {
      ArrayList var1 = Lists.newArrayList();
      final int var2 = func_179653_a(var0, "m", WorldSettings.GameType.NOT_SET.func_77148_a());
      if (var2 != WorldSettings.GameType.NOT_SET.func_77148_a()) {
         var1.add(new Predicate<Entity>() {
            public boolean apply(Entity var1) {
               if (!(var1 instanceof EntityPlayerMP)) {
                  return false;
               } else {
                  EntityPlayerMP var2x = (EntityPlayerMP)var1;
                  return var2x.field_71134_c.func_73081_b().func_77148_a() == var2;
               }
            }

            // $FF: synthetic method
            public boolean apply(Object var1) {
               return this.apply((Entity)var1);
            }
         });
      }

      return var1;
   }

   private static List<Predicate<Entity>> func_179659_d(Map<String, String> var0) {
      ArrayList var1 = Lists.newArrayList();
      final String var2 = func_179651_b(var0, "team");
      final boolean var3 = var2 != null && var2.startsWith("!");
      if (var3) {
         var2 = var2.substring(1);
      }

      if (var2 != null) {
         var1.add(new Predicate<Entity>() {
            public boolean apply(Entity var1) {
               if (!(var1 instanceof EntityLivingBase)) {
                  return false;
               } else {
                  EntityLivingBase var2x = (EntityLivingBase)var1;
                  Team var3x = var2x.func_96124_cp();
                  String var4 = var3x == null ? "" : var3x.func_96661_b();
                  return var4.equals(var2) != var3;
               }
            }

            // $FF: synthetic method
            public boolean apply(Object var1) {
               return this.apply((Entity)var1);
            }
         });
      }

      return var1;
   }

   private static List<Predicate<Entity>> func_179657_e(Map<String, String> var0) {
      ArrayList var1 = Lists.newArrayList();
      final Map var2 = func_96560_a(var0);
      if (var2 != null && var2.size() > 0) {
         var1.add(new Predicate<Entity>() {
            public boolean apply(Entity var1) {
               Scoreboard var2x = MinecraftServer.func_71276_C().func_71218_a(0).func_96441_U();
               Iterator var3 = var2.entrySet().iterator();

               Entry var4;
               boolean var6;
               int var10;
               do {
                  if (!var3.hasNext()) {
                     return true;
                  }

                  var4 = (Entry)var3.next();
                  String var5 = (String)var4.getKey();
                  var6 = false;
                  if (var5.endsWith("_min") && var5.length() > 4) {
                     var6 = true;
                     var5 = var5.substring(0, var5.length() - 4);
                  }

                  ScoreObjective var7 = var2x.func_96518_b(var5);
                  if (var7 == null) {
                     return false;
                  }

                  String var8 = var1 instanceof EntityPlayerMP ? var1.func_70005_c_() : var1.func_110124_au().toString();
                  if (!var2x.func_178819_b(var8, var7)) {
                     return false;
                  }

                  Score var9 = var2x.func_96529_a(var8, var7);
                  var10 = var9.func_96652_c();
                  if (var10 < (Integer)var4.getValue() && var6) {
                     return false;
                  }
               } while(var10 <= (Integer)var4.getValue() || var6);

               return false;
            }

            // $FF: synthetic method
            public boolean apply(Object var1) {
               return this.apply((Entity)var1);
            }
         });
      }

      return var1;
   }

   private static List<Predicate<Entity>> func_179647_f(Map<String, String> var0) {
      ArrayList var1 = Lists.newArrayList();
      final String var2 = func_179651_b(var0, "name");
      final boolean var3 = var2 != null && var2.startsWith("!");
      if (var3) {
         var2 = var2.substring(1);
      }

      if (var2 != null) {
         var1.add(new Predicate<Entity>() {
            public boolean apply(Entity var1) {
               return var1.func_70005_c_().equals(var2) != var3;
            }

            // $FF: synthetic method
            public boolean apply(Object var1) {
               return this.apply((Entity)var1);
            }
         });
      }

      return var1;
   }

   private static List<Predicate<Entity>> func_180698_a(Map<String, String> var0, final BlockPos var1) {
      ArrayList var2 = Lists.newArrayList();
      final int var3 = func_179653_a(var0, "rm", -1);
      final int var4 = func_179653_a(var0, "r", -1);
      if (var1 != null && (var3 >= 0 || var4 >= 0)) {
         final int var5 = var3 * var3;
         final int var6 = var4 * var4;
         var2.add(new Predicate<Entity>() {
            public boolean apply(Entity var1x) {
               int var2 = (int)var1x.func_174831_c(var1);
               return (var3 < 0 || var2 >= var5) && (var4 < 0 || var2 <= var6);
            }

            // $FF: synthetic method
            public boolean apply(Object var1x) {
               return this.apply((Entity)var1x);
            }
         });
      }

      return var2;
   }

   private static List<Predicate<Entity>> func_179662_g(Map<String, String> var0) {
      ArrayList var1 = Lists.newArrayList();
      final int var2;
      final int var3;
      if (var0.containsKey("rym") || var0.containsKey("ry")) {
         var2 = func_179650_a(func_179653_a(var0, "rym", 0));
         var3 = func_179650_a(func_179653_a(var0, "ry", 359));
         var1.add(new Predicate<Entity>() {
            public boolean apply(Entity var1) {
               int var2x = PlayerSelector.func_179650_a((int)Math.floor((double)var1.field_70177_z));
               if (var2 > var3) {
                  return var2x >= var2 || var2x <= var3;
               } else {
                  return var2x >= var2 && var2x <= var3;
               }
            }

            // $FF: synthetic method
            public boolean apply(Object var1) {
               return this.apply((Entity)var1);
            }
         });
      }

      if (var0.containsKey("rxm") || var0.containsKey("rx")) {
         var2 = func_179650_a(func_179653_a(var0, "rxm", 0));
         var3 = func_179650_a(func_179653_a(var0, "rx", 359));
         var1.add(new Predicate<Entity>() {
            public boolean apply(Entity var1) {
               int var2x = PlayerSelector.func_179650_a((int)Math.floor((double)var1.field_70125_A));
               if (var2 > var3) {
                  return var2x >= var2 || var2x <= var3;
               } else {
                  return var2x >= var2 && var2x <= var3;
               }
            }

            // $FF: synthetic method
            public boolean apply(Object var1) {
               return this.apply((Entity)var1);
            }
         });
      }

      return var1;
   }

   private static <T extends Entity> List<T> func_179660_a(Map<String, String> var0, Class<? extends T> var1, List<Predicate<Entity>> var2, String var3, World var4, BlockPos var5) {
      ArrayList var6 = Lists.newArrayList();
      String var7 = func_179651_b(var0, "type");
      var7 = var7 != null && var7.startsWith("!") ? var7.substring(1) : var7;
      boolean var8 = !var3.equals("e");
      boolean var9 = var3.equals("r") && var7 != null;
      int var10 = func_179653_a(var0, "dx", 0);
      int var11 = func_179653_a(var0, "dy", 0);
      int var12 = func_179653_a(var0, "dz", 0);
      int var13 = func_179653_a(var0, "r", -1);
      Predicate var14 = Predicates.and(var2);
      Predicate var15 = Predicates.and(EntitySelectors.field_94557_a, var14);
      if (var5 != null) {
         int var16 = var4.field_73010_i.size();
         int var17 = var4.field_72996_f.size();
         boolean var18 = var16 < var17 / 16;
         final AxisAlignedBB var19;
         if (!var0.containsKey("dx") && !var0.containsKey("dy") && !var0.containsKey("dz")) {
            if (var13 >= 0) {
               var19 = new AxisAlignedBB((double)(var5.func_177958_n() - var13), (double)(var5.func_177956_o() - var13), (double)(var5.func_177952_p() - var13), (double)(var5.func_177958_n() + var13 + 1), (double)(var5.func_177956_o() + var13 + 1), (double)(var5.func_177952_p() + var13 + 1));
               if (var8 && var18 && !var9) {
                  var6.addAll(var4.func_175661_b(var1, var15));
               } else {
                  var6.addAll(var4.func_175647_a(var1, var19, var15));
               }
            } else if (var3.equals("a")) {
               var6.addAll(var4.func_175661_b(var1, var14));
            } else if (!var3.equals("p") && (!var3.equals("r") || var9)) {
               var6.addAll(var4.func_175644_a(var1, var15));
            } else {
               var6.addAll(var4.func_175661_b(var1, var15));
            }
         } else {
            var19 = func_179661_a(var5, var10, var11, var12);
            if (var8 && var18 && !var9) {
               Predicate var20 = new Predicate<Entity>() {
                  public boolean apply(Entity var1) {
                     if (var1.field_70165_t >= var19.field_72340_a && var1.field_70163_u >= var19.field_72338_b && var1.field_70161_v >= var19.field_72339_c) {
                        return var1.field_70165_t < var19.field_72336_d && var1.field_70163_u < var19.field_72337_e && var1.field_70161_v < var19.field_72334_f;
                     } else {
                        return false;
                     }
                  }

                  // $FF: synthetic method
                  public boolean apply(Object var1) {
                     return this.apply((Entity)var1);
                  }
               };
               var6.addAll(var4.func_175661_b(var1, Predicates.and(var15, var20)));
            } else {
               var6.addAll(var4.func_175647_a(var1, var19, var15));
            }
         }
      } else if (var3.equals("a")) {
         var6.addAll(var4.func_175661_b(var1, var14));
      } else if (!var3.equals("p") && (!var3.equals("r") || var9)) {
         var6.addAll(var4.func_175644_a(var1, var15));
      } else {
         var6.addAll(var4.func_175661_b(var1, var15));
      }

      return var6;
   }

   private static <T extends Entity> List<T> func_179658_a(List<T> var0, Map<String, String> var1, ICommandSender var2, Class<? extends T> var3, String var4, final BlockPos var5) {
      int var6 = func_179653_a(var1, "c", !var4.equals("a") && !var4.equals("e") ? 1 : 0);
      if (!var4.equals("p") && !var4.equals("a") && !var4.equals("e")) {
         if (var4.equals("r")) {
            Collections.shuffle((List)var0);
         }
      } else if (var5 != null) {
         Collections.sort((List)var0, new Comparator<Entity>() {
            public int compare(Entity var1, Entity var2) {
               return ComparisonChain.start().compare(var1.func_174818_b(var5), var2.func_174818_b(var5)).result();
            }

            // $FF: synthetic method
            public int compare(Object var1, Object var2) {
               return this.compare((Entity)var1, (Entity)var2);
            }
         });
      }

      Entity var7 = var2.func_174793_f();
      if (var7 != null && var3.isAssignableFrom(var7.getClass()) && var6 == 1 && ((List)var0).contains(var7) && !"r".equals(var4)) {
         var0 = Lists.newArrayList(new Entity[]{var7});
      }

      if (var6 != 0) {
         if (var6 < 0) {
            Collections.reverse((List)var0);
         }

         var0 = ((List)var0).subList(0, Math.min(Math.abs(var6), ((List)var0).size()));
      }

      return (List)var0;
   }

   private static AxisAlignedBB func_179661_a(BlockPos var0, int var1, int var2, int var3) {
      boolean var4 = var1 < 0;
      boolean var5 = var2 < 0;
      boolean var6 = var3 < 0;
      int var7 = var0.func_177958_n() + (var4 ? var1 : 0);
      int var8 = var0.func_177956_o() + (var5 ? var2 : 0);
      int var9 = var0.func_177952_p() + (var6 ? var3 : 0);
      int var10 = var0.func_177958_n() + (var4 ? 0 : var1) + 1;
      int var11 = var0.func_177956_o() + (var5 ? 0 : var2) + 1;
      int var12 = var0.func_177952_p() + (var6 ? 0 : var3) + 1;
      return new AxisAlignedBB((double)var7, (double)var8, (double)var9, (double)var10, (double)var11, (double)var12);
   }

   public static int func_179650_a(int var0) {
      var0 %= 360;
      if (var0 >= 160) {
         var0 -= 360;
      }

      if (var0 < 0) {
         var0 += 360;
      }

      return var0;
   }

   private static BlockPos func_179664_b(Map<String, String> var0, BlockPos var1) {
      return new BlockPos(func_179653_a(var0, "x", var1.func_177958_n()), func_179653_a(var0, "y", var1.func_177956_o()), func_179653_a(var0, "z", var1.func_177952_p()));
   }

   private static boolean func_179665_h(Map<String, String> var0) {
      Iterator var1 = field_179666_d.iterator();

      String var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (String)var1.next();
      } while(!var0.containsKey(var2));

      return true;
   }

   private static int func_179653_a(Map<String, String> var0, String var1, int var2) {
      return var0.containsKey(var1) ? MathHelper.func_82715_a((String)var0.get(var1), var2) : var2;
   }

   private static String func_179651_b(Map<String, String> var0, String var1) {
      return (String)var0.get(var1);
   }

   public static Map<String, Integer> func_96560_a(Map<String, String> var0) {
      HashMap var1 = Maps.newHashMap();
      Iterator var2 = var0.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (var3.startsWith("score_") && var3.length() > "score_".length()) {
            var1.put(var3.substring("score_".length()), MathHelper.func_82715_a((String)var0.get(var3), 1));
         }
      }

      return var1;
   }

   public static boolean func_82377_a(String var0) {
      Matcher var1 = field_82389_a.matcher(var0);
      if (!var1.matches()) {
         return false;
      } else {
         Map var2 = func_82381_h(var1.group(2));
         String var3 = var1.group(1);
         int var4 = !"a".equals(var3) && !"e".equals(var3) ? 1 : 0;
         return func_179653_a(var2, "c", var4) != 1;
      }
   }

   public static boolean func_82378_b(String var0) {
      return field_82389_a.matcher(var0).matches();
   }

   private static Map<String, String> func_82381_h(String var0) {
      HashMap var1 = Maps.newHashMap();
      if (var0 == null) {
         return var1;
      } else {
         int var2 = 0;
         int var3 = -1;

         for(Matcher var4 = field_82387_b.matcher(var0); var4.find(); var3 = var4.end()) {
            String var5 = null;
            switch(var2++) {
            case 0:
               var5 = "x";
               break;
            case 1:
               var5 = "y";
               break;
            case 2:
               var5 = "z";
               break;
            case 3:
               var5 = "r";
            }

            if (var5 != null && var4.group(1).length() > 0) {
               var1.put(var5, var4.group(1));
            }
         }

         if (var3 < var0.length()) {
            Matcher var6 = field_82388_c.matcher(var3 == -1 ? var0 : var0.substring(var3));

            while(var6.find()) {
               var1.put(var6.group(1), var6.group(2));
            }
         }

         return var1;
      }
   }
}
