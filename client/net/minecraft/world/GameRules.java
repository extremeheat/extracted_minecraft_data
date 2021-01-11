package net.minecraft.world;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules {
   private TreeMap<String, GameRules.Value> field_82771_a = new TreeMap();

   public GameRules() {
      super();
      this.func_180262_a("doFireTick", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("mobGriefing", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("keepInventory", "false", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("doMobSpawning", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("doMobLoot", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("doTileDrops", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("doEntityDrops", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("commandBlockOutput", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("naturalRegeneration", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("doDaylightCycle", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("logAdminCommands", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("showDeathMessages", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("randomTickSpeed", "3", GameRules.ValueType.NUMERICAL_VALUE);
      this.func_180262_a("sendCommandFeedback", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.func_180262_a("reducedDebugInfo", "false", GameRules.ValueType.BOOLEAN_VALUE);
   }

   public void func_180262_a(String var1, String var2, GameRules.ValueType var3) {
      this.field_82771_a.put(var1, new GameRules.Value(var2, var3));
   }

   public void func_82764_b(String var1, String var2) {
      GameRules.Value var3 = (GameRules.Value)this.field_82771_a.get(var1);
      if (var3 != null) {
         var3.func_82757_a(var2);
      } else {
         this.func_180262_a(var1, var2, GameRules.ValueType.ANY_VALUE);
      }

   }

   public String func_82767_a(String var1) {
      GameRules.Value var2 = (GameRules.Value)this.field_82771_a.get(var1);
      return var2 != null ? var2.func_82756_a() : "";
   }

   public boolean func_82766_b(String var1) {
      GameRules.Value var2 = (GameRules.Value)this.field_82771_a.get(var1);
      return var2 != null ? var2.func_82758_b() : false;
   }

   public int func_180263_c(String var1) {
      GameRules.Value var2 = (GameRules.Value)this.field_82771_a.get(var1);
      return var2 != null ? var2.func_180255_c() : 0;
   }

   public NBTTagCompound func_82770_a() {
      NBTTagCompound var1 = new NBTTagCompound();
      Iterator var2 = this.field_82771_a.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         GameRules.Value var4 = (GameRules.Value)this.field_82771_a.get(var3);
         var1.func_74778_a(var3, var4.func_82756_a());
      }

      return var1;
   }

   public void func_82768_a(NBTTagCompound var1) {
      Set var2 = var1.func_150296_c();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         String var6 = var1.func_74779_i(var4);
         this.func_82764_b(var4, var6);
      }

   }

   public String[] func_82763_b() {
      Set var1 = this.field_82771_a.keySet();
      return (String[])var1.toArray(new String[var1.size()]);
   }

   public boolean func_82765_e(String var1) {
      return this.field_82771_a.containsKey(var1);
   }

   public boolean func_180264_a(String var1, GameRules.ValueType var2) {
      GameRules.Value var3 = (GameRules.Value)this.field_82771_a.get(var1);
      return var3 != null && (var3.func_180254_e() == var2 || var2 == GameRules.ValueType.ANY_VALUE);
   }

   public static enum ValueType {
      ANY_VALUE,
      BOOLEAN_VALUE,
      NUMERICAL_VALUE;

      private ValueType() {
      }
   }

   static class Value {
      private String field_82762_a;
      private boolean field_82760_b;
      private int field_82761_c;
      private double field_82759_d;
      private final GameRules.ValueType field_180256_e;

      public Value(String var1, GameRules.ValueType var2) {
         super();
         this.field_180256_e = var2;
         this.func_82757_a(var1);
      }

      public void func_82757_a(String var1) {
         this.field_82762_a = var1;
         this.field_82760_b = Boolean.parseBoolean(var1);
         this.field_82761_c = this.field_82760_b ? 1 : 0;

         try {
            this.field_82761_c = Integer.parseInt(var1);
         } catch (NumberFormatException var4) {
         }

         try {
            this.field_82759_d = Double.parseDouble(var1);
         } catch (NumberFormatException var3) {
         }

      }

      public String func_82756_a() {
         return this.field_82762_a;
      }

      public boolean func_82758_b() {
         return this.field_82760_b;
      }

      public int func_180255_c() {
         return this.field_82761_c;
      }

      public GameRules.ValueType func_180254_e() {
         return this.field_180256_e;
      }
   }
}
