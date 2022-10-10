package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class TextComponentBase implements ITextComponent {
   protected List<ITextComponent> field_150264_a = Lists.newArrayList();
   private Style field_150263_b;

   public TextComponentBase() {
      super();
   }

   public ITextComponent func_150257_a(ITextComponent var1) {
      var1.func_150256_b().func_150221_a(this.func_150256_b());
      this.field_150264_a.add(var1);
      return this;
   }

   public List<ITextComponent> func_150253_a() {
      return this.field_150264_a;
   }

   public ITextComponent func_150255_a(Style var1) {
      this.field_150263_b = var1;
      Iterator var2 = this.field_150264_a.iterator();

      while(var2.hasNext()) {
         ITextComponent var3 = (ITextComponent)var2.next();
         var3.func_150256_b().func_150221_a(this.func_150256_b());
      }

      return this;
   }

   public Style func_150256_b() {
      if (this.field_150263_b == null) {
         this.field_150263_b = new Style();
         Iterator var1 = this.field_150264_a.iterator();

         while(var1.hasNext()) {
            ITextComponent var2 = (ITextComponent)var1.next();
            var2.func_150256_b().func_150221_a(this.field_150263_b);
         }
      }

      return this.field_150263_b;
   }

   public Stream<ITextComponent> func_212640_c() {
      return Streams.concat(new Stream[]{Stream.of(this), this.field_150264_a.stream().flatMap(ITextComponent::func_212640_c)});
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TextComponentBase)) {
         return false;
      } else {
         TextComponentBase var2 = (TextComponentBase)var1;
         return this.field_150264_a.equals(var2.field_150264_a) && this.func_150256_b().equals(var2.func_150256_b());
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.func_150256_b(), this.field_150264_a});
   }

   public String toString() {
      return "BaseComponent{style=" + this.field_150263_b + ", siblings=" + this.field_150264_a + '}';
   }
}
