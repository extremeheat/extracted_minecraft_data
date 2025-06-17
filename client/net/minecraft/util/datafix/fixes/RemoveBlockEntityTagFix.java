package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class RemoveBlockEntityTagFix extends DataFix {
   private final Set<String> blockEntityIdsToDrop;

   public RemoveBlockEntityTagFix(Schema var1, Set<String> var2) {
      super(var1, true);
      this.blockEntityIdsToDrop = var2;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = var1.findField("tag");
      OpticFinder var3 = var2.type().findField("BlockEntityTag");
      Type var4 = this.getInputSchema().getType(References.ENTITY);
      OpticFinder var5 = DSL.namedChoice("minecraft:falling_block", this.getInputSchema().getChoiceType(References.ENTITY, "minecraft:falling_block"));
      OpticFinder var6 = var5.type().findField("TileEntityData");
      Type var7 = this.getInputSchema().getType(References.STRUCTURE);
      OpticFinder var8 = var7.findField("blocks");
      OpticFinder var9 = DSL.typeFinder(((List.ListType)var8.type()).getElement());
      OpticFinder var10 = var9.type().findField("nbt");
      OpticFinder var11 = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("ItemRemoveBlockEntityTagFix", var1, (var4x) -> var4x.updateTyped(var2, (var3x) -> this.removeBlockEntity(var3x, var3, var11, "BlockEntityTag"))), new TypeRewriteRule[]{this.fixTypeEverywhereTyped("FallingBlockEntityRemoveBlockEntityTagFix", var4, (var4x) -> var4x.updateTyped(var5, (var3) -> this.removeBlockEntity(var3, var6, var11, "TileEntityData"))), this.fixTypeEverywhereTyped("StructureRemoveBlockEntityTagFix", var7, (var5x) -> var5x.updateTyped(var8, (var4) -> var4.updateTyped(var9, (var3) -> this.removeBlockEntity(var3, var10, var11, "nbt")))), this.convertUnchecked("ItemRemoveBlockEntityTagFix - update block entity type", this.getInputSchema().getType(References.BLOCK_ENTITY), this.getOutputSchema().getType(References.BLOCK_ENTITY))});
   }

   private Typed<?> removeBlockEntity(Typed<?> var1, OpticFinder<?> var2, OpticFinder<String> var3, String var4) {
      Optional var5 = var1.getOptionalTyped(var2);
      if (var5.isEmpty()) {
         return var1;
      } else {
         String var6 = (String)((Typed)var5.get()).getOptional(var3).orElse("");
         return !this.blockEntityIdsToDrop.contains(var6) ? var1 : Util.writeAndReadTypedOrThrow(var1, var1.getType(), (var1x) -> var1x.remove(var4));
      }
   }
}
