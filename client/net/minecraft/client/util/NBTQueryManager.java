package net.minecraft.client.util;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketNBTQueryEntity;
import net.minecraft.network.play.client.CPacketNBTQueryTileEntity;
import net.minecraft.util.math.BlockPos;

public class NBTQueryManager {
   private final NetHandlerPlayClient field_211550_a;
   private int field_211551_b = -1;
   @Nullable
   private Consumer<NBTTagCompound> field_211552_c;

   public NBTQueryManager(NetHandlerPlayClient var1) {
      super();
      this.field_211550_a = var1;
   }

   public boolean func_211548_a(int var1, @Nullable NBTTagCompound var2) {
      if (this.field_211551_b == var1 && this.field_211552_c != null) {
         this.field_211552_c.accept(var2);
         this.field_211552_c = null;
         return true;
      } else {
         return false;
      }
   }

   private int func_211546_a(Consumer<NBTTagCompound> var1) {
      this.field_211552_c = var1;
      return ++this.field_211551_b;
   }

   public void func_211549_a(int var1, Consumer<NBTTagCompound> var2) {
      int var3 = this.func_211546_a(var2);
      this.field_211550_a.func_147297_a(new CPacketNBTQueryEntity(var3, var1));
   }

   public void func_211547_a(BlockPos var1, Consumer<NBTTagCompound> var2) {
      int var3 = this.func_211546_a(var2);
      this.field_211550_a.func_147297_a(new CPacketNBTQueryTileEntity(var3, var1));
   }
}
