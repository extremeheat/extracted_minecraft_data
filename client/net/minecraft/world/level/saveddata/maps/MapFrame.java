package net.minecraft.world.level.saveddata.maps;

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

   public static MapFrame load(CompoundTag var0) {
      BlockPos var1 = NbtUtils.readBlockPos(var0.getCompound("Pos"));
      int var2 = var0.getInt("Rotation");
      int var3 = var0.getInt("EntityId");
      return new MapFrame(var1, var2, var3);
   }

   public CompoundTag save() {
      CompoundTag var1 = new CompoundTag();
      var1.put("Pos", NbtUtils.writeBlockPos(this.pos));
      var1.putInt("Rotation", this.rotation);
      var1.putInt("EntityId", this.entityId);
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
      return "frame-" + var0.getX() + "," + var0.getY() + "," + var0.getZ();
   }
}
