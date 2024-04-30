package proyecto3Infracomp;

import java.security.PrivateKey;
import java.security.Signature;

public class Firma {
	    private Signature signature;

	    public Firma(PrivateKey privateKey) throws Exception {
	        this.signature = Signature.getInstance("SHA256withRSA");
	        this.signature.initSign(privateKey);
	    }

	    public byte[] firmar(byte[] data) throws Exception {
	        signature.update(data);
	        return signature.sign();
	    }
	}

