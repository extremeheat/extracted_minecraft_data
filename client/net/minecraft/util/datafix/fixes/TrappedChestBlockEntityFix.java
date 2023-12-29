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
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class TrappedChestBlockEntityFix extends DataFix {
   private static final Logger LOGGER = LogUtils.getLogger();
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
            return TypeRewriteRule.seq(
               new AddNewChoices(this.getOutputSchema(), "AddTrappedChestFix", References.BLOCK_ENTITY).makeRule(),
               this.fixTypeEverywhereTyped(
                  "Trapped Chest fix",
                  var6,
                  var5x -> var5x.updateTyped(
                        var7,
                        var4xx -> {
                           Optional var5xxx = var4xx.getOptionalTyped(var8);
                           if (var5xxx.isEmpty()) {
                              return var4xx;
                           } else {
                              List var6xx = ((Typed)var5xxx.get()).getAllTyped(var11);
                              IntOpenHashSet var7xx = new IntOpenHashSet();
         
                              for(Typed var9xx : var6xx) {
                                 TrappedChestBlockEntityFix.TrappedChestSection var10xx = new TrappedChestBlockEntityFix.TrappedChestSection(
                                    var9xx, this.getInputSchema()
                                 );
                                 if (!var10xx.isSkippable()) {
                                    for(int var11xx = 0; var11xx < 4096; ++var11xx) {
                                       int var12 = var10xx.getBlock(var11xx);
                                       if (var10xx.isTrappedChest(var12)) {
                                          var7xx.add(var10xx.getIndex() << 12 | var11xx);
                                       }
                                    }
                                 }
                              }
         
                              Dynamic var13 = (Dynamic)var4xx.get(DSL.remainderFinder());
                              int var14 = var13.get("xPos").asInt(0);
                              int var15 = var13.get("zPos").asInt(0);
                              TaggedChoiceType var16 = this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
                              return var4xx.updateTyped(
                                 var5,
                                 var4xxx -> var4xxx.updateTyped(
                                       var16.finder(),
                                       var4xxxx -> {
                                          Dynamic var5xxxx = (Dynamic)var4xxxx.getOrCreate(DSL.remainderFinder());
                                          int var6xxx = var5xxxx.get("x").asInt(0) - (var14 << 4);
                                          int var7xxx = var5xxxx.get("y").asInt(0);
                                          int var8xx = var5xxxx.get("z").asInt(0) - (var15 << 4);
                                          return var7x.contains(LeavesFix.getIndex(var6xxx, var7xxx, var8xx))
                                             ? var4xxxx.update(var16.finder(), var0xx -> var0xx.mapFirst(var0xxx -> {
                                                   if (!Objects.equals(var0xxx, "minecraft:chest")) {
                                                      LOGGER.warn("Block Entity was expected to be a chest");
                                                   }
                  
                                                   return "minecraft:trapped_chest";
                                                }))
                                             : var4xxxx;
                                       }
                                    )
                              );
                           }
                        }
                     )
               )
            );
         }
      }
   }

   public static final class TrappedChestSection extends LeavesFix.Section {
      @Nullable
      private IntSet chestIds;

      public TrappedChestSection(Typed<?> var1, Schema var2) {
         super(var1, var2);
      }

      @Override
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
