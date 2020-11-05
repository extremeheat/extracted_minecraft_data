package net.minecraft.world.entity.ai.village.poi;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;

public class PoiRecord {
   private final BlockPos pos;
   private final PoiType poiType;
   private int freeTickets;
   private final Runnable setDirty;

   public static Codec<PoiRecord> codec(Runnable var0) {
      return RecordCodecBuilder.create((var1) -> {
         return var1.group(BlockPos.CODEC.fieldOf("pos").forGetter((var0x) -> {
            return var0x.pos;
         }), Registry.POINT_OF_INTEREST_TYPE.fieldOf("type").forGetter((var0x) -> {
            return var0x.poiType;
         }), Codec.INT.fieldOf("free_tickets").orElse(0).forGetter((var0x) -> {
            return var0x.freeTickets;
         }), RecordCodecBuilder.point(var0)).apply(var1, PoiRecord::new);
      });
   }

   private PoiRecord(BlockPos var1, PoiType var2, int var3, Runnable var4) {
      super();
      this.pos = var1.immutable();
      this.poiType = var2;
      this.freeTickets = var3;
      this.setDirty = var4;
   }

   public PoiRecord(BlockPos var1, PoiType var2, Runnable var3) {
      this(var1, var2, var2.getMaxTickets(), var3);
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
