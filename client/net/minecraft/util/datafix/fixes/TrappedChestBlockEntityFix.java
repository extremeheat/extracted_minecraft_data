package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrappedChestBlockEntityFix extends DataFix {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int SIZE = 4096;
   private static final short SIZE_BITS = 12;

   public TrappedChestBlockEntityFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getOutputSchema().getType(References.CHUNK);
      Type var2 = var1.findFieldType("Level");
      Type var3 = var2.findFieldType("TileEntities");
      if (!(var3 instanceof ListType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         ListType var4 = (ListType)var3;
         OpticFinder var5 = DSL.fieldFinder("TileEntities", var4);
         Type var6 = this.getInputSchema().getType(References.CHUNK);
         OpticFinder var7 = var6.findField("Level");
         OpticFinder var8 = var7.type().findField("Sections");
         Type var9 = var8.type();
         if (!(var9 instanceof ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
         } else {
            Type var10 = ((ListType)var9).getElement();
            OpticFinder var11 = DSL.typeFinder(var10);
            return TypeRewriteRule.seq((new AddNewChoices(this.getOutputSchema(), "AddTrappedChestFix", References.BLOCK_ENTITY)).makeRule(), this.fixTypeEverywhereTyped("Trapped Chest fix", var6, (var5x) -> {
               return var5x.updateTyped(var7, (var4) -> {
                  Optional var5x = var4.getOptionalTyped(var8);
                  if (!var5x.isPresent()) {
                     return var4;
                  } else {
                     List var6 = ((Typed)var5x.get()).getAllTyped(var11);
                     IntOpenHashSet var7 = new IntOpenHashSet();
                     Iterator var8x = var6.iterator();

                     while(true) {
                        TrappedChestBlockEntityFix.TrappedChestSection var10;
                        do {
                           if (!var8x.hasNext()) {
                              Dynamic var13 = (Dynamic)var4.get(DSL.remainderFinder());
                              int var14 = var13.get("xPos").asInt(0);
                              int var15 = var13.get("zPos").asInt(0);
                              TaggedChoiceType var16 = this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
                              return var4.updateTyped(var5, (var4x) -> {
                                 return var4x.updateTyped(var16.finder(), (var4) -> {
                                    Dynamic var5 = (Dynamic)var4.getOrCreate(DSL.remainderFinder());
                                    int var6 = var5.get("x").asInt(0) - (var14 << 4);
                                    int var7x = var5.get("y").asInt(0);
                                    int var8 = var5.get("z").asInt(0) - (var15 << 4);
                                    return var7.contains(LeavesFix.getIndex(var6, var7x, var8)) ? var4.update(var16.finder(), (var0) -> {
                                       return var0.mapFirst((var0x) -> {
                                          if (!Objects.equals(var0x, "minecraft:chest")) {
                                             LOGGER.warn("Block Entity was expected to be a chest");
                                          }

                                          return "minecraft:trapped_chest";
                                       });
                                    }) : var4;
                                 });
                              });
                           }

                           Typed var9 = (Typed)var8x.next();
                           var10 = new TrappedChestBlockEntityFix.TrappedChestSection(var9, this.getInputSchema());
                        } while(var10.isSkippable());

                        for(int var11x = 0; var11x < 4096; ++var11x) {
                           int var12 = var10.getBlock(var11x);
                           if (var10.isTrappedChest(var12)) {
                              var7.add(var10.getIndex() << 12 | var11x);
                           }
                        }
                     }
                  }
               });
            }));
         }
      }
   }

   public static final class TrappedChestSection extends LeavesFix.Section {
      @Nullable
      private IntSet chestIds;

      public TrappedChestSection(Typed<?> var1, Schema var2) {
         super(var1, var2);
      }

      protected boolean skippable() {
         this.chestIds = new IntOpenHashSet();

         for(int var1 = 0; var1 < this.palette.size(); ++var1) {
            Dynamic var2 = (Dynamic)this.palette.get(var1);
            String var3 = var2.get("Name").asString("");
            if (Objects.equals(var3, "minecraft:trapped_chest")) {
               this.chestIds.add(var1);
            }
         }

         return this.chestIds.isEmpty();
      }

      public boolean isTrappedChest(int var1) {
         return this.chestIds.contains(var1);
      }
   }
}
