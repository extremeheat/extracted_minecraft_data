package net.minecraft.stats;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;

public class StatFileWriter {
   protected final Map<StatBase, TupleIntJsonSerializable> field_150875_a = Maps.newConcurrentMap();

   public StatFileWriter() {
      super();
   }

   public boolean func_77443_a(Achievement var1) {
      return this.func_77444_a(var1) > 0;
   }

   public boolean func_77442_b(Achievement var1) {
      return var1.field_75992_c == null || this.func_77443_a(var1.field_75992_c);
   }

   public int func_150874_c(Achievement var1) {
      if (this.func_77443_a(var1)) {
         return 0;
      } else {
         int var2 = 0;

         for(Achievement var3 = var1.field_75992_c; var3 != null && !this.func_77443_a(var3); ++var2) {
            var3 = var3.field_75992_c;
         }

         return var2;
      }
   }

   public void func_150871_b(EntityPlayer var1, StatBase var2, int var3) {
      if (!var2.func_75967_d() || this.func_77442_b((Achievement)var2)) {
         this.func_150873_a(var1, var2, this.func_77444_a(var2) + var3);
      }
   }

   public void func_150873_a(EntityPlayer var1, StatBase var2, int var3) {
      TupleIntJsonSerializable var4 = (TupleIntJsonSerializable)this.field_150875_a.get(var2);
      if (var4 == null) {
         var4 = new TupleIntJsonSerializable();
         this.field_150875_a.put(var2, var4);
      }

      var4.func_151188_a(var3);
   }

   public int func_77444_a(StatBase var1) {
      TupleIntJsonSerializable var2 = (TupleIntJsonSerializable)this.field_150875_a.get(var1);
      return var2 == null ? 0 : var2.func_151189_a();
   }

   public <T extends IJsonSerializable> T func_150870_b(StatBase var1) {
      TupleIntJsonSerializable var2 = (TupleIntJsonSerializable)this.field_150875_a.get(var1);
      return var2 != null ? var2.func_151187_b() : null;
   }

   public <T extends IJsonSerializable> T func_150872_a(StatBase var1, T var2) {
      TupleIntJsonSerializable var3 = (TupleIntJsonSerializable)this.field_150875_a.get(var1);
      if (var3 == null) {
         var3 = new TupleIntJsonSerializable();
         this.field_150875_a.put(var1, var3);
      }

      var3.func_151190_a(var2);
      return var2;
   }
}
