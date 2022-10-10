package net.minecraft.tags;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class NetworkTagCollection<T> extends TagCollection<T> {
   private final IRegistry<T> field_200044_a;

   public NetworkTagCollection(IRegistry<T> var1, String var2, String var3) {
      super(var1::func_212607_c, var1::func_212608_b, var2, false, var3);
      this.field_200044_a = var1;
   }

   public void func_200042_a(PacketBuffer var1) {
      var1.func_150787_b(this.func_200039_c().size());
      Iterator var2 = this.func_200039_c().entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.func_192572_a((ResourceLocation)var3.getKey());
         var1.func_150787_b(((Tag)var3.getValue()).func_199885_a().size());
         Iterator var4 = ((Tag)var3.getValue()).func_199885_a().iterator();

         while(var4.hasNext()) {
            Object var5 = var4.next();
            var1.func_150787_b(this.field_200044_a.func_148757_b(var5));
         }
      }

   }

   public void func_200043_b(PacketBuffer var1) {
      int var2 = var1.func_150792_a();

      for(int var3 = 0; var3 < var2; ++var3) {
         ResourceLocation var4 = var1.func_192575_l();
         int var5 = var1.func_150792_a();
         ArrayList var6 = Lists.newArrayList();

         for(int var7 = 0; var7 < var5; ++var7) {
            var6.add(this.field_200044_a.func_148754_a(var1.func_150792_a()));
         }

         this.func_200039_c().put(var4, Tag.Builder.func_200047_a().func_200046_a(var6).func_200051_a(var4));
      }

   }
}
