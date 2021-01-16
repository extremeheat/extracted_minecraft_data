package io.netty.handler.ssl;

import java.nio.ByteBuffer;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;

class JdkSslEngine extends SSLEngine implements ApplicationProtocolAccessor {
   private final SSLEngine engine;
   private volatile String applicationProtocol;

   JdkSslEngine(SSLEngine var1) {
      super();
      this.engine = var1;
   }

   public String getNegotiatedApplicationProtocol() {
      return this.applicationProtocol;
   }

   void setNegotiatedApplicationProtocol(String var1) {
      this.applicationProtocol = var1;
   }

   public SSLSession getSession() {
      return this.engine.getSession();
   }

   public SSLEngine getWrappedEngine() {
      return this.engine;
   }

   public void closeInbound() throws SSLException {
      this.engine.closeInbound();
   }

   public void closeOutbound() {
      this.engine.closeOutbound();
   }

   public String getPeerHost() {
      return this.engine.getPeerHost();
   }

   public int getPeerPort() {
      return this.engine.getPeerPort();
   }

   public SSLEngineResult wrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      return this.engine.wrap(var1, var2);
   }

   public SSLEngineResult wrap(ByteBuffer[] var1, ByteBuffer var2) throws SSLException {
      return this.engine.wrap(var1, var2);
   }

   public SSLEngineResult wrap(ByteBuffer[] var1, int var2, int var3, ByteBuffer var4) throws SSLException {
      return this.engine.wrap(var1, var2, var3, var4);
   }

   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      return this.engine.unwrap(var1, var2);
   }

   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2) throws SSLException {
      return this.engine.unwrap(var1, var2);
   }

   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2, int var3, int var4) throws SSLException {
      return this.engine.unwrap(var1, var2, var3, var4);
   }

   public Runnable getDelegatedTask() {
      return this.engine.getDelegatedTask();
   }

   public boolean isInboundDone() {
      return this.engine.isInboundDone();
   }

   public boolean isOutboundDone() {
      return this.engine.isOutboundDone();
   }

   public String[] getSupportedCipherSuites() {
      return this.engine.getSupportedCipherSuites();
   }

   public String[] getEnabledCipherSuites() {
      return this.engine.getEnabledCipherSuites();
   }

   public void setEnabledCipherSuites(String[] var1) {
      this.engine.setEnabledCipherSuites(var1);
   }

   public String[] getSupportedProtocols() {
      return this.engine.getSupportedProtocols();
   }

   public String[] getEnabledProtocols() {
      return this.engine.getEnabledProtocols();
   }

   public void setEnabledProtocols(String[] var1) {
      this.engine.setEnabledProtocols(var1);
   }

   public SSLSession getHandshakeSession() {
      return this.engine.getHandshakeSession();
   }

   public void beginHandshake() throws SSLException {
      this.engine.beginHandshake();
   }

   public HandshakeStatus getHandshakeStatus() {
      return this.engine.getHandshakeStatus();
   }

   public void setUseClientMode(boolean var1) {
      this.engine.setUseClientMode(var1);
   }

   public boolean getUseClientMode() {
      return this.engine.getUseClientMode();
   }

   public void setNeedClientAuth(boolean var1) {
      this.engine.setNeedClientAuth(var1);
   }

   public boolean getNeedClientAuth() {
      return this.engine.getNeedClientAuth();
   }

   public void setWantClientAuth(boolean var1) {
      this.engine.setWantClientAuth(var1);
   }

   public boolean getWantClientAuth() {
      return this.engine.getWantClientAuth();
   }

   public void setEnableSessionCreation(boolean var1) {
      this.engine.setEnableSessionCreation(var1);
   }

   public boolean getEnableSessionCreation() {
      return this.engine.getEnableSessionCreation();
   }

   public SSLParameters getSSLParameters() {
      return this.engine.getSSLParameters();
   }

   public void setSSLParameters(SSLParameters var1) {
      this.engine.setSSLParameters(var1);
   }
}
