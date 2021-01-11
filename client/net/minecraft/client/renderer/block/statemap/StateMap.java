package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

public class StateMap extends StateMapperBase {
   private final IProperty<?> field_178142_a;
   private final String field_178141_c;
   private final List<IProperty<?>> field_178140_d;

   private StateMap(IProperty<?> var1, String var2, List<IProperty<?>> var3) {
      super();
      this.field_178142_a = var1;
      this.field_178141_c = var2;
      this.field_178140_d = var3;
   }

   protected ModelResourceLocation func_178132_a(IBlockState var1) {
      LinkedHashMap var2 = Maps.newLinkedHashMap(var1.func_177228_b());
      String var3;
      if (this.field_178142_a == null) {
         var3 = ((ResourceLocation)Block.field_149771_c.func_177774_c(var1.func_177230_c())).toString();
      } else {
         var3 = this.field_178142_a.func_177702_a((Comparable)var2.remove(this.field_178142_a));
      }

      if (this.field_178141_c != null) {
         var3 = var3 + this.field_178141_c;
      }

      Iterator var4 = this.field_178140_d.iterator();

      while(var4.hasNext()) {
         IProperty var5 = (IProperty)var4.next();
         var2.remove(var5);
      }

      return new ModelResourceLocation(var3, this.func_178131_a(var2));
   }

   // $FF: synthetic method
   StateMap(IProperty var1, String var2, List var3, Object var4) {
      this(var1, var2, var3);
   }

   public static class Builder {
      private IProperty<?> field_178445_a;
      private String field_178443_b;
      private final List<IProperty<?>> field_178444_c = Lists.newArrayList();

      public Builder() {
         super();
      }

      public StateMap.Builder func_178440_a(IProperty<?> var1) {
         this.field_178445_a = var1;
         return this;
      }

      public StateMap.Builder func_178439_a(String var1) {
         this.field_178443_b = var1;
         return this;
      }

      public StateMap.Builder func_178442_a(IProperty<?>... var1) {
         Collections.addAll(this.field_178444_c, var1);
         return this;
      }

      public StateMap func_178441_a() {
         return new StateMap(this.field_178445_a, this.field_178443_b, this.field_178444_c);
      }
   }
}
