package P3infra;

import java.math.BigInteger;
import java.util.Scanner;

public class Principal {
    static CifradoAsimetrico cifrador = new CifradoAsimetrico();
    static LlaveMaestra llaveMaestra = new LlaveMaestra();
    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        boolean estado = true;
        while (estado) {
            System.out.println("Seleccione una opción: ");
            menu();

            int opcion = 0;
            try {
                opcion = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                opcion = 0;
            }

            if (opcion == 1) {
                Servidor servidor = new Servidor();
                Cliente cliente = new Cliente();

                //
                /*Long reto = cliente.getReto();
                byte[] retoCifrado = servidor.cifrar_Reto(reto, cifrador);
                String descifrado = cliente.verificarReto(retoCifrado, cifrador, servidor.getPublicKey());

                if (descifrado.equals("OK")) {
                    System.out.println("OK");

                } else if (descifrado.equals("ERROR")) {
                    System.out.println("ERROR");
                }*/

                Long reto = cliente.getReto();
                byte[] retoCifrado = servidor.resolverReto(reto, cifrador);
                String descifrado = cliente.verificarReto(retoCifrado, cifrador, servidor.getPublicKey());

                if (descifrado.equals("OK")) {
                    System.out.println("Comunicación exitosa.");

                    // Diffie Hellman datos
                    System.out.println("Sacando datos DH");
                    BigInteger p = llaveMaestra.getP();
                    BigInteger g = llaveMaestra.getG();
                    byte[] iv = llaveMaestra.generarIV();

                    System.out.println("generando datos DH");
                    String infoDH = servidor.generarDatosDH(p, g);
                    System.out.println("Datos DH: " + infoDH);

                    System.out.println("Cifrando datos DH");
                    byte[] infoDHCifrada = servidor.cifrar(infoDH, cifrador);

                    System.out.println("Verificando datos DH");
                    String respuestaVerificacionDH = cliente.verificarDH(infoDHCifrada, p, g,
                            servidor.getGX(), iv, servidor.getPublicKey(), cifrador);

                    if (respuestaVerificacionDH.equals("OK")) {
                        System.out.println("Verificación exitosa de DH.");

                    } else if (respuestaVerificacionDH.equals("ERROR")) {
                        System.out.println("Fallo en la verficacion DH.");
                    }

                } else if (descifrado.equals("ERROR")) {
                    System.out.println("Fallo en la comunicación.");
                }

                break;

                
                
                
                
                
                
                
      
                
            } else if (opcion == 2) {
                // Código para la opción 2
                System.out.println("Seleccionó la opción 2");
            } else if (opcion == 3) {
                // Código para la opción 3
                System.out.println("Seleccionó la opción 3");
            } else {
                System.out.println("Opción no válida, intente nuevamente.");
            }
        }
        input.close();
    }
    public static void menu() {
        System.out.println("1. Un cliente");
        System.out.println("2.Más de uno");
        System.out.println("3. Salir");
    }

}


