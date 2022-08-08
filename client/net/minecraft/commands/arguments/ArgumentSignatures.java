package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Crypt;

public record ArgumentSignatures(long a, Map<String, byte[]> b) {
   private final long salt;
   private final Map<String, byte[]> signatures;
   private static final int MAX_ARGUMENT_COUNT = 8;
   private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

   public ArgumentSignatures(FriendlyByteBuf var1) {
      this(var1.readLong(), var1.readMap(FriendlyByteBuf.limitValue(HashMap::new, 8), (var0) -> {
         return var0.readUtf(16);
      }, FriendlyByteBuf::readByteArray));
   }

   public ArgumentSignatures(long var1, Map<String, byte[]> var3) {
      super();
      this.salt = var1;
      this.signatures = var3;
   }

   public static ArgumentSignatures empty() {
      return new ArgumentSignatures(0L, Map.of());
   }

   @Nullable
   public Crypt.SaltSignaturePair get(String var1) {
      byte[] var2 = (byte[])this.signatures.get(var1);
      return var2 != null ? new Crypt.SaltSignaturePair(this.salt, var2) : null;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeLong(this.salt);
      var1.writeMap(this.signatures, (var0, var1x) -> {
         var0.writeUtf(var1x, 16);
      }, FriendlyByteBuf::writeByteArray);
   }

   public static Map<String, Component> collectLastChildPlainSignableComponents(CommandContextBuilder<?> var0) {
      CommandContextBuilder var1 = var0.getLastChild();
      Object2ObjectArrayMap var2 = new Object2ObjectArrayMap();
      Iterator var3 = var1.getNodes().iterator();

      while(var3.hasNext()) {
         ParsedCommandNode var4 = (ParsedCommandNode)var3.next();
         CommandNode var7 = var4.getNode();
         if (var7 instanceof ArgumentCommandNode var5) {
            ArgumentType var8 = var5.getType();
            if (var8 instanceof SignedArgument var6) {
               ParsedArgument var9 = (ParsedArgument)var1.getArguments().get(var5.getName());
               if (var9 != null) {
                  var2.put(var5.getName(), getPlainComponentUnchecked(var6, var9));
               }
            }
         }
      }

      return var2;
   }

   private static <T> Component getPlainComponentUnchecked(SignedArgument<T> var0, ParsedArgument<?, ?> var1) {
      return var0.getPlainSignableComponent(var1.getResult());
   }

   public long salt() {
      return this.salt;
   }

   public Map<String, byte[]> signatures() {
      return this.signatures;
   }
}
