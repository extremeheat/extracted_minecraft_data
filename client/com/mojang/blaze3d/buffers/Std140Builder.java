package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.DontObfuscate;
import java.nio.ByteBuffer;
import net.minecraft.util.Mth;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.joml.Vector4fc;
import org.joml.Vector4ic;
import org.lwjgl.system.MemoryStack;

@DontObfuscate
public class Std140Builder {
   private final ByteBuffer buffer;
   private final int start;

   private Std140Builder(ByteBuffer var1) {
      super();
      this.buffer = var1;
      this.start = var1.position();
   }

   public static Std140Builder intoBuffer(ByteBuffer var0) {
      return new Std140Builder(var0);
   }

   public static Std140Builder onStack(MemoryStack var0, int var1) {
      return new Std140Builder(var0.malloc(var1));
   }

   public ByteBuffer get() {
      return this.buffer.flip();
   }

   public Std140Builder align(int var1) {
      int var2 = this.buffer.position();
      this.buffer.position(this.start + Mth.roundToward(var2 - this.start, var1));
      return this;
   }

   public Std140Builder putFloat(float var1) {
      this.align(4);
      this.buffer.putFloat(var1);
      return this;
   }

   public Std140Builder putInt(int var1) {
      this.align(4);
      this.buffer.putInt(var1);
      return this;
   }

   public Std140Builder putVec2(float var1, float var2) {
      this.align(8);
      this.buffer.putFloat(var1);
      this.buffer.putFloat(var2);
      return this;
   }

   public Std140Builder putVec2(Vector2fc var1) {
      this.align(8);
      var1.get(this.buffer);
      this.buffer.position(this.buffer.position() + 8);
      return this;
   }

   public Std140Builder putIVec2(int var1, int var2) {
      this.align(8);
      this.buffer.putInt(var1);
      this.buffer.putInt(var2);
      return this;
   }

   public Std140Builder putIVec2(Vector2ic var1) {
      this.align(8);
      var1.get(this.buffer);
      this.buffer.position(this.buffer.position() + 8);
      return this;
   }

   public Std140Builder putVec3(float var1, float var2, float var3) {
      this.align(16);
      this.buffer.putFloat(var1);
      this.buffer.putFloat(var2);
      this.buffer.putFloat(var3);
      this.buffer.position(this.buffer.position() + 4);
      return this;
   }

   public Std140Builder putVec3(Vector3fc var1) {
      this.align(16);
      var1.get(this.buffer);
      this.buffer.position(this.buffer.position() + 16);
      return this;
   }

   public Std140Builder putIVec3(int var1, int var2, int var3) {
      this.align(16);
      this.buffer.putInt(var1);
      this.buffer.putInt(var2);
      this.buffer.putInt(var3);
      this.buffer.position(this.buffer.position() + 4);
      return this;
   }

   public Std140Builder putIVec3(Vector3ic var1) {
      this.align(16);
      var1.get(this.buffer);
      this.buffer.position(this.buffer.position() + 16);
      return this;
   }

   public Std140Builder putVec4(float var1, float var2, float var3, float var4) {
      this.align(16);
      this.buffer.putFloat(var1);
      this.buffer.putFloat(var2);
      this.buffer.putFloat(var3);
      this.buffer.putFloat(var4);
      return this;
   }

   public Std140Builder putVec4(Vector4fc var1) {
      this.align(16);
      var1.get(this.buffer);
      this.buffer.position(this.buffer.position() + 16);
      return this;
   }

   public Std140Builder putIVec4(int var1, int var2, int var3, int var4) {
      this.align(16);
      this.buffer.putInt(var1);
      this.buffer.putInt(var2);
      this.buffer.putInt(var3);
      this.buffer.putInt(var4);
      return this;
   }

   public Std140Builder putIVec4(Vector4ic var1) {
      this.align(16);
      var1.get(this.buffer);
      this.buffer.position(this.buffer.position() + 16);
      return this;
   }

   public Std140Builder putMat4f(Matrix4fc var1) {
      this.align(16);
      var1.get(this.buffer);
      this.buffer.position(this.buffer.position() + 64);
      return this;
   }
}
