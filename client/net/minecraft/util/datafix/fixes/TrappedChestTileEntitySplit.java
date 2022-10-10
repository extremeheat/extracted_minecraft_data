package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrappedChestTileEntitySplit extends DataFix {
   private static final Logger field_212536_a = LogManager.getLogger();

   public TrappedChestTileEntitySplit(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getOutputSchema().getType(TypeReferences.field_211287_c);
      Type var2 = var1.findFieldType("Level");
      Type var3 = var2.findFieldType("TileEntities");
      if (!(var3 instanceof ListType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         ListType var4 = (ListType)var3;
         OpticFinder var5 = DSL.fieldFinder("TileEntities", var4);
         Type var6 = this.getInputSchema().getType(TypeReferences.field_211287_c);
         OpticFinder var7 = var6.findField("Level");
         OpticFinder var8 = var7.type().findField("Sections");
         Type var9 = var8.type();
         if (!(var9 instanceof ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
         } else {
            Type var10 = ((ListType)var9).getElement();
            OpticFinder var11 = DSL.typeFinder(var10);
            return TypeRewriteRule.seq((new AddNewChoices(this.getOutputSchema(), "AddTrappedChestFix", TypeReferences.field_211294_j)).makeRule(), this.fixTypeEverywhereTyped("Trapped Chest fix", var6, (var5x) -> {
               return var5x.updateTyped(var7, (var4) -> {
                  Optional var5x = var4.getOptionalTyped(var8);
                  if (!var5x.isPresent()) {
                     return var4;
                  } else {
                     List var6 = ((Typed)var5x.get()).getAllTyped(var11);
                     IntOpenHashSet var7 = new IntOpenHashSet();
                     Iterator var8x = var6.iterator();

                     while(true) {
                        TrappedChestTileEntitySplit.Section var10;
                        do {
                           if (!var8x.hasNext()) {
                              Dynamic var13 = (Dynamic)var4.get(DSL.remainderFinder());
                              int var14 = var13.getInt("xPos");
                              int var15 = var13.getInt("zPos");
                              TaggedChoiceType var16 = this.getInputSchema().findChoiceType(TypeReferences.field_211294_j);
                              return var4.updateTyped(var5, (var4x) -> {
                                 return var4x.updateTyped(var16.finder(), (var4) -> {
                                    Dynamic var5 = (Dynamic)var4.getOrCreate(DSL.remainderFinder());
                                    int var6 = var5.getInt("x") - (var14 << 4);
                                    int var7x = var5.getInt("y");
                                    int var8 = var5.getInt("z") - (var15 << 4);
                                    return var7.contains(LeavesFix.func_208411_a(var6, var7x, var8)) ? var4.update(var16.finder(), (var0) -> {
                                       return var0.mapFirst((var0x) -> {
                                          if (!Objects.equals(var0x, "minecraft:chest")) {
                                             field_212536_a.warn("Block Entity was expected to be a chest");
                                          }

                                          return "minecraft:trapped_chest";
                                       });
                                    }) : var4;
                                 });
                              });
                           }

                           Typed var9 = (Typed)var8x.next();
                           var10 = new TrappedChestTileEntitySplit.Section(var9, this.getInputSchema());
                        } while(var10.func_208461_a());

                        for(int var11x = 0; var11x < 4096; ++var11x) {
                           int var12 = var10.func_208453_a(var11x);
                           if (var10.func_212511_a(var12)) {
                              var7.add(var10.func_208456_b() << 12 | var11x);
                           }
                        }
                     }
                  }
               });
            }));
         }
      }
   }

   public static final class Section extends LeavesFix.Section {
      @Nullable
      private IntSet field_212512_f;

      public Section(Typed<?> var1, Schema var2) {
         super(var1, var2);
      }

      protected boolean func_212508_a() {
         this.field_212512_f = new IntOpenHashSet();

         for(int var1 = 0; var1 < this.field_208469_d.size(); ++var1) {
            Dynamic var2 = (Dynamic)this.field_208469_d.get(var1);
            String var3 = var2.getString("Name");
            if (Objects.equals(var3, "minecraft:trapped_chest")) {
               this.field_212512_f.add(var1);
            }
         }

         return this.field_212512_f.isEmpty();
      }

      public boolean func_212511_a(int var1) {
         return this.field_212512_f.contains(var1);
      }
   }
}
