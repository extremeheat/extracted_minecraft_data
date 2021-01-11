package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

public class FlatGeneratorInfo {
   private final List<FlatLayerInfo> field_82655_a = Lists.newArrayList();
   private final Map<String, Map<String, String>> field_82653_b = Maps.newHashMap();
   private int field_82654_c;

   public FlatGeneratorInfo() {
      super();
   }

   public int func_82648_a() {
      return this.field_82654_c;
   }

   public void func_82647_a(int var1) {
      this.field_82654_c = var1;
   }

   public Map<String, Map<String, String>> func_82644_b() {
      return this.field_82653_b;
   }

   public List<FlatLayerInfo> func_82650_c() {
      return this.field_82655_a;
   }

   public void func_82645_d() {
      int var1 = 0;

      FlatLayerInfo var3;
      for(Iterator var2 = this.field_82655_a.iterator(); var2.hasNext(); var1 += var3.func_82657_a()) {
         var3 = (FlatLayerInfo)var2.next();
         var3.func_82660_d(var1);
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(3);
      var1.append(";");

      int var2;
      for(var2 = 0; var2 < this.field_82655_a.size(); ++var2) {
         if (var2 > 0) {
            var1.append(",");
         }

         var1.append(((FlatLayerInfo)this.field_82655_a.get(var2)).toString());
      }

      var1.append(";");
      var1.append(this.field_82654_c);
      if (!this.field_82653_b.isEmpty()) {
         var1.append(";");
         var2 = 0;
         Iterator var3 = this.field_82653_b.entrySet().iterator();

         while(true) {
            Map var5;
            do {
               if (!var3.hasNext()) {
                  return var1.toString();
               }

               Entry var4 = (Entry)var3.next();
               if (var2++ > 0) {
                  var1.append(",");
               }

               var1.append(((String)var4.getKey()).toLowerCase());
               var5 = (Map)var4.getValue();
            } while(var5.isEmpty());

            var1.append("(");
            int var6 = 0;
            Iterator var7 = var5.entrySet().iterator();

            while(var7.hasNext()) {
               Entry var8 = (Entry)var7.next();
               if (var6++ > 0) {
                  var1.append(" ");
               }

               var1.append((String)var8.getKey());
               var1.append("=");
               var1.append((String)var8.getValue());
            }

            var1.append(")");
         }
      } else {
         var1.append(";");
         return var1.toString();
      }
   }

   private static FlatLayerInfo func_180715_a(int var0, String var1, int var2) {
      String[] var3 = var0 >= 3 ? var1.split("\\*", 2) : var1.split("x", 2);
      int var4 = 1;
      int var5 = 0;
      if (var3.length == 2) {
         try {
            var4 = Integer.parseInt(var3[0]);
            if (var2 + var4 >= 256) {
               var4 = 256 - var2;
            }

            if (var4 < 0) {
               var4 = 0;
            }
         } catch (Throwable var8) {
            return null;
         }
      }

      Block var6 = null;

      try {
         String var7 = var3[var3.length - 1];
         if (var0 < 3) {
            var3 = var7.split(":", 2);
            if (var3.length > 1) {
               var5 = Integer.parseInt(var3[1]);
            }

            var6 = Block.func_149729_e(Integer.parseInt(var3[0]));
         } else {
            var3 = var7.split(":", 3);
            var6 = var3.length > 1 ? Block.func_149684_b(var3[0] + ":" + var3[1]) : null;
            if (var6 != null) {
               var5 = var3.length > 2 ? Integer.parseInt(var3[2]) : 0;
            } else {
               var6 = Block.func_149684_b(var3[0]);
               if (var6 != null) {
                  var5 = var3.length > 1 ? Integer.parseInt(var3[1]) : 0;
               }
            }

            if (var6 == null) {
               return null;
            }
         }

         if (var6 == Blocks.field_150350_a) {
            var5 = 0;
         }

         if (var5 < 0 || var5 > 15) {
            var5 = 0;
         }
      } catch (Throwable var9) {
         return null;
      }

      FlatLayerInfo var10 = new FlatLayerInfo(var0, var4, var6, var5);
      var10.func_82660_d(var2);
      return var10;
   }

   private static List<FlatLayerInfo> func_180716_a(int var0, String var1) {
      if (var1 != null && var1.length() >= 1) {
         ArrayList var2 = Lists.newArrayList();
         String[] var3 = var1.split(",");
         int var4 = 0;
         String[] var5 = var3;
         int var6 = var3.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            FlatLayerInfo var9 = func_180715_a(var0, var8, var4);
            if (var9 == null) {
               return null;
            }

            var2.add(var9);
            var4 += var9.func_82657_a();
         }

         return var2;
      } else {
         return null;
      }
   }

   public static FlatGeneratorInfo func_82651_a(String var0) {
      if (var0 == null) {
         return func_82649_e();
      } else {
         String[] var1 = var0.split(";", -1);
         int var2 = var1.length == 1 ? 0 : MathHelper.func_82715_a(var1[0], 0);
         if (var2 >= 0 && var2 <= 3) {
            FlatGeneratorInfo var3 = new FlatGeneratorInfo();
            int var4 = var1.length == 1 ? 0 : 1;
            List var5 = func_180716_a(var2, var1[var4++]);
            if (var5 != null && !var5.isEmpty()) {
               var3.func_82650_c().addAll(var5);
               var3.func_82645_d();
               int var6 = BiomeGenBase.field_76772_c.field_76756_M;
               if (var2 > 0 && var1.length > var4) {
                  var6 = MathHelper.func_82715_a(var1[var4++], var6);
               }

               var3.func_82647_a(var6);
               if (var2 > 0 && var1.length > var4) {
                  String[] var7 = var1[var4++].toLowerCase().split(",");
                  String[] var8 = var7;
                  int var9 = var7.length;

                  for(int var10 = 0; var10 < var9; ++var10) {
                     String var11 = var8[var10];
                     String[] var12 = var11.split("\\(", 2);
                     HashMap var13 = Maps.newHashMap();
                     if (var12[0].length() > 0) {
                        var3.func_82644_b().put(var12[0], var13);
                        if (var12.length > 1 && var12[1].endsWith(")") && var12[1].length() > 1) {
                           String[] var14 = var12[1].substring(0, var12[1].length() - 1).split(" ");

                           for(int var15 = 0; var15 < var14.length; ++var15) {
                              String[] var16 = var14[var15].split("=", 2);
                              if (var16.length == 2) {
                                 var13.put(var16[0], var16[1]);
                              }
                           }
                        }
                     }
                  }
               } else {
                  var3.func_82644_b().put("village", Maps.newHashMap());
               }

               return var3;
            } else {
               return func_82649_e();
            }
         } else {
            return func_82649_e();
         }
      }
   }

   public static FlatGeneratorInfo func_82649_e() {
      FlatGeneratorInfo var0 = new FlatGeneratorInfo();
      var0.func_82647_a(BiomeGenBase.field_76772_c.field_76756_M);
      var0.func_82650_c().add(new FlatLayerInfo(1, Blocks.field_150357_h));
      var0.func_82650_c().add(new FlatLayerInfo(2, Blocks.field_150346_d));
      var0.func_82650_c().add(new FlatLayerInfo(1, Blocks.field_150349_c));
      var0.func_82645_d();
      var0.func_82644_b().put("village", Maps.newHashMap());
      return var0;
   }
}
