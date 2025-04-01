package net.minecraft.world.inventory;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class GhettoSpline {
   private final List<Entry> entries;
   private final double totalLength;

   public GhettoSpline(List<Vec2> var1) {
      super();
      LinkedList var2 = new LinkedList(var1);
      var2.addAll(var1.reversed());
      this.entries = new LinkedList();
      double var3 = 0.0;

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         Vec2 var6 = (Vec2)var2.get(var5);
         Vec2 var7 = var5 + 1 < var2.size() ? (Vec2)var2.get(var5 + 1) : (Vec2)var2.get(var5);
         double var8 = (double)Mth.sqrt(var6.distanceToSqr(var7));
         this.entries.add(new Entry(var6, var8));
         var3 += var8;
      }

      this.totalLength = var3;
   }

   public Vec2 interpolate(float var1) {
      double var2 = this.totalLength * ((double)var1 % 1.0);

      for(int var4 = 0; var4 < this.entries.size(); ++var4) {
         Entry var5 = (Entry)this.entries.get(var4);
         var2 -= var5.distance;
         if (var2 <= 0.0) {
            Entry var6 = var4 + 1 < this.entries.size() ? (Entry)this.entries.get(var4 + 1) : (Entry)this.entries.get(var4 - 1);
            Vec2 var7 = var6.pos.add(var5.pos.negated()).normalized();
            float var8 = (float)(var5.distance + var2);
            return var5.pos.add(var7.scale(var8));
         }
      }

      return ((Entry)this.entries.getFirst()).pos;
   }

   static record Entry(Vec2 pos, double distance) {
      final Vec2 pos;
      final double distance;

      Entry(Vec2 var1, double var2) {
         super();
         this.pos = var1;
         this.distance = var2;
      }
   }
}
