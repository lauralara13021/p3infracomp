package P3infra;

import java.math.BigInteger;
import java.security.SecureRandom;

public class LlaveMaestra {

	
	/*P:   
    00:a6:33:96:e0:21:7b:35:44:77:5f:e5:19:5f:2a:
    ad:b2:71:d1:b4:23:e2:36:0e:db:4b:ce:4a:d8:9c:
    d9:92:d8:44:9e:71:5f:31:3e:ee:4a:69:26:d0:2c:
    fd:01:0f:db:85:29:3c:45:85:3c:ca:c0:47:81:a0:
    82:5a:d1:65:67:78:d9:f9:27:38:99:e7:eb:56:fa:
    27:95:43:ad:21:09:dd:04:0a:89:98:88:0f:e9:64:
    17:4e:c8:26:e0:f1:92:d8:ca:7c:cc:33:57:3f:19:
    45:34:06:e3:63:9e:85:fd:1e:1d:58:c0:fd:af:27:
    07:c3:9b:57:e1:9a:ad:b5:8f*/
	
	//G:    2 (0x2)
    private final BigInteger p = new BigInteger(
            "00a63396e0217b3544775fe5195f2aadb271d1b423e2360edb4bce4ad89cd992d8449e715f313eee4a6926d02cfd010fdb85293c45853ccac04781a0825ad1656778d9f9273899e7eb56fa279543ad2109dd040a8998880fe964174ec826e0f192d8ca7ccc33573f19453406e3639e85fd1e1d58c0fdaf2707c39b57e19aadb58f",16);
    private final BigInteger g = BigInteger.valueOf(2);

    public BigInteger llaveMaestra_generador(BigInteger X) {
        BigInteger y = g.modPow(X, p);
        return y;
    }
    public BigInteger getP() {
        return this.p;
    }

    public BigInteger getG() {
        return this.g;
    }
    public static byte[] generarIV() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        return iv;
    }
}