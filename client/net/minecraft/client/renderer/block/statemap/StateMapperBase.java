package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public abstract class StateMapperBase implements IStateMapper {
   protected Map<IBlockState, ModelResourceLocation> field_178133_b = Maps.newLinkedHashMap();

   public StateMapperBase() {
      super();
   }

   public String func_178131_a(Map<IProperty, Comparable> var1) {
      StringBuilder var2 = new StringBuilder();
      Iterator var3 = var1.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (var2.length() != 0) {
            var2.append(",");
         }

         IProperty var5 = (IProperty)var4.getKey();
         Comparable var6 = (Comparable)var4.getValue();
         var2.append(var5.func_177701_a());
         var2.append("=");
         var2.append(var5.func_177702_a(var6));
      }

      if (var2.length() == 0) {
         var2.append("normal");
      }

      return var2.toString();
   }

   public Map<IBlockState, ModelResourceLocation> func_178130_a(Block var1) {
      Iterator var2 = var1.func_176194_O().func_177619_a().iterator();

      while(var2.hasNext()) {
         IBlockState var3 = (IBlockState)var2.next();
         this.field_178133_b.put(var3, this.func_178132_a(var3));
      }

      return this.field_178133_b;
   }

   protected abstract ModelResourceLocation func_178132_a(IBlockState var1);
}
