package net.minecraft.world.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

public class FlatGeneratorInfo {
   private final List field_82655_a = new ArrayList();
   private final Map field_82653_b = new HashMap();
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

   public Map func_82644_b() {
      return this.field_82653_b;
   }

   public List func_82650_c() {
      return this.field_82655_a;
   }

   public void func_82645_d() {
      int var1 = 0;

      for(FlatLayerInfo var3 : this.field_82655_a) {
         var3.func_82660_d(var1);
         var1 += var3.func_82657_a();
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(2);
      var1.append(";");

      for(int var2 = 0; var2 < this.field_82655_a.size(); ++var2) {
         if (var2 > 0) {
            var1.append(",");
         }

         var1.append(((FlatLayerInfo)this.field_82655_a.get(var2)).toString());
      }

      var1.append(";");
      var1.append(this.field_82654_c);
      if (!this.field_82653_b.isEmpty()) {
         var1.append(";");
         int var9 = 0;

         for(Entry var4 : this.field_82653_b.entrySet()) {
            if (var9++ > 0) {
               var1.append(",");
            }

            var1.append(((String)var4.getKey()).toLowerCase());
            Map var5 = (Map)var4.getValue();
            if (!var5.isEmpty()) {
               var1.append("(");
               int var6 = 0;

               for(Entry var8 : var5.entrySet()) {
                  if (var6++ > 0) {
                     var1.append(" ");
                  }

                  var1.append((String)var8.getKey());
                  var1.append("=");
                  var1.append((String)var8.getValue());
               }

               var1.append(")");
            }
         }
      } else {
         var1.append(";");
      }

      return var1.toString();
   }

   private static FlatLayerInfo func_82646_a(String var0, int var1) {
      String[] var2 = var0.split("x", 2);
      int var3 = 1;
      int var5 = 0;
      if (var2.length == 2) {
         try {
            var3 = Integer.parseInt(var2[0]);
            if (var1 + var3 >= 256) {
               var3 = 256 - var1;
            }

            if (var3 < 0) {
               var3 = 0;
            }
         } catch (Throwable var7) {
            return null;
         }
      }

      int var4;
      try {
         String var6 = var2[var2.length - 1];
         var2 = var6.split(":", 2);
         var4 = Integer.parseInt(var2[0]);
         if (var2.length > 1) {
            var5 = Integer.parseInt(var2[1]);
         }

         if (Block.func_149729_e(var4) == Blocks.field_150350_a) {
            var4 = 0;
            var5 = 0;
         }

         if (var5 < 0 || var5 > 15) {
            var5 = 0;
         }
      } catch (Throwable var8) {
         return null;
      }

      FlatLayerInfo var10 = new FlatLayerInfo(var3, Block.func_149729_e(var4), var5);
      var10.func_82660_d(var1);
      return var10;
   }

   private static List func_82652_b(String var0) {
      if (var0 != null && var0.length() >= 1) {
         ArrayList var1 = new ArrayList();
         String[] var2 = var0.split(",");
         int var3 = 0;

         for(String var7 : var2) {
            FlatLayerInfo var8 = func_82646_a(var7, var3);
            if (var8 == null) {
               return null;
            }

            var1.add(var8);
            var3 += var8.func_82657_a();
         }

         return var1;
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
         if (var2 >= 0 && var2 <= 2) {
            FlatGeneratorInfo var3 = new FlatGeneratorInfo();
            int var4 = var1.length == 1 ? 0 : 1;
            List var5 = func_82652_b(var1[var4++]);
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

                  for(String var11 : var7) {
                     String[] var12 = var11.split("\\(", 2);
                     HashMap var13 = new HashMap();
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
                  var3.func_82644_b().put("village", new HashMap());
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
      var0.func_82644_b().put("village", new HashMap());
      return var0;
   }
}
