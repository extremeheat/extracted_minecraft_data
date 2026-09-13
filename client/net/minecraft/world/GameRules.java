package net.minecraft.world;

import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules {
   private TreeMap field_82771_a = new TreeMap();

   public GameRules() {
      super();
      this.func_82769_a("doFireTick", "true");
      this.func_82769_a("mobGriefing", "true");
      this.func_82769_a("keepInventory", "false");
      this.func_82769_a("doMobSpawning", "true");
      this.func_82769_a("doMobLoot", "true");
      this.func_82769_a("doTileDrops", "true");
      this.func_82769_a("commandBlockOutput", "true");
      this.func_82769_a("naturalRegeneration", "true");
      this.func_82769_a("doDaylightCycle", "true");
   }

   public void func_82769_a(String var1, String var2) {
      this.field_82771_a.put(var1, new GameRules$Value(var2));
   }

   public void func_82764_b(String var1, String var2) {
      GameRules$Value var3 = (GameRules$Value)this.field_82771_a.get(var1);
      if (var3 != null) {
         var3.func_82757_a(var2);
      } else {
         this.func_82769_a(var1, var2);
      }
   }

   public String func_82767_a(String var1) {
      GameRules$Value var2 = (GameRules$Value)this.field_82771_a.get(var1);
      return var2 != null ? var2.func_82756_a() : "";
   }

   public boolean func_82766_b(String var1) {
      GameRules$Value var2 = (GameRules$Value)this.field_82771_a.get(var1);
      return var2 != null ? var2.func_82758_b() : false;
   }

   public NBTTagCompound func_82770_a() {
      NBTTagCompound var1 = new NBTTagCompound();

      for(String var3 : this.field_82771_a.keySet()) {
         GameRules$Value var4 = (GameRules$Value)this.field_82771_a.get(var3);
         var1.func_74778_a(var3, var4.func_82756_a());
      }

      return var1;
   }

   public void func_82768_a(NBTTagCompound var1) {
      for(String var4 : var1.func_150296_c()) {
         String var6 = var1.func_74779_i(var4);
         this.func_82764_b(var4, var6);
      }
   }

   public String[] func_82763_b() {
      return this.field_82771_a.keySet().toArray(new String[0]);
   }

   public boolean func_82765_e(String var1) {
      return this.field_82771_a.containsKey(var1);
   }
}
