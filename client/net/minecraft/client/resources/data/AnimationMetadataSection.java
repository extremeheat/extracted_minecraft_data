package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AnimationMetadataSection {
   public static final AnimationMetadataSectionSerializer field_195817_a = new AnimationMetadataSectionSerializer();
   private final List<AnimationFrame> field_110478_a;
   private final int field_110476_b;
   private final int field_110477_c;
   private final int field_110475_d;
   private final boolean field_177220_e;

   public AnimationMetadataSection(List<AnimationFrame> var1, int var2, int var3, int var4, boolean var5) {
      super();
      this.field_110478_a = var1;
      this.field_110476_b = var2;
      this.field_110477_c = var3;
      this.field_110475_d = var4;
      this.field_177220_e = var5;
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

   public boolean func_177219_e() {
      return this.field_177220_e;
   }

   private AnimationFrame func_130072_d(int var1) {
      return (AnimationFrame)this.field_110478_a.get(var1);
   }

   public int func_110472_a(int var1) {
      AnimationFrame var2 = this.func_130072_d(var1);
      return var2.func_110495_a() ? this.field_110475_d : var2.func_110497_b();
   }

   public int func_110468_c(int var1) {
      return ((AnimationFrame)this.field_110478_a.get(var1)).func_110496_c();
   }

   public Set<Integer> func_130073_e() {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = this.field_110478_a.iterator();

      while(var2.hasNext()) {
         AnimationFrame var3 = (AnimationFrame)var2.next();
         var1.add(var3.func_110496_c());
      }

      return var1;
   }
}
