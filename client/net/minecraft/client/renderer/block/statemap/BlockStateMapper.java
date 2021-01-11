package net.minecraft.client.renderer.block.statemap;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class BlockStateMapper {
   private Map<Block, IStateMapper> field_178450_a = Maps.newIdentityHashMap();
   private Set<Block> field_178449_b = Sets.newIdentityHashSet();

   public BlockStateMapper() {
      super();
   }

   public void func_178447_a(Block var1, IStateMapper var2) {
      this.field_178450_a.put(var1, var2);
   }

   public void func_178448_a(Block... var1) {
      Collections.addAll(this.field_178449_b, var1);
   }

   public Map<IBlockState, ModelResourceLocation> func_178446_a() {
      IdentityHashMap var1 = Maps.newIdentityHashMap();
      Iterator var2 = Block.field_149771_c.iterator();

      while(var2.hasNext()) {
         Block var3 = (Block)var2.next();
         if (!this.field_178449_b.contains(var3)) {
            var1.putAll(((IStateMapper)Objects.firstNonNull(this.field_178450_a.get(var3), new DefaultStateMapper())).func_178130_a(var3));
         }
      }

      return var1;
   }
}
