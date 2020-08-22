package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Serializable;

public class PoiRecord implements Serializable {
   private final BlockPos pos;
   private final PoiType poiType;
   private int freeTickets;
   private final Runnable setDirty;

   private PoiRecord(BlockPos var1, PoiType var2, int var3, Runnable var4) {
      this.pos = var1.immutable();
      this.poiType = var2;
      this.freeTickets = var3;
      this.setDirty = var4;
   }

   public PoiRecord(BlockPos var1, PoiType var2, Runnable var3) {
      this(var1, var2, var2.getMaxTickets(), var3);
   }

   public PoiRecord(Dynamic var1, Runnable var2) {
      this((BlockPos)var1.get("pos").map(BlockPos::deserialize).orElse(new BlockPos(0, 0, 0)), (PoiType)Registry.POINT_OF_INTEREST_TYPE.get(new ResourceLocation(var1.get("type").asString(""))), var1.get("free_tickets").asInt(0), var2);
   }

   public Object serialize(DynamicOps var1) {
      return var1.createMap(ImmutableMap.of(var1.createString("pos"), this.pos.serialize(var1), var1.createString("type"), var1.createString(Registry.POINT_OF_INTEREST_TYPE.getKey(this.poiType).toString()), var1.createString("free_tickets"), var1.createInt(this.freeTickets)));
   }

   protected boolean acquireTicket() {
      if (this.freeTickets <= 0) {
         return false;
      } else {
         --this.freeTickets;
         this.setDirty.run();
         return true;
      }
   }

   protected boolean releaseTicket() {
      if (this.freeTickets >= this.poiType.getMaxTickets()) {
         return false;
      } else {
         ++this.freeTickets;
         this.setDirty.run();
         return true;
      }
   }

   public boolean hasSpace() {
      return this.freeTickets > 0;
   }

   public boolean isOccupied() {
      return this.freeTickets != this.poiType.getMaxTickets();
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public PoiType getPoiType() {
      return this.poiType;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 != null && this.getClass() == var1.getClass() ? Objects.equals(this.pos, ((PoiRecord)var1).pos) : false;
      }
   }

   public int hashCode() {
      return this.pos.hashCode();
   }
}
