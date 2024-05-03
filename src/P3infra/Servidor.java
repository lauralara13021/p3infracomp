package P3infra;
import java.math.BigInteger;
import java.security.*;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.security.*;
import java.security.NoSuchAlgorithmException;

import java.math.BigInteger;
import java.security.*;
import java.util.Random;

public class Servidor extends Thread {

    private PublicKey publica;
    private PrivateKey privada;
    private int x;
    private BigInteger g_elevado_x;
    private Random random;

    public Servidor() throws NoSuchAlgorithmException {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        this.random = new Random();
        
        KeyPair keyPair = generator.generateKeyPair();
        this.publica = keyPair.getPublic();
        this.privada = keyPair.getPrivate();
        this.x = random.nextInt(0, 16);
    }
    public byte[] resolverReto(Long reto, CifradoAsimetrico cifrador) {
        byte[] retoCifrado = cifrador.cifrarLlave(privada, String.valueOf(reto));
        return retoCifrado;
    }

    public byte[] cifrar(String mensaje, CifradoAsimetrico cifrador) {
        byte[] mCifrado = cifrador.cifrarLlave(privada, mensaje);
        return mCifrado;
    }

    public String generarDatosDH(BigInteger p, BigInteger g) {

        System.out.println("valor de x: " + this.x);
        BigInteger gALaX = g.pow(this.x);
        System.out.println("Calculado");
        String gToThePowerX = gALaX.toString();

        String data = p + "$";
        data += g;
        data += "$";
        data += gToThePowerX;
        return data;
    }
    public PublicKey getPublicKey() {
        return publica;
    }

    public PrivateKey getPrivateKey() {
        return privada;
    }

    public BigInteger getGX() {
        return this.g_elevado_x;
    }

}