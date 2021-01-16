package org.apache.logging.log4j.core.util;

import javax.crypto.SecretKey;

public interface SecretKeyProvider {
   SecretKey getSecretKey();
}
