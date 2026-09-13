package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnimationMetadataSection implements IMetadataSection {
   private final List field_110478_a;
   private final int field_110476_b;
   private final int field_110477_c;
   private final int field_110475_d;

   public AnimationMetadataSection(List var1, int var2, int var3, int var4) {
      super();
      this.field_110478_a = var1;
      this.field_110476_b = var2;
      this.field_110477_c = var3;
      this.field_110475_d = var4;
   }

   public int func_110471_a() {
      return this.field_110477_c;
   }

   public int func_110474_b() {
      return this.field_110476_b;
   }

   public int func_110473_c() {
      return this.field_110478_a.size();
   }

   public int func_110469_d() {
      return this.field_110475_d;
   }

   private AnimationFrame func_130072_d(int var1) {
      return (AnimationFrame)this.field_110478_a.get(var1);
   }

   public int func_110472_a(int var1) {
      AnimationFrame var2 = this.func_130072_d(var1);
      return var2.func_110495_a() ? this.field_110475_d : var2.func_110497_b();
   }

   public boolean func_110470_b(int var1) {
      return !((AnimationFrame)this.field_110478_a.get(var1)).func_110495_a();
   }

   public int func_110468_c(int var1) {
      return ((AnimationFrame)this.field_110478_a.get(var1)).func_110496_c();
   }

   public Set func_130073_e() {
      HashSet var1 = Sets.newHashSet();

      for(AnimationFrame var3 : this.field_110478_a) {
         var1.add(var3.func_110496_c());
      }

      return var1;
   }
}
