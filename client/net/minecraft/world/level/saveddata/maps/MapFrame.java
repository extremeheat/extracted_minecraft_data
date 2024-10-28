package net.minecraft.world.level.saveddata.maps;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

public class MapFrame {
   private final BlockPos pos;
   private final int rotation;
   private final int entityId;

   public MapFrame(BlockPos var1, int var2, int var3) {
      super();
      this.pos = var1;
      this.rotation = var2;
      this.entityId = var3;
   }

   @Nullable
   public static MapFrame load(CompoundTag var0) {
      Optional var1 = NbtUtils.readBlockPos(var0, "pos");
      if (var1.isEmpty()) {
         return null;
      } else {
         int var2 = var0.getInt("rotation");
         int var3 = var0.getInt("entity_id");
         return new MapFrame((BlockPos)var1.get(), var2, var3);
      }
   }

   public CompoundTag save() {
      CompoundTag var1 = new CompoundTag();
      var1.put("pos", NbtUtils.writeBlockPos(this.pos));
      var1.putInt("rotation", this.rotation);
      var1.putInt("entity_id", this.entityId);
      return var1;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getRotation() {
      return this.rotation;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public String getId() {
      return frameId(this.pos);
   }

   public static String frameId(BlockPos var0) {
      int var10000 = var0.getX();
      return "frame-" + var10000 + "," + var0.getY() + "," + var0.getZ();
   }
}
