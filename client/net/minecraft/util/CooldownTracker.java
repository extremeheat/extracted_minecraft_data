package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

public class CooldownTracker {
   private final Map<Item, CooldownTracker.Cooldown> field_185147_a = Maps.newHashMap();
   private int field_185148_b;

   public CooldownTracker() {
      super();
   }

   public boolean func_185141_a(Item var1) {
      return this.func_185143_a(var1, 0.0F) > 0.0F;
   }

   public float func_185143_a(Item var1, float var2) {
      CooldownTracker.Cooldown var3 = (CooldownTracker.Cooldown)this.field_185147_a.get(var1);
      if (var3 != null) {
         float var4 = (float)(var3.field_185138_b - var3.field_185137_a);
         float var5 = (float)var3.field_185138_b - ((float)this.field_185148_b + var2);
         return MathHelper.func_76131_a(var5 / var4, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   public void func_185144_a() {
      ++this.field_185148_b;
      if (!this.field_185147_a.isEmpty()) {
         Iterator var1 = this.field_185147_a.entrySet().iterator();

         while(var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
            if (((CooldownTracker.Cooldown)var2.getValue()).field_185138_b <= this.field_185148_b) {
               var1.remove();
               this.func_185146_c((Item)var2.getKey());
            }
         }
      }

   }

   public void func_185145_a(Item var1, int var2) {
      this.field_185147_a.put(var1, new CooldownTracker.Cooldown(this.field_185148_b, this.field_185148_b + var2));
      this.func_185140_b(var1, var2);
   }

   public void func_185142_b(Item var1) {
      this.field_185147_a.remove(var1);
      this.func_185146_c(var1);
   }

   protected void func_185140_b(Item var1, int var2) {
   }

   protected void func_185146_c(Item var1) {
   }

   class Cooldown {
      private final int field_185137_a;
      private final int field_185138_b;

      private Cooldown(int var2, int var3) {
         super();
         this.field_185137_a = var2;
         this.field_185138_b = var3;
      }

      // $FF: synthetic method
      Cooldown(int var2, int var3, Object var4) {
         this(var2, var3);
      }
   }
}
