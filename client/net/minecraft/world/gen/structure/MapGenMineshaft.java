package net.minecraft.world.gen.structure;

import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.MathHelper;

public class MapGenMineshaft extends MapGenStructure {
   private double field_82673_e = 0.004;

   public MapGenMineshaft() {
      super();
   }

   @Override
   public String func_143025_a() {
      return "Mineshaft";
   }

   public MapGenMineshaft(Map var1) {
      super();

      for(Entry var3 : var1.entrySet()) {
         if (((String)var3.getKey()).equals("chance")) {
            this.field_82673_e = MathHelper.func_82712_a((String)var3.getValue(), this.field_82673_e);
         }
      }
   }

   @Override
   protected boolean func_75047_a(int var1, int var2) {
      return this.field_75038_b.nextDouble() < this.field_82673_e && this.field_75038_b.nextInt(80) < Math.max(Math.abs(var1), Math.abs(var2));
   }

   @Override
   protected StructureStart func_75049_b(int var1, int var2) {
      return new StructureMineshaftStart(this.field_75039_c, this.field_75038_b, var1, var2);
   }
}
