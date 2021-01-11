package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentStyle implements IChatComponent {
   protected List<IChatComponent> field_150264_a = Lists.newArrayList();
   private ChatStyle field_150263_b;

   public ChatComponentStyle() {
      super();
   }

   public IChatComponent func_150257_a(IChatComponent var1) {
      var1.func_150256_b().func_150221_a(this.func_150256_b());
      this.field_150264_a.add(var1);
      return this;
   }

   public List<IChatComponent> func_150253_a() {
      return this.field_150264_a;
   }

   public IChatComponent func_150258_a(String var1) {
      return this.func_150257_a(new ChatComponentText(var1));
   }

   public IChatComponent func_150255_a(ChatStyle var1) {
      this.field_150263_b = var1;
      Iterator var2 = this.field_150264_a.iterator();

      while(var2.hasNext()) {
         IChatComponent var3 = (IChatComponent)var2.next();
         var3.func_150256_b().func_150221_a(this.func_150256_b());
      }

      return this;
   }

   public ChatStyle func_150256_b() {
      if (this.field_150263_b == null) {
         this.field_150263_b = new ChatStyle();
         Iterator var1 = this.field_150264_a.iterator();

         while(var1.hasNext()) {
            IChatComponent var2 = (IChatComponent)var1.next();
            var2.func_150256_b().func_150221_a(this.field_150263_b);
         }
      }

      return this.field_150263_b;
   }

   public Iterator<IChatComponent> iterator() {
      return Iterators.concat(Iterators.forArray(new ChatComponentStyle[]{this}), func_150262_a(this.field_150264_a));
   }

   public final String func_150260_c() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         IChatComponent var3 = (IChatComponent)var2.next();
         var1.append(var3.func_150261_e());
      }

      return var1.toString();
   }

   public final String func_150254_d() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         IChatComponent var3 = (IChatComponent)var2.next();
         var1.append(var3.func_150256_b().func_150218_j());
         var1.append(var3.func_150261_e());
         var1.append(EnumChatFormatting.RESET);
      }

      return var1.toString();
   }

   public static Iterator<IChatComponent> func_150262_a(Iterable<IChatComponent> var0) {
      Iterator var1 = Iterators.concat(Iterators.transform(var0.iterator(), new Function<IChatComponent, Iterator<IChatComponent>>() {
         public Iterator<IChatComponent> apply(IChatComponent var1) {
            return var1.iterator();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((IChatComponent)var1);
         }
      }));
      var1 = Iterators.transform(var1, new Function<IChatComponent, IChatComponent>() {
         public IChatComponent apply(IChatComponent var1) {
            IChatComponent var2 = var1.func_150259_f();
            var2.func_150255_a(var2.func_150256_b().func_150206_m());
            return var2;
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((IChatComponent)var1);
         }
      });
      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ChatComponentStyle)) {
         return false;
      } else {
         ChatComponentStyle var2 = (ChatComponentStyle)var1;
         return this.field_150264_a.equals(var2.field_150264_a) && this.func_150256_b().equals(var2.func_150256_b());
      }
   }

   public int hashCode() {
      return 31 * this.field_150263_b.hashCode() + this.field_150264_a.hashCode();
   }

   public String toString() {
      return "BaseComponent{style=" + this.field_150263_b + ", siblings=" + this.field_150264_a + '}';
   }
}
