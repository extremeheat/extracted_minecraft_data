package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.Deserializer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class ListPoolElement extends StructurePoolElement {
   private final List elements;

   @Deprecated
   public ListPoolElement(List var1) {
      this(var1, StructureTemplatePool.Projection.RIGID);
   }

   public ListPoolElement(List var1, StructureTemplatePool.Projection var2) {
      super(var2);
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Elements are empty");
      } else {
         this.elements = var1;
         this.setProjectionOnEachElement(var2);
      }
   }

   public ListPoolElement(Dynamic var1) {
      super(var1);
      List var2 = var1.get("elements").asList((var0) -> {
         return (StructurePoolElement)Deserializer.deserialize(var0, Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE);
      });
      if (var2.isEmpty()) {
         throw new IllegalArgumentException("Elements are empty");
      } else {
         this.elements = var2;
      }
   }

   public List getShuffledJigsawBlocks(StructureManager var1, BlockPos var2, Rotation var3, Random var4) {
      return ((StructurePoolElement)this.elements.get(0)).getShuffledJigsawBlocks(var1, var2, var3, var4);
   }

   public BoundingBox getBoundingBox(StructureManager var1, BlockPos var2, Rotation var3) {
      BoundingBox var4 = BoundingBox.getUnknownBox();
      Iterator var5 = this.elements.iterator();

      while(var5.hasNext()) {
         StructurePoolElement var6 = (StructurePoolElement)var5.next();
         BoundingBox var7 = var6.getBoundingBox(var1, var2, var3);
         var4.expand(var7);
      }

      return var4;
   }

   public boolean place(StructureManager var1, LevelAccessor var2, ChunkGenerator var3, BlockPos var4, Rotation var5, BoundingBox var6, Random var7) {
      Iterator var8 = this.elements.iterator();

      StructurePoolElement var9;
      do {
         if (!var8.hasNext()) {
            return true;
         }

         var9 = (StructurePoolElement)var8.next();
      } while(var9.place(var1, var2, var3, var4, var5, var6, var7));

      return false;
   }

   public StructurePoolElementType getType() {
      return StructurePoolElementType.LIST;
   }

   public StructurePoolElement setProjection(StructureTemplatePool.Projection var1) {
      super.setProjection(var1);
      this.setProjectionOnEachElement(var1);
      return this;
   }

   public Dynamic getDynamic(DynamicOps var1) {
      Object var2 = var1.createList(this.elements.stream().map((var1x) -> {
         return var1x.serialize(var1).getValue();
      }));
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("elements"), var2)));
   }

   public String toString() {
      return "List[" + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
   }

   private void setProjectionOnEachElement(StructureTemplatePool.Projection var1) {
      this.elements.forEach((var1x) -> {
         var1x.setProjection(var1);
      });
   }
}
