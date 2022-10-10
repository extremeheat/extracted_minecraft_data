package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SPacketPlayerLook implements Packet<INetHandlerPlayClient> {
   private double field_200532_a;
   private double field_200533_b;
   private double field_200534_c;
   private int field_200535_d;
   private EntityAnchorArgument.Type field_201065_e;
   private EntityAnchorArgument.Type field_201066_f;
   private boolean field_200536_e;

   public SPacketPlayerLook() {
      super();
   }

   public SPacketPlayerLook(EntityAnchorArgument.Type var1, double var2, double var4, double var6) {
      super();
      this.field_201065_e = var1;
      this.field_200532_a = var2;
      this.field_200533_b = var4;
      this.field_200534_c = var6;
   }

   public SPacketPlayerLook(EntityAnchorArgument.Type var1, Entity var2, EntityAnchorArgument.Type var3) {
      super();
      this.field_201065_e = var1;
      this.field_200535_d = var2.func_145782_y();
      this.field_201066_f = var3;
      Vec3d var4 = var3.func_201017_a(var2);
      this.field_200532_a = var4.field_72450_a;
      this.field_200533_b = var4.field_72448_b;
      this.field_200534_c = var4.field_72449_c;
      this.field_200536_e = true;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_201065_e = (EntityAnchorArgument.Type)var1.func_179257_a(EntityAnchorArgument.Type.class);
      this.field_200532_a = var1.readDouble();
      this.field_200533_b = var1.readDouble();
      this.field_200534_c = var1.readDouble();
      if (var1.readBoolean()) {
         this.field_200536_e = true;
         this.field_200535_d = var1.func_150792_a();
         this.field_201066_f = (EntityAnchorArgument.Type)var1.func_179257_a(EntityAnchorArgument.Type.class);
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_201065_e);
      var1.writeDouble(this.field_200532_a);
      var1.writeDouble(this.field_200533_b);
      var1.writeDouble(this.field_200534_c);
      var1.writeBoolean(this.field_200536_e);
      if (this.field_200536_e) {
         var1.func_150787_b(this.field_200535_d);
         var1.func_179249_a(this.field_201066_f);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_200232_a(this);
   }

   public EntityAnchorArgument.Type func_201064_a() {
      return this.field_201065_e;
   }

   @Nullable
   public Vec3d func_200531_a(World var1) {
      if (this.field_200536_e) {
         Entity var2 = var1.func_73045_a(this.field_200535_d);
         return var2 == null ? new Vec3d(this.field_200532_a, this.field_200533_b, this.field_200534_c) : this.field_201066_f.func_201017_a(var2);
      } else {
         return new Vec3d(this.field_200532_a, this.field_200533_b, this.field_200534_c);
      }
   }
}
