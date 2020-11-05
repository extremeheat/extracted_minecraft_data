package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

public class JigsawJunction {
   private final int sourceX;
   private final int sourceGroundY;
   private final int sourceZ;
   private final int deltaY;
   private final StructureTemplatePool.Projection destProjection;

   public JigsawJunction(int var1, int var2, int var3, int var4, StructureTemplatePool.Projection var5) {
      super();
      this.sourceX = var1;
      this.sourceGroundY = var2;
      this.sourceZ = var3;
      this.deltaY = var4;
      this.destProjection = var5;
   }

   public int getSourceX() {
      return this.sourceX;
   }

   public int getSourceGroundY() {
      return this.sourceGroundY;
   }

   public int getSourceZ() {
      return this.sourceZ;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var1.createString("source_x"), var1.createInt(this.sourceX)).put(var1.createString("source_ground_y"), var1.createInt(this.sourceGroundY)).put(var1.createString("source_z"), var1.createInt(this.sourceZ)).put(var1.createString("delta_y"), var1.createInt(this.deltaY)).put(var1.createString("dest_proj"), var1.createString(this.destProjection.getName()));
      return new Dynamic(var1, var1.createMap(var2.build()));
   }

   public static <T> JigsawJunction deserialize(Dynamic<T> var0) {
      return new JigsawJunction(var0.get("source_x").asInt(0), var0.get("source_ground_y").asInt(0), var0.get("source_z").asInt(0), var0.get("delta_y").asInt(0), StructureTemplatePool.Projection.byName(var0.get("dest_proj").asString("")));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         JigsawJunction var2 = (JigsawJunction)var1;
         if (this.sourceX != var2.sourceX) {
            return false;
         } else if (this.sourceZ != var2.sourceZ) {
            return false;
         } else if (this.deltaY != var2.deltaY) {
            return false;
         } else {
            return this.destProjection == var2.destProjection;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.sourceX;
      var1 = 31 * var1 + this.sourceGroundY;
      var1 = 31 * var1 + this.sourceZ;
      var1 = 31 * var1 + this.deltaY;
      var1 = 31 * var1 + this.destProjection.hashCode();
      return var1;
   }

   public String toString() {
      return "JigsawJunction{sourceX=" + this.sourceX + ", sourceGroundY=" + this.sourceGroundY + ", sourceZ=" + this.sourceZ + ", deltaY=" + this.deltaY + ", destProjection=" + this.destProjection + '}';
   }
}
