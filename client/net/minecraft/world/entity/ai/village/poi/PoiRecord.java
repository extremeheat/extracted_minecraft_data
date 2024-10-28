package net.minecraft.world.entity.ai.village.poi;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.VisibleForDebug;

public class PoiRecord {
   private final BlockPos pos;
   private final Holder<PoiType> poiType;
   private int freeTickets;
   private final Runnable setDirty;

   PoiRecord(BlockPos var1, Holder<PoiType> var2, int var3, Runnable var4) {
      super();
      this.pos = var1.immutable();
      this.poiType = var2;
      this.freeTickets = var3;
      this.setDirty = var4;
   }

   public PoiRecord(BlockPos var1, Holder<PoiType> var2, Runnable var3) {
      this(var1, var2, ((PoiType)var2.value()).maxTickets(), var3);
   }

   public Packed pack() {
      return new Packed(this.pos, this.poiType, this.freeTickets);
   }

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public int getFreeTickets() {
      return this.freeTickets;
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
      if (this.freeTickets >= ((PoiType)this.poiType.value()).maxTickets()) {
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
      return this.freeTickets != ((PoiType)this.poiType.value()).maxTickets();
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Holder<PoiType> getPoiType() {
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

   public static record Packed(BlockPos pos, Holder<PoiType> poiType, int freeTickets) {
      public static final Codec<Packed> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(BlockPos.CODEC.fieldOf("pos").forGetter(Packed::pos), RegistryFixedCodec.create(Registries.POINT_OF_INTEREST_TYPE).fieldOf("type").forGetter(Packed::poiType), Codec.INT.fieldOf("free_tickets").orElse(0).forGetter(Packed::freeTickets)).apply(var0, Packed::new);
      });

      public Packed(BlockPos var1, Holder<PoiType> var2, int var3) {
         super();
         this.pos = var1;
         this.poiType = var2;
         this.freeTickets = var3;
      }

      public PoiRecord unpack(Runnable var1) {
         return new PoiRecord(this.pos, this.poiType, this.freeTickets, var1);
      }

      public BlockPos pos() {
         return this.pos;
      }

      public Holder<PoiType> poiType() {
         return this.poiType;
      }

      public int freeTickets() {
         return this.freeTickets;
      }
   }
}
