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
import net.minecraft.util.Util;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class RemoveBlockEntityTagFix extends DataFix {
   private final Set<String> blockEntityIdsToDrop;

   public RemoveBlockEntityTagFix(final Schema outputSchema, final Set<String> blockEntityIdsToDrop) {
      super(outputSchema, true);
      this.blockEntityIdsToDrop = blockEntityIdsToDrop;
   }

   public TypeRewriteRule makeRule() {
      Type<?> itemStackType = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<?> itemTagF = itemStackType.findField("tag");
      OpticFinder<?> itemBlockEntityF = itemTagF.type().findField("BlockEntityTag");
      Type<?> entityType = this.getInputSchema().getType(References.ENTITY);
      OpticFinder<?> fallingBlockF = DSL.namedChoice("minecraft:falling_block", this.getInputSchema().getChoiceType(References.ENTITY, "minecraft:falling_block"));
      OpticFinder<?> fallingBlockEntityTagF = fallingBlockF.type().findField("TileEntityData");
      Type<?> structureType = this.getInputSchema().getType(References.STRUCTURE);
      OpticFinder<?> blocksF = structureType.findField("blocks");
      OpticFinder<?> blockTypeF = DSL.typeFinder(((List.ListType)blocksF.type()).getElement());
      OpticFinder<?> blockNbtF = blockTypeF.type().findField("nbt");
      OpticFinder<String> blockEntityIdF = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("ItemRemoveBlockEntityTagFix", itemStackType, (input) -> input.updateTyped(itemTagF, (tag) -> this.removeBlockEntity(tag, itemBlockEntityF, blockEntityIdF, "BlockEntityTag"))), new TypeRewriteRule[]{this.fixTypeEverywhereTyped("FallingBlockEntityRemoveBlockEntityTagFix", entityType, (input) -> input.updateTyped(fallingBlockF, (tag) -> this.removeBlockEntity(tag, fallingBlockEntityTagF, blockEntityIdF, "TileEntityData"))), this.fixTypeEverywhereTyped("StructureRemoveBlockEntityTagFix", structureType, (input) -> input.updateTyped(blocksF, (tag) -> tag.updateTyped(blockTypeF, (blockTag) -> this.removeBlockEntity(blockTag, blockNbtF, blockEntityIdF, "nbt")))), this.convertUnchecked("ItemRemoveBlockEntityTagFix - update block entity type", this.getInputSchema().getType(References.BLOCK_ENTITY), this.getOutputSchema().getType(References.BLOCK_ENTITY))});
   }

   private Typed<?> removeBlockEntity(final Typed<?> tag, final OpticFinder<?> blockEntityF, final OpticFinder<String> blockEntityIdF, final String blockEntityFieldName) {
      Optional<? extends Typed<?>> maybeBlockEntity = tag.getOptionalTyped(blockEntityF);
      if (maybeBlockEntity.isEmpty()) {
         return tag;
      } else {
         String blockEntityId = (String)((Typed)maybeBlockEntity.get()).getOptional(blockEntityIdF).orElse("");
         return !this.blockEntityIdsToDrop.contains(blockEntityId) ? tag : Util.writeAndReadTypedOrThrow(tag, tag.getType(), (tagData) -> tagData.remove(blockEntityFieldName));
      }
   }
}
