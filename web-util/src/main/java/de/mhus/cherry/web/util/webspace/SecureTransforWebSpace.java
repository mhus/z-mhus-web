package de.mhus.cherry.web.util.webspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.HashMap;

import de.mhus.cherry.web.api.CallContext;
import de.mhus.lib.core.IReadProperties;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.crypt.MBouncy;
import de.mhus.lib.core.crypt.MCrypt;

public class SecureTransforWebSpace extends TransformWebSpace {

    HashMap<String,Info> cache = new HashMap<>();
    
    public SecureTransforWebSpace() {
        denyExtensions = new String[] { ".cfg", ".sig" };
    }
    
    @Override
    protected void doTransform(CallContext context, File from, IReadProperties cfg, String type) throws Exception {
        if (from.exists() && from.isFile()) {
            Info info = cache.get(from.getAbsolutePath());
            
            if (info != null) {
                // check modified
                if (info.modified != from.lastModified() || info.length != from.length()) {
                    info = null;
                } else {
                    // check hash
                    try (InputStream is = new FileInputStream(from)) {
                        String hash = MCrypt.sha256(is);
                        if (!info.hash.equals(hash))
                            throw new Exception("Invalid modification");
                    }
                }
            }
            if (info == null) {
                try (InputStream is = new FileInputStream(from)) {
                    String sign = MFile.readFile(new File(from.getAbsolutePath() + ".sig"));
                    if (sign == null)
                        throw new Exception("Signature not found");
                    if (!MBouncy.validateSignature(getPublicKey(), is, sign))
                        throw new Exception("File invalid");
                }
                info = new Info();
                info.modified = from.lastModified();
                info.length = from.length();
                try (InputStream is = new FileInputStream(from)) {
                    info.hash = MCrypt.sha256(is);
                }
                cache.put(from.getAbsolutePath(), info);
            }
            
        }
    }

    private PublicKey getPublicKey() {
        // TODO Auto-generated method stub
        return null;
    }

    private class Info {

        public long length;
        public long modified;
        public String hash;
        
    }
}
