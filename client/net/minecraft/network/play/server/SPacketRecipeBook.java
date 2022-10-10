package net.minecraft.network.play.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;

public class SPacketRecipeBook implements Packet<INetHandlerPlayClient> {
   private SPacketRecipeBook.State field_193646_a;
   private List<ResourceLocation> field_192596_a;
   private List<ResourceLocation> field_193647_c;
   private boolean field_192598_c;
   private boolean field_192599_d;
   private boolean field_202494_f;
   private boolean field_202495_g;

   public SPacketRecipeBook() {
      super();
   }

   public SPacketRecipeBook(SPacketRecipeBook.State var1, Collection<ResourceLocation> var2, Collection<ResourceLocation> var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      super();
      this.field_193646_a = var1;
      this.field_192596_a = ImmutableList.copyOf(var2);
      this.field_193647_c = ImmutableList.copyOf(var3);
      this.field_192598_c = var4;
      this.field_192599_d = var5;
      this.field_202494_f = var6;
      this.field_202495_g = var7;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_191980_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_193646_a = (SPacketRecipeBook.State)var1.func_179257_a(SPacketRecipeBook.State.class);
      this.field_192598_c = var1.readBoolean();
      this.field_192599_d = var1.readBoolean();
      this.field_202494_f = var1.readBoolean();
      this.field_202495_g = var1.readBoolean();
      int var2 = var1.func_150792_a();
      this.field_192596_a = Lists.newArrayList();

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         this.field_192596_a.add(var1.func_192575_l());
      }

      if (this.field_193646_a == SPacketRecipeBook.State.INIT) {
         var2 = var1.func_150792_a();
         this.field_193647_c = Lists.newArrayList();

         for(var3 = 0; var3 < var2; ++var3) {
            this.field_193647_c.add(var1.func_192575_l());
         }
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_193646_a);
      var1.writeBoolean(this.field_192598_c);
      var1.writeBoolean(this.field_192599_d);
      var1.writeBoolean(this.field_202494_f);
      var1.writeBoolean(this.field_202495_g);
      var1.func_150787_b(this.field_192596_a.size());
      Iterator var2 = this.field_192596_a.iterator();

      ResourceLocation var3;
      while(var2.hasNext()) {
         var3 = (ResourceLocation)var2.next();
         var1.func_192572_a(var3);
      }

      if (this.field_193646_a == SPacketRecipeBook.State.INIT) {
         var1.func_150787_b(this.field_193647_c.size());
         var2 = this.field_193647_c.iterator();

         while(var2.hasNext()) {
            var3 = (ResourceLocation)var2.next();
            var1.func_192572_a(var3);
         }
      }

   }

   public List<ResourceLocation> func_192595_a() {
      return this.field_192596_a;
   }

   public List<ResourceLocation> func_193644_b() {
      return this.field_193647_c;
   }

   public boolean func_192593_c() {
      return this.field_192598_c;
   }

   public boolean func_192594_d() {
      return this.field_192599_d;
   }

   public boolean func_202492_e() {
      return this.field_202494_f;
   }

   public boolean func_202493_f() {
      return this.field_202495_g;
   }

   public SPacketRecipeBook.State func_194151_e() {
      return this.field_193646_a;
   }

   public static enum State {
      INIT,
      ADD,
      REMOVE;

      private State() {
      }
   }
}
