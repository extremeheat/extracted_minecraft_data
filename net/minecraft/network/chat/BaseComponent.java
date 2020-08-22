package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class BaseComponent implements Component {
   protected final List siblings = Lists.newArrayList();
   private Style style;

   public Component append(Component var1) {
      var1.getStyle().inheritFrom(this.getStyle());
      this.siblings.add(var1);
      return this;
   }

   public List getSiblings() {
      return this.siblings;
   }

   public Component setStyle(Style var1) {
      this.style = var1;
      Iterator var2 = this.siblings.iterator();

      while(var2.hasNext()) {
         Component var3 = (Component)var2.next();
         var3.getStyle().inheritFrom(this.getStyle());
      }

      return this;
   }

   public Style getStyle() {
      if (this.style == null) {
         this.style = new Style();
         Iterator var1 = this.siblings.iterator();

         while(var1.hasNext()) {
            Component var2 = (Component)var1.next();
            var2.getStyle().inheritFrom(this.style);
         }
      }

      return this.style;
   }

   public Stream stream() {
      return Streams.concat(new Stream[]{Stream.of(this), this.siblings.stream().flatMap(Component::stream)});
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof BaseComponent)) {
         return false;
      } else {
         BaseComponent var2 = (BaseComponent)var1;
         return this.siblings.equals(var2.siblings) && this.getStyle().equals(var2.getStyle());
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getStyle(), this.siblings});
   }

   public String toString() {
      return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
   }
}
