package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentStyle implements IChatComponent {
   protected List field_150264_a = Lists.newArrayList();
   private ChatStyle field_150263_b;

   public ChatComponentStyle() {
      super();
   }

   @Override
   public IChatComponent func_150257_a(IChatComponent var1) {
      var1.func_150256_b().func_150221_a(this.func_150256_b());
      this.field_150264_a.add(var1);
      return this;
   }

   @Override
   public List func_150253_a() {
      return this.field_150264_a;
   }

   @Override
   public IChatComponent func_150258_a(String var1) {
      return this.func_150257_a(new ChatComponentText(var1));
   }

   @Override
   public IChatComponent func_150255_a(ChatStyle var1) {
      this.field_150263_b = var1;

      for(IChatComponent var3 : this.field_150264_a) {
         var3.func_150256_b().func_150221_a(this.func_150256_b());
      }

      return this;
   }

   @Override
   public ChatStyle func_150256_b() {
      if (this.field_150263_b == null) {
         this.field_150263_b = new ChatStyle();

         for(IChatComponent var2 : this.field_150264_a) {
            var2.func_150256_b().func_150221_a(this.field_150263_b);
         }
      }

      return this.field_150263_b;
   }

   @Override
   public Iterator iterator() {
      return Iterators.concat(Iterators.forArray(new ChatComponentStyle[]{this}), func_150262_a(this.field_150264_a));
   }

   @Override
   public final String func_150260_c() {
      StringBuilder var1 = new StringBuilder();

      for(IChatComponent var3 : this) {
         var1.append(var3.func_150261_e());
      }

      return var1.toString();
   }

   @Override
   public final String func_150254_d() {
      StringBuilder var1 = new StringBuilder();

      for(IChatComponent var3 : this) {
         var1.append(var3.func_150256_b().func_150218_j());
         var1.append(var3.func_150261_e());
         var1.append(EnumChatFormatting.RESET);
      }

      return var1.toString();
   }

   public static Iterator func_150262_a(Iterable var0) {
      Iterator var1 = Iterators.concat(Iterators.transform(var0.iterator(), new ChatComponentStyle$1()));
      return Iterators.transform(var1, new ChatComponentStyle$2());
   }

   @Override
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

   @Override
   public int hashCode() {
      return 31 * this.field_150263_b.hashCode() + this.field_150264_a.hashCode();
   }

   @Override
   public String toString() {
      return "BaseComponent{style=" + this.field_150263_b + ", siblings=" + this.field_150264_a + '}';
   }
}
